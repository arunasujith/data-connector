package org.wso2.carbon.data;

/**
 * Created by aruna on 2/9/14.
 */
public class BambooCommitsData {



    private String planID;
    private String commitAuthor;
    private String comment;
    private String changesetID;
    private String date;

    public String getPlanID() {
        return planID;
    }

    public void setPlanID(String planID) {
        this.planID = planID;
    }
    public String getCommitAuthor() {
        return commitAuthor;
    }

    public void setCommitAuthor(String commitAuthor) {
        this.commitAuthor = commitAuthor;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getChangesetID() {
        return changesetID;
    }

    public void setChangesetID(String changesetID) {
        this.changesetID = changesetID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
