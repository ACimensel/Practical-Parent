package ca.cmpt276.flame;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.cmpt276.flame.model.Child;
import ca.cmpt276.flame.model.FlipManager;

/**
 * A fragment class to pop up dialog box to enable user to choose child to go next
 */
public class ChooseFlipperFragment extends Fragment {
    private View v;
    private RecyclerView recyclerView;
    private FlipManager flipManager;
    private List<Child> childrenQueue;

    public ChooseFlipperFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_choose_flipper, container, false);

        recyclerView = v.findViewById(R.id.chooseFlipper_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerViewAdapter recyclerAdapter = new RecyclerViewAdapter(getContext(), childrenQueue);
        recyclerView.setAdapter(recyclerAdapter);

        setupButtons();

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        flipManager = FlipManager.getInstance();
        childrenQueue = flipManager.getTurnQueue();
    }

    private void setupButtons() {
        Button cancelBtn = v.findViewById(R.id.chooseFlipper_btnSelectCanceled);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        Button noOneBtn = v.findViewById(R.id.chooseFlipper_btnSelectNoOne);
        noOneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlipCoinActivity parent = ((FlipCoinActivity)getActivity());

                parent.childDisabled = true;
                parent.childTurnTxt.setText(R.string.no_user_selected_to_flip);
                parent.chooseSideRadioGroup.setVisibility(View.INVISIBLE);
                parent.enableFlipCoinBtn();

                getFragmentManager().popBackStack();
            }
        });
    }
}