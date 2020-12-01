package ca.cmpt276.flame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

import ca.cmpt276.flame.model.BGMusicPlayer;
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
    public static final int DECIMAL_TO_PERCENTAGE = 100;
    public static final int TIMER_SPEED_MIN_VALUE = 0;
    public static final int TIMER_SPEED_MAX_VALUE = 15;
    public static final int PICKER_VALUES_INCREMENT = 25;
    private final TimeoutManager timeoutManager = TimeoutManager.getInstance();
    private double speedPercentage = timeoutManager.getRateModifier() * DECIMAL_TO_PERCENTAGE;
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
        setUpSettingsBtn();
        setupPauseButton();
        setUpResetButton();
        setupTimer();
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

    private void updateTimerProgress() {
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

    private void updateTimerSpeedTxt() {
        TextView timeSpeed = findViewById(R.id.timeoutActivity_timeSpeedView);
        timeSpeed.setText(getString(R.string.timer_speed, timeoutManager.getRateModifier() * DECIMAL_TO_PERCENTAGE));
    }

    private void setUpSettingsBtn() {
        ImageButton settingBtn = findViewById(R.id.timeoutActivity_settingsImageButton);
        settingBtn.setBackgroundColor(Color.TRANSPARENT);
        settingBtn.setOnClickListener(view -> {
            chooseSpeedDialog();
        });
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
                updateTimerProgress();
                updateButtons();
                speedPercentage = timeoutManager.getDefaultSpeedPercentage();
                updateTimerSpeedTxt();
            }
        });
    }

    private void setupTimer() {
        countDownTimer = new CountDownTimer(Long.MAX_VALUE, COUNTDOWN_INTERVAL_MILLIS) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimerProgress();
            }

            @Override
            public void onFinish() {
                // method is required but nothing needs to happen here
                // instead, the TimeoutManager manages what happens when the timer finishes
            }
        };
    }

    private void chooseSpeedDialog() {
        NumberPicker speedPicker = new NumberPicker(this);
        speedPicker.setWrapSelectorWheel(true);
        speedPicker.setMinValue(TIMER_SPEED_MIN_VALUE);
        speedPicker.setMaxValue(TIMER_SPEED_MAX_VALUE);
        String[] numberString = new String[TIMER_SPEED_MAX_VALUE + 1];
        int j = 0;
        while(j <= TIMER_SPEED_MAX_VALUE) {
            numberString[j] = String.valueOf((j * PICKER_VALUES_INCREMENT) + PICKER_VALUES_INCREMENT);
            j++;
        }
        speedPicker.setDisplayedValues(numberString);
        speedPicker.setValue((int)(((timeoutManager.getRateModifier() * DECIMAL_TO_PERCENTAGE) - PICKER_VALUES_INCREMENT) / PICKER_VALUES_INCREMENT));
        LinearLayout numberLayout = setLinearNumberLayout(speedPicker);
        new AlertDialog.Builder(TimeoutActivity.this)
                .setTitle(R.string.choose_time_speed)
                .setView(numberLayout)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    speedPercentage = (PICKER_VALUES_INCREMENT * speedPicker.getValue()) + PICKER_VALUES_INCREMENT;
                    timeoutManager.setRateModifier(this, speedPercentage / DECIMAL_TO_PERCENTAGE);
                    updateTimerSpeedTxt();
                })
                .setNegativeButton(R.string.cancel, null).show();
    }

    private LinearLayout setLinearNumberLayout(NumberPicker numberPicker) {
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.addView(numberPicker);
        layout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        return layout;
    }


    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.timeout);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateTimerProgress();
        updateButtons();
        updateTimerSpeedTxt();
        if(timeoutManager.getTimerState() == TimeoutManager.TimerState.RUNNING) {
            countDownTimer.start();
        }

        BGMusicPlayer.resumeBgMusic();
    }

    protected static Intent makeIntent(Context context) {
        return new Intent(context, TimeoutActivity.class);
    }

}