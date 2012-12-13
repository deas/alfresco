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
package org.alfresco.share.site;

import org.alfresco.share.AbstractTest;
import org.alfresco.webdrone.share.DashBoardPage;
import org.alfresco.webdrone.share.site.CreateSitePage;
import org.alfresco.webdrone.share.site.SitePage;
/**
 * Abstract to all site related pre test setups and
 * helper methods for running tests on site based pages.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class AbstractSiteTest extends AbstractTest
{
    /**
     * Create site helper method.
     * @param siteName String name of the site
     * @return {@link SitePage} page response of site creation
     * @throws Exception if error
     */
    public static SitePage createSite(final String siteName) throws Exception
    {
        DashBoardPage dashBoard = loginAs(username, password);
        CreateSitePage createSite = dashBoard.getNav().selectCreateSite();
        return createSite.createNewSite(siteName).render();
    }
}
