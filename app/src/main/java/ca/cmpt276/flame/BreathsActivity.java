package ca.cmpt276.flame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
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
    public static final int INHALE_HOLD_TIME = 3000;
    public static final int EXHALE_WAIT_TIME = 3000;
    public static final int MAX_TIME = 10000;
    private TextView buttonText;
    private TextView headingText;
    private TextView numLeftText;
    private ImageButton breathButton;
    private Animation growAnim;

    private final State beginState = new BeginState();
    private final State inhaleState = new InhaleState();
    private final State exhaleState = new ExhaleState();
    private final State finishState = new FinishState();
    private State currentState = new IdleState();

    //***************************************************************
    //State code start here
    //***************************************************************

    /**
     * This State class represent three different State of Breath which is begin,inhale and exhale.
     * Each State have their own setting of Breath button.
     * Also do actions when exit and enter a new State.
     */
    private abstract static class State {
        void handleEnter() { }
        void handleExit() { }
        boolean handleTouch(View view, MotionEvent motionEvent) {
            return false;
        }
        void handleClick(View view) { }
    }

    private static class IdleState extends State { }

    private class BeginState extends State {
        @Override
        void handleEnter() {
            breathNum = breathsManager.getNumBreaths();
            setEditBtnVisible(true);

            headingText.setText(getResources().getQuantityString(R.plurals.breaths_heading_begin, breathNum, breathNum));
            buttonText.setText(R.string.begin);
            numLeftText.setVisibility(View.INVISIBLE);

            breathButton.setImageResource(R.drawable.breaths_btn_begin);
        }

        @Override
        void handleClick(View view) {
            setState(inhaleState);
        }

        @Override
        void handleExit() {
            setEditBtnVisible(false);
        }
    }

    private class InhaleState extends State {
        Handler handler = new Handler();
        boolean heldLongEnough;

        Runnable holdLongEnough = () -> {
            heldLongEnough = true;
            buttonText.setText(R.string.out);
        };

        Runnable holdTooLong = () -> {
            headingText.setText(R.string.breaths_hold_too_long);
        };

        @Override
        void handleEnter() {
            heldLongEnough = false;

            headingText.setText(R.string.breath_before_inhale);
            buttonText.setText(R.string.in);
            numLeftText.setVisibility(View.VISIBLE);
            numLeftText.setText(getString(R.string.breaths_num_left, breathNum));

            breathButton.setImageResource(R.drawable.breaths_btn_in);
        }

        @Override
        boolean handleTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startAnimation(false);
                    headingText.setText(R.string.keep_inhaling);
                    handler.postDelayed(holdLongEnough, INHALE_HOLD_TIME);
                    handler.postDelayed(holdTooLong, MAX_TIME);
                    break;

                case MotionEvent.ACTION_UP:
                    stopAnimation();
                    handler.removeCallbacks(holdLongEnough);
                    handler.removeCallbacks(holdTooLong);

                    if(heldLongEnough) {
                        setState(exhaleState);
                    } else {
                        headingText.setText(R.string.breath_before_inhale);
                    }
                    break;
            }
            return true;
        }
    }

    private class ExhaleState extends State {
        Handler handler = new Handler();

        Runnable waitLongEnough = () -> {
            breathNum--;
            numLeftText.setText(getString(R.string.breaths_num_left, breathNum));

            if(breathNum > 0) {
                buttonText.setText(R.string.in);
            } else {
                buttonText.setText(R.string.good_job);
            }

            breathButton.setEnabled(true);
        };

        Runnable waitTooLong = () -> {
            handleDoneExhale();
        };

        @Override
        void handleEnter() {
            headingText.setText(R.string.keep_exhaling);
            buttonText.setText(R.string.out);
            breathButton.setImageResource(R.drawable.breaths_btn_out);
            breathButton.setEnabled(false);
            startAnimation(true);

            handler.postDelayed(waitLongEnough, EXHALE_WAIT_TIME);
            handler.postDelayed(waitTooLong, MAX_TIME);
        }

        @Override
        void handleClick(View view) {
            // handleClick is only possible after the EXHALE_WAIT_TIME
            handler.removeCallbacks(waitTooLong);
            handleDoneExhale();
        }

        void handleDoneExhale() {
            stopAnimation();

            if(breathNum > 0) {
                setState(inhaleState);
            } else {
                setState(finishState);
            }
        }
    }

    private class FinishState extends State {
        @Override
        void handleEnter() {
            headingText.setText(R.string.all_done);
            breathButton.setImageResource(R.drawable.breaths_btn_begin);
        }

        @Override
        void handleClick(View view) {
            breathNum = breathsManager.getNumBreaths();
            setState(beginState);
        }
    }

    private void setState(State newState) {
        currentState.handleExit();
        currentState = newState;
        currentState.handleEnter();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentState = beginState;
        currentState.handleEnter();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupViews() {
        buttonText = findViewById(R.id.breaths_txtBtn);
        headingText = findViewById(R.id.breaths_txtHeading);
        numLeftText = findViewById(R.id.breaths_txtNumLeft);
        breathButton = findViewById(R.id.breathes_imageButton);

        breathButton.setOnTouchListener((view,  motionEvent) -> {
            return currentState.handleTouch(view, motionEvent);
        });

        breathButton.setOnClickListener((view) -> {
            currentState.handleClick(view);
        });
    }

    private float getAnimationScale() {
        ConstraintLayout rootLayout = findViewById(R.id.breaths_layoutContent);

        int parentMaxDim = Math.max(rootLayout.getWidth(), rootLayout.getHeight());
        int btnDim = breathButton.getWidth();

        return (float) parentMaxDim / (float) btnDim;
    }

    private void startAnimation(boolean isShrink) {
        final float HALF_LAYOUT = 0.5f;

        float bigScale = getAnimationScale();
        float fromScale = 1f;
        float toScale = bigScale;

        if(isShrink) {
            fromScale = bigScale;
            toScale = 1f;
        }

        growAnim = new ScaleAnimation(fromScale, toScale, fromScale, toScale, Animation.RELATIVE_TO_SELF, HALF_LAYOUT, Animation.RELATIVE_TO_SELF, HALF_LAYOUT);
        growAnim.setDuration(MAX_TIME);
        breathButton.startAnimation(growAnim);
    }

    private void stopAnimation() {
        if(growAnim != null) {
            growAnim.cancel();
            breathButton.clearAnimation();
        }
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
                .setTitle(R.string.number_of_breaths)
                .setView(numberLayout)
                .setPositiveButton(R.string.save, (dialogInterface, i) -> {
                    breathsManager.setNumBreaths(numberPicker.getValue());
                    currentState.handleEnter();
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