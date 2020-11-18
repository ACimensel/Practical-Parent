package ca.cmpt276.flame.model;

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

    public Child(String name, String imagePath) {
        id = ChildrenManager.getInstance().getNextChildId();
        setName(name);
        setImagePath(imagePath);
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

    public void setImagePath(String imagePath) {

        this.imagePath = imagePath;
    }
}
