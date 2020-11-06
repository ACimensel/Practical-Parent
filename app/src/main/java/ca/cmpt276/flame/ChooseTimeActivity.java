package ca.cmpt276.flame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
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

        createNotificationChannel();
        setUpTimeValueTxt();
        setupToolbar();
        createTimerOptions();
        setUpButtons();
    }

    private void setUpTimeValueTxt() {
        final int MAX_INPUT_LENGTH = 3;
        timeValueTxt = findViewById(R.id.chooseTime_inputTime);
        timeValueTxt.setHint(R.string.chooseTimeActivity_timer_hint);
        timeValueTxt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_INPUT_LENGTH)});
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.timeout);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void setUpButtons() {
        Button startBtn = findViewById(R.id.chooseTime_btnStart);
        startBtn.setOnClickListener(view -> {
            int minutes;

            try {
                minutes = Integer.parseInt(timeValueTxt.getText().toString());
            } catch (NumberFormatException e) {
                minutes = 0;
            }

            if(minutes <= 0) {
                Toast.makeText(this, R.string.choose_time_error, Toast.LENGTH_SHORT).show();
                return;
            }

            timeoutManager.setMinutesEntered(getApplicationContext(), minutes);
            timeoutManager.start(getApplicationContext());
            startActivity(TimeoutActivity.makeIntent(this));
        });
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

    // inspired by code from: https://developer.android.com/training/notify-user/channels
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            String name = getString(R.string.timer_notification_channel_name);
            String description = getString(R.string.timer_notification_channel_desc);

            NotificationChannel channel = new NotificationChannel(TimerAlarmReceiver.NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    protected static Intent makeIntent(Context context) {
        return new Intent(context, ChooseTimeActivity.class);
    }
}