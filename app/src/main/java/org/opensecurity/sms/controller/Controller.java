package org.opensecurity.sms.controller;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.widget.Toast;

import org.opensecurity.sms.model.modelView.conversation.Bubble;
import org.opensecurity.sms.model.modelView.conversation.ConversationItem;
import org.opensecurity.sms.model.modelView.listConversation.ConversationLine;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 */
public class Controller {


    private static final String SMS_SEND_ACTION = "CTS_SMS_SEND_ACTION";
    private static final String SMS_DELIVERY_ACTION = "CTS_SMS_DELIVERY_ACTION";


    /**
     * This method fill an ArrayList of ConversationLine with every last
     * message for every contact
     *
     * @param contentResolver the contentResolver of the activity
     * @return an ArrayList of ConversationLine containing every last message for every contact
     */
    static public ArrayList<ConversationLine> loadLastMessages(ContentResolver contentResolver){
        //create a ArrayList of ConversationLine object.
        ArrayList<ConversationLine> conversationLines = new ArrayList<>();

        // We want to get the sms  with some of their attributes
        //SELECT distinct ADDRESS, Body, Type, Thread_id, Date FROM content://sms/ WHERE Address is not null  GROUP BY thread_id
        Cursor cursor = contentResolver.query(Uri.parse("content://sms"),
                new String[]{"DISTINCT " + Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.TYPE, Telephony.Sms.THREAD_ID, Telephony.Sms.DATE},
                Telephony.Sms.ADDRESS + " IS NOT NULL)" + "Group by (" + Telephony.Sms.THREAD_ID ,
                null,
                null);
        List<String> phoneNumbers = new ArrayList<>();
        // While there is a message
        int i = 0;
        while (cursor.moveToNext()) {
            // We get the phoneNumber and the type of the message

            /*for (i=0; i<cursor.getColumnCount();i++ ) {
                System.out.println(cursor.getColumnName(i) + " : " + cursor.getString(cursor.getColumnIndexOrThrow(cursor.getColumnName(i))));
            }
            System.out.println();
            System.out.println();*/
            String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
            int type = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE));
            // if we don't already have this phoneNumber in the list and the message is not a draft
            if ((!phoneNumbers.contains(phoneNumber)) && (type != 3) && (phoneNumber.length() >= 1)) {
                String name = null;
                String photo = null;
                // we get the smsContent and the date
                String smsContent = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                Calendar date = Calendar.getInstance();
                date.setTimeInMillis(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE))));
                Uri personUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, phoneNumber);
                // in order to get the contact name, we do a query
                Cursor localCursor = contentResolver.query(personUri,
                        new String[]{ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_THUMBNAIL_URI},
                        null,
                        null,
                        null);
                if (localCursor.getCount() != 0) {
                    localCursor.moveToFirst();
                    name = localCursor.getString(localCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    photo = localCursor.getString(localCursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                }
                localCursor.close();
                phoneNumbers.add(phoneNumber);
                name = (name == null) ? phoneNumber : name;
                // we add a new ConversationLine with an id to send to the conversationActivity.
                int thID = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.THREAD_ID)));
                // localCursor = contentResolver.query(Uri.parse("content://sms/conversations"), new String[]{Telephony.Sms.Conversations.MESSAGE_COUNT}, thID + " = thread_id" , null, null);
                //localCursor.moveToNext();
                conversationLines.add(new ConversationLine(name, smsContent, date, thID, photo, phoneNumber, 0));
                //localCursor.close();
            }
        }
        cursor.close();

        return conversationLines;
    }

    static public ArrayList<ConversationItem> loadMessages(ContentResolver contentResolver, ConversationLine conversationLine, int offset, int limit){
        ArrayList<ConversationItem> bubbleData = new ArrayList<>();
        String content;
        boolean isMe;
        Calendar lastDate = Calendar.getInstance(); lastDate.set(Calendar.YEAR, 1970);

        try {
            Cursor cursor = contentResolver.query(Uri.parse("content://sms"), new String[]{Telephony.Sms.BODY, Telephony.Sms.TYPE, Telephony.Sms.SUBJECT, Telephony.Sms.PERSON, Telephony.Sms.DATE, Telephony.Sms.ADDRESS, Telephony.Sms._ID}, conversationLine.getThread_ID() + " = thread_id" , null, "date ASC");// LIMIT " + String.valueOf(offset) + "," + String.valueOf(limit));

            while (cursor.moveToNext()) {
                //if it's a recevied message :
                isMe = !(cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE)) == 1 || cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.PERSON)) != null);

                content = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                Calendar date = Calendar.getInstance();
                date.setTimeInMillis(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE))));

                Bubble bubble = new Bubble(content, date, isMe);
                if (lastDate != date && bubble.hasToManagedDate(lastDate)) {
                    bubbleData.add(new ConversationItem(date));
                }
                lastDate = date;

                bubbleData.add(bubble);
            }
            cursor.close();
        } catch (Exception e) {
            System.out.print("ERROR : Loading messages !");
        }

        return bubbleData;
    }

    /**
     * Function called from sendButton click event in ConversationActivity.
     * @param c the context of ConversationActivity
     * @param cont to get all informations about our contact
     * @param message  to get the message body we want to send
     *
     * We try to send a message, but if the message does not contains text
     * We toast a Message nothing to send.
     */
    static public void sendSMS(Context c, ConversationLine cont, String message) {
        SmsManager smsManager = SmsManager.getDefault();

        try {
            ArrayList<String> messages = smsManager.divideMessage(message);


            ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
            ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();

            for (int i = 0; i < messages.size(); i++)
            {
                sentIntents.add(PendingIntent.getBroadcast(c, 0, new Intent(SMS_SEND_ACTION), 0));
                deliveryIntents.add(PendingIntent.getBroadcast(c, 0, new Intent(SMS_DELIVERY_ACTION), 0));
            }

            smsManager.sendMultipartTextMessage(cont.getNumber(),
                    null,
                    messages,
                    sentIntents,
                    deliveryIntents);
        } catch(Exception e) {
            System.out.println("Erreur : " + e.getLocalizedMessage());
            if (message.length()>0) {
                Toast.makeText(c, "Le message n'a pas pu être envoyé", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(c, "Nothing to send", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
