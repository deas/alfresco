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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.alfresco.repo.lock.JobLockService;
import org.alfresco.repo.lock.LockAcquisitionException;
import org.alfresco.repo.transfer.ManifestProcessorFactory;
import org.alfresco.repo.transfer.TransferModel;
import org.alfresco.repo.transfer.TransferProgressMonitor;
import org.alfresco.repo.transfer.TransferVersionChecker;
import org.alfresco.repo.transfer.TransferVersionImpl;
import org.alfresco.repo.transfer.manifest.TransferManifestProcessor;
import org.alfresco.repo.transfer.manifest.XMLTransferManifestReader;
import org.alfresco.repo.transfer.requisite.XMLTransferRequsiteWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.transfer.TransferException;
import org.alfresco.service.cmr.transfer.TransferProgress;
import org.alfresco.service.cmr.transfer.TransferReceiver;
import org.alfresco.service.cmr.transfer.TransferVersion;
import org.alfresco.service.descriptor.Descriptor;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.alfresco.util.GUID;
import org.springframework.util.FileCopyUtils;

public class FileTransferReceiver implements TransferReceiver
{
    private final static Log log = LogFactory.getLog(FileTransferReceiver.class);
    private static final String SNAPSHOT_FILE_NAME = "snapshot.xml";

    private JobLockService jobLockService;
    /**
     * Reference to the TransactionService instance.
     */
    private TransactionService transactionService;

    /**
     * Locks for the transfers in progress
     * <p>
     * TransferId, Lock
     */
    private Map<String, Lock> locks = new ConcurrentHashMap<String, Lock>();

    /**
     * How many mS before refreshing the lock?
     */
    private long lockRefreshTime = 60000;

    /**
     * How many times to retry to obtain the lock
     */
    private int lockRetryCount = 2;

    /**
     * How long to wait between retries
     */
    private long lockRetryWait = 100;

    /**
     * How long in mS to keep the lock before giving up and ending the transfer, possibly the client has terminated?
     */
    private long lockTimeOut = 3600000;

    private TransferVersionChecker transferVersionChecker;

    private String rootStagingDirectory;

    private String defaultReceivingroot;

    private ManifestProcessorFactory manifestProcessorFactory;

    private Map<String, File> contents = new ConcurrentHashMap<String, File>();

    private TransferProgressMonitor progressMonitor;

    private FileTransferInfoDAO fileTransferInfoDAO;

    private String fileTransferRootNodeRef;

    private Set<String> setOfNodesBeforeSyncMode;

    public void cancel(String transferId) throws TransferException
    {
        // TODO Auto-generated method stub

    }

    public void commit(String transferId) throws TransferException
    {
        if (log.isDebugEnabled())
        {
            log.debug("Committing transferId=" + transferId);
        }

        /**
         * A side-effect of checking the lock here is that it ensures that the lock timeout is suspended.
         */
        checkLock(transferId);

        final String fTransferId = transferId;

        try
        {
            /* lock is going to be released */checkLock(transferId);
            progressMonitor.updateStatus(transferId, TransferProgress.Status.COMMITTING);

            RetryingTransactionHelper.RetryingTransactionCallback<Object> commitWork = new RetryingTransactionCallback<Object>()
                {
                    public Object execute() throws Throwable
                    {

                        List<TransferManifestProcessor> commitProcessors = manifestProcessorFactory
                                .getCommitProcessors(FileTransferReceiver.this, fTransferId);

                        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                        SAXParser parser = saxParserFactory.newSAXParser();
                        File snapshotFile = getSnapshotFile(fTransferId);

                        if (snapshotFile.exists())
                        {
                            if (log.isDebugEnabled())
                            {
                                log.debug("Processing manifest file:" + snapshotFile.getAbsolutePath());
                            }
                            // We parse the file as many times as we have processors
                            for (TransferManifestProcessor processor : commitProcessors)
                            {
                                XMLTransferManifestReader reader = new XMLTransferManifestReader(processor);

                                try
                                {
                                    parser.parse(snapshotFile, reader);
                                }
                                finally
                                {

                                }
                                parser.reset();
                            }
                        }
                        else
                        {
                            progressMonitor.logException(fTransferId,
                                    "Unable to start commit. No snapshot file received", new TransferException(
                                            "MSG_NO_SNAPSHOT_RECEIVED", new Object[]
                                            { fTransferId }));
                        }
                        return null;
                    }
                };

            transactionService.getRetryingTransactionHelper().doInTransaction(commitWork, false, true);

            Throwable error = progressMonitor.getProgress(transferId).getError();
            if (error != null)
            {
                if (TransferException.class.isAssignableFrom(error.getClass()))
                {
                    throw (TransferException) error;
                }
                else
                {
                    throw new TransferException("MSG_ERROR_WHILE_COMMITTING_TRANSFER", new Object[]
                    { transferId }, error);
                }
            }

            /**
             * Successfully committed
             */
            if (log.isDebugEnabled())
            {
                log.debug("Commit success transferId=" + transferId);
            }
        }
        catch (Exception ex)
        {
            if (TransferException.class.isAssignableFrom(ex.getClass()))
            {
                throw (TransferException) ex;
            }
            else
            {
                throw new TransferException("MSG_ERROR_WHILE_COMMITTING_TRANSFER", ex);
            }
        }
        finally
        {

            /**
             * Clean up at the end of the transfer
             */
            try
            {
                end(transferId);
            }
            catch (Exception ex)
            {
                log.error("Failed to clean up transfer. Lock may still be in place: " + transferId, ex);
            }
        }

    }

