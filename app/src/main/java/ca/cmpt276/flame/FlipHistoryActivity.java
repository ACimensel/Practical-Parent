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
import android.widget.TextView;

import java.util.ArrayList;

import ca.cmpt276.flame.model.Child;
import ca.cmpt276.flame.model.ChildrenManager;
import ca.cmpt276.flame.model.FlipHistoryEntry;
import ca.cmpt276.flame.model.FlipManager;

/**
 * FlipHistoryActivity allows the user to see the history of flip coin, which included child's name,
 * the result of flip coin, the state of win or lose, and the date teh child flip the coin.
 * If user click switch button, user could change state between only shows the turn child's history
 * and shows all children's history
 */
public class FlipHistoryActivity extends AppCompatActivity {
    FlipManager flipManager = FlipManager.getInstance();
    ChildrenManager childrenManager = ChildrenManager.getInstance();
    Child turnChild = flipManager.getTurnChild();
    private final ArrayList<FlipHistoryEntry> historyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_history);
        setupToolbar();
        setupSwitchButton();
        populateList();
        setupListView();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_flipHistory);
        toolbar.setTitle(R.string.flip_history);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void setupSwitchButton() {
        SwitchCompat sw = findViewById(R.id.flip_history_switch);
        //check if there is no child has been added
        if (turnChild != null) {
            sw.setText(getString(R.string.shows_only, turnChild.getName()));
        } else {
            sw.setText(R.string.shows_null);
        }

        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                //show only turn child's history
                historyList.clear();
                for (FlipHistoryEntry history : flipManager) {
                    if (history.getChildUuid().equals(turnChild.getUuid())) {
                        historyList.add(history);
                    }
                }
            } else {
                //show all history
                historyList.clear();
                for (FlipHistoryEntry history : flipManager) {
                    historyList.add(history);
                }
            }
            setupListView();
        });
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
        MyListAdapter() {
            super(FlipHistoryActivity.this, R.layout.list_view_filp_history, historyList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
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
            if (currentHistory.wasWon()) {
                win = "won";
                txtWin.setTextColor(getResources().getColor(R.color.colorAccent));
            } else {
                win = "lost";
                txtWin.setTextColor(getResources().getColor(R.color.colorRed));
            }

            txtWin.setText(win);
            txtName.setText(firsttxt);
            txtTime.setText(time);

            return itemView;
        }
    }


    protected static Intent makeIntent(Context context) {
        return new Intent(context, FlipHistoryActivity.class);
    }

}