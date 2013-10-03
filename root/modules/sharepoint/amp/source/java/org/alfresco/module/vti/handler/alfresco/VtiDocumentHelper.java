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

import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.metadata.dic.DocumentStatus;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.webdav.LockInfo;
import org.alfresco.repo.webdav.WebDAVLockService;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;

/**
 * Helper for documents
 * 
 * @author Dmitry Lazurkin
 */
public class VtiDocumentHelper
{
    private NodeService nodeService;
    private CheckOutCheckInService checkOutCheckInService;
    private WebDAVLockService webDAVLockService;
    private LockService lockService;

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
     * Set checkout-checkin service
     * 
     * @param checkOutCheckInService the checkout-checkin service to set ({@link CheckOutCheckInService})
     */
    public void setCheckOutCheckInService(CheckOutCheckInService checkOutCheckInService)
    {
        this.checkOutCheckInService = checkOutCheckInService;
    }

    /**
     * @param webDAVLockService
     *            the webDAVLockService to set
     */
    public void setWebDAVLockService(WebDAVLockService webDAVLockService)
    {
        this.webDAVLockService = webDAVLockService;
    }

    /**
     * 
     * @param lockService the lockService to set
     */
    public void setLockService(LockService lockService)
    {
        this.lockService = lockService;
    }

    /**
     * Returns document status for node reference
     * 
     * @param nodeRef node reference ({@link NodeRef})
     * @return DocumentStatus document status
     */
    public DocumentStatus getDocumentStatus(NodeRef nodeRef)
    {
        DocumentStatus status = DocumentStatus.NORMAL;

        LockInfo lockInfo = webDAVLockService.getLockInfo(nodeRef);
        NodeRef workingCopyNodeRef = checkOutCheckInService.getWorkingCopy(nodeRef);

        if (workingCopyNodeRef != null)
        {
            status = isWorkingCopyOwner(workingCopyNodeRef) ? DocumentStatus.LONG_CHECKOUT_OWNER : DocumentStatus.LONG_CHECKOUT;
            return status;
        }

        if (isShortCheckedout(nodeRef))
        {
            // short-term checkout
            if (lockInfo != null && lockInfo.getOwner().equals(AuthenticationUtil.getFullyAuthenticatedUser()))
            {
                status = DocumentStatus.SHORT_CHECKOUT_OWNER;
            }
            else
            {
                status = DocumentStatus.SHORT_CHECKOUT;
            }
            return status;
        }

        return status;
    }

    /**
     * Determines short-term checkout on node reference
     * 
     * @param nodeRef node reference ({@link NodeRef})
     * @return <b>true</b> if document is checked out, <b>false</b> otherwise
     */
    public boolean isShortCheckedout(NodeRef nodeRef)
    {
        LockInfo lockInfo = webDAVLockService.getLockInfo(nodeRef);

        return lockInfo != null && lockInfo.isLocked() && !lockInfo.isExpired();
    }

    /**
     * Determines long-term checkout on node reference
     * 
     * @param nodeRef node reference ({@link NodeRef})
     * @return <b>true</b> if document is checked out, <b>else</b> otherwise
     */
    public boolean isLongCheckedout(NodeRef nodeRef)
    {
        NodeRef workingCopyNodeRef = checkOutCheckInService.getWorkingCopy(nodeRef);

        boolean isLongCheckedout = false;

        if (workingCopyNodeRef != null)
        {
            isLongCheckedout = true;
        }

        return isLongCheckedout;
    }

    /**
     * Check document on checkout
     * 
     * @param documentStatus status of document ({@link DocumentStatus})
     * @return <i>true</i>, if document is checkout; otherwise, <i>false</i>
     */
    public static boolean isCheckedout(DocumentStatus documentStatus)
    {
        return documentStatus.equals(DocumentStatus.NORMAL) == false && documentStatus.equals(DocumentStatus.READONLY) == false;
    }

    /**
     * Check document on long term checkout
     * 
     * @param documentStatus status of document ({@link DocumentStatus})
     * @return <i>true</i>, if document is long term checkout; otherwise, <i>false</i>
     */
    public static boolean isLongCheckedout(DocumentStatus documentStatus)
    {
        return documentStatus.equals(DocumentStatus.LONG_CHECKOUT) || documentStatus.equals(DocumentStatus.LONG_CHECKOUT_OWNER);
    }

    /**
     * Check document on short term checkout
     * 
     * @param documentStatus status of document ({@link DocumentStatus})
     * @return <i>true</i>, if document is short term checkout; otherwise, <i>false</i>
     */
    public static boolean isShortCheckedout(DocumentStatus documentStatus)
    {
        return documentStatus.equals(DocumentStatus.SHORT_CHECKOUT) || documentStatus.equals(DocumentStatus.SHORT_CHECKOUT_OWNER);
    }

    /**
     * Check document on owner checkout
     * 
     * @param documentStatus status of document ({@link DocumentStatus})
     * @return <i>true</i>, if document is owner checkout; otherwise, <i>false</i>
     */
    public static boolean isCheckoutOwner(DocumentStatus documentStatus)
    {
        return documentStatus.equals(DocumentStatus.LONG_CHECKOUT_OWNER) || documentStatus.equals(DocumentStatus.SHORT_CHECKOUT_OWNER);
    }

    /**
     * Checks match filename for file filters
     * 
     * @param fileName file name
     * @param fileDialogFilterValue list of file filters
     * @return <i>true</i>, if file name matches at least one file filter; otherwise, <i>false</i>
     */
    public static boolean applyFilters(String fileName, List<String> fileDialogFilterValue)
    {
        fileName = fileName.toLowerCase();

        for (String filter : fileDialogFilterValue)
        {
            char[] globalPat = filter.toLowerCase().toCharArray();
            int len = globalPat.length;

            StringBuilder regexPat = new StringBuilder(len * 3);

            for (int i = 0; i < len; i++)
            {
                switch (globalPat[i])
                {
                case '*':
                    regexPat.append(".*");
                    break;

                case '?':
                    regexPat.append(".");
                    break;

                case '.':
                    regexPat.append("\\.");
                    break;

                default:
                    regexPat.append(globalPat[i]);
                    break;
                }
            }

            if (fileName.matches(regexPat.toString()))
            {
                return true;
            }
        }

        return false;
    }

    /*
     * Tests whether the current user is owner of working copy
     */
    private boolean isWorkingCopyOwner(NodeRef workingCopy)
    {
        String ownerUsername = (String) nodeService.getProperty(workingCopy, ContentModel.PROP_WORKING_COPY_OWNER);

        return ownerUsername.equals(AuthenticationUtil.getFullyAuthenticatedUser());
    }
}
