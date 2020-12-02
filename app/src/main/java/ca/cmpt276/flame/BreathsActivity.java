package ca.cmpt276.flame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import ca.cmpt276.flame.model.BreathsManager;

/**
 * TODO: add description to the activity
 */
public class BreathsActivity extends AppCompatActivity {
    private final BreathsManager breathsManager = BreathsManager.getInstance();
    private boolean isExhale;
    private boolean isInvisible = false;
    public static final int MAX_NUM_BREATH = 10;
    public static final int MIN_NUM_BREATH = 1;
    public static final int INHALE_DURATION_TIME = 3000;
    public static final int EXHALE_DURATION_TIME = 3000;
    public static final int OVER_TIME = 10000;

    //***************************************************************
    //State code start here
    //***************************************************************
    /**
     * TODO: add description to the activity
     */
    public abstract static class State {

        void handleEnter() {
        }

        void handleExit() {
        }

        void handleBreathButton() {
        }
    }

    private final State beginState = new BeginState();
    private final State inhaleState = new InhaleState();
    private final State exhaleState = new ExhaleState();
    private State currentState = new IdleState();

    private void setState(State newState) {
        currentState.handleExit();
        currentState = newState;
        currentState.handleEnter();
    }

    private class BeginState extends State {
        @Override
        void handleEnter() {
            super.handleEnter();
            TextView txt = findViewById(R.id.breaths_txtBtn);
            txt.setText(R.string.begin);

            TextView heading = findViewById(R.id.breaths_txtHeading);
            heading.setText(getString(R.string.breaths_heading_begin, breathsManager.getNumBreaths()));

            TextView numLeft = findViewById(R.id.breaths_txtNumLeft);
            numLeft.setVisibility(View.INVISIBLE);
        }

        @Override
        void handleExit() {
            super.handleExit();
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        void handleBreathButton() {
            super.handleBreathButton();
            ImageButton breath = findViewById(R.id.breathes_imageButton);
            hideEditButton(false);
            breath.setOnTouchListener(null);
            breath.setOnClickListener(v -> {
                hideEditButton(true);
                setState(inhaleState);
                setupBreathsButton();
            });
        }
    }

    private class InhaleState extends State {
        @Override
        void handleEnter() {
            super.handleEnter();
            TextView txt = findViewById(R.id.breaths_txtBtn);
            txt.setText(R.string.in);

            TextView heading = findViewById(R.id.breaths_txtHeading);
            heading.setText(R.string.breath_before_inhale);

            TextView numLeft = findViewById(R.id.breaths_txtNumLeft);
            numLeft.setVisibility(View.VISIBLE);
            numLeft.setText(getString(R.string.breaths_num_left, breathsManager.getNumBreaths()));
        }

        @Override
        void handleExit() {
            super.handleExit();
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        void handleBreathButton() {
            super.handleBreathButton();

            TextView heading = findViewById(R.id.breaths_txtHeading);
            TextView txt = findViewById(R.id.breaths_txtBtn);

            Runnable runBreathe = () -> {
                txt.setText(R.string.out);
                isExhale = true;
            };

            Runnable runOverTime = () -> {
                heading.setText(R.string.breaths_hold_too_long);
            };

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
                            setState(exhaleState);
                            setupBreathsButton();
                        } else {
                            heading.setText(R.string.breath_before_inhale);
                        }
                        break;

                }
                return true;
            });
        }
    }

    private class ExhaleState extends State {
        @Override
        void handleEnter() {
            super.handleEnter();
            TextView heading = findViewById(R.id.breaths_txtHeading);
            TextView txt = findViewById(R.id.breaths_txtBtn);
            Handler handler = new Handler();

            heading.setText(R.string.keep_exhaling);
            ImageButton breath = findViewById(R.id.breathes_imageButton);

            @SuppressLint("ClickableViewAccessibility") Runnable runExhale = () -> {
                int breathsLeft = breathsManager.getNumBreaths() - 1;

                if (breathsLeft == 0) {
                    txt.setText(R.string.good_job);
                    heading.setText(R.string.breaths_finish);
                    TextView numLeft = findViewById(R.id.breaths_txtNumLeft);
                    numLeft.setText(getString(R.string.breaths_num_left, breathsLeft));
                    breath.setEnabled(true);
                    breath.setOnTouchListener(null);
                    breath.setOnClickListener(v -> {
                        setState(beginState);
                        setupBreathsButton();
                    });
                } else {
                    breathsManager.setNumBreaths(breathsLeft);
                    heading.setText(R.string.breath_before_inhale);
                    breath.setEnabled(true);
                    isExhale = false;
                    setState(inhaleState);
                    setupBreathsButton();
                }

            };

            handler.postDelayed(runExhale, EXHALE_DURATION_TIME);
        }

        @Override
        void handleExit() {
            super.handleExit();
            ImageButton breath = findViewById(R.id.breathes_imageButton);
            breath.setEnabled(true);

            isExhale = false;
        }

        @Override
        void handleBreathButton() {
            super.handleBreathButton();
            ImageButton breath = findViewById(R.id.breathes_imageButton);
            breath.setEnabled(false);
        }
    }

    private static class IdleState extends State {
    }
    //***************************************************************
    //State code end here
    //***************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breaths);
        setupToolbar();
        setState(beginState);
        setupBreathsButton();
        //setupBeginPart();

    }

    private void setupBreathsButton() {
        currentState.handleBreathButton();
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
            startEditDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startEditDialog() {
        NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setMinValue(MIN_NUM_BREATH);
        numberPicker.setMaxValue(MAX_NUM_BREATH);
        numberPicker.setValue(breathsManager.getNumBreaths());

        LinearLayout numberLayout = setSettingDialogLayout(numberPicker);
        new AlertDialog.Builder(BreathsActivity.this)
                .setTitle(R.string.edit_breaths)
                .setView(numberLayout)
                .setPositiveButton(R.string.save, (dialogInterface, i) -> {
                    breathsManager.setNumBreaths(numberPicker.getValue());
                    refreshHeadingTxt();
                })
                .setNegativeButton(R.string.cancel, null).show();

    }

    private LinearLayout setSettingDialogLayout(NumberPicker numberPicker) {
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.addView(numberPicker);
        layout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        return layout;
    }

    private void hideEditButton(boolean state) {
        isInvisible = state;
        invalidateOptionsMenu();
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