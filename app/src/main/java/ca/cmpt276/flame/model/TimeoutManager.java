package ca.cmpt276.flame.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

import androidx.core.app.AlarmManagerCompat;

import static ca.cmpt276.flame.TimerAlarmReceiver.cancelNotifications;
import static ca.cmpt276.flame.TimerAlarmReceiver.getNotificationPendingIntent;

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
    private TimerState timerState = TimerState.STOPPED;
    private int minutesEntered;
    private double rateModifier = 1.0;
    private long timerFinishTime;
    private long timeLeftMillis;

    public static TimeoutManager getInstance() {
        if(timeoutManager == null) {
            timeoutManager = (TimeoutManager) PrefsManager.restoreObj(SHARED_PREFS_KEY, TimeoutManager.class);
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

    public double getRateModifier() {
        return rateModifier;
    }

    public void setRateModifier(Context context, double newRateModifier) {
        if(newRateModifier <= 0.0) {
            throw new IllegalArgumentException("TimeoutManager expects non-zero, positive rate modifier");
        }

        timeLeftMillis = (long) (getMillisRemaining() / newRateModifier);
        timerFinishTime = System.currentTimeMillis() + timeLeftMillis;
        rateModifier = newRateModifier;

        if(getTimerState() == TimerState.RUNNING) {
            cancelAlarm(context);
            setAlarm(context);
        }

        persistToSharedPrefs();
    }

    public void start(Context context) {
        if(getTimerState() == TimerState.RUNNING) {
            return;
        }

        timerFinishTime = System.currentTimeMillis() + timeLeftMillis;
        timerState = TimerState.RUNNING;
        setAlarm(context);
        persistToSharedPrefs();
    }

    public void pause(Context context) {
        timeLeftMillis = getMillisRemainingReal();
        timerState = TimerState.PAUSED;
        cancelAlarm(context);
        persistToSharedPrefs();
    }

    public void reset(Context context) {
        rateModifier = 1.0;
        timeLeftMillis = minutesEntered * MILLIS_IN_MIN;
        timerState = TimerState.STOPPED;
        cancelAlarm(context);
        persistToSharedPrefs();
    }

    private void setAlarm(Context context) {
        AlarmManager alarmManager = getAlarmManager(context);
        PendingIntent pendingIntent = getNotificationPendingIntent(context);
        AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager, AlarmManager.RTC_WAKEUP, timerFinishTime, pendingIntent);
    }

    public void cancelAlarm(Context context) {
        AlarmManager alarmManager = getAlarmManager(context);
        alarmManager.cancel(getNotificationPendingIntent(context));
        cancelNotifications(context);
    }

    private AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private long getMillisRemainingReal() {
        if(timerState == TimerState.RUNNING) {
            long offset = timerFinishTime - System.currentTimeMillis();
            return offset > 0 ? offset : 0;
        } else {
            return timeLeftMillis;
        }
    }

    // this function will "lie" about how many milliseconds are remaining based upon the rateModifier
    public long getMillisRemaining() {
        return (long) (getMillisRemainingReal() * rateModifier);
    }

    public TimerState getTimerState() {
        if(timerState == TimerState.RUNNING && getMillisRemainingReal() == 0) {
            timerState = TimerState.STOPPED;
        }

        return timerState;
    }

    private void persistToSharedPrefs() {
        PrefsManager.persistObj(SHARED_PREFS_KEY, this);
    }
}
