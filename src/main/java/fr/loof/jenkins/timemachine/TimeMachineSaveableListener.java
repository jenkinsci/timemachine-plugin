package fr.loof.jenkins.timemachine;

import hudson.Extension;
import hudson.XmlFile;
import hudson.model.Item;
import hudson.model.ModelObject;
import hudson.model.Saveable;
import hudson.model.listeners.SaveableListener;
import hudson.search.SearchItem;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.inject.Inject;
import java.io.IOException;

@Extension
public class TimeMachineSaveableListener extends SaveableListener {

    @Inject
    TimeMachine timeMachine;

    @Override
    public void onChange(Saveable o, XmlFile file) {
        String name = o instanceof ModelObject ?
                ((ModelObject) o).getDisplayName():
                o.toString();

        try {
            timeMachine.add(name, file, Action.UPDATE);
        } catch (IOException | GitAPIException e) {
            System.err.println("oups" + e);
            throw new RuntimeException("oups", e);
        }
    }
}

