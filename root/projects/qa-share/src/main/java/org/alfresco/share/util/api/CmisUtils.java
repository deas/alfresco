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
package org.alfresco.share.util.api;

import java.io.IOException;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.opencmis.CMISDispatcherRegistry.Binding;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.filestore.FileContentWriter;
import org.alfresco.rest.api.tests.client.PublicApiClient.CmisSession;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.api.tests.client.data.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.share.enums.CMISBinding;
import org.alfresco.util.GUID;
import org.alfresco.util.TempFileProvider;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Cmis Utils
 * 
 * @author Meenal Bhave
 */
public class CmisUtils extends PublicAPIAbstract
{

    private static Log logger = LogFactory.getLog(CmisUtils.class);

    /**
     * Creates a CMIS session using the publicApiClient
     * 
     * @param CMISBinding CMIS binding and Version
     * @param authUser
     * @param domain
     */    
    public CmisSession getCmisSession(CMISBinding cmisBinding, String authUser, String domain)
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        CmisSession cmisSession = null;

        switch (cmisBinding)
        {
            case ATOMPUB10:
                cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, "1.0");
                logger.info("Binding: atom 1.0");
                break;
            case ATOMPUB11:
                cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, "1.1");
                logger.info("Binding: atom 1.1");
                break;
            case BROWSER11:
                cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, "1.1");
                logger.info("Binding: browser 1.1");
                break;
            default:
                // cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, "1.0");
                logger.info("Binding: Not specified");
        }        

        return cmisSession;        
    }
    
    /**
     * Creates a folder using CMIS
     * 
     * @param CMISBinding CMIS binding and Version
     * @param authUser
     * @param forUser
     * @param domain
     * @param parentFolderPath
     * @param properties
     */
    public Folder createFolder(CMISBinding cmisBinding, String authUser, String forUser, String domain, String parentFolderPath, Map<String, String> properties)            
    {        
        CmisSession cmisSession = getCmisSession(cmisBinding, authUser, domain);     
        Session session = cmisSession.getCMISSession();
        Folder rootFolder = session.getRootFolder();
        
        logger.info("Root folder Id: " + rootFolder.getId());
        logger.info("Root folder Type: " + rootFolder.getType());

        System.out.println("Created By: " + rootFolder.getName());

        Folder documentLibrary = (Folder) cmisSession.getObjectByPath("/Sites/" + parentFolderPath + "/documentLibrary");
        Folder f = documentLibrary.createFolder(properties);
        Folder subF = f.createFolder(properties);
        
        Assert.assertEquals("1 subfolder expected", 1, f.getChildren().getTotalNumItems());
        Assert.assertEquals("no children expected", 0, subF.getChildren().getTotalNumItems());
        
        Assert.assertEquals("Incorrect folderName", properties.get(PropertyIds.NAME), f.getName());
        Assert.assertEquals("Incorrect folderName", properties.get(PropertyIds.NAME), subF.getName());
        
        Assert.assertTrue("Incorrect Created By", forUser.equalsIgnoreCase(f.getCreatedBy()));  
        
        return f;
    }
    
    /**
     * Creates a Document using CMIS
     * 
     * @param CMISBinding CMIS binding and Version
     * @param authUser
     * @param forUser
     * @param domain
     * @param parentFolderPath
     * @param DocumentName
     * @param properties
     * @throws IOException 
     */
    public Document createDocument(CMISBinding cmisBinding, String authUser, String forUser, String domain, String parentFolderPath, String docContent, Map<String, String> properties) throws IOException            
    {        
        CmisSession cmisSession = getCmisSession(cmisBinding, authUser, domain);        
        Session session = cmisSession.getCMISSession();
        
        Folder rootFolder = session.getRootFolder();

        System.out.println("Created By: " + rootFolder.getName());

        Folder documentLibrary = (Folder) session.getObjectByPath("/Sites/" + parentFolderPath + "/documentLibrary");

        // Document
        ContentStreamImpl fileContent = new ContentStreamImpl();
        {
            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
            writer.putContent(docContent);
            ContentReader reader = writer.getReader();
            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            fileContent.setStream(reader.getContentInputStream());
        }

        Document d = documentLibrary.createDocument(properties, fileContent, VersioningState.MAJOR);

        Assert.assertTrue("Incorrect Created By", forUser.equalsIgnoreCase(d.getCreatedBy()));
        
        ContentData content = cmisSession.getContent(d.getId());   
        logger.info(docContent.getBytes().toString());
        logger.info(content.getBytes().toString());

        return d;
    }
    
    /**
     * Creates a Document using CMIS
     * 
     * @param CMISBinding CMIS binding and Version
     * @param authUser
     * @param forUser
     * @param domain
     * @param parentFolderPath
     * @param DocumentName
     * @param properties
     * @throws IOException 
     */
    public Document CheckInDocument(CMISBinding cmisBinding, String authUser, String forUser, String domain, Document docToCheckIn, String docContent, Map<String, String> properties, Boolean majorVersion, String checkInComment) throws IOException            
    {        
        CmisSession cmisSession = getCmisSession(cmisBinding, authUser, domain);        
        Session session = cmisSession.getCMISSession();
        
        ObjectId d2WorkingCopy = docToCheckIn.checkOut();
        Document pwc = (Document)cmisSession.getObject(d2WorkingCopy.getId());
        
        // TODO: 
        d2WorkingCopy = pwc.checkIn(majorVersion, properties, streamContent(docContent, MimetypeMap.MIMETYPE_TEXT_PLAIN), checkInComment);
        
        return (Document) session.getObject(docToCheckIn.getId());        
    }
    
    public ContentStream streamContent(String docContent, String mimeType)
    {
        ContentStreamImpl fileContent = new ContentStreamImpl();
        {
            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
            writer.putContent(docContent);
            ContentReader reader = writer.getReader();
            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            fileContent.setStream(reader.getContentInputStream());
        }
        
        return fileContent;
    }
    
}
