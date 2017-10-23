package com.cloudbees.jenkins.plugins.timemachine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class ChangeSet {

    private List<String> changes = new ArrayList<>();
    private String message;

    public void add(String path) {
        this.changes.add(path);
    }

    public boolean isEmpty() {
        return changes.isEmpty();
    }

    public Iterable<String> getChanges() {
        return changes;
    }

    public String getMessage() {
        return message;
    }

    public ChangeSet withMessage(String message) {
        this.message = message;
        return this;
    }
}
