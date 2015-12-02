package org.opensecurity.sms.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import org.opensecurity.sms.controller.Controller;
import org.opensecurity.sms.view.ConversationActivity;
import org.opensecurity.sms.view.OpenSecuritySMS;

/**
 * Created by Valentin on 10/11/2015.
 */
public class SMSReceiver extends BroadcastReceiver {
    private final String ACTION_RECEIVE_SMS = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_RECEIVE_SMS)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");

                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++)
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                if (messages.length > -1) {
                    final String messageBody = messages[0].getMessageBody();
                    final String phoneNumber = messages[0].getDisplayOriginatingAddress();
                    Contact contact = Controller.getContact(phoneNumber, context.getContentResolver());

                    if (OpenSecuritySMS.getInstance() != null) {
                        OpenSecuritySMS.getInstance().update();

                        if (ConversationActivity.getInstance() != null) {
                            Intent intentConversationActivity = new Intent(context, ConversationActivity.class);
                            intent.putExtra("ConversationLine", OpenSecuritySMS.getInstance().getConversationLineByContact(contact));

                            ConversationActivity.getInstance().update(intentConversationActivity);
                        }

                        Bundle saveOpenSecuritySMS = new Bundle();
                        saveOpenSecuritySMS.putSerializable("ConversationLine", OpenSecuritySMS.getInstance().getConversationLineByContact(contact));
                        Controller.makeNotification(contact.getName(), messageBody, contact.getPhoto(context.getContentResolver()), ConversationActivity.getInstance(), saveOpenSecuritySMS);
                    }
                }
            }
        }
    }
}