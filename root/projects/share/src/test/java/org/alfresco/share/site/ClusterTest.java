package org.alfresco.share.site;
/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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


import java.io.File;

import junit.framework.Assert;

import org.alfresco.share.util.SiteUtil;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.share.DashBoardPage;
import org.alfresco.webdrone.share.dashlet.MySitesDashlet;
import org.alfresco.webdrone.share.site.SiteDashboardPage;
import org.alfresco.webdrone.share.site.UploadFilePage;
import org.alfresco.webdrone.share.site.document.DocumentDetailsPage;
import org.alfresco.webdrone.share.site.document.DocumentLibraryPage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Functional test to verify an update to a node of alfresco
 * is replicated to other alfresco nodes in a cluster.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class ClusterTest extends AbstractSiteTest
{
    private static String siteName;
    private static File file;

    /**
     * Pre test setup using defualt admin user:
     * <ul>
     *     <li> Make up a site name. </li>
     *     <li> Create a dummy txt file. </li>
     *     <li> Create site using {@link WebDrone}. </li>
     *     <li> Upload dummy file to newly created site. </li>
     *     <li> Log user out </li>
     * </ul>
     */
    @BeforeClass
    public static void setup() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        file = SiteUtil.prepareFile();
        getWebDrone();
        try
        {
            createSite(siteName);
            //Upload the doucment
            SiteDashboardPage site = drone.getCurrentPage().render();
            DocumentLibraryPage docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
            UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload();
            docPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();   
        } 
        finally
        {
            quitWebDrone();
        }
    }

    @AfterClass
    public static void clean() throws Exception
    {
        SiteUtil.deleteSite(siteName, isCloud, shareUrl);
    }
    
    @Before
    public void startWebDrone() throws Exception
    {
        getWebDrone();  
    }
    
    @After
    public void closeWebDrone() throws Exception
    {
        quitWebDrone();  
    }
    
    @Test
    public void addLikeToDocument() throws Exception
    {
        DocumentLibraryPage documentLibPage = getDocumentLibraryPage();
        DocumentDetailsPage documentDetailsPage = documentLibPage.selectFile(file.getName());
        
        Assert.assertEquals("0", documentDetailsPage.getLikeCount());
        documentDetailsPage = documentDetailsPage.selectLike().render();
        Assert.assertEquals("1", documentDetailsPage.getLikeCount());
        
        for(int i = 0; i <= maxIteration; i++)
        {
            drone.refresh();
            documentDetailsPage = drone.getCurrentPage().render();
            Assert.assertEquals(documentDetailsPage.getLikeCount(),"1");
        }
    }

    /**
     * Helper method to navigate to document library page of a site that
     * we have created for the test.
     * 
     * @return {@link DocumentLibraryPage}.
     * @throws Exception if error
     */
    private DocumentLibraryPage getDocumentLibraryPage() throws Exception
    {
        DashBoardPage dashBoard = loginAs(username, password).render();
        MySitesDashlet dashlet = dashBoard.getDashlet("my-sites").render();

        SiteDashboardPage site = dashlet.selectSite(siteName).render();
        return site.getSiteNav().selectSiteDocumentLibrary().render();
    }
}
