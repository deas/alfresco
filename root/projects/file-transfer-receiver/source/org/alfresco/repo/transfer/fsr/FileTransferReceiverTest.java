package org.alfresco.repo.transfer.fsr;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper;

import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.transfer.TransferException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

import org.alfresco.util.GUID;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import junit.framework.TestCase;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;

import org.alfresco.repo.transfer.fsr.FileTransferReceiver;
import org.alfresco.repo.transfer.manifest.TransferManifestHeader;
import org.alfresco.repo.transfer.manifest.TransferManifestNode;
import org.alfresco.repo.transfer.manifest.TransferManifestNormalNode;
import org.alfresco.repo.transfer.manifest.XMLTransferManifestWriter;
import org.apache.tools.ant.filters.StringInputStream;
import org.alfresco.service.cmr.repository.Path;

public class FileTransferReceiverTest extends TestCase
{
    protected ApplicationContext context = null;
    protected RetryingTransactionHelper rth = null;
    protected FileTransferReceiverTransactionServiceImpl transactionService = null;
    protected FileTransferReceiver ftTransferReceiver = null;
    /** Number of transactions started */
    private int transactionsStarted = 0;
    /**
     * TransactionStatus for this test. Typical subclasses won't need to use it.
     */
    protected TransactionStatus transactionStatus;
    /** The transaction manager to use */
    protected PlatformTransactionManager transactionManager;
    /**
     * Transaction definition used by this test class: by default, a plain DefaultTransactionDefinition. Subclasses can
     * change this to cause different behavior.
     */
    protected TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
    /** Should we commit the current transaction? */
    private boolean complete = false;
    /** Should we roll back by default? */
    private boolean defaultRollback = true;
    private static int fileCount = 0;
    private NodeRef companytHome = null;
    String path = "/app:company_home/";
    private String dummyContent = "This is some dummy content.";
    private byte[] dummyContentBytes = null;

    /**
     * Sets up the fixture, for example, open a network connection. This method is called before a test is executed.
     */
    protected void setUp() throws Exception
    {

        context = new ClassPathXmlApplicationContext("fsr-bootstrap-context.xml");

        rth = (RetryingTransactionHelper) context.getBean("retryingTransactionHelper");
        transactionService = (FileTransferReceiverTransactionServiceImpl) context.getBean("transactionService");
        ftTransferReceiver = (FileTransferReceiver) context.getBean("transferReceiver");
        transactionManager = (PlatformTransactionManager) context.getBean("transactionManager");
        companytHome = new NodeRef("workspace://SpacesStore/47d5f7e8-caff-4c9d-8677-c236b343d724");
        dummyContentBytes = dummyContent.getBytes("UTF-8");
    }

    /**
     * Tests start and end with regard to locking.
     * 
     * @throws Exception
     */
    public void testStartAndEnd() throws Exception
    {

        RetryingTransactionHelper trx = transactionService.getRetryingTransactionHelper();

        RetryingTransactionCallback<Object> cb = new RetryingTransactionCallback<Object>()
            {

                public Object execute() throws Throwable
                {

                    String transferId = ftTransferReceiver.start("1234", true, ftTransferReceiver.getVersion());
                    File stagingFolder = null;
                    try
                    {
                        System.out.println("TransferId == " + transferId);

                        stagingFolder = ftTransferReceiver.getStagingFolder(transferId);
                        assertTrue(ftTransferReceiver.getStagingFolder(transferId).exists());
                        NodeRef tempFolder = ftTransferReceiver.getTempFolder(transferId);
                        assertNotNull("tempFolder is null", tempFolder);

                        Thread.sleep(1000);
                        try
                        {
                            ftTransferReceiver.start("1234", true, ftTransferReceiver.getVersion());
                            fail("Successfully started twice!");
                        }
                        catch (TransferException ex)
                        {
                            // Expected
                        }

                        Thread.sleep(300);
                        try
                        {
                            ftTransferReceiver.start("1234", true, ftTransferReceiver.getVersion());
                            fail("Successfully started twice!");
                        }
                        catch (TransferException ex)
                        {
                            // Expected
                        }

                        try
                        {
                            ftTransferReceiver.end(new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, GUID
                                    .generate()).toString());
                            // fail("Successfully ended with transfer id that doesn't own lock.");
                        }
                        catch (TransferException ex)
                        {
                            // Expected
                        }
                    }
                    finally
                    {

                        ftTransferReceiver.end(transferId);

                        /**
                         * Check clean-up
                         */
                        if (stagingFolder != null)
                        {
                            assertFalse(stagingFolder.exists());
                        }
                    }

                    return null;
                }
            };

