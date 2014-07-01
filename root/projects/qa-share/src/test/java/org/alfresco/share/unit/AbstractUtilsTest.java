package org.alfresco.share.unit;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareTestProperty;
import org.alfresco.webdrone.WebDrone;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests of the AbstractUtils class. Do not place any functional tests here, i.e. 
 * anything that connects externally to Share or the Alfresco repository or anything 
 * which utilises webdriver. This is to test the abstract test class methods only.
 * 
 * @author wabson
 *
 */
public class AbstractUtilsTest extends AbstractUtils
{

    /**
     * Cloud environment, API URL inferred from Share URL
     */
    @Test
    public void testGetAPIURLCloud()
    {
        ShareTestProperty testProperties = new ShareTestProperty("https://my.alfresco.me/share", 
                "", "", SUPERADMIN_USERNAME, DEFAULT_PASSWORD, 
                AlfrescoVersion.Cloud.toString(), cloudUrlForHybrid, downloadDirectory, 
                googleUserName, googlePassword, false, "", "", "", "", "", "", "", "", "", "", "", 
                0, "", mimeTypes,"","","","", licenseShare, maxWaitTimeCloudSync.toString());

        // Will create a new instance of the bean since it has the prototype scope
        WebDrone newDrone = (WebDrone) ctx.getBean("webDrone");
        dronePropertiesMap.put(newDrone, testProperties);

        testProperties.setShareUrl("https://my.alfresco.me/share");
        Assert.assertEquals(getAPIURL(newDrone), "https://api.alfresco.me/");

        testProperties.setShareUrl("https://my.alfresco.me:/share");
        Assert.assertEquals(getAPIURL(newDrone), "https://api.alfresco.me/");

        testProperties.setShareUrl("https://my.alfresco.me:443/share");
        Assert.assertEquals(getAPIURL(newDrone), "https://api.alfresco.me/");

        testProperties.setShareUrl("https://stagmy.alfresco.me/share");
        Assert.assertEquals(getAPIURL(newDrone), "https://stagapi.alfresco.me/");
    }

    /**
     * Enterprise environment, API URL inferred from Share URL
     */
    @Test
    public void testGetAPIURLEnterprise()
    {
        ShareTestProperty testProperties = new ShareTestProperty("http://localhost:8080/share", 
                "", "", SUPERADMIN_USERNAME, DEFAULT_PASSWORD, 
                AlfrescoVersion.Enterprise.toString(), cloudUrlForHybrid, downloadDirectory, 
                googleUserName, googlePassword, false, "", "", "", "", "", "", "", "", "", "", "", 
                0, "", mimeTypes,"","","","", licenseShare, maxWaitTimeCloudSync.toString());

        // Will create a new instance of the bean since it has the prototype scope
        WebDrone newDrone = (WebDrone) ctx.getBean("webDrone");
        dronePropertiesMap.put(newDrone, testProperties);

        // TODO Shouldn't this be 'http://localhost:8080/' since 'alfresco/api/' gets added
        Assert.assertEquals(getAPIURL(newDrone), "http://localhost:8080/alfresco/api/");
    }

    /**
     * API URL explicitly provided in the test properties
     */
    @Test
    public void testGetAPIURLAfterExplicitSet()
    {
        ShareTestProperty testProperties = new ShareTestProperty("http://localhost:8081/share", 
                "http://localhost:8080/", "", SUPERADMIN_USERNAME, DEFAULT_PASSWORD, 
                AlfrescoVersion.Enterprise.toString(), cloudUrlForHybrid, downloadDirectory, 
                googleUserName, googlePassword, false, "", "", "", "", "", "", "", "", "", "", "", 
                0, "", mimeTypes,"","","","", licenseShare, maxWaitTimeCloudSync.toString());

        // Will create a new instance of the bean since it has the prototype scope
        WebDrone newDrone = (WebDrone) ctx.getBean("webDrone");
        dronePropertiesMap.put(newDrone, testProperties);

        Assert.assertEquals(getAPIURL(newDrone), "http://localhost:8080/");
    }

}
