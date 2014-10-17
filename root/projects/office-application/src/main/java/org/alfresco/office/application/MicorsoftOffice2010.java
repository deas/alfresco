/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * This class all the method involved in using the actions involved using Microsoft Excel
 * 
 * @author sprasanna
 */
package org.alfresco.office.application;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.cobra.ldtp.Ldtp;
import com.cobra.ldtp.LdtpExecutionError;

public class MicorsoftOffice2010 implements OfficeApplications
{

    private static final Logger logger = Logger.getLogger(MicorsoftOffice2010.class);

    Application application;
    protected String applicationExe;
    public String applicationWindowName;
    public LdtpInitialisation abstractUtil;
    protected String fileMenuPage = "File";
    public String blankWindow;
    public String officeVersion;

    protected int retryRefreshCount = 6;
    protected int waitInSeconds;

    public MicorsoftOffice2010(Application application, String officeVersion)
    {
        super();
        this.application = application;
        this.officeVersion = officeVersion;
        findApplicationName();
        setWaitTime();

    }

    public void setAbstractUtil(LdtpInitialisation abstractUtil)
    {
        this.abstractUtil = abstractUtil;
    }

    /*
     * Method to implement opening of any Office application based on the application name set
     */
    @Override
    public Ldtp openOfficeApplication() throws LdtpExecutionError, IOException
    {
        Ldtp ldtp;

        try
        {
            TimeUnit.SECONDS.sleep(5);
        }
        catch (InterruptedException e)
        {
        }

        String excelApplicationPath = abstractUtil.getApplicationPath(officeVersion) + applicationExe;
        Runtime.getRuntime().exec(excelApplicationPath);

        try
        {
            TimeUnit.SECONDS.sleep(3);
        }
        catch (InterruptedException e)
        {
        }

        String windowName = waitForWindow(applicationWindowName);
        System.out.println(windowName);
        ldtp = new Ldtp(windowName);
        return ldtp;
    }

    /*
     * Method to implement adding a data inside the Office application based on the application
     */
    @Override
    public void editOffice(Ldtp ldtp, String data) throws LdtpExecutionError
    {
        switch (application)
        {
            case WORD:
            {
                ldtp.enterString("edit", data);
                break;
            }
            case EXCEL:
            {
                ldtp.enterString("pane8", data);
                break;
            }
            case POWERPOINT:
            {
                ldtp.enterString("Slide", data);
                break;
            }
            default:
            {
                throw new LdtpExecutionError("Cannot find the application to edit");
            }

        }
    }

    /*
     * Method to implement Save
     */
    @Override
    public void saveOffice(Ldtp ldtp) throws LdtpExecutionError
    {
        ldtp.click(abstractUtil.getSaveButton());
    }

    /*
     * Method to implement Save for the first time
     */
    @Override
    public void saveOffice(Ldtp ldtp, String location) throws LdtpExecutionError
    {
        ldtp.click(abstractUtil.getSaveButton());

        String currentWin = waitForWindow(abstractUtil.getExplorerSaveAs());
        if (currentWin.isEmpty())
        {
            throw new LdtpExecutionError("Cannot find the Save window");
        }

        ldtp.activateWindow(abstractUtil.getExplorerSaveAs());
        ldtp.deleteText("txtFilename", 0);
        ldtp.enterString("txtFilename", location);
        ldtp.click(abstractUtil.getSaveButton());
    }

    /*
     * Method to implement SaveAs of office application
     */
    @Override
    public void saveAsOffice(Ldtp ldtp, String location) throws LdtpExecutionError
    {

        ldtp.click(abstractUtil.getFileMenu());
        ldtp.waitTillGuiExist(fileMenuPage, waitInSeconds);
        ldtp.click(abstractUtil.getSaveAsButton());

        String currentWin = waitForWindow(abstractUtil.getExplorerSaveAs());
        if (currentWin.isEmpty())
        {
            throw new LdtpExecutionError("Cannot find the Save As window");
        }
        ldtp.activateWindow(abstractUtil.getExplorerSaveAs());
        ldtp.deleteText("txtFilename", 0);
        ldtp.enterString("txtFilename", location);
        ldtp.click(abstractUtil.getSaveButton());

        ldtp.waitTillGuiNotExist(abstractUtil.getExplorerSaveAs());
    }

