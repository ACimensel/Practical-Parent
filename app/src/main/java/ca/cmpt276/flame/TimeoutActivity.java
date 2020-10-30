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
    //private static final String EXTRA_TIME = "ca.cmpt276.flame - time";

    @SuppressLint("ResourceType")
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
        CountDownTimer countDownTimer = new CountDownTimer(timeOutManager.getTimer_time()*60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countTime.setText(""+ millisUntilFinished/60000+":"+(millisUntilFinished%60000)/1000);
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

        pauseTimerBtn.setOnClickListener(view -> {

        });
        resetBtn.setOnClickListener(view-> {
            countDownTimer.cancel();
            countDownTimer.start();
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