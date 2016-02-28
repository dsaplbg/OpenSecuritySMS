package org.opensecurity.sms.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Just a class used to start a service ServiceCommunication at the boot time of Android
 * Created by calliste on 28/02/16.
 */
public class StartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startService = new Intent(context, ServiceComunication.class);
        context.startService(startService);
    }
}
