package ca.cmpt276.flame.model;

import java.util.UUID;

/**
 * Child represents a single child. A hash map of Child objects are managed by the
 * ChildrenManager class. Each child is given a UUID so it can be uniquely referenced by
 * other classes. If children are instead referred to by index (ex. in FlipHistory), then
 * history "breaks" when a Child is deleted. If we store a Child object in FlipHistory,
 * then we'll get multiple copies of the object when we restore state from SharedPreferences
 * (one in ChildManager and a separate one in each FlipHistory) which breaks renaming.
 */
public class Child {
    private String name;
    private final UUID uuid = UUID.randomUUID();

    public Child(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    protected void setName(String name) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name must be non-empty");
        }

        this.name = name;
    }
}
