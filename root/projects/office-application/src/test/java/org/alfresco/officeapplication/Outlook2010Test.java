package org.alfresco.officeapplication;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.alfresco.office.application.Application;
import org.alfresco.office.application.LdtpInitialisation;
import org.alfresco.office.application.MicorsoftOffice2010;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.cobra.ldtp.Ldtp;
import com.cobra.ldtp.LdtpExecutionError;

public class Outlook2010Test
{
    MicorsoftOffice2010 outlook = new MicorsoftOffice2010(Application.OUTLOOK, "2010");
    LdtpInitialisation abstractUtil = new LdtpInitialisation();
    private String path;
    private String linkSite;

    @BeforeSuite
    public void initialSetup() throws LdtpExecutionError, IOException
    {
        outlook.setAbstractUtil(abstractUtil);
        
        Properties officeAppProperty = new Properties();
        officeAppProperty.load(this.getClass().getClassLoader().getResourceAsStream("office.properties"));
        path = officeAppProperty.getProperty("sharepoint.path");
        
        linkSite = "OutlookMeeting2";
    }
    
    @AfterMethod(alwaysRun = true)
    public void teardownMethod() throws Exception
    {
        Runtime.getRuntime().exec("taskkill /F /IM OUTLOOK.EXE");
    }
    
    @Test
    public void testCreateNewMeetingWorkspace() throws LdtpExecutionError, IOException
    {
        
        String location = "Test Room";
        
        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();

        // create new meeting workspace
        outlook.operateOnCreateNewMeetingWorkspace(l, path, linkSite, location, "admin", "admin", true, false);
        String currentWin = outlook.findWindowName(linkSite);
        Assert.assertTrue(currentWin.contains(linkSite));
    }
    
    @Test
    public void testNewMeetingWithoutSubject() throws LdtpExecutionError, IOException
    {
        
        String subject = "SiteTest";
        String location = "Test Room";
        
        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();

        // create new meeting workspace
        outlook.operateOnCreateNewMeetingWorkspace(l, path, subject, location, "admin", "admin", false, false);
        
        Ldtp event = outlook.setOnWindow("Microsoft Outlook");
        outlook.exitOfficeApplication(event);
        
        String currentWin = outlook.findWindowName(subject);
        Assert.assertFalse(currentWin.contains(subject));
        
        
        
    }
    
    @Test
    public void testNewMeetingAndRemove() throws LdtpExecutionError, IOException
    {
        
        String subject = "subject" + System.currentTimeMillis();
        String location = "Test Room";
        
        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();

        // create new meeting workspace
        outlook.operateOnCreateNewMeetingWorkspace(l, path, subject, location, "admin", "admin", true, true);
        
        Ldtp l1 = outlook.setOnWindow(subject);
        String objects[] = l1.getObjectList();

        String objs = Arrays.toString(objects);
        
        Assert.assertTrue(objs.contains("btnCreate"));
        Assert.assertFalse(objs.contains("btnRemove"));
       
    }
    
    @Test(groups = "alfresco-one")
    public void testLinkToExistingWorkspace() throws Exception
    {
        
        String subject = "subject" + System.currentTimeMillis();
        String location = "Test Room";

        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();

        // create new meeting workspace
        outlook.operateOnLinkToExistingWorkspace(l, path, linkSite, subject, location, "admin", "admin", true);
        
        String currentWin = outlook.findWindowName(subject);
        Assert.assertTrue(currentWin.contains(subject));
    }
    
    @Test(groups = "alfresco-one")
    public void testLinkToWorkspaceNoSubject() throws Exception
    {
        
        String subject = "subject";
        String location = "Test Room";

        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();

        // create new meeting workspace
        outlook.operateOnLinkToExistingWorkspace(l, path, linkSite, subject, location, "admin", "admin", false);
        
        Ldtp event = outlook.setOnWindow("Microsoft Outlook");
        outlook.exitOfficeApplication(event);
        
        String currentWin = outlook.findWindowName(subject);
        Assert.assertFalse(currentWin.contains(subject));
    }

}
