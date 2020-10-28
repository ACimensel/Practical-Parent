package ca.cmpt276.flame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import ca.cmpt276.flame.model.ChildrenManager;
import ca.cmpt276.flame.model.FlipManager;

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
        setupSharedPrefs();
        setupButtons();
    }

    private void setupSharedPrefs() {
        // let model classes access Shared Preferences
        SharedPreferences sharedPrefs = getPreferences(MODE_PRIVATE);
        ChildrenManager.init(sharedPrefs);
        FlipManager.init(sharedPrefs);
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