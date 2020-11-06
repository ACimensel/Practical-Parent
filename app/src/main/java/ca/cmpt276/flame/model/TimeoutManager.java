package ca.cmpt276.flame.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.AlarmManagerCompat;

import com.google.gson.Gson;

import ca.cmpt276.flame.TimerAlarmReceiver;

/**
 * TimeoutManager is a singleton class that manages the current state of the timer:
 * - the number of minutes entered by the user
 * - the number of milliseconds remaining
 * - whether the timer is running, paused or stopped
 */
public class TimeoutManager {
    /** TimerState represents the current state of the timer */
    public enum TimerState {
        STOPPED,
        RUNNING,
        PAUSED
    }

    private static final String SHARED_PREFS_KEY = "SHARED_PREFS_TIMEOUT_MANAGER";
    private static final int MILLIS_IN_MIN = 60000;
    private static TimeoutManager timeoutManager;
    private static SharedPreferences sharedPrefs;
    private TimerState timerState = TimerState.STOPPED;
    private int minutesEntered;
    private long timerFinishTime;
    private long timerOffsetMillis;

    public static void init(SharedPreferences sharedPrefs) {
        if(timeoutManager != null) {
            return;
        }

        TimeoutManager.sharedPrefs = sharedPrefs;
        String json = sharedPrefs.getString(SHARED_PREFS_KEY, "");

        if(json != null && !json.isEmpty()) {
            timeoutManager = (new Gson()).fromJson(json, TimeoutManager.class);
        } else {
            timeoutManager = new TimeoutManager();
        }
    }

    public static TimeoutManager getInstance() {
        if(timeoutManager == null) {
            throw new IllegalStateException("TimeoutManager must be initialized before use");
        }
        return timeoutManager;
    }

    private TimeoutManager() {
    }

    public int getMinutesEntered() {
        return minutesEntered;
    }

    public void setMinutesEntered(Context context, int minutes) {
        minutesEntered = minutes;
        reset(context);
    }

    public void start(Context context) {
        if(getTimerState() == TimerState.RUNNING) {
            return;
        }

        timerFinishTime = System.currentTimeMillis() + timerOffsetMillis;
        timerState = TimerState.RUNNING;
        setAlarm(context);
        persistToSharedPrefs();
    }

    public void pause(Context context) {
        timerOffsetMillis = getMillisRemaining();
        timerState = TimerState.PAUSED;
        cancelAlarm(context);
        persistToSharedPrefs();
    }

    public void reset(Context context) {
        timerOffsetMillis = minutesEntered * MILLIS_IN_MIN;
        timerState = TimerState.STOPPED;
        cancelAlarm(context);
        persistToSharedPrefs();
    }

    private void setAlarm(Context context) {
        AlarmManager alarmManager = getAlarmManager(context);
        PendingIntent pendingIntent = getNotificationPendingIntent(context);
        AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager, AlarmManager.RTC_WAKEUP, timerFinishTime, pendingIntent);
    }

    private void cancelAlarm(Context context) {
        AlarmManager alarmManager = getAlarmManager(context);
        alarmManager.cancel(getNotificationPendingIntent(context));
    }

    private AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private PendingIntent getNotificationPendingIntent(Context context) {
        Intent intent = new Intent(context, TimerAlarmReceiver.class);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public long getMillisRemaining() {
        if(timerState == TimerState.RUNNING) {
            long offset = timerFinishTime - System.currentTimeMillis();
            return offset > 0 ? offset : 0;
        } else {
            return timerOffsetMillis;
        }
    }

    public TimerState getTimerState() {
        if(timerState == TimerState.RUNNING && getMillisRemaining() == 0) {
            timerState = TimerState.STOPPED;
        }

        return timerState;
    }

    private void persistToSharedPrefs() {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        String json = (new Gson()).toJson(this);
        editor.putString(SHARED_PREFS_KEY, json);
        editor.apply();
    }
}
