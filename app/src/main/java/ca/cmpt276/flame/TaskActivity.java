package ca.cmpt276.flame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
    private final ArrayList<Task> taskList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        setupToolbar();
        populateList();
        setupListView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        populateList();
        setupListView();
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


    private void populateList() {
        taskList.clear();
        for (Task task : taskManager) {
            taskList.add(0, task);
        }
    }

    private void setupListView() {
        ArrayAdapter<Task> adapter = new TaskListAdapter();
        ListView list = findViewById(R.id.task_listView);
        list.setAdapter(adapter);
    }

    private class TaskListAdapter extends ArrayAdapter<Task> {
        TaskListAdapter() {
            super(TaskActivity.this, R.layout.list_view_task, taskList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.list_view_task, parent, false);
            }

            //find the task to work with
            Task clickedTask = taskList.get(position);

            //set up the layout
            TextView txtTaskName = itemView.findViewById(R.id.task_txtTaskName);
            TextView txtChildName = itemView.findViewById(R.id.task_txtChildName);
            ImageView imagePortrait = itemView.findViewById(R.id.task_imagePortrait);

            txtTaskName.setText(clickedTask.getName());
            txtChildName.setText(clickedTask.getNextChild().getName());
            imagePortrait.setImageResource(R.drawable.default_child);

            //passing task data to DialogFragment
            Bundle args = new Bundle();
            args.putLong("TaskId", clickedTask.getId());

            //set onClick to show Dialog
            itemView.setOnClickListener(v -> {
                FragmentManager manager = getSupportFragmentManager();
                TaskFragment dialog = new TaskFragment();
                dialog.setArguments(args);
                dialog.show(manager, "TaskDialog");
            });

            return itemView;
        }
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