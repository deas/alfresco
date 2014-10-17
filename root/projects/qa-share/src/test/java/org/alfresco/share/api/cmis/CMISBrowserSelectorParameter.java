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


import org.alfresco.rest.api.tests.client.HttpResponse;
import org.alfresco.share.enums.CMISBinding;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertTrue;

/**
 * Class to include: Tests for CMIS Selector Parameter for Browser binding
 * 
 * @author Abhijeet Bharade
 * 
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "AlfrescoOne", "MyAlfresco" })
public class CMISBrowserSelectorParameter extends CMISSelectorParameter
{

    private static Log logger = LogFactory.getLog(CMISBrowserSelectorParameter.class);

    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception
    {
        try
        {
            super.beforeClass();
            binding = CMISBinding.BROWSER11;

            testName = this.getClass().getSimpleName();

            createTestData(testName);
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }


    @Test
    public void AONE_14495() throws Exception
    {
        String thisTestName = getTestName();
        selectorTypeChildren(thisTestName);
    }

    @Test(groups = {"IntermittentBugs"})
    public void AONE_14496() throws Exception
    {
        String thisTestName = getTestName();
        selectorTypeDefinition(thisTestName);
    }

    @Test(groups = {"IntermittentBugs"})
    public void AONE_14497() throws Exception
    {
        String thisTestName = getTestName();
        selectorTypeDescendants(thisTestName);
    }

    @Test
    public void AONE_14498() throws Exception
    {
        String thisTestName = getTestName();
        selectorRepoInfo();
    }

    @Test
    public void AONE_14499() throws Exception
    {
        String thisTestName = getTestName();
        selectorRepoURL(thisTestName);
    }

    @Test
    public void AONE_14500() throws Exception
    {
        String thisTestName = getTestName();
        rootFolderURL(thisTestName);
    }

    @Test
    public void AONE_14501() throws Exception
    {
        String thisTestName = getTestName();
        objectsUsingPath(thisTestName);
    }

    @Test
    public void AONE_14502() throws Exception
    {
        String thisTestName = getTestName();
        objectsUsingObjectId(thisTestName);
    }

    @Test
    public void AONE_14503() throws Exception
    {
        String thisTestName = getTestName();
        selectChildren(thisTestName);
    }

    @Test
    public void AONE_14504() throws Exception
    {
        String thisTestName = getTestName();
        compactJSONResponse(thisTestName);
    }

    @Test
    public void AONE_14506() throws Exception
    {
        String thisTestName = getTestName();
        descendants(thisTestName);
    }

    @Test
    public void AONE_14507() throws Exception
    {
        String thisTestName = getTestName();
        checkedOut(thisTestName);
    }

    @Test
    public void AONE_14508() throws Exception
    {
        String thisTestName = getTestName();
        cmisSelectorParents(thisTestName);
    }

    @Test
    public void AONE_14509() throws Exception
    {
        String thisTestName = getTestName();
        cmisSelectorParents(thisTestName);
    }

    @Test
    public void AONE_14510() throws Exception
    {
        String thisTestName = getTestName();
        allowableActions(thisTestName);
    }

    @Test
    public void AONE_14511() throws Exception
    {
        String thisTestName = getTestName();
        Map<String, String> params = new HashMap<String, String>();
        params.put("cmisselector", "object");
        String resourcePath = DOMAIN + "/public/cmis/versions/1.1/browser/root/Sites/" + siteName + "/documentLibrary/" + folderName;
        HttpResponse httpResponse = getHttpResponse(resourcePath, params);
        assertTrue(httpResponse.getStatusCode() == 200, httpResponse.getResponse());
        httpResponse = getHttpResponse(resourcePath + "/" + fileName1, params);
        assertTrue(httpResponse.getStatusCode() == 200, httpResponse.getResponse());
    }

    @Test
    public void AONE_14512() throws Exception
    {
        String thisTestName = getTestName();
        objectProperties(thisTestName);
    }

    @Test
    public void AONE_14513() throws Exception
    {
        String thisTestName = getTestName();
        selectorContent(thisTestName);
    }

    @Test
    public void AONE_14514() throws Exception
    {
        String thisTestName = getTestName();
        renditionsSelector(thisTestName);
    }
}
