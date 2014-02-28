package org.wso2.carbon.manager;

import org.apache.log4j.Logger;
import org.wso2.carbon.processor.GitHubProcessor;
import org.wso2.carbon.data.GitHubCommitsData;
import org.wso2.carbon.data.GitHubData;
import org.wso2.carbon.data.GithubCommittersData;
import org.wso2.carbon.publisher.BAMDataPublisher;

import java.util.ArrayList;

/**
 * GitHubManager.java
 */
public class GitHubManager {

    GitHubProcessor con = new GitHubProcessor();
    BAMDataPublisher pub = new BAMDataPublisher();
    private static Logger log = Logger.getLogger(GitHubProcessor.class);

    /**
     * Extract data from github api's and save data in BAM
     */
    public void execute(String repoName, String repoOwnerName) {

        GitHubData bean = con.getGithubData(repoName, repoOwnerName);
        ArrayList<GitHubCommitsData> list = con.getCommitsList();
        ArrayList<GithubCommittersData> committersList = con.getCommittersList();

        try {

            pub.gitDataPublish(getGitArray(bean), BAMDataPublisher.GIT_HUB_STREAM, BAMDataPublisher.VERSION);

            for (GitHubCommitsData data : list) {
                pub.gitCommitsDataPublish(getGitCommitsArray(data), BAMDataPublisher.GIT_COMMITS_STREAM, BAMDataPublisher.VERSION);
            }

            for (GithubCommittersData data : committersList) {
                pub.gitCommittersDataPublich(getCommitterGitArray(data), BAMDataPublisher.GIT_COMMITTERS_STREAM, BAMDataPublisher.VERSION);
            }

        } catch (Exception e) {
            log.error("Exception", e);
        } finally {

        }

    }

    /**
     * stop the publisher
     */
    public void finalize() {
        pub.stopPublisher();
    }

    /**
     * Convert GitHubData to String array
     * @param bean Bean contains the github data
     * @return github data string array
     */
    private String[] getGitArray(GitHubData bean) {
        String[] gitDataArray = {
                bean.getRepositoryID(), bean.getOwnerID(), bean.getTotalNumberOfCommits()
                , bean.getTopContributor(), bean.getTotalPullRequests(), bean.getLastCommitDateTime()
                , "", bean.getCommitLastYear()};

        return gitDataArray;

    }

    /**
     * Convert GitHubCommitterData to String array
     * @param bean GithubCommittersData
     * @return git committers string array
     */
    private String[] getCommitterGitArray(GithubCommittersData bean) {
        String[] gitDataArray = {bean.getGitRepoName(), bean.getCommitterName(), bean.getNumberofCommits()};

        return gitDataArray;

    }


    /**
     * Convert GitHubCommitsData to String array
     * @param data
     * @return
     */
    private String[] getGitCommitsArray(GitHubCommitsData data) {
        String[] gitCommitsDataArray = {
                data.getGitRepoName(), data.getShaValue(), data.getAuthorName(), data.getAuthorEMail(),
                data.getAuthorDate(), data.getCommitMessage()
        };
        return gitCommitsDataArray;
    }
}
