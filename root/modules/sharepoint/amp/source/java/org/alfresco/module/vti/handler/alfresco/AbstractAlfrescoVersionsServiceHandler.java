/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.handler.VersionsServiceHandler;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.metadata.dic.VtiError;
import org.alfresco.module.vti.metadata.model.DocumentVersionBean;
import org.alfresco.repo.version.VersionModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionDoesNotExistException;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.cmr.version.VersionType;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Alfresco implementation of VersionsServiceHandler interface
 * 
 * @author PavelYur
 */
public abstract class AbstractAlfrescoVersionsServiceHandler implements VersionsServiceHandler
{

    private static Log logger = LogFactory.getLog(AbstractAlfrescoVersionsServiceHandler.class);

    protected NodeService nodeService;
    protected FileFolderService fileFolderService;
    protected VersionService versionService;
    protected TransactionService transactionService;

    protected VtiPathHelper pathHelper;

    /**
     * Set node service
     * 
     * @param nodeService the node service to set ({@link NodeService})
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Set file folder service
     * 
     * @param fileFolderService the file-folder service to set ({@link FileFolderService})
     */
    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }

    /**
     * Set version service
     * 
     * @param versionService the version service to set ({@link VersionService})
     */
    public void setVersionService(VersionService versionService)
    {
        this.versionService = versionService;
    }

    /**
     * Set transaction service
     * 
     * @param transactionService the transaction service to set ({@link TransactionService})
     */
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    /**
     * Set path helper
     * 
     * @param pathHelper the path helper to set ({@link VtiPathHelper})
     */
    public void setPathHelper(VtiPathHelper pathHelper)
    {
        this.pathHelper = pathHelper;
    }

    /**
     * @see org.alfresco.module.vti.handler.VersionsServiceHandler#getVersions(java.lang.String)
     */
    public List<DocumentVersionBean> getVersions(String fileName) throws FileNotFoundException
    {
        if (logger.isDebugEnabled())
            logger.debug("Method with name 'getVersions' is started.");

        FileInfo documentFileInfo = pathHelper.resolvePathFileInfo(fileName);
        
        // Asking for a non existent file is valid for listing
        if(documentFileInfo == null)
        {
           throw new FileNotFoundException(fileName);
        }

        // Ensure it's a valid thing to query versions for
        if (logger.isDebugEnabled())
            logger.debug("Asserting documentFileInfo for file '" + fileName + "'.");
        assertDocument(documentFileInfo);

        // Fetch all the version details
        List<DocumentVersionBean> result = getVersions(documentFileInfo);

        if (logger.isDebugEnabled())
            logger.debug("Method with name 'getVersions' is finished.");

        return result;
    }

    /**
     * @see org.alfresco.module.vti.handler.VersionsServiceHandler#restoreVersion(java.lang.String, java.lang.String)
     */
    public List<DocumentVersionBean> restoreVersion(String fileName, String fileVersion)
    {
        if (logger.isDebugEnabled())
            logger.debug("Method with name 'restoreVersion' is started.");

        FileInfo documentFileInfo = pathHelper.resolvePathFileInfo(fileName);

        assertDocument(documentFileInfo);
        UserTransaction tx = transactionService.getUserTransaction(false);
        try
        {
            tx.begin();

            Map<String, Serializable> props = new HashMap<String, Serializable>(1, 1.0f);
            props.put(Version.PROP_DESCRIPTION, "");
            props.put(VersionModel.PROP_VERSION_TYPE, VersionType.MAJOR);

            if (logger.isDebugEnabled())
                logger.debug("Creating a new version for '" + fileName + "'.");
            versionService.createVersion(documentFileInfo.getNodeRef(), props);

            String alfrescoVersionLabel = VtiUtils.toAlfrescoVersionLabel(fileVersion);
            VersionHistory versionHistory = versionService.getVersionHistory(documentFileInfo.getNodeRef());
            Version version = versionHistory.getVersion(alfrescoVersionLabel);
            if (logger.isDebugEnabled())
                logger.debug("Reverting version '" + fileVersion + " for '" + fileName + "'.");
            versionService.revert(documentFileInfo.getNodeRef(), version);

            tx.commit();
        }
        catch (Exception e)
        {
            try
            {
                tx.rollback();
            }
            catch (Exception tex)
            {
            }
            if (logger.isDebugEnabled())
                logger.debug("Error: version was not restored. ", e);
            throw new RuntimeException("Version was not restored. May be you don't have appropriate permissions.");
        }

        List<DocumentVersionBean> result = getVersions(documentFileInfo);

        if (logger.isDebugEnabled())
            logger.debug("Method with name 'restoreVersion' is finished.");

        return result;
    }

    /**
     * @see org.alfresco.module.vti.handler.VersionsServiceHandler#deleteVersion(java.lang.String, java.lang.String)
     */
    public List<DocumentVersionBean> deleteVersion(String fileName, String fileVersion)
    {
       if (logger.isDebugEnabled())
          logger.debug("Method with name 'deleteVersion' is started for " + fileVersion + " of " + fileName);

      FileInfo documentFileInfo = pathHelper.resolvePathFileInfo(fileName);

      assertDocument(documentFileInfo);
      UserTransaction tx = transactionService.getUserTransaction(false);
      try
      {
          tx.begin();
          
          VersionHistory history = versionService.getVersionHistory(documentFileInfo.getNodeRef());
          if(history == null)
          {
              // Versioning is disabled
              throw new VtiHandlerException(VtiError.V_VERSION_NOT_FOUND);
          }
          
          Version version = history.getVersion(fileVersion);

          if(logger.isDebugEnabled())
          {
             logger.debug("Deleteing version " + version);
          }
          versionService.deleteVersion(documentFileInfo.getNodeRef(), version);

          tx.commit();
      }
      catch (Exception e)
      {
          try
          {
              tx.rollback();
          }
          catch (Exception tex)
          {
          }
          if (logger.isDebugEnabled())
              logger.debug("Error: version was not deleted. ", e);
          
          if(e instanceof VersionDoesNotExistException)
          {
             throw new VtiHandlerException(VtiError.V_VERSION_NOT_FOUND); 
          }
          if(e instanceof VtiHandlerException)
          {
             throw (VtiHandlerException)e;
          }
          throw new RuntimeException("Version was not deleted. May be you don't have appropriate permissions.");
      }

      List<DocumentVersionBean> result = getVersions(documentFileInfo);

      if (logger.isDebugEnabled())
          logger.debug("Method with name 'deleteVersion' is finished.");

      return result;
    }

    /**
     * Asserts file info for existent document
     * 
     * @param documentFileInfo document file info ({@link FileInfo})
     */
    protected void assertDocument(FileInfo documentFileInfo)
    {
        if (documentFileInfo == null)
        {
            if (logger.isDebugEnabled())
                logger.debug("Error: That document doesn't exist.");
            throw new RuntimeException("That document doesn't exist");
        }

        if (documentFileInfo.isFolder() == true)
        {
            if (logger.isDebugEnabled())
                logger.debug("Error: It isn't document. It is folder.");
            throw new RuntimeException("It isn't document. It is folder");
        }
    }

    /**
     * Returns DocumentVersionBean list for file info
     * 
     * @param documentFileInfo file info ({@link FileInfo})
     * @return List<DocumentVersionBean> list of DocumentVersionBean
     */
    protected List<DocumentVersionBean> getVersions(FileInfo documentFileInfo)
    {
        if (logger.isDebugEnabled())
            logger.debug("Getting all versions for '" + documentFileInfo.getName() + "'.");

        List<DocumentVersionBean> versions = new LinkedList<DocumentVersionBean>();
        String id = documentFileInfo.getNodeRef().toString();

        if (logger.isDebugEnabled())
            logger.debug("Getting current version.");
        Version currentVersion = versionService.getCurrentVersion(documentFileInfo.getNodeRef());
        if (currentVersion != null)
        {
            if (logger.isDebugEnabled())
                logger.debug("Adding current version to result.");

            versions.add(getDocumentVersionInfo(currentVersion, id));

            boolean currentFound = false;
            for (Version version : versionService.getVersionHistory(documentFileInfo.getNodeRef()).getAllVersions())
            {
                if (currentFound == false && currentVersion.getVersionLabel().equals(version.getVersionLabel()))
                {
                    currentFound = true;
                }
                else
                {
                    if (logger.isDebugEnabled())
                        logger.debug("Adding version '" + version.getVersionLabel() + "' to result.");
                    versions.add(getDocumentVersionInfo(version, id));
                }
            }
        }
        else
        {
            if (logger.isDebugEnabled())
                logger.debug("Current version doesn't exist. Creating a new current version.");
            versions.add(getDocumentVersionInfo(documentFileInfo));
        }

        return versions;
    }

    /**
     * Get document version bean by version
     * 
     * @param version version ({@link Version})
     * @param id the file id
     * @return DocumentVersionBean version bean
     */
    protected DocumentVersionBean getDocumentVersionInfo(Version version, String id)
    {
        DocumentVersionBean docVersion = new DocumentVersionBean();

        NodeRef versionNodeRef = version.getFrozenStateNodeRef();
        FileInfo documentFileInfo = fileFolderService.getFileInfo(versionNodeRef);

        docVersion.setId(id);
        docVersion.setUrl(generateDownloadURL(documentFileInfo.getNodeRef(), documentFileInfo.getName()));
        docVersion.setVersion(version.getVersionLabel());
        docVersion.setCreatedBy(version.getFrozenModifier());
        docVersion.setCreatedTime(VtiUtils.formatVersionDate(version.getFrozenModifiedDate()));
        ContentData content = (ContentData) nodeService.getProperty(version.getFrozenStateNodeRef(), ContentModel.PROP_CONTENT);
        
        if(content != null)
        {
           docVersion.setSize(content.getSize());
        }
        else
        {
           logger.info(
                 "The frozen version " + version.getFrozenStateNodeRef() + " of " + version.getVersionedNodeRef() + 
                 " at " + version.getVersionLabel() + " has no content property, so no size can be given for the version"
           );
        }

        String versionDescription = version.getDescription();
        if (versionDescription != null)
        {
            docVersion.setComments(versionDescription);
        }
        else
        {
            docVersion.setComments("");
        }

        return docVersion;
    }

    /**
     * Generate URL to download
     * 
     * @param ref ({@link NodeRef})
     * @param name name
     * @return download URL
     */
    private String generateDownloadURL(NodeRef ref, String name)
    {
        return "/_vti_history/" + ref.toString() + "/" + name;
    }

    /**
     * Get document version bean for document without version history
     * 
     * @param documentFileInfo document file info ({@link FileInfo})
     * @return DocumentVersionBean document version bean
     */
    protected DocumentVersionBean getDocumentVersionInfo(FileInfo documentFileInfo)
    {
        DocumentVersionBean docVersion = new DocumentVersionBean();

        docVersion.setId(documentFileInfo.getNodeRef().toString());
        docVersion.setUrl("/" + pathHelper.toUrlPath(documentFileInfo));
        docVersion.setVersion("1.0");
        docVersion.setCreatedBy((String) documentFileInfo.getProperties().get(ContentModel.PROP_CREATOR));
        docVersion.setCreatedTime(VtiUtils.formatVersionDate(documentFileInfo.getCreatedDate()));
        docVersion.setSize(documentFileInfo.getContentData().getSize());
        docVersion.setComments("");

        return docVersion;
    }
}
