package com.sandro.oneinvite.utils;

import com.sandro.oneinvite.model.NotificationSettings;

public class NotificationSettingsUtils {

    public static Integer getNumberOfHoursBetweenCheck(Integer notificationSettings) {
        if (NotificationSettings.NEVER.ordinal() == notificationSettings) {
            return null;
        }

        if (NotificationSettings.EVERY_HOUR.ordinal() == notificationSettings) {
            return 1;
        }

        if (NotificationSettings.EVERY_TWO_HOURS.ordinal() == notificationSettings) {
            return 2;
        }

        if (NotificationSettings.EVERY_FOUR_HOURS.ordinal() == notificationSettings) {
            return 4;
        }

        if (NotificationSettings.EVERY_EIGHT_HOURS.ordinal() == notificationSettings) {
            return 8;
        }

        if (NotificationSettings.EVERY_TWELVE_HOURS.ordinal() == notificationSettings) {
            return 12;
        }

        if (NotificationSettings.EVERY_DAY.ordinal() == notificationSettings) {
            return 24;
        }

        return null;
    }

}
