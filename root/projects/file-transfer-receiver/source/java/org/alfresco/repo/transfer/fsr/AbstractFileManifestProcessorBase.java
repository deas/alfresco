package org.alfresco.repo.transfer.fsr;

import java.io.File;

import org.alfresco.repo.transfer.TransferProcessingException;
import org.alfresco.repo.transfer.manifest.TransferManifestDeletedNode;
import org.alfresco.repo.transfer.manifest.TransferManifestHeader;
import org.alfresco.repo.transfer.manifest.TransferManifestNormalNode;
import org.alfresco.service.cmr.transfer.TransferException;
import org.alfresco.service.cmr.transfer.TransferReceiver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractFileManifestProcessorBase extends org.alfresco.repo.transfer.AbstractManifestProcessorBase
{
    private final static Log log = LogFactory.getLog(AbstractFileManifestProcessorBase.class);
    protected static String TEMP_VIRT_ROOT = "T_V_R_1234432123478";
    protected FileTransferReceiver fTReceiver;
    protected String fTransferId;

    public AbstractFileManifestProcessorBase(TransferReceiver receiver, String transferId)
    {
        super(receiver, transferId);
        this.fTReceiver = (FileTransferReceiver)receiver;
        this.fTransferId = transferId;
        
    }

    @Override
    protected void endManifest()
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void processHeader(TransferManifestHeader header)
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void processNode(TransferManifestNormalNode node) throws TransferProcessingException
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void processNode(TransferManifestDeletedNode node) throws TransferProcessingException
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void startManifest()
    {
        // TODO Auto-generated method stub

    }

    protected void purgeTemporaryVirtualRoot()
    {
        // do the file system clean up first
        // delete TEMP_VIRT_ROOT is exist
        File tvr = new File(fTReceiver.getDefaultReceivingroot() + "/" + TEMP_VIRT_ROOT);
        if (tvr.exists())
        {
            if (log.isDebugEnabled())
            {
                log.debug("Purgin TEMP_VIRT_ROOT:" + fTReceiver.getDefaultReceivingroot() + "/" + TEMP_VIRT_ROOT);
            }
            tvr.delete();
        }
    }

    protected File getOrCreateFolderIfNotExist(String path)
    {
        File tempFolder = new File(path);
        if (!tempFolder.exists())
        {
            if (!tempFolder.mkdirs())
            {
                tempFolder = null;
                log.error("Failed to create temp folder:" + path);
                throw new TransferException("MSG_FAILED_TO_CREATE_STAGING_FOLDER");
            }
        }
        return tempFolder;
    }

}
