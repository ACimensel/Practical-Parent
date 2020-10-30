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

import java.util.UUID;

import ca.cmpt276.flame.model.Child;
import ca.cmpt276.flame.model.ChildrenManager;

public class ChildEditActivity extends AppCompatActivity {
    private String new_name;
    private String old_name;
    private String uuid_string;
    private UUID uuid;
    public static final String UUID = "uuid";
    public static final String OLD_NAME = "old_name";
    public static final String NEW_NAME = "new_name";
    ChildrenManager manager = ChildrenManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_edit);
        setupToolbar();
        extractDataFromIntent();
        if(old_name == "only_for_edit"){
            setupSaveForAddButton();
        }

        setupSaveButton();
    }

    private void setupSaveForAddButton() {
        Button btn = (Button) findViewById(R.id.button_save);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input_name = (EditText) findViewById(R.id.editText_Name);
                new_name = input_name.getText().toString();
                Intent intent = new Intent();
                intent.putExtra(NEW_NAME, new_name);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    private void setupSaveButton() {
        Button btn = (Button) findViewById(R.id.button_save);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input_name = (EditText) findViewById(R.id.editText_Name);
                new_name = input_name.getText().toString();
                Intent intent = new Intent();
                intent.putExtra(NEW_NAME, new_name);
                intent.putExtra(UUID, uuid_string);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_edit);
        toolbar.setTitle(R.string.edit);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    protected static Intent makeIntent(Context context, Child child) {
        Intent intent = new Intent(context, ChildEditActivity.class);
        intent.putExtra(UUID, child.getUuid().toString());
        intent.putExtra(OLD_NAME, child.getName());
        return intent;
    }

    // get extra data
    private void extractDataFromIntent() {
        Intent intent = getIntent();
        uuid_string = intent.getStringExtra(UUID);
        old_name = intent.getStringExtra(OLD_NAME);
        uuid = uuid.fromString(uuid_string);
    }
}