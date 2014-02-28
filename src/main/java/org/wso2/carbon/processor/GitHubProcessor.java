package org.wso2.carbon.processor;

import org.apache.http.HttpException;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.carbon.connector.RestConnector;
import org.wso2.carbon.data.GitHubCommitsData;
import org.wso2.carbon.data.GitHubData;
import org.wso2.carbon.data.GithubCommittersData;
import org.wso2.carbon.data.HttpHeaderData;
import org.wso2.carbon.util.Configurations;
import org.wso2.carbon.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *GitHubProcessor.java
 */
public class GitHubProcessor {

    private String gitRepoName;
    private String gitOwner;
    private String gitRestUrl;

    private static Logger log = Logger.getLogger(GitHubProcessor.class);
    private ArrayList<GitHubCommitsData> commitsList = new ArrayList<GitHubCommitsData>();
    private ArrayList<GithubCommittersData> committersList = new ArrayList<GithubCommittersData>();

    public GitHubProcessor() {

    }

    /**
     * Return the latest commits list for the repository
     * @return array list of commits data
     */
    public ArrayList<GitHubCommitsData> getCommitsList() {
        return commitsList;
    }

    /**
     * Return the latest commits list for the repository
     * @return array list of commiters data
     */
    public ArrayList<GithubCommittersData> getCommittersList() {
        return committersList;
    }

    /**
     * Create the rest url string
     * @param type type of the rest url
     * @param sha  sha value of the commits
     */
    private void getRestURL(String type, String sha) {

        if (type.equals(Constants.BRANCHES)) {
            this.gitRestUrl = Configurations.getWSO2_GIT_URL() + this.gitOwner + "/" + this.gitRepoName + "/branches";
        } else if (type.equals(Constants.COMMITS)) {
            this.gitRestUrl = Configurations.getWSO2_GIT_URL() + this.gitOwner + "/" + this.gitRepoName + "/commits?sha=" + sha;
        } else if (type.equals(Constants.STATS)) {
            this.gitRestUrl = Configurations.getWSO2_GIT_URL() + this.gitOwner + "/" + this.gitRepoName + "/stats/contributors";
        } else if (type.equals(Constants.PULL)) {
            this.gitRestUrl = Configurations.getWSO2_GIT_URL() + this.gitOwner + "/" + this.gitRepoName + "/pulls?per_page=100";
        } else if (type.equals(Constants.WEEK_STATS)) {
            this.gitRestUrl = Configurations.getWSO2_GIT_URL() + this.gitOwner + "/" + this.gitRepoName + "/stats/participation";
        }
        log.info("Git Rest URl :: " +gitRestUrl);
    }

    /**
     * Get the rest result string value
     * @param type type of the rest url
     * @param sha  sha value of the commits
     * @return json result string
     */
    private String getGithubRestData(String type, String sha) throws HttpException, IOException {

        getRestURL(type, sha);

        HttpHeaderData acceptData = new HttpHeaderData(Constants.HEADER_ACCEPT, Constants.HEADER_ACCEPT_JSON);
        HttpHeaderData authData = new HttpHeaderData(Constants.HEADER_AUTHORIZATION , Configurations.getGTIHUB_TOKEN());
        ArrayList<HttpHeaderData> headerDataList = new ArrayList<HttpHeaderData>();
        headerDataList.add(acceptData);
        headerDataList.add(authData);

        RestConnector restConnector = new RestConnector();

        String json = restConnector.getRestData(this.gitRestUrl , headerDataList);

        return json;
    }

