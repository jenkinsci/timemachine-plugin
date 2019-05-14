package fr.loof.jenkins.timemachine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class ChangeSet {

    private List<Change> changes = new LinkedList<>();

    public void add(Change change) {
        this.changes.add(change);
    }

    public boolean isEmpty() {
        return changes.isEmpty();
    }

    public Collection<Change> getChanges() {
        // Deduplicate changes on same path
        // As an Item is created, we record both "Item create" + "Saveable update" events
        return changes.stream()
                .collect(Collectors.groupingBy(c -> c.path))
                .values().stream()
                .map(l -> {
                    l.sort((c1,c2) -> c1.action.compareTo(c2.action));
                    return l.get(0);
                }).collect(Collectors.toList());
    }
}
