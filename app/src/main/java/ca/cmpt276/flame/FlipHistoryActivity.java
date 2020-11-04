package ca.cmpt276.flame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ca.cmpt276.flame.model.Child;
import ca.cmpt276.flame.model.ChildrenManager;
import ca.cmpt276.flame.model.FlipHistoryEntry;
import ca.cmpt276.flame.model.FlipManager;

public class FlipHistoryActivity extends AppCompatActivity {
    FlipManager flipManager = FlipManager.getInstance();
    ChildrenManager childrenManager = ChildrenManager.getInstance();
    private ArrayList<FlipHistoryEntry> historyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_history);
        setupToolbar();
        setupSwitchButton();
        populateList();
        setupListView();
    }

    private void populateList() {
        for (FlipHistoryEntry history : flipManager) {
            historyList.add(history);
        }
    }

    private void setupListView() {
        ArrayAdapter<FlipHistoryEntry> adapter = new MyListAdapter();
        ListView list = findViewById(R.id.flipHistory_listView);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<FlipHistoryEntry> {
        public MyListAdapter() {
            super(FlipHistoryActivity.this, R.layout.list_view_filp_history, historyList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.list_view_filp_history, parent, false);
            }

            //find the history to work with
            FlipHistoryEntry currentHistory = historyList.get(position);

            //Fill the view
            TextView txtName = itemView.findViewById(R.id.flip_history_txtName);
            TextView txtWin = itemView.findViewById(R.id.flip_history_txtWin);
            TextView txtTime = itemView.findViewById(R.id.flip_history_txtTime);
            Child temp = childrenManager.getChild(currentHistory.getChildUuid());
            String firsttxt = temp.getName() + " flipped " + currentHistory.getResult() + " and ";
            String win;
            String time = currentHistory.getDate().toString();
            if(currentHistory.wasWon()){
                win = "won";
                txtWin.setTextColor(getResources().getColor(R.color.colorAccent));
            }else{
                win = "lost";
                txtWin.setTextColor(getResources().getColor(R.color.colorRed));
            }

            txtWin.setText(win);
            txtName.setText(firsttxt);
            txtTime.setText(time);

            return itemView;
        }
    }

    private void setupSwitchButton() {
        SwitchCompat sw = findViewById(R.id.flip_history_switch);
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                Toast.makeText(this, "You clicked " + isChecked, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "You clicked " + isChecked, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_flipHistory);
        toolbar.setTitle(R.string.flip_history);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    protected static Intent makeIntent(Context context) {
        return new Intent(context, FlipHistoryActivity.class);
    }

}