package ca.cmpt276.flame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ProgressBar;
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
    private static final int PROGRESS_BAR_STEPS = 1000;
    private static final int COUNTDOWN_INTERVAL_MILLIS = 40;

    private final TimeoutManager timeoutManager = TimeoutManager.getInstance();
    private CountDownTimer countDownTimer;
    private Button pauseTimerBtn;
    private Button resetBtn;
    private TextView countdownTimeTxt;
    private ProgressBar circularProgressBar;
    private float millisEntered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeout);

        millisEntered = timeoutManager.getMinutesEntered() * MILLIS_IN_MIN;
        circularProgressBar = findViewById(R.id.timeout_progressBar);
        circularProgressBar.setMax(PROGRESS_BAR_STEPS);

        setupToolbar();
        setupPauseButton();
        setUpResetButton();
        setupTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateUI();
        updateButtons();

        if(timeoutManager.getTimerState() == TimeoutManager.TimerState.RUNNING) {
            countDownTimer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
    }

    private void updateButtons() {
        switch(timeoutManager.getTimerState()) {
            case RUNNING:
                pauseTimerBtn.setText(R.string.pause);
                resetBtn.setText(R.string.reset);
                break;
            case PAUSED:
                pauseTimerBtn.setText(R.string.resume);
                resetBtn.setText(R.string.reset);
                break;
            case STOPPED:
                pauseTimerBtn.setText(R.string.start);
                resetBtn.setText(R.string.cancel);
                break;
        }
    }

    private void updateUI() {
        if(countdownTimeTxt == null) {
            countdownTimeTxt = findViewById(R.id.timeout_txtTimeRemaining);
        }

        long millisRemaining = timeoutManager.getMillisRemaining();
        int progressBarStepsLeft = (int) (PROGRESS_BAR_STEPS * millisRemaining / millisEntered);

        circularProgressBar.setProgress(progressBarStepsLeft);

        long minRemaining = millisRemaining / MILLIS_IN_MIN;
        long secRemaining = (millisRemaining % MILLIS_IN_MIN) / MILLIS_IN_SEC;
        String timeStr = String.format(Locale.getDefault(), "%d:%02d", minRemaining, secRemaining);

        countdownTimeTxt.setText(timeStr);

        if(timeoutManager.getMillisRemaining() == 0) {
            countDownTimer.cancel();
            countdownTimeTxt.setText(R.string.finished);
            updateButtons();
        }
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
                timeoutManager.cancelAlarm(getApplicationContext());
                finish();
            } else {
                countDownTimer.cancel();
                timeoutManager.reset(getApplicationContext());
                updateUI();
                updateButtons();
            }
        });
    }

    private void setupTimer() {
        countDownTimer = new CountDownTimer(Long.MAX_VALUE, COUNTDOWN_INTERVAL_MILLIS) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateUI();
            }

            @Override
            public void onFinish() { }
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