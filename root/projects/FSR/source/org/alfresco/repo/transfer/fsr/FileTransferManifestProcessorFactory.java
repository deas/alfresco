package org.alfresco.repo.transfer.fsr;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.repo.transfer.ManifestProcessorFactory;
import org.alfresco.repo.transfer.manifest.TransferManifestProcessor;
import org.alfresco.repo.transfer.requisite.TransferRequsiteWriter;
import org.alfresco.service.cmr.transfer.TransferReceiver;

public class FileTransferManifestProcessorFactory implements ManifestProcessorFactory
{


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
     * @return the requsite processor
     */
    public List<TransferManifestProcessor> getCommitProcessors(TransferReceiver receiver, String transferId)
    {
        List<TransferManifestProcessor> processors = new ArrayList<TransferManifestProcessor>();
        TransferManifestProcessor processor = new FileTransferPrimaryManifestProcessor(receiver, transferId);
        processors.add(processor);
        return processors;
    }


}
