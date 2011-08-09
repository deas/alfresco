package org.alfresco.repo.transfer.fsr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.transfer.AbstractManifestProcessorBase;
import org.alfresco.repo.transfer.TransferCommons;
import org.alfresco.repo.transfer.TransferProcessingException;
import org.alfresco.repo.transfer.manifest.TransferManifestDeletedNode;
import org.alfresco.repo.transfer.manifest.TransferManifestHeader;
import org.alfresco.repo.transfer.manifest.TransferManifestNormalNode;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.transfer.TransferException;
import org.alfresco.service.cmr.transfer.TransferReceiver;
import org.springframework.util.FileCopyUtils;
import org.alfresco.service.namespace.QName;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This ManifestProcessor will for every new node, receive it and create it with his UID name and content.
 * Track of the original name will be kept in a separate table (alf_table_to_be_renamed).
 * The nodes (on file system) will only take their final at the end of FileTransferSecondaryManifestProcessor.
 * For existing node we will act on, they will be also be renamed. They will be renamed using counter in order
 * to minimise the path length on the file system.
 *
 *
 * @author philippe
 *
 */
public class FileTransferPrimaryManifestProcessor extends AbstractFileManifestProcessorBase
{
    private final static Log log = LogFactory.getLog(FileTransferPrimaryManifestProcessor.class);

    //counter usesed to rename nodes temporarily in order to avoid
    private long renamingCounter = 0;

    public FileTransferPrimaryManifestProcessor(TransferReceiver receiver, String transferId)
    {
        super(receiver, transferId);

    }

    @Override
    protected void startManifest()
    {
        if (log.isDebugEnabled())
        {
            log.debug("Processing manigest started");
        }

        purgeTemporaryVirtualRoot();
        // recreate temporary root on file system
        getOrCreateFolderIfNotExist(fTReceiver.getDefaultReceivingroot());
        getOrCreateFolderIfNotExist(fTReceiver.getDefaultReceivingroot() + "/" + TEMP_VIRT_ROOT);

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

    @Override
    protected void endManifest()
    {
        if (log.isDebugEnabled())
        {
            log.debug("End manifest!");
        }
    }

    @Override
    protected void processHeader(TransferManifestHeader header)
    {

    }

    @Override
    protected void processNode(TransferManifestNormalNode node) throws TransferProcessingException
    {

    }

    @Override
    protected void processNode(TransferManifestDeletedNode node) throws TransferProcessingException
    {

    }


}
