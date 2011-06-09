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
package org.alfresco.solr;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.core.SolrCore;
import org.apache.solr.search.QueryParsing;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.CommitUpdateCommand;
import org.apache.solr.update.DeleteUpdateCommand;
import org.apache.solr.update.DirectUpdateHandler2;
import org.apache.solr.update.MergeIndexesCommand;
import org.apache.solr.update.RollbackUpdateCommand;
import org.apache.solr.update.UpdateHandler;
import org.apache.solr.util.RefCounted;

/**
 * <code>DirectUpdateHandler2</code> implements an UpdateHandler where documents are added directly to the main Lucene
 * index as opposed to adding to a separate smaller index. For this reason, not all combinations to/from pending and
 * committed are supported. This version supports efficient removal of duplicates on a commit. It works by maintaining a
 * related count for every document being added or deleted. At commit time, for every id with a count, all but the last
 * "count" docs with that id are deleted.
 * <p>
 * Supported add command parameters:
 * <TABLE BORDER>
 * <TR>
 * <TH>allowDups</TH>
 * <TH>overwritePending</TH>
 * <TH>overwriteCommitted</TH>
 * <TH>efficiency</TH>
 * </TR>
 * <TR>
 * <TD>false</TD>
 * <TD>false</TD>
 * <TD>true</TD>
 * <TD>fast</TD>
 * </TR>
 * <TR>
 * <TD>true or false</TD>
 * <TD>true</TD>
 * <TD>true</TD>
 * <TD>fast</TD>
 * </TR>
 * <TR>
 * <TD>true</TD>
 * <TD>false</TD>
 * <TD>false</TD>
 * <TD>fastest</TD>
 * </TR>
 * </TABLE>
 * <p>
 * Supported delete commands:
 * <TABLE BORDER>
 * <TR>
 * <TH>command</TH>
 * <TH>fromPending</TH>
 * <TH>fromCommitted</TH>
 * <TH>efficiency</TH>
 * </TR>
 * <TR>
 * <TD>delete</TD>
 * <TD>true</TD>
 * <TD>true</TD>
 * <TD>fast</TD>
 * </TR>
 * <TR>
 * <TD>deleteByQuery</TD>
 * <TD>true</TD>
 * <TD>true</TD>
 * <TD>very slow*</TD>
 * </TR>
 * </TABLE>
 * <p>
 * deleteByQuery causes a commit to happen (close current index writer, open new index reader) before it can be
 * processed. If deleteByQuery functionality is needed, it's best if they can be batched and executed together so they
 * may share the same index reader.
 * 
 * @version $Id: DirectUpdateHandler2.java 824380 2009-10-12 15:18:08Z koji $
 * @since solr 0.9
 */

public class AlfrescoUpdateHandler extends UpdateHandler
{

    // stats
    AtomicLong addCommands = new AtomicLong();

    AtomicLong addCommandsCumulative = new AtomicLong();

    AtomicLong deleteByIdCommands = new AtomicLong();

    AtomicLong deleteByIdCommandsCumulative = new AtomicLong();

    AtomicLong deleteByQueryCommands = new AtomicLong();

    AtomicLong deleteByQueryCommandsCumulative = new AtomicLong();

    AtomicLong expungeDeleteCommands = new AtomicLong();

    AtomicLong mergeIndexesCommands = new AtomicLong();

    AtomicLong commitCommands = new AtomicLong();

    AtomicLong optimizeCommands = new AtomicLong();

    AtomicLong rollbackCommands = new AtomicLong();

    AtomicLong numDocsPending = new AtomicLong();

    AtomicLong numErrors = new AtomicLong();

    AtomicLong numErrorsCumulative = new AtomicLong();

    ConcurrentHashMap<Long, Long> deletedLeaves = new ConcurrentHashMap<Long, Long>();

    ConcurrentHashMap<Long, Long> addedLeaves = new ConcurrentHashMap<Long, Long>();

    ConcurrentHashMap<Long, Long> updatedLeaves = new ConcurrentHashMap<Long, Long>();
    
    ConcurrentHashMap<Long, Long> deletedAux = new ConcurrentHashMap<Long, Long>();

