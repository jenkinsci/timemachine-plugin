package com.cloudbees.jenkins.plugins.timemachine;

import hudson.Extension;
import hudson.XmlFile;
import hudson.init.InitMilestone;
import hudson.init.Initializer;
import hudson.util.PluginServletFilter;
import jenkins.install.SetupWizard;
import jenkins.model.Jenkins;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.jvnet.hudson.reactor.Reactor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@Extension
public class TimeMachine {


    private Git git;
    private int rel;

    private ThreadLocal<ChangeSet> changeSet = new ThreadLocal<>() ;

    @Initializer(after = InitMilestone.PLUGINS_PREPARED)
    public void init() throws Exception {
        File rootDir = Jenkins.getInstance().getRootDir();
        try {
            git = Git.open(rootDir);
        } catch (RepositoryNotFoundException e) {
            git = Git.init().setDirectory(rootDir).call();
        }
        rel = rootDir.getCanonicalPath().length() + 1;
        System.out.printf("Timemachine ready");
        PluginServletFilter.addFilter(new TimeMachineFilter(this));
    }


    public void add(XmlFile file) throws IOException, GitAPIException {
        if (git == null) return; // jenkins init
        final String path = file.getFile().getCanonicalPath().substring(rel);
        System.out.println( " +" +path);
        final ChangeSet changeSet = this.changeSet.get();
        if (changeSet != null) {
            changeSet.add(path);
        } else {
            // change happens outside a web request
            synchronized (git) {
                git.add().addFilepattern(path).call();
                String cause = guessCause();
                final Object principal = Jenkins.getAuthentication().getPrincipal();
                String sha1 = git.commit()
                        .setAuthor(principal.toString(), "system@nowhere.org")
                        .setCommitter("👻", "timemachine-plugin@jenkins.io")
                        .setMessage("internally updated "+ path + '\n' + cause)
                        .call().name();
                System.out.println("> " + sha1);
            }
        }
    }

    /**
     * Analyze the call stack to guess the cause for this SYSTEM configuration change.
     */
    private String guessCause() {

        // TODO search for known system invokers
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            switch (element.getClassName()) {
                case "org.jvnet.hudson.reactor.Reactor":
                    return "Jenkins initialisation";
                case "jenkins.install.SetupWizard":
                    return "Setup Wizard";
            }
        }


        try (StringWriter w = new StringWriter();
             PrintWriter p = new PrintWriter(w)) {
            new Throwable().printStackTrace(p);
            return w.toString();
        } catch (IOException e) {
            return "";
        }


    }

    public void start(String message) {
        changeSet.set(new ChangeSet().withMessage(message));
    }


    public void commit() throws GitAPIException {
        if (git == null) return; // jenkins init
        final ChangeSet changeSet = this.changeSet.get();
        if (changeSet == null || changeSet.isEmpty()) return;

        synchronized (git) {
            for (String path : changeSet.getChanges()) {
                git.add().addFilepattern(path).call();
            }
            final Object principal = Jenkins.getAuthentication().getPrincipal();
            String sha1 = git.commit()
                .setAuthor(principal.toString(), "")
                .setMessage(changeSet.getMessage()) //
                .call().name();
            System.out.println("> " + sha1);
        }
    }
}
