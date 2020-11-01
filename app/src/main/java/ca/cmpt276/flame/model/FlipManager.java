package ca.cmpt276.flame.model;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * FlipManager is a singleton class that manages coin flips and
 * a list of FlipHistoryEntry objects. It is persisted between app
 * launches using SharedPreferences.
 */
public class FlipManager implements Iterable<FlipHistoryEntry> {
    /** CoinSide represents the two possible sides of a coin */
    public enum CoinSide {
        HEADS,
        TAILS
    }

    private static final String SHARED_PREFS_KEY = "SHARED_PREFS_FLIP_MANAGER";
    private static FlipManager flipManager;
    private static SharedPreferences sharedPrefs;
    private final List<FlipHistoryEntry> history = new ArrayList<>();

    // Singleton

    // must be initialized to give access to SharedPreferences
    public static void init(SharedPreferences sharedPrefs) {
        if(flipManager != null) {
            return;
        }

        FlipManager.sharedPrefs = sharedPrefs;
        String json = sharedPrefs.getString(SHARED_PREFS_KEY, "");

        if(json != null && !json.isEmpty()) {
            flipManager = (new Gson()).fromJson(json, FlipManager.class);
        } else {
            flipManager = new FlipManager();
        }
    }

    public static FlipManager getInstance() {
        if(flipManager == null) {
            throw new IllegalStateException("FlipManager must be initialized before use");
        }

        return flipManager;
    }

    // Normal class

    private FlipManager() {
        // singleton: prevent other classes from creating new ones
    }

    // returns the child who's turn it is to flip
    // may return null if there are no children configured
    public Child getTurnChild() {
        // if we have no children then return null
        ChildrenManager childrenManager = ChildrenManager.getInstance();
        if(childrenManager.getNumChildren() == 0) {
            return null;
        }

        // if we have no history entries, return the first child
        if(history.size() == 0) {
            return childrenManager.iterator().next();
        }

        // find the last child who flipped and then let the next one flip
        FlipHistoryEntry lastFlip = history.get(history.size() - 1);
        UUID lastChild = lastFlip.getChildUuid();

        Iterator<Child> itr = childrenManager.iterator();
        while(itr.hasNext()) {
            Child child = itr.next();

            if(child.getUuid().equals(lastChild) && itr.hasNext()) {
                return itr.next();
            }
        }

        // return first child if it "wrapped around"
        return childrenManager.iterator().next();
    }

    // performs a coin flip, adds it to the history and returns the result
    public CoinSide doFlip(CoinSide selection) {
        CoinSide result = getRandomCoinSide();
        Child child = getTurnChild();

        if(child == null) {
            history.add(new FlipHistoryEntry(null, result, false));
        } else {
            history.add(new FlipHistoryEntry(child.getUuid(), result, result == selection));
        }

        persistToSharedPrefs();
        return result;
    }

    private CoinSide getRandomCoinSide() {
        CoinSide[] sides = CoinSide.values();
        int randomChoice = (int) Math.floor(Math.random() * sides.length);
        return sides[randomChoice];
    }

    public CoinSide getLastCoinValue() {
        if(history.size() == 0) {
            return CoinSide.TAILS;
        }

        return history.get(history.size() - 1).getResult();
    }

    // when a child is deleted, also remove them from history
    // should only be called by the ChildrenManager
    protected void removeChildFromHistory(UUID childUuid) {
        for(int i = 0; i < history.size(); i++) {
            if(history.get(i).getChildUuid().equals(childUuid)) {
                history.remove(i);
                i--; // array size has now changed
            }
        }
        persistToSharedPrefs();
    }

    private void persistToSharedPrefs() {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        String json = (new Gson()).toJson(this);
        editor.putString(SHARED_PREFS_KEY, json);
        editor.apply();
    }

    @NonNull
    @Override
    public Iterator<FlipHistoryEntry> iterator() {
        return history.iterator();
    }
}
