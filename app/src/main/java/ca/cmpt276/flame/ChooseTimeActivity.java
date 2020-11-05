package ca.cmpt276.flame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import ca.cmpt276.flame.model.TimeoutManager;

/**
 * ChooseTimeActivity: Allows user to choose quick timer from the list or enter a custom time for timer.
 * Start button and cancel button starts the time and returns to the main activity respectively.
 */
public class ChooseTimeActivity extends AppCompatActivity {
    private final TimeoutManager timeoutManager = TimeoutManager.getInstance();
    private TextView timeValueTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_time);
        timeValueTxt = findViewById(R.id.chooseTime_inputTime);

        setupToolbar();
        createTimerOptions();
        setUpButtons();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.timeout);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void setUpButtons() {
        Button startBtn = findViewById(R.id.chooseTime_btnStart);
        Button cancelBtn = findViewById(R.id.chooseTime_btnCancel);

        startBtn.setOnClickListener(view -> {
            final int MAX_MINUTES = 999;
            int minutes;

            try {
                minutes = Integer.parseInt(timeValueTxt.getText().toString());
            } catch (NumberFormatException e) {
                minutes = 0;
            }

            if(minutes <= 0 || minutes > MAX_MINUTES) {
                String errorMsg = getResources().getQuantityString(R.plurals.choose_time_minutes_error, MAX_MINUTES, MAX_MINUTES);
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                return;
            }

            timeoutManager.setMinutesEntered(minutes);
            startActivity(TimeoutActivity.makeIntent(this));
        });

        cancelBtn.setOnClickListener(view -> finish());
    }

    private void createTimerOptions() {
        RadioGroup group = findViewById(R.id.chooseTime_radioTimeOptions);
        final int[] MINUTE_OPTIONS =  {1, 2, 3, 5, 10};

        for(int numMinutes : MINUTE_OPTIONS) {
            RadioButton timerBtn = new RadioButton(this);
            timerBtn.setText(getResources().getQuantityString(R.plurals.minute, numMinutes, numMinutes));

            String numMinutesStr = String.format(Locale.getDefault(), "%d", numMinutes);
            timerBtn.setOnClickListener(v -> timeValueTxt.setText(numMinutesStr));

            group.addView(timerBtn);

            if (numMinutes == timeoutManager.getMinutesEntered()) {
                timerBtn.setChecked(true);
                timeValueTxt.setText(numMinutesStr);
            }
        }
    }

    protected static Intent makeIntent(Context context) {
        return new Intent(context, ChooseTimeActivity.class);
    }
}