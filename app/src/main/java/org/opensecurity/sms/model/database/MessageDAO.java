package org.opensecurity.sms.model.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import org.opensecurity.sms.activities.OpenSecuritySMS;
import org.opensecurity.sms.model.Contact;
import org.opensecurity.sms.model.discussion.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * @author Calliste Hanriat
 */
public class MessageDAO {
    /**
     * Context currentContext useful to use some native android functions
     */
    private Context currentContex;

    /**
     * listContacts. I whould like to delete it but I don't think over for the moment
     */
    private static HashMap<String, Contact> listContacts = new HashMap<String, Contact>();

    /**
     * contactDAO is needed to access to android database & openSecurity database
     */
    private ContactDAO contactDAO;

    public static final String ADDRESS = "address";
    public static final String PERSON = "person";
    public static final String DATE = "date";
    public static final String READ = "read";
    public static final String STATUS = "status";
    public static final String TYPE = "type";
    public static final String BODY = "body";
    public static final String SEEN = "seen";
    public static final String SMS_EXTRA_NAME = "pdus";
    public static final String SMS_URI = "content://sms";

    public static final int MESSAGE_TYPE_INBOX = 1;
    public static final int MESSAGE_TYPE_SENT = 2;

    public static final int MESSAGE_IS_NOT_READ = 0;
    public static final int MESSAGE_IS_READ = 1;

    public static final int MESSAGE_IS_NOT_SEEN = 0;
    public static final int MESSAGE_IS_SEEN = 1;

    public MessageDAO(Context currentContex, ContactDAO contactDAO) {
        this.currentContex = currentContex;
        listContacts = new HashMap<String, Contact>();
        setContactDAO(contactDAO);
    }

    /**
     * This function fill an ArrayList of ConversationLine with every last
     * message for every contact
     *
     * @param contentResolver the contentResolver of the activity
     * @return an ArrayList of ConversationLine containing every last message for every contact
     */
    public ArrayList<Message> loadLastMessages(ContentResolver contentResolver) {
        //Empty all contacts
        listContacts.clear();
        //create a ArrayList of ConversationLine object.
        ArrayList<Message> messages = new ArrayList<>();

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
                contact = this.getContactDAO().findContactByPhoneNumberInDefaultBase(phoneNumber, contentResolver, listContacts);
                // if we don't already have this phoneNumber in the list
                if ((!listContacts.containsKey(contact.getPhoneNumber())) && (phoneNumber.length() >= 1)) {
                    listContacts.put(contact.getPhoneNumber(), contact);
                    // we get the smsContent and the date
                    smsContent = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                    //nbMessages = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.MESSAGE_COUNT));

                    Calendar date = Calendar.getInstance();
                    date.setTimeInMillis(cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)));

                    // we add a new ConversationLine with an id to send to the conversationActivity.
                    contact.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.THREAD_ID))));
                    contact.setNbMessages(nbMessages);
                    messages.add(new Message(smsContent, date, contact,
                            cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE)) == Telephony.Sms.MESSAGE_TYPE_SENT));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return messages;
    }

    /**
     * This function has to load an array of messages from one contact, or all last messages if contact == null
     *
     * @param contentResolver to manage access to a structured set of data in your phone
     * @param contact         is the current contact in our selected conversation. Could be null if we want all latest message
     * @param offset          never ised for the moment
     * @param limit           the limit of bubble what we want to load. (to dispence to load all bubbles)
     * @return bubbleData the ArrayList of Bubbles in a conversation.
     */
    public ArrayList<Message> loadMessages(ContentResolver contentResolver,
                                           Contact contact, int offset, int limit) {
        ArrayList<Message> bubbleData = new ArrayList<>();
        String content;
        boolean isMe;
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.YEAR, 1970);

        if (contact == null) {
            return loadLastMessages(contentResolver);
        } else {
            try {
                Cursor cursor = contentResolver.query(Uri.parse("content://sms"),
                        new String[]{Telephony.Sms.BODY, Telephony.Sms.TYPE,
                                Telephony.Sms.PERSON, Telephony.Sms.DATE,
                                Telephony.Sms.ADDRESS},
                        "thread_id = " + contact.getId(),
                        null,
                        "date DESC LIMIT " + String.valueOf(offset) + "," + String.valueOf(limit));

                if (cursor.moveToFirst()) {
                    do {
                        //if it's a recevied message :
                        isMe = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE)) == Telephony.Sms.MESSAGE_TYPE_SENT;
                        content = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                        Calendar date = Calendar.getInstance();
                        date.setTimeInMillis(cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)));
                        Message message = new Message(content, date, contact, isMe);

                        bubbleData.add(0, message);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            } catch (Exception e) {
                System.out.print("ERROR : Loading messages !");
            }

            return bubbleData;
        }
    }

    /**
     * insert sms sent by us into the default database
     * @param c the current context
     * @param message the contains of the sms
     */
    public void insertSMSSentIntoDefaultDataBase(Context c, String message) {
        TelephonyManager tMgr = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        ContentValues values = new ContentValues();
        values.put("address", mPhoneNumber);
        values.put("body", message);
        try {
            System.out.println("waiting...");
            Thread.sleep(150);
            System.out.println("Done");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        OpenSecuritySMS.getInstance().getContentResolver().insert(Uri.parse("content://sms/sent"), values);
    }

    /**
     * insert sms received into default database.
     * @param sms the sms
     * @param smsContent the content of the sms
     */
    public void insertSMSReceivedIntoDefaultDataBase(SmsMessage sms, String smsContent) {
        ContentResolver contentResolver = OpenSecuritySMS.getInstance().getContentResolver();
        ContentValues values = new ContentValues();
        values.put( ADDRESS, sms.getOriginatingAddress() );
        values.put( DATE, sms.getTimestampMillis() );
        values.put( READ, MESSAGE_IS_NOT_READ );
        values.put( STATUS, sms.getStatus() );
        values.put( TYPE, MESSAGE_TYPE_INBOX );
        values.put( SEEN, MESSAGE_IS_NOT_SEEN );
        values.put( BODY, smsContent );

        try {
            System.out.println("waiting...");
            Thread.sleep(150);
            System.out.println("Done");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Push row into the SMS table
        contentResolver.insert(Uri.parse(SMS_URI), values);
    }

    public Context getCurrentContex() {
        return currentContex;
    }

    public void setCurrentContex(Context currentContex) {
        this.currentContex = currentContex;
    }

    public ContactDAO getContactDAO() {
        return contactDAO;
    }

    public void setContactDAO(ContactDAO contactDAO) {
        this.contactDAO = contactDAO;
    }
}
