package fr.loof.jenkins.timemachine;

import hudson.Extension;
import hudson.XmlFile;
import hudson.init.InitMilestone;
import hudson.init.Initializer;
import hudson.model.ManagementLink;
import hudson.util.PluginServletFilter;
import jenkins.model.Jenkins;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.revwalk.RevCommit;

import javax.annotation.CheckForNull;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@Extension
public class TimeMachine extends ManagementLink {

    public static final Logger log = Logger.getLogger(TimeMachine.class.getName());

    @Override
    public String getIconFileName() {
        return "/plugin/timemachine/48x48/delorean.png";
    }

    @Override
    public String getDisplayName() {
        return "Time machine";
    }

    @Override
    public String getDescription() {
        return "Track and manage configuration changes";
    }


    @Override
    public String getUrlName() {
        return "timemachine";
    }



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
        log.info("Timemachine ready, using git repository "+rootDir);
        PluginServletFilter.addFilter(new TimeMachineFilter(this));
    }


    public void add(XmlFile file) throws IOException, GitAPIException {
        if (git == null) return; // jenkins init
        final String path = file.getFile().getCanonicalPath().substring(rel);
        log.fine( " +" +path);
        final ChangeSet changeSet = this.changeSet.get();
        if (changeSet != null) {
            changeSet.add(path);
        } else {
            // change happens outside a web request
            synchronized (git) {
                git.add().addFilepattern(path).call();
                if (git.status().call().isClean()) return;

                String cause = guessCause();
                final Object principal = Jenkins.getAuthentication().getPrincipal();
                final String author = principal.toString();
                String sha1 = git.commit()
                        .setAuthor(author, "system@nowhere.org") // TODO get author's email from User.getUserProperty
                        .setCommitter("👻", "timemachine-plugin@jenkins.io")
                        .setMessage("internally updated "+ path + '\n' + cause)
                        .call().name();
                log.fine("> " + sha1);
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

    public void start() {
        changeSet.set(new ChangeSet());
    }


    public void commit(String message) throws GitAPIException {
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
                .setMessage(message)
                .call().name();
            log.fine("> " + sha1);
        }
    }

    public Iterable<RevCommit> getHistory() throws GitAPIException {
        return git.log()
            .setMaxCount(100)
            .call();
    }

}
