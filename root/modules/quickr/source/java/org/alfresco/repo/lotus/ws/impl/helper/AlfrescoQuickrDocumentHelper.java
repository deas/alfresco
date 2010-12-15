/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * and Open Source Software ("FLOSS") applications as described in Alfresco's
 * FLOSS exception.  You should have recieved a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.repo.lotus.ws.impl.helper;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockStatus;
import org.alfresco.service.cmr.lock.LockType;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;

/**
 * @author Eugene Zheleznyakov
 */
public class AlfrescoQuickrDocumentHelper
{
    private LockService lockService;

    private NodeService nodeService;

    private CheckOutCheckInService checkOutCheckInService;

    public void setLockService(LockService lockService)
    {
        this.lockService = lockService;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setCheckOutCheckInService(CheckOutCheckInService checkOutCheckInService)
    {
        this.checkOutCheckInService = checkOutCheckInService;
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
            if (LockType.valueOf((String) nodeService.getProperty(nodeRef, ContentModel.PROP_LOCK_TYPE)).equals(LockType.WRITE_LOCK))
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
                    if (lockStatus.equals(LockStatus.LOCKED))
                    {
                        status = DocumentStatus.READONLY;
                    }
                    else
                    {
                        // There is no working copy yet.
                        status = DocumentStatus.LONG_CHECKOUT_OWNER;
                    }
                }
            }
        }

        return status;
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
     * Check document on long term checkout
     * 
     * @param documentStatus status of document ({@link DocumentStatus})
     * @return <i>true</i>, if document is long term checkout; otherwise, <i>false</i>
     */
    public static boolean isLongCheckedout(DocumentStatus documentStatus)
    {
        return documentStatus.equals(DocumentStatus.LONG_CHECKOUT) || documentStatus.equals(DocumentStatus.LONG_CHECKOUT_OWNER);
    }
}
