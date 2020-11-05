package ca.cmpt276.flame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import ca.cmpt276.flame.model.TimeoutManager;

/**
 * TimeoutActivity shows the currently running timer, and allows the user
 * to pause, reset, resume or cancel the timer
 */
public class TimeoutActivity extends AppCompatActivity {

    private static final int MILLIS_IN_MIN = 60000;
    private static final int MILLIS_IN_SEC = 1000;
    private static final int VIBRATION_TIME_IN_MS = 5000;

    private final TimeoutManager timeoutManager = TimeoutManager.getInstance();
    private CountDownTimer countDownTimer;
    private Button pauseTimerBtn;
    private Button resetBtn;
    private TextView countdownTimeTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeout);
        setupToolbar();
        setupPauseButton();
        setUpResetButton();
        setupTimer();
        updateCountdownTimeTxt();
        updateButtons();

        if(timeoutManager.getTimerState() == TimeoutManager.TimerState.RUNNING) {
            countDownTimer.start();
        }
    }

    private void updateButtons() {
        switch(timeoutManager.getTimerState()) {
            case RUNNING:
                pauseTimerBtn.setText(R.string.pause_btn_text);
                resetBtn.setText(R.string.reset_btn_text);
                break;
            case PAUSED:
                pauseTimerBtn.setText(R.string.resume_btn_text);
                resetBtn.setText(R.string.reset_btn_text);
                break;
            case STOPPED:
                pauseTimerBtn.setText(R.string.start_btn_text);
                resetBtn.setText(R.string.cancel_btn_text);
                break;
        }
    }

    private void updateCountdownTimeTxt() {
        if(countdownTimeTxt == null) {
            countdownTimeTxt = findViewById(R.id.timeout_txtTimeRemaining);
        }

        long timeInMillis = timeoutManager.getMillisRemaining();

        long minRemaining = timeInMillis / MILLIS_IN_MIN;
        long secRemaining = (timeInMillis % MILLIS_IN_MIN) / MILLIS_IN_SEC;
        String timeStr = String.format(Locale.getDefault(), "%d:%02d", minRemaining, secRemaining);
        countdownTimeTxt.setText(timeStr);
    }

    private void setupPauseButton() {
        pauseTimerBtn = findViewById(R.id.timeout_btnPause);

        pauseTimerBtn.setOnClickListener(view -> {
            switch (timeoutManager.getTimerState()) {
                case RUNNING:
                    // "Pause" button
                    countDownTimer.cancel();
                    timeoutManager.pause(getApplicationContext());
                    break;
                case PAUSED:
                    // "Resume" button if timer is paused, fall through
                case STOPPED:
                    // "Start" button if timer is stopped
                    countDownTimer.start();
                    timeoutManager.start(getApplicationContext());
                    break;
            }

            updateButtons();
        });
    }

    private void setUpResetButton() {
        resetBtn = findViewById(R.id.timeout_btnReset);

        // reset button cancels the previous timer and sets remaining time to the starting time
        resetBtn.setOnClickListener(view -> {
            if (timeoutManager.getTimerState() == TimeoutManager.TimerState.STOPPED) {
                finish();
            } else {
                countDownTimer.cancel();
                timeoutManager.reset(getApplicationContext());
                updateCountdownTimeTxt();
                updateButtons();
            }
        });
    }

    private void setupTimer() {
        countDownTimer = new CountDownTimer(timeoutManager.getMillisRemaining(), MILLIS_IN_SEC / 2) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateCountdownTimeTxt();
            }

            @Override
            public void onFinish() {
                updateButtons();
                countdownTimeTxt.setText(R.string.finished);
            }
        };
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.timeout);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    protected static Intent makeIntent(Context context) {
        return new Intent(context, TimeoutActivity.class);
    }

}