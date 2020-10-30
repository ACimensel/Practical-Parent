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

public class ChoosingTime extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosing_time);
        createTimerOptions();
        Button StartBtn = (Button) findViewById(R.id.start_time_btn);

        StartBtn.setOnClickListener(view ->{
            TextView timeValue = findViewById(R.id.choosing_time_value);
            int time = Integer.parseInt(timeValue.getText().toString());
            startActivity(TimeoutActivity.makeIntent(this, time));
        });

    }

    @SuppressLint("SetTextI18n")
    private void createTimerOptions(){
        RadioGroup group = (RadioGroup) findViewById(R.id.choosing_time_options);
        int [] buttonValues =  {1,2,3,5,10};
        for(int i=0; i<5; i++){
            int index = i;
            RadioButton timerBtn = new RadioButton(this);
            timerBtn.setText(buttonValues[index]+ "minute");
            timerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView timer_time = (TextView) findViewById(R.id.choosing_time_value);
                    timer_time.setText(""+ buttonValues[index]);
                }
            });

            group.addView(timerBtn);
        }
    }

    protected static Intent makeIntent(Context context) {
        return new Intent(context, ChoosingTime.class);
    }
}