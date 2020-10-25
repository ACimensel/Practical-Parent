package ca.cmpt276.flame.model;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ChildrenManager is a singleton that manages multiple Child objects.
 * It is saved to SharedPreferences so that children persist when the
 * app is closed and restarted.
 */
public class ChildrenManager implements Iterable<Child> {
    private static final String SHARED_PREFS_KEY = "SHARED_PREFS_CHILDREN_MANAGER";
    private static ChildrenManager childrenManager;
    private static SharedPreferences sharedPrefs;
    private List<Child> children = new ArrayList<>();

    // Singleton

    // must be initialized to give access to SharedPreferences
    public static void init(SharedPreferences sharedPrefs) {
        if(childrenManager != null) {
            return;
        }

        ChildrenManager.sharedPrefs = sharedPrefs;
        String json = sharedPrefs.getString(SHARED_PREFS_KEY, "");

        if(json != null && !json.isEmpty()) {
            childrenManager = (new Gson()).fromJson(json, ChildrenManager.class);
        } else {
            childrenManager = new ChildrenManager();
        }
    }

    public static ChildrenManager getInstance() {
        if(childrenManager == null) {
            throw new IllegalStateException("ChildrenManager must be initialized before use");
        }

        return childrenManager;
    }

    // Normal class

    private ChildrenManager() {
        // singleton: prevent other classes from creating new ones
    }

    public void addChild(Child child) {
        if(child == null) {
            throw new IllegalArgumentException("addChild expects non-null child, null given");
        }

        children.add(child);
        persistToSharedPrefs();
    }

    public Child getChild(int i) {
        if(i < 0 || i >= children.size()) {
            throw new IllegalArgumentException("getChild expects valid in-range index, invalid index given");
        }

        return children.get(i);
    }

    public int getNumChildren() {
        return children.size();
    }

    public void removeChild(int i) {
        if(i < 0 || i >= children.size()) {
            throw new IllegalArgumentException("removeChild expects valid in-range index, invalid index given");
        }

        children.remove(i);
        persistToSharedPrefs();
    }

    // method is public because some classes might modify a
    // Child object directly and then need to save the result
    public void persistToSharedPrefs() {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        String json = (new Gson()).toJson(this);
        editor.putString(SHARED_PREFS_KEY, json);
        editor.apply();
    }

    @NonNull
    @Override
    public Iterator<Child> iterator() {
        return children.iterator();
    }
}
