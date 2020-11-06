package ca.cmpt276.flame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Build;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
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
    private static final int VIBRATION_TIME_IN_MS = 5000;
    private static final int PROGRESS_BAR_STEPS = 1000;
    private static final int COUNTDOWN_INTERVAL_MILLIS = 20;

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

    private void setupTimer() {
        countdownTimeTxt = findViewById(R.id.timeout_txtTimeRemaining);
        updateCountdownTimeTxt();
        countDownTimer = startTimer();
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

    private void updateCountdownTimeTxt() {
        long millisRemaining = timeoutManager.getMillisRemaining();
        int progressBarStepsLeft = (int) (PROGRESS_BAR_STEPS * millisRemaining / millisEntered);

        circularProgressBar.setProgress(progressBarStepsLeft);

        long minRemaining = millisRemaining / MILLIS_IN_MIN;
        long secRemaining = (millisRemaining % MILLIS_IN_MIN) / MILLIS_IN_SEC;
        String timeStr = String.format(Locale.getDefault(), "%d:%02d", minRemaining, secRemaining);

        countdownTimeTxt.setText(timeStr);
    }

    private void setupPauseButton() {
        pauseTimerBtn = findViewById(R.id.timeout_btnPause);

        // pause button which also act as resume or start button
        pauseTimerBtn.setOnClickListener(view -> {
            switch (timeoutManager.getTimerState()) {
                case RUNNING:
                    timeoutManager.setTimerState(TimeoutManager.TimerState.PAUSED);
                    countDownTimer.cancel();
                    updateButtons();
                    break;
                case PAUSED:
                    // same behaviour for both buttons
                case STOPPED:
                    // As a resume or start button, start the timer
                    countDownTimer = startTimer();
                    break;
            }
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
                timeoutManager.resetMillisRemaining();
                timeoutManager.setTimerState(TimeoutManager.TimerState.STOPPED);
                updateCountdownTimeTxt();
                updateButtons();
            }
        });
    }

    private CountDownTimer startTimer() {
        timeoutManager.setTimerState(TimeoutManager.TimerState.RUNNING);
        updateButtons();

        long timeInMillis = timeoutManager.getMillisRemaining();

        return new CountDownTimer(timeInMillis, COUNTDOWN_INTERVAL_MILLIS) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeoutManager.setMillisRemaining(millisUntilFinished);
                updateCountdownTimeTxt();
            }

            @Override
            public void onFinish() {
                timeoutManager.setTimerState(TimeoutManager.TimerState.STOPPED);
                timeoutManager.resetMillisRemaining();
                updateButtons();
                countdownTimeTxt.setText(R.string.finished);
                circularProgressBar.setProgress(0);

                // code from  https://stackoverflow.com/a/13950364
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(VIBRATION_TIME_IN_MS, VibrationEffect.DEFAULT_AMPLITUDE));

                } else {
                    vibrator.vibrate(VIBRATION_TIME_IN_MS);
                }
            }
        }.start();
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