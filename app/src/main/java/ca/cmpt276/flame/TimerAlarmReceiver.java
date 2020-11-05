package ca.cmpt276.flame;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static androidx.core.app.NotificationCompat.FLAG_INSISTENT;

/**
 * TimerAlarmReceiver runs when the timer is finished and sends the user a notification
 */
public class TimerAlarmReceiver extends BroadcastReceiver {
    public static final String NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_TIMER_ALARM";
    public static final long[] VIBRATION_PATTERN = {500, 1000, 500, 1000, 500, 1000};

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.practical_parent_icon)
                .setContentTitle(context.getString(R.string.timer_notification_title))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVibrate(VIBRATION_PATTERN)
                .setSound(Settings.System.DEFAULT_ALARM_ALERT_URI);

        Notification notification = builder.build();
        notification.flags = FLAG_INSISTENT;

        // generate a unique notificationId using the current date/time
        int notificationId = Integer.parseInt(new SimpleDateFormat("MMddHHmmss",  Locale.US).format(new Date()));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, notification);
    }
}
