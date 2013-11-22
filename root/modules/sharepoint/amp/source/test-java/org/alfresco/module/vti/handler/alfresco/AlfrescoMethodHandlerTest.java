package org.alfresco.module.vti.handler.alfresco;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.handler.MethodHandler;
import org.alfresco.module.vti.web.VtiRequestDispatcher;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.webdav.ActivityPoster;
import org.alfresco.repo.webdav.WebDAVServerException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.service.cmr.webdav.WebDavService;
import org.alfresco.util.ApplicationContextHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@RunWith(MockitoJUnitRunner.class)
public class AlfrescoMethodHandlerTest
{
    // The class under test.
    private MethodHandler handler;
    private static ApplicationContext ctx;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private SiteService siteService;
    private FileFolderService fileFolderService;
    private String shortSiteId;
    private NodeRef docLib;
    private @Mock ActivityPoster mockActivityPoster;
    private @Mock WebDavService mockDavService;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        ctx = ApplicationContextHelper.getApplicationContext();
    }

    @Before
    public void setUp() throws Exception
    {
        // The class under test
        handler = (MethodHandler) ctx.getBean("vtiHandler");
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
        String fileName = "test_file.txt";
        // VtiIfHeaderAction PUT handler expects the file to have already been created (in most cases)
        FileInfo createdFile = fileFolderService.create(docLib, fileName, ContentModel.TYPE_CONTENT);
        
        request = new MockHttpServletRequest("PUT", "/alfresco/"+shortSiteId+"/documentLibrary/"+fileName);
        String fileContent = "This is the test file's content."; 
        request.setContent(fileContent.getBytes());
        request.addHeader("If", "(<rt:792589C1-2E8F-410E-BC91-4EF42DA88D3C@00862604462>)");
        
        handler.putResource(request, response);
        
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        String retContent = fileFolderService.getReader(createdFile.getNodeRef()).getContentString();
        assertEquals(fileContent, retContent);
    }
    
    @Test
    public void putFileResultsInActivityPost() throws ServletException, IOException, WebDAVServerException
    {
        // Inject a mock activity poster, so that we can verify it is called.
        AlfrescoMethodHandler handlerTarget = (AlfrescoMethodHandler) ctx.getBean("vtiHandlerTarget");
        handlerTarget.setActivityPoster(mockActivityPoster);
        
        // Inject a mock WebDavService that always states activity posting is enabled.
        when(mockDavService.activitiesEnabled()).thenReturn(true);
        handlerTarget.setDavService(mockDavService);
        
        String fileName = "test_file.txt";
        // VtiIfHeaderAction PUT handler expects the file to have already been created (in most cases)
        FileInfo createdFile = fileFolderService.create(docLib, fileName, ContentModel.TYPE_CONTENT);
        
        request = new MockHttpServletRequest("PUT", "/alfresco/"+shortSiteId+"/documentLibrary/"+fileName);
        String fileContent = "This is the test file's content."; 
        request.setContent(fileContent.getBytes());
        request.addHeader("If", "(<rt:792589C1-2E8F-410E-BC91-4EF42DA88D3C@00862604462>)");
        
        // PUT the file
        handler.putResource(request, response);
        
        // Check the activity was posted
        verify(mockActivityPoster).postFileFolderUpdated(shortSiteId, TenantService.DEFAULT_DOMAIN, createdFile);
        
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        String retContent = fileFolderService.getReader(createdFile.getNodeRef()).getContentString();
        assertEquals(fileContent, retContent);
    }
}
