package com.sandro.oneinvite.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.sandro.oneinvite.receiver.service.SchedulingService;
import com.sandro.oneinvite.sharedpreferences.SharedPreferencesManager;
import com.sandro.oneinvite.utils.NotificationSettingsUtils;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, SchedulingService.class);

        startWakefulService(context, service);
    }

    public void setAlarm(Context context) {
        Integer intervalBetweenCheck = NotificationSettingsUtils.getNumberOfHoursBetweenCheck(SharedPreferencesManager.getNotificationSettings(context));

        if (intervalBetweenCheck == null) {
            return;
        }

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
            intervalBetweenCheck,
            intervalBetweenCheck * AlarmManager.INTERVAL_HOUR, alarmIntent);

        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP);
    }

    public void cancelAlarm(Context context) {

        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }

        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP);
    }

}
