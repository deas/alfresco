package org.alfresco.officeapplication;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.alfresco.office.application.Application;
import org.alfresco.office.application.LdtpInitialisation;
import org.alfresco.office.application.MicrosoftOffice2013;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.cobra.ldtp.Ldtp;
import com.cobra.ldtp.LdtpExecutionError;

public class Word2013Test 
{
    public String location;
    MicrosoftOffice2013 word = new MicrosoftOffice2013(Application.WORD,"2013");

    @BeforeSuite
    public void initialSetup()
    {
        try
        {
        //   startLDTP();
            LdtpInitialisation abstractUtil = new LdtpInitialisation();
            word.setAbstractUtil(abstractUtil);
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
     * 1) Open word application
     * 2) Add a text
     * 3) Click on Save as button
     * @throws IOException 
     * 
     * @throws InterruptedException
     */
    @Test
    public void testwordCreation() 
    {
       try
       {
        Ldtp ldtp = word.openOfficeApplication();
        word.editOffice(ldtp, "hello world");
        word.saveOffice(ldtp, location + "\\" + "hello world");
        word.exitOfficeApplication(ldtp);
        File propFile = new File(location, "hello world.docx");
        Assert.assertTrue(propFile.exists());
        ldtp = null;
       }
       catch (LdtpExecutionError e)
       {
           Assert.fail("The test case failed " + this.getClass(), e);
       }
       catch(IOException ie)
       {
           Assert.fail("Open word application failed");
       }
    }

    @AfterSuite
    public void tearDown() throws SecurityException
    {
        File propFile = new File(location, "hello world.docx");
        propFile.setWritable(true);
        propFile.delete();
        Assert.assertFalse(propFile.exists());
    }     
}