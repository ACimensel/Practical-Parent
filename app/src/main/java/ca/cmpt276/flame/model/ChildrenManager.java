package ca.cmpt276.flame.model;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * ChildrenManager is a singleton that manages multiple Child objects.
 * It is saved to SharedPreferences so that children persist when the
 * app is closed and restarted.
 */
public class ChildrenManager implements Iterable<Child> {
    private static final String SHARED_PREFS_KEY = "SHARED_PREFS_CHILDREN_MANAGER";
    private static ChildrenManager childrenManager;
    private static SharedPreferences sharedPrefs;
    private final LinkedHashMap<UUID, Child> children = new LinkedHashMap<>();

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

    public void addChild(Child child) {
        if(child == null) {
            throw new IllegalArgumentException("addChild expects non-null child, null given");
        }

        children.put(child.getUuid(), child);
        persistToSharedPrefs();
    }

    public Child getChild(UUID id) {
        checkValidChildUuid(id);
        return children.get(id);
    }

    public void renameChild(UUID id, String name) {
        checkValidChildUuid(id);
        children.get(id).setName(name);
        persistToSharedPrefs();
    }

    public void changeChildPic(UUID id, String imageUri) {
        checkValidChildUuid(id);
        children.get(id).setImageUri(imageUri);
        persistToSharedPrefs();
    }

    public void removeChild(UUID id) {
        checkValidChildUuid(id);
        FlipManager.getInstance().removeChildFromHistory(id);
        children.remove(id);
        persistToSharedPrefs();
    }

    private void checkValidChildUuid(UUID id) {
        if(!children.containsKey(id)) {
            throw new IllegalArgumentException("ChildrenManager expects ID to correspond to valid child");
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
