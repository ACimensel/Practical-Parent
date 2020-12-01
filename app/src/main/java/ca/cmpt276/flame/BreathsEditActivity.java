package ca.cmpt276.flame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;


import ca.cmpt276.flame.model.BreathsManager;

/**
 * Allow user to change number of breaths if desired by using number picker
 */
public class BreathsEditActivity extends AppCompatActivity {
    private final BreathsManager breathsManager = BreathsManager.getInstance();
    private int pickedNum;
    public static final int MAX_VALUE = 10;
    public static final int MIN_VALUE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breaths_edit);
        setupToolbar();
        setupNumPicker();
        setupSaveButton();
        setupCancelButton();
    }

    private void setupNumPicker() {
        NumberPicker numberPicker = findViewById(R.id.breaths_edit_numPicker);
        numberPicker.setMinValue(MIN_VALUE);
        numberPicker.setMaxValue(MAX_VALUE);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setValue(breathsManager.getNumBreaths());

        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            pickedNum = numberPicker.getValue();
            setButtonVisible(true);
            TextView test = findViewById(R.id.breathes_test);
            test.setText(getString(R.string.breaths_edit_choosing, pickedNum));
        });
    }

    private void setupSaveButton() {
        Button btn = findViewById(R.id.breaths_edit_btnSave);
        setButtonVisible(false);
        btn.setOnClickListener(v -> {
            breathsManager.setNumBreaths(pickedNum);
            finish();
        });
    }

    private void setupCancelButton() {
        Button btn = findViewById(R.id.breaths_edit_btnCancel);
        btn.setOnClickListener(v -> finish());
    }

    private void setButtonVisible(boolean visible) {
        Button btn = findViewById(R.id.breaths_edit_btnSave);
        if (visible) {
            btn.setVisibility(View.VISIBLE);
        } else {
            btn.setVisibility(View.INVISIBLE);

        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_breathsEdit);
        toolbar.setTitle(R.string.edit_breaths);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    protected static Intent makeIntent(Context context) {
        return new Intent(context, BreathsEditActivity.class);
    }
}