package org.opensecurity.sms.controller;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import org.opensecurity.sms.R;
import org.opensecurity.sms.model.Contact;
import org.opensecurity.sms.model.modelView.conversation.Bubble;
import org.opensecurity.sms.model.modelView.conversation.ConversationItem;
import org.opensecurity.sms.model.modelView.listConversation.ConversationLine;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class Controller {


    private static final String SMS_SEND_ACTION = "CTS_SMS_SEND_ACTION";
    private static final String SMS_DELIVERY_ACTION = "CTS_SMS_DELIVERY_ACTION";

    private static HashMap<String, Contact> listContacts = new HashMap<>();

    static public Contact getContact(String phoneNumber, ContentResolver contentResolver) {
        if (listContacts.containsKey(phoneNumber)) return listContacts.get(phoneNumber);

        Contact contact = new Contact(phoneNumber);
        Uri personUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, phoneNumber);
        Cursor localCursor = contentResolver.query(personUri,
                new String[]{ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_THUMBNAIL_URI},
                null,
                null,
                null);
        if (localCursor.moveToFirst()) {
            contact.setName(localCursor.getString(localCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)));
            contact.setPhotoURL(localCursor.getString(localCursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)));
        }
        localCursor.close();

        return contact;
    }

    /**
     * This method fill an ArrayList of ConversationLine with every last
     * message for every contact
     *
     * @param contentResolver the contentResolver of the activity
     * @return an ArrayList of ConversationLine containing every last message for every contact
     */
    static public ArrayList<ConversationLine> loadLastMessages(ContentResolver contentResolver) {
        //Empty all contacts
        listContacts.clear();
        //create a ArrayList of ConversationLine object.
        ArrayList<ConversationLine> conversationLines = new ArrayList<>();

        // We want to get the sms  with some of their attributes
        //SELECT distinct ADDRESS, Body, Type, Thread_id, Date FROM content://sms/ WHERE Address is not null  GROUP BY thread_id
        Cursor cursor = contentResolver.query(Uri.parse("content://sms"),
                new String[]{"DISTINCT " + Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.TYPE, Telephony.Sms.THREAD_ID, Telephony.Sms.DATE},
                Telephony.Sms.ADDRESS + " IS NOT NULL)" + "Group by (" + Telephony.Sms.THREAD_ID,
                null,
                null);

        // While there is a message
        if (cursor.moveToFirst()) {
            String phoneNumber, smsContent;
            int nbMessages = cursor.getCount();
            Contact contact;
            do {
                phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                contact = getContact(phoneNumber, contentResolver);
                // if we don't already have this phoneNumber in the list
                if ((!listContacts.containsKey(contact.getNumber())) && (phoneNumber.length() >= 1)) {
                    listContacts.put(contact.getNumber(), contact);
                    // we get the smsContent and the date
                    smsContent = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                    //nbMessages = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.MESSAGE_COUNT));

                    Calendar date = Calendar.getInstance();
                    date.setTimeInMillis(cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)));

                    // we add a new ConversationLine with an id to send to the conversationActivity.
                    contact.setThreadId(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.THREAD_ID))));
                    contact.setNbMessages(nbMessages);
                    conversationLines.add(new ConversationLine(contact, smsContent, date));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return conversationLines;
    }

    static public ArrayList<ConversationItem> loadMessages(ContentResolver contentResolver, Contact contact, int offset, int limit){
        ArrayList<ConversationItem> bubbleData = new ArrayList<>();
        String content;
        boolean isMe;
        Calendar lastDate = Calendar.getInstance(); lastDate.set(Calendar.YEAR, 1970);

        try {
            Cursor cursor = contentResolver.query(Uri.parse("content://sms"),
                    new String[]{Telephony.Sms.BODY, Telephony.Sms.TYPE, Telephony.Sms.PERSON, Telephony.Sms.DATE, Telephony.Sms.ADDRESS},
                    contact.getThreadId() + " = thread_id",
                    null,
                    "date ASC");// LIMIT " + String.valueOf(offset) + "," + String.valueOf(limit));

            if (cursor.moveToFirst()) {
                do {
                    //if it's a recevied message :
                    isMe = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE)) == Telephony.Sms.MESSAGE_TYPE_SENT;

                    content = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                    Calendar date = Calendar.getInstance();
                    date.setTimeInMillis(cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)));

                    Bubble bubble = new Bubble(content, date, isMe);
                    if (lastDate != date && bubble.hasToManagedDate(lastDate)) {
                        bubbleData.add(new ConversationItem(date));
                    }
                    lastDate = date;

                    bubbleData.add(bubble);
                } while (cursor.moveToNext());
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
     * @param contact to get all informations about our contact
     * @param message  to get the message body we want to send
     *
     * We try to send a message, but if the message does not contains text
     * We toast a Message nothing to send.
     */
    static public boolean sendSMS(Context c, Contact contact, String message) {
        SmsManager smsManager = SmsManager.getDefault();

        try {
            ArrayList<String> messages = smsManager.divideMessage(message);


            ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
            ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();

            for (int i = 0; i < messages.size(); i++)  {
                sentIntents.add(PendingIntent.getBroadcast(c, 0, new Intent(SMS_SEND_ACTION), 0));
                deliveryIntents.add(PendingIntent.getBroadcast(c, 0, new Intent(SMS_DELIVERY_ACTION), 0));
            }

            smsManager.sendMultipartTextMessage(contact.getNumber(),
                    null,
                    messages,
                    sentIntents,
                    deliveryIntents);
        } catch(Exception e) {
            System.out.println("Erreur : " + e.getLocalizedMessage());
            if (message.length()>0) {
                Toast.makeText(c, "Probleme to send", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(c, "Nothing to send", Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        return true;
    }

    static public void makeNotification(String title, String content, Bitmap icon, Activity activity, Class activityRunClass, HashMap<String, Serializable> save) {
        if (activity != null) {
            Intent intent = new Intent(activity, activityRunClass );
            for (Map.Entry<String, Serializable> entry : save.entrySet()) intent.putExtra(entry.getKey(), entry.getValue());
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity);
            stackBuilder.addParentStack(activityRunClass);
            stackBuilder.addNextIntent(intent);
            PendingIntent pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity);
            mBuilder.setSmallIcon(R.drawable.bulle_not_me);
            mBuilder.setLargeIcon(icon);
            mBuilder.setContentTitle(title);
            mBuilder.setContentText(content);
            mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            mBuilder.setContentIntent(pIntent);
            mBuilder.setAutoCancel(true);
            mBuilder.setCategory(Notification.CATEGORY_MESSAGE);
            mBuilder.setDefaults(Notification.DEFAULT_ALL);

            NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());
        }
    }
}
