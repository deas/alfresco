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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.metadata.dic.DocumentStatus;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.version.VersionModel;
import org.alfresco.repo.webdav.LockInfoImpl;
import org.alfresco.repo.webdav.WebDAV;
import org.alfresco.repo.webdav.WebDAVLockService;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionType;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

/**
 * Tests for the {@link VtiDocumentHelper} class.
 * 
 * @author pavel.yurkevich
 *
 */
public class VtiDocumentHelperTest
{
    private static ApplicationContext ctx;
    private VtiDocumentHelper documentHelper;
    private SearchService searchService;
    private FileFolderService fileFolderService;
    private CheckOutCheckInService checkOutCheckInService;
    private WebDAVLockService webDAVLockService;
    private ContentService contentService;
    private TransactionService transactionService;

    private FileInfo testFileInfo;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        ctx = ApplicationContextHelper.getApplicationContext();
    }

    @Before
    public void setUp() throws Exception
    {
        documentHelper = ctx.getBean("vtiDocumentHelper", VtiDocumentHelper.class);
        searchService = ctx.getBean("SearchService", SearchService.class);
        fileFolderService = ctx.getBean("FileFolderService", FileFolderService.class);
        checkOutCheckInService = ctx.getBean("CheckoutCheckinService", CheckOutCheckInService.class);
        webDAVLockService = ctx.getBean("webDAVLockService", WebDAVLockService.class);
        contentService = ctx.getBean("ContentService", ContentService.class);
        transactionService = ctx.getBean("transactionService", TransactionService.class);

        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());

        testFileInfo = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<FileInfo>()
        {
            @Override
            public FileInfo execute() throws Throwable
            {
                // find "Company Home" 
                StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
                ResultSet resultSet = searchService.query(storeRef, SearchService.LANGUAGE_LUCENE, "PATH:\"/app:company_home\"");
                NodeRef companyHome = resultSet.getNodeRef(0);
                resultSet.close();

                // create test document
                FileInfo newContent = fileFolderService.create(companyHome, "test" + System.currentTimeMillis(), ContentModel.TYPE_CONTENT);

                // write content to test document
                ContentWriter writer = contentService.getWriter(newContent.getNodeRef(), ContentModel.PROP_CONTENT, true);
                writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
                writer.setEncoding("UTF-8");
                String text = "Test content";
                writer.putContent(text);

                return newContent;
            }
        });
    }

    @After
    public void tearDown()
    {
        AuthenticationUtil.clearCurrentSecurityContext();
    }

    @Test
    public void testDocumentStatus()
    {
        final NodeRef testNodeRef = testFileInfo.getNodeRef();
        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
        {
            @Override
            public Object execute() throws Throwable
            {
                DocumentStatus status = null;

                status = documentHelper.getDocumentStatus(testNodeRef);
                assertEquals(DocumentStatus.NORMAL, status);

                LockInfoImpl lockInfo = new LockInfoImpl();
                lockInfo.setDepth("1");
                lockInfo.setOwner(AuthenticationUtil.getAdminUserName());
                lockInfo.setExclusiveLockToken(WebDAV.makeLockToken(testNodeRef, AuthenticationUtil.getAdminUserName()));
                lockInfo.setTimeoutSeconds(WebDAV.TIMEOUT_24_HOURS);
                lockInfo.setScope(WebDAV.XML_EXCLUSIVE);

                // lock the node, emulates LOCK webdav request
                webDAVLockService.lock(testNodeRef, lockInfo);

                status = documentHelper.getDocumentStatus(testNodeRef);
                assertEquals(DocumentStatus.SHORT_CHECKOUT_OWNER, status);

                // switch authentication
                AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());

                status = documentHelper.getDocumentStatus(testNodeRef);
                assertEquals(DocumentStatus.SHORT_CHECKOUT, status);

                AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());

                // unlock node, emulate unlock during CheckOutFile soap method
                webDAVLockService.unlock(testNodeRef);

                status = documentHelper.getDocumentStatus(testNodeRef);
                assertEquals(DocumentStatus.NORMAL, status);

                // check out document for offline editing
                NodeRef workingCopy = checkOutCheckInService.checkout(testNodeRef);
                
                status = documentHelper.getDocumentStatus(testNodeRef);
                assertEquals(DocumentStatus.LONG_CHECKOUT_OWNER, status);

                // switch authentication
                AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());

                status = documentHelper.getDocumentStatus(testNodeRef);
                assertEquals(DocumentStatus.LONG_CHECKOUT, status);

                AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());

                // Set the properties on the new version
                Map<String, Serializable> versionProperties = new HashMap<String, Serializable>(1, 1.0f);
                versionProperties.put(Version.PROP_DESCRIPTION, "Test checkin comment");
                versionProperties.put(VersionModel.PROP_VERSION_TYPE, VersionType.MAJOR);

                // finish offline editing
                checkOutCheckInService.checkin(workingCopy, versionProperties);

                status = documentHelper.getDocumentStatus(testNodeRef);
                assertEquals(DocumentStatus.NORMAL, status);

                return null;
            }
        });
    }
}
