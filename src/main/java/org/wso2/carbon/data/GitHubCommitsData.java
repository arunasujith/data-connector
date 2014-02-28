package org.wso2.carbon.data;

/**
 * Created by aruna on 2/12/14.
 */
public class GitHubCommitsData {

    private String gitRepoName;
    private String shaValue;
    private String authorName;
    private String authorEMail;
    private String authorDate;
    private String commitMessage;

    public String getGitRepoName() {
        return gitRepoName;
    }

    public void setGitRepoName(String gitRepoName) {
        this.gitRepoName = gitRepoName;
    }

    public String getShaValue() {
        return shaValue;
    }

    public void setShaValue(String shaValue) {
        this.shaValue = shaValue;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorDate() {
        return authorDate;
    }

    public void setAuthorDate(String authorDate) {
        this.authorDate = authorDate;
    }

    public String getAuthorEMail() {
        return authorEMail;
    }

    public void setAuthorEMail(String authorEMail) {
        this.authorEMail = authorEMail;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }

    public String toString() {
        return

                gitRepoName + " " +
                        shaValue + " " +
                        authorName + " " +
                        authorEMail + " " +
                        authorDate + " " +
                        commitMessage;
    }
}