    /*
     * Method to implement Opening a file inside the office application
     */
    @Override
    public void openOfficeFromFileMenu(Ldtp ldtp, String location) throws LdtpExecutionError
    {
        ldtp.click(abstractUtil.getFileMenu());
        ldtp.waitTillGuiExist(fileMenuPage, waitInSeconds);

        ldtp.click("Open");

        String currentWin = waitForWindow("dlgOpen");
        if (currentWin.isEmpty())
        {
            throw new LdtpExecutionError("Cannot find the Open window");
        }

        ldtp.activateWindow("dlgOpen");
        ldtp.enterString("txtFilename", location);
        ldtp.mouseLeftClick("uknOpen");

    }

    /*
     * Method to implement close of file.
     */
    @Override
    public void closeOfficeApplication(String fileName) throws LdtpExecutionError
    {
        Ldtp ldtp;
        ldtp = new Ldtp(findWindowName(fileName));
        ldtp.click(abstractUtil.getFileMenu());

        ldtp.waitTillGuiExist(fileMenuPage, waitInSeconds);
        ldtp.click(abstractUtil.getExitButton());
    }

    /**
     * Finding Window name against the list of open windows
     */
    public String findWindowName(String fileName) throws LdtpExecutionError
    {
        String windowName = "";

        Ldtp ldtp = new Ldtp(applicationWindowName);
        String[] windowList = ldtp.getWindowList();
        for (String window : windowList)
        {
            if (window.contains(fileName))
            {
                windowName = window;
                break;
            }
        }
        return windowName;
    }

    protected void findApplicationName()
    {
        switch (application)
        {
            case WORD:
            {
                if (officeVersion.equals("2010"))
                {
                    applicationWindowName = "Microsoft Word";
                }
                else
                {
                    applicationWindowName = "Word";
                    blankWindow = "Blank document";
                }
                applicationExe = "WINWORD.EXE";
                break;
            }
            case EXCEL:
            {
                if (officeVersion.equals("2010"))
                {
                    applicationWindowName = "Microsoft Excel - ";
                }
                else
                {
                    applicationWindowName = "Excel";
                    blankWindow = "Blank workbook";
                }
                applicationExe = "EXCEL.EXE";
                break;
            }
            case POWERPOINT:
            {
                if (officeVersion.equals("2010"))
                {
                    applicationWindowName = "Microsoft PowerPoint";
                }
                else
                {
                    applicationWindowName = "PowerPoint";
                    blankWindow = "Blank Presentation";
                }
                applicationExe = "POWERPNT.EXE";
                break;

            }

            case OUTLOOK:
            {
                if (officeVersion.equals("2013"))
                {
                    applicationWindowName = "Outlook";
                }
                else
                {
                    applicationWindowName = "Microsoft Outlook";
                    blankWindow = "Blank Presentation";
                }

                applicationExe = "OUTLOOK.EXE";
                break;

            }

            default:
            {
                throw new LdtpExecutionError("Cannot find the application to open :" + application);
            }

        }
    }

    /**
     * Method set the wait time for dialog / window to open
     */
    protected void setWaitTime()
    {
        try
        {
            Properties officeAppProperty = new Properties();
            officeAppProperty.load(this.getClass().getClassLoader().getResourceAsStream("office-application.properties"));
            String wait = officeAppProperty.getProperty("window.wait.time");
            waitInSeconds = Integer.parseInt(wait);
        }
        catch (IOException e)
        {
            throw new LdtpExecutionError("Cannot find the waiting time value");
        }

    }

    // public String readDataFromFile(Ldtp ldtp)
    // {
    // String data = "";
    // try
    // {
    // switch (application)
    // {
    // case "word":
    // {
    // data = ldtp.getTextValue("edit");
    // return data;
    // }
    // case "excel":
    // {
    // data = ldtp.getTextValue("pane8");
    // return data;
    // }
    // case "powerpoint":
    // {
    // data = ldtp.getTextValue("Slide");
    // return data;
    // }
    // default:
    // {
    // throw new LdtpExecutionError("Cannot find the application to edit");
    // }
    // }
    // }
    // catch (LdtpExecutionError ld)
    // {
    // return data;
    // }
    //
    // }

