package org.wso2.carbon.processor;

import org.apache.log4j.Logger;
import org.wso2.carbon.connector.HttpConnector;
import org.wso2.carbon.data.EmmaData;
import org.wso2.carbon.util.Constants;
import org.wso2.carbon.util.EmmaXMLParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Connect to the Bamboo Server and retrieve emma report data
 */
public class EmmaProcessor {

    private String reportID;
    private static Logger log = Logger.getLogger(EmmaProcessor.class);


    private EmmaData emmaData = null;

    public EmmaProcessor() {
    }

    /**
     * Create the emma artifact path url
     *
     * @return emma artifact path url string
     */
    private String getBambooEmmaURL() {
        return "https://wso2.org/bamboo/browse/" + this.reportID + "/artifact/shared/artifact_emma/coverage.xml";
        //return "http://localhost:8085/browse/WSO2-ORBIT-2/artifact/shared/artifact_emma/coverage.xml";
    }

//    /**
//     * Get the emma data results bean
//     *
//     * @return EmmaData bean
//     */
//    public EmmaData getEmmaData() {
//        return emmaData;
//    }

    /**
     * Download and extract the emma data
     *
     * @param buildID     bamboo build ID value
     * @param buildNumber build number of the latest build value
     */
    public EmmaData getEmmaData(String buildID, String buildNumber) {

        this.reportID = buildID + "-" + buildNumber; // create the bamboo report ID

        if (downloadEmmaReport()) {
            emmaData = parseReport(this.reportID);
        }
        return emmaData;
    }

    /**
     * Download the emma report file
     *
     * @return success or failure
     */
    private boolean downloadEmmaReport() {
        HttpConnector httpConnector = new HttpConnector();
        InputStream inputStream = httpConnector.getHttpData(getBambooEmmaURL());
        FileOutputStream fileOutputStream = null;
        boolean downloadFlag = false;

        try {
            if (inputStream != null) {
                File file = new File(Constants.EMMA_FILE_NAME);
                if (file.exists()) {
                    file.delete();
                }
                fileOutputStream = new FileOutputStream(file);

                int inByte;
                while ((inByte = inputStream.read()) != -1) {
                    fileOutputStream.write(inByte);
                }
                downloadFlag = true;
            } else {
                log.info("404 Emma xml File not found");
                File file = new File(Constants.EMMA_FILE_NAME);
                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (IOException e){
            log.error("Exception", e);
        } finally{
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("Error Occurred", e);
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    log.error("Error Occurred", e);
                }
            }

        }
        return downloadFlag;
    }

    /**
     * Parse the emma report xml file and generate the EmmaReport
     *
     * @return EmmaData bean
     */
    private EmmaData parseReport(String reportID) {

        EmmaXMLParser parser = new EmmaXMLParser(Constants.EMMA_FILE_NAME);
        EmmaData data = parser.parse(reportID);

        return data;
    }
}
