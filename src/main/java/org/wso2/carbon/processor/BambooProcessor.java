package org.wso2.carbon.processor;

import org.apache.http.HttpException;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.carbon.connector.RestConnector;
import org.wso2.carbon.data.BambooCommitsData;
import org.wso2.carbon.data.BambooData;
import org.wso2.carbon.data.HttpHeaderData;
import org.wso2.carbon.util.Configurations;
import org.wso2.carbon.util.Constants;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * BambooConnector.java
 */
public class BambooProcessor {

    private String projectKey;
    private static ArrayList<String> planList = new ArrayList<String>();
    private ArrayList<BambooCommitsData> commitsList = new ArrayList<BambooCommitsData>();

    private static Logger log = Logger.getLogger(BambooProcessor.class);
    private String bambooRestUrl;

    public BambooProcessor() {
    }

    /**
     * Get the rest url value
     *
     * @param type       parameter of the action type
     * @param plan       plan name
     * @param number     build number of a plan
     * @param startIndex start index value to fetch data
     */
    private void getRestURL(String type, String plan, String number, String startIndex) {
        if (type.equals(Constants.PROJECT_KEY)) {
            this.bambooRestUrl = Configurations.getWSO2_BAMBOO_URL() + "/project/" + this.projectKey + "?expand=plans&start-index=" + startIndex;
        } else if (type.equals(Constants.BUILD_PLAN)) {
            this.bambooRestUrl = Configurations.getWSO2_BAMBOO_URL() + "/result/" + plan + "";
        } else if (type.equals(Constants.BUILD_PLAN_NUMBER)) {
            this.bambooRestUrl = Configurations.getWSO2_BAMBOO_URL() + "/result/" + plan + "-" + number + "?expand=changes.change.files";
        }
        log.info("Bamboo Rest URl :: " + bambooRestUrl);
    }

    /**
     * Get the rest result json string
     *
     * @param type   parameter of the action type
     * @param plan   plan name
     * @param number build number of a plan
     * @return Json string of the result
     */
    private String getBambooRestData(String type, String plan, String number, String startIndex) throws HttpException, IOException {

        getRestURL(type, plan, number, startIndex);

        HttpHeaderData acceptData = new HttpHeaderData(Constants.HEADER_ACCEPT, Constants.HEADER_ACCEPT_JSON);
        ArrayList<HttpHeaderData> headerDataList = new ArrayList<HttpHeaderData>();
        headerDataList.add(acceptData);

        RestConnector restConnector = new RestConnector();

        String json = restConnector.getRestData(this.bambooRestUrl, headerDataList);


        return json;
    }

    /**
     * Get the details of a particular plan
     *
     * @param projectKey Project key value
     * @return get the BambooData of the latest build plan
     */
    public BambooData getBambooData(String projectKey) {

        this.projectKey = projectKey;

        //get plans list using the project key
        String jsonPlanString;
        try {
            jsonPlanString = getBambooRestData(Constants.PROJECT_KEY, "", "", "0");
        } catch (Exception e) {
            log.error("Exception", e);
            jsonPlanString = "{}";
        }
        parseProjectJson(jsonPlanString);
        //iterate plan list nad find the latest build plan
        BambooData bean = getLatestBuildPlanDetails();
        log.info("Latest Build Plan Details :: " + bean.toString());

        return bean;

    }

    /**
     * @return
     */
    public ArrayList<BambooCommitsData> getBambooCommitsData() {
        return commitsList;
    }

