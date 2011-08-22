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

import org.alfresco.repo.transaction.TransactionListenerAdapter;
import org.alfresco.service.cmr.transfer.TransferVersion;

public class FileTransferTransactionListener extends TransactionListenerAdapter
{
    private String transferId;
    private FileTransferHookInterface hook;
    private String fromRepositoryId;
    TransferVersion fromVersion;

    public FileTransferTransactionListener(String fromRepositoryId, TransferVersion fromVersion,String transferId,FileTransferHookInterface hook)
    {
        this.fromRepositoryId = fromRepositoryId;
        this.fromVersion = fromVersion;
        this.transferId = transferId;
        this.hook = hook;
    }

    @Override
    public void afterCommit()
    {
        hook.notify(fromRepositoryId, fromVersion, transferId,FileTransferHookInterface.Status.SUCCESS);
        FileTransferManifestProcessorFactory.notificationRecords.remove(transferId);
    }

    @Override
    public void afterRollback()
    {
        hook.notify(fromRepositoryId, fromVersion,transferId,FileTransferHookInterface.Status.FAILED);
        FileTransferManifestProcessorFactory.notificationRecords.remove(transferId);
    }
}
