package org.wso2.carbon.data;

/**
 * JenkinsData.java
 */
public class JenkinsData {

    private String jobName;
    private String jobID;
    private String buildState;
    private long buildNumber;
    private float successRate;
    private String buildDate;
    private long buildDuration;
    private String buildDescription;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public String getBuildState() {
        return buildState;
    }

    public void setBuildState(String buildState) {
        this.buildState = buildState;
    }

    public long getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(long buildNumber) {
        this.buildNumber = buildNumber;
    }

    public float getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(float successRate) {
        this.successRate = successRate;
    }

    public String getBuildDate() {
        return buildDate;
    }

    public void setBuildDate(String buildDate) {
        this.buildDate = buildDate;
    }

    public long getBuildDuration() {
        return buildDuration;
    }

    public void setBuildDuration(long buildDuration) {
        this.buildDuration = buildDuration;
    }

    public String getBuildDescription() {
        return buildDescription;
    }

    public void setBuildDescription(String buildDescription) {
        this.buildDescription = buildDescription;
    }

    public String toString() {
        return

                "planName :: " + jobName + "\n" +
                        "job ID :: " + jobID + "\n" +
                        "buildState :: " + buildState + "\n" +
                        "buildNumber :: " + buildNumber + "\n" +
                        "successRate :: " + successRate + "\n" +
                        "buildDate :: " + buildDate + "\n" +
                        "buildDescription :: " + buildDescription + "\n" +
                        "buildDuration :: " + buildDuration + "\n";

    }
}

