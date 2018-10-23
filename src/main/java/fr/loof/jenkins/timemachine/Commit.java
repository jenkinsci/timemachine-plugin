package fr.loof.jenkins.timemachine;

import java.util.List;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class Commit {
    private String sha1;
    private List<String> details;

    public Commit(String sha1, List<String> details) {

        this.sha1 = sha1;
        this.details = details;
    }

    public String getSha1() {
        return sha1;
    }

    public List<String> getDetails() {
        return details;
    }
}
