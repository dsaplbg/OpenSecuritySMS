package org.opensecurity.sms.model;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;
import android.content.ContentValues;
import android.telephony.TelephonyManager;

import org.opensecurity.sms.R;
import org.opensecurity.sms.activities.OpenSecuritySMS;
import org.opensecurity.sms.model.database.ContactDAO;
import org.opensecurity.sms.model.discussion.Message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class Engine {
    /**
     * The Engine is used by all activities. It contains various useful functions to access to the database.
     */


    private static final String SMS_SEND_ACTION = "CTS_SMS_SEND_ACTION";
    private static final String SMS_DELIVERY_ACTION = "CTS_SMS_DELIVERY_ACTION";

    /**
     * the list of contacts. In hashmap. The key is his name.
     */
    private static HashMap<String, Contact> listContacts = new HashMap<String, Contact>();

    /**
     * uset for paterSingleton
     */
    private static volatile Engine instance = null;

    /**
     * the DAO to access to the database with contact functions
     */
    private ContactDAO contactDAO;

    /**
     * the context of the app
     */
    private Context context;


    public static final String SMS_EXTRA_NAME = "pdus";
    public static final String SMS_URI = "content://sms";

    public static final String ADDRESS = "address";
    public static final String PERSON = "person";
    public static final String DATE = "date";
    public static final String READ = "read";
    public static final String STATUS = "status";
    public static final String TYPE = "type";
    public static final String BODY = "body";
    public static final String SEEN = "seen";

    public static final int MESSAGE_TYPE_INBOX = 1;
    public static final int MESSAGE_TYPE_SENT = 2;

    public static final int MESSAGE_IS_NOT_READ = 0;
    public static final int MESSAGE_IS_READ = 1;

    public static final int MESSAGE_IS_NOT_SEEN = 0;
    public static final int MESSAGE_IS_SEEN = 1;

    /**
     * private (because of singleton) constructor
     */
    public Engine(Context context) {
        setContext(context);
        setContactDAO(new ContactDAO(this.getContext()));
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
                    messages.add(new Message(smsContent, date, contact,
                            cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE)) == Telephony.Sms.MESSAGE_TYPE_SENT));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return messages;
    }

    /**
     * This function has to load bubbles of one conversation in an ArrayList of object ConversationItem.
     *
     * @param contentResolver to manage access to a structured set of data in your phone
     * @param contact         is the current contact in our selected conversation
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
                        "thread_id = " + contact.getThreadId(),
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
     * We try to send a message, but if the message does not contains text
     * We toast a Message nothing to send.
     * Function called from sendButton click event in ConversationActivity.
     *
     * @param c       the context of ConversationActivity
     * @param contact to get all informations about our contact
     * @param message to get the message body we want to send
     */
    public boolean sendSMS(Context c, Contact contact, String message) {
        SmsManager smsManager = SmsManager.getDefault();

        try {
            insertSMSSentIntoDefaultDataBase(c, message);
            System.out.println("Insertion terminée. ");

            ArrayList<String> messages = smsManager.divideMessage(message);
            ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
            ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();

            for (int i = 0; i < messages.size(); i++) {
                sentIntents.add(PendingIntent.getBroadcast(c, 0, new Intent(SMS_SEND_ACTION), 0));
                deliveryIntents.add(PendingIntent.getBroadcast(c, 0, new Intent(SMS_DELIVERY_ACTION), 0));
            }

            smsManager.sendMultipartTextMessage(contact.getNumber(),
                    null,
                    messages,
                    sentIntents,
                    deliveryIntents);

        } catch (Exception e) {
            System.out.println("Erreur : " + e.getLocalizedMessage());
            if (message.length() > 0) {
                Toast.makeText(c, "Probleme to send", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(c, "Nothing to send", Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        return true;
    }

    public void putSmsIntoDataBase(SmsMessage sms, String smsContent) {
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
    /**
     * This function is use to initialize, create and display a notification in Android.
     *
     * @param title            the title of the notification.
     * @param content          the content of the notification
     * @param icon             the icon of the notification
     * @param activity         current activity
     * @param activityRunClass
     * @param save
     */
    public void makeNotification(String title, String content, Bitmap icon, Activity activity,
                                 Class activityRunClass, HashMap<String, Serializable> save) {
        if (activity != null) {
            Intent intent = new Intent(activity, activityRunClass);
            for (Map.Entry<String, Serializable> entry : save.entrySet())
                intent.putExtra(entry.getKey(), entry.getValue());
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity);
            stackBuilder.addParentStack(activityRunClass);
            stackBuilder.addNextIntent(intent);

            //specify the action which should be performed once the user select the notification
            PendingIntent pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);

            //the object use to create the design of a notification
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity);
            mBuilder.setSmallIcon(R.drawable.bulle_not_me);
            mBuilder.setLargeIcon(icon);
            mBuilder.setContentTitle(title);
            mBuilder.setContentText(content);
            mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            mBuilder.setContentIntent(pIntent);
            mBuilder.setAutoCancel(true);
           // mBuilder.setCategory(Notification.CATEGORY_MESSAGE);
            mBuilder.setDefaults(Notification.DEFAULT_ALL);

            NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());
        }
    }

    public ContactDAO getContactDAO() {
        return contactDAO;
    }

    public void setContactDAO(ContactDAO contactDAO) {
        this.contactDAO = contactDAO;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}