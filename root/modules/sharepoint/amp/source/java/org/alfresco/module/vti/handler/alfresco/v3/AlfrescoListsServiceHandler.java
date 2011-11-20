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

package org.alfresco.module.vti.handler.alfresco.v3;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.handler.ListsServiceHandler;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.metadata.model.ListBean;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Alfresco implementation of ListsServiceHandler
 * 
 * TODO Merge this into {@link AlfrescoListServiceHandler}
 * 
 * @author PavelYur
 */
public class AlfrescoListsServiceHandler implements ListsServiceHandler
{
    private static Log logger = LogFactory.getLog(AlfrescoListsServiceHandler.class);
    
    private SiteService siteService;
    private FileFolderService fileFolderService;
    private NodeService nodeService;
    private TransactionService transactionService;
    
    /**
     * Set site service
     * 
     * @param siteService the site service to set ({@link SiteService})
     */
    public void setSiteService(SiteService siteService)
    {
        this.siteService = siteService;
    }
    
    /**
     * Set file folder service
     * 
     * @param fileFolderService the file folder service to set ({@link FileFolderService})
     */
    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }
    
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
     * Set transaction service
     * 
     * @param transactionService the transaction service to set ({@link TransactionService})
     */
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }
    
    /**
     * @see org.alfresco.module.vti.handler.ListsServiceHandler#getListCollection(String)
     */
    public List<ListBean> getListCollection(final String siteName)
    {
        List<ListBean> results = 
            transactionService.getRetryingTransactionHelper().doInTransaction(
                new RetryingTransactionCallback<List<ListBean>>()
                {

                    @Override
                    public List<ListBean> execute() throws Throwable
                    {
                        List<ListBean> results = new ArrayList<ListBean>();

                        SiteInfo siteInfo = siteService.getSite(siteName);

                        if (siteInfo == null)
                        {
                            throw new VtiHandlerException(VtiHandlerException.BAD_URL);
                        }

                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Looking for containers in site: " + siteName);
                        }

                        List<FileInfo> folders = fileFolderService.listFolders(siteInfo.getNodeRef());

                        for (FileInfo folder : folders)
                        {
                            if (nodeService.hasAspect(folder.getNodeRef(), SiteModel.ASPECT_SITE_CONTAINER))
                            {
                                results.add(buildListBean(folder));
                            }
                        }

                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Found " + results.size() + " containers in site " + siteName);
                        }

                        return results;
                    }
                }, true, false);

        return results;
    }
    
    /**
     * @see org.alfresco.module.vti.handler.ListsServiceHandler#getList(String)
     */
    public ListBean getList(String listName)
    {
        if (listName.startsWith("{"))
        {
            listName = listName.substring(1);
        }
        
        if (listName.endsWith("}"))
        {
            listName = listName.substring(0, listName.length() - 1);
        }
        
        final String safeListName = listName;
        
        FileInfo listFileInfo = 
            transactionService.getRetryingTransactionHelper().doInTransaction(
                new RetryingTransactionCallback<FileInfo>()
                {

                    @Override
                    public FileInfo execute() throws Throwable
                    {
                        return fileFolderService.getFileInfo(new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, safeListName));
                    }
                    
                }, true, false);            
        
        return buildListBean(listFileInfo);
    }

    private ListBean buildListBean(FileInfo fileInfo)
    {
        ListBean result = new ListBean();
        result.setId("{" + fileInfo.getNodeRef().getId() + "}");
        result.setTitle((String)fileInfo.getProperties().get(ContentModel.PROP_NAME));
        result.setName("{" + fileInfo.getNodeRef().getId() + "}");
        result.setDescription((String)fileInfo.getProperties().get(ContentModel.PROP_DESCRIPTION));
        return result;
    }
}
