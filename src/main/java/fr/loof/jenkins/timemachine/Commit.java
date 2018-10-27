package fr.loof.jenkins.timemachine;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.Date;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class Commit {

    private String sha1;
    private final String message;
    private final String author;
    private final long time;
    private String diff;

    public Commit(RevCommit commit, String diff) {
        this.sha1 = commit.name();
        this.message = commit.getFullMessage();
        this.author = commit.getAuthorIdent().getName();
        this.time = commit.getCommitTime();
        this.diff = diff;

    }

    public String getSha1() {
        return sha1;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public Date getTime() {
        return new Date(time*1000);
    }

    public String getDiff() {
        return diff;
    }

    public String getEscapedDiff() {
        final String s = StringEscapeUtils.escapeJava(diff);
        return StringUtils.replace(s, "<", "\\u003C");
    }
}
