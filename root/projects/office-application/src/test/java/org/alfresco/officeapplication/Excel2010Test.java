package org.alfresco.officeapplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.alfresco.office.application.Application;
import org.alfresco.office.application.LdtpInitialisation;
import org.alfresco.office.application.MicorsoftOffice2010;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.cobra.ldtp.Ldtp;
import com.cobra.ldtp.LdtpExecutionError;

public class Excel2010Test
{
    public String location;
    MicorsoftOffice2010 excel = new MicorsoftOffice2010(Application.EXCEL, "2010");
    public String fileName;

    @BeforeSuite
    public void initialSetup()
    {
        try
        {
            // startLDTP();
            LdtpInitialisation abstractUtil = new LdtpInitialisation();
            excel.setAbstractUtil(abstractUtil);
            Properties confOfficeProperty = new Properties();
            confOfficeProperty.load(this.getClass().getClassLoader().getResourceAsStream("test.properties"));
            location = confOfficeProperty.getProperty("location");
            fileName = "unittest";
        }
        catch (Exception e)
        {

        }
    }

    /**
     * Steps
     * 1) Open excel application
     * 2) Add a text
     * 3) Click on Save as button
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testExcelCreation()
    {
        try
        {
            Ldtp ldtp = excel.openOfficeApplication();
            excel.editOffice(ldtp, "hello world");
            excel.saveAsOffice(ldtp, location + "\\" + fileName);
            excel.closeOfficeApplication(fileName);
            ldtp.waitTime(2);
            File propFile = new File(location, fileName + ".xlsx");
            Assert.assertTrue(propFile.exists());
            ldtp = null;
        }
        catch (LdtpExecutionError e)
        {
            Assert.fail("The test case failed " + this.getClass(), e);
        }
        catch (IOException ie)
        {
            Assert.fail("Open excel application failed");
        }
    }

    @Test
    public void testClickOnObject()
    {

        try
        {
            Ldtp ldtp = excel.openOfficeApplication();

            excel.clickOnObject(ldtp, "File Tab");

            Assert.assertTrue(excel.isObjectDisplayed(ldtp, "Info"), "Verify Info is displayed");
            excel.clickOnObject(ldtp, "Close");
        }
        catch (LdtpExecutionError e)
        {
            Assert.fail("The test case failed " + this.getClass(), e);
        }
        catch (IOException ie)
        {
            Assert.fail("Open excel application failed");
        }

    }

  
    @Test
    public void testFindWindowName()
    {
        try
        {
            Ldtp ldtp = excel.openOfficeApplication();
            excel.editOffice(ldtp, "test for findWindowName");
            excel.saveAsOffice(ldtp, location + "\\" + fileName);

            String nameOfWindow = excel.findWindowName(fileName);
            
            Assert.assertTrue(nameOfWindow.contains(fileName) && nameOfWindow.contains(excel.applicationWindowName), "Verify the window name");
            
            excel.exitOfficeApplication(ldtp);
            ldtp = null;
        }
        catch (LdtpExecutionError e)
        {
            Assert.fail("The test case failed " + this.getClass(), e);
        }
        catch (IOException ie)
        {
            Assert.fail("Open excel application failed");
        }
    }
    
    @Test
    public void testGoToFile()
    {
        try
        {
            Ldtp ldtp = excel.openOfficeApplication();
            excel.goToFile(ldtp);
            boolean isDisplayed = excel.isObjectDisplayed(ldtp, "Info");
            Assert.assertTrue(isDisplayed, "Verify Info is displayed");
            
            excel.exitOfficeApplication(ldtp);
            ldtp = null;
        }
        catch (LdtpExecutionError e)
        {
            Assert.fail("The test case failed " + this.getClass(), e);
        }
        catch (IOException ie)
        {
            Assert.fail("Open excel application failed");
        }
    }
    
    
    
    @Test
    public void testOpenOfficeApplication()
    {
    
        try
        {
            Ldtp ldtp = excel.openOfficeApplication();

            String nameOfWindow = excel.findWindowName(excel.applicationWindowName);
            
            Assert.assertTrue(!nameOfWindow.isEmpty(), "Verify the window name is not empty");
            
            excel.exitOfficeApplication(ldtp);
            ldtp = null;
            
        }
        catch (LdtpExecutionError e)
        {
            Assert.fail("The test case failed " + this.getClass(), e);
        }
        catch (IOException ie)
        {
            Assert.fail("Open excel application failed");
        }
    }
    

    
    @Test
    public void testIsObjectDisplayed()
    {
        try
        {
            Ldtp ldtp = excel.openOfficeApplication();
            boolean isDisplayed = excel.isObjectDisplayed(ldtp, "pane8");
            Assert.assertTrue(isDisplayed, "Verify pane8 is displayed");
            
            
            excel.goToFile(ldtp);
            boolean isNotDisplayed = excel.isObjectDisplayed(ldtp, "pane8");
            Assert.assertFalse(isNotDisplayed, "Verify pane8 is not displayed");
            
            excel.exitOfficeApplication(ldtp);
            ldtp = null;
        }
        catch (LdtpExecutionError e)
        {
            Assert.fail("The test case failed " + this.getClass(), e);
        }
        catch (IOException ie)
        {
            Assert.fail("Open excel application failed");
        }
    }

    
    
    @Test
    public void testOperateOnConfirmSaveAs()
    {
        try
        {
            Ldtp ldtp = excel.openOfficeApplication();
            excel.saveAsOffice(ldtp, location + "\\" + fileName);
            
            ldtp = excel.setOnWindow(fileName);
            excel.saveAsOffice(ldtp, location + "\\" + fileName);
            excel.operateOnConfirmSaveAs(ldtp);
            
            String winName = excel.findWindowName("Confirm Save As");
            
            Assert.assertTrue(winName.isEmpty(), "Verify 'Confirm Save As' is not displayed");
            excel.exitOfficeApplication(ldtp);
            ldtp = null;
        }
        catch (LdtpExecutionError e)
        {
            Assert.fail("The test case failed " + this.getClass(), e);
        }
        catch (IOException ie)
        {
            Assert.fail("Open excel application failed");
        }
    }
    
    @Test
    public void testOpenOfficeFromFileMenu()
    {
        
        try
        {
            Ldtp ldtp = excel.openOfficeApplication();
            excel.saveAsOffice(ldtp, location + "\\" + fileName);
            excel.exitOfficeApplication(ldtp);
            
            ldtp = excel.openOfficeApplication();
            excel.openOfficeFromFileMenu(ldtp, location + "\\" + fileName);
            
            String nameOfWindow = excel.findWindowName(fileName);
            
            Assert.assertTrue(!nameOfWindow.isEmpty(), "Verify the window is opened");
            
            excel.exitOfficeApplication(ldtp);
            ldtp = null;
            
        }
        catch (LdtpExecutionError e)
        {
            Assert.fail("The test case failed " + this.getClass(), e);
        }
        catch (IOException ie)
        {
            Assert.fail("Open excel application failed");
        }
    }
    
    
    @Test
    public void testSaveOffice()
    {
        try
        {
            Ldtp ldtp = excel.openOfficeApplication();
            excel.editOffice(ldtp, "hello world");
            excel.saveOffice(ldtp, location + "\\" + fileName);
            
            excel.exitOfficeApplication(ldtp);
            ldtp.waitTime(2);
            
            File propFile = new File(location, fileName + ".xlsx");
            Assert.assertTrue(propFile.exists());
            ldtp = null;
        }
        catch (LdtpExecutionError e)
        {
            Assert.fail("The test case failed " + this.getClass(), e);
        }
        catch (IOException ie)
        {
            Assert.fail("Open excel application failed");
        }
    }
    
    
    @Test
    public void testExitOfficeApplication()
    {
        try
        {
            Ldtp ldtp = excel.openOfficeApplication();
            boolean isDisplayed = excel.isObjectDisplayed(ldtp, "pane8");
            Assert.assertTrue(isDisplayed, "Verify pane8 is displayed");
            
            excel.exitOfficeApplication(ldtp);
            String nameOfWindow = excel.findWindowName(excel.applicationWindowName);
            
            Assert.assertTrue(nameOfWindow.isEmpty(), "Verify the window was closed");
            
            ldtp = null;
        }
        catch (LdtpExecutionError e)
        {
            Assert.fail("The test case failed " + this.getClass(), e);
        }
        catch (IOException ie)
        {
            Assert.fail("Open excel application failed");
        }
    }
    
    
    @Test
    public void testWaitForWindow()
    {
        try
        {
            Ldtp ldtp = excel.openOfficeApplication();
            String currWin = excel.waitForWindow(excel.applicationWindowName);
            Assert.assertTrue(!currWin.isEmpty(), "Verify the window is displayed");
            
            excel.exitOfficeApplication(ldtp);
            
            ldtp = null;
        }
        catch (LdtpExecutionError e)
        {
            Assert.fail("The test case failed " + this.getClass(), e);
        }
        catch (IOException ie)
        {
            Assert.fail("Open excel application failed");
        }
    }
    
    @Test
    public void testSaveOffice1()
    {
        try
        {
            Ldtp ldtp = excel.openOfficeApplication();
            excel.saveOffice(ldtp);
            String currWin = excel.waitForWindow("Save As");
            Assert.assertTrue(!currWin.isEmpty(), "Verify the window 'Save As' is displayed");
            
            excel.exitOfficeApplication(ldtp);
            
            ldtp = null;
        }
        catch (LdtpExecutionError e)
        {
            Assert.fail("The test case failed " + this.getClass(), e);
        }
        catch (IOException ie)
        {
            Assert.fail("Open excel application failed");
        }
    }

 
    
 
    @AfterMethod
    public void tearDown() throws IOException
    {
        
        Runtime.getRuntime().exec("taskkill /F /IM EXCEL.EXE");
        
        try
        {
            TimeUnit.SECONDS.sleep(2);
        }
        catch (InterruptedException e)
        {
        }
        
        Path path = FileSystems.getDefault().getPath(location, fileName + ".xlsx");
        
        Files.deleteIfExists(path);
        
       }
}