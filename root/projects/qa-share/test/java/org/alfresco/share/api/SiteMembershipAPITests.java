package org.alfresco.share.api;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.rest.api.tests.client.HttpResponse;
import org.alfresco.rest.api.tests.client.PublicApiClient.ListResponse;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.api.tests.client.data.SiteMembershipRequest;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.share.util.api.SiteMembershipAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class to include: Tests for SiteMembershipAPI implemented in alfresco-remote-api.
 * 
 * @author Abhijeet Bharade
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "AlfrescoOne", "Cloud2" })
public class SiteMembershipAPITests extends SiteMembershipAPI
{
    private static final String MESSAGE = "This is a newly created site";
    private static String requestForUser;
    private static String requestForUser1;
    private String mainUser;
    private static String siteName;
    private static String siteName2;
    private static String modSiteName;
    private static String modSiteName2;
    private static String modSiteName3;
    private static String probeSite;
    private static String privateSiteName;
    private static Log logger = LogFactory.getLog(SiteMembershipAPITests.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception
    {
        super.beforeClass();

        testName = this.getClass().getSimpleName() + System.currentTimeMillis();

        requestForUser = getUserNameFreeDomain(testName + "req");
        requestForUser1 = getUserNameFreeDomain(testName + "req_1");
        mainUser = getUserNameFreeDomain(testName + "main");

        siteName = getSiteName(testName) + System.currentTimeMillis();
        siteName2 = siteName + "public2";
        modSiteName = siteName + "mod1";
        modSiteName2 = siteName + "mod2";
        modSiteName3 = siteName + "mod3";
        probeSite = siteName + "probe";
        privateSiteName = siteName + "private";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, mainUser);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, requestForUser);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, requestForUser1);

        ShareUser.login(drone, mainUser, DEFAULT_PASSWORD);

        SiteUtil.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        SiteUtil.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC);
        SiteUtil.createSite(drone, modSiteName, SITE_VISIBILITY_MODERATED);
        SiteUtil.createSite(drone, modSiteName2, SITE_VISIBILITY_MODERATED);
        SiteUtil.createSite(drone, modSiteName3, SITE_VISIBILITY_MODERATED);
        SiteUtil.createSite(drone, probeSite, SITE_VISIBILITY_MODERATED);
        SiteUtil.createSite(drone, privateSiteName, SITE_VISIBILITY_PRIVATE);

        ShareUser.logout(drone);
    }

    @Test
    public void ALF_251001() throws Exception
    {

        SiteMembershipRequest siteMembershipRequest = createSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, modSiteName, MESSAGE);
        assertNotNull(siteMembershipRequest);

        assertEquals(ShareUser.getSiteShortname(modSiteName), siteMembershipRequest.getId().toLowerCase());
    }

    @Test(enabled = true)
    public void ALF_253801() throws Exception
    {
        SiteMembershipRequest siteMembershipRequest = createSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, siteName2, "");
        assertNotNull(siteMembershipRequest);
        assertEquals(siteMembershipRequest.getId().toLowerCase(), ShareUser.getSiteShortname(siteName2));
        siteMembershipRequest = null;
    }

    @Test(enabled = true)
    public void ALF_250201() throws Exception
    {

        SiteMembershipRequest siteMembershipRequest = createSiteMembershipRequest(requestForUser, DOMAIN, "-me-", siteName, "");
        assertNotNull(siteMembershipRequest);
        assertEquals(ShareUser.getSiteShortname(siteName), siteMembershipRequest.getId().toLowerCase());
        siteMembershipRequest = null;

        siteMembershipRequest = createSiteMembershipRequest(requestForUser, DOMAIN, "-me-", getSiteShortname(modSiteName2), MESSAGE);
        assertNotNull(siteMembershipRequest);
        assertEquals(ShareUser.getSiteShortname(modSiteName2), siteMembershipRequest.getId().toLowerCase());
        assertEquals(MESSAGE.toLowerCase(), siteMembershipRequest.getMessage().toLowerCase());
        siteMembershipRequest = null;

        ShareUser.login(drone, mainUser);

        AbstractWorkflow.openMyTaskPage(drone);

        AbstractWorkflow.checkIfTaskIsPresent(drone, "Request to join " + modSiteName2 + " site", true);
    }

    @Test(enabled = true)
    public void ALF_250101() throws Exception
    {
        ListResponse<SiteMembershipRequest> smrListResponse = getSiteMembershipRequest(mainUser, DOMAIN, mainUser, null);
        assertNotNull(smrListResponse);
        assertEquals(smrListResponse.getList().size(), 0);
    }

    @Test(enabled = true)
    public void ALF_253101() throws Exception
    {
        ListResponse<SiteMembershipRequest> smrListResponse = getSiteMembershipRequest(mainUser, DOMAIN, "-me-", null);
        assertNotNull(smrListResponse);
        assertEquals(smrListResponse.getList().size(), 0);
    }

    @Test(enabled = true)
    public void ALF_250301() throws Exception
    {

        SiteMembershipRequest siteMembershipRequest = createSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, siteName, null);
        assertNotNull(siteMembershipRequest);
        assertEquals(ShareUser.getSiteShortname(siteName), siteMembershipRequest.getId().toLowerCase());
        siteMembershipRequest = null;

        try
        {
            updateSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, siteName, MESSAGE);
            Assert.fail("ALF_250301: SMR should not exist as site is public.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    // updateSiteMembershipRequestNoChangeMessage
    @Test(enabled = true)
    public void ALF_251501() throws Exception
    {
        SiteMembershipRequest siteMembershipRequest = createSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, modSiteName, null);
        assertNotNull(siteMembershipRequest);
        assertEquals(modSiteName.toLowerCase(), siteMembershipRequest.getId().toLowerCase());
        siteMembershipRequest = null;

        siteMembershipRequest = updateSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, getSiteShortname(modSiteName), MESSAGE);
        assertNotNull(siteMembershipRequest);
        assertEquals(MESSAGE.toLowerCase(), siteMembershipRequest.getMessage().toLowerCase());
        Date firstMod = siteMembershipRequest.getModifiedAt();

        siteMembershipRequest = updateSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, getSiteShortname(modSiteName), MESSAGE);
        assertNotNull(siteMembershipRequest);
        assertEquals(MESSAGE.toLowerCase(), siteMembershipRequest.getMessage().toLowerCase());
        assertTrue(firstMod.before(siteMembershipRequest.getModifiedAt()));
    }

    // updateSiteMembershipRequestInvalid
    @Test(enabled = true)
    public void ALF_251601() throws Exception
    {

        SiteMembershipRequest siteMembershipRequest = createSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, modSiteName3, "");
        assertNotNull(siteMembershipRequest);
        assertEquals(ShareUser.getSiteShortname(modSiteName3), siteMembershipRequest.getId().toLowerCase());
        siteMembershipRequest = null;

        try
        {
            updateSiteMembershipRequest(requestForUser, DOMAIN, requestForUser + "aa", getSiteShortname(modSiteName3), MESSAGE);
            Assert.fail("ALF_251601: username error - " + requestForUser + "aa");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }

        try
        {
            updateSiteMembershipRequest(requestForUser, DOMAIN, requestForUser + "aa", getSiteShortname(modSiteName3), "");
            Assert.fail("ALF_251601: username error - " + requestForUser + "aa");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }

    }

    @Test(enabled = true)
    public void ALF_250401() throws Exception
    {

        SiteMembershipRequest siteMembershipRequest = createSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, siteName2, MESSAGE);
        assertNotNull(siteMembershipRequest);
        assertEquals(ShareUser.getSiteShortname(siteName2), siteMembershipRequest.getId().toLowerCase());
        siteMembershipRequest = null;

        try
        {
            cancelSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, getSiteShortname(siteName2));
            Assert.fail("ALF_250401: cannot cancel SMR for a public site");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    // getInvalidSiteMember
    @Test
    public void ALF_250601() throws Exception
    {
        try
        {
            getSiteMembershipRequest(mainUser, DOMAIN, requestForUser, null);
            Assert.fail("Get SMR for user other than auth user should fail with 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    @Test
    public void ALF_251101() throws Exception
    {
        try
        {
            createSiteMembershipRequest(requestForUser, DOMAIN, requestForUser + "ggg", modSiteName, MESSAGE);
            Assert.fail("ALF_251101: Cannot create SMR for invalid user - " + requestForUser + "ggg");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    @Test
    public void ALF_252401() throws Exception
    {
        try
        {
            cancelSiteMembershipRequest(requestForUser, DOMAIN, requestForUser + "fff", getSiteShortname(modSiteName));
            Assert.fail("Delete SMR when personid is different than auth user should fail with 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    // deleteSMRTwice
    @Test
    public void ALF_252801() throws Exception
    {

        SiteMembershipRequest siteMembershipRequest = createSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, modSiteName3, MESSAGE);
        assertNotNull(siteMembershipRequest);
        assertEquals(modSiteName3.toLowerCase(), siteMembershipRequest.getId().toLowerCase());
        siteMembershipRequest = null;

        HttpResponse response = cancelSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, getSiteShortname(modSiteName3));
        assertNotNull(response);
        assertEquals(response.getStatusCode(), 204);

        try
        {
            cancelSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, getSiteShortname(modSiteName3));
            Assert.fail("Delete deleted SMR: should return 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    // getPagingSiteMembership
    @Test
    public void ALF_250901() throws Exception
    {
        ListResponse<SiteMembershipRequest> smrResponse = getSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, null);
        assertEquals(smrResponse.getPaging().getSkipCount(), new Integer(0));
        assertEquals(smrResponse.getPaging().getMaxItems(), new Integer(100));

        smrResponse = null;
        Map<String, String> param = new HashMap<String, String>();
        param.put("skipCount", "2");
        smrResponse = getSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, param);
        assertEquals(smrResponse.getPaging().getSkipCount(), new Integer(2));
        assertEquals(smrResponse.getPaging().getMaxItems(), new Integer(100));

        smrResponse = null;
        param = new HashMap<String, String>();
        param.put("maxItems", "3");
        smrResponse = getSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, param);
        assertEquals(smrResponse.getPaging().getSkipCount(), new Integer(0));
        assertEquals(smrResponse.getPaging().getMaxItems(), new Integer(3));

        smrResponse = null;
        param = new HashMap<String, String>();
        param.put("skipCount", "2");
        param.put("maxItems", "10");

        smrResponse = getSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, param);
        assertEquals(smrResponse.getPaging().getSkipCount(), new Integer(2));
        assertEquals(smrResponse.getPaging().getMaxItems(), new Integer(10));
    }

    // getInvalidSiteMember
    @Test
    public void ALF_254201() throws Exception
    {
        try
        {
            getSiteMembershipRequest(requestForUser, DOMAIN, requestForUser + "sdf", null);
            Assert.fail("ALF_254201: Cannot get existing SMR for invalid user - " + requestForUser + "sdf");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    // getInvalidSiteMember
    @Test
    public void ALF_253901() throws Exception
    {
        SiteMembershipRequest siteMembershipRequest = createSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, probeSite, "");
        assertNotNull(siteMembershipRequest);
        assertEquals(ShareUser.getSiteShortname(probeSite), siteMembershipRequest.getId().toLowerCase());

        ShareUser.login(drone, mainUser);
        SiteUtil.changeSiteVisibility(drone, probeSite, true, false);

        try
        {
            createSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, probeSite, "");
            Assert.fail("ALF_254201: Cannot create SMR for private site " + probeSite + "Error 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

}
