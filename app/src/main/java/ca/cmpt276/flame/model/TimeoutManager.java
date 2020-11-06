package ca.cmpt276.flame.model;

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

    private static TimeoutManager timeoutManager;
    private static final int MILLIS_IN_MIN = 60000;
    private int minutesEntered;
    private long millisRemaining;
    private TimerState timerState;

    public static TimeoutManager getInstance() {
        if(timeoutManager == null) {
            timeoutManager = new TimeoutManager();
        }
        return timeoutManager;
    }

    private TimeoutManager() {
        setTimerState(TimerState.STOPPED);
    }

    public int getMinutesEntered() {
        return minutesEntered;
    }

    public void setMinutesEntered(int minutes) {
        minutesEntered = minutes;
        resetMillisRemaining();
    }

    public void resetMillisRemaining() {
        millisRemaining = minutesEntered * MILLIS_IN_MIN;
    }

    public long getMillisRemaining() {
        return millisRemaining;
    }

    public void setMillisRemaining(long millisRemaining) {
        this.millisRemaining = millisRemaining;
    }

    public TimerState getTimerState() {
        return timerState;
    }

    public void setTimerState(TimerState timerState) {
        this.timerState = timerState;
    }
}
