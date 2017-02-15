package com.vote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
 * Created by Admin on 15.02.2017.
 */
//Make app as Launcher after reboot
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myIntent = new Intent(context, MainActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent);
    }
}
