package org.wso2.carbon.util;

import java.net.URL;

/**
 * StringUtility.java
 */
public class StringUtility {


    public static String getTableString(String[] fields, String[] types, String streamName, String version
            , String nickname, String descrption) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{  'name':'");
        stringBuilder.append(streamName);
        stringBuilder.append("',  'version':'");
        stringBuilder.append(version);
        stringBuilder.append("',  'nickName': '");
        stringBuilder.append(nickname);
        stringBuilder.append("',  'description': '");
        stringBuilder.append(descrption);
        stringBuilder.append("',  'payloadData':[");
        for (int i = 0; i < fields.length; i++) {

            stringBuilder.append("{'name':'");
            stringBuilder.append(fields[i]);
            stringBuilder.append("','type':'");
            stringBuilder.append(types[i]);
            if (i == fields.length - 1) {
                stringBuilder.append("'}");
            } else {
                stringBuilder.append("'},");
            }

        }

        stringBuilder.append(" ]}");
        return stringBuilder.toString();
    }

    /**
     * Get Git repository name by the url
     * @param repoURL url of the repository
     * @return
     */
    public static String getGitRepoName(String repoURL) {
        String repoName = null;

        try {
            URL u = new URL(repoURL);
            String[] array = repoURL.split("/");
            repoName = array[array.length - 1];
        } catch (Exception e) {

        }

        return repoName;

    }

    /**
     * Get Git repository owner name by the url
     * @param repoURL url of the repository
     * @return
     */
    public static String getGitRepoOwner(String repoURL) {
        String repoOwner = null;
        try {
            URL u = new URL(repoURL);
            String[] array = repoURL.split("/");
            repoOwner = array[array.length - 2];
        } catch (Exception e) {

        }

        return repoOwner;

    }

    /**
     * Get bamboo project key by the bamboo url
     * @param bambooURL url to bamboo project
     * @return bamboo project key
     */
    public static String getBambooPorjectKey(String bambooURL) {
        String bambooProjectKey = null;
        try {
            URL u = new URL(bambooURL);
            String[] array = bambooURL.split("/");
            bambooProjectKey = array[array.length - 1];
        } catch (Exception e) {

        }

        return bambooProjectKey;

    }

}