package org.alfresco.office.application;

import java.io.IOException;

import com.cobra.ldtp.Ldtp;
import com.cobra.ldtp.LdtpExecutionError;

public interface OfficeApplications
{

    /**
     * Method to open Excel application
     * 
     * @throws LdtpExecutionError, IOException
     */
    public abstract Ldtp openOfficeApplication() throws LdtpExecutionError, IOException;

    /**
     * Method to add some lines to the file
     * 
     * @throws LdtpExecutionError
     */
    public abstract void editOffice(Ldtp ldtp, String data) throws LdtpExecutionError;

    /**
     * Method to Save the  file which is already saved to particular location
     * 
     * @throws LdtpExecutionError
     */
    public abstract void saveOffice(Ldtp ldtp) throws LdtpExecutionError;

    /**
     * Method to Save the excel file for the first time in a particular location
     * 
     * @throws LdtpExecutionError
     */
    public abstract void saveOffice(Ldtp ldtp, String location) throws LdtpExecutionError;


    /**
     * Method to SaveAs to a particular location
     * 
     * @throws LdtpExecutionError
     */
    public abstract void saveAsOffice(Ldtp ldtp, String location) throws LdtpExecutionError;

    /**
     *Method to Open a particular file from a location
     * 
     * @throws LdtpExecutionError
     */
    public abstract void openOfficeFromFileMenu(Ldtp ldtp, String location) throws LdtpExecutionError;

    /**
     * Method to close a particular file 
     * 
     * @throws LdtpExecutionError
     */
    public abstract void closeOfficeApplication(String fileName) throws LdtpExecutionError;

    /**
     * Method to find window name for excel
     * 
     * @throws LdtpExecutionError
     */
    public abstract String findWindowName(String fileName) throws LdtpExecutionError;

    
    /**
     * Method to click on any object
     * 
     * @throws LdtpExecutionError
     */
    public abstract void clickOnObject(Ldtp ldtp, String myObject);
    
    /**
     * Method to exit office application (sending ALT + f4)
     * 
     * @throws LdtpExecutionError
     */ 
    public abstract void exitOfficeApplication( Ldtp ldtp);
    
    
    /**
     * Method to navigate to File from office application
     * 
     * @throws LdtpExecutionError
     */ 
    public abstract void goToFile(Ldtp ldtp);
}