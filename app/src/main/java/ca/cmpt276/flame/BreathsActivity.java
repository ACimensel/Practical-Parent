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
 * BreathActivity allow user to change number of breaths if desired
 * One big breath button which does most of the interactions
 * show help message to indicate the user to go through the process of breath.
 */
public class BreathsActivity extends AppCompatActivity {
    private final BreathsManager breathsManager = BreathsManager.getInstance();
    private boolean isExhale;
    private boolean editBtnVisible = true;
    private int breathNum;
    public static final int MAX_NUM_BREATH = 10;
    public static final int MIN_NUM_BREATH = 1;
    public static final int INHALE_DURATION_TIME = 3000;
    public static final int EXHALE_DURATION_TIME = 3000;
    public static final int OVER_TIME = 10000;
    private TextView buttonText;
    private TextView headingText;
    private TextView numLeftText;
    private ImageButton breathButton;

    //***************************************************************
    //State code start here
    //***************************************************************

    /**
     * This State class represent three different State of Breath which is begin,inhale and exhale.
     * Each State have their own setting of Breath button.
     * Also do actions when exit and enter a new State.
     */
    private abstract static class State {

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
            breathNum = breathsManager.getNumBreaths();

            buttonText.setText(R.string.begin);
            headingText.setText(getString(R.string.breaths_heading_begin, breathNum));
            numLeftText.setVisibility(View.INVISIBLE);
        }

        @Override
        void handleExit() {
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        void handleBreathButton() {
            setEditBtnVisible(true);
            breathButton.setOnTouchListener(null);
            breathButton.setOnClickListener(v -> {
                setEditBtnVisible(false);
                setState(inhaleState);
                setupBreathsButton();
            });
        }
    }

    private class InhaleState extends State {
        @Override
        void handleEnter() {
            buttonText.setText(R.string.in);
            headingText.setText(R.string.breath_before_inhale);
            numLeftText.setVisibility(View.VISIBLE);
            numLeftText.setText(getString(R.string.breaths_num_left, breathNum));
        }

        @Override
        void handleExit() {
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        void handleBreathButton() {
            Runnable runBreathe = () -> {
                buttonText.setText(R.string.out);
                isExhale = true;
            };

            Runnable runOverTime = () -> {
                headingText.setText(R.string.breaths_hold_too_long);
            };

            Handler handler = new Handler();
            breathButton.setOnTouchListener((arg0, arg1) -> {
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        headingText.setText(R.string.keep_inhaling);
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
                            headingText.setText(R.string.breath_before_inhale);
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
            headingText.setText(R.string.keep_exhaling);

            Handler handler = new Handler();

            @SuppressLint("ClickableViewAccessibility") Runnable runExhale = () -> {
                int breathsLeft = breathNum - 1;

                if (breathsLeft == 0) {
                    buttonText.setText(R.string.good_job);
                    headingText.setText(R.string.breaths_finish);
                    numLeftText.setText(getString(R.string.breaths_num_left, breathsLeft));
                    breathButton.setEnabled(true);
                    breathButton.setOnTouchListener(null);
                    breathButton.setOnClickListener(v -> {
                        setState(beginState);
                        setupBreathsButton();
                    });
                } else {
                    breathNum = breathsLeft;
                    headingText.setText(R.string.breath_before_inhale);
                    breathButton.setEnabled(true);
                    isExhale = false;
                    setState(inhaleState);
                    setupBreathsButton();
                }

            };

            handler.postDelayed(runExhale, EXHALE_DURATION_TIME);
        }

        @Override
        void handleExit() {
            breathButton.setEnabled(true);
            isExhale = false;
        }

        @Override
        void handleBreathButton() {
            breathButton.setEnabled(false);
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
        setupViews();
        setState(beginState);
        setupBreathsButton();
    }

    private void setupViews() {
        buttonText = findViewById(R.id.breaths_txtBtn);
        headingText = findViewById(R.id.breaths_txtHeading);
        numLeftText = findViewById(R.id.breaths_txtNumLeft);
        breathButton = findViewById(R.id.breathes_imageButton);
    }

    private void setupBreathsButton() {
        currentState.handleBreathButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        breathNum = breathsManager.getNumBreaths();
        refreshHeadingTxt();
    }

    private void refreshHeadingTxt() {
        headingText.setText(getString(R.string.breaths_heading_begin, breathsManager.getNumBreaths()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_breaths_edit, menu);

        if (!editBtnVisible) {
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
                    breathNum = breathsManager.getNumBreaths();
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

    private void setEditBtnVisible(boolean state) {
        editBtnVisible = state;
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