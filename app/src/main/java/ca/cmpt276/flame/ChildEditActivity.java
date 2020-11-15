package ca.cmpt276.flame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ca.cmpt276.flame.model.BGMusicPlayer;
import ca.cmpt276.flame.model.Child;
import ca.cmpt276.flame.model.ChildrenManager;

/**
 * ChildEditActivity:
 * Add a new child
 * Rename an existing child
 * Delete a child
 */
public class ChildEditActivity extends AppCompatActivity {
    private static final String EXTRA_CHILD_ID = "EXTRA_CHILD_ID";
    private Child clickedChild;
    private String newName;
    private final ChildrenManager childrenManager = ChildrenManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_edit);
        getDataFromIntent();
        setupToolbar();
        fillChildName();
        setupSaveButton();

        if (clickedChild == null) {
            hideDeleteButton();
        } else {
            setupDeleteButton();
        }
    }

    private void getDataFromIntent() {
        long childId = getIntent().getLongExtra(EXTRA_CHILD_ID, Child.NONE);
        clickedChild = childrenManager.getChild(childId);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_edit);

        if(clickedChild == null) {
            toolbar.setTitle(R.string.add_child);
        } else {
            toolbar.setTitle(R.string.edit_child);
        }

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void fillChildName() {
        if(clickedChild != null) {
            EditText inputName = findViewById(R.id.childEdit_editTxtChildName);
            inputName.setText(clickedChild.getName());
        }
    }

    private void setupSaveButton() {
        Button btn = findViewById(R.id.childEdit_btnSave);

        btn.setOnClickListener(v -> {
            EditText inputName = findViewById(R.id.childEdit_editTxtChildName);
            newName = inputName.getText().toString();

            if(newName.isEmpty()) {
                Toast.makeText(this, getString(R.string.child_name_empty_error), Toast.LENGTH_SHORT).show();
                return;
            }

            if (clickedChild != null) {
                childrenManager.renameChild(clickedChild, newName);
            } else {
                childrenManager.addChild(newName);
            }

            finish();
        });
    }

    private void hideDeleteButton() {
        Button btn = findViewById(R.id.childEdit_btnDelete);
        btn.setVisibility(View.GONE);
    }

    private void setupDeleteButton() {
        Button btn = findViewById(R.id.childEdit_btnDelete);
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(v -> {
            new AlertDialog.Builder(ChildEditActivity.this)
                    .setTitle(R.string.confirm)
                    .setMessage(R.string.childEditActivity_confirmDeleteMsg)
                    .setPositiveButton(R.string.delete, ((dialogInterface, i) -> {
                        childrenManager.removeChild(clickedChild);
                        finish();
                    }))
                    .setNegativeButton(R.string.cancel, null).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BGMusicPlayer.resumeBgMusic();
    }

    protected static Intent makeIntent(Context context, Child child) {
        long childId = Child.NONE;
        if(child != null) {
            childId = child.getId();
        }

        Intent intent = new Intent(context, ChildEditActivity.class);
        intent.putExtra(EXTRA_CHILD_ID, childId);
        return intent;
    }
}