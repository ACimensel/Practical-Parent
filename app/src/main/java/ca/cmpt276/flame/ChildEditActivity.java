package ca.cmpt276.flame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import ca.cmpt276.flame.model.Child;
import ca.cmpt276.flame.model.ChildrenManager;

/**
 * ChildrenActivity:
 * Add a new child
 * Rename an existing child
 * Delete a child
 */
public class ChildEditActivity extends AppCompatActivity {
    private static Child clickedChild;
    private String newName;
    ChildrenManager childManager = ChildrenManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_edit);
        setupToolbar();

        if (clickedChild == null) {
            setupSaveButton();
            hideDeleteButton();
        } else {
            setupSaveButton();
            setupDeleteButton();
        }
    }

    protected static Intent makeIntent(Context context, Child child) {
        clickedChild = child;
        Intent intent = new Intent(context, ChildEditActivity.class);
        return intent;
    }

    private void hideDeleteButton() {
        Button btn = findViewById(R.id.button_delete);
        btn.setVisibility(View.GONE);
    }

    private void setupDeleteButton() {
        Button btn = findViewById(R.id.button_delete);
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(v -> {
            childManager.removeChild(clickedChild.getUuid());
            setResult(Activity.RESULT_OK);
            finish();
        });
    }


    private void setupSaveButton() {
        Button btn = findViewById(R.id.button_save);
        btn.setOnClickListener(v -> {
            EditText inputName = findViewById(R.id.editText_Name);
            newName = inputName.getText().toString();
            Intent intent = new Intent();
            //check if the user leaves the name field empty
            System.out.println(newName);
            if (newName.equals("")) {
                setResult(Activity.RESULT_CANCELED);
            } else if (clickedChild != null) {
                //passing uuid and new name for renaming
                childManager.renameChild(clickedChild.getUuid(), newName);
            } else {
                //passing new name for add child
                childManager.addChild(new Child(newName));
            }
            setResult(Activity.RESULT_OK);
            finish();
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_edit);
        toolbar.setTitle(R.string.edit);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

}