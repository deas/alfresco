package org.alfresco.repo.transfer.fsr;

import org.alfresco.service.cmr.transfer.TransferVersion;
/**
 *
 * @author philippe
 *
 */
public class FileTransferHookDummy implements FileTransferHookInterface
{

    @Override
    public void notify(String fromRepositoryId, TransferVersion fromVersion,String transferId, Status status)
    {
        // TODO Auto-generated method stub

    }

}
