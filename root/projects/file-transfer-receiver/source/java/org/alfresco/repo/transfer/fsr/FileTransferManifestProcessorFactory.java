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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transfer.ManifestProcessorFactory;
import org.alfresco.repo.transfer.manifest.TransferManifestProcessor;
import org.alfresco.repo.transfer.requisite.TransferRequsiteWriter;
import org.alfresco.service.cmr.transfer.TransferReceiver;
import org.alfresco.service.cmr.transfer.TransferVersion;

public class FileTransferManifestProcessorFactory implements ManifestProcessorFactory
{

    private FileTransferHookInterface hook;
    public static  Map<String, InnerNotificationRecord> notificationRecords = Collections
            .synchronizedMap(new HashMap<String, InnerNotificationRecord>(1000));

    public void startTransfer(String transferId, String fromRepositoryId, TransferVersion fromVersion)
    {
        InnerNotificationRecord innerNotificationRecord = new InnerNotificationRecord();
        innerNotificationRecord.fromRepositoryId = fromRepositoryId;
        innerNotificationRecord.fromVersion = fromVersion;
        notificationRecords.put(transferId, innerNotificationRecord);

    }

    /**
     * The requisite processor
     *
     * @param receiver
     * @param transferId
     * @return the requisite processor
     */
    public TransferManifestProcessor getRequsiteProcessor(
            TransferReceiver receiver,
            String transferId,
            TransferRequsiteWriter out)
    {
        return new FileTransferReceiverRequisiteManifestProcessor(receiver, transferId, out);
    }

    /**
     * The commit processors
     *
     * @param receiver
     * @param transferId
     * @return the requisite processor
     */
    public List<TransferManifestProcessor> getCommitProcessors(TransferReceiver receiver, String transferId)
    {
        InnerNotificationRecord innerNotificationRecord = notificationRecords.get(transferId);
        // register hook
        FileTransferTransactionListener fileTransferTransactionListener = new FileTransferTransactionListener(
                innerNotificationRecord.fromRepositoryId, innerNotificationRecord.fromVersion, transferId, hook);
        AlfrescoTransactionSupport.bindListener(fileTransferTransactionListener);
        List<TransferManifestProcessor> processors = new ArrayList<TransferManifestProcessor>();

        TransferManifestProcessor processor = new FileTransferPrimaryManifestProcessor(receiver, transferId);
        processors.add(processor);
        processor = new FileTransferSecondaryManifestProcessor(receiver, transferId);
        processors.add(processor);

        return processors;
    }

    public void setHook(FileTransferHookInterface hook)
    {
        this.hook = hook;
    }

    private class InnerNotificationRecord
    {
        public String fromRepositoryId;
        public TransferVersion fromVersion;
    }

}
