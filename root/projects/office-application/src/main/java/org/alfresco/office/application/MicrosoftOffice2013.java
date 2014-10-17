package org.alfresco.office.application;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.cobra.ldtp.Ldtp;
import com.cobra.ldtp.LdtpExecutionError;

public class MicrosoftOffice2013 extends MicorsoftOffice2010
{

    private static final Logger logger = Logger.getLogger(MicrosoftOffice2013.class);

    public MicrosoftOffice2013(Application application, String officeVersion)
    {
        super(application, officeVersion);
    }

    /**
     * Method to up check the start up window
     * 
     * @throws IOException
     * @throws LdtpExecutionError
     */
    public void unCheckStartUp() throws LdtpExecutionError, IOException
    {
        Ldtp ldtp;
        String excelApplicationPath = abstractUtil.getApplicationPath(officeVersion) + applicationExe;
        Runtime.getRuntime().exec(excelApplicationPath);
        ldtp = new Ldtp("Excel");
        ldtp.click(blankWindow);
        String window = findWindowName(applicationWindowName);
        Ldtp ldtp2 = new Ldtp(window);
        ldtp2.click(abstractUtil.getFileMenu());
        ldtp2.click(abstractUtil.getOptionsButton());
        ldtp2.activateWindow(abstractUtil.getOptionDialog());
        if (ldtp2.check(abstractUtil.getStartUpCheckBox()) == 1)
        {
            ldtp2.unCheck(abstractUtil.getStartUpCheckBox());
        }
        ldtp2.click(abstractUtil.getOkButton());
        closeOfficeApplication(window);
    }

    /*
     * Method to implement Save for the first time as well as for Save as of
     * office applications
     */
    @Override
    public void saveOffice(Ldtp ldtp, String location) throws LdtpExecutionError
    {

        ldtp.click(abstractUtil.getSaveButton());
        ldtp.waitTillGuiExist(fileMenuPage, waitInSeconds);
        ldtp.waitTillGuiExist("Browse", waitInSeconds);
        ldtp.doubleClick("Browse");
        
        ldtp.waitTillGuiExist(abstractUtil.getExplorerSaveAs(), waitInSeconds);
        ldtp.activateWindow(abstractUtil.getExplorerSaveAs());
        ldtp.enterString("edit", location);
        ldtp.click(abstractUtil.getSaveButton());
    }



    /**
     * Method to open office application in 2013
     */

    @Override
    public Ldtp openOfficeApplication() throws LdtpExecutionError, IOException
    {

        try
        {
            TimeUnit.SECONDS.sleep(5);
        }
        catch (InterruptedException e)
        {
        }

        String excelApplicationPath = abstractUtil.getApplicationPath(officeVersion) + applicationExe;
        Runtime.getRuntime().exec("\"" + excelApplicationPath + "\"");

        try
        {
            TimeUnit.SECONDS.sleep(3);
        }
        catch (InterruptedException e)
        {
        }
        
        Ldtp ldtp;
        String windowName = waitForWindow(applicationWindowName);
        System.out.println(windowName);
        ldtp = new Ldtp(windowName);
        ldtp.click(blankWindow);
        return new Ldtp(findWindowName(applicationWindowName));   

    }



  /**
   * Performs the operations in order to open a file from document library
   * 
   * @param ldtp1
   * @param path
   * @param siteName
   * @param fileName
   * @param userName
   * @param password
   */
    public void operateOnOpen(Ldtp ldtp1, String path, String siteName, String fileName, String userName, String password)
    {
        logger.info("Operate on 'Open'");

        String currentWin = waitForWindow("dlgOpen");
        if(currentWin.isEmpty())
            throw new LdtpExecutionError("Cannot find the Open window");

        ldtp1.activateWindow("dlgOpen");
        
        ldtp1.enterString("txtFilename", path);
        ldtp1.mouseLeftClick("uknOpen");

        operateOnSecurity(ldtp1, userName, password);

        String siteObject = "lbl" + siteName.replace(".", "").replace("_", "");
        waitForObject(ldtp1, siteObject.toLowerCase());
        ldtp1.doubleClick(siteObject.toLowerCase());
        
        waitForObject(ldtp1, "lbldocumentLibrary");
        ldtp1.doubleClick("lbldocumentLibrary");

        String fileObject = "lbl" + fileName.replace(".", "").replace("_", "");
        waitForObject(ldtp1, fileObject);
        ldtp1.doubleClick(fileObject);

    }

    /**
     * Save the document with SharePoint values
     * 
     * @param path
     *            SharePoint Path where to save the file
     */
    public void operateOnSaveAsWithSharepoint(Ldtp ldtp1, String path, String siteName, String fileName, String userName, String password)
    {
        
        operateOnSecurity(ldtp1, userName, password);
        
        saveAs(ldtp1,path);
        
        String siteObject = "lbl" + siteName.replace(".", "").replace("_", "");
        waitForObject(ldtp1, siteObject.toLowerCase());
        ldtp1.doubleClick(siteObject.toLowerCase());
        
        waitForObject(ldtp1, "lbldocumentLibrary");
        ldtp1.doubleClick("lbldocumentLibrary");

        waitForObject(ldtp1, "txtFilename");
        ldtp1.enterString("txtFilename", fileName);
        ldtp1.mouseLeftClick("btnSave");

        operateOnConfirmSaveAs(ldtp1);
        
     }
    
    
    /**
     * Operates on Save As dialog
     * 
     * @param ldtp1
     * @param path
     */
    public void saveAs(Ldtp ldtp1, String path)
    {
        logger.info("Operate on 'Save As'");

        String currentWin = waitForWindow("Save As");
        if(currentWin.isEmpty())
            throw new LdtpExecutionError("Cannot find the Save As window");

        ldtp1.activateWindow("Save As");
        ldtp1.enterString("txtFilename", path);
        ldtp1.mouseLeftClick("btnSave");
    }
    
    
    
    /**
     * Click on File -> Save As -> SharePoint -> Browse
     * 
     * @param l
     */
    public void navigateToSaveAsSharePointBrowse(Ldtp l)
    {
        goToFile(l);
        clickOnObject(l, "SaveAs");
        clickOnObject(l, "SharePoint");
        clickOnObject(l, "Browse");
    }
    
    /**
     * Click on File -> Open -> SharePoint -> Browse
     * 
     */
    public void navigateToOpenSharePointBrowse(Ldtp l)
    {
        goToFile(l);
        clickOnObject(l, "Open");
        clickOnObject(l, "SharePoint");
        clickOnObject(l, "Browse");
    
    }
    
    /**
     * Performs the 'Check out' operation
     * 
     * @param l1
     */
    public void checkOutOffice(Ldtp l1)
    {
        goToFile(l1);
        clickOnObject(l1, "Info");
        clickOnObject(l1, "ManageVersions");
        
        // Click Check Out action;
        waitForObject(l1, "Check Out");
        
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");
    
    }
    
    
    /**
     * Navigate to File-> Info - > 'Check in' and enter comments
     * 
     *      
     * @param l1
     */
    public void checkInOffice(Ldtp l2, String comment, boolean checkBoxState)
    {
        goToFile(l2);
        clickOnObject(l2, "Info");
        clickOnObject(l2, "CheckIn");

        //Enter any comment and click OK button;
        operateOnCheckIn(l2, comment, checkBoxState);
    }
    

}