    ConcurrentHashMap<Long, Long> addedAux = new ConcurrentHashMap<Long, Long>();

    ConcurrentHashMap<Long, Long> updatedAux = new ConcurrentHashMap<Long, Long>();
    
    ConcurrentHashMap<Long, Long> deletedAcl = new ConcurrentHashMap<Long, Long>();

    ConcurrentHashMap<Long, Long> addedAcl = new ConcurrentHashMap<Long, Long>();

    ConcurrentHashMap<Long, Long> updatedAcl = new ConcurrentHashMap<Long, Long>();
    
    ConcurrentHashMap<Long, Long> deletedTx = new ConcurrentHashMap<Long, Long>();

    ConcurrentHashMap<Long, Long> addedTx = new ConcurrentHashMap<Long, Long>();
    
    ConcurrentHashMap<Long, Long> updatedTx = new ConcurrentHashMap<Long, Long>();
    
    AtomicBoolean deleteAll = new AtomicBoolean(false);
    
    AtomicBoolean checkCache = new AtomicBoolean(false);

    // tracks when auto-commit should occur
    protected final CommitTracker tracker;

    // iwCommit protects internal data and open/close of the IndexWriter and
    // is a mutex. Any use of the index writer should be protected by iwAccess,
    // which admits multiple simultaneous acquisitions. iwAccess is
    // mutually-exclusive with the iwCommit lock.
    protected final Lock iwAccess, iwCommit;

    protected IndexWriter writer;

    public AlfrescoUpdateHandler(SolrCore core) throws IOException
    {
        super(core);

        ReadWriteLock rwl = new ReentrantReadWriteLock();
        iwAccess = rwl.readLock();
        iwCommit = rwl.writeLock();

        tracker = new CommitTracker();
    }

    // must only be called when iwCommit lock held
    private void deleteAll() throws IOException
    {
        SolrCore.log.info(core.getLogId() + "REMOVING ALL DOCUMENTS FROM INDEX");
        closeWriter();
        writer = createMainIndexWriter("DirectUpdateHandler2", true);
    }

    // must only be called when iwCommit lock held
    protected void openWriter() throws IOException
    {
        if (writer == null)
        {
            writer = createMainIndexWriter("DirectUpdateHandler2", false);
        }
    }

    // must only be called when iwCommit lock held
    protected void closeWriter() throws IOException
    {
        try
        {
            numDocsPending.set(0);
            if (writer != null)
                writer.close();
        }
        finally
        {
            // if an exception causes the writelock to not be
            // released, we could try and delete it here
            writer = null;
        }
    }

    // must only be called when iwCommit lock held
    protected void rollbackWriter() throws IOException
    {
        try
        {
            numDocsPending.set(0);
            if (writer != null)
                writer.rollback();
        }
        finally
        {
            writer = null;
        }
    }

