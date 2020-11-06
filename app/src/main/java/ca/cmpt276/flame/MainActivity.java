package ca.cmpt276.flame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import ca.cmpt276.flame.model.ChildrenManager;
import ca.cmpt276.flame.model.FlipManager;
import ca.cmpt276.flame.model.BGMusicPlayer;

/**
 * Main Activity: displays a menu to the user, allowing them to open
 * the FlipCoinActivity, the TimeoutActivity, the ChildrenActivity and
 * the AboutActivity
 *
 * onTrimMemory() and listenForScreenTurningOff() code inspired from:
 * http://www.developerphil.com/no-you-can-not-override-the-home-button-but-you-dont-have-to/
 */
public class MainActivity extends AppCompatActivity {
    public static boolean isMusicEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupSharedPrefs();
        setupButtons();
        listenForScreenTurningOff();

        if(isMusicEnabled) {
            BGMusicPlayer.playBgMusic(this);
        }
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

        flipCoinBtn.setOnClickListener(view -> startActivity(FlipCoinActivity.makeIntent(this)));
        timeoutBtn.setOnClickListener(view -> startActivity(ChooseTimeActivity.makeIntent(this)));
        childrenBtn.setOnClickListener(view -> startActivity(ChildrenActivity.makeIntent(this)));
        aboutBtn.setOnClickListener(view -> startActivity(AboutActivity.makeIntent(this)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isMusicEnabled) {
            BGMusicPlayer.resumeBgMusic();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // turns off music if back button pressed from main activity
        BGMusicPlayer.pauseBgMusic();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            // turns off music if overview/home buttons pressed
            BGMusicPlayer.pauseBgMusic();
        }
    }

    private void listenForScreenTurningOff() {
        IntentFilter screenStateFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // turns off music if screen is turned off
                BGMusicPlayer.pauseBgMusic();
            }
        }, screenStateFilter);
    }
}