package com.sandro.oneinvite.receiver.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.sandro.oneinvite.MainActivity;
import com.sandro.oneinvite.R;
import com.sandro.oneinvite.model.Result;
import com.sandro.oneinvite.receiver.AlarmReceiver;
import com.sandro.oneinvite.restclient.RestClient;
import com.sandro.oneinvite.sharedpreferences.SharedPreferencesManager;
import com.sandro.oneinvite.utils.InternetUtils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SchedulingService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    public SchedulingService() {
        super("SchedulingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (InternetUtils.isInternetAvailable(SchedulingService.this) && !TextUtils.isEmpty(SharedPreferencesManager.getUserId(SchedulingService.this))) {
            new RestClient().getOnePlusService().getUserRank(SharedPreferencesManager.getUserId(SchedulingService.this), new Callback<Result>() {

                @Override
                public void success(Result result, Response response) {
                    if (result.getData() != null) {

                        if (Double.parseDouble(result.getData().getRank()) > Double.parseDouble(SharedPreferencesManager.getUserPosition(SchedulingService.this))) {
                            sendNotification(getString(R.string.notifications_lost_positions));
                        }

                        if (Double.parseDouble(result.getData().getRank()) < Double.parseDouble(SharedPreferencesManager.getUserPosition(SchedulingService.this))) {
                            sendNotification(getString(R.string.notifications_earned_positions));
                        }

                        SharedPreferencesManager.setUserPosition(SchedulingService.this, result.getData().getRank());
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }

            });
        }

        AlarmReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.notification_title))
            .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
            .setAutoCancel(true)
            .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
