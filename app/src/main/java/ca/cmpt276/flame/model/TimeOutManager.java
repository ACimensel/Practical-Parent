package ca.cmpt276.flame.model;

import android.content.Context;
import android.content.Intent;

public class TimeOutManager {
    private static TimeOutManager timeOutManager;
    private int timer_time = 1;
    public static TimeOutManager getInstance(){
        if(timeOutManager == null) {
            timeOutManager = new TimeOutManager();
        }
        return timeOutManager;
    }

    private TimeOutManager() {
    }

    public void setTimer_time(int timeEntered) {
        timer_time = timeEntered;
    }

    public int getTimer_time() {
        return timer_time;
    }
}
