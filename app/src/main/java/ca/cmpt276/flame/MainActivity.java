package ca.cmpt276.flame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

/**
 * Main Activity: displays a menu to the user, allowing them to open
 * the FlipCoinActivity, the TimeoutActivity, the ChildrenActivity and
 * the AboutActivity
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupButtons();
    }

    private void setupButtons() {
        Button flipCoinBtn = findViewById(R.id.main_btnFlipCoin);
        Button timeoutBtn = findViewById(R.id.main_btnTimeout);
        Button childrenBtn = findViewById(R.id.main_btnChildren);
        Button aboutBtn = findViewById(R.id.main_btnAbout);

        flipCoinBtn.setOnClickListener(view -> {
            startActivity(FlipCoinActivity.makeIntent(this));
        });

        timeoutBtn.setOnClickListener(view -> {
            startActivity(TimeoutActivity.makeIntent(this));
        });

        childrenBtn.setOnClickListener(view -> {
            startActivity(ChildrenActivity.makeIntent(this));
        });

        aboutBtn.setOnClickListener(view -> {
            startActivity(AboutActivity.makeIntent(this));
        });
    }
}