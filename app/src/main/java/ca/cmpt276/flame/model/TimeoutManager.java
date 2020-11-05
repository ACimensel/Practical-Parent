package ca.cmpt276.flame.model;

import android.content.SharedPreferences;

import com.google.gson.Gson;

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
    private static final int MIN_TO_MILLIS = 60 * 1000;
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

    public void setMinutesEntered(int minutes) {
        minutesEntered = minutes;
        reset();
    }

    public void start() {
        if(timerState == TimerState.RUNNING) {
            return;
        }

        timerFinishTime = System.currentTimeMillis() + timerOffsetMillis;
        timerState = TimerState.RUNNING;
        persistToSharedPrefs();
    }

    public void pause() {
        timerOffsetMillis = getMillisRemaining();
        timerState = TimerState.PAUSED;
        persistToSharedPrefs();
    }

    public void reset() {
        timerOffsetMillis = minutesEntered * MIN_TO_MILLIS;
        timerState = TimerState.STOPPED;
        persistToSharedPrefs();
    }

    public long getMillisRemaining() {
        if(timerState == TimerState.RUNNING) {
            long offset = timerFinishTime - System.currentTimeMillis();
            return offset > 0 ? offset : 0;
        }
        else {
            return timerOffsetMillis;
        }
    }

    public TimerState getTimerState() {
        return timerState;
    }

    private void persistToSharedPrefs() {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        String json = (new Gson()).toJson(this);
        editor.putString(SHARED_PREFS_KEY, json);
        editor.apply();
    }
}
