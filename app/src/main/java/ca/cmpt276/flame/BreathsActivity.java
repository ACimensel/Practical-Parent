package ca.cmpt276.flame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.TextView;

import ca.cmpt276.flame.model.BreathsManager;

/**
 * TODO: add description to the activity
 */
public class BreathsActivity extends AppCompatActivity {
    private final BreathsManager breathsManager = BreathsManager.getInstance();
    private boolean isExhale;
    private boolean isInvisible = false;
    public static final int DEFAULT_NUM = 3;
    public static final int INHALE_DURATION_TIME = 3000;
    public static final int EXHALE_DURATION_TIME = 3000;
    public static final int OVER_TIME = 10000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breaths);
        setupToolbar();
        setupBeginPart();

        breathsManager.setNumBreaths(DEFAULT_NUM);
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshHeadingTxt();
    }

    private void refreshHeadingTxt() {
        TextView heading = findViewById(R.id.breaths_txtHeading);
        heading.setText(getString(R.string.breaths_heading_begin, breathsManager.getNumBreaths()));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupBeginPart() {
        TextView txt = findViewById(R.id.breaths_txtBtn);
        txt.setText(R.string.begin);

        TextView heading = findViewById(R.id.breaths_txtHeading);
        heading.setText(getString(R.string.breaths_heading_begin, breathsManager.getNumBreaths()));

        ImageButton breath = findViewById(R.id.breathes_imageButton);
        hideEditButton(false);
        breath.setOnTouchListener(null);
        breath.setOnClickListener(v -> {
            hideEditButton(true);
            heading.setText(R.string.breath_before_inhale);
            setupInhalePart();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_breaths_edit, menu);

        if (isInvisible) {
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setVisible(false);
            }
        }
        return true;
    }

    //setup action_add button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_breaths_add) {
            Intent intent = BreathsEditActivity.makeIntent(BreathsActivity.this);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideEditButton(boolean state) {
        isInvisible = state;
        invalidateOptionsMenu();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupInhalePart() {
        TextView heading = findViewById(R.id.breaths_txtHeading);

        TextView txt = findViewById(R.id.breaths_txtBtn);
        txt.setText(R.string.in);

        Runnable runBreathe = () -> {
            txt.setText(R.string.out);
            isExhale = true;
        };

        Runnable runOverTime = () -> heading.setText(R.string.breaths_hold_too_long);

        ImageButton breath = findViewById(R.id.breathes_imageButton);
        Handler handler = new Handler();
        breath.setOnTouchListener((arg0, arg1) -> {
            switch (arg1.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    heading.setText(R.string.keep_inhaling);
                    handler.postDelayed(runBreathe, INHALE_DURATION_TIME);
                    handler.postDelayed(runOverTime, OVER_TIME);
                    break;

                case MotionEvent.ACTION_UP:
                    handler.removeCallbacks(runBreathe);
                    handler.removeCallbacks(runOverTime);
                    if (isExhale) {
                        runExhalePart(handler);
                        breath.setEnabled(false);
                        heading.setText(R.string.keep_exhaling);
                    } else {
                        breath.setOnTouchListener(null);
                        setupBeginPart();
                    }
                    break;

            }
            return true;
        });
    }

    private void runExhalePart(Handler handler) {
        TextView txt = findViewById(R.id.breaths_txtBtn);
        ImageButton breath = findViewById(R.id.breathes_imageButton);
        TextView heading = findViewById(R.id.breaths_txtHeading);

        @SuppressLint("ClickableViewAccessibility") Runnable runExhale = () -> {
            int breathsLeft = breathsManager.getNumBreaths() - 1;

            if (breathsLeft == 0) {
                txt.setText(R.string.good_job);
                heading.setText(R.string.breaths_finish);
                breath.setEnabled(true);
                breath.setOnTouchListener(null);
                breath.setOnClickListener(v -> setupBeginPart());
            } else {
                breathsManager.setNumBreaths(breathsLeft);
                heading.setText(R.string.breath_before_inhale);
                breath.setEnabled(true);
                isExhale = false;
                setupInhalePart();
            }

        };

        handler.postDelayed(runExhale, EXHALE_DURATION_TIME);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_Breaths);
        toolbar.setTitle(R.string.take_a_breath);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    protected static Intent makeIntent(Context context) {
        return new Intent(context, BreathsActivity.class);
    }
}