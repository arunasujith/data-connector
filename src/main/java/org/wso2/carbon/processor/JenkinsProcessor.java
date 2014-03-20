package org.wso2.carbon.processor;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.*;
import org.apache.http.HttpException;
import org.apache.log4j.Logger;
import org.wso2.carbon.connector.RestConnector;
import org.wso2.carbon.data.HttpHeaderData;
import org.wso2.carbon.data.JenkinsData;
import org.wso2.carbon.util.Configurations;
import org.wso2.carbon.util.Constants;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * JenkinsProcessor.java
 */
public class JenkinsProcessor {

    private static Logger log = Logger.getLogger(JenkinsProcessor.class);

    private String jobName;
    private String jenkinsUrl;
    private JenkinsServer jenkinsServer;


    public JenkinsProcessor(String url) {
        this.jenkinsUrl = "http://localhost:8080";
        jenkinsServer = new JenkinsServer(URI.create("http://localhost:8080"));// get url from configs //////////////////////////////////
    }

    /**
     * Get the job Name
     *
     * @return job Name
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * Set the Job Name value
     *
     * @param jobName
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * Get the rest url
     *
     * @param type   parameter of the action type
     * @param job    job name
     * @param number build number
     * @return rest url
     */
    private String getRestURL(String type, String job, String number) {
        String url = ""; //http://localhost:8080/job/test-jenkins/2/api/json
        if (type.equals(Constants.COMMITS)) {
            url = "http://localhost:8080" + "/job/" + job + "/" + number + "/api/json";
        }
        log.info("Jenkins Rest URl :: " + url);
        return url;
    }


    /**
     * Get the rest result json string
     *
     * @param type   parameter of the action type
     * @param plan   plan name
     * @param number build number of a plan
     * @return Json string of the result
     */
    private String getJenkinsRestData(String type, String plan, String number) throws HttpException, IOException {

        HttpHeaderData acceptData = new HttpHeaderData(Constants.HEADER_ACCEPT, Constants.HEADER_ACCEPT_JSON);
        ArrayList<HttpHeaderData> headerDataList = new ArrayList<HttpHeaderData>();
        headerDataList.add(acceptData);

        RestConnector restConnector = new RestConnector();

        String json = restConnector.getRestData(getRestURL(type, plan, number), headerDataList);

        return json;
    }

    /**
     * Return the Jenkins Data
     *
     * @return JenkinsData bean
     */
    public JenkinsData getJenkinsData() {
        JenkinsData jenkinsData = new JenkinsData();
        try {
            BuildWithDetails buildWithDetails = getLatestbuild().getLastBuild().details();
            jenkinsData.setJobName(this.jobName);
            jenkinsData.setJobID(buildWithDetails.getId());
            jenkinsData.setBuildState(buildWithDetails.getResult().toString());
            jenkinsData.setBuildNumber(buildWithDetails.getNumber());
            jenkinsData.setSuccessRate(calculateSuccessRate());
            jenkinsData.setBuildDate(timestampToDate(buildWithDetails.getTimestamp()));
            jenkinsData.setBuildDuration(buildWithDetails.getDuration());
            jenkinsData.setBuildDescription(buildWithDetails.getDescription());

        } catch (IOException e) {
            log.error("Error getting jenkiins data :: ", e);
        }
        return jenkinsData;
    }

    private int getJobBuildNumber() {
        int buildNumber = 0;
        try {
            BuildWithDetails buildWithDetails = getLatestbuild().getLastBuild().details();
            buildNumber = buildWithDetails.getNumber();
        } catch (IOException e) {
            log.error("Error getting Job Build Number");
        }
        return buildNumber;

    }

    /**
     * get the latest build details
     *
     * @return JobWithDetails
     */

    private JobWithDetails getLatestbuild() {
        JobWithDetails jobWithDetails = null;
        try {
            Map<String, Job> jobs = jenkinsServer.getJobs();
            if (this.getJobName() != null) {
                Job j = jobs.get(this.getJobName());
                jobWithDetails = j.details();
            }
        } catch (IOException e) {
            log.error("Error getting the latest build ::: ", e);
        }
        return jobWithDetails;
    }

    /**
     * Get the build list for a particular job
     *
     * @return Build List
     */

    private List<Build> getAllBuildList() {

        return getLatestbuild().getBuilds();
    }

    /**
     * Calculate the success rate of the job
     *
     * @return success rate of the job
     */

    private float calculateSuccessRate() {
        List<Build> buildList = getAllBuildList();
        long numberOfBuilds = buildList.size();
        long numberOfSuccessBuilds = 0;
        for (Build build : buildList) {
            try {
                if (build.details().getResult().equals(BuildResult.SUCCESS)) {
                    numberOfSuccessBuilds++;
                }
            } catch (IOException e) {
                log.error("Error calculating success rate :: ", e);
            }
        }
        return ((float) numberOfSuccessBuilds / numberOfBuilds) * 100;
    }

    /**
     * Convert unix timestamp to a data format
     * @param timestamp unix timesstamp value
     * @return date string
     */

    private String timestampToDate(long timestamp) {
        String formatDateString = "";

        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd  HH:mm:ss");
        formatDateString = sdf.format(date);
        return formatDateString;
    }

    public static void main(String[] args)throws Exception {
        JenkinsProcessor p = new JenkinsProcessor("http://localhost:8080");
        p.setJobName("test-jenkins");
        System.out.println(p.calculateSuccessRate());
        System.out.println(p.getJenkinsData().toString());

        System.out.println(p.getJenkinsRestData(Constants.COMMITS, p.getJobName(), ""+p.getJobBuildNumber()));
    }
}
