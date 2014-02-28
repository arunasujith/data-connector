package org.wso2.carbon.connector;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.carbon.data.GRegData;
import org.wso2.carbon.registry.core.Collection;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.governance.api.util.GovernanceUtils;
import org.wso2.carbon.registry.app.RemoteRegistry;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.util.Configurations;
import org.wso2.carbon.util.Constants;
import org.wso2.carbon.util.StringUtility;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;


public class GregConnector {

    private static Logger log = Logger.getLogger(GregConnector.class);

    public GregConnector() {

    }

    /**
     * Initialize the Greg registry
     * @return
     */
    public Registry init() {

        Registry governanceRegistry = null;
        try {
            System.setProperty("carbon.repo.write.mode", "true");
            System.setProperty("javax.net.ssl.trustStore", Configurations.getGREG_HOME()
                    + "/repository/resources/security/client-truststore.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
            System.setProperty("javax.net.ssl.trustStoreType", "JKS");

            Registry rootRegistry = new RemoteRegistry(Configurations.getGREG_URL(), Configurations.getGREG_USERNAME(), Configurations.getGREG_PASSWORD());
            governanceRegistry = GovernanceUtils.getGovernanceUserRegistry(rootRegistry, Configurations.getGREG_USERNAME());
        } catch (RegistryException e) {
            log.error("Registry Exception ", e);
        } catch (MalformedURLException e) {
            log.error("Malformed URL Exceprion ", e);
        }
        return governanceRegistry;
    }

    /**
     * Get the GregData Array List
     * @param governanceRegistry Registry object
     * @return GregData Array List
     */
    public ArrayList<GRegData> getProjectsData(Registry governanceRegistry) {

        ArrayList<GRegData> gregDataList = new ArrayList<GRegData>();
        try {
            if (governanceRegistry.resourceExists(Constants.GREG_RESOURCE_PATH)) {
                Collection projectsCollection = (Collection) governanceRegistry.get(Constants.GREG_RESOURCE_PATH);
                String[] child = projectsCollection.getChildren();
                for (int i = 0; i < child.length; i++) {
                    Collection collection = (Collection) governanceRegistry.get(child[i]);
                    String[] ch = collection.getChildren();
                    for (int j = 0; j < ch.length; j++) {
                        Resource r = governanceRegistry.get(ch[j]);
                        try{
                            byte[] contentBytes = (byte[]) r.getContent();
                            String content = new String(contentBytes);
                            gregDataList.add(parseXMLString(content));
                        }catch(ClassCastException e){
                            log.error("GREG Class cast Exception" , e);
                        }

                    }
                }
            }
        } catch (RegistryException e) {
            log.error("Registry Exception ", e);
        }
        return gregDataList;
    }

    /**
     * Parse the greg data xml string
     * @param xmlString greg xml string
     * @return GReg Data Object
     */
    public GRegData parseXMLString(String xmlString) {
        GRegData gRegData = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xmlString));
            Document document = builder.parse(is);

            document.getDocumentElement().normalize();

            //read the project data
            NodeList otherNodeList = document.getElementsByTagName(Constants.OTHER);

            Node nNode = otherNodeList.item(0);
            String gitRepoURL = null, bambooURL = null;
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;
                gitRepoURL = eElement.getElementsByTagName(Constants.VERSION_CONTROL).item(0).getTextContent();
                bambooURL = eElement.getElementsByTagName(Constants.CONTINUOUS_INTEGRATION).item(0).getTextContent();
            }

            gRegData = new GRegData();
            gRegData.setGithubOwnerName(StringUtility.getGitRepoOwner(gitRepoURL));
            gRegData.setGithubRepoName(StringUtility.getGitRepoName(gitRepoURL));
            gRegData.setBambooName(StringUtility.getBambooPorjectKey(bambooURL));


        } catch (ParserConfigurationException e) {
            log.error("Parser Configuration Exception ", e);
        } catch (SAXException e) {
            log.error("SAX Exception ", e);
        } catch (IOException e) {
            log.error("IOException ", e);
        }
        return gRegData;
    }

}
