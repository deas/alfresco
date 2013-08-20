/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.module.vti.handler.alfresco;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.alfresco.repo.SessionUser;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.apache.commons.httpclient.HttpException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests for the {@link AlfrescoDwsServiceHandler} class.
 * 
 * @author Matt Ward
 */
@RunWith(MockitoJUnitRunner.class)
public class AlfrescoDwsServiceHandlerTest
{
    private AlfrescoDwsServiceHandler handler;
    private @Mock SessionUser user;
    private ShareUtilsEx shareUtils;
    private @Mock SiteService siteService;
    
    @Before
    public void setUp() throws Exception
    {
        shareUtils = new ShareUtilsEx();
        handler = new AlfrescoDwsServiceHandler();
        handler.setShareUtils(shareUtils);
        handler.setSiteService(siteService);
    }

    @Test
    public void sanitizeShortSiteName()
    {
        assertEquals("myshortname", handler.sanitizeShortName("MyShortName"));
        assertEquals("my-short-name", handler.sanitizeShortName("My Short Name"));
        assertEquals("project-123-site-name", handler.sanitizeShortName("Project 123 Site-Name"));
    }

    @Test
    public void testDoCreateDws() throws HttpException, IOException
    {
        String dwsName = "Project 123 Site-Name";
        String title = "Project 123's Proper Site Title";
        
        handler.doCreateDws(dwsName, title, user);
        
        assertEquals("project-123-site-name", shareUtils.getSiteShortName());
        assertEquals("Project 123's Proper Site Title", shareUtils.getSiteTitle());
    }
    
    @Test
    public void testDoCreateDwsWhenShortNameAlreadyTaken() throws HttpException, IOException
    {
        // These names are said to be already taken...
        SiteInfo siteInfo = Mockito.mock(SiteInfo.class); // non-null SiteInfo
        Mockito.when(siteService.getSite("project-123-site-name")).thenReturn(siteInfo);
        Mockito.when(siteService.getSite("project-123-site-name_1")).thenReturn(siteInfo);
        Mockito.when(siteService.getSite("project-123-site-name_2")).thenReturn(siteInfo);
        String dwsName = "Project 123 Site-Name";
        String title = "Project 123's Proper Site Title";
        
        handler.doCreateDws(dwsName, title, user);
        
        // The next available name is chosen
        assertEquals("project-123-site-name_3", shareUtils.getSiteShortName());
        assertEquals("Project 123's Proper Site Title", shareUtils.getSiteTitle());
    }
    
    /**
     * As {@link ShareUtils} isn't an interface, Mockito cannot be used to
     * create a stub automatically - so subclassing here to create a stub.
     */
    private static class ShareUtilsEx extends ShareUtils
    {
        private String siteShortName;
        private String siteTitle;
        
        @Override
        public void createSite(SessionUser user, String sitePreset, String shortName, String title,
                    String description, boolean isPublic) throws HttpException, IOException
        {
            this.siteShortName = shortName;
            this.siteTitle = title;
        }

        public String getSiteShortName()
        {
            return this.siteShortName;
        }

        public String getSiteTitle()
        {
            return this.siteTitle;
        }
    }
}
