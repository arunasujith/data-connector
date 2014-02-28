package org.wso2.carbon.data;

/**
 * GitHubData.java
 */
public class GitHubData {

    private String repositoryID;
    private String ownerID;
    private String totalNumberOfCommits;
    private String topContributor;
    private String totalPullRequests;
    private String lastCommitDateTime;
    private String commitLastYear;

    public String getCommitLastYear() {
        return commitLastYear;
    }

    public void setCommitLastYear(String commitLastYear) {
        this.commitLastYear = commitLastYear;
    }

    public String getRepositoryID() {
        return repositoryID;
    }

    public void setRepositoryID(String repositoryID) {
        this.repositoryID = repositoryID;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getTotalNumberOfCommits() {
        return totalNumberOfCommits;
    }

    public void setTotalNumberOfCommits(String totalNumberOfCommits) {
        this.totalNumberOfCommits = totalNumberOfCommits;
    }

    public String getTopContributor() {
        return topContributor;
    }

    public void setTopContributor(String topContributor) {
        this.topContributor = topContributor;
    }

    public String getTotalPullRequests() {
        return totalPullRequests;
    }

    public void setTotalPullRequests(String totalPullRequests) {
        this.totalPullRequests = totalPullRequests;
    }

    public String getLastCommitDateTime() {
        return lastCommitDateTime;
    }

    public void setLastCommitDateTime(String lastCommitDateTime) {
        this.lastCommitDateTime = lastCommitDateTime;
    }

    public String toString() {
        return "COMMITS :: " + totalNumberOfCommits + "\n" +
                "Top :: " + topContributor + "\n" +
                "PULL :: " + totalPullRequests + "\n" +
                "DATE :: " + lastCommitDateTime + "\n" +
                "RepoID :: " + repositoryID + "\n" +
                "Owner :: " + ownerID + "\n" +
                "Year :: " + commitLastYear + "\n";

    }
}