        long oldRefreshTime = ftTransferReceiver.getLockRefreshTime();
        try
        {
            ftTransferReceiver.setLockRefreshTime(500);

            for (int i = 0; i < 5; i++)
            {

                trx.doInTransaction(cb, false, true);
            }
        }
        finally
        {
            ftTransferReceiver.setLockRefreshTime(oldRefreshTime);
        }
    }

    /**
     * Tests start and end with regard to locking. Going to cut down the timeout to a very short period, the lock should
     * expire
     * 
     * @throws Exception
     */
    public void testLockTimeout() throws Exception
    {

        RetryingTransactionHelper trx = transactionService.getRetryingTransactionHelper();

        /**
         * Simulates a client starting a transfer and then "going away";
         */
        RetryingTransactionCallback<Object> startWithoutAnythingElse = new RetryingTransactionCallback<Object>()
            {

                public Object execute() throws Throwable
                {

                    String transferId = ftTransferReceiver.start("1234", true, ftTransferReceiver.getVersion());
                    return null;
                }
            };

        RetryingTransactionCallback<Object> slowTransfer = new RetryingTransactionCallback<Object>()
            {

                public Object execute() throws Throwable
                {

                    String transferId = ftTransferReceiver.start("1234", true, ftTransferReceiver.getVersion());
                    Thread.sleep(1000);
                    try
                    {
                        ftTransferReceiver.saveSnapshot(transferId, null);
                        fail("did not timeout");
                    }
                    catch (TransferException te)
                    {
                        // expect to go here with a timeout
                    }
                    return null;
                }
            };

        long lockRefreshTime = ftTransferReceiver.getLockRefreshTime();
        long lockTimeOut = ftTransferReceiver.getLockTimeOut();

        try
        {
            ftTransferReceiver.setLockRefreshTime(500);
            ftTransferReceiver.setLockTimeOut(200);

            /**
             * This test simulates a client that starts a transfer and then "goes away". We kludge the timeouts to far
             * shorter than normal to make the test run in a reasonable time.
             */
            for (int i = 0; i < 3; i++)
            {
                trx.doInTransaction(startWithoutAnythingElse, false, true);
                Thread.sleep(1000);
            }
            trx.doInTransaction(slowTransfer, false, true);
        }
        finally
        {
            ftTransferReceiver.setLockRefreshTime(lockRefreshTime);
            ftTransferReceiver.setLockTimeOut(lockTimeOut);
        }
    }

    public void testSaveSnapshot() throws Exception
    {
        startNewTransaction();
        try
        {
            String transferId = ftTransferReceiver.start("1234", true, ftTransferReceiver.getVersion());
            File snapshotFile = null;
            try
            {
                TransferManifestNode node = createContentNode(transferId);
                List<TransferManifestNode> nodes = new ArrayList<TransferManifestNode>();
                nodes.add(node);
                String snapshot = createSnapshot(nodes);

                ftTransferReceiver.saveSnapshot(transferId, new StringInputStream(snapshot, "UTF-8"));

                File stagingFolder = ftTransferReceiver.getStagingFolder(transferId);
                snapshotFile = new File(stagingFolder, "snapshot.xml");
                assertTrue(snapshotFile.exists());
                assertEquals(snapshot.getBytes("UTF-8").length, snapshotFile.length());
            }
            finally
            {
                ftTransferReceiver.end(transferId);
                if (snapshotFile != null)
                {
                    assertFalse(snapshotFile.exists());
                }
            }
        }
        finally
        {
            endTransaction();
        }
    }

    public void testSaveContent() throws Exception
    {
        startNewTransaction();
        try
        {
            String transferId = ftTransferReceiver.start("1234", true, ftTransferReceiver.getVersion());
            try
            {
                String contentId = "mytestcontent";
                ftTransferReceiver.saveContent(transferId, contentId, new ByteArrayInputStream(dummyContentBytes));
                File contentFile = new File(ftTransferReceiver.getStagingFolder(transferId), contentId);
                assertTrue(contentFile.exists());
                assertEquals(dummyContentBytes.length, contentFile.length());
            }
            finally
            {
                ftTransferReceiver.end(transferId);
            }
        }
        finally
        {
            endTransaction();
        }
    }

    public void testBasicCommit() throws Exception
    {
        startNewTransaction();
        TransferManifestNode node = null;

        try
        {
            String transferId = ftTransferReceiver.start("1234", true, ftTransferReceiver.getVersion());
            try
            {
                node = createContentNode(transferId);
                List<TransferManifestNode> nodes = new ArrayList<TransferManifestNode>();
                nodes.add(node);
                String snapshot = createSnapshot(nodes);
                System.out.println(snapshot);

                ftTransferReceiver.saveSnapshot(transferId, new StringInputStream(snapshot, "UTF-8"));
                ftTransferReceiver.saveContent(transferId, node.getUuid(), new ByteArrayInputStream(dummyContentBytes));
                ftTransferReceiver.commit(transferId);

            }
            catch (Exception ex)
            {
                ftTransferReceiver.end(transferId);
                throw ex;
            }
        }
        finally
        {
            endTransaction();
        }

        startNewTransaction();
        try
        {
            // assertTrue(nodeService.exists(node.getNodeRef()));
            // nodeService.deleteNode(node.getNodeRef());
        }
        finally
        {
            endTransaction();
        }
    }

    /**
     * @return
     */
    private TransferManifestNormalNode createContentNode(String transferId) throws Exception
    {
        TransferManifestNormalNode node = new TransferManifestNormalNode();
        String uuid = GUID.generate();
        NodeRef nodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, uuid);
        node.setNodeRef(nodeRef);
        node.setUuid(uuid);
        byte[] dummyContent = "This is some dummy content.".getBytes("UTF-8");

        node.setType(ContentModel.TYPE_CONTENT);

        /**
         * Get guest home
         */
        NodeRef parentFolder = companytHome;

        String nodeName = uuid + ".testnode" + getNameSuffix();

        List<ChildAssociationRef> parents = new ArrayList<ChildAssociationRef>();
        ChildAssociationRef primaryAssoc = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, parentFolder, QName
                .createQName(NamespaceService.CONTENT_MODEL_1_0_URI, nodeName), node.getNodeRef(), true, -1);
        parents.add(primaryAssoc);
        node.setParentAssocs(parents);
        node.setParentPath(null);
        node.setPrimaryParentAssoc(primaryAssoc);

        Map<QName, Serializable> props = new HashMap<QName, Serializable>();
        props.put(ContentModel.PROP_NODE_UUID, uuid);
        props.put(ContentModel.PROP_NAME, nodeName);
        ContentData contentData = new ContentData("/" + uuid, "text/plain", dummyContent.length, "UTF-8");
        props.put(ContentModel.PROP_CONTENT, contentData);
        node.setProperties(props);

        return node;
    }

    private String createSnapshot(List<TransferManifestNode> nodes) throws Exception
    {
        XMLTransferManifestWriter manifestWriter = new XMLTransferManifestWriter();
        StringWriter output = new StringWriter();
        manifestWriter.startTransferManifest(output);
        TransferManifestHeader header = new TransferManifestHeader();
        header.setCreatedDate(new Date());
        header.setNodeCount(nodes.size());
        header.setRepositoryId("repo 1");
        manifestWriter.writeTransferManifestHeader(header);
        for (TransferManifestNode node : nodes)
        {
            manifestWriter.writeTransferManifestNode(node);
        }
        manifestWriter.endTransferManifest();

        return output.toString();

    }

    /**
     * Start a new transaction. Only call this method if {@link #endTransaction()} has been called.
     * {@link #setComplete()} can be used again in the new transaction. The fate of the new transaction, by default,
     * will be the usual rollback.
     * 
     * @throws TransactionException if starting the transaction failed
     */
    protected void startNewTransaction() throws TransactionException
    {
        if (this.transactionStatus != null)
        {
            throw new IllegalStateException("Cannot start new transaction without ending existing transaction: "
                    + "Invoke endTransaction() before startNewTransaction()");
        }
        if (this.transactionManager == null)
        {
            throw new IllegalStateException("No transaction manager set");
        }

        this.transactionStatus = this.transactionManager.getTransaction(this.transactionDefinition);
        ++this.transactionsStarted;
        this.complete = !this.isRollback();

    }

    /**
     * Immediately force a commit or rollback of the transaction, according to the <code>complete</code> and
     * {@link #isRollback() rollback} flags.
     * <p>
     * Can be used to explicitly let the transaction end early, for example to check whether lazy associations of
     * persistent objects work outside of a transaction (that is, have been initialized properly).
     * 
     * @see #setComplete()
     */
    protected void endTransaction()
    {
        final boolean commit = this.complete || !isRollback();
        if (this.transactionStatus != null)
        {
            try
            {
                if (commit)
                {
                    this.transactionManager.commit(this.transactionStatus);
                }
                else
                {
                    this.transactionManager.rollback(this.transactionStatus);
                }
            }
            finally
            {
                this.transactionStatus = null;
            }
        }
    }

    /**
     * Tears down the fixture, for example, close a network connection. This method is called after a test is executed.
     */
    protected void tearDown() throws Exception
    {
        // Call onTearDownInTransaction and end transaction if the transaction
        // is still active.
        if (this.transactionStatus != null && !this.transactionStatus.isCompleted())
        {
            try
            {
                onTearDownInTransaction();
            }
            finally
            {
                endTransaction();
            }
        }

        // Call onTearDownAfterTransaction if there was at least one
        // transaction, even if it has been completed early through an
        // endTransaction() call.
        if (this.transactionsStarted > 0)
        {
            onTearDownAfterTransaction();
        }
        ((ClassPathXmlApplicationContext) context).close();
    }

    /**
     * Subclasses can override this method to run invariant tests here. The transaction is <i>still active</i> at this
     * point, so any changes made in the transaction will still be visible. However, there is no need to clean up the
     * database, as a rollback will follow automatically.
     * <p>
     * <b>NB:</b> Not called if there is no actual transaction, for example due to no transaction manager being
     * provided in the application context.
     * 
     * @throws Exception simply let any exception propagate
     */
    protected void onTearDownInTransaction() throws Exception
    {
    }

    /**
     * Subclasses can override this method to perform cleanup after a transaction here. At this point, the transaction
     * is <i>not active anymore</i>.
     * 
     * @throws Exception simply let any exception propagate
     */
    protected void onTearDownAfterTransaction() throws Exception
    {
    }

    /**
     * Determines whether or not to rollback transactions for the current test.
     * <p>
     * The default implementation delegates to {@link #isDefaultRollback()}. Subclasses can override as necessary.
     */
    protected boolean isRollback()
    {
        return isDefaultRollback();
    }

    /**
     * Get the <em>default rollback</em> flag for this test.
     * 
     * @see #setDefaultRollback(boolean)
     * @return The <em>default rollback</em> flag.
     */
    protected boolean isDefaultRollback()
    {
        return this.defaultRollback;
    }

    private String getNameSuffix()
    {
        return "" + fileCount++;
    }

}