    /**
     * Get the branches names and sha values
     * @param json string value contains branches details
     * @return map contains branches and sha values
     */
    private Map<String, String> parseBranchesJason(String json) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json);
            JSONArray jsonObject = (JSONArray) obj;

            Iterator<JSONObject> iterator = jsonObject.iterator();
            String name, sha;
            while (iterator.hasNext()) {
                JSONObject jObject = iterator.next();
                name = (String) jObject.get(Constants.NAME);
                JSONObject jObject1 = (JSONObject) jObject.get(Constants.COMMIT);
                sha = (String) jObject1.get(Constants.SHA);
                map.put(name, sha);
            }
        } catch (ParseException e) {
            log.error("PARSE EXCEPTION", e);
        }
        return map;
    }

    /**
     * Get the date value from the json string
     * @param json json string
     * @return date value
     */
    private String parseCommitsJason(String json) {
        String date = "";
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json);
            JSONArray jsonArrayObject = (JSONArray) obj;
            if (jsonArrayObject.size() > 0) {
                JSONObject jObject = (JSONObject) jsonArrayObject.get(0);
                JSONObject jObject1 = (JSONObject) jObject.get(Constants.COMMIT);
                JSONObject committerObject = (JSONObject) jObject1.get(Constants.COMMITTER);
                date = (String) committerObject.get(Constants.DATE);
            }

        } catch (ParseException e) {
            log.error("PARSE EXCEPTION", e);
        }
        return date;
    }

    /**
     * Get the number of Pull requests of a repository
     * @param json JSON String of the pull requests
     * @return Number of pull requests
     */
    private int parsePullRequestsJason(String json) {

        int pullRequests = 0;
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json);
            JSONArray jsonObject = (JSONArray) obj;
            pullRequests = jsonObject.size();
        } catch (ParseException e) {
            log.error("PARSE EXCEPTION", e);
        }
        return pullRequests;
    }

    /**
     * Get the weekly commit report
     *
     * @param json JSON String of the weekly commits
     * @return ArrayList of weekly commits
     */
    private ArrayList<Long> parseWeeklyCommits(String json) {

        ArrayList<Long> list = new ArrayList<Long>();
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json);
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray array = (JSONArray) jsonObject.get(Constants.ALL);
            if (jsonObject != null && array!= null) {
                for (int i = 0; i < array.size(); i++) {
                    long numberofCommits = (Long) array.get(i);
                    list.add(numberofCommits);
                }
            }
        } catch (ParseException e) {
            log.error("PARSE EXCEPTION", e);
        }
        return list;
    }


    /**
     *Get the Git Hub results in a particular repository
     * @param repoName repository name
     * @param owner repository owner
     * @return GitHubData bean
     */
    public GitHubData getGithubData(String repoName, String owner) {
        this.gitRepoName = repoName;
        this.gitOwner = owner;

        GitHubData bean = new GitHubData();
        bean.setRepositoryID(this.gitRepoName);
        bean.setOwnerID(this.gitOwner);
        bean.setLastCommitDateTime(getLastDateTime());
        bean.setTotalPullRequests(getNumberOfPullRequests());
        String json = getCommitStats();
        String[] commitData = getTopContributorCommits(json);
        bean.setTopContributor(commitData[0]);
        bean.setTotalNumberOfCommits(commitData[1]);
        bean.setCommitLastYear(getWeeklyCommitList().toString());

        log.info("Github results value :: "+ bean.toString());

        return bean;
    }


    /**
     * Get number of pull requests o a repository
     * @return number of pull requests
     */
    private String getNumberOfPullRequests() {
        int numberOfPullRequests = 0;

        String pullRequestsJsonString = "[]";
        try {
            pullRequestsJsonString = getGithubRestData(Constants.PULL, "");
        } catch (Exception e) {
            log.error("Exception ", e);
        }

        numberOfPullRequests = parsePullRequestsJason(pullRequestsJsonString);

        return "" + numberOfPullRequests;
    }

    /**
     * Get weekly commits list of a repository
     * @return Array List contain the weekly commits list
     */

    private ArrayList<Long> getWeeklyCommitList() {

        String json = "{}";
        try {
            json = getGithubRestData(Constants.WEEK_STATS, "");
        } catch (Exception e) {
            log.error("Exception occurred ", e);
            json = "{}";
        }

        ArrayList<Long> list = parseWeeklyCommits(json);
        return list;

    }

    /**
     * Get the latest commits date time
     * @return latest commit date time
     */
    private String getLastDateTime() {

        String branchesJsonString;
        try {
            branchesJsonString = getGithubRestData(Constants.BRANCHES, "");

        } catch (Exception e) {
            log.error("Exception occurred", e);
            branchesJsonString = "[]";
        }

        Map<String, String> map = parseBranchesJason(branchesJsonString);
        String commitsJsonString = "[]";
        try {
            if (map.get(Constants.MASTER) != null) {
                commitsJsonString = getGithubRestData(Constants.COMMITS, map.get(Constants.MASTER));
                parseCommitsList(commitsJsonString);
            }
        } catch (Exception e) {
            log.error("Exception occurred", e);
            commitsJsonString = "[]";
        }
        return parseCommitsJason(commitsJsonString);
    }

    /**
     * Parse and get the commits list
     * @param json String contains the commits data
     */
    private void parseCommitsList(String json) {

        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json);
            JSONArray jsonArrayObject = (JSONArray) obj;

            Iterator<JSONObject> iterator = jsonArrayObject.iterator();

            while (iterator.hasNext()) {
                GitHubCommitsData commitsData = new GitHubCommitsData();

                JSONObject jsonObject = iterator.next();
                commitsData.setGitRepoName(this.gitRepoName);
                commitsData.setShaValue((String) jsonObject.get(Constants.SHA));
                JSONObject jsonCommmitObject = (JSONObject) jsonObject.get(Constants.COMMIT);
                commitsData.setCommitMessage((String) jsonCommmitObject.get(Constants.MESSAGE));
                JSONObject jsonAuthorObject = (JSONObject) jsonCommmitObject.get(Constants.AUTHOR);
                commitsData.setAuthorName((String) jsonAuthorObject.get(Constants.NAME));
                commitsData.setAuthorEMail((String) jsonAuthorObject.get(Constants.EMAIL));
                commitsData.setAuthorDate((String) jsonAuthorObject.get(Constants.DATE));

                commitsList.add(commitsData);

            }

        } catch (ParseException e) {
            log.error("PARSE EXCEPTION", e);
        }

    }

    /**
     * Get the commit stats json string  // LIMITATION only 100 contributors returned
     *
     * @return json string
     */
    private String getCommitStats() {
        String json;
        try {
            json = getGithubRestData(Constants.STATS, "");
        } catch (Exception e) {
            log.error("Exception occurred", e);
            json = "[]";
        }
        return json;

    }

    /**
     * Get the top contributor and the total number of commits
     * Populate the committers
     *
     * @return json string
     */
    private String[] getTopContributorCommits(String json) {
        String[] data = {"", ""}; // author_name , numberOfCommits
        long commits = 0;
        long totalCommits = 0;
        committersList.clear();
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json);
            JSONArray jsonArrayObject = (JSONArray) obj;

            Iterator<JSONObject> iterator = jsonArrayObject.iterator(); //*************** if more than 100 contributors

            while (iterator.hasNext()) {
                GithubCommittersData committer = new GithubCommittersData();
                committer.setGitRepoName(this.gitRepoName);

                JSONObject jsonObject = iterator.next();
                long total = (Long) jsonObject.get(Constants.TOTAL);
                committer.setNumberofCommits("" + total);
                totalCommits += total;
                JSONObject authorObject = (JSONObject) jsonObject.get(Constants.AUTHOR);
                committer.setCommitterName((String) authorObject.get(Constants.LOGIN));

                if (total >= commits) {
                    data[0] = (String) authorObject.get(Constants.LOGIN);
                }
                committersList.add(committer);
            }

        } catch (ParseException e) {
            log.error("PARSE EXCEPTION", e);
        }
        data[1] = "" + totalCommits;
        return data;

    }

}

