package ca.cmpt276.flame.model;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * ChildrenManager is a singleton that manages multiple Child objects.
 * It is saved to SharedPreferences so that children persist when the
 * app is closed and restarted.
 */
public class ChildrenManager implements Iterable<Child> {
    private static final String SHARED_PREFS_KEY = "SHARED_PREFS_CHILDREN_MANAGER";
    private static ChildrenManager childrenManager;
    private static SharedPreferences sharedPrefs;
    private long nextChildId = 1L;
    private final LinkedHashMap<Long, Child> children = new LinkedHashMap<>();

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

    public int getNumChildren() {
        return children.size();
    }

    public void addChild(String name) {
        Child child = new Child(name);
        children.put(child.getId(), child);
        persistToSharedPrefs();
    }

    // may return null if the child ID does not exist
    public Child getChild(long id) {
        if(children.containsKey(id)) {
            return children.get(id);
        }

        return null;
    }

    public void renameChild(Child child, String name) {
        checkValidChild(child);
        child.setName(name);
        persistToSharedPrefs();
    }

    public void removeChild(Child child) {
        checkValidChild(child);
        FlipManager.getInstance().removeChildFromHistory(child.getId());
        children.remove(child.getId());
        persistToSharedPrefs();
    }

    protected long getNextChildId() {
        return nextChildId++;
    }

    private void checkValidChild(Child child) {
        if(child == null) {
            throw new IllegalArgumentException("ChildrenManager expects non-null child");
        } else {
            if(!children.containsKey(child.getId())) {
                throw new IllegalArgumentException("ChildrenManager expects ID to correspond to valid child");
            }
        }
    }

    private void persistToSharedPrefs() {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        String json = (new Gson()).toJson(this);
        editor.putString(SHARED_PREFS_KEY, json);
        editor.apply();
    }

    @NonNull
    @Override
    public Iterator<Child> iterator() {
        return children.values().iterator();
    }
}
