package ca.cmpt276.flame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.List;

import ca.cmpt276.flame.model.Child;
import ca.cmpt276.flame.model.FlipManager;

/**
 *  A fragment class to pop up dialog box to enable user to choose child to go next
 */
public class ChooseFlipperFragment extends AppCompatDialogFragment {
    ListView listView;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Create the view to show
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_choose_flipper, null);

        // Build the alert dialog
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();

        //Make Background Transparent
        dialog.getWindow().setBackgroundDrawableResource(R.color.colorTransparentOffWhite);


        listView = v.findViewById(R.id.chooseFlipper_flipQueueList);
        populateListView();

        return dialog;
    }

    private void populateListView() {
        FlipManager fM = FlipManager.getInstance();
        List<Child> lst = fM.getTurnQueue();

        //ArrayAdapter<Child> adapter = new ArrayAdapter<>(getActivity(), R.layout.fragment_choose_flipper, lst);
        //listView.setAdapter(adapter);

        for(Child i : lst) {
            Log.d("TESTTESTTEST", "TEST " + i.getName());
        }
    }
}