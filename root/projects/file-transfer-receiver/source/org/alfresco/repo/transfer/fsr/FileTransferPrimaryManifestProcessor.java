package org.alfresco.repo.transfer.fsr;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.transfer.AbstractManifestProcessorBase;
import org.alfresco.repo.transfer.TransferProcessingException;
import org.alfresco.repo.transfer.manifest.TransferManifestDeletedNode;
import org.alfresco.repo.transfer.manifest.TransferManifestHeader;
import org.alfresco.repo.transfer.manifest.TransferManifestNormalNode;
import org.alfresco.service.cmr.transfer.TransferException;
import org.alfresco.service.cmr.transfer.TransferReceiver;
import org.springframework.util.FileCopyUtils;

public class FileTransferPrimaryManifestProcessor extends AbstractManifestProcessorBase
{
    protected FileTransferReceiver fTReceiver;
    protected String fTransferId;

    public FileTransferPrimaryManifestProcessor(TransferReceiver receiver, String transferId)
    {
        super(receiver, transferId);
        this.fTransferId = transferId;
        this.fTReceiver = (FileTransferReceiver) receiver;

    }

    @Override
    protected void endManifest()
    {
        System.out.println("End Manifest!!");

    }

    @Override
    protected void processHeader(TransferManifestHeader header)
    {
        System.out.println("process header!!");

    }

    @Override
    protected void processNode(TransferManifestNormalNode node) throws TransferProcessingException
    {
        System.out.println(node);
        String name = (String) node.getProperties().get(ContentModel.PROP_NAME);
        System.out.println("Name = " + name);
        File receivedContent = fTReceiver.getContents().get(this.fTransferId);

        // create the file with the name
        File receivedFile = new File(fTReceiver.getDefaultReceivingroot() + "/" + name);

        try
        {
            getOrCreateFolderIfNotExist(fTReceiver.getDefaultReceivingroot());
            receivedFile.createNewFile();
            FileCopyUtils.copy(receivedContent, receivedFile);
        }
        catch (Exception ex)
        {
            throw new TransferException("MSG_ERROR_WHILE_STAGING_CONTENT", ex);
        }
        // copy to it

    }

    @Override
    protected void processNode(TransferManifestDeletedNode node) throws TransferProcessingException
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void startManifest()
    {
        System.out.println("Start Manufest!!");

    }

    private File getOrCreateFolderIfNotExist(String path)
    {
        File tempFolder = new File(path);
        if (!tempFolder.exists())
        {
            if (!tempFolder.mkdirs())
            {
                tempFolder = null;
                throw new TransferException("MSG_FAILED_TO_CREATE_STAGING_FOLDER");
            }
        }
        return tempFolder;
    }
}
