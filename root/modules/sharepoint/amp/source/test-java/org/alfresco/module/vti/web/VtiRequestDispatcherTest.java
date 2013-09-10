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
package org.alfresco.module.vti.web;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.util.ApplicationContextHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests for the {@link VtiRequestDispatcher} class.
 * 
 * @author Matt Ward
 */
public class VtiRequestDispatcherTest
{
    private static ApplicationContext ctx;
    private VtiRequestDispatcher dispatcher;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private SiteService siteService;
    private FileFolderService fileFolderService;
    private String shortSiteId;
    private NodeRef docLib;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        ctx = ApplicationContextHelper.getApplicationContext();
    }

    @Before
    public void setUp() throws Exception
    {
        // The class under test
        dispatcher = ctx.getBean("vtiRequestDispatcher", VtiRequestDispatcher.class);
        fileFolderService = ctx.getBean("FileFolderService", FileFolderService.class);
        siteService = ctx.getBean("SiteService", SiteService.class);
        response = new MockHttpServletResponse();
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        
        // Create a site for use in these tests
        shortSiteId = "SharepointTest-" + UUID.randomUUID();        
        if (!siteService.hasSite(shortSiteId))
        {
            siteService.createSite("sitePreset1", shortSiteId, "Test site", "Sharepoint tests", SiteVisibility.PUBLIC);
            docLib = siteService.createContainer(shortSiteId, SiteService.DOCUMENT_LIBRARY, ContentModel.TYPE_FOLDER, null);
        }
    }

    @Test
    public void canPutFileWithResourceTag() throws ServletException, IOException
    {
        String fileName = "test_ALF-18821.txt";
        // VtiIfHeaderAction PUT handler expects the file to have already been created (in most cases)
        FileInfo createdFile = fileFolderService.create(docLib, fileName, ContentModel.TYPE_CONTENT);
        
        request = new MockHttpServletRequest("PUT", "/alfresco/"+shortSiteId+"/documentLibrary/"+fileName);
        String fileContent = "This is the test file's content."; 
        request.setContent(fileContent.getBytes());
        request.addHeader("If", "(<rt:792589C1-2E8F-410E-BC91-4EF42DA88D3C@00862604462>)");
        
        dispatcher.service(request, response);
        
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        String retContent = fileFolderService.getReader(createdFile.getNodeRef()).getContentString();
        assertEquals(fileContent, retContent);
    }

}
