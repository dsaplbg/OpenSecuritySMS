package org.opensecurity.sms.controller;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import org.opensecurity.sms.model.ConversationLine;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Controller {

    /**
     * This method fill an ArrayList of ConversationLine with every last
     * message for every contact
     *
     * @param contentResolver the contentResolver of the activity
     * @return an ArrayList of ConversationLine containing every last message for every contact
     */
    static public ArrayList<ConversationLine> initLastMessage(ContentResolver contentResolver){
        //create a ArrayList of ConversationLine object.
        ArrayList<ConversationLine> conversationLines = new ArrayList<>();

        ContentResolver cr = contentResolver;
        // We want to get the sms in the inbox with all their attributes
        Cursor cursor = cr.query(Uri.parse("content://sms/inbox"),
                null,
                null,
                null,
                null);
        List<String> phoneNumbers = new ArrayList<>();
        // While there is a message
        while (cursor.moveToNext()) {
            // We get the phoneNumber and the type of the message
            String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            int type = cursor.getInt(cursor.getColumnIndexOrThrow("type"));
            // if we don't already have this phoneNumber in the list and the message is not a draft
            if ((!phoneNumbers.contains(phoneNumber)) && (type != 3) && (phoneNumber.length() >= 1)) {
                String name = null;
                // we get the smsContent and the date
                String smsContent = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                Date date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow("date"))));
                Uri personUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, phoneNumber);
                // in order to get the contact name, we do a query
                Cursor localCursor = cr.query(personUri,
                        new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                        null,
                        null,
                        null);
                if (localCursor.getCount() != 0) {
                    localCursor.moveToFirst();
                    name = localCursor.getString(localCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                }
                localCursor.close();
                phoneNumbers.add(phoneNumber);
                name = (name == null) ? phoneNumber : name;
                String[] sms = new String[]{name, phoneNumber, smsContent, date.toString()};
                // we add a new ConversationLine with an id to send to the conversationActivity.
                conversationLines.add(new ConversationLine(name, smsContent, sms[3], cursor.getString(cursor.getColumnIndexOrThrow("thread_id"))));
            }
        }
        cursor.close();

        return conversationLines;
    }

}