    /**
     * Set on the current window
     */
    public Ldtp setOnWindow(String windowName) throws LdtpExecutionError, IOException
    {

        logger.info("Set on " + windowName);
        String currentWin = waitForWindow(windowName);
        Ldtp ldtp = new Ldtp(currentWin);
        ldtp.activateWindow(currentWin);
        return ldtp;
    }

    /**
     * General method that clicks on a object
     * 
     * @param ldtp
     * @param name
     */

    public void clickOnObject(Ldtp ldtp, String myObject)
    {
        logger.info("Click on " + myObject);

        waitForObject(ldtp, myObject);

        ldtp.mouseMove(myObject);
        ldtp.waitTime(1);
        ldtp.mouseLeftClick(myObject);
    }

    /**
     * Click in File
     * 
     * @param ldtp
     */
    public void goToFile(Ldtp ldtp)
    {
        logger.info("Go to file");

        ldtp.click(abstractUtil.getFileMenu());
        ldtp.waitTillGuiExist(fileMenuPage, waitInSeconds);

    }

    /**
     * Set the name and password on Windows Security
     * 
     * @param userName
     * @param password
     */
    public void operateOnSecurity(Ldtp ldtp1, String userName, String password)
    {
        logger.info("Waiting for... 'Windows Security' window");
        String securyWin = waitForWindow("Windows Security");
        if (securyWin.isEmpty())
            return;

        logger.info("'Windows Security' found. Type user/password and click OK on 'Windows Security'");
        ldtp1.deleteText("txtUsername", 0);
        ldtp1.enterString("txtUsername", userName);
        ldtp1.enterString("txtPassword", password);
        ldtp1.click("OK");

    }

    /**
     * Set a comment on Check In window
     * 
     * @param Ldtp
     * @param comment
     * @param checkBoxState
     */
    public void operateOnCheckIn(Ldtp l, String comment, boolean checkBoxState)
    {
        logger.info("Type a comment and click OK on 'Check In'");

        String currentWin = waitForWindow("Check In");
        if (currentWin.isEmpty())
            throw new LdtpExecutionError("Cannot find the Check in window");

        if (!comment.isEmpty())
        {
            l.enterString("txtVersionComments", comment);
        }
        if (checkBoxState == true)
        {
            l.check("chkKeepthedocumentcheckedoutaftercheckinginthisversion");
        }
        l.mouseLeftClick("btnOK");

        l.waitTime(2);
    }

    /***
     * @param filename
     */
    public void operateOnOpen(Ldtp ldtp1, String path, String siteName, String fileName, String userName, String password)
    {
        logger.info("Operate on 'Open'");

        String currentWin = waitForWindow("dlgOpen");
        if (currentWin.isEmpty())
        {
            throw new LdtpExecutionError("Cannot find the Open window");
        }
        ldtp1.activateWindow(currentWin);
        ldtp1.enterString("txtFilename", path);
        ldtp1.mouseLeftClick("uknOpen");

        operateOnSecurity(ldtp1, userName, password);

        waitObjectHasValue(ldtp1, "txtFilename", "");

        ldtp1.enterString("txtFilename", siteName.toLowerCase());
        ldtp1.mouseLeftClick("uknOpen");
        waitObjectHasValue(ldtp1, "txtFilename", "");

        ldtp1.enterString("txtFilename", "documentLibrary");
        ldtp1.mouseLeftClick("uknOpen");

        operateOnSecurity(ldtp1, userName, password);

        waitObjectHasValue(ldtp1, "txtFilename", "");

        ldtp1.enterString("txtFilename", fileName);
        ldtp1.mouseLeftClick("uknOpen");

        operateOnSecurity(ldtp1, userName, password);

        ldtp1.waitTime(5);

    }

    /**
     * Waits for an object to have a certain value
     * 
     * @param ldtp
     * @param objectName
     * @param valueToWait
     */
    private void waitObjectHasValue(Ldtp ldtp, String objectName, String valueToWait)
    {
        int waitInSeconds = 2;
        int counter = 0;
        while (counter < retryRefreshCount)
        {
            String fileNameContent = ldtp.getTextValue(objectName);
            if (fileNameContent.equals(valueToWait))
                break;
            else
            {
                ldtp.waitTime(waitInSeconds);
                waitInSeconds = (waitInSeconds * 2);
            }
        }
    }

