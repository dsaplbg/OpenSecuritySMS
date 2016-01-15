package org.opensecurity.sms.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import org.opensecurity.sms.view.ConversationActivity;
import org.opensecurity.sms.view.OpenSecuritySMS;
import org.opensecurity.sms.view.PopupConversationActivity;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Valentin on 10/11/2015.
 * Is a big listener for android. Active this piece of code when Android detect a new entrance
 * of a sms
 */
public class SMSReceiver extends BroadcastReceiver {

    /**
     * an override of BroadcastReceiver function. To execute code whe android detect an intent.
     * @param context interface to global information about an application environment.
     * @param intent abstract description of an operation to be performed.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");

                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++)
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                if (messages.length > -1) {
                    final String messageBody = messages[0].getMessageBody();
                    final String phoneNumber = messages[0].getDisplayOriginatingAddress();
                    Contact contact = DAO.getInstance().findContactByPhoneNumber(phoneNumber, context.getContentResolver());

                    if (OpenSecuritySMS.getInstance() != null) {
                        OpenSecuritySMS.getInstance().update();

                        if (ConversationActivity.getInstance() != null) {
                            Intent intentConversationActivity = new Intent(context, ConversationActivity.class);
                            intent.putExtra("Contact", contact);

                            ConversationActivity.getInstance().update(intentConversationActivity);
                        }

                        HashMap<String, Serializable> save = new HashMap<>();
                        save.put("Contact", contact);
                        save.put("Message", messageBody);
                        DAO.getInstance().makeNotification(contact.getName(), messageBody, contact.getPhoto(context.getContentResolver()), OpenSecuritySMS.getInstance(), PopupConversationActivity.class, save);
                    }
                }
            }
        }
    }
}