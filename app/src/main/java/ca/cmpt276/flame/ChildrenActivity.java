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

import ca.cmpt276.flame.model.Child;
import ca.cmpt276.flame.model.ChildrenManager;

import ca.cmpt276.flame.model.BGMusicPlayer;

/**
 * ChildrenActivity: allow users to view a list of children
 * Users may click 'add' button on the top right to add a new child
 * Users may click an existing child to rename or delete the child
 */
public class ChildrenActivity extends AppCompatActivity {
    private final ChildrenManager childrenManager = ChildrenManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children);
        setupToolbar();
    }
    @Override
    protected void onStart() {
        super.onStart();
        setupChildrenView();
        setupClickCallBack();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_children_edit, menu);
        return true;
    }

    //setup action_add button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_action_add) {
            Intent intent = ChildEditActivity.makeIntent(ChildrenActivity.this, null);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupChildrenView() {
        ListView list = findViewById(R.id.children_listView);
        ArrayList<Child> children = new ArrayList<>();
        for (Child child : childrenManager) {
            children.add(child);
        }

        ArrayAdapter<Child> adapter = new ArrayAdapter<>(this, R.layout.list_view_children, children);
        list.setAdapter(adapter);
    }


    //setup register click callback for list view
    private void setupClickCallBack() {
        ListView list = findViewById(R.id.children_listView);
        list.setOnItemClickListener((parent, view, position, id) -> {
            ArrayList<Child> children = new ArrayList<>();
            for (Child child : childrenManager) {
                children.add(child);
            }
            //passing Child information to ChildEditActivity
            Child clickedChild = children.get(position);
            Intent intent = ChildEditActivity.makeIntent(ChildrenActivity.this, clickedChild);
            startActivity(intent);
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_Children);
        toolbar.setTitle(R.string.children);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MainActivity.isMusicEnabled) {
            BGMusicPlayer.resumeBgMusic();
        }
    }

    protected static Intent makeIntent(Context context) {
        return new Intent(context, ChildrenActivity.class);
    }
}