    /**
     * Wait for an object
     * 
     * @param ldtp
     * @param objectName
     */
    protected void waitForObject(Ldtp ldtp, String objectName)
    {
        int counter = 0;
        int exists = 0;

        while (counter < retryRefreshCount)
        {
            exists = ldtp.objectExist(objectName);
            if (exists == 1)
                break;
            else
            {
                counter++;
            }
        }

        if (exists == 0)
        {
            throw new LdtpExecutionError("Cannot find the object: " + objectName);
        }
    }

    /**
     * Wait for an window
     * 
     * @param windowName
     * @return
     */
    public String waitForWindow(String windowName)
    {
        String windowNameFound = "";
        int counter = 0;

        while (counter < retryRefreshCount)
        {
            windowNameFound = findWindowName(windowName);
            if (!windowNameFound.isEmpty())
                break;
            else
            {
                counter++;
            }

        }

        return windowNameFound;

    }

    /**
     * Set a comment on Save As window
     * 
     * @param path
     *            SharePoint Path where to save the file
     */
    public void operateOnSaveAs(Ldtp ldtp1, String path, String siteName, String fileName, String userName, String password)
    {
        logger.info("Operate on 'Save As'");

        String currentWin = waitForWindow("Save As");
        if (currentWin.isEmpty())
        {
            throw new LdtpExecutionError("Cannot find the 'Save As' window");
        }

        ldtp1.activateWindow(currentWin);
        ldtp1.enterString("txtFilename", path);
        ldtp1.mouseLeftClick("btnSave");

        operateOnSecurity(ldtp1, userName, password);

        String siteObject = siteName.replace(".", "").replace("_", "");

        waitForObject(ldtp1, siteObject.toLowerCase());

        ldtp1.doubleClick(siteObject.toLowerCase());

        operateOnSecurity(ldtp1, userName, password);

        waitForObject(ldtp1, "lstdocumentLibrary");
        ldtp1.doubleClick("lstdocumentLibrary");

        String addressBarObject = "tbarAddress" + path.replace(":", "").replace(".", "") + "/" + siteName.replace(".", "").replace("_", "").toLowerCase() + "/"
                + "documentLibrary";
        waitForObject(ldtp1, addressBarObject);
        ldtp1.enterString("txtFilename", fileName);
        ldtp1.mouseLeftClick("btnSave");

        operateOnConfirmSaveAs(ldtp1);

        operateOnSecurity(ldtp1, userName, password);

        ldtp1.waitTime(5);

    }

    /**
     * Click on 'Yes' button from 'Confirm Save As'
     * 
     * @param ldtp1
     */
    public void operateOnConfirmSaveAs(Ldtp ldtp1)
    {
        logger.info("Click OK on 'Confirm Save As'");

        String currentWin = waitForWindow("Confirm Save As");
        if (currentWin.isEmpty())
            return;

        ldtp1.activateWindow("Confirm Save As");
        ldtp1.click("Yes");

    }

    /**
     * Return the object name
     * 
     * @param ldtp
     * @param name
     * @return
     */
    protected String getObjectName(Ldtp ldtp, String name)
    {
        String[] objects = ldtp.getObjectList();
        String myObject = "";
        for (int i = 0; i < objects.length; i++)
        {
            if (objects[i].contains(name))
            {
                myObject = objects[i];
                break;
            }
        }

        return myObject;
    }

