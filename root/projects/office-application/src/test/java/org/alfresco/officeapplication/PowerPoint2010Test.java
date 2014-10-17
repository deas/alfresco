package org.alfresco.officeapplication;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.alfresco.office.application.Application;
import org.alfresco.office.application.LdtpInitialisation;
import org.alfresco.office.application.MicorsoftOffice2010;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.cobra.ldtp.Ldtp;
import com.cobra.ldtp.LdtpExecutionError;

public class PowerPoint2010Test 
{
    public String location;
    MicorsoftOffice2010 powerpoint = new MicorsoftOffice2010(Application.POWERPOINT,"2010");

    @BeforeSuite
    public void initialSetup()
    {
        try
        {
        //   startLDTP();
            LdtpInitialisation abstractUtil = new LdtpInitialisation();
            powerpoint.setAbstractUtil(abstractUtil);
            Properties confOfficeProperty = new Properties();
            confOfficeProperty.load(this.getClass().getClassLoader().getResourceAsStream("test.properties"));
            location = confOfficeProperty.getProperty("location");
        }
        catch (Exception e)
        {

        }
    }


    /**
     * Steps
     * 1) Open powerpoint application
     * 2) Add a text
     * 3) Click on Save as button
     * @throws IOException 
     * 
     * @throws InterruptedException
     */
    @Test
    public void testpowerpointCreation() 
    {
       try
       {
        Ldtp ldtp = powerpoint.openOfficeApplication();
        powerpoint.editOffice(ldtp, "hello world");
        powerpoint.saveAsOffice(ldtp, location + "\\" + "hello world");
        powerpoint.closeOfficeApplication("hello world");
        File propFile = new File(location, "hello world.pptx");
        Assert.assertTrue(propFile.exists());
        ldtp = null;
       }
       catch (LdtpExecutionError e)
       {
           Assert.fail("The test case failed " + this.getClass(), e);
       }
       catch(IOException ie)
       {
           Assert.fail("Open powerpoint application failed");
       }
    }

    @AfterSuite
    public void tearDown()
    {
        File propFile = new File(location, "hello world.pptx");
        propFile.setWritable(true);
        propFile.delete();
        Assert.assertFalse(propFile.exists());
    }
}