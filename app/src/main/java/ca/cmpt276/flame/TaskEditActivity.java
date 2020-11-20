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

import ca.cmpt276.flame.model.Task;
import ca.cmpt276.flame.model.TaskManager;

/**
 * TaskEditActivity:
 * Add a new task
 * Rename an existing task
 * Delete a task
 */
public class TaskEditActivity extends AppCompatActivity {
    private static final String EXTRA_TASK_ID = "EXTRA_TASK_ID";
    private Task clickedTask;
    private String newName;
    private String newDesc;
    private final TaskManager taskManager = TaskManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        getDataFromIntent();
        setupToolbar();
        fillTaskName();
        setupSaveButton();

        if (clickedTask == null) {
            hideDeleteButton();
        } else {
            setupDeleteButton();
        }
    }

    private void getDataFromIntent() {
        long taskId = getIntent().getLongExtra(EXTRA_TASK_ID, Task.NONE);
        clickedTask = taskManager.getTask(taskId);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_taskEdit);

        if (clickedTask == null) {
            toolbar.setTitle(R.string.add_task);
        } else {
            toolbar.setTitle(R.string.edit_task);
        }

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void fillTaskName() {
        if (clickedTask != null) {
            EditText inputName = findViewById(R.id.taskEdit_editTxtTaskName);
            EditText inputDesc = findViewById(R.id.taskEdit_editTxtTaskDesc);
            inputName.setText(clickedTask.getName());
            inputDesc.setText(clickedTask.getDesc());
        }
    }

    private void setupSaveButton() {
        Button btn = findViewById(R.id.taskEdit_btnSave);

        btn.setOnClickListener(v -> {
            EditText inputName = findViewById(R.id.taskEdit_editTxtTaskName);
            EditText inputDesc = findViewById(R.id.taskEdit_editTxtTaskDesc);
            newName = inputName.getText().toString();
            newDesc = inputDesc.getText().toString();

            if (newName.isEmpty()) {
                Toast.makeText(this, getString(R.string.task_name_empty_error), Toast.LENGTH_SHORT).show();
                return;
            }

            if (newDesc.isEmpty()) {
                Toast.makeText(this, getString(R.string.task_description_empty_error), Toast.LENGTH_SHORT).show();
                return;
            }

            if (clickedTask != null) {
                taskManager.modifyTask(clickedTask, newName, newDesc);
            } else {
                taskManager.addTask(newName, newDesc);
            }

            //set up go back to TaskActivity
            Intent intent = new Intent(this, TaskActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    private void hideDeleteButton() {
        Button btn = findViewById(R.id.taskEdit_btnDelete);
        btn.setVisibility(View.GONE);
    }

    private void setupDeleteButton() {
        Button btn = findViewById(R.id.taskEdit_btnDelete);
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(v -> {
            new AlertDialog.Builder(TaskEditActivity.this)
                    .setTitle(R.string.confirm)
                    .setMessage(R.string.taskEditActivity_confirmDeleteMsg)
                    .setPositiveButton(R.string.delete, ((dialogInterface, i) -> {
                        taskManager.removeTask(clickedTask);
                        //set up go back to TaskActivity
                        Intent intent = new Intent(this, TaskActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }))
                    .setNegativeButton(R.string.cancel, null).show();
        });
    }

    protected static Intent makeIntent(Context context, Task task) {
        long taskId = Task.NONE;
        if (task != null) {
            taskId = task.getId();
        }

        Intent intent = new Intent(context, TaskEditActivity.class);
        intent.putExtra(EXTRA_TASK_ID, taskId);
        return intent;
    }
}