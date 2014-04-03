package org.alfresco.share.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;

/**
 * Provides single entry point for creating datasets for tests
 * 
 * @author mbhave
 * 
 */
public class TestDataSetup extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(TestDataSetup.class);
    private static final String PROP_FILE = "src/main/resources/webdrone.properties";
    
    public static void main(String[] args)
    {
        Properties properties = new Properties();
        try
        {
            properties.load(new FileInputStream(PROP_FILE));
            List<XmlSuite> suites = new ArrayList<XmlSuite>();
            List<String> files = new ArrayList<String>();

            String baseDirectory = properties.getProperty("date.prep.baseDirectory");
            String suiteFiles = properties.getProperty("data.prep.testng.suite.files");

            StringTokenizer suiteFileTokenizer = new StringTokenizer(suiteFiles, ",");
            while (suiteFileTokenizer.hasMoreElements())
            {
                String suiteFile = (String) suiteFileTokenizer.nextElement();
                files.add(baseDirectory + suiteFile.trim());
            }

            XmlSuite suite = new XmlSuite();
            suite.setName("Data Prepartion suite");
            suite.setSuiteFiles(files);
            suites.add(suite);
            TestNG testNG = new TestNG();

            testNG.setXmlSuites(suites);
            testNG.run();
        }
        catch (IOException exception)
        {
            logger.error("Not able to read the property file: " + PROP_FILE);
        }
        
    }
 }