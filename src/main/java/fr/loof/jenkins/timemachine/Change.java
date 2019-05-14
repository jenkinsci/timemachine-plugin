package fr.loof.jenkins.timemachine;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class Change {

    public final String item;
    public final String path;
    public final Action action;

    public Change(String item, String path, Action action) {
        this.item = item;
        this.path = path;
        this.action = action;
    }

    @Override
    public String toString() {
        switch (action) {
            case CREATE:
                return "Created " + item;
            case DELETE:
                return "Deleted " + item;
            default:
                return "Updated " + item;
        }
    }

}
