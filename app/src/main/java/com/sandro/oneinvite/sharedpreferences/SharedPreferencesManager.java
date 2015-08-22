package com.sandro.oneinvite.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.sandro.oneinvite.model.NotificationSettings;

public class SharedPreferencesManager {

    private static final String PREFS_NAME = "com.sandro.oneinvite.PREFERENCE_FILE_KEY";
    private static final String PREFS_NOTIFICATION_SETTINGS = "NOTIFICATION_SETTINGS";
    private static final String PREFS_USER_ID = "USER_ID";
    private static final String PREFS_USER_POSITION = "USER_POSITION";

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static Integer getNotificationSettings(Context context) {
        return getPreferences(context).getInt(PREFS_NOTIFICATION_SETTINGS, NotificationSettings.EVERY_TWO_HOURS.ordinal());
    }

    public static void setNotificationSettings(Context context, int notificationSettings) {
        SharedPreferences.Editor editor = getPreferences(context).edit();

        editor.putInt(PREFS_NOTIFICATION_SETTINGS, notificationSettings);
        editor.apply();
    }

    public static String getUserId(Context context) {
        return getPreferences(context).getString(PREFS_USER_ID, null);
    }

    public static void setUserId(Context context, String email) {
        SharedPreferences.Editor editor = getPreferences(context).edit();

        editor.putString(PREFS_USER_ID, email);
        editor.apply();
    }

    public static String getUserPosition(Context context) {
        return getPreferences(context).getString(PREFS_USER_POSITION, null);
    }

    public static void setUserPosition(Context context, String position) {
        SharedPreferences.Editor editor = getPreferences(context).edit();

        editor.putString(PREFS_USER_POSITION, position);
        editor.apply();
    }

}
