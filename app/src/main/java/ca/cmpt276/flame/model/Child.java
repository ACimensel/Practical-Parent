package ca.cmpt276.flame.model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import ca.cmpt276.flame.R;

/**
 * Child represents a single child. A hash map of Child objects are managed by the
 * ChildrenManager class. Each child is given an ID so it can be uniquely referenced by
 * other classes. If they are instead referred to by index (ex. in a FlipHistoryEntry), then
 * history "breaks" when a Child is deleted. If we store a Child object in a FlipHistoryEntry,
 * then we'll get multiple copies of the object when we restore state from SharedPreferences
 * (one in ChildManager and a separate one in each FlipHistoryEntry) which breaks renaming.
 */
public class Child {
    public static final long NONE = 0L;
    private final long id;
    private String name;
    private String imagePath;

    protected Child(String name) {
        id = ChildrenManager.getInstance().getNextChildId();
        setName(name);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name must be non-empty");
        }
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    protected void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public Bitmap loadChildImage(Resources resources) {
        //Code from: https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
        Bitmap childImage;
        File f = null;
        try {
            if (this != null) {
                f = new File(this.imagePath, "" + this.getId() + "profile.jpg");
            }
            childImage = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            childImage = BitmapFactory.decodeResource(resources, R.drawable.default_child);
        }
        return childImage;
    }
}
