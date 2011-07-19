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
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockStatus;
import org.alfresco.service.cmr.lock.LockType;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;

/**
 * Helper for documents
 * 
 * @author Dmitry Lazurkin
 */
public class VtiDocumentHepler
{
    private NodeService nodeService;
    private CheckOutCheckInService checkOutCheckInService;
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
     * Lock service
     * 
     * @param lockService the lock service to set ({@link LockService})
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

        LockStatus lockStatus = lockService.getLockStatus(nodeRef);

        if (lockStatus.equals(LockStatus.LOCKED) || lockStatus.equals(LockStatus.LOCK_OWNER))
        {
            if (LockType.valueOf((String) nodeService.getProperty(nodeRef, ContentModel.PROP_LOCK_TYPE)).equals(LockType.WRITE_LOCK) == true)
            {
                // short-term checkout
                if (lockStatus.equals(LockStatus.LOCKED))
                {
                    status = DocumentStatus.SHORT_CHECKOUT;
                }
                else
                {
                    status = DocumentStatus.SHORT_CHECKOUT_OWNER;
                }
            }
            else
            {
                NodeRef workingCopyNodeRef = checkOutCheckInService.getWorkingCopy(nodeRef);

                // checks for long-term checkout
                if (workingCopyNodeRef != null)
                {
                    // long-term checkout
                    String ownerUsername = (String) nodeService.getProperty(workingCopyNodeRef, ContentModel.PROP_WORKING_COPY_OWNER);
                    if (ownerUsername.equals(AuthenticationUtil.getFullyAuthenticatedUser()))
                    {
                        status = DocumentStatus.LONG_CHECKOUT_OWNER;
                    }
                    else
                    {
                        status = DocumentStatus.LONG_CHECKOUT;
                    }
                }
                else
                {
                    // just readonly document
                    status = DocumentStatus.READONLY;
                }
            }
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
        LockStatus lockStatus = lockService.getLockStatus(nodeRef);

        boolean isShortCheckedout = false;

        if (lockStatus.equals(LockStatus.LOCKED) || lockStatus.equals(LockStatus.LOCK_OWNER))
        {
            if (LockType.valueOf((String) nodeService.getProperty(nodeRef, ContentModel.PROP_LOCK_TYPE)).equals(LockType.WRITE_LOCK) == true)
            {
                isShortCheckedout = true;
            }
        }

        return isShortCheckedout;
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
}
