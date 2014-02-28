package org.wso2.carbon.manager;

import org.apache.log4j.Logger;
import org.wso2.carbon.processor.BambooProcessor;
import org.wso2.carbon.processor.EmmaProcessor;
import org.wso2.carbon.data.BambooCommitsData;
import org.wso2.carbon.data.BambooData;
import org.wso2.carbon.data.EmmaData;
import org.wso2.carbon.data.EmmaPackageData;
import org.wso2.carbon.publisher.BAMDataPublisher;

import java.util.ArrayList;

/**
 * BambooManager.java
 */
public class BambooManager {


    private static Logger log = Logger.getLogger(BambooManager.class);

    BAMDataPublisher pub = new BAMDataPublisher();
    BambooProcessor bamboo = new BambooProcessor();
    EmmaProcessor emmaProcessor = new EmmaProcessor();

    /**
     * Extract data from Bamboo api's and save data in BAM
     *
     */
    public void execute(String bambooProjectID) {

        BambooData bambooBean = bamboo.getBambooData(bambooProjectID);
        ArrayList<BambooCommitsData> bambooCommitsList = bamboo.getBambooCommitsData();

        EmmaData emmaData =emmaProcessor.getEmmaData(bambooBean.getPlanID(), "" + bambooBean.getBuildNumber());

        try {

            pub.bambooDataPublish(getBambooDataArray(bambooBean), BAMDataPublisher.BAMBOO_STREAM, BAMDataPublisher.VERSION);

            if(emmaData != null){
                ArrayList<String []> emmaDataList = getBambooEmmaDataList(emmaData);
                for (String[] emmaReport : emmaDataList) {
                    pub.bambooEmmaDataPublish(emmaReport, BAMDataPublisher.BAMBOO_EMMA_STREAM, BAMDataPublisher.VERSION);
                }
            }

            if(bambooCommitsList != null){
                for(BambooCommitsData data : bambooCommitsList){
                    pub.bambooCommitsDataPublish(getBambooCommitsArray(data), BAMDataPublisher.BAMBOO_COMMITS_STREAM, BAMDataPublisher.VERSION);
                }
            }

        } catch (Exception e) {
            log.error("Exception " , e);
        }
        finally{

        }

    }

    /**
     * stop the publisher
     */
    public void finalize(){
        pub.stopPublisher();
    }

    /**
     * Prepare the array to store in BAM
     * @param data Bamboo commits data
     * @return bamboo commits data array
     */
    private String[] getBambooCommitsArray(BambooCommitsData data){

        String [] bambooCommitsArray = new String[5];
        bambooCommitsArray[0] =  data.getPlanID();
        bambooCommitsArray[1] =  data.getCommitAuthor();
        bambooCommitsArray[2] =  data.getComment();
        bambooCommitsArray[3] =  data.getChangesetID();
        bambooCommitsArray[4] =  data.getDate();

        return bambooCommitsArray;
    }

    /**
     * Prepare the array to store in BAM
     * @param bambooBean Bamboo data
     * @return bamboo data array
     */

    private String[] getBambooDataArray(BambooData bambooBean) {

        String[] bambooDataArray = new String[8];
        bambooDataArray[0] = bambooBean.getProjectID();
        bambooDataArray[1] = bambooBean.getPlanID();
        bambooDataArray[2] = bambooBean.getPlanName();
        bambooDataArray[3] = bambooBean.getBuildState();
        bambooDataArray[4] = "" + bambooBean.getBuildNumber();
        bambooDataArray[5] = "" + bambooBean.getSuccessRate();
        bambooDataArray[6] = bambooBean.getBuildDate();
        bambooDataArray[7] = bambooBean.getRelativeBuildDate();

        return bambooDataArray;
    }

    /**
     * Prepare the array to store in BAM
     * @param emmaData emma data
     * @return emma data array of array list
     */
    private ArrayList<String[]> getBambooEmmaDataList(EmmaData emmaData) {

         ArrayList<String[]> bambooEmmaDataList = new ArrayList<String[]>();

        ArrayList<EmmaPackageData> emmaPackageList = emmaData.getList();
        for (EmmaPackageData packageData : emmaPackageList) {
            String bambooEmmaDataArray[] = new String[16];
            bambooEmmaDataArray[0] = emmaData.getReportID();
            bambooEmmaDataArray[1] = "" + emmaData.getNumberOfPackages();
            bambooEmmaDataArray[2] = "" + emmaData.getNumberOfClasses();
            bambooEmmaDataArray[3] = "" + emmaData.getNumberOfMethods();
            bambooEmmaDataArray[4] = "" + emmaData.getNumberOfBlocks();
            bambooEmmaDataArray[5] = "" + emmaData.getNumberOfLines();
            bambooEmmaDataArray[6] = "" + emmaData.getNumberOfFiles();

            bambooEmmaDataArray[7] = emmaData.getAllClassCoverage();
            bambooEmmaDataArray[8] = emmaData.getAllMethodCoverage();
            bambooEmmaDataArray[9] = emmaData.getAllBlockCoverage();
            bambooEmmaDataArray[10] = emmaData.getAllLineCoverage();

            bambooEmmaDataArray[11] = packageData.getPackageName();
            bambooEmmaDataArray[12] = packageData.getClassCoverage();
            bambooEmmaDataArray[13] = packageData.getMethodCoverage();
            bambooEmmaDataArray[14] = packageData.getBlockCoverage();
            bambooEmmaDataArray[15] = packageData.getLineCoverage();

            bambooEmmaDataList.add(bambooEmmaDataArray);
        }
        return bambooEmmaDataList;
    }
}
