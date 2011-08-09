package org.alfresco.repo.transfer.fsr;

import java.io.InputStream;

import org.alfresco.repo.transfer.TransferProgressMonitor;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.transfer.TransferException;
import org.alfresco.service.cmr.transfer.TransferProgress;
import org.alfresco.service.cmr.transfer.TransferProgress.Status;

public class FileTransferProgressMonitor implements TransferProgressMonitor
{

    public InputStream getLogInputStream(String transferId) throws TransferException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public TransferProgress getProgress(String transferId) throws TransferException
    {

        TransferProgress tpr = new TransferProgress();
        tpr.setCurrentPosition(1);
        tpr.setEndPosition(1);
        tpr.setError(null);
        tpr.setStatus(Status.COMPLETE);
        return tpr;

    }

    public void logComment(String transferId, Object obj) throws TransferException
    {
        // TODO Auto-generated method stub

    }

    public void logCreated(
            String transferId,
            NodeRef sourceNode,
            NodeRef destNode,
            NodeRef newParent,
            Path newPath,
            boolean orphan)
    {
        // TODO Auto-generated method stub

    }

    public void logDeleted(String transferId, NodeRef sourceNode, NodeRef destNode, Path parentPath)
    {
        // TODO Auto-generated method stub

    }

    public void logException(String transferId, Object obj, Throwable ex) throws TransferException
    {
        // TODO Auto-generated method stub

    }

    public void logMoved(
            String transferId,
            NodeRef sourceNodeRef,
            NodeRef destNodeRef,
            Path oldPath,
            NodeRef newParent,
            Path newPath)
    {
        // TODO Auto-generated method stub

    }

    public void logUpdated(String transferId, NodeRef sourceNode, NodeRef destNode, Path parentPath)
    {
        // TODO Auto-generated method stub

    }

    public void updateProgress(String transferId, int currPos) throws TransferException
    {
        // TODO Auto-generated method stub

    }

    public void updateProgress(String transferId, int currPos, int endPos) throws TransferException
    {
        // TODO Auto-generated method stub

    }

    public void updateStatus(String transferId, Status status) throws TransferException
    {
        // TODO Auto-generated method stub

    }

}
