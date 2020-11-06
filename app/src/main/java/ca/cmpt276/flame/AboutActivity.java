package ca.cmpt276.flame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import ca.cmpt276.flame.model.BGMusicPlayer;

/**
 * AboutActivity: TODO add proper comment once activity created
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.about);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MainActivity.isMusicEnabled) {
            BGMusicPlayer.resumeBgMusic();
        }
    }

    protected static Intent makeIntent(Context context) {
        return new Intent(context, AboutActivity.class);
    }
}