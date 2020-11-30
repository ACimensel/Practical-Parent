package ca.cmpt276.flame;

import android.content.Context;
import android.content.Intent;
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
    public static final int DIVIDER_FOR_PERCENTAGE_TO_NUMBER = 100;
    private double speedPercentage = 100;
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

    private void updateTimerSpeedTxt() {
        TextView timeSpeed = findViewById(R.id.timoutActivity_timeSpeedView);
        timeSpeed.setText(getString(R.string.timer_speed, speedPercentage));
    }

    private void setUpSettingsBtn() {
        ImageButton settingBtn = findViewById(R.id.timeoutActivity_settingsImageButton);
        settingBtn.setOnClickListener(view->{
            setUpSpeedChooser();
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
                updateUI();
                updateButtons();
                speedPercentage = 100;
                updateTimerSpeedTxt();
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
            public void onFinish() {
                // method is required but nothing needs to happen here
                // instead, the TimeoutManager manages what happens when the timer finishes
            }
        };
    }

    private void setUpSpeedChooser() {
        final NumberPicker chooseSpeed = new NumberPicker(this);
        chooseSpeed.setMaxValue(400);
        chooseSpeed.setMinValue(25);
        chooseSpeed.setWrapSelectorWheel(true);
        LinearLayout numberLayout = setLinearNumberLayout(chooseSpeed);

        chooseSpeedDialog(chooseSpeed, numberLayout);
    }

    private void chooseSpeedDialog(NumberPicker chooseSpeed, LinearLayout numberLayout) {
        new AlertDialog.Builder(TimeoutActivity.this)
                .setTitle(R.string.choose_time_speed)
                .setView(numberLayout)
                .setPositiveButton("Ok",(dialogInterface, i) -> {
                    speedPercentage = chooseSpeed.getValue();
                    timeoutManager.setRateModifier(this, speedPercentage / DIVIDER_FOR_PERCENTAGE_TO_NUMBER);
                    updateTimerSpeedTxt();
                    timeoutManager.pause(this);
                    timeoutManager.start(this);
                })
                .setNegativeButton(R.string.cancel, null).show();
    }

    private LinearLayout setLinearNumberLayout(NumberPicker numbers) {
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.addView(numbers);
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

        updateUI();
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