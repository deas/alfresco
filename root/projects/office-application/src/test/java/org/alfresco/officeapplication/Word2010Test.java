package org.alfresco.officeapplication;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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

public class Word2010Test
{
    public String location;
    MicorsoftOffice2010 word = new MicorsoftOffice2010(Application.WORD, "2010");

    @BeforeSuite
    public void initialSetup()
    {
        try
        {
            // startLDTP();
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
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testwordCreation()
    {
        try
        {
            Ldtp ldtp = word.openOfficeApplication();
            word.editOffice(ldtp, "hello world");
            word.saveAsOffice(ldtp, location + "\\" + "hello world");
            word.closeOfficeApplication("hello world");
            File propFile = new File(location, "hello world.docx");
            Assert.assertTrue(propFile.exists());
            ldtp = null;
        }
        catch (LdtpExecutionError e)
        {
            Assert.fail("The test case failed " + this.getClass(), e);
        }
        catch (IOException ie)
        {
            Assert.fail("Open word application failed");
        }
    }

    @AfterSuite
    public void tearDown()
    {
        File propFile = new File(location, "hello world.docx");
        propFile.setWritable(true);
        propFile.delete();
        Assert.assertFalse(propFile.exists());
    }

    @Test
    public void setOnWindow() throws LdtpExecutionError, IOException
    {

        Ldtp l1 = word.setOnWindow("Microsoft Word");

        String[] la = l1.getObjectList();
       
        for (int i = 0; i < la.length; i++)
        {

                System.out.println(la[i]);
                String aa1= l1.getObjectProperty(la[i], "label");
                //String[] aa = l1.getObjectInfo(la[i]);
                System.out.println(aa1);
                if(aa1.contains("iii"))
                    continue;

        }

    }
}