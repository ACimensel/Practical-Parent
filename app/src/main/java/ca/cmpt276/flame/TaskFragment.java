package ca.cmpt276.flame;


import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import ca.cmpt276.flame.model.Task;
import ca.cmpt276.flame.model.TaskManager;

/**
 * TaskFragment: set up a custom DialogFragment layout to show detail information
 * of clicked task including the Name of task, the Description, who is turn child.
 * Allow user to confirm the turn child.
 * Right top image button allow user to edit task in TaskEditActivity
 */

public class TaskFragment extends AppCompatDialogFragment {
    private final TaskManager taskManager = TaskManager.getInstance();
    private static final String TASK_ID = "TaskId";
    private String taskName;
    private String childName;
    private String taskDesc;
    private Task clickedTask;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //create the view to show
        View dialogLayout = LayoutInflater.from(getActivity())
                .inflate(R.layout.task_description_layout, null);

        //Receive data from clicked Task
        extraDataFromActivity();

        //set up dialog layout
        setupLayout(dialogLayout);

        //Build the dialog
        return new AlertDialog.Builder(getActivity())
                .setView(dialogLayout)
                .create();
    }

    private void setupLayout(View dialogLayout) {
        //set up textView
        TextView txtTaskName = dialogLayout.findViewById(R.id.task_dialog_txtName);
        txtTaskName.setText(taskName);

        TextView txtChildName = dialogLayout.findViewById(R.id.task_dialog_txtTurnChild);
        txtChildName.setText(getString(R.string.task_turn_child, childName));

        TextView txtTaskDesc = dialogLayout.findViewById(R.id.task_dialog_txtDescription);
        txtTaskDesc.setText(taskDesc);

        //set up button
        Button btnTookTurn = dialogLayout.findViewById(R.id.task_dialog_btnTookTurn);
        btnTookTurn.setOnClickListener(v -> {
            taskManager.takeTurn(clickedTask);
            Intent intent = new Intent(this.getContext(), TaskActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        Button btnCancel = dialogLayout.findViewById(R.id.task_dialog_btnCancel);
        btnCancel.setOnClickListener(v -> {
            this.dismiss();
        });

        //set up image button
        ImageButton btnEdit = dialogLayout.findViewById(R.id.task_dialog_imageBtnEdit);
        btnEdit.setOnClickListener(v -> {
            Intent intent = TaskEditActivity.makeIntent(getActivity(), clickedTask);
            startActivity(intent);
        });
    }

    private void extraDataFromActivity() {
        Bundle mArgs = getArguments();
        long taskId = 0L;
        if (mArgs != null) {
            taskId = mArgs.getLong(TASK_ID);
        }
        clickedTask = taskManager.getTask(taskId);
        taskName = clickedTask.getName();
        childName = clickedTask.getNextChild().getName();
        taskDesc = clickedTask.getDesc();
    }

}
