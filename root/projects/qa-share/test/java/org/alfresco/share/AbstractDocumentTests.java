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
package org.alfresco.share;

import java.io.File;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.exception.PageException;
/**
 * Abstract test holds all common methods and functionality to test against
 * Document based tests.
 * 
 * @author Michael Suzuki
 */
public abstract class AbstractDocumentTests extends AbstractTests
{
    /**
     * Helper method to navigate to document library page of a site that we
     * create for the test.
     * @param siteName String site identifier
     * @return HtmlPage document library page.
     * @throws PageException if error
     */
    protected HtmlPage getDocumentLibraryPage(final String siteName) throws PageException
    {
        DashBoardPage dashBoard = drone.getCurrentPage().render();
        MySitesDashlet dashlet = dashBoard.getDashlet("my-sites").render();

        SiteDashboardPage site = dashlet.selectSite(siteName).click().render();
        return site.getSiteNav().selectSiteDocumentLibrary();
    }
    
    /**
     * Prepare test by getting the drone to the correct page.
     * 
     * @param fileName String file name
     * @return {@link DocumentDetailsPage} page object
     * @throws PageException if error
     */
    protected HtmlPage selectDocument(File file) throws PageException
    {
        DocumentLibraryPage docsPage = (DocumentLibraryPage) drone.getCurrentPage();
        return docsPage.selectFile(file.getName()).render();
    }
}
