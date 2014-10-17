package org.alfresco.share.api;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.rest.api.tests.client.HttpResponse;
import org.alfresco.rest.api.tests.client.PublicApiClient.ListResponse;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.api.tests.client.data.SiteMembershipRequest;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.RandomUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.share.util.api.SiteMembershipAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * Class to include: Tests for SiteMembershipAPI implemented in alfresco-remote-api.
 *
 * @author Abhijeet Bharade
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "AlfrescoOne" })
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
    public void AONE_14323() throws Exception
    {

        SiteMembershipRequest siteMembershipRequest = createSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, modSiteName, MESSAGE);
        assertNotNull(siteMembershipRequest);
        assertEquals(ShareUser.getSiteShortname(modSiteName), siteMembershipRequest.getId().toLowerCase());
    }

    @Test(enabled = true)
    public void AONE_14349() throws Exception
    {
        SiteMembershipRequest siteMembershipRequest = createSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, siteName2, "");
        assertNotNull(siteMembershipRequest);
        assertEquals(siteMembershipRequest.getId().toLowerCase(), ShareUser.getSiteShortname(siteName2));
    }

    @Test(enabled = true, dependsOnMethods = "AONE_14314")
    public void AONE_14315() throws Exception
    {

        SiteMembershipRequest siteMembershipRequest = createSiteMembershipRequest(requestForUser, DOMAIN, "-me-", siteName, "");
        assertNotNull(siteMembershipRequest);
        assertEquals(ShareUser.getSiteShortname(siteName), siteMembershipRequest.getId().toLowerCase());

        siteMembershipRequest = createSiteMembershipRequest(requestForUser, DOMAIN, "-me-", getSiteShortname(modSiteName2), MESSAGE);
        assertNotNull(siteMembershipRequest);
        assertEquals(ShareUser.getSiteShortname(modSiteName2), siteMembershipRequest.getId().toLowerCase());
        assertEquals(MESSAGE.toLowerCase(), siteMembershipRequest.getMessage().toLowerCase());

        ShareUser.login(drone, mainUser);

        AbstractWorkflow.openMyTaskPage(drone);

        AbstractWorkflow.checkIfTaskIsPresent(drone, "Request to join " + modSiteName2 + " site", true);
    }

    @Test(enabled = true)
    public void AONE_14314() throws Exception
    {
        ListResponse<SiteMembershipRequest> smrListResponse = getSiteMembershipRequest(mainUser, DOMAIN, mainUser, null);
        assertNotNull(smrListResponse);
        assertEquals(smrListResponse.getList().size(), 0);
    }

    @Test(enabled = true)
    public void AONE_14344() throws Exception
    {
        ListResponse<SiteMembershipRequest> smrListResponse = getSiteMembershipRequest(mainUser, DOMAIN, "-me-", null);
        assertNotNull(smrListResponse);
        assertEquals(smrListResponse.getList().size(), 0);
    }

    @Test(enabled = true, dependsOnMethods = "AONE_14315")
    public void AONE_14316() throws Exception
    {

        SiteMembershipRequest siteMembershipRequest = createSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, siteName, null);
        assertNotNull(siteMembershipRequest);
        assertEquals(ShareUser.getSiteShortname(siteName), siteMembershipRequest.getId().toLowerCase());

        try
        {
            updateSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, siteName, MESSAGE);
            fail("AONE_14316: SMR should not exist as site is public.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    // updateSiteMembershipRequestNoChangeMessage
    @Test(enabled = true)
    public void AONE_14328() throws Exception
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
    public void AONE_14329() throws Exception
    {

        SiteMembershipRequest siteMembershipRequest = createSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, modSiteName3, "");
        assertNotNull(siteMembershipRequest);
        assertEquals(ShareUser.getSiteShortname(modSiteName3), siteMembershipRequest.getId().toLowerCase());

        try
        {
            updateSiteMembershipRequest(requestForUser, DOMAIN, requestForUser1, getSiteShortname(modSiteName3), MESSAGE);
            fail("AONE_14329: username error - " + requestForUser1);
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }

        try
        {
            updateSiteMembershipRequest(requestForUser, DOMAIN, requestForUser + "aa", getSiteShortname(modSiteName3), MESSAGE);
            fail("AONE_14329: username error - " + requestForUser + "aa");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }

    }

    @Test(enabled = true, dependsOnMethods = "AONE_14316")
    public void AONE_14317() throws Exception
    {

        SiteMembershipRequest siteMembershipRequest = createSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, siteName2, MESSAGE);
        assertNotNull(siteMembershipRequest);
        assertEquals(ShareUser.getSiteShortname(siteName2), siteMembershipRequest.getId().toLowerCase());

        try
        {
            cancelSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, getSiteShortname(siteName2));
            fail("AONE_14317: cannot cancel SMR for a public site");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    // getInvalidSiteMember
    @Test
    public void AONE_14319() throws Exception
    {
        try
        {
            getSiteMembershipRequest(mainUser, DOMAIN, requestForUser, null);
            fail("Get SMR for user other than auth user should fail with 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    @Test
    public void AONE_14324() throws Exception
    {
        try
        {
            createSiteMembershipRequest(requestForUser, DOMAIN, requestForUser + "ggg", modSiteName, MESSAGE);
            fail("AONE_14324: Cannot create SMR for invalid user - " + requestForUser + "ggg");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    @Test
    public void AONE_14337() throws Exception
    {
        try
        {
            cancelSiteMembershipRequest(requestForUser, DOMAIN, requestForUser + "fff", getSiteShortname(modSiteName));
            fail("Delete SMR when personid is different than auth user should fail with 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    // deleteSMRTwice
    @Test
    public void AONE_14341() throws Exception
    {

        SiteMembershipRequest siteMembershipRequest = createSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, modSiteName3, MESSAGE);
        assertNotNull(siteMembershipRequest);
        assertEquals(modSiteName3.toLowerCase(), siteMembershipRequest.getId().toLowerCase());

        HttpResponse response = cancelSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, getSiteShortname(modSiteName3));
        assertNotNull(response);
        assertEquals(response.getStatusCode(), 204);

        try
        {
            cancelSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, getSiteShortname(modSiteName3));
            fail("Delete deleted SMR: should return 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    // getPagingSiteMembership
    @Test
    public void AONE_14322() throws Exception
    {
        ListResponse<SiteMembershipRequest> smrResponse = getSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, null);
        assertEquals(smrResponse.getPaging().getSkipCount(), new Integer(0));
        assertEquals(smrResponse.getPaging().getMaxItems(), new Integer(100));

        Map<String, String> param = new HashMap<String, String>();
        param.put("skipCount", "2");
        smrResponse = getSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, param);
        assertEquals(smrResponse.getPaging().getSkipCount(), new Integer(2));
        assertEquals(smrResponse.getPaging().getMaxItems(), new Integer(100));

        param = new HashMap<String, String>();
        param.put("maxItems", "3");
        smrResponse = getSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, param);
        assertEquals(smrResponse.getPaging().getSkipCount(), new Integer(0));
        assertEquals(smrResponse.getPaging().getMaxItems(), new Integer(3));

        param = new HashMap<String, String>();
        param.put("skipCount", "2");
        param.put("maxItems", "10");

        smrResponse = getSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, param);
        assertEquals(smrResponse.getPaging().getSkipCount(), new Integer(2));
        assertEquals(smrResponse.getPaging().getMaxItems(), new Integer(10));
    }

    // getInvalidSiteMember
    @Test
    public void AONE_14353() throws Exception
    {
        try
        {
            getSiteMembershipRequest(requestForUser, DOMAIN, requestForUser + "sdf", null);
            fail("AONE_14353: Cannot get existing SMR for invalid user - " + requestForUser + "sdf");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    // getInvalidSiteMember
    @Test
    public void AONE_14350() throws Exception
    {
        SiteMembershipRequest siteMembershipRequest = createSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, probeSite, "");
        assertNotNull(siteMembershipRequest);
        assertEquals(ShareUser.getSiteShortname(probeSite), siteMembershipRequest.getId().toLowerCase());

        ShareUser.login(drone, mainUser);
        SiteUtil.changeSiteVisibility(drone, probeSite, true, false);

        try
        {
            createSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, probeSite, "");
            fail("AONE_14353: Cannot create SMR for private site " + probeSite + "Error 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    @Test(dependsOnMethods = "AONE_14317", alwaysRun = true)
    public void AONE_14320() throws ParseException
    {
        Map<String, String> param = new HashMap<String, String>();
        try
        {
            param.put("maxItems", "a");
            getSiteMembershipRequest(mainUser, DOMAIN, requestForUser, param);
            fail("Get SMR for user with bad maxItems should fail with 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }
        try
        {
            param.clear();
            param.put("skipCount", "s");
            getSiteMembershipRequest(mainUser, DOMAIN, requestForUser, param);
            fail("Get SMR for user with bad skipCount should fail with 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }
        try
        {
            param.put("maxItems", "a");
            getSiteMembershipRequest(mainUser, DOMAIN, requestForUser, param);
            fail("Get SMR for user with bad skipCount and maxItems should fail with 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }
        try
        {
            param.clear();
            param.put("maxItems", "-2");
            getSiteMembershipRequest(mainUser, DOMAIN, requestForUser, param);
            fail("Get SMR for user with negative maxItems should fail with 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }
        try
        {
            param.clear();
            param.put("skipCount", "-2");
            getSiteMembershipRequest(mainUser, DOMAIN, requestForUser, param);
            fail("Get SMR for user with negative skipCount should fail with 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }
        try
        {
            param.put("maxItems", "-2");
            getSiteMembershipRequest(mainUser, DOMAIN, requestForUser, param);
            fail("Get SMR for user with negative skipCount and maxItems should fail with 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }
    }

    @Test(dependsOnMethods = "AONE_14320", alwaysRun = true)
    public void AONE_14321() throws ParseException, PublicApiException
    {
        Map<String, String> param = new HashMap<String, String>();
        try
        {
            param.put("filter", "invalid");
            getSiteMembershipRequest(mainUser, DOMAIN, mainUser, param);
            fail("Get SMR for user with bad filter should fail with 400. Please, see: ACE-1329");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }
        try
        {
            param.clear();
            param.put("filter", "id");
            getSiteMembershipRequest(mainUser, DOMAIN, mainUser, param);
        }
        catch (PublicApiException e)
        {
            fail("Get SMR for user with filter 'id' should return 200");
        }
        try
        {
            param.clear();
            param.put("filter", "site");
            getSiteMembershipRequest(mainUser, DOMAIN, mainUser, param);
        }
        catch (PublicApiException e)
        {
            fail("Get SMR for user with filter 'site' should return 200");
        }
        try
        {
            param.clear();
            param.put("filter", "message");
            getSiteMembershipRequest(mainUser, DOMAIN, mainUser, param);
        }
        catch (PublicApiException e)
        {
            fail("Get SMR for user with filter 'message' should return 200");
        }
        try
        {
            param.clear();
            param.put("filter", "createdAt");
            getSiteMembershipRequest(mainUser, DOMAIN, mainUser, param);
        }
        catch (PublicApiException e)
        {
            fail("Get SMR for user with filter 'createdAt' should return 200");
        }
        try
        {
            param.clear();
            param.put("filter", "modifiedAt");
            getSiteMembershipRequest(mainUser, DOMAIN, mainUser, param);
        }
        catch (PublicApiException e)
        {
            fail("Get SMR for user with filter 'modifiedAt' should return 200");
        }
    }

    @Test(dependsOnMethods = "AONE_14321", alwaysRun = true)
    public void AONE_14354() throws ParseException
    {
        try
        {
            getSiteMembershipRequest(mainUser, DOMAIN, "null", null);
            fail("Get SMR for user 'null' should fail with 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    @Test(dependsOnMethods = "AONE_14354", alwaysRun = true)
    public void AONE_14318() throws ParseException, PublicApiException
    {
        try
        {
            cancelSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, modSiteName2);
            try
            {
                createSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, modSiteName2, null);
            }
            catch (PublicApiException e)
            {
                logger.error("Cant return user to site. Some wrong!");
            }
        }
        catch (PublicApiException e)
        {
            fail("Http response code must be 204, but actual is " + e.getHttpResponse());
        }
    }

    @Test(dependsOnMethods = "AONE_14318", alwaysRun = true)
    public void AONE_14352() throws ParseException
    {
        Map<String, String> param = new HashMap<String, String>();
        try
        {
            param.put("sortby", "tm.order=ascending");
            param.put("filter", "id");
            getSiteMembershipRequest(mainUser, DOMAIN, mainUser, param);
        }
        catch (PublicApiException e)
        {
            fail("Http response code must be 200, but actual is " + e.getHttpResponse().getStatusCode());
        }
    }

    @Test(dependsOnMethods = "AONE_14352", alwaysRun = true)
    public void AONE_14351() throws Exception
    {
        try
        {
            createSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, modSiteName3, "I need this access for national security reasons!");
        }
        catch (PublicApiException e)
        {
            fail("Http response code must be 201, but actual is " + e.getHttpResponse().getStatusCode());
        }
        DashBoardPage dashBoardPage = ShareUser.login(drone, mainUser).render();
        MyTasksPage myTasksPage = dashBoardPage.getNav().selectMyTasks();
        EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage("Request to join " + modSiteName3 + " site");
        editTaskPage.selectRejectButton();
        try
        {
            createSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, modSiteName3, "I need this access for national security reasons!");
        }
        catch (PublicApiException e)
        {
            fail("Http response code must be 201, but actual is " + e.getHttpResponse().getStatusCode());
        }
    }

    @Test(dependsOnMethods = "AONE_14351", alwaysRun = true)
    public void AONE_14326() throws Exception
    {
        try
        {
            createSiteMembershipRequest(requestForUser1, DOMAIN, "-me-", "null", "");
            fail("Create SMR for siteId 'null' should fail with 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, "Create SMR for siteId 'null' should fail with 404");
        }
        try
        {
            createSiteMembershipRequest(requestForUser1, DOMAIN, "-me-", RandomUtil.getRandomString(7), "");
            fail("Create SMR for siteId 'null' should fail with 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, "Create SMR for siteId 'invalid site' should fail with 404");
        }
        try
        {
            createSiteMembershipRequest(requestForUser1, DOMAIN, "-me-", privateSiteName, "");
            fail("Create SMR for siteId 'null' should fail with 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, "Create SMR for siteId 'private site' should fail with 404");
        }
    }

    @Test(dependsOnMethods = "AONE_14326", alwaysRun = true)
    public void AONE_14327() throws Exception
    {
        try
        {
            createSiteMembershipRequest(mainUser, DOMAIN, mainUser, modSiteName, "");
            fail("Create SMR for personId 'already joined, member' should fail with 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400, "Create SMR for siteId 'invalid site' should fail with 400");
        }
        try
        {
            createSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, modSiteName2, "");
            fail("Create SMR for personId 'already requested, request pending' should fail with 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400, "Create SMR for siteId 'invalid site' should fail with 400");
        }
    }

    @Test(dependsOnMethods = "AONE_14327", alwaysRun = true)
    public void AONE_14330() throws ParseException, PublicApiException
    {
        try
        {
            SiteMembershipRequest siteMR = updateSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, modSiteName2, "I need this access for national security reasons!");
            assertNotNull(siteMR.getModifiedAt(), "Update SMR should change 'modifiedAt'.");
            assertEquals(siteMR.getMessage(), "I need this access for national security reasons!", "Message don't changed.");
        }
        catch (PublicApiException e)
        {
            fail("Update SMR  should be OK with 200, actual is " + e.getHttpResponse().getStatusCode());
        }
        cancelSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, modSiteName2);
        createSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, modSiteName2, null);
        try
        {
            SiteMembershipRequest siteMR = updateSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, modSiteName2, "I need this access for national security reasons!");
            assertNotNull(siteMR.getModifiedAt(), "Update SMR should change 'modifiedAt'.");
            assertEquals(siteMR.getMessage(), "I need this access for national security reasons!", "Message don't changed.");
        }
        catch (PublicApiException e)
        {
            fail("Update SMR  should be OK with 200, actual is " + e.getHttpResponse().getStatusCode());
        }
    }

    @Test(dependsOnMethods = "AONE_14330", alwaysRun = true)
    public void AONE_14331() throws ParseException, PublicApiException
    {
        Date modifiedAtBefore = null;
        try
        {
            SiteMembershipRequest siteMR = updateSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, modSiteName2, "I need this access for national security reasons!");
            assertNotNull(siteMR.getModifiedAt(), "Update SMR should change 'modifiedAt'.");
            assertEquals(siteMR.getMessage(), "I need this access for national security reasons!", "Message don't changed.");
            modifiedAtBefore = siteMR.getModifiedAt();
        }
        catch (PublicApiException e)
        {
            fail("Update SMR  should be OK with 200, actual is " + e.getHttpResponse().getStatusCode());
        }
        try
        {
            SiteMembershipRequest siteMR = updateSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, modSiteName2, "I need this access !!!!!!!!!!!!!!! reasons!");
            assertNotNull(siteMR.getModifiedAt(), "Update SMR should change 'modifiedAt'.");
            assertTrue(modifiedAtBefore.before(siteMR.getModifiedAt()), "Update SMR should change 'modifiedAt'.");
            assertEquals(siteMR.getMessage(), "I need this access !!!!!!!!!!!!!!! reasons!");
        }
        catch (PublicApiException e)
        {
            fail("Update SMR  should be OK with 200, actual is " + e.getHttpResponse().getStatusCode());
        }
    }

    @Test(dependsOnMethods = "AONE_14331", alwaysRun = true)
    public void AONE_14332() throws ParseException, PublicApiException
    {
        try
        {
            SiteMembershipRequest siteMR = updateSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, modSiteName2, "I need this access for national security reasons!");
            assertNotNull(siteMR.getModifiedAt(), "Update SMR should change 'modifiedAt'.");
            assertEquals(siteMR.getMessage(), "I need this access for national security reasons!", "Message don't changed.");
        }
        catch (PublicApiException e)
        {
            fail("Update SMR  should be OK with 200, actual is " + e.getHttpResponse().getStatusCode());
        }
        try
        {
            updateSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, "null", "I need this access for secret reasons!");
            fail("Update SMR for siteId 'null' should fail with 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, "Update SMR for siteId 'null' should fail");
        }
    }

    @Test(dependsOnMethods = "AONE_14332", alwaysRun = true)
    public void AONE_14333() throws ParseException, PublicApiException
    {
        try
        {
            updateSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, RandomUtil.getRandomString(5), "I need this access for secret reasons!");
            fail("Update SMR for siteId 'invalid site' should fail with 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, "Update SMR for siteId 'invalid site' should fail");
        }
    }

    @Test(dependsOnMethods = "AONE_14333", alwaysRun = true)
    public void AONE_14334() throws Exception
    {
        try
        {
            updateSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, siteName, "I need this access for secret reasons!");
            fail("Update SMR for siteId 'no site-membership request exists' should fail with 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, "Update SMR for siteId 'no site-membership request exists' should fail");
        }
    }

    @Test(dependsOnMethods = "AONE_14334", alwaysRun = true)
    public void AONE_14335() throws Exception
    {
        DashBoardPage dashBoardPage = ShareUser.login(drone, mainUser).render();
        MyTasksPage myTasksPage = dashBoardPage.getNav().selectMyTasks();
        EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage("Request to join " + modSiteName2 + " site");
        editTaskPage.selectRejectButton();
        try
        {
            updateSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, modSiteName2, "I need this access for secret reasons!");
            fail("Update rejected SMR should fail with 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, "Update rejected SMR should fail");
        }
    }

    @Test(dependsOnMethods = "AONE_14335", alwaysRun = true)
    public void AONE_14336() throws Exception
    {
        DashBoardPage dashBoardPage = ShareUser.login(drone, mainUser).render();
        MyTasksPage myTasksPage = dashBoardPage.getNav().selectMyTasks();
        EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage("Request to join " + modSiteName + " site");
        editTaskPage.selectApproveButton();
        try
        {
            updateSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, modSiteName, "I need this access for secret reasons!");
            fail("Update rejected SMR should fail with 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, "Update rejected SMR should fail");
        }
    }

    @Test(dependsOnMethods = "AONE_14336", alwaysRun = true)
    public void AONE_14338()
    {
        try
        {
            cancelSiteMembershipRequest(mainUser, DOMAIN, RandomUtil.getRandomString(5), modSiteName);
            fail("Delete SMR with invalid personId should fail with 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, "Delete SMR with invalid personId should fail with 404");
        }
    }

    @Test(dependsOnMethods = "AONE_14338", alwaysRun = true)
    public void AONE_14339()
    {
        try
        {
            cancelSiteMembershipRequest(mainUser, DOMAIN, mainUser, "blank");
            fail("Delete SMR with siteId 'blank' should fail with 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, "Delete SMR with siteId 'blank' should fail with 404");
        }
        try
        {
            cancelSiteMembershipRequest(mainUser, DOMAIN, mainUser, "null");
            fail("Delete SMR with siteId 'null' should fail with 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, "Delete SMR with siteId 'null' should fail with 404");
        }
    }

    @Test(dependsOnMethods = "AONE_14339", alwaysRun = true)
    public void AONE_14340()
    {
        try
        {
            cancelSiteMembershipRequest(mainUser, DOMAIN, mainUser, RandomUtil.getRandomString(5));
            fail("Delete SMR with siteId 'invalid site' should fail with 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, "Delete SMR with siteId 'invalid site' should fail with 404");
        }
    }

    @Test(dependsOnMethods = "AONE_14340", alwaysRun = true)
    public void AONE_14342() throws Exception
    {
        DashBoardPage dashBoardPage = ShareUser.login(drone, mainUser).render();
        MyTasksPage myTasksPage = dashBoardPage.getNav().selectMyTasks();
        EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage("Request to join " + probeSite + " site");
        editTaskPage.selectRejectButton();
        try
        {
            cancelSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, probeSite);
            fail("Delete rejected SMR should fail with 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, "Delete rejected SMR should fail with 404");
        }
    }

    @Test(dependsOnMethods = "AONE_14342", alwaysRun = true)
    public void AONE_14343() throws Exception
    {
        DashBoardPage dashBoardPage = ShareUser.login(drone, mainUser).render();
        MyTasksPage myTasksPage = dashBoardPage.getNav().selectMyTasks();
        EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage("Request to join " + modSiteName + " site");
        editTaskPage.selectApproveButton();
        try
        {
            cancelSiteMembershipRequest(requestForUser, DOMAIN, requestForUser, modSiteName);
            fail("Delete rejected SMR should fail with 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, "Delete rejected SMR should fail with 404");
        }
    }

    @Test(dependsOnMethods = "AONE_14343", alwaysRun = true)
    public void AONE_14345() throws Exception
    {
        createSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, modSiteName2, "");
        try
        {
            ListResponse<SiteMembershipRequest> smRs = getSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, null);
            assertEquals((int) smRs.getPaging().getCount(), 2, "Wrong SMR count returned.");
            List<SiteMembershipRequest> smrList = smRs.getList();
            assertEquals(smrList.get(0).getId(), modSiteName2.toLowerCase(), "Wrong element returned.");
            assertEquals(smrList.get(1).getId(), modSiteName3.toLowerCase(), "Wrong element returned.");
        }
        catch (PublicApiException e)
        {
            fail("Get SMR should be with status 200.");
        }
    }

    @Test(dependsOnMethods = "AONE_14345", alwaysRun = true)
    public void AONE_14346() throws Exception
    {
        cancelSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, modSiteName2);
        cancelSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, modSiteName3);
        try
        {
            ListResponse<SiteMembershipRequest> smRs = getSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, null);
            assertEquals((int) smRs.getPaging().getCount(), 0, "Wrong SMR count returned.");
        }
        catch (PublicApiException e)
        {
            fail("Get SMR should be with status 200.");
        }
    }

    @Test(dependsOnMethods = "AONE_14346", alwaysRun = true)
    public void AONE_14347() throws Exception
    {
        createSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, modSiteName2, "");
        DashBoardPage dashBoardPage = ShareUser.login(drone, mainUser).render();
        MyTasksPage myTasksPage = dashBoardPage.getNav().selectMyTasks();
        EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage("Request to join " + modSiteName2 + " site");
        editTaskPage.selectApproveButton();
        try
        {
            ListResponse<SiteMembershipRequest> smRs = getSiteMembershipRequest(requestForUser1, DOMAIN, requestForUser1, null);
            assertEquals((int) smRs.getPaging().getCount(), 0, "Wrong SMR count returned.");
        }
        catch (PublicApiException e)
        {
            fail("Get SMR should be with status 200.");
        }
    }

    @Test(dependsOnMethods = "AONE_14347", alwaysRun = true)
    public void AONE_14348() throws Exception
    {
        try
        {
            ListResponse<SiteMembershipRequest> smRs = getSiteMembershipRequest(requestForUser1, DOMAIN, "-me-", null);
            assertEquals((int) smRs.getPaging().getCount(), 0, "Wrong SMR count returned.");
        }
        catch (PublicApiException e)
        {
            fail("Get SMR should be with status 200.");
        }
    }

}
