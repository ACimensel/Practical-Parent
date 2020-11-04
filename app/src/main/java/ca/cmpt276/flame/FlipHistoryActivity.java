package ca.cmpt276.flame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

import ca.cmpt276.flame.model.Child;
import ca.cmpt276.flame.model.FlipHistoryEntry;
import ca.cmpt276.flame.model.FlipManager;

public class FlipHistoryActivity extends AppCompatActivity {
    FlipManager flipManager = FlipManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_history);
        setupToolbar();
        setupSwitchButton();
        //setupListView();
    }

    private void setupListView() {
        ListView list = findViewById(R.id.flipHistory_listView);
        ArrayList<FlipHistoryEntry> historyList = new ArrayList<>();
        for (FlipHistoryEntry history : flipManager) {
            historyList.add(history);
        }
        ArrayAdapter<FlipHistoryEntry> adapter = new ArrayAdapter<>(this, R.layout.list_view_filp_history, historyList);
        list.setAdapter(adapter);
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