    /**
     * Get the plan list for a project
     *
     * @param json String contains all plan list of a project
     */
    private void parseProjectJson(String json) {
        try {
            planList.clear();
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json);
            JSONObject jsonObject = (JSONObject) obj;
            if (jsonObject != null) {
                JSONObject jsonPlansObject = (JSONObject) jsonObject.get(Constants.PLANS);

                if (jsonPlansObject != null) {
                    JSONArray jsonPlanObjectArray = (JSONArray) jsonPlansObject.get(Constants.PLAN);
                    log.info("Number of Plans :: " + jsonPlanObjectArray.size());
                    Iterator<JSONObject> iterator = jsonPlanObjectArray.iterator();

                    while (iterator.hasNext()) {
                        JSONObject jObject = iterator.next();
                        planList.add((String) jObject.get(Constants.KEY));
                    }

                    if (jsonPlanObjectArray.size() >= 25) {                  // if more than 25 plans has to iterate
                        parseMorePlans();
                    }
                }
            }
        } catch (ParseException e) {
            log.error("PARSE EXCEPTION", e);
        }
    }

    /**
     * Get the plan details of number of plans exceeds twenty five
     */
    private void parseMorePlans() {
        String jsonPlanString;
        try {

            jsonPlanString = getBambooRestData(Constants.PROJECT_KEY, "", "", "25");

            JSONParser parser = new JSONParser();
            Object objectMore = parser.parse(jsonPlanString);
            JSONObject jsonObjectMore = (JSONObject) objectMore;
            if (jsonObjectMore != null) {
                JSONObject jsonPlansObjectMore = (JSONObject) jsonObjectMore.get(Constants.PLANS);

                if (jsonPlansObjectMore != null) {
                    JSONArray jsonPlanObjectArrayMore = (JSONArray) jsonPlansObjectMore.get(Constants.PLAN);
                    log.info("Number of plans :: " + jsonPlanObjectArrayMore.size() + " in " + this.projectKey);

                    Iterator<JSONObject> iterator = jsonPlanObjectArrayMore.iterator();

                    while (iterator.hasNext()) {
                        JSONObject jObject = iterator.next();
                        planList.add((String) jObject.get(Constants.KEY));
                    }
                }
            }
        } catch (Exception e) {
            log.error("Exception", e);
        }

    }

    /**
     * Get the latest build number for a particular plan
     *
     * @param json json build string for a particular plan
     * @return the latest plan number
     */

    private long parsePlanJson(String json) {
        long number = 0;
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json);
            JSONObject jsonObject = (JSONObject) obj;

            if (jsonObject != null) {
                JSONObject jsonPlansObject = (JSONObject) jsonObject.get(Constants.RESULTS);
                if (jsonPlansObject != null) {
                    JSONArray jsonPlanObjectArray = (JSONArray) jsonPlansObject.get(Constants.RESULT);

                    if (jsonPlanObjectArray.size() > 0) {
                        JSONObject jObject = (JSONObject) jsonPlanObjectArray.get(0);
                        number = (Long) jObject.get(Constants.NUMBER);
                    }
                }
            }
        } catch (ParseException e) {
            log.error("PARSE EXCEPTION", e);
        }
        return number;
    }

    /**
     * Get the latest build plan details
     *
     * @return Latest Bamboo build data bean
     */
    private BambooData getLatestBuildPlanDetails() {
        Map<String, Long> planNUmberMap = new HashMap<String, Long>();
        String jsonPlanString;
        for (String plan : planList) {
            try {
                jsonPlanString = getBambooRestData(Constants.BUILD_PLAN, plan, "", "");
            } catch (Exception e) {
                log.error("Exception", e);
                jsonPlanString = "{}";
            }
            long number = parsePlanJson(jsonPlanString);
            planNUmberMap.put(plan, number);
        }

        Set<String> plansKeySet = planNUmberMap.keySet();

        ArrayList<String> planNumberList = new ArrayList<String>();
        for (String plan : plansKeySet) {
            long number = planNUmberMap.get(plan);

            String jsonPlanNumberString;
            try {
                jsonPlanNumberString = getBambooRestData(Constants.BUILD_PLAN_NUMBER, plan, "" + number, "");
            } catch (Exception e) {
                log.error("Exception getting plan ::" + plan, e);
                jsonPlanNumberString = "{}";
            }

            planNumberList.add(jsonPlanNumberString);
        }
        String latestBuildJsonString = extractLatestBuildPlan(planNumberList);
        BambooData bean = parseLatestBuildJson(latestBuildJsonString);

        return bean;
    }

    /**
     * Calculate the latest build and return the latest build json string
     *
     * @param planNubmerJsonStringList List contains the latest build number json string
     * @return the latest build plan json string
     */

    private String extractLatestBuildPlan(ArrayList<String> planNubmerJsonStringList) {

        SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String latestJsonBuildString = "{}";
        try {
            Date latestBuildDateTime = simpleDateFormatter.parse("1900-01-01T00:00:00"); // older date to start comparison

            for (String jsonPlanNumberString : planNubmerJsonStringList) {
                try {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(jsonPlanNumberString);
                    JSONObject jsonObject = (JSONObject) obj;
                    if (jsonObject != null) {
                        String buildCompletedTime = (String) jsonObject.get(Constants.BUILD_COMPLETED_TIME);
                        if (buildCompletedTime != null) {
                            Date date = simpleDateFormatter.parse(buildCompletedTime);

                            if (date.compareTo(latestBuildDateTime) > 0) {
                                latestJsonBuildString = jsonPlanNumberString;
                                latestBuildDateTime = date;
                            }
                        }
                    }
                } catch (ParseException e) {
                    log.error("PARSE EXCEPTION", e);
                }
            }
        } catch (java.text.ParseException e) {
            log.error("PARSE EXCEPTION", e);
        }
        return latestJsonBuildString;
    }

    /**
     * Get the latest build plan details to a BambooData
     *
     * @param latestBuildJsonString
     * @return BambooData contains the latest build plan details
     */
    private BambooData parseLatestBuildJson(String latestBuildJsonString) {
        BambooData bean = new BambooData();
        bean.setProjectID(this.projectKey);
        commitsList.clear();

        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(latestBuildJsonString);
            JSONObject jsonObject = (JSONObject) obj;

            if (jsonObject != null) {

                JSONObject jsonPlanObject = (JSONObject) jsonObject.get(Constants.PLAN);
                if (jsonPlanObject != null) {
                    String key = (String) jsonPlanObject.get(Constants.KEY);
                    bean.setPlanName((String) jsonPlanObject.get(Constants.NAME));
                    bean.setPlanID(key);
                    bean.setBuildNumber((Long) jsonObject.get(Constants.NUMBER));
                    bean.setBuildState((String) jsonObject.get(Constants.STATE));
                    bean.setBuildDate(((String) jsonObject.get(Constants.BUILD_COMPLETED_TIME)).substring(0, 19));
                    bean.setRelativeBuildDate((String) jsonObject.get(Constants.BUILD_RELATIVE_TIME));
                    bean.setSuccessRate(calculateSuccessRate( bean.getPlanID()));

                    JSONObject commitsJonObject = (JSONObject) jsonObject.get(Constants.CHANGES);
                    JSONArray commitsJsonArray = (JSONArray) commitsJonObject.get(Constants.CHANGE);

                    Iterator<JSONObject> iterator = commitsJsonArray.iterator();

                    while (iterator.hasNext()) {
                        BambooCommitsData commitsData = new BambooCommitsData();
                        JSONObject jObject = iterator.next();

                        commitsData.setPlanID(key);
                        commitsData.setCommitAuthor((String) jObject.get(Constants.AUTHOR));
                        commitsData.setComment((String) jObject.get(Constants.COMMENT));
                        commitsData.setChangesetID((String) jObject.get(Constants.CHANGE_SET_ID));
                        commitsData.setDate((String) jObject.get(Constants.DATE));
                        commitsList.add(commitsData);
                    }
                }
            }

        } catch (ParseException e) {
            log.error("PARSE EXCEPTION", e);
        }

        return bean;
    }

    /**
     * Return the success rate for a particular build planID
     *
     * @param planID build plan id
     * @return success rate
     */
    public float calculateSuccessRate(String planID) {

        float successRate = 0;
        String jsonString;
        try {
            jsonString = getBambooRestData(Constants.BUILD_PLAN, planID , "", "");
        } catch (Exception e) {
            log.error("Exception ", e);
            jsonString = "{}";
        }

        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(jsonString);
            JSONObject jsonObject = (JSONObject) obj;
            if (jsonObject != null) {
                JSONObject jsonResultsObject = (JSONObject) jsonObject.get(Constants.RESULTS);
                if (jsonResultsObject != null) {
                    long size = (Long) jsonResultsObject.get(Constants.SIZE);
                    long success = 0;
                    if (size != 0) {
                        JSONArray jsonPlanResultsObjectArray = (JSONArray) jsonResultsObject.get(Constants.RESULT);

                        Iterator<JSONObject> iterator = jsonPlanResultsObjectArray.iterator();

                        while (iterator.hasNext()) {
                            JSONObject jObject = iterator.next();
                            String state = (String) jObject.get(Constants.STATE);
                            if (state.equals(Constants.SUCCESS_STRING)) {
                                ++success;
                            }
                        }
                        float successFloatVal = (float) success;
                        successRate = (successFloatVal / size) * 100;
                    }
                }
            }
        } catch (ParseException e) {
            log.error("PARSE EXCEPTION", e);
        }

        return successRate;
    }
}
