/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.share.api.cmis;

import org.alfresco.share.enums.CMISBinding;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class to include: Tests for CMIS Action values for Browser binding
 * 
 * @author Abhijeet Bharade
 * 
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "AlfrescoOne" })
public class CMISBrowserActionValuesTest extends CMISActionValuesTest
{

    private static Log logger = LogFactory.getLog(CMISBrowserActionValuesTest.class);


    protected String testName;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception
    {
        try
        {
            super.beforeClass();
            binding = CMISBinding.BROWSER11;

            testName = this.getClass().getSimpleName();

            createTestData(drone, testName);
        }
        catch (Throwable e)
        {
            logger.error("Failed for params: Binding - " + binding + " testName - " + testName + " testUser - " + testUser + " siteName - " + siteName
                    + " fileName - " + fileName + " deleteVersionFile - " + deleteVersionFile + " fileName1 - " + fileName1 + " folderName - " + folderName
                    + " sourceFolderName - " + sourceFolderName);
            reportError(drone, testName, e);
        }
    }

    @Test
    public void ALF_159921() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);

        createDocTest(thisFileName);

    }

    @Test
    public void ALF_159691() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);

        createFromSource(drone, thisFileName);
    }

    @Test
    public void ALF_159701() throws Exception
    {
        createRelationship("R:cm:basis");
    }

    @Test
    public void ALF_159711() throws Exception
    {
        deleteAllVersionsTest();
    }

    @Test
    public void ALF_159741() throws Exception
    {
        updateTest();

    }

    @Test
    public void ALF_159721() throws Exception
    {
        queryTest();
    }

    @Test
    public void ALF_159731() throws Exception
    {
        String thisTestName = getTestName();
        String thisFolderName = getFolderName(thisTestName);

        createFolderTest(drone, thisFolderName);
    }

    @Test
    public void ALF_159751() throws Exception
    {

        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);

        moveTest(drone, thisFileName);
    }

    @Test
    public void ALF_159761() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        String thisFolderName = getFolderName(thisTestName);
        deleteTest(drone, thisFileName, thisFolderName);

    }

    @Test
    public void ALF_159771() throws Exception
    {
        String thisTestName = getTestName();
        String thisFolderName = getFolderName(thisTestName);
        String thisSubFolderName1 = getFileName(thisTestName + "Sub1");
        String thisSubFolderName2 = getFileName(thisTestName + "Sub2");

        deleteTreeTest(drone, thisFolderName, thisSubFolderName1, thisSubFolderName2);
    }

    @Test
    public void ALF_159781() throws Exception
    {
        setContentTest(drone);
    }

    @Test
    public void ALF_159791() throws Exception
    {
        appendTest(drone);
    }

    @Test
    public void ALF_159801() throws Exception
    {
        deleteContentTest(drone);
    }

    @Test
    public void ALF_159811() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        String thisFolderName = getFolderName(thisTestName);
        addObjectToFolderTest(drone, thisFileName, thisFolderName);
    }
    @Test
    public void ALF_159821() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        String thisFolderName = getFolderName(thisTestName);
        removeObjectFromFolderTest(drone, thisFileName, thisFolderName);
    }

    @Test
    public void ALF_159831() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        checkOutTest(drone, thisFileName);
    }

    @Test
    public void ALF_159841() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        checkInTest(drone, thisFileName, false, false);
    }

    @Test
    public void ALF_159851() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        checkInTest(drone, thisFileName, false, true);
    }

    @Test
    public void ALF_159861() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        checkInTest(drone, thisFileName, true, false);
    }

    @Test
    public void ALF_159871() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        checkInTest(drone, thisFileName, true, true);
    }

    @Test
    public void ALF_159881() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        cancelCheckOutTest(drone, thisFileName);
    }

    @Test
    public void ALF_159911() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        applyACLTest(drone, thisFileName);
    }
}
