package fr.loof.jenkins.timemachine;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class Commit {
    private String sha1;
    private String diff;

    public Commit(String sha1, String diff) {
        this.sha1 = sha1;
        this.diff = diff;

    }

    public String getSha1() {
        return sha1;
    }

    public String getDiff() {
        return diff;
    }

    public String getEscapedDiff() {
        return StringEscapeUtils.escapeJava(diff);
    }
}
