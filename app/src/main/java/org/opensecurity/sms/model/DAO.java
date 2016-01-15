package org.opensecurity.sms.model;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

import org.opensecurity.sms.R;
import org.opensecurity.sms.model.Contact;
import org.opensecurity.sms.model.modelView.conversation.Bubble;
import org.opensecurity.sms.model.modelView.conversation.ConversationItem;
import org.opensecurity.sms.model.modelView.listConversation.ConversationLine;
import org.opensecurity.sms.view.OpenSecuritySMS;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class DAO {
    /**
     * The DAO is used by all activities. It contains various useful functions to access to the database.
     */


    private static final String SMS_SEND_ACTION = "CTS_SMS_SEND_ACTION";
    private static final String SMS_DELIVERY_ACTION = "CTS_SMS_DELIVERY_ACTION";


    /**
     * the database to do queries.
     */
    private static SQLiteDatabase mDb = null;

    /**
     * handler to create or upgrade table
     */
    private static DatabaseHandler mHandler = null;

    /**
     * the list of contacts. In hashmap. The key is his name.
     */
    private static HashMap<String, Contact> listContacts = new HashMap<>();

    /**
     * uset for paterSingleton
     */
    private static volatile DAO instance = null;

    /**
     * handler to create or upgrade table
     */
    public static final String NAME = "openSecuritySMSDatabase.db";

    /**
     * the version
     */
    public static final int VERSION = 1;


    /**
     * private (because of singleton) constructor
     * @param c the context
     */
    private DAO(Context c) {
        mHandler = new DatabaseHandler(c, NAME, null, VERSION);
    }


    /**
     * Méthode permettant de renvoyer une instance de la classe Singleton
     *
     * @return Retourne l'instance du singleton.
     */
    public final static DAO getInstance() {
        //Le "Double-Checked Singleton"/"Singleton doublement vérifié" permet
        //d'éviter un appel coûteux à synchronized,
        //une fois que l'instanciation est faite.
        if (DAO.instance == null) {
            // Le mot-clé synchronized sur ce bloc empêche toute instanciation
            // multiple même par différents "threads".
            // Il est TRES important.
            synchronized (DAO.class) {
                if (DAO.instance == null) {
                    DAO.instance = new DAO(OpenSecuritySMS.getInstance().getApplicationContext());
                }
            }
        }
        return DAO.instance;
    }

    /**
     * to open the database.
     */
    public void openDb() {
        // Pas besoin de fermer la dernière base puisque getWritableDatabase s'en charge
        mDb = mHandler.getWritableDatabase();
    }

    /**
     * to close the dataBase
     */
    public void close() {
        mDb.close();
    }

    /**
     * to insert a contact into the dataBase
     * @param c the context
     */
    public void insertContactIntoDB(Contact c) {
        mDb.beginTransaction();
        try {
            ContentValues value = new ContentValues();

            mDb.beginTransaction();

            value.put(DatabaseHandler.CONTACT_NAME, c.getName());
            value.put(DatabaseHandler.NUMBER_OF_MESSAGE, c.getNbMessages());
            value.put(DatabaseHandler.PHONE_NUMBER, c.getNumber());
            value.put(DatabaseHandler.PHOTO_URL, c.getPhotoURL());
            value.put(DatabaseHandler.THREAD_ID, c.getThreadId());

            mDb.insert(DatabaseHandler.CONTACT_TABLE_NAME, null, value);
            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            System.out.println("ERROR : Cannot insert contact ! ");
        } finally {
            mDb.endTransaction();
            Toast.makeText(OpenSecuritySMS.getInstance().getBaseContext(), "Contact inserted", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This function has to return a contact Object thanks to a phoneNumber and an access
     * to the android dataBase
     *
     * @param phoneNumber     the phoneNumber of a Contact in your phone.
     * @param contentResolver to manage access to a structured set of data in your phone
     * @return the contact who has this phoneNumber
     */
    public Contact findContactByPhoneNumber(String phoneNumber, ContentResolver contentResolver) {
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
     * This function fill an ArrayList of ConversationLine with every last
     * message for every contact
     *
     * @param contentResolver the contentResolver of the activity
     * @return an ArrayList of ConversationLine containing every last message for every contact
     */
    public ArrayList<ConversationLine> loadLastMessages(ContentResolver contentResolver) {
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
                contact = findContactByPhoneNumber(phoneNumber, contentResolver);
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

    /**
     * This function has to load bubbles of one conversation in an ArrayList of object ConversationItem.
     *
     * @param contentResolver to manage access to a structured set of data in your phone
     * @param contact         is the current contact in our selected conversation
     * @param offset          never ised for the moment
     * @param limit           the limit of bubble what we want to load. (to dispence to load all bubbles)
     * @return bubbleData the ArrayList of Bubbles in a conversation.
     */
    public ArrayList<ConversationItem> loadMessages(ContentResolver contentResolver,
                                                           Contact contact, int offset, int limit) {
        ArrayList<ConversationItem> bubbleData = new ArrayList<>();
        String content;
        boolean isMe;
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.YEAR, 1970);

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
            mBuilder.setCategory(Notification.CATEGORY_MESSAGE);
            mBuilder.setDefaults(Notification.DEFAULT_ALL);

            NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());
        }
    }

    /**
     * Get all table Details from teh sqlite_master table in Db.
     *
     * @return An ArrayList of table details.
     */
    public ArrayList<String[]> getDbTableDetails() {
        Cursor c = mDb.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table'", null);
        ArrayList<String[]> result = new ArrayList<String[]>();
        int i = 0;
        result.add(c.getColumnNames());
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            String[] temp = new String[c.getColumnCount()];
            for (i = 0; i < temp.length; i++) {
                temp[i] = c.getString(i);
            }
            result.add(temp);
        }

        return result;
    }
}
