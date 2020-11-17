package ca.cmpt276.flame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


import ca.cmpt276.flame.model.Task;
import ca.cmpt276.flame.model.TaskManager;

/**
 * TaskActivity: allow users to view a list of tasks
 * Users may click 'add' button on the top right to add a new task
 * Users may click an existing task to rename or delete the task
 */
public class TaskActivity extends AppCompatActivity {
    private final TaskManager taskManager = TaskManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        setupToolbar();

    }

    @Override
    protected void onStart() {
        super.onStart();
        setupTaskView();
        setupClickCallBack();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_edit, menu);
        return true;
    }

    //setup action_add button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_task_add) {
            Intent intent = TaskEditActivity.makeIntent(TaskActivity.this, null);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupTaskView() {
        ListView list = findViewById(R.id.task_listView);
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task task : taskManager) {
            tasks.add(task);
        }

        ArrayAdapter<Task> adapter = new ArrayAdapter<>(this, R.layout.list_view_task, tasks);
        list.setAdapter(adapter);
    }

    //setup register click callback for list view
    private void setupClickCallBack() {
        ListView list = findViewById(R.id.task_listView);
        list.setOnItemClickListener((parent, view, position, id) -> {
            ArrayList<Task> tasks = new ArrayList<>();
            for (Task task : taskManager) {
                tasks.add(task);
            }
            //passing Task information to TaskEditActivity
            Task clickedTask = tasks.get(position);
            Intent intent = TaskEditActivity.makeIntent(TaskActivity.this, clickedTask);
            startActivity(intent);
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_Task);
        toolbar.setTitle(R.string.task);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    protected static Intent makeIntent(Context context) {
        return new Intent(context, TaskActivity.class);
    }
}