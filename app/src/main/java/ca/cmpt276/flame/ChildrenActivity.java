package ca.cmpt276.flame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
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

/**
 * ChildrenActivity: allow users to view a list of children
 * Users may click 'add' button on the top right to add a new child
 * Users may click an existing child to rename or delete the child
 */
public class ChildrenActivity extends AppCompatActivity {
    private ChildrenManager childManager = ChildrenManager.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children);
        setupToolbar();
        setupChildrenView();
        setupClickCallBack();
    }

    //setup menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_children_edit, menu);
        return true;
    }

    //setup action_add button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = ChildEditActivity.makeIntent(ChildrenActivity.this, null);
                startActivityForResult(intent, 2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupChildrenView() {
        ListView list = (ListView) findViewById(R.id.listView_children);
        ArrayList<Child> children = new ArrayList<Child>();
        for (Child child : childManager) {
            children.add(child);
        }

        ArrayAdapter<Child> adapter = new ArrayAdapter<>(this, R.layout.list_view_children, children);
        list.setAdapter(adapter);
    }


    //setup register click callback for list view
    private void setupClickCallBack() {
        ListView list = (ListView) findViewById(R.id.listView_children);
        list.setOnItemClickListener((parent, view, position, id) -> {
            ArrayList<Child> children = new ArrayList<Child>();
            for (Child child : childManager) {
                children.add(child);
            }
            //passing Child information to ChildEditActivity
            Child clickedChild = children.get(position);
            Intent intent = ChildEditActivity.makeIntent(ChildrenActivity.this, clickedChild);
            startActivityForResult(intent, 1);
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_Children);
        toolbar.setTitle(R.string.children);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    protected static Intent makeIntent(Context context) {
        return new Intent(context, ChildrenActivity.class);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //refresh list view
            setupChildrenView();
        }
    }
}