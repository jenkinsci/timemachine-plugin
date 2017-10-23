package fr.loof.jenkins.timemachine;

import hudson.Extension;
import hudson.XmlFile;
import hudson.model.Saveable;
import hudson.model.listeners.SaveableListener;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.inject.Inject;
import java.io.IOException;

@Extension
public class TimeMachineSaveableListener extends SaveableListener {

    @Inject
    TimeMachine timeMachine;

    @Override
    public void onChange(Saveable o, XmlFile file) {
        try {
            timeMachine.add(file);
        } catch (IOException | GitAPIException e) {
            System.err.println("oups" + e);
            throw new RuntimeException("oups", e);
        }
    }
}

