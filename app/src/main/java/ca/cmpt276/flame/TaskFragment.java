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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //create the view to show
        View dialogLayout = LayoutInflater.from(getActivity())
                .inflate(R.layout.task_description_layout, null);

        //Receive data from clicked Task
        Bundle mArgs = getArguments();
        long taskId = 0L;
        if (mArgs != null) {
            taskId = mArgs.getLong(TASK_ID);
        }
        Task clickedTask = taskManager.getTask(taskId);
        String taskName = clickedTask.getName();
        String childName = clickedTask.getNextChild().getName();
        String taskDesc = clickedTask.getDesc();

        //set up dialog layout
        TextView txtTaskName = dialogLayout.findViewById(R.id.task_dialog_txtName);
        txtTaskName.setText(taskName);

        TextView txtChildName = dialogLayout.findViewById(R.id.task_dialog_txtTurnChild);
        txtChildName.setText(getString(R.string.task_turn_child, childName));

        TextView txtTaskDesc = dialogLayout.findViewById(R.id.task_dialog_txtDescription);
        txtTaskDesc.setText(taskDesc);

        Button btnTookTurn = dialogLayout.findViewById(R.id.task_dialog_btnTookTurn);
        btnTookTurn.setOnClickListener(v -> {
            this.dismiss();
        });

        Button btnCancel = dialogLayout.findViewById(R.id.task_dialog_btnCancel);
        btnCancel.setOnClickListener(v -> {
            this.dismiss();
        });

        ImageButton btnEdit = dialogLayout.findViewById(R.id.task_dialog_imageBtnEdit);
        btnEdit.setOnClickListener(v -> {
            Intent intent = TaskEditActivity.makeIntent(getActivity(), clickedTask);
            startActivity(intent);
        });

        //Build the dialog
        return new AlertDialog.Builder(getActivity())
                .setView(dialogLayout)
                .create();
    }
}
