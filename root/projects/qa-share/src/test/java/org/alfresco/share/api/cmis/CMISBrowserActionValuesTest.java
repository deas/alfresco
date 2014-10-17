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
    public void AONE_14406() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);

        createDocTest(thisFileName);

    }

    @Test
    public void AONE_14407() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);

        createFromSource(drone, thisFileName);
    }

    @Test
    public void AONE_14408() throws Exception
    {
        createRelationship("R:cm:basis");
    }

    @Test
    public void AONE_14409() throws Exception
    {
        deleteAllVersionsTest();
    }

    @Test(groups = {"IntermittentBugs"})
    public void AONE_14410() throws Exception
    {
        updateTest();
    }

    @Test
    public void AONE_14411() throws Exception
    {
        queryTest();
    }

    @Test
    public void AONE_14412() throws Exception
    {
        String thisTestName = getTestName();
        String thisFolderName = getFolderName(thisTestName);

        createFolderTest(drone, thisFolderName);
    }

    @Test
    public void AONE_14413() throws Exception
    {

        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);

        moveTest(drone, thisFileName);
    }

    @Test
    public void AONE_14414() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        String thisFolderName = getFolderName(thisTestName);
        deleteTest(drone, thisFileName, thisFolderName);

    }

    @Test
    public void AONE_14415() throws Exception
    {
        String thisTestName = getTestName();
        String thisFolderName = getFolderName(thisTestName);
        String thisSubFolderName1 = getFileName(thisTestName + "Sub1");
        String thisSubFolderName2 = getFileName(thisTestName + "Sub2");

        deleteTreeTest(drone, thisFolderName, thisSubFolderName1, thisSubFolderName2);
    }

    @Test
    public void AONE_14416() throws Exception
    {
        setContentTest(drone);
    }

    @Test(groups = {"IntermittentBugs"})
    public void AONE_14417() throws Exception
    {
        appendTest(drone);
    }

    @Test
    public void AONE_14418() throws Exception
    {
        deleteContentTest(drone);
    }

    @Test
    public void AONE_14419() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        String thisFolderName = getFolderName(thisTestName);
        addObjectToFolderTest(drone, thisFileName, thisFolderName);
    }

    @Test
    public void AONE_14420() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        String thisFolderName = getFolderName(thisTestName);
        removeObjectFromFolderTest(drone, thisFileName, thisFolderName);
    }

    @Test
    public void AONE_14421() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        checkOutTest(drone, thisFileName);
    }

    @Test
    public void AONE_14422() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        checkInTest(drone, thisFileName, false, false);
    }

    @Test
    public void AONE_14423() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        checkInTest(drone, thisFileName, false, true);
    }

    @Test
    public void AONE_14424() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        checkInTest(drone, thisFileName, true, false);
    }

    @Test
    public void AONE_14425() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        checkInTest(drone, thisFileName, true, true);
    }

    @Test
    public void AONE_14426() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        cancelCheckOutTest(drone, thisFileName);
    }

    @Test(groups = {"IntermittentBugs"})
    public void AONE_14427() throws Exception
    {
        String thisTestName = getTestName();
        String thisFileName = getFileName(thisTestName);
        String thisFolderName = getFolderName(thisTestName);
        applyACLTest(drone, thisFileName, thisFolderName);
    }

    /**
     * Preconditions
     * 1) Alfresco started
     * 2) RESTClient is opened (for example, use Firefox RESTClient plugin).
     * 3) POST HTTP method is specified.
     * 4) Http Authentication is specified.
     * 
     * Put this in the URL:
     * http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/browser?cmisaction=query
     * 
     * Put this in the Body:
     * statement=SELECT * FROM cmis:item
     * 
     * Expected:
     * Header Status Code: 400 Bad Request 
     * Body contains CmisInvalidArgumentException
     */
	@Test(expectedExceptions = org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException.class)
	public void AONE_14428()
    {
        super.cmisItemTypeShouldNotBeQueryable();
    }

    /**
     * Preconditions
     * 1) Alfresco started
     * 2) RESTClient is opened (for example, use Firefox RESTClient plugin).
     * 3) POST HTTP method is specified.
     * 4) Http Authentication is specified.
     * 
     * Put this in the URL:
     * http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/browser?cmisaction=query
     * 
     * Put this in the Body:
     * statement=SELECT * FROM cm:person
     * 
     * Expected:
     * Header Status Code: 200 OK
     * Body contains cm:homeFolder, abeecher, mjackson, admin, guest
     */
    @Test
    public void AONE_14429()
    {
        super.cmPersonShouldFindPeople();
    }

    /**
     * Preconditions
     * 1) Alfresco started
     * 2) RESTClient is opened (for example, use Firefox RESTClient plugin).
     * 3) POST HTTP method is specified.
     * 4) Http Authentication is specified.
     * 
     * Put this in the URL:
     * http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/browser?cmisaction=query
     * 
     * Put this in the Body:
     * statement=SELECT * FROM cm:person where cm:userName like '%25ee%25'
     * 
     * Expected:
     * Header Status Code: 200 OK
     * Body contains abeecher for Enterprise and engineering for Cloud
     */
    @Test
    public void AONE_14430()
    {
        super.cmPersonWithWhereClauseShouldFindPerson();
    }

    /**
     * Preconditions
     * 1) Alfresco started
     * 2) RESTClient is opened (for example, use Firefox RESTClient plugin).
     * 3) POST HTTP method is specified.
     * 4) Basic Authentication is specified, and user is logged in as abeecher,abeecher
     * 5) Get the objectid and location of abeecher via a cmis query
     * 
     * Put this in the URL:
     * http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/browser/root?cmisaction=update
     * 
     * Put this in the Body:
     * objectid=dc103838-645f-43c1-8a2a-bc187e13c343&propertyId[0]=cm:location&propertyValue[0]=Tilbury, UK a change
     * 
     * Expected:
     * Header Status Code: 200 OK
     * Body contains the changed location "Tilbury, UK a change"
     */
    @Test
    public void AONE_14431()
    {
        super.cmPersonCanBeUpdatedBySelf();
    }

    /**
     * Preconditions
     * 1) Alfresco started
     * 2) RESTClient is opened (for example, use Firefox RESTClient plugin).
     * 3) POST HTTP method is specified.
     * 4) Basic Authentication is specified, and user is logged in as abeecher,abeecher
     * 5) Get the objectid and location of admin via a cmis query
     * 
     * Put this in the URL:
     * http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/browser/root?cmisaction=update
     * 
     * Put this in the Body:
     * objectid=c16a8bde-631d-4e0d-822d-c8fb267d7efb&propertyId[0]=cm:location&propertyValue[0]=Tilbury, UK a change
     * 
     * Expected:
     * Header Status Code: 400 Bad Request 
     * Body contains CmisUnauthorizedException
     */
	@Test(expectedExceptions = org.apache.chemistry.opencmis.commons.exceptions.CmisUnauthorizedException.class)
	public void AONE_14432()
    {
        super.cmPersonCannotBeUpdatedByUnauthorizedUser();
    }

    /**
     * Preconditions
     * 1) Alfresco started
     * 2) RESTClient is opened (for example, use Firefox RESTClient plugin).
     * 3) POST HTTP method is specified.
     * 4) Basic Authentication is specified, and user is logged in as mjackson,mjackson
     * 5) Get the objectid and location of abeecher via a cmis query
     * 
     * Put this in the URL:
     * http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/browser/root?objectid=dc103838-645f-43c1-8a2a-bc187e13c343&cmisaction=delete
     * 
     * Put nothing in the Body.
     * 
     * Expected:
     * Header Status Code: 400 Bad Request 
     * Body contains CmisUnauthorizedException
     */
	@Test(expectedExceptions = org.apache.chemistry.opencmis.commons.exceptions.CmisUnauthorizedException.class)
	public void AONE_14433()
    {
        super.cmPersonCannotBeDeletedByUnauthorizedUser();
    }

    /**
     * Preconditions
     * 1) Alfresco started
     * 2) RESTClient is opened (for example, use Firefox RESTClient plugin).
     * 3) POST HTTP method is specified.
     * 4) Basic Authentication is specified, and user is logged in as admin,admin
     * 5) Get the objectid and location of abeecher via a cmis query
     * 
     * Put this in the URL:
     * http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/browser/root?objectid=dc103838-645f-43c1-8a2a-bc187e13c343&cmisaction=delete
     * 
     * Put nothing in the Body.
     * 
     * Expected:
     * Header Status Code: 500 Internal Server Error 
     * Body contains CmisRuntimeException
     */
	@Test(expectedExceptions = org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException.class)
	public void AONE_14434()
    {
        super.cmPersonCannotBeDeletedByAuthorizedUserViaCmis();
    }

}
