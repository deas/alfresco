package org.alfresco.repo.transfer.fsr;

import org.alfresco.service.cmr.transfer.TransferVersion;

/**
 * Interface to be implemented by hook class.
 * notify is called indicating if the transfer failed or succeed.
 *
 * @author philippe
 *
 */
public interface FileTransferHookInterface
{
    public enum Status { SUCCESS, FAILED }

    void notify(String fromRepositoryId, TransferVersion fromVersion,String transferId, Status status);
}