    public void commitAsync(String transferId) throws TransferException
    {
        this.commit(transferId);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.alfresco.repo.web.scripts.transfer.TransferReceiver#end(org.alfresco.service.cmr.repository.NodeRef)
     */
    public void end(final String transferId)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Request to end transfer " + transferId);
        }
        if (transferId == null)
        {
            throw new IllegalArgumentException("transferId = null");
        }

        try
        {
            Lock lock = locks.get(transferId);
            if (lock != null)
            {
                log.debug("releasing lock:" + lock.lockToken);
                lock.releaseLock();
                locks.remove(lock);
            }

            removeTempFolders(transferId);
        }
        catch (TransferException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new TransferException("MSG_ERROR_WHILE_ENDING_TRANSFER", new Object[]
            { transferId }, ex);
        }
    }

    public void generateRequsite(String transferId, OutputStream requsiteStream) throws TransferException
    {
        log.debug("Generate Requsite for transfer:" + transferId);
        try
        {
            File snapshotFile = getSnapshotFile(transferId);

            if (snapshotFile.exists())
            {
                log.debug("snapshot does exist");
                SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                SAXParser parser = saxParserFactory.newSAXParser();
                OutputStreamWriter dest = new OutputStreamWriter(requsiteStream, "UTF-8");

                XMLTransferRequsiteWriter writer = new XMLTransferRequsiteWriter(dest);
                TransferManifestProcessor processor = manifestProcessorFactory.getRequsiteProcessor(
                        FileTransferReceiver.this, transferId, writer);

                XMLTransferManifestReader reader = new XMLTransferManifestReader(processor);

                /**
                 * Now run the parser
                 */
                parser.parse(snapshotFile, reader);

                /**
                 * And flush the destination in case any content remains in the writer.
                 */
                dest.flush();

            }
            log.debug("Generate Requsite done transfer:" + transferId);

        }
        catch (Exception ex)
        {
            if (TransferException.class.isAssignableFrom(ex.getClass()))
            {
                throw (TransferException) ex;
            }
            else
            {
                throw new TransferException("MSG_ERROR_WHILE_GENERATING_REQUISITE", ex);
            }
        }

    }

    public TransferProgressMonitor getProgressMonitor()
    {
        return this.progressMonitor;
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

    /**
     * @param stagingFolder
     */
    private void deleteFile(File file)
    {
        if (file.isDirectory())
        {
            File[] fileList = file.listFiles();
            if (fileList != null)
            {
                for (File currentFile : fileList)
                {
                    deleteFile(currentFile);
                }
            }
        }
        file.delete();
    }

    public File getStagingFolder(String transferId)
    {
        if (transferId == null)
        {
            throw new IllegalArgumentException("transferId = " + transferId);
        }
        NodeRef transferNodeRef = new NodeRef(transferId);
        File tempFolder;
        String tempFolderPath = rootStagingDirectory + "/" + transferNodeRef.getId();
        tempFolder = getOrCreateFolderIfNotExist(tempFolderPath);
        return tempFolder;
    }

    public TransferProgress getStatus(String transferId) throws TransferException
    {
        return getProgressMonitor().getProgress(transferId);
    }

    public NodeRef getTempFolder(String transferId)
    {
        if (transferId == null)
        {
            throw new IllegalArgumentException("transferId = " + transferId);
        }

        return new NodeRef(transferId);

    }

    public InputStream getTransferReport(String transferId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public TransferVersion getVersion()
    {
        return new TransferVersionImpl("4", "0", "0", "Community");
    }

    public void prepare(String transferId) throws TransferException
    {
        // TODO Auto-generated method stub

    }

    public void saveContent(String transferId, String contentFileId, InputStream contentStream)
            throws TransferException
    {
        Lock lock = checkLock(transferId);
        try
        {
            File stagedFile = new File(getStagingFolder(transferId), contentFileId);
            if (stagedFile.createNewFile())
            {
                FileCopyUtils.copy(contentStream, new BufferedOutputStream(new FileOutputStream(stagedFile)));
            }
            contents.put(contentFileId, stagedFile);
        }
        catch (Exception ex)
        {
            throw new TransferException("MSG_ERROR_WHILE_STAGING_CONTENT", new Object[]
            { transferId, contentFileId }, ex);
        }
        finally
        {
            lock.enableLockTimeout();
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see org.alfresco.service.cmr.transfer.TransferReceiver#nudgeLock(java.lang.String)
     */
    public Lock checkLock(final String transferId) throws TransferException
    {
        if (transferId == null)
        {
            throw new IllegalArgumentException("nudgeLock: transferId = null");
        }

        Lock lock = locks.get(transferId);
        if (lock != null)
        {
            if (lock.isActive())
            {
                lock.suspendLockTimeout();
                return lock;
            }
            else
            {
                // lock is no longer active
                log.debug("lock not active");
                throw new TransferException("MSG_LOCK_TIMED_OUT", new Object[]
                { transferId });

            }
        }
        else
        {
            log.debug("lock not found");
            throw new TransferException("MSG_LOCK_NOT_FOUND", new Object[]
            { transferId });
            // lock not found
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.alfresco.service.cmr.transfer.TransferReceiver#saveSnapshot(java.io.InputStream)
     */
    public void saveSnapshot(String transferId, InputStream openStream) throws TransferException
    {
        // Check that this transfer still owns the lock
        Lock lock = checkLock(transferId);
        try
        {
            if (log.isDebugEnabled())
            {
                log.debug("Saving snapshot for transferId =" + transferId);
            }

            File snapshotFile = new File(getStagingFolder(transferId), SNAPSHOT_FILE_NAME);
            try
            {
                if (snapshotFile.createNewFile())
                {
                    FileCopyUtils.copy(openStream, new BufferedOutputStream(new FileOutputStream(snapshotFile)));
                }
                if (log.isDebugEnabled())
                {
                    log.debug("Saved snapshot for transferId =" + transferId);
                }
            }
            catch (Exception ex)
            {
                throw new TransferException("MSG_ERROR_WHILE_STAGING_SNAPSHOT", ex);
            }
        }
        finally
        {
            lock.enableLockTimeout();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.alfresco.repo.web.scripts.transfer.TransferReceiver#start()
     */
    public String start(String fromRepositoryId, boolean transferToSelf, TransferVersion fromVersion)
    {
        log.debug("Start transfer");


        /**
         * Check that transfer is allowed to this repository
         */
        checkTransfer(fromRepositoryId, transferToSelf);

        /**
         * Check that the versions are compatible
         */
        TransferVersion toVersion = getVersion();

        if (!getTransferVersionChecker().checkTransferVersions(fromVersion, toVersion))
        {
            throw new TransferException("Transfer Incompatible versions", new Object[]
            { "None", fromVersion, toVersion });
        }

        /**
         * First get the transfer lock for this domain
         */

        String lockStr = "transfer.server.default";
        QName lockQName = QName.createQName(TransferModel.TRANSFER_MODEL_1_0_URI, lockStr);
        Lock lock = new Lock(lockQName);

        try
        {

            lock.makeLock();

            /**
             * Transfer Lock held if we get this far
             */
            String transferId = null;

            try
            {
                /**
                 * Now create a transfer record and use its NodeRef as the transfer id
                 */
                RetryingTransactionHelper txHelper = transactionService.getRetryingTransactionHelper();

                transferId = txHelper.doInTransaction(
                        new RetryingTransactionHelper.RetryingTransactionCallback<String>()
                            {
                                public String execute() throws Throwable
                                {
                                    final NodeRef relatedTransferRecord = createTransferRecord();
                                    String transferId = relatedTransferRecord.toString();
                                    getTempFolder(transferId);
                                    getStagingFolder(transferId);

                                    // TransferServicePolicies.OnStartInboundTransferPolicy onStartPolicy =
                                    // onStartInboundTransferDelegate.get(TransferModel.TYPE_TRANSFER_RECORD);
                                    // onStartPolicy.onStartInboundTransfer(transferId);

                                    return transferId;
                                }
                            }, false, true);
            }
            catch (Exception e)
            {
                log.debug("Exception while staring transfer", e);
                log.debug("releasing lock - we never created the transfer id");
                lock.releaseLock();
                throw new TransferException("Error while starting!", e);
            }

            /**
             * Here if we have begun a transfer and have a valid transfer id
             */
            lock.transferId = transferId;
            locks.put(transferId, lock);
            log.info("transfer started:" + transferId);
            lock.enableLockTimeout();
            return transferId;

        }
        catch (LockAcquisitionException lae)
        {
            log.debug("transfer lock is already taken", lae);
            // lock is already taken.
            throw new TransferException("MSG_TRANSFER_LOCK_UNAVAILABLE");
        }
    }

    public void setJobLockService(JobLockService jobLockService)
    {
        this.jobLockService = jobLockService;
    }

    public JobLockService getJobLockService()
    {
        return jobLockService;
    }

    public void setLockRefreshTime(long lockRefreshTime)
    {
        this.lockRefreshTime = lockRefreshTime;
    }

    public long getLockRefreshTime()
    {
        return lockRefreshTime;
    }

    /**
     * A Transfer Lock
     */
    private class Lock implements JobLockService.JobLockRefreshCallback
    {
        /**
         * The name of the lock - unique for each domain
         */
        QName lockQName;

        /**
         * The unique token for this lock instance.
         */
        String lockToken;

        /**
         * The transfer that this lock belongs to.
         */
        String transferId;

        /**
         * Is the lock active ?
         */
        private boolean active = false;

        /**
         * Is the server processing ?
         */
        private boolean processing = false;

        /**
         * When did we last check whether the lock is active
         */
        Date lastActive = new Date();

        public Lock(QName lockQName)
        {
            this.lockQName = lockQName;
        }

        /**
         * Called by Job Lock Service to determine whether the lock is still active
         */
        public boolean isActive()
        {
            Date now = new Date();

            synchronized (this)
            {
                if (active)
                {
                    if (!processing)
                    {
                        if (now.getTime() > lastActive.getTime() + getLockTimeOut())
                        {
                            return false;
                        }
                    }
                }

                if (log.isDebugEnabled())
                {
                    log.debug("transfer service callback isActive: " + active);
                }

                return active;
            }
        }

        /**
         * Make the lock - called on main thread
         *
         * @throws LockAquisitionException
         */
        public void makeLock()
        {
            if (log.isDebugEnabled())
            {
                log.debug("makeLock" + lockQName);
            }

            lockToken = getJobLockService().getLock(lockQName, getLockRefreshTime(), getLockRetryWait(),
                    getLockRetryCount());

            synchronized (this)
            {
                active = true;
            }

            if (log.isDebugEnabled())
            {
                log.debug("lock taken: name" + lockQName + " token:" + lockToken);
            }
            log.debug("register lock callback, target lock refresh time :" + getLockRefreshTime());
            getJobLockService().refreshLock(lockToken, lockQName, getLockRefreshTime(), this);
            log.debug("refreshLock callback registered");
        }

        /**
         * Check that the lock is still active Called on main transfer thread as transfer proceeds.
         *
         * @throws TransferException (Lock timeout)
         */
        public void suspendLockTimeout()
        {
            log.debug("suspend lock called");
            if (active)
            {
                processing = true;
            }
            else
            {
                // lock is no longer active
                log.debug("lock not active, throw timed out exception");
                throw new TransferException("lock not active, throw timed out exception");
            }
        }

        public void enableLockTimeout()
        {
            Date now = new Date();

            // Update lastActive to 1S boundary
            if (now.getTime() > lastActive.getTime() + 1000)
            {
                lastActive = new Date();
                log.debug("start waiting : lastActive:" + lastActive);
            }

            processing = false;
        }

        /**
         * Release the lock Called on main thread
         */
        public void releaseLock()
        {
            if (log.isDebugEnabled())
            {
                log.debug("transfer service about to releaseLock : " + lockQName);
            }

            synchronized (this)
            {
                if (active)
                {
                    getJobLockService().releaseLock(lockToken, lockQName);
                }
                active = false;
            }
        }

        /**
         * Called by Job Lock Service on release of the lock after time-out
         */
        public void lockReleased()
        {
            synchronized (this)
            {
                if (active)
                {
                    log.info("transfer service: lock has timed out, timeout :" + lockQName);
                    timeout(transferId);
                }

                active = false;
            }
        }

    }

    /**
     * Timeout a transfer. Called after the lock has been released via a timeout. This is the last chance to clean up.
     *
     * @param transferId
     */
    private void timeout(final String transferId)
    {
        log.info("Inbound Transfer has timed out transferId:" + transferId);
        /*
         * There is no transaction or authentication context in this method since it is called via a timer thread.
         */
        final RetryingTransactionCallback<Object> timeoutCB = new RetryingTransactionCallback<Object>()
            {

                public Object execute() throws Throwable
                {
                    TransferProgress progress = getProgressMonitor().getProgress(transferId);

                    if (progress.getStatus().equals(TransferProgress.Status.PRE_COMMIT))
                    {
                        log.warn("Inbound Transfer Lock Timeout - transferId:" + transferId);
                        /**
                         * Did not get out of PRE_COMMIT. The client has probably "gone away" after calling "start", but
                         * before calling commit, cancel or error.
                         */
                        locks.remove(transferId);
                        removeTempFolders(transferId);
                        Object[] msgParams =
                        { transferId };
                        getProgressMonitor().logException(transferId, "transfer timeout",
                                new TransferException("Lock time out", msgParams));
                        getProgressMonitor().updateStatus(transferId, TransferProgress.Status.ERROR);
                    }
                    else
                    {
                        // We got beyond PRE_COMMIT, therefore leave the clean up to either
                        // commit, cancel or error command, since there may still be "in-flight"
                        // transfer in another thread. Although why, in that case, are we here?
                        log.warn("Inbound Transfer Lock Timeout - already past PRE-COMMIT - do no cleanup transferId:"
                                + transferId);
                    }
                    return null;
                }
            };

        transactionService.getRetryingTransactionHelper().doInTransaction(timeoutCB, false, true);
    }

    private void removeTempFolders(final String transferId)
    {
        NodeRef tempStoreNode = null;
        try
        {
            log.debug("Deleting temporary store node...");
            tempStoreNode = getTempFolder(transferId);
            log.debug("Deleted temporary store node.");
        }
        catch (Exception ex)
        {
            log.warn("Failed to delete temp store node for transfer id " + transferId + "\nTemp store noderef = "
                    + tempStoreNode);
        }

        File stagingFolder = null;
        try
        {
            log.debug("delete staging folder " + transferId);
            // Delete the staging folder.
            stagingFolder = getStagingFolder(transferId);
            deleteFile(stagingFolder);
            log.debug("Staging folder deleted");
        }
        catch (Exception ex)
        {
            log.warn("Failed to delete staging folder for transfer id " + transferId + "\nStaging folder = "
                    + stagingFolder.toString());
        }
    }

    public FileTransferInfoEntity findFileTransferInfoByNodeRef(String nodeRef)
    {
        RetryingTransactionHelper txHelper = transactionService.getRetryingTransactionHelper();

        final String localNodeRef = nodeRef;

        FileTransferInfoEntity fileTransferInfoEntity = txHelper.doInTransaction(
                new RetryingTransactionHelper.RetryingTransactionCallback<FileTransferInfoEntity>()
                    {
                        public FileTransferInfoEntity execute() throws Throwable
                        {
                            FileTransferInfoEntity fileTransferInfoEntity = fileTransferInfoDAO
                                    .findFileTransferInfoByNodeRef(localNodeRef);

                            return fileTransferInfoEntity;

                        }
                    }, true, false);
        return fileTransferInfoEntity;
    }

    public List<FileTransferInfoEntity> findFileTransferInfoByParentNodeRef(String nodeRef)
    {
        RetryingTransactionHelper txHelper = transactionService.getRetryingTransactionHelper();

        final String localNodeRef = nodeRef;

        List<FileTransferInfoEntity> fileTransferInfoEntityList = txHelper.doInTransaction(
                new RetryingTransactionHelper.RetryingTransactionCallback<List<FileTransferInfoEntity>>()
                    {
                        public List<FileTransferInfoEntity> execute() throws Throwable
                        {
                            List<FileTransferInfoEntity> fileTransferInfoEntityList = fileTransferInfoDAO
                                    .findFileTransferInfoByParentNodeRef(localNodeRef);

                            return fileTransferInfoEntityList;

                        }
                    }, true, false);
        return fileTransferInfoEntityList;
    }

    public void updateFileTransferInfoByNodeRef(FileTransferInfoEntity modifiedEntity)
    {
        RetryingTransactionHelper txHelper = transactionService.getRetryingTransactionHelper();

        final FileTransferInfoEntity localmodifiedEntity = modifiedEntity;

        txHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    fileTransferInfoDAO.updateFileTransferInfoByNodeRef(localmodifiedEntity);

                    return null;
                }
            }, false, false);
    }

    public void deleteNodeByNodeRef(String nodeRef)
    {
        RetryingTransactionHelper txHelper = transactionService.getRetryingTransactionHelper();

        final String localNodeRef = nodeRef;

        txHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    fileTransferInfoDAO.deleteFileTransferInfoByNodeRef(localNodeRef);

                    return null;
                }
            }, false, false);
    }

    public void createNodeInDB(String nodeRef, String parentNodeRef, String path, String name, String contentUrl)
    {
        RetryingTransactionHelper txHelper = transactionService.getRetryingTransactionHelper();

        final String localNodeRef = nodeRef;
        final String localParentNodeRef = parentNodeRef;
        final String localPath = path;
        final String localName = name;
        final String localContentUrl = contentUrl;

        txHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    fileTransferInfoDAO.createFileTransferInfo(localNodeRef, localParentNodeRef, localPath, localName,
                            localContentUrl);

                    return null;

                }
            }, false, false);
    }

    public boolean isContentNewOrModified(String nodeRef, String contentUrl)
    {
        RetryingTransactionHelper txHelper = transactionService.getRetryingTransactionHelper();

        final String localNodeRef = nodeRef;
        final String localContentUrl = contentUrl;

        Boolean isContentNewOrModified = txHelper.doInTransaction(
                new RetryingTransactionHelper.RetryingTransactionCallback<Boolean>()
                    {
                        public Boolean execute() throws Throwable
                        {
                            FileTransferInfoEntity fileTransferInfoEntity = fileTransferInfoDAO
                                    .findFileTransferInfoByNodeRef(localNodeRef);
                            if (fileTransferInfoEntity == null)
                                return true;
                            if (localContentUrl != null
                                    && !fileTransferInfoEntity.getContentUrl().equals(localContentUrl))
                                return true;

                            return false;

                        }
                    }, true, false);
        return isContentNewOrModified;
    }

    protected File getSnapshotFile(String transferId)
    {
        return new File(getStagingFolder(transferId), SNAPSHOT_FILE_NAME);
    }

    /*
     * Reset the set of nodes that should be used when handling sync=true
     * This is the initial set of nodes.
     */
    public void resetListOfNodesBeforeSyncMode()
    {
        this.setOfNodesBeforeSyncMode = new HashSet<String>(500);
    }

    /*
     * Build the list of nodes that should be considered when handling sync=true
     * This set comes from the DB. The set of nodes that will be deleted
     * in sync mode will be the set build when removing the received nodes
     * from this set (the one that where in the DB before transfer).
     */
    public void updateListOfDescendantsForSyncMode(String nodeRef)
    {
        this.setOfNodesBeforeSyncMode.add(nodeRef);
        // get all children of nodeToModify
        List<FileTransferInfoEntity> childrenList = this.findFileTransferInfoByParentNodeRef(nodeRef);
        // iterate on children
        for (FileTransferInfoEntity curChild : childrenList)
        {
            if (this.setOfNodesBeforeSyncMode.contains(curChild.toString()))
                continue;
            updateListOfDescendantsForSyncMode(curChild.getNodeRef().toString());
        }
    }

    public Set<String> getListOfDescendentsForSyncMode()
    {
        return this.setOfNodesBeforeSyncMode;
    }

    /**
     * Check Whether transfer is allowed from the specified repository. Called prior to "begin".
     */

    private void checkTransfer(String fromRepository, boolean transferToSelf)
    {
        // to be filled
    }

    /**
     * @return
     */
    private NodeRef createTransferRecord()
    {
        return new NodeRef("workspace://SpaceStore/" + GUID.generate());
    }

    public long getLockRetryWait()
    {
        return lockRetryWait;
    }

    public void setLockRetryWait(long lockRetryWait)
    {
        this.lockRetryWait = lockRetryWait;
    }

    public int getLockRetryCount()
    {
        return lockRetryCount;
    }

    public void setLockRetryCount(int lockRetryCount)
    {
        this.lockRetryCount = lockRetryCount;
    }

    public long getLockTimeOut()
    {
        return lockTimeOut;
    }

    public void setLockTimeOut(long lockTimeOut)
    {
        this.lockTimeOut = lockTimeOut;
    }

    public TransferVersionChecker getTransferVersionChecker()
    {
        return transferVersionChecker;
    }

    public void setTransferVersionChecker(TransferVersionChecker transferVersionChecker)
    {
        this.transferVersionChecker = transferVersionChecker;
    }

    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    public String getRootStagingDirectory()
    {
        return rootStagingDirectory;
    }

    public void setRootStagingDirectory(String rootStagingDirectory)
    {
        this.rootStagingDirectory = rootStagingDirectory;
    }

    public ManifestProcessorFactory getManifestProcessorFactory()
    {
        return manifestProcessorFactory;
    }

    public void setManifestProcessorFactory(ManifestProcessorFactory manifestProcessorFactory)
    {
        this.manifestProcessorFactory = manifestProcessorFactory;
    }

    public void setProgressMonitor(TransferProgressMonitor progressMonitor)
    {
        this.progressMonitor = progressMonitor;
    }

    public String getDefaultReceivingroot()
    {
        return defaultReceivingroot;
    }

    public void setDefaultReceivingroot(String defaultReceivingroot)
    {
        this.defaultReceivingroot = defaultReceivingroot;
    }

    public Map<String, File> getContents()
    {
        return contents;
    }

    public void setFileTransferRootNodeFileFileSystem(String rootFileSystem)
    {
        this.fileTransferRootNodeRef = rootFileSystem;
    }

    public String getFileTransferRootNodeFileFileSystem()
    {
        return this.fileTransferRootNodeRef;
    }

    public void setFileTransferInfoDAO(FileTransferInfoDAO fileTransferInfoDAO)
    {
        this.fileTransferInfoDAO = fileTransferInfoDAO;
    }

}
