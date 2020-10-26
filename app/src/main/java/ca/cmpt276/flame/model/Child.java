package ca.cmpt276.flame.model;

/**
 * Child represents a single child. A collection of Child objects
 * are managed by the ChildrenManager class.
 */
public class Child {
    private String name;

    public Child(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name must be non-empty");
        }

        this.name = name;
    }
}
