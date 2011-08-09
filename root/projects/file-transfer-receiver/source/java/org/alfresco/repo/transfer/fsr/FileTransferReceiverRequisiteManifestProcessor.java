package org.alfresco.repo.transfer.fsr;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.transfer.AbstractManifestProcessorBase;
import org.alfresco.repo.transfer.TransferCommons;
import org.alfresco.repo.transfer.manifest.TransferManifestDeletedNode;
import org.alfresco.repo.transfer.manifest.TransferManifestHeader;
import org.alfresco.repo.transfer.manifest.TransferManifestNormalNode;
import org.alfresco.repo.transfer.requisite.TransferRequsiteWriter;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.transfer.TransferReceiver;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mrogers The requisite manifest processor performs a parse of the manifest file to determine which resources
 *         are required. In particular it returns a list of nodes which require content to be transferred.
 */
public class FileTransferReceiverRequisiteManifestProcessor extends AbstractManifestProcessorBase
{
    private static String TEMP_VIRTUAL_ROOT = "t_v_r_1223321445643";
    private TransferRequsiteWriter out;
    private FileTransferReceiver fileTransferReceiver;

    private static final Log log = LogFactory.getLog(FileTransferReceiverRequisiteManifestProcessor.class);

    /**
     * @param receiver
     * @param transferId
     */
    public FileTransferReceiverRequisiteManifestProcessor(
            TransferReceiver receiver,
            String transferId,
            TransferRequsiteWriter out)
    {
        super(receiver, transferId);
        this.out = out;
        fileTransferReceiver = (FileTransferReceiver) receiver;
    }

    protected void endManifest()
    {
        log.debug("End Requsite");
        out.endTransferRequsite();
    }

    protected void processNode(TransferManifestDeletedNode node)
    {
        // NOOP
    }

    protected void processNode(TransferManifestNormalNode node)
    {

        log.debug("Node does not exist on destination nodeRef:" + node.getNodeRef());

        /**
         * there is no corresponding node so all content properties are "missing."
         */
        for (Map.Entry<QName, Serializable> propEntry : node.getProperties().entrySet())
        {
            Serializable value = propEntry.getValue();
            if (log.isDebugEnabled())
            {
                if (value == null)
                {
                    log.debug("Received a null value for property " + propEntry.getKey());
                }
            }
            if ((value != null) && ContentData.class.isAssignableFrom(value.getClass()))
            {
                ContentData srcContent = (ContentData) value;
                if (srcContent.getContentUrl() != null && !srcContent.getContentUrl().isEmpty())
                {
                    // Only ask for content if content is new or if contentUrl is modified
                    boolean contentisMissing = fileTransferReceiver.isContentNewOrModified(
                            node.getNodeRef().toString(), srcContent.getContentUrl());
                    if (contentisMissing == true)
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug("no node on destination, content is required" + propEntry.getKey()
                                    + srcContent.getContentUrl());
                        }
                        out.missingContent(node.getNodeRef(), propEntry.getKey(), TransferCommons
                                .URLToPartName(srcContent.getContentUrl()));
                    }
                }
            }
        }
    }

    protected void processHeader(TransferManifestHeader header)
    {
        // T.B.D
    }

    /*
     * (non-Javadoc)
     *
     * @see org.alfresco.repo.transfer.manifest.TransferManifestProcessor#startTransferManifest()
     */
    protected void startManifest()
    {
        log.debug("Start Requsite");
        out.startTransferRequsite();
    }

    /**
     * @param nodeService the nodeService to set
     */
    public void setNodeService(NodeService nodeService)
    {

    }

}