    /**
     * Verifies that an object is displayed
     * 
     * @param ldtp
     * @param name
     */
    public boolean isObjectDisplayed(Ldtp ldtp, String name)
    {
        String myObject = getObjectName(ldtp, name);
        if (myObject.isEmpty())
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * This method creates a new meeting workspace in Outlook 2010
     * 
     * @param sharePointPath - path for SharePoint
     * @param siteName - name of the site
     * @param location - location for the meeting
     * @param userName
     * @param password
     */
    public void operateOnCreateNewMeetingWorkspace(Ldtp l, String sharePointPath, String siteName, String location, String userName, String password, boolean withSubject, boolean clickRemove)
    {

        logger.info("Start creating new meeting workspace");

        Ldtp security = new Ldtp("Windows Security");

        l.click("btnMeeting");

        // set focus on new window
        String windowNameUntitled = waitForWindow("Untitled");
        Ldtp l1 = new Ldtp(windowNameUntitled);
        l.activateWindow(windowNameUntitled);

        l1.click("btnMeetingWorkspace");

        l1.click("hlnkChangesettings");
        l1.waitTime(2);
        l1.selectItem("cboWebsiteDropdown", "Other...");

        String windowNameServer = waitForWindow("Other Workspace Server");
        Ldtp l2 = new Ldtp(windowNameServer);
        l.activateWindow(windowNameServer);

        l2.deleteText("txtServerTextbox", 0);
        l2.enterString("txtServerTextbox", sharePointPath);

        l2.click("btnOK");
        l2.waitTime(3);
        operateOnSecurity(security, userName, password);

        l1.click("chkAlldayevent");

        windowNameUntitled = waitForWindow("Untitled");
        Ldtp l3 = new Ldtp(windowNameUntitled);
        l3.activateWindow(windowNameUntitled);

        l3.click("btnOK");

        if (withSubject == true)
        {
            l3.enterString("txtLocation", location);
            l3.enterString("txtSubject", siteName);
        }
        else
        {
            l3.enterString("txtLocation", location);
        }

        if (withSubject == true)
        {
            logger.info("Creating the event");
            l3.click("btnCreate");

            l3.waitTime(4);

            // first verification
            operateOnSecurity(security, userName, password);
            // second verification
            operateOnSecurity(security, userName, password);

            if (clickRemove == true)
            {
                String forRemove = waitForWindow(siteName);
                Ldtp remove = new Ldtp(forRemove);
                remove.activateWindow(forRemove);

                remove.doubleClick("btnRemove");

                String message = waitForWindow("Microsoft Outlook");
                Ldtp l_error = new Ldtp(message);
                l_error.click("btnYes");
            }
        }
        else
        {
            // Your attempt to create a Meeting Workspace or link to an existing one can't be completed.
            // Reason: Site name is not specified. Please fill up subject field.
            logger.info("Error when subject is not filled");
            l3.click("btnCreate");
            l3.waitTime(4);
            operateOnSecurity(security, userName, password);
        }
    }

    /**
     * This method creates meeting workspace in Outlook 2010 from an existing Site
     * 
     * @param sharePointPath - path for SharePoint
     * @param siteName - name of the site
     * @param subject
     * @param location - location for the meeting
     * @param userName
     * @param password
     */
    public void operateOnLinkToExistingWorkspace(Ldtp l, String sharePointPath, String siteName, String subject, String location, String userName, String password, boolean withSubject)
    {

        Ldtp security = new Ldtp("Windows Security");

        // click meeting
        l.click("btnMeeting");

        // set focus on new window
        String windowNameUntitled = waitForWindow("Untitled");
        Ldtp l1 = new Ldtp(windowNameUntitled);
        l.activateWindow(windowNameUntitled);

        l1.click("btnMeetingWorkspace");

        l1.waitTime(2);
        l1.click("hlnkChangesettings");
        l1.click("rbtnLinktoanexistingworkspace");

        l1.mouseLeftClick("cboWorkspaceDropdown");
        l1.waitTime(4);
        operateOnSecurity(security, userName, password);

        l1.selectItem("cboWorkspaceDropdown", siteName);

        l1.click("btnOK");

        if (withSubject == true)
        {
            l1.enterString("txtLocation", location);
            l1.enterString("txtSubject", subject);
        }
        else
        {
            l1.enterString("txtLocation", location);
        }

        // String windowsSite = findWindowName(subject);
        if (withSubject == true)
        {
            // click Link button
            l1.click("btnLink");
            l1.waitTime(4);
            operateOnSecurity(security, userName, password);
        }
        else
        {
            // Your attempt to create a Meeting Workspace or link to an existing one can't be completed.
            // Reason: Site name is not specified. Please fill up subject field.
            windowNameUntitled = waitForWindow("Untitled");
            Ldtp l2 = new Ldtp(windowNameUntitled);
            l2.activateWindow(windowNameUntitled);

            logger.info("Error when subject is not filled");

            // click Link button
            l2.click("btnLink");
            l2.waitTime(4);
            operateOnSecurity(security, userName, password);

        }

    }

    /*
     * Method to exit any office application
     */

    public void exitOfficeApplication(Ldtp ldtp)
    {
        // clickOnObject(ldtp, "btnClose1");

        ldtp.generateKeyEvent("<alt><f4>");
    }

}
