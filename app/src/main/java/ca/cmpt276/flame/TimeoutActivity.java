package ca.cmpt276.flame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Build;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.TextView;

import java.text.MessageFormat;

import ca.cmpt276.flame.model.TimeOutManager;

/**
 * TimeoutActivity: TODO add proper comment once activity created
 */
public class TimeoutActivity extends AppCompatActivity {

    public static final int CONVERT_MIN_TO_MILLIS = 60000;
    public static final int VIBRATION_TIME_IN_MS = 5000;
    public static final int COUNTDOWN_VALUE_IN_MS = 1000;
    private Button resetBtn;
    private Button pauseTimerBtn;

    @SuppressLint({"ResourceType", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeout);
        setupToolbar();
        resetBtn = (Button) findViewById(R.id.reset_timer_button);
        pauseTimerBtn = (Button) findViewById(R.id.pause_timer_button);
        TimeOutManager timeOutManager = TimeOutManager.getInstance();
        TextView countTime = findViewById(R.id.time_left_value);
        countTime.setText("" + timeOutManager.getTimerTime() + ":00");
        long[] timeRemaining = new long[1];
        timeRemaining[0] = timeOutManager.getTimerTime() * CONVERT_MIN_TO_MILLIS;
        CountDownTimer[] countDownTimer = setUpCountDownTimer(countTime, timeRemaining);
        setUpButtons(timeOutManager, countTime, timeRemaining, countDownTimer);
    }

    private void setUpResetButton(TimeOutManager timeOutManager, TextView countTime, long[] timeRemaining, CountDownTimer countDownTimer) {
        //reset button cancel the previous timer and sets remaining timer time to the starting time
        resetBtn.setOnClickListener(view -> {
            countDownTimer.cancel();
            if(resetBtn.getText().toString().equals("Reset")) {
                timeRemaining[0] = timeOutManager.getTimerTime() * CONVERT_MIN_TO_MILLIS;
                countTime.setText(MessageFormat.format("{0}:00", timeOutManager.getTimerTime()));
                pauseTimerBtn.setText(R.string.start_btn_text);
                resetBtn.setText(R.string.cancel_btn_text);
            } else {
                finish();
            }
        });
    }

    private void setUpButtons(TimeOutManager timeOutManager, TextView countTime, long[] timeRemaining, CountDownTimer[] countDownTimer) {
        //pause button which also act as resume or start button
        pauseTimerBtn.setOnClickListener(view -> {
            String btnName = pauseTimerBtn.getText().toString();
            //for pause functionality
            switch (btnName) {
                case "Pause":
                    countDownTimer[0].cancel();
                    pauseTimerBtn.setText(R.string.resume_btn_text);
                    break;
                case "Resume":
                    //As a resume button creates a new time starting from where the timer was stopped
                    countDownTimer[0] = setNewTimer(countTime, timeRemaining);
                    pauseTimerBtn.setText(R.string.pause_btn_text);
                    break;
                case "Start":
                    //As start button creates a new timer which starts from the initial timer time
                    timeRemaining[0] = timeOutManager.getTimerTime() * CONVERT_MIN_TO_MILLIS;
                    countDownTimer[0] = setNewTimer(countTime, timeRemaining);
                    pauseTimerBtn.setText(R.string.pause_btn_text);
                    resetBtn.setText(R.string.reset_btn_text);
                    break;
            }
        });
        setUpResetButton(timeOutManager, countTime, timeRemaining, countDownTimer[0]);
    }

    private CountDownTimer setNewTimer(TextView countTime, long[] timeRemaining) {
        return new CountDownTimer(timeRemaining[0], COUNTDOWN_VALUE_IN_MS) {
            @Override
            public void onTick(long millisUntilFinished) {
                countTime.setText((millisUntilFinished / CONVERT_MIN_TO_MILLIS + ":" + (millisUntilFinished % CONVERT_MIN_TO_MILLIS) / COUNTDOWN_VALUE_IN_MS).toString());
                timeRemaining[0] = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                countTime.setText("Finished");
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(VIBRATION_TIME_IN_MS, VibrationEffect.DEFAULT_AMPLITUDE));

                } else {
                    vibrator.vibrate(VIBRATION_TIME_IN_MS);
                }

            }
        }.start();
    }

    private CountDownTimer[] setUpCountDownTimer(TextView countTime, long[] timeRemaining) {
        //setting up counter for the first time starting
        return new CountDownTimer[]{setNewTimer(countTime, timeRemaining)};
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.timeout);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    public static Intent makeIntent(Context context, int time) {
        TimeOutManager timeOutManager = TimeOutManager.getInstance();
        Intent intent = new Intent(context, TimeoutActivity.class);
        timeOutManager.setTimerTime(time);
        return intent;
    }

}