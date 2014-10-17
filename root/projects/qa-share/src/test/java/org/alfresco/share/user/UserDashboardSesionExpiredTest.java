package org.alfresco.share.user;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CreateUserAPI;
import org.junit.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class UserDashboardSesionExpiredTest extends AbstractUtils
{
    /*
     * Preconditions
     * 1. Login as any user;
     * 2. Wait untill session will expire (~30 mins).
     */
    private String testName;
    private String testUser;
    protected SharePage sharePage;
    
    private String cmdChangeNewDateUnix = "date +%d-%m-%Y -s ";
    private String cmdChangeDefaultDateUnix = "ntpdate ntp.ubuntu.com";

    private String cmdChangeCurrentDate1 = "cmd /C w32tm /config /update";
    private String cmdChangeCurrentDate2 = "cmd /C w32tm /resync";
    private String cmdChangeNewDate = "cmd /C time ";

    private String cmdDefaultDateWin1;
    private String cmdDefaultDateWin2;
    private String cmdDefaultDateUnix;
    private String cmdNewDate;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName();
        testUser = getUserNameFreeDomain(testName);

        System.out.println("UserName is: " + testUser + "and test name is: " + testName);
        
        if (System.getProperty("os.name").contains("Windows"))
        {
            cmdDefaultDateWin1 = cmdChangeCurrentDate1;
            cmdDefaultDateWin2 = cmdChangeCurrentDate2;
            cmdNewDate = cmdChangeNewDate;
        }
        else
        {
            cmdDefaultDateUnix = cmdChangeDefaultDateUnix;
            cmdNewDate = cmdChangeNewDateUnix;
        }
    }

    @Test(groups = { "DataPrepSesionExpired" })
    public void dataPrep_AONE_sesion_expired() throws Exception
    {
        // Create normal User
        String[] testUser2 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);
    }
    
    
    @AfterMethod(alwaysRun = true)
    public void teardownMethod() throws Exception
    {
        if (System.getProperty("os.name").contains("Windows"))
        {
            Runtime.getRuntime().exec(cmdDefaultDateWin1);
            Runtime.getRuntime().exec(cmdDefaultDateWin2);
        }
        else
        {
            Runtime.getRuntime().exec(cmdDefaultDateUnix);
        }
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_12090() throws Exception
    {
     // Login with user
        ShareUser.login(drone, testUser);
        
        // Step 1: Click Home link on the header;
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        SimpleDateFormat sdf2 = new SimpleDateFormat("mm");
        String currentHour = sdf.format(cal.getTime());
        String currentMinute = sdf2.format(cal.getTime());
        int newHour = Integer.parseInt(currentHour) + 3;
        //int oldHour = Integer.parseInt(currentHour) - 3;
        
        System.out.println("CurrentHour is: " + currentHour + ":" + currentMinute);

        // change system time to new time
        //Runtime.getRuntime().exec("cmd /C time " + newHour + ":" + currentMinute);
        Runtime.getRuntime().exec(cmdNewDate + newHour + ":" + currentMinute);
        System.out.println("NewHour is: " + newHour);

        // click home button
        sharePage = drone.getCurrentPage().render();
        sharePage.getNav().selectMyDashBoard();

        SharePage page;
        page = drone.getCurrentPage().render();
        Assert.assertTrue(page.isBrowserTitle("login"));

        // Step 2: Enter correct Email and Password and click Login button.
        ShareUser.login(drone, testUser);
        String title = page.getTitle();
        Assert.assertTrue(title.contains("User Dashboard"));

    }
    
}
