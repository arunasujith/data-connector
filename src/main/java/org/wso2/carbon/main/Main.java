package org.wso2.carbon.main;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.wso2.carbon.connector.GregConnector;
import org.wso2.carbon.data.GRegData;
import org.wso2.carbon.manager.BambooManager;
import org.wso2.carbon.manager.GitHubManager;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.util.Configurations;

import java.util.ArrayList;

/**
 * Created by aruna on 1/30/14.
 */
public class Main {

    private static Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) {

        System.out.println("\n\n");
        System.out.println("-----------------------------------------------------------------------------" +
                "----------------------------------------------------------------------------- ");
        System.out.println("-----------------------------------------------------------------------------" +
                "----------------------------------------------------------------------------- ");
        PropertyConfigurator.configure(Main.class.getClassLoader().getResource("log4j.properties"));

        boolean isConfigsLoaded = Configurations.loadConfigs();
        if (!isConfigsLoaded) {
            log.error("ERROR :: Loading Configs Failed");
            log.error("Terminating...");
            System.exit(0);
        }
        GitHubManager github = new GitHubManager();
        BambooManager bamboo = new BambooManager();

        GregConnector gregCon = new GregConnector();
        Registry registry = gregCon.init();
        ArrayList<GRegData> list = gregCon.getProjectsData(registry);

        for (GRegData data : list) {
            log.info("Greg Data :: "+ data.toString());
            if(data.getGithubRepoName() != null && !data.getGithubRepoName().equals("")
                    && data.getGithubOwnerName() != null && !data.getGithubOwnerName().equals("")){
                github.execute(data.getGithubRepoName(), data.getGithubOwnerName());
            }
            if(data.getBambooName() != null && !data.getBambooName().equals("")){
                bamboo.execute(data.getBambooName());
            }

        }

//        String[] git = {"carbon-kernel", "product-as", "wso2-synapse", "jaggery", "wso2mobileserver", "enterprise-store"};
//        String[] bam = {"WSO2CARBON", "DS0001", "WSJ001", "CR001", "TEST001", "WCB005"};
//        GitHubManager github = new GitHubManager();
//        BambooManager bamboo = new BambooManager();
//
//        for (int i = 0; i < git.length; i++) {
//            //github.execute(git[i] , "wso2");
//
//            bamboo.execute("WSO2CARBON");
//        }


        github.finalize();
        bamboo.finalize();

        System.out.println("exit");

    }


}
