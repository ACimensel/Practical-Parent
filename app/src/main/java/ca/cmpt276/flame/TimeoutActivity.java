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

import ca.cmpt276.flame.model.TimeOutManager;

/**
 * TimeoutActivity: TODO add proper comment once activity created
 */
public class TimeoutActivity extends AppCompatActivity {

    @SuppressLint({"ResourceType", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeout);
        setupToolbar();
        Button resetBtn = (Button) findViewById(R.id.reset_timer_button);
        Button pauseTimerBtn = (Button) findViewById(R.id.pause_timer_button);
        TimeOutManager timeOutManager = TimeOutManager.getInstance();
        TextView countTime = findViewById(R.id.time_left_value);
        countTime.setText("" +timeOutManager.getTimer_time()+ ":00");
        final long[] timeRemaining = new long[1];
        timeRemaining[0] = timeOutManager.getTimer_time()*60000;
        //setting up counter for the first time starting
        final CountDownTimer[] countDownTimer = {new CountDownTimer(timeRemaining[0], 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countTime.setText("" + millisUntilFinished / 60000 + ":" + (millisUntilFinished % 60000) / 1000);
                timeRemaining[0] = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                countTime.setText("Finished");
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(5000);
                }
            }
        }.start()};
        //pause button which also act as resume or start button
        pauseTimerBtn.setOnClickListener(view -> {
            String btnName = pauseTimerBtn.getText().toString();
            //for pause functionality
            if(btnName.equals("Pause")) {
                countDownTimer[0].cancel();
                pauseTimerBtn.setText("Resume");
            }
            //As a resume button creates a new time starting from where the timer was stopped
            else if(btnName.equals("Resume")){
                countDownTimer[0] = new CountDownTimer(timeRemaining[0],1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        countTime.setText("" + millisUntilFinished / 60000 + ":" + (millisUntilFinished % 60000) / 1000);
                        timeRemaining[0] = millisUntilFinished;
                    }

                    @Override
                    public void onFinish() {
                        countTime.setText("Finished");
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE));

                        } else {
                            vibrator.vibrate(5000);
                        }

                    }
                }.start();
                pauseTimerBtn.setText("Pause");
            }
            //As start button creates a new timer which starts from the initial timer time
            else if(btnName.equals("Start")){
                timeRemaining[0] = timeOutManager.getTimer_time() * 60000;
                countDownTimer[0] = new CountDownTimer(timeRemaining[0],1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        countTime.setText("" + millisUntilFinished / 60000 + ":" + (millisUntilFinished % 60000) / 1000);
                        timeRemaining[0] = millisUntilFinished;
                    }

                    @Override
                    public void onFinish() {
                        countTime.setText("Finished");
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            vibrator.vibrate(500);
                        }
                    }
                }.start();
                pauseTimerBtn.setText("Pause");
                resetBtn.setText("Reset");
            }
        });
        //reset button cancel the previous timer and sets remaining timer time to the starting time
        resetBtn.setOnClickListener(view-> {
            countDownTimer[0].cancel();
            if(resetBtn.getText().toString().equals("Reset")) {
                timeRemaining[0] = timeOutManager.getTimer_time() * 60000 ;
                countTime.setText(""+ timeOutManager.getTimer_time() + ":00" );
                pauseTimerBtn.setText("Start");
                resetBtn.setText("Cancel");
            }
            else{
                finish();
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.timeout);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    public static Intent makeIntent(Context context,int time) {
        TimeOutManager timeOutManager = TimeOutManager.getInstance();
        Intent intent = new Intent(context, TimeoutActivity.class);
        timeOutManager.setTimer_time(time);
        return intent;
    }

}