package com.sandro.oneinvite.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sandro.oneinvite.sharedpreferences.SharedPreferencesManager;
import com.sandro.oneinvite.utils.NotificationSettingsUtils;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if (NotificationSettingsUtils.getNumberOfHoursBetweenCheck(SharedPreferencesManager.getNotificationSettings(context)) != null) {
                new AlarmReceiver().setAlarm(context);
            }
        }
    }

}
