package com.radioentertainment.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.radioentertainment.radio.PlayService;

public class SleepTimeReceiver extends BroadcastReceiver {

    SharedPref sharedPref;

    @Override
    public void onReceive(Context context, Intent intent) {

        sharedPref = new SharedPref(context);

        if (sharedPref.getIsSleepTimeOn()) {
            sharedPref.setSleepTime(false, 0,0);
        }

        Intent intent_close = new Intent(context, PlayService.class);
        intent_close.setAction(PlayService.ACTION_STOP);
        context.startService(intent_close);
    }
}