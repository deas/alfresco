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
import org.alfresco.rest.api.tests.client.RequestContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
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
    public void ALF_159651() throws Exception
    {
        String thisTestName = getTestName();
        selectorTypeChildren(thisTestName);
    }

    @Test
    public void ALF_159661() throws Exception
    {
        String thisTestName = getTestName();
        selectorTypeDefinition(thisTestName);
    }

    @Test
    public void ALF_159671() throws Exception
    {
        String thisTestName = getTestName();
        selectorTypeDescendants(thisTestName);
    }

    @Test
    public void ALF_159681() throws Exception
    {
        String thisTestName = getTestName();
        selectorRepoInfo();
    }

    @Test
    public void ALF_158731() throws Exception
    {
        String thisTestName = getTestName();
        selectorRepoURL(thisTestName);
    }

    @Test
    public void ALF_158741() throws Exception
    {
        String thisTestName = getTestName();
        rootFolderURL(thisTestName);
    }

    @Test
    public void ALF_158751() throws Exception
    {
        String thisTestName = getTestName();
        objectsUsingPath(thisTestName);
    }

    @Test
    public void ALF_158761() throws Exception
    {
        String thisTestName = getTestName();
        objectsUsingObjectId(thisTestName);
    }

    @Test
    public void ALF_158771() throws Exception
    {
        String thisTestName = getTestName();
        selectChildren(thisTestName);
    }

    @Test(enabled = false)
    public void ALF_158781() throws Exception
    {
        String thisTestName = getTestName();
        compactJSONResponse(thisTestName);
    }

    @Test
    public void ALF_158791() throws Exception
    {
        String thisTestName = getTestName();
        /* Cannot be coded */
    }

    @Test
    public void ALF_158811() throws Exception
    {
        String thisTestName = getTestName();
        descendants(thisTestName);
    }

    @Test
    public void ALF_158821() throws Exception
    {
        String thisTestName = getTestName();
        checkedOut(thisTestName);
    }

    @Test
    public void ALF_158831() throws Exception
    {
        String thisTestName = getTestName();
        cmisSelectorParents(thisTestName);
    }

    @Test
    public void ALF_158841() throws Exception
    {
        String thisTestName = getTestName();
        cmisSelectorParents(thisTestName);
    }

    @Test
    public void ALF_158851() throws Exception
    {
        String thisTestName = getTestName();
        allowableActions(thisTestName);
    }

    @Test(enabled = false)
    public void ALF_158861() throws Exception
    {
        String thisTestName = getTestName();
        Map<String, String> params = new HashMap<String, String>();
        params.put("cmisselector", "object");
        HttpResponse httpResponse = getHttpResponse("/public/cmis/versions/1.1/browser/root/test", params);
        assertTrue(httpResponse.getStatusCode() == 200, httpResponse.getResponse());
    }

    @Test
    public void ALF_158871() throws Exception
    {
        String thisTestName = getTestName();
        objectProperties(thisTestName);
    }

    @Test
    public void ALF_158881() throws Exception
    {
        String thisTestName = getTestName();
        selectorContent(thisTestName);
    }

    @Test
    public void ALF_158891() throws Exception
    {
        String thisTestName = getTestName();
        renditionsSelector(thisTestName);
    }
}
