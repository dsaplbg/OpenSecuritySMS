package org.opensecurity.sms.model;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

import org.opensecurity.sms.R;
import org.opensecurity.sms.activities.ConversationActivity;
import org.opensecurity.sms.model.database.ContactDAO;
import org.opensecurity.sms.model.database.MessageDAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Engine {
    /**
     * The Engine is used by all activities. It contains various useful functions to access to the database.
     */


    private static final String SMS_SEND_ACTION = "CTS_SMS_SEND_ACTION";
    private static final String SMS_DELIVERY_ACTION = "CTS_SMS_DELIVERY_ACTION";
    public static final String CONTACT_KEY = "Contact_key";


    /**
     * the DAO to access to the database with contact functions
     */
    private ContactDAO contactDAO;

    /**
     * the DAO to access to the database with message functions
     */
    private MessageDAO messageDAO;

    /**
     * the context of the app
     */
    private Context context;

    /**
     * constructor
     */
    public Engine(Context context) {
        setContext(context);
        setContactDAO(new ContactDAO(this.getContext()));
        setMessageDAO(new MessageDAO(this.getContext(), this.getContactDAO()));
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
            this.getMessageDAO().insertSMSSentIntoDefaultDataBase(c, message);
            System.out.println("Insertion terminée. ");

            ArrayList<String> messages = smsManager.divideMessage(message);
            ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
            ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();

            for (int i = 0; i < messages.size(); i++) {
                sentIntents.add(PendingIntent.getBroadcast(c, 0, new Intent(SMS_SEND_ACTION), 0));
                deliveryIntents.add(PendingIntent.getBroadcast(c, 0, new Intent(SMS_DELIVERY_ACTION), 0));
            }

            smsManager.sendMultipartTextMessage(contact.getPhoneNumber(),
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
     */
    public void makeNotificationReceivedMessage(Contact c, String content, Activity activity,
                                 Class activityRunClass, HashMap<String, Serializable> save) {
        Intent intent = new Intent(this.getContext(), activityRunClass);
        intent.putExtra(this.CONTACT_KEY, c);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.getContext(), (int) System.currentTimeMillis(),
                intent, 0);

        Notification n = new Notification.Builder(this.getContext())
                .setContentTitle(c.getName())
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setLargeIcon(c.getPhoto(this.getContext().getContentResolver()))
                .setSmallIcon(R.drawable.bulle_not_me)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) this.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
    }

    public MessageDAO getMessageDAO() {
        return messageDAO;
    }

    public void setMessageDAO(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
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