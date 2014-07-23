/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
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
 * Class to include: Tests for CMIS Action values for ATOM binding
 * 
 * @author Abhijeet Bharade
 * 
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "AlfrescoOne"})
public class CMISAtomActionValuesTests extends CMISActionValuesTest
{
    private static Log logger = LogFactory.getLog(CMISAtomActionValuesTests.class);

    protected String testName;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception
    {
        try
        {
            super.beforeClass();
            binding = CMISBinding.ATOMPUB11;

            testName = this.getClass().getSimpleName();

            createTestData(drone, testName);
        }
        catch (Throwable t)
        {
            logger.error("Failed for params: Binding - " + binding + " testName - " + testName + " testUser - " + testUser + " siteName - " + siteName
                    + " fileName - " + fileName + " deleteVersionFile - " + deleteVersionFile + " fileName1 - " + fileName1 + " folderName - " + folderName
                    + " sourceFolderName - " + sourceFolderName);
            reportError(drone, testName, t);
        }
    }

    @Test
    public void ALF_2455() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);

        createDocTest(thisFileName);

        // check the node ref from share.
    }

    @Test(groups = { "BambooBug" })
    public void ALF_2456() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);

        createFromSource(drone, thisFileName);
    }

    @Test(groups = { "BambooBug" })
    public void ALF_2458() throws Exception
    {
        createRelationship("R:cmiscustom:assoc");
    }

    @Test
    public void ALF_2476() throws Exception
    {
        deleteAllVersionsTest();
    }

    @Test
    public void ALF_2465() throws Exception
    {
        updateTest();
    }

    @Test
    public void ALF_2461() throws Exception
    {
        queryTest();
    }

    @Test
    public void ALF_2463() throws Exception
    {
        String thisTestName = getTestName();
        String thisFolderName = getFolderName(thisTestName);

        createFolderTest(drone, thisFolderName);
    }

    @Test
    public void ALF_2467() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);

        moveTest(drone, thisFileName);
    }

    @Test
    public void ALF_2475() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        String thisFolderName = getFolderName(thisTestName);
        deleteTest(drone, thisFileName, thisFolderName);
    }

    @Test
    public void ALF_2457() throws Exception
    {
        String thisTestName = getTestName();
        String thisFolderName = getFolderName(thisTestName);
        String thisSubFolderName1 = getFileName(thisTestName + "Sub1");
        String thisSubFolderName2 = getFileName(thisTestName + "Sub2");

        deleteTreeTest(drone, thisFolderName, thisSubFolderName1, thisSubFolderName2);
    }

    @Test(groups = { "BambooBug" })
    public void ALF_2459() throws Exception
    {
        setContentTest(drone);
    }

    @Test
    public void ALF_2460() throws Exception
    {
        appendTest(drone);
    }

    @Test
    public void ALF_2462() throws Exception
    {
        deleteContentTest(drone);
    }

    @Test
    public void ALF_2464() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        String thisFolderName = getFolderName(thisTestName);
        addObjectToFolderTest(drone, thisFileName, thisFolderName);
    }

    @Test
    public void ALF_2466() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        String thisFolderName = getFolderName(thisTestName);
        removeObjectFromFolderTest(drone, thisFileName, thisFolderName);
    }

    @Test
    public void ALF_2468() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        checkOutTest(drone, thisFileName);
    }

    @Test
    public void ALF_2469() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        checkInTest(drone, thisFileName, false, false);
    }

    @Test
    public void ALF_2470() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        checkInTest(drone, thisFileName, false, true);
    }

    @Test
    public void ALF_2471() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        checkInTest(drone, thisFileName, true, false);
    }

    @Test
    public void ALF_2472() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        checkInTest(drone, thisFileName, true, true);
    }

    @Test
    public void ALF_2473() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        cancelCheckOutTest(drone, thisFileName);
    }

    @Test
    public void ALF_2474() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        applyACLTest(drone, thisFileName);
    }

    @Test(expectedExceptions=org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException.class)
    public void ALF_3073() 
    {
        super.cmisItemTypeShouldNotBeQueryable();
    }

    @Test
    public void ALF_3074()
    {
        super.cmPersonShouldFindPeople();
    }

    @Test
    public void ALF_3075()
    {
        super.cmPersonWithWhereClauseShouldFindPerson();
    }

    @Test
    public void ALF_3076()
    {
        super.cmPersonCanBeUpdatedBySelf();
    }

    @Test(expectedExceptions=org.apache.chemistry.opencmis.commons.exceptions.CmisUnauthorizedException.class)
    public void ALF_3077()
    {
        super.cmPersonCannotBeUpdatedByUnauthorizedUser();
    }

    @Test(expectedExceptions=org.apache.chemistry.opencmis.commons.exceptions.CmisUnauthorizedException.class)
    public void ALF_3078()
    {
        super.cmPersonCannotBeDeletedByUnauthorizedUser();
    }
    
    @Test(expectedExceptions=org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException.class)
    public void ALF_3079() 
    {
        super.cmPersonCannotBeDeletedByAuthorizedUserViaCmis();
    }
    
}
