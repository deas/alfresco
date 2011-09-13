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
package org.alfresco.repo.transfer.fsr;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.transfer.TransferModel;
import org.alfresco.repo.transfer.TransferTargetImpl;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.transfer.TransferService;
import org.alfresco.util.BaseAlfrescoSpringTest;

public class FileTransferReceiverUnitTest extends BaseAlfrescoSpringTest
{

    //create ROOT_OF_TRANSFER > ROOT_FOLDER_TO_TRANSFER
    private final String ROOT_OF_TRANSFER = "ROOT_OF_TRANSFER";
    private final String ROOT_FOLDER_TO_TRANSFER = "ROOT_FOLDER_TO_TRANSFER";

    private TransferService transferService;
    private NodeService nodeService;
    private FileFolderService fileFolderService;

    /** Name of the target */
    public static final String TARGET_NAME = "ftr";



    /**
     * Called during the transaction setup
     */
    @SuppressWarnings(value={"deprecation"})
    protected void onSetUp() throws Exception
    {

        super.onSetUp();
        // Get the required services
        this.transferService = (TransferService)this.applicationContext.getBean("TransferService");
        this.nodeService = (NodeService) this.applicationContext.getBean("nodeService");
        this.fileFolderService = (FileFolderService)this.applicationContext.getBean("fileFolderService");
    }

    /**
     * Test create target.
     *
     * @throws Exception
     */
    public void testCreateTarget() throws Exception
    {
        if (!transferService.targetExists(TARGET_NAME))
        {
            //create ROOT_OF_TRANSFER > ROOT_FOLDER_TO_TRANSFER
            StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
            NodeRef companyHome = nodeService.getRootNode(storeRef);

            FileInfo fi = fileFolderService.create(companyHome, ROOT_OF_TRANSFER, ContentModel.TYPE_FOLDER);


            TransferTargetImpl newTarget = new TransferTargetImpl();
            newTarget.setEndpointProtocol("http");
            newTarget.setEndpointHost("localhost");
            newTarget.setEndpointPort(9090);
            newTarget.setEndpointPath("/alfresco-ftr/service/api/transfer");
            newTarget.setName(TARGET_NAME);
            newTarget.setTitle("FTR test title target");
            newTarget.setUsername("phil");
            newTarget.setPassword("phil".toCharArray());
            transferService.saveTransferTarget(newTarget);
            // get the created target and define the root
            // the root is implicitly defined has the root of the current node.
            // get underlying node ref corresponding to the target definition
            NodeRef transferTargetNodeRef = transferService.getTransferTarget(TARGET_NAME).getNodeRef();
            //Get the primary parent of the node the action is execute up on
            //NodeRef rooTarget = nodeService.getPrimaryParent(actionedUponNodeRef).getParentRef();
            //Set the type to "fileTransferTarget" and associate it to the root
            nodeService.setType(transferTargetNodeRef, TransferModel.TYPE_FILE_TRANSFER_TARGET);
            //create the association
            //nodeService.createAssociation(transferTargetNodeRef, rooTarget, TransferModel.ASSOC_ROOT_FILE_TRANSFER);
        }
    }
}

