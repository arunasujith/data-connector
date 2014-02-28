package org.wso2.carbon.data;

/**
 * Created by aruna on 2/5/14.
 */
public class BambooData {

    private String projectID;
    private String planID;
    private String planName;
    private String buildState;
    private long buildNumber;
    private float successRate;
    private String buildDate;
    private String relativeBuildDate;

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getPlanID() {
        return planID;
    }

    public void setPlanID(String planID) {
        this.planID = planID;
    }
    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
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

    public String getRelativeBuildDate() {
        return relativeBuildDate;
    }

    public void setRelativeBuildDate(String relativeBuildDate) {
        this.relativeBuildDate = relativeBuildDate;
    }
    public String toString(){
        return "projectID :: "+projectID+"\n"+
                "planID :: "+planID+"\n"+
                "planName :: "+planName+"\n"+
                "buildState :: "+buildState+"\n"+
                "buildNumber :: "+buildNumber+"\n"+
                "successRate :: "+successRate+"\n"+
                "buildDate :: "+buildDate+"\n"+
                "relativeBuildDate :: "+relativeBuildDate+"\n";

    }
}

