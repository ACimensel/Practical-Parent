package ca.cmpt276.flame.model;

/**
 * TimeoutActivity: TODO add proper comment once activity created
 */
public class TimeOutManager {
    private static TimeOutManager timeOutManager;
    private int timerTime = 1;
    public static TimeOutManager getInstance() {
        if(timeOutManager == null) {
            timeOutManager = new TimeOutManager();
        }
        return timeOutManager;
    }

    private TimeOutManager() {
    }

    public void setTimerTime(int timeEntered) {
        timerTime = timeEntered;
    }

    public int getTimerTime() {
        return timerTime;
    }
}
