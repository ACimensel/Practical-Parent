package ca.cmpt276.flame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ca.cmpt276.flame.model.Child;
import ca.cmpt276.flame.model.ChildrenManager;

/**
 * ChildrenActivity: TODO add proper comment once activity created
 */
public class ChildrenActivity extends AppCompatActivity {
    private static final int ACTIVITY_RESULT_EDIT = 1;
    private static final int ACTIVITY_RESULT_ADD = 2;

    private ChildrenManager manager = ChildrenManager.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children);
        setupToolbar();
        setupChildrenView();
        ClickCallBack();
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
                Intent intent = ChildEditActivity.makeIntent(ChildrenActivity.this, new Child("only_for_add"));
                startActivityForResult(intent, 2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupChildrenView() {
        ListView list = (ListView) findViewById(R.id.listView_children);
        ArrayList<Child> children = new ArrayList<Child>();
        for (Child child : manager) {
            children.add(child);
        }

        ArrayAdapter<Child> adapter = new ArrayAdapter<>(this, R.layout.list_view_children, children);
        list.setAdapter(adapter);
    }


    //setup register click callback for listview
    private void ClickCallBack() {
        ListView list = (ListView) findViewById(R.id.listView_children);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                {
                    ArrayList<Child> children = new ArrayList<Child>();
                    for (Child child : manager) {
                        children.add(child);
                    }
                    //passing Child information to ChildEditActivity
                    Child clickedChild = children.get(position);
                    String uuid = clickedChild.getUuid().toString();
                    Intent intent = ChildEditActivity.makeIntent(ChildrenActivity.this, clickedChild);

                    startActivityForResult(intent, 1);
                }
            }
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
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        switch (requestCode) {
            case (ACTIVITY_RESULT_EDIT): {
                //if resultcode == OK, rename the child
                if (resultCode == Activity.RESULT_OK) {
                    //get information
                    String new_name = data.getStringExtra(ChildEditActivity.NEW_NAME);
                    String uuid_string = data.getStringExtra(ChildEditActivity.UUID);
                    UUID uuid = UUID.fromString(uuid_string);
                    manager.renameChild(uuid, new_name);
                    //update listview
                    setupChildrenView();
                }
                //if resultcode == First_User, delete teh child
                if (resultCode == Activity.RESULT_FIRST_USER) {
                    //get information
                    String uuid_string = data.getStringExtra(ChildEditActivity.UUID);
                    UUID uuid = UUID.fromString(uuid_string);
                    //delete the child
                    manager.removeChild(uuid);
                    //update listview
                    setupChildrenView();
                }
                break;
            }
            case (ACTIVITY_RESULT_ADD): {
                //get information
                String new_name = data.getStringExtra(ChildEditActivity.NEW_NAME);
                manager.addChild(new Child(new_name));
                //update listview
                setupChildrenView();
                break;
            }
        }
    }
}