    public int addDoc(AddUpdateCommand cmd) throws IOException
    {
        addCommands.incrementAndGet();
        addCommandsCumulative.incrementAndGet();
        int rc = -1;

        // if there is no ID field, use allowDups
        if (idField == null)
        {
            cmd.allowDups = true;
            cmd.overwriteCommitted = false;
            cmd.overwritePending = false;
        }

        iwAccess.lock();
        try
        {

            // We can't use iwCommit to protect internal data here, since it would
            // block other addDoc calls. Hence, we synchronize to protect internal
            // state. This is safe as all other state-changing operations are
            // protected with iwCommit (which iwAccess excludes from this block).
            synchronized (this)
            {
                // adding document -- prep writer
                openWriter();
                tracker.addedDocument(cmd.commitWithin);
            } // end synchronized block

            // this is the only unsynchronized code in the iwAccess block, which
            // should account for most of the time
            Term updateTerm = null;

            if (cmd.overwriteCommitted || cmd.overwritePending)
            {
                if (cmd.indexedId == null)
                {
                    cmd.indexedId = getIndexedId(cmd.doc);
                }
                Term idTerm = this.idTerm.createTerm(cmd.indexedId);
                boolean del = false;
                if (cmd.updateTerm == null)
                {
                    updateTerm = idTerm;
                }
                else
                {
                    del = true;
                    updateTerm = cmd.updateTerm;
                }

                if(idTerm.text().equals("CHECK_CACHE"))
                {
                    checkCache.set(true);
                }
                else
                {

                    writer.updateDocument(updateTerm, cmd.getLuceneDocument(schema));
                    if (del)
                    { // ensure id remains unique
                        BooleanQuery bq = new BooleanQuery();
                        bq.add(new BooleanClause(new TermQuery(updateTerm), Occur.MUST_NOT));
                        bq.add(new BooleanClause(new TermQuery(idTerm), Occur.MUST));
                        writer.deleteDocuments(bq);
                    }

                    DbidAndAclid dbidAncAclid = new DbidAndAclid(cmd.getLuceneDocument(schema));
                    switch(dbidAncAclid.getDocType())
                    {
                    case ACL:
                        updatedAcl.put(dbidAncAclid.aclid, dbidAncAclid.aclid);
                        break;
                    case AUX:
                        updatedAux.put(dbidAncAclid.dbid, dbidAncAclid.dbid);
                        break;
                    case LEAF:
                        updatedLeaves.put(dbidAncAclid.dbid, dbidAncAclid.dbid);
                        break;
                    case TX:
                        updatedTx.put(dbidAncAclid.txid, dbidAncAclid.txid);
                        break;
                    case UNKOWN:
                    default:
                        break;
                    }
                }
            }
            else
            {
                if(idTerm.text().equals("CHECK_CACHE"))
                {
                    checkCache.set(true);
                }
                else
                {
                    // allow duplicates
                    writer.addDocument(cmd.getLuceneDocument(schema));  

                    DbidAndAclid dbidAncAclid = new DbidAndAclid(cmd.getLuceneDocument(schema));
                    switch(dbidAncAclid.getDocType())
                    {
                    case ACL:
                        addedAcl.put(dbidAncAclid.aclid, dbidAncAclid.aclid);
                        break;
                    case AUX:
                        addedAux.put(dbidAncAclid.dbid, dbidAncAclid.aclid);
                        break;
                    case LEAF:
                        addedLeaves.put(dbidAncAclid.dbid, dbidAncAclid.dbid);
                        break;
                    case TX:
                        addedTx.put(dbidAncAclid.txid, dbidAncAclid.txid);
                        break;
                    case UNKOWN:
                    default:
                        break;
                    }
                }
            }

            rc = 1;
        }
        finally
        {
            iwAccess.unlock();
            if (rc != 1)
            {
                numErrors.incrementAndGet();
                numErrorsCumulative.incrementAndGet();
            }
            else
            {
                numDocsPending.incrementAndGet();
            }
        }

        return rc;
    }

