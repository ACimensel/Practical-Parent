package ca.cmpt276.flame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import ca.cmpt276.flame.model.BGMusicPlayer;

/**
 * AboutActivity: provides details about the app usage, details about developing team and references to used images and sounds.
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setupToolbar();
        setLinksClickable();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.about);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void setLinksClickable() {
        TextView references = findViewById(R.id.aboutActivity_reference_body);
        references.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onResume() {
        super.onResume();
        BGMusicPlayer.resumeBgMusic();
    }

    protected static Intent makeIntent(Context context) {
        return new Intent(context, AboutActivity.class);
    }
}