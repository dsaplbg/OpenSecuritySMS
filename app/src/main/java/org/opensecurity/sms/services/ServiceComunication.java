package org.opensecurity.sms.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import org.opensecurity.sms.model.SMSReceiver;

/**
 * Created by couim on 22/02/16.
 */
public class ServiceComunication extends Service {
    private SMSReceiver smsReceiver;
    private IntentFilter intentFilter;
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    @Override
    public void onCreate() {
        super.onCreate();

        smsReceiver = new SMSReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(SMS_RECEIVED);
        registerReceiver(smsReceiver, intentFilter);
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