    // could return the number of docs deleted, but is that always possible to know???
    public void delete(DeleteUpdateCommand cmd) throws IOException
    {
        deleteByIdCommands.incrementAndGet();
        deleteByIdCommandsCumulative.incrementAndGet();

        if (!cmd.fromPending && !cmd.fromCommitted)
        {
            numErrors.incrementAndGet();
            numErrorsCumulative.incrementAndGet();
            throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "meaningless command: " + cmd);
        }
        if (!cmd.fromPending || !cmd.fromCommitted)
        {
            numErrors.incrementAndGet();
            numErrorsCumulative.incrementAndGet();
            throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "operation not supported" + cmd);
        }

        iwCommit.lock();
        try
        { 
            openWriter();
            Term term = idTerm.createTerm(idFieldType.toInternal(cmd.id));
            writer.deleteDocuments(term);
            
            if(cmd.id.startsWith("LEAF-"))
            {
                Long dbid = Long.valueOf(cmd.id.substring(5));
                deletedLeaves.put(dbid, dbid);
            }
            else if(cmd.id.startsWith("AUX-"))
            {
                Long dbid = Long.valueOf(cmd.id.substring(4));
                deletedAux.put(dbid, dbid);
            }
            else if(cmd.id.startsWith("ACL-"))
            {
                Long aclid = Long.valueOf(cmd.id.substring(4));
                deletedAcl.put(aclid, aclid);
            }
            else if(cmd.id.startsWith("TX-"))
            {
                Long txid = Long.valueOf(cmd.id.substring(4));
                deletedTx.put(txid, txid);
            }
            
        }
        finally
        {
            iwCommit.unlock();
        }

        if (tracker.timeUpperBound > 0)
        {
            tracker.scheduleCommitWithin(tracker.timeUpperBound);
        }
    }

    // why not return number of docs deleted?
    // Depending on implementation, we may not be able to immediately determine the num...
    public void deleteByQuery(DeleteUpdateCommand cmd) throws IOException
    {
        deleteByQueryCommands.incrementAndGet();
        deleteByQueryCommandsCumulative.incrementAndGet();

        if (!cmd.fromPending && !cmd.fromCommitted)
        {
            numErrors.incrementAndGet();
            numErrorsCumulative.incrementAndGet();
            throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "meaningless command: " + cmd);
        }
        if (!cmd.fromPending || !cmd.fromCommitted)
        {
            numErrors.incrementAndGet();
            numErrorsCumulative.incrementAndGet();
            throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "operation not supported" + cmd);
        }

        boolean madeIt = false;
        boolean delAll = false;
        try
        {
            Query q = QueryParsing.parseQuery(cmd.query, schema);
            delAll = MatchAllDocsQuery.class == q.getClass();

            iwCommit.lock();
            try
            {
                if (delAll)
                {
                    deleteAll();
                    deleteAll.set(true);
                }
                else
                {
                    throw new UnsupportedOperationException();
                    //openWriter();
                    //writer.deleteDocuments(q);
                }
            }
            finally
            {
                iwCommit.unlock();
            }

            madeIt = true;

            if (tracker.timeUpperBound > 0)
            {
                tracker.scheduleCommitWithin(tracker.timeUpperBound);
            }
        }
        finally
        {
            if (!madeIt)
            {
                numErrors.incrementAndGet();
                numErrorsCumulative.incrementAndGet();
            }
        }
    }

    public int mergeIndexes(MergeIndexesCommand cmd) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    public void forceOpenWriter() throws IOException
    {
        iwCommit.lock();
        try
        {
            openWriter();
        }
        finally
        {
            iwCommit.unlock();
        }
    }

    public void commit(CommitUpdateCommand cmd) throws IOException
    {

        if (cmd.optimize)
        {
            optimizeCommands.incrementAndGet();
        }
        else
        {
            commitCommands.incrementAndGet();
            if (cmd.expungeDeletes)
                expungeDeleteCommands.incrementAndGet();
        }

        Future[] waitSearcher = null;
        if (cmd.waitSearcher)
        {
            waitSearcher = new Future[1];
        }

        boolean error = true;
        iwCommit.lock();
        try
        {
            log.info("start " + cmd);

            if (cmd.optimize)
            {
                openWriter();
                writer.optimize(cmd.maxOptimizeSegments);
            }
            else if (cmd.expungeDeletes)
            {
                openWriter();
                writer.expungeDeletes();
            }

            closeWriter();

            callPostCommitCallbacks();
            if (cmd.optimize)
            {
                callPostOptimizeCallbacks();
            }
            
            // Add tracking data to the old searcher
            RefCounted<SolrIndexSearcher> refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher oldSearcher = refCounted.get();
            oldSearcher.cacheInsert(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_ADDED_LEAVES, addedLeaves);
            oldSearcher.cacheInsert(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_ADDED_AUX, addedAux);
            oldSearcher.cacheInsert(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_ADDED_ACL, addedAcl);
            oldSearcher.cacheInsert(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_UPDATED_LEAVES, updatedLeaves);
            oldSearcher.cacheInsert(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_UPDATED_AUX, updatedAux);
            oldSearcher.cacheInsert(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_UPDATED_ACL, updatedAcl);
            oldSearcher.cacheInsert(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_DELETED_LEAVES, deletedLeaves);
            oldSearcher.cacheInsert(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_DELETED_AUX, deletedAux);
            oldSearcher.cacheInsert(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_DELETED_ACL, deletedAcl);
            oldSearcher.cacheInsert(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_DELETE_ALL, deleteAll);
            oldSearcher.cacheInsert(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_CHECK_CACHE, checkCache);
            
            oldSearcher.cacheInsert(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_ADDED_TX, addedTx);
            oldSearcher.cacheInsert(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_DELETED_TX, deletedTx);
            oldSearcher.cacheInsert(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_UPDATED_TX, updatedTx);
            refCounted.decref();
            
            // open a new searcher in the sync block to avoid opening it
            // after a deleteByQuery changed the index, or in between deletes
            // and adds of another commit being done.
            core.getSearcher(true, false, waitSearcher);

            // reset commit tracking
            tracker.didCommit();

            log.info("end_commit_flush");

            addedLeaves = new ConcurrentHashMap<Long, Long>();
            addedAux = new ConcurrentHashMap<Long, Long>();
            addedAcl = new ConcurrentHashMap<Long, Long>();
            updatedLeaves = new ConcurrentHashMap<Long, Long>();
            updatedAux = new ConcurrentHashMap<Long, Long>();
            updatedAcl = new ConcurrentHashMap<Long, Long>();
            deletedLeaves = new ConcurrentHashMap<Long, Long>();
            deletedAux = new ConcurrentHashMap<Long, Long>();
            deletedAcl = new ConcurrentHashMap<Long, Long>();
        
            deleteAll = new AtomicBoolean(false);
            checkCache = new AtomicBoolean(false);
            
            addedTx = new ConcurrentHashMap<Long, Long>();
            deletedTx = new ConcurrentHashMap<Long, Long>();
            updatedTx = new ConcurrentHashMap<Long, Long>();
            
            error = false;
        }
        finally
        {
            iwCommit.unlock();
            addCommands.set(0);
            deleteByIdCommands.set(0);
            deleteByQueryCommands.set(0);
            numErrors.set(error ? 1 : 0);
            
           
           
        }

        // if we are supposed to wait for the searcher to be registered, then we should do it
        // outside of the synchronized block so that other update operations can proceed.
        if (waitSearcher != null && waitSearcher[0] != null)
        {
            try
            {
                waitSearcher[0].get();
            }
            catch (InterruptedException e)
            {
                SolrException.log(log, e);
            }
            catch (ExecutionException e)
            {
                SolrException.log(log, e);
            }
        }
    }

    /**
     * @since Solr 1.4
     */
    public void rollback(RollbackUpdateCommand cmd) throws IOException
    {

        rollbackCommands.incrementAndGet();

        boolean error = true;
        iwCommit.lock();
        try
        {
            log.info("start " + cmd);

            rollbackWriter();

            // callPostRollbackCallbacks();

            // reset commit tracking
            tracker.didRollback();

            log.info("end_rollback");

            addedLeaves = new ConcurrentHashMap<Long, Long>();
            addedAux = new ConcurrentHashMap<Long, Long>();
            addedAcl = new ConcurrentHashMap<Long, Long>();
            updatedLeaves = new ConcurrentHashMap<Long, Long>();
            updatedAux = new ConcurrentHashMap<Long, Long>();
            updatedAcl = new ConcurrentHashMap<Long, Long>();
            deletedLeaves = new ConcurrentHashMap<Long, Long>();
            deletedAux = new ConcurrentHashMap<Long, Long>();
            deletedAcl = new ConcurrentHashMap<Long, Long>();
        
            deleteAll = new AtomicBoolean(false);
            checkCache = new AtomicBoolean(false);
            
            addedTx = new ConcurrentHashMap<Long, Long>();
            deletedTx = new ConcurrentHashMap<Long, Long>();
            updatedTx = new ConcurrentHashMap<Long, Long>();
            
            error = false;
        }
        finally
        {
            iwCommit.unlock();
            addCommandsCumulative.set(addCommandsCumulative.get() - addCommands.getAndSet(0));
            deleteByIdCommandsCumulative.set(deleteByIdCommandsCumulative.get() - deleteByIdCommands.getAndSet(0));
            deleteByQueryCommandsCumulative.set(deleteByQueryCommandsCumulative.get() - deleteByQueryCommands.getAndSet(0));
            numErrors.set(error ? 1 : 0);
            
            
        }
    }

    public void close() throws IOException
    {
        log.info("closing " + this);
        iwCommit.lock();
        try
        {
            // cancel any pending operations
            if (tracker.pending != null)
            {
                tracker.pending.cancel(true);
                tracker.pending = null;
            }
            tracker.scheduler.shutdown();
            closeWriter();
        }
        finally
        {
            iwCommit.unlock();
        }
        log.info("closed " + this);
    }

    /**
     * Helper class for tracking autoCommit state. Note: This is purely an implementation detail of autoCommit and will
     * definitely change in the future, so the interface should not be relied-upon Note: all access must be
     * synchronized.
     */
    class CommitTracker implements Runnable
    {
        // scheduler delay for maxDoc-triggered autocommits
        public final int DOC_COMMIT_DELAY_MS = 250;

        // settings, not final so we can change them in testing
        int docsUpperBound;

        long timeUpperBound;

        private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        private ScheduledFuture pending;

        // state
        long docsSinceCommit;

        int autoCommitCount = 0;

        long lastAddedTime = -1;

        public CommitTracker()
        {
            docsSinceCommit = 0;
            pending = null;

            docsUpperBound = core.getSolrConfig().getUpdateHandlerInfo().autoCommmitMaxDocs; // getInt("updateHandler/autoCommit/maxDocs",
            // -1);
            timeUpperBound = core.getSolrConfig().getUpdateHandlerInfo().autoCommmitMaxTime; // getInt("updateHandler/autoCommit/maxTime",
            // -1);

            SolrCore.log.info("AutoCommit: " + this);
        }

        /** schedule individual commits */
        public synchronized void scheduleCommitWithin(long commitMaxTime)
        {
            _scheduleCommitWithin(commitMaxTime);
        }

        private void _scheduleCommitWithin(long commitMaxTime)
        {
            // Check if there is a commit already scheduled for longer then this time
            if (pending != null && pending.getDelay(TimeUnit.MILLISECONDS) >= commitMaxTime)
            {
                pending.cancel(false);
                pending = null;
            }

            // schedule a new commit
            if (pending == null)
            {
                pending = scheduler.schedule(this, commitMaxTime, TimeUnit.MILLISECONDS);
            }
        }

        /**
         * Indicate that documents have been added
         */
        public void addedDocument(int commitWithin)
        {
            docsSinceCommit++;
            lastAddedTime = System.currentTimeMillis();
            // maxDocs-triggered autoCommit
            if (docsUpperBound > 0 && (docsSinceCommit > docsUpperBound))
            {
                _scheduleCommitWithin(DOC_COMMIT_DELAY_MS);
            }

            // maxTime-triggered autoCommit
            long ctime = (commitWithin > 0) ? commitWithin : timeUpperBound;
            if (ctime > 0)
            {
                _scheduleCommitWithin(ctime);
            }
        }

        /** Inform tracker that a commit has occurred, cancel any pending commits */
        public void didCommit()
        {
            if (pending != null)
            {
                pending.cancel(false);
                pending = null; // let it start another one
            }
            docsSinceCommit = 0;
        }

        /** Inform tracker that a rollback has occurred, cancel any pending commits */
        public void didRollback()
        {
            if (pending != null)
            {
                pending.cancel(false);
                pending = null; // let it start another one
            }
            docsSinceCommit = 0;
        }

        /** This is the worker part for the ScheduledFuture **/
        public synchronized void run()
        {
            long started = System.currentTimeMillis();
            try
            {
                CommitUpdateCommand command = new CommitUpdateCommand(false);
                command.waitFlush = true;
                command.waitSearcher = true;
                // no need for command.maxOptimizeSegments = 1; since it is not optimizing
                commit(command);
                autoCommitCount++;
            }
            catch (Exception e)
            {
                log.error("auto commit error...");
                e.printStackTrace();
            }
            finally
            {
                pending = null;
            }

            // check if docs have been submitted since the commit started
            if (lastAddedTime > started)
            {
                if (docsUpperBound > 0 && docsSinceCommit > docsUpperBound)
                {
                    pending = scheduler.schedule(this, 100, TimeUnit.MILLISECONDS);
                }
                else if (timeUpperBound > 0)
                {
                    pending = scheduler.schedule(this, timeUpperBound, TimeUnit.MILLISECONDS);
                }
            }
        }

        // to facilitate testing: blocks if called during commit
        public synchronized int getCommitCount()
        {
            return autoCommitCount;
        }

        public String toString()
        {
            if (timeUpperBound > 0 || docsUpperBound > 0)
            {
                return (timeUpperBound > 0 ? ("if uncommited for " + timeUpperBound + "ms; ") : "") + (docsUpperBound > 0 ? ("if " + docsUpperBound + " uncommited docs ") : "");

            }
            else
            {
                return "disabled";
            }
        }
    }

    // ///////////////////////////////////////////////////////////////////
    // SolrInfoMBean stuff: Statistics and Module Info
    // ///////////////////////////////////////////////////////////////////

    public String getName()
    {
        return DirectUpdateHandler2.class.getName();
    }

    public String getVersion()
    {
        return SolrCore.version;
    }

    public String getDescription()
    {
        return "Update handler that efficiently directly updates the on-disk main lucene index";
    }

    public Category getCategory()
    {
        return Category.UPDATEHANDLER;
    }

    public String getSourceId()
    {
        return "$Id: DirectUpdateHandler2.java 824380 2009-10-12 15:18:08Z koji $";
    }

    public String getSource()
    {
        return "$URL: https://svn.apache.org/repos/asf/lucene/solr/branches/branch-1.4/src/java/org/apache/solr/update/DirectUpdateHandler2.java $";
    }

    public URL[] getDocs()
    {
        return null;
    }

    public NamedList getStatistics()
    {
        NamedList lst = new SimpleOrderedMap();
        lst.add("commits", commitCommands.get());
        if (tracker.docsUpperBound > 0)
        {
            lst.add("autocommit maxDocs", tracker.docsUpperBound);
        }
        if (tracker.timeUpperBound > 0)
        {
            lst.add("autocommit maxTime", "" + tracker.timeUpperBound + "ms");
        }
        lst.add("autocommits", tracker.autoCommitCount);
        lst.add("optimizes", optimizeCommands.get());
        lst.add("rollbacks", rollbackCommands.get());
        lst.add("expungeDeletes", expungeDeleteCommands.get());
        lst.add("docsPending", numDocsPending.get());
        // pset.size() not synchronized, but it should be fine to access.
        // lst.add("deletesPending", pset.size());
        lst.add("adds", addCommands.get());
        lst.add("deletesById", deleteByIdCommands.get());
        lst.add("deletesByQuery", deleteByQueryCommands.get());
        lst.add("errors", numErrors.get());
        lst.add("cumulative_adds", addCommandsCumulative.get());
        lst.add("cumulative_deletesById", deleteByIdCommandsCumulative.get());
        lst.add("cumulative_deletesByQuery", deleteByQueryCommandsCumulative.get());
        lst.add("cumulative_errors", numErrorsCumulative.get());
        return lst;
    }

    public String toString()
    {
        return "DirectUpdateHandler2" + getStatistics();
    }
 
    
    static enum DocType
    {
        LEAF, AUX, ACL, UNKOWN, TX;
    }
    
    static class DbidAndAclid
    {
        Long dbid;
        Long aclid;
        Long txid;
        
        
        DbidAndAclid(Document doc) 
        {
            Fieldable[] dbidField = doc.getFieldables("DBID");
            if((dbidField != null) && (dbidField.length > 0))
            {
                dbid = Long.valueOf(dbidField[0].stringValue());
            }
            
            Fieldable[] aclField = doc.getFieldables("ACLID");
            if((aclField != null) && (aclField.length > 0))
            {
                aclid = Long.valueOf(aclField[0].stringValue());
            }
            
            Fieldable[] idField = doc.getFieldables("TXID");
            if((idField != null) && (idField.length > 0))
            {
                
                txid = Long.valueOf(idField[0].stringValue());
            }

        }
        
        DbidAndAclid()
        {
            super();
        }
       
        DocType getDocType()
        {
            if(dbid == null)
            {
                if(aclid == null)
                {
                    if(txid == null)
                    {
                        return DocType.UNKOWN;
                    }
                    else
                    {
                        return DocType.TX;
                    }
                }
                else
                {
                    return DocType.ACL;
                }
            }
            else
            {
                if(aclid == null)
                {
                    return DocType.LEAF;
                }
                else
                {
                    return DocType.AUX;
                }
            }
        }
    }
}