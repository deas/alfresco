package org.alfresco.officeapplication;

import java.io.IOException;
import java.util.Properties;

import org.alfresco.office.application.Application;
import org.alfresco.office.application.LdtpInitialisation;
import org.alfresco.office.application.MicrosoftOffice2013;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.cobra.ldtp.Ldtp;
import com.cobra.ldtp.LdtpExecutionError;

public class Outlook2013Test
{
    public String location;
    MicrosoftOffice2013 outlook = new MicrosoftOffice2013(Application.OUTLOOK, "2013");


    @BeforeSuite
    public void initialSetup() throws LdtpExecutionError, IOException
    {
            LdtpInitialisation abstractUtil = new LdtpInitialisation();
            outlook.setAbstractUtil(abstractUtil);
          //  excel.unCheckStartUp();
            Properties confOfficeProperty = new Properties();
            confOfficeProperty.load(this.getClass().getClassLoader().getResourceAsStream("test.properties"));
            location = confOfficeProperty.getProperty("location");
    }
    
  

}
