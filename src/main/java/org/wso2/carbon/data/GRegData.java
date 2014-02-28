package org.wso2.carbon.data;

/**
 * Created by aruna on 2/18/14.
 */
public class GRegData {

    private String githubRepoName;
    private String githubOwnerName;
    private String bambooName;

    public String getGithubRepoName() {
        return githubRepoName;
    }

    public void setGithubRepoName(String githubRepoName) {
        this.githubRepoName = githubRepoName;
    }

    public String getBambooName() {
        return bambooName;
    }

    public void setBambooName(String bambooName) {
        this.bambooName = bambooName;
    }

    public String getGithubOwnerName() {
        return githubOwnerName;
    }

    public void setGithubOwnerName(String githubOwnerName) {
        this.githubOwnerName = githubOwnerName;
    }

    public String toString() {
        return githubRepoName + " " +
                githubOwnerName + " " +
                bambooName;
    }
}
