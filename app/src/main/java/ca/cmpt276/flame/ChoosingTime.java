package ca.cmpt276.flame;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import ca.cmpt276.flame.model.TimeOutManager;

/**
 * TimeoutActivity: TODO add proper comment once activity created
 */
public class ChoosingTime extends AppCompatActivity {
    TimeOutManager timeOutManager = TimeOutManager.getInstance();

    private TextView timerTime;
    private static final int TIME_OPTION1 = 1;
    private static final int TIME_OPTION2 = 2;
    private static final int TIME_OPTION3 = 3;
    private static final int TIME_OPTION4 = 5;
    private static final int TIME_OPTION5 = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosing_time);

        createTimerOptions();
        timerTime = findViewById(R.id.choosing_time_value);
        Button startBtn = (Button) findViewById(R.id.start_time_btn);
        Button cancelBtn = (Button) findViewById(R.id.cancel_timer_btn);

        startBtn.setOnClickListener(view -> {
            int time = Integer.parseInt(timerTime.getText().toString());
            startActivity(TimeoutActivity.makeIntent(this, time));
        });
        cancelBtn.setOnClickListener(view -> finish());
    }

    @SuppressLint("SetTextI18n")
    private void createTimerOptions() {
        RadioGroup group = (RadioGroup) findViewById(R.id.choosing_time_options);
        int[] buttonValues =  {TIME_OPTION1, TIME_OPTION2, TIME_OPTION3, TIME_OPTION4, TIME_OPTION5};

        for(int i = 0; i < buttonValues.length; i++) {
            int index = i;
            RadioButton timerBtn = new RadioButton(this);
            timerBtn.setText(buttonValues[index] + "minute");

            timerBtn.setOnClickListener((View.OnClickListener) v -> timerTime.setText("" + buttonValues[index]));

            group.addView(timerBtn);

            if (buttonValues[index] == timeOutManager.getTimerTime()) {
                timerBtn.setChecked(true);
                timerTime.setText("" + buttonValues[index]);
            }
        }
    }

    protected static Intent makeIntent(Context context) {
        return new Intent(context, ChoosingTime.class);
    }
}