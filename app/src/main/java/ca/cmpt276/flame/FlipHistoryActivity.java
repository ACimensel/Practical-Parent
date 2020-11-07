package ca.cmpt276.flame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

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
        SwitchCompat switchCompat = findViewById(R.id.flipHistory_switch);
        //check if there is no child has been added
        if (turnChild != null) {
            switchCompat.setText(getString(R.string.shows_only_person_name, turnChild.getName()));
        } else {
            switchCompat.setVisibility(View.GONE);
        }

        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            historyList.clear();
            if (isChecked) {
                //show only turn child's history
                for (FlipHistoryEntry history : flipManager) {
                    if (history.getChildUuid() != null && history.getChildUuid().equals(turnChild.getUuid())) {
                        historyList.add(0, history);
                    }
                }
            } else {
                //show all history
                for (FlipHistoryEntry history : flipManager) {
                    historyList.add(0, history);
                }
            }
            setupListView();
        });
    }

    private void populateList() {
        for (FlipHistoryEntry history : flipManager) {
            historyList.add(0, history);
        }
    }

    private void setupListView() {
        TextView noCoinsFlipped = findViewById(R.id.flipHistory_txtNoCoinsFlipped);

        if (historyList.isEmpty()) {
            noCoinsFlipped.setVisibility(View.VISIBLE);
        } else {
            noCoinsFlipped.setVisibility(View.GONE);
        }

        ArrayAdapter<FlipHistoryEntry> adapter = new HistoryListAdapter();
        ListView list = findViewById(R.id.flipHistory_listView);
        list.setAdapter(adapter);
    }

    private class HistoryListAdapter extends ArrayAdapter<FlipHistoryEntry> {
        HistoryListAdapter() {
            super(FlipHistoryActivity.this, R.layout.list_view_flip_history, historyList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.list_view_flip_history, parent, false);
            }

            //find the history to work with
            FlipHistoryEntry currentHistory = historyList.get(position);

            TextView txtName = itemView.findViewById(R.id.flip_history_txtMain);

            String coinSideResult;
            if (currentHistory.getResult() == FlipManager.CoinSide.HEADS) {
                coinSideResult = getString(R.string.heads);
            } else {
                coinSideResult = getString(R.string.tails);
            }

            String flipResult;

            if (currentHistory.getChildUuid() != null) {
                Child child = childrenManager.getChild(currentHistory.getChildUuid());
                String wonOrLost;

                if (currentHistory.wasWon()) {
                    wonOrLost = getString(R.string.won_green);
                } else {
                    wonOrLost = getString(R.string.lost_red);
                }

                flipResult = getString(R.string.flip_result_child, child.getName(), coinSideResult, wonOrLost);
            } else {
                flipResult = getString(R.string.flip_result, coinSideResult);
            }

            txtName.setText(getTextFromHtml(flipResult));

            TextView txtTime = itemView.findViewById(R.id.flip_history_txtTime);
            //change the data format
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String time = format.format(currentHistory.getDate());
            txtTime.setText(time);

            return itemView;
        }
    }


    protected static Intent makeIntent(Context context) {
        return new Intent(context, FlipHistoryActivity.class);
    }

    // https://stackoverflow.com/questions/7130619/bold-words-in-a-string-of-strings-xml-in-android
    private Spanned getTextFromHtml(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(text);
        }
    }
}