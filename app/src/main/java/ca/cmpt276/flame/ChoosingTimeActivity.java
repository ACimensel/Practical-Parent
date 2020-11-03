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

import java.text.MessageFormat;

import ca.cmpt276.flame.model.TimeOutManager;

/**
 * TimeoutActivity: Allows user to choose quick timer from the list or enter a custom time for timer.
 * Start button and cancel button starts the time and returns to the main activity respectively.
 */
public class ChoosingTimeActivity extends AppCompatActivity {
    TimeOutManager timeOutManager = TimeOutManager.getInstance();
    TextView timeValueTxt;

    private static final int TIME_OPTION1 = 1;
    private static final int TIME_OPTION2 = 2;
    private static final int TIME_OPTION3 = 3;
    private static final int TIME_OPTION4 = 5;
    private static final int TIME_OPTION5 = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosing_time);
        timeValueTxt = findViewById(R.id.choosing_time_value);

        createTimerOptions();
        setUpButtons();
    }

    private void setUpButtons() {
        Button startBtn = (Button) findViewById(R.id.start_time_btn);
        Button cancelBtn = (Button) findViewById(R.id.cancel_timer_btn);

        startBtn.setOnClickListener(view -> {
            int time = Integer.parseInt(timeValueTxt.getText().toString());
            startActivity(TimeoutActivity.makeIntent(this, time));
        });
        cancelBtn.setOnClickListener(view -> finish());
    }

    private void createTimerOptions() {
        RadioGroup group = (RadioGroup) findViewById(R.id.choosing_time_options);
        int[] buttonValues =  {TIME_OPTION1, TIME_OPTION2, TIME_OPTION3, TIME_OPTION4, TIME_OPTION5};

        for(int i = 0; i < buttonValues.length; i++) {
            int index = i;
            RadioButton timerBtn = new RadioButton(this);
            timerBtn.setText(MessageFormat.format("{0}minute", buttonValues[index]));

            timerBtn.setOnClickListener((View.OnClickListener) v -> timeValueTxt.setText(MessageFormat.format("{0}", buttonValues[index])));

            group.addView(timerBtn);

            if (buttonValues[index] == timeOutManager.getTimerTime()) {
                timerBtn.setChecked(true);
                timeValueTxt.setText(MessageFormat.format("{0}", buttonValues[index]));
            }
        }
    }

    protected static Intent makeIntent(Context context) {
        return new Intent(context, ChoosingTimeActivity.class);
    }
}