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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.solr.AlfrescoSolrEventListener.CacheEntry;
import org.alfresco.util.EqualsHelper;
import org.alfresco.util.NumericEncoder;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.util.OpenBitSet;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrEventListener;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.FastLRUCache;
import org.apache.solr.search.SolrCache;
import org.apache.solr.search.SolrIndexReader;
import org.apache.solr.search.SolrIndexSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andy
 */
public class AlfrescoSolrEventListener implements SolrEventListener
{
    protected final static Logger log = LoggerFactory.getLogger(AlfrescoSolrEventListener.class);

    public static final String KEY_ADDED_LEAVES = "KEY_ADDED_LEAVES";

    public static final String KEY_ADDED_ACL = "KEY_ADDED_ACL";

    public static final String KEY_ADDED_AUX = "KEY_ADDED_AUX";

    public static final String KEY_ADDED_TX = "KEY_ADDED_TX";

    public static final String KEY_ADDED_ACL_TX = "KEY_ADDED_ACL_TX";
    
    public static final String KEY_CHECK_INDEXED_BY_DOC_ID_LIST = "KEY_CHECK_INDEXED_BY_DOC_ID_LIST";
    
    public static final String KEY_DELETED_LEAVES = "KEY_DELETED_LEAVES";

    public static final String KEY_DELETED_ACL = "KEY_DELETED_ACL";

    public static final String KEY_DELETED_AUX = "KEY_DELETED_AUX";

    public static final String KEY_DELETED_TX = "KEY_DELETED_TX";

    public static final String KEY_DELETED_ACL_TX = "KEY_DELETED_ACL_TX";

    public static final String KEY_UPDATED_LEAVES = "KEY_UPDATED_LEAVES";

    public static final String KEY_UPDATED_ACL = "KEY_UPDATED_ALC";

    public static final String KEY_UPDATED_AUX = "KEY_UPDATED_AUX";

    public static final String KEY_UPDATED_TX = "KEY_UPDATED_TX";

    public static final String KEY_UPDATED_ACL_TX = "KEY_UPDATED_ACL_TX";

    public static final String KEY_DELETE_ALL = "KEY_DELETE_ALL";

    public static String ALFRESCO_CACHE = "alfrescoCache";
    
    public static String ALFRESCO_ARRAYLIST_CACHE = "alfrescoArrayListCache";

    public static String ALFRESCO_AUTHORITY_CACHE = "alfrescoAuthorityCache";

    public static String ALFRESCO_PATH_CACHE = "alfrescoPathCache";
    
    public static String ALFRESCO_READER_TO_ACL_IDS_CACHE = "alfrescoReaderToAclIdsCache";

    // Full cache of doc position to DBID, and leaf and path oposition
    public static String KEY_DBID_LEAF_PATH_BY_DOC_ID = "KEY_DBID_LEAF_PATH_BY_DOC_ID";

    // Cache of ACL doc to act id
    public static String KEY_ACL_ID_BY_DOC_ID = "KEY_ACL_ID_BY_DOC_ID";

    public static String KEY_TX_ID_BY_DOC_ID = "KEY_TX_ID_BY_DOC_ID";

    public static String KEY_ACL_TX_ID_BY_DOC_ID = "KEY_ACL_TX_ID_BY_DOC_ID";

    public static String KEY_GLOBAL_READERS = "KEY_GLOBAL_READERS";

    public static String KEY_ALL_LEAF_DOCS = "KEY_ALL_LEAF_DOCS";

    public static String KEY_ACL_LOOKUP = "KEY_ACL_LOOKUP";

    public static String KEY_DBID_LEAF_PATH_BY_ACL_ID_THEN_LEAF = "KEY_DBID_LEAF_PATH_BY_ACL_ID_THEN_LEAF";

    public static String KEY_DBID_LEAF_PATH_BY_OWNER_ID_THEN_LEAF = "KEY_DBID_LEAF_PATH_BY_OWNER_ID_THEN_LEAF";

    public static String KEY_OWNER_LOOKUP = "KEY_OWNER_LOOKUP";

    public static String KEY_CHECK_CACHE = "KEY_CHECK_CACHE";

    public static String KEY_OWNER_ID_MANAGER = "KEY_OWNER_ID_MANAGER";

    public static String KEY_PUBLIC_DOC_SET = "KEY_PUBLIC_DOC_SET";

    private NamedList args;

    private boolean forceCheckCache = false;

    private boolean incrementalCacheRebuild = true;

    private SolrCore core;

    // SOLR will use this constructor if found
    // We can then get at the core to load cofig files etc
    public AlfrescoSolrEventListener(SolrCore core)
    {
        this.core = core;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.solr.core.SolrEventListener#init(org.apache.solr.common.util.NamedList)
     */
    @Override
    public void init(NamedList args)
    {
        this.args = args;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.solr.core.SolrEventListener#newSearcher(org.apache.solr.search.SolrIndexSearcher,
     * org.apache.solr.search.SolrIndexSearcher)
     */
    @Override
    public void newSearcher(SolrIndexSearcher newSearcher, SolrIndexSearcher currentSearcher)
    {
        Properties p = core.getResourceLoader().getCoreProperties();
        boolean doPermissionChecks = Boolean.parseBoolean(p.getProperty("alfresco.doPermissionChecks", "true"));

        SolrIndexReader newReader = newSearcher.getReader();
        log.info("Max " + newReader.maxDoc());
        log.info("Docs " + newReader.numDocs());
        log.info("Deleted " + newReader.numDeletedDocs());

        long startTime = System.nanoTime();

        ResizeableArrayList<CacheEntry> indexedByDocId = (ResizeableArrayList<CacheEntry>) newSearcher.cacheLookup(ALFRESCO_ARRAYLIST_CACHE, KEY_DBID_LEAF_PATH_BY_DOC_ID);
        indexedByDocId.resize(newReader.maxDoc());
        HashSet<String> globalReaders = new HashSet<String>();
        OpenBitSet allLeafDocs = new OpenBitSet(newReader.maxDoc());
        long[] aclIdByDocId = new long[newReader.maxDoc()];
        long[] txByDocId = new long[newReader.maxDoc()];
        long[] aclTxByDocId = new long[newReader.maxDoc()];
        for (int i = 0; i < aclIdByDocId.length; i++)
        {
            aclIdByDocId[i] = -1;
            txByDocId[i] = -1;
            aclTxByDocId[i] = -1;
        }


        OpenBitSet deleted = new OpenBitSet(newReader.maxDoc());
        OwnerIdManager ownerIdManager = new OwnerIdManager();

        HashMap<Long, CacheEntry> unmatchedByDBID = new HashMap<Long, CacheEntry>();

        if ((incrementalCacheRebuild) && currentSearcher != null)
        {
            ResizeableArrayList<CacheEntry> oldIndexedByDocId = (ResizeableArrayList<CacheEntry>) currentSearcher.cacheLookup(ALFRESCO_ARRAYLIST_CACHE, KEY_DBID_LEAF_PATH_BY_DOC_ID);
            long[] oldAclIdByDocId = (long[]) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_ACL_ID_BY_DOC_ID);
            long[] oldTxIdByDocId = (long[]) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_TX_ID_BY_DOC_ID);
            long[] oldAclTxIdByDocId = (long[]) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_ACL_TX_ID_BY_DOC_ID);
            OpenBitSet oldAllLeafDocs = (OpenBitSet) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_ALL_LEAF_DOCS);
            OwnerIdManager oldOwnerIdManager = (OwnerIdManager) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_OWNER_ID_MANAGER);
            ownerIdManager.addAll(oldOwnerIdManager);

            ConcurrentHashMap<Long, Long> addedLeaves = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_ADDED_LEAVES);
            ConcurrentHashMap<Long, Long> addedAux = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_ADDED_AUX);
            ConcurrentHashMap<Long, Long> addedAcl = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_ADDED_ACL);
            ConcurrentHashMap<Long, Long> addedTx = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_ADDED_TX);
            ConcurrentHashMap<Long, Long> addedAclTx = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_ADDED_ACL_TX);
            ConcurrentHashMap<Long, Long> updatedLeaves = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_UPDATED_LEAVES);
            ConcurrentHashMap<Long, Long> updatedAux = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_UPDATED_AUX);
            ConcurrentHashMap<Long, Long> updatedAcl = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_UPDATED_ACL);
            ConcurrentHashMap<Long, Long> updatedTx = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_UPDATED_TX);
            ConcurrentHashMap<Long, Long> updatedAclTx = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_UPDATED_ACL_TX);
            ConcurrentHashMap<Long, Long> deletedLeaves = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_DELETED_LEAVES);
            ConcurrentHashMap<Long, Long> deletedAux = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_DELETED_AUX);
            ConcurrentHashMap<Long, Long> deletedAcl = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_DELETED_ACL);
            ConcurrentHashMap<Long, Long> deletedTx = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_DELETED_TX);
            ConcurrentHashMap<Long, Long> deletedAclTx = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_DELETED_ACL_TX);
            AtomicBoolean deleteAll = (AtomicBoolean) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_DELETE_ALL);
            AtomicBoolean checkCache = (AtomicBoolean) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_CHECK_CACHE);

            if (checkCache == null)
            {
                checkCache = new AtomicBoolean(false);
            }

            boolean hasNew = (addedLeaves.size()
                    + addedAux.size() + addedAcl.size() + addedTx.size() + addedAclTx.size() + updatedLeaves.size() + updatedAux.size() + updatedAcl.size() + updatedTx.size() + updatedAclTx
                    .size()) > 0;

                    if (newReader.maxDoc() == 0)
                    {
                        // nothing to do
                    }
                    else if ((oldIndexedByDocId == null) || (oldAclIdByDocId == null) || (oldTxIdByDocId == null) || (oldAclTxIdByDocId == null) || (oldAllLeafDocs == null) || (oldOwnerIdManager == null))
                    {
                        log.warn("Recover from missing cache");
                        buildCacheForReader(indexedByDocId, allLeafDocs, aclIdByDocId, txByDocId, aclTxByDocId, newReader, 0, newReader.maxDoc(), unmatchedByDBID, ownerIdManager);

                    }
                    else if (deleteAll.get())
                    {
                        buildCacheForReader(indexedByDocId, allLeafDocs, aclIdByDocId, txByDocId, aclTxByDocId, newReader, 0, newReader.maxDoc(), unmatchedByDBID, ownerIdManager);
                    }
                    else
                    {

                        try
                        {
                            SolrIndexReader[] before = currentSearcher.getReader().getSequentialSubReaders();
                            SolrIndexReader[] after = newSearcher.getReader().getSequentialSubReaders();

                            CacheSection[] cacheSectionsBefore = SolrIndexReaderCacheSection.getCacheSections(before);
                            CacheSection[] cacheSectionsAfter = SolrIndexReaderCacheSection.getCacheSections(after);

                            // Copy old to new and apply deletions

                            int currentCache = 0;

                            for (int i = 0; i < oldAclIdByDocId.length; i++)
                            {
                                CacheSection section = cacheSectionsBefore[currentCache];
                                if (section.getStart() + section.getLength() == i)
                                {
                                    currentCache++;
                                    if (currentCache == cacheSectionsBefore.length)
                                    {
                                        currentCache--;
                                    }
                                    section = cacheSectionsBefore[currentCache];
                                }

                                CacheEntry entry = oldIndexedByDocId.get(i);
                                if (entry != null)
                                {
                                    if (entry.getLeaf() == i)
                                    {
                                        // Leaf
                                        if ((updatedLeaves.get(entry.dbid) == null) && (deletedLeaves.get(entry.dbid) == null))
                                        {
                                            // leave
                                        }
                                        else
                                        {
                                            section.addDeletion(i);
                                            deleted.set(i);
                                        }
                                    }
                                    else if (entry.getPath() == i)
                                    {
                                        // Aux
                                        if ((updatedAux.get(entry.dbid) == null) && (deletedAux.get(entry.dbid) == null))
                                        {
                                            // leave
                                        }
                                        else
                                        {
                                            section.addDeletion(i);
                                            deleted.set(i);
                                        }
                                    }
                                }
                                else
                                {
                                    if ((updatedAcl.get(oldAclIdByDocId[i]) != null) || (deletedAcl.get(oldAclIdByDocId[i]) != null))
                                    {
                                        section.addDeletion(i);
                                        deleted.set(i);
                                    }

                                    if ((updatedTx.get(oldTxIdByDocId[i]) != null) || (deletedTx.get(oldTxIdByDocId[i]) != null))
                                    {
                                        section.addDeletion(i);
                                        deleted.set(i);
                                    }

                                    if ((updatedAclTx.get(oldAclTxIdByDocId[i]) != null) || (deletedAclTx.get(oldAclTxIdByDocId[i]) != null))
                                    {
                                        section.addDeletion(i);
                                        deleted.set(i);
                                    }

                                }
                            }
                            LinkedList<CacheMatch> operations = buildCacheUpdateOperations(hasNew, cacheSectionsBefore, cacheSectionsAfter, after);

                            log.info("Cache operatoins ...");
                            for (CacheMatch match : operations)
                            {
                                log.info(match.toString());
                            }

                            CacheUpdateTracker tracker = new CacheUpdateTracker(0, 0, 0);

                            for (CacheMatch match : operations)
                            {
                                match.updateCache(tracker, oldIndexedByDocId, oldAclIdByDocId, oldTxIdByDocId, oldAclTxIdByDocId, indexedByDocId, allLeafDocs, aclIdByDocId, txByDocId,
                                        aclTxByDocId, unmatchedByDBID, deleted, newReader, ownerIdManager);
                            }

                            // Check unmatched

                            int hiddenDocCount = 0;
                            for(Long unmatchedDBID : unmatchedByDBID.keySet())
                            {
                                // hidden docs appear as an unmatched path
                                CacheEntry entry = unmatchedByDBID.get(unmatchedDBID);
                                if( (entry.getLeaf() != 0) && (entry.getPath() == 0))
                                {
                                    // leaf doc with no aux doc;
                                    log.info("Leaf has no AUX doc for DBID "+unmatchedDBID+ " at position "+entry.getLeaf());
                                }
                                if( (entry.getLeaf() == 0) && (entry.getPath() != 0))
                                {
                                    hiddenDocCount++;
                                }
                            }
                            log.info("Cache unindexed/error doc count = "+hiddenDocCount);

                            // Simple position check;

                            boolean simpleCheckOk = true;
                            for(int i = 0; i < indexedByDocId.size(); i++)
                            {
                                CacheEntry entry = indexedByDocId.get(i);
                                if(entry != null)
                                {
                                    if((entry.getLeaf() != i ) && (entry.getPath() != i))
                                    {
                                        log.warn("Core "+newSearcher.getIndexDir());
                                        log.warn("Simple cache caheck failed: Incorrect indexedByDocId at " + i);
                                        log.warn(".. leaf and path doc poistion do not match the doc position     .. " + indexedByDocId.get(i));
                                        simpleCheckOk = false;
                                        break;
                                    }
                                }
                            }

                            if ((simpleCheckOk == false) || forceCheckCache || checkCache.get())
                            {
                                ResizeableArrayList<CacheEntry> checkIndexedByDocId = (ResizeableArrayList<CacheEntry>) currentSearcher.cacheLookup(ALFRESCO_ARRAYLIST_CACHE, KEY_CHECK_INDEXED_BY_DOC_ID_LIST);
                                checkIndexedByDocId.resize(newReader.maxDoc());
                                OpenBitSet checkAllLeafDocs = new OpenBitSet(newReader.maxDoc());
                                long[] checkAclIdByDocId = new long[newReader.maxDoc()];
                                long[] checkTxIdByDocId = new long[newReader.maxDoc()];
                                long[] checkAclTxIdByDocId = new long[newReader.maxDoc()];

                                buildCacheForReader(checkIndexedByDocId, checkAllLeafDocs, checkAclIdByDocId, checkTxIdByDocId, checkAclTxIdByDocId, newReader, 0, newReader.maxDoc(),
                                        new HashMap<Long, CacheEntry>(), ownerIdManager);

                                boolean ok = true;
                                boolean thisTestOk = true;
                                for (int i = 0; i < checkIndexedByDocId.size(); i++)
                                {
                                    if (!EqualsHelper.nullSafeEquals(checkIndexedByDocId.get(i), indexedByDocId.get(i)))
                                    {
                                        if(thisTestOk)
                                        {
                                            log.warn("Core "+newSearcher.getIndexDir());
                                            log.warn("Invalid indexedByDocId at " + i);
                                            log.warn(".. found     .. " + indexedByDocId.get(i));
                                            log.warn(".. expected  .. " + checkIndexedByDocId.get(i));
                                            ok = false;
                                            thisTestOk = false;
                                        }
                                    }
                                }


                                thisTestOk = true;
                                if (!checkAllLeafDocs.equals(allLeafDocs))
                                {
                                    if(thisTestOk)
                                    {
                                        log.warn("Core "+newSearcher.getIndexDir());
                                        log.warn("Invalid AllLeafDocs cache");
                                        ok = false;
                                        thisTestOk= false;
                                    }
                                }

                                thisTestOk = true;
                                for (int i = 0; i < checkAclIdByDocId.length; i++)
                                {
                                    if (checkAclIdByDocId[i] != aclIdByDocId[i])
                                    {


                                        if (thisTestOk)
                                        {
                                            log.warn("Core "+newSearcher.getIndexDir());
                                            log.warn("Invalid AclIdByDocId cache at " + i);
                                            log.warn(".. found    .. " + aclIdByDocId[i]);
                                            log.warn(".. expected .. " + checkAclIdByDocId[i]);
                                            try
                                            {
                                                log.warn(".. expected .. " + newSearcher.doc(i));
                                            }
                                            catch (IOException e)
                                            {
                                                log.error("IO Exception", e);
                                            }
                                            ok = false;
                                            thisTestOk= false;
                                        }

                                    }
                                }

                                thisTestOk = true;
                                for (int i = 0; i < checkTxIdByDocId.length; i++)
                                {
                                    if (checkTxIdByDocId[i] != txByDocId[i])
                                    {


                                        if (thisTestOk)
                                        {
                                            log.warn("Core "+newSearcher.getIndexDir());
                                            log.warn("Invalid txByDocId cache at " + i);
                                            log.warn(".. found    .. " + txByDocId[i]);
                                            log.warn(".. expected .. " + checkTxIdByDocId[i]); 
                                            try
                                            {
                                                log.warn(".. expected .. " + newSearcher.doc(i));
                                            }
                                            catch (IOException e)
                                            {
                                                log.error("IO Exception", e);
                                            }

                                            ok = false;
                                            thisTestOk= false;
                                        }

                                    }
                                }

                                thisTestOk = true;
                                for (int i = 0; i < checkAclTxIdByDocId.length; i++)
                                {
                                    if (checkAclTxIdByDocId[i] != aclTxByDocId[i])
                                    {


                                        if (thisTestOk)
                                        {
                                            log.warn("Core "+newSearcher.getIndexDir());
                                            log.warn("Invalid aclTxByDocId cache at " + i);
                                            log.warn(".. found    .. " + aclTxByDocId[i]);
                                            log.warn(".. expected .. " + checkAclTxIdByDocId[i]);

                                            try
                                            {
                                                log.warn(".. expected .. " + newSearcher.doc(i));
                                            }
                                            catch (IOException e)
                                            {
                                                log.error("IO Exception", e);
                                            }

                                            ok = false;
                                            thisTestOk= false;
                                        }

                                    }
                                }


                                if (!ok)
                                {
                                    indexedByDocId.copyFrom(checkIndexedByDocId);
                                    allLeafDocs = checkAllLeafDocs;
                                    aclIdByDocId = checkAclIdByDocId;
                                    txByDocId = checkTxIdByDocId;
                                    aclTxByDocId = checkAclTxIdByDocId;

                                    log.warn("... Using recomputed cache");
                                }
                                else
                                {
                                    log.info("... cache OK");
                                }

                            }
                        }
                        catch (IllegalStateException ise)
                        {
                            log.info("Cache state error -> rebuilding", ise);
                            buildCacheForReader(indexedByDocId, allLeafDocs, aclIdByDocId, txByDocId, aclTxByDocId, newReader, 0, newReader.maxDoc(), new HashMap<Long, CacheEntry>(), ownerIdManager);
                        }
                    }

        }
        else
        {
            buildCacheForReader(indexedByDocId, allLeafDocs, aclIdByDocId, txByDocId, aclTxByDocId, newReader, 0, newReader.maxDoc(), new HashMap<Long, CacheEntry>(), ownerIdManager);
        }

        long endTime = System.nanoTime();
        log.info("Core cache rebuilt in " + ((endTime - startTime) / (1.0e9)));
        startTime = System.nanoTime();

        int size = doPermissionChecks ? (int) allLeafDocs.cardinality() : 0;

        ResizeableArrayList<CacheEntry> indexedOderedByAclIdThenDoc = (ResizeableArrayList<CacheEntry>) newSearcher.cacheLookup(ALFRESCO_ARRAYLIST_CACHE, KEY_DBID_LEAF_PATH_BY_ACL_ID_THEN_LEAF);
        indexedOderedByAclIdThenDoc.resize(size);
        ResizeableArrayList<CacheEntry> indexedOderedByOwnerIdThenDoc = (ResizeableArrayList<CacheEntry>) newSearcher.cacheLookup(ALFRESCO_ARRAYLIST_CACHE, KEY_DBID_LEAF_PATH_BY_OWNER_ID_THEN_LEAF); 
        indexedOderedByOwnerIdThenDoc.resize(size);
        

        if(doPermissionChecks)
        {
            int doc = -1;
            int pos = 0;
            while ((doc = allLeafDocs.nextSetBit(doc + 1)) != -1)
            {
                CacheEntry entry = indexedByDocId.get(doc);
                indexedOderedByAclIdThenDoc.set(pos, entry);
                indexedOderedByOwnerIdThenDoc.set(pos, entry);
                pos++;
            }
        }

        indexedOderedByAclIdThenDoc.sort(new Comparator<CacheEntry>()
            {
                @Override
                public int compare(CacheEntry o1, CacheEntry o2)
                {
                    if (o2 == null)
                    {
                        if (o1 == null)
                        {
                            return 0;
                        }
    
                        else
                        {
                            return -1; // nulls at the end
                        }
                    }
                    else
                    {
                        if (o1 == null)
                        {
                            return 1;
                        }
                        else
                        {
                            long diff = o1.getAclid() - o2.getAclid();
                            if (diff == 0L)
                            {
                                return o1.getLeaf() - o2.getLeaf();
                            }
                            else
                            {
                                return (diff > 0L) ? 1 : -1;
                            }
                        }
                    }
    
                }
            });

        // build lookups

        HashMap<AclLookUp, AclLookUp> aclLookUp = new HashMap<AclLookUp, AclLookUp>();

        AclLookUp currentAclLookUp = null;
        for (int i = 0; i < indexedOderedByAclIdThenDoc.size(); i++)
        {
            CacheEntry entry = indexedOderedByAclIdThenDoc.get(i);
            if (entry != null)
            {
                if (currentAclLookUp == null)
                {
                    currentAclLookUp = new AclLookUp(entry.getAclid(), i);
                }
                else
                {
                    if (currentAclLookUp.aclid == entry.aclid)
                    {
                        // carry on
                    }
                    else
                    {
                        // acl id has changed - new set
                        currentAclLookUp.setEnd(i);
                        AclLookUp next = new AclLookUp(entry.getAclid(), i);
                        aclLookUp.put(currentAclLookUp, currentAclLookUp);
                        currentAclLookUp = next;
                    }
                }
            }
            else
            {
                // found first null we are done
                if (currentAclLookUp != null)
                {
                    currentAclLookUp.setEnd(i);
                    aclLookUp.put(currentAclLookUp, currentAclLookUp);
                }
                break;
            }
        }
        if (currentAclLookUp != null)
        {
            currentAclLookUp.setEnd(indexedOderedByAclIdThenDoc.size());
            aclLookUp.put(currentAclLookUp, currentAclLookUp);
        }

        indexedOderedByOwnerIdThenDoc.sort(new Comparator<CacheEntry>()
        {

            @Override
            public int compare(CacheEntry o1, CacheEntry o2)
            {
                if (o2 == null)
                {
                    if (o1 == null)
                    {
                        return 0;
                    }

                    else
                    {
                        return -1; // nulls at the end
                    }
                }
                else
                {
                    if (o1 == null)
                    {
                        return 1;
                    }
                    else
                    {
                        int diff = o1.getOwner() - o2.getOwner();
                        if (diff == 0)
                        {
                            return o1.getLeaf() - o2.getLeaf();
                        }
                        else
                        {
                            return diff;
                        }
                    }
                }

            }
        });

        // build lookups

        HashMap<String, OwnerLookUp> ownerLookUp = new HashMap<String, OwnerLookUp>();

        OwnerLookUp currentOwnerLookUp = null;
        for (int i = 0; i < indexedOderedByOwnerIdThenDoc.size(); i++)
        {
            CacheEntry entry = indexedOderedByOwnerIdThenDoc.get(i);
            if (entry != null)
            {
                if (currentOwnerLookUp == null)
                {
                    currentOwnerLookUp = new OwnerLookUp(entry.getOwner(), i);
                }
                else
                {
                    if (currentOwnerLookUp.owner == entry.owner)
                    {
                        // carry on
                    }
                    else
                    {
                        // acl id has changed - new set
                        currentOwnerLookUp.setEnd(i);
                        OwnerLookUp next = new OwnerLookUp(entry.getOwner(), i);
                        try
                        {
                            ownerLookUp.put(ownerIdManager.get(currentOwnerLookUp.owner), currentOwnerLookUp);
                        }
                        catch (IndexOutOfBoundsException e)
                        {
                            log.warn("  " + ownerIdManager);
                            log.warn("  looking for " + currentOwnerLookUp.owner);
                            throw e;
                        }
                        currentOwnerLookUp = next;
                    }
                }
            }
            else
            {
                // found first null we are done
                if (currentOwnerLookUp != null)
                {
                    currentOwnerLookUp.setEnd(i);
                    try
                    {
                        ownerLookUp.put(ownerIdManager.get(currentOwnerLookUp.owner), currentOwnerLookUp);
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        log.warn("  " + ownerIdManager);
                        log.warn("  looking for " + currentOwnerLookUp.owner);
                        throw e;
                    }
                }
                break;
            }
        }
        if (currentOwnerLookUp != null)
        {
            currentOwnerLookUp.setEnd(indexedOderedByOwnerIdThenDoc.size());
            try
            {
                ownerLookUp.put(ownerIdManager.get(currentOwnerLookUp.owner), currentOwnerLookUp);
            }
            catch (IndexOutOfBoundsException e)
            {
                log.warn("  " + ownerIdManager);
                log.warn("  looking for " + currentOwnerLookUp.owner);
                throw e;
            }
        }

        // cache readers and acl doc ids

        //HashMap<String, HashSet<Long>> readerToAclIds = new HashMap<String, HashSet<Long>>();
        BitDocSet publicDocSet = new BitDocSet(new OpenBitSet(newReader.maxDoc()));

        if(doPermissionChecks)
        {
            try
            {
                HashSet<Long> globallyReadableAcls = buildReaderAclIds(newSearcher, "GROUP_EVERYONE", aclIdByDocId);     
                newSearcher.cacheInsert(AlfrescoSolrEventListener.ALFRESCO_READER_TO_ACL_IDS_CACHE, "GROUP_EVERYONE", globallyReadableAcls);

                AclLookUp key = new AclLookUp(0);
                for(Long longAcl : globallyReadableAcls)
                {

                    key.setAclid(longAcl);
                    AlfrescoSolrEventListener.AclLookUp value = aclLookUp.get(key);
                    if(value != null)
                    {
                        for(int i = value.getStart(); i < value.getEnd(); i++)
                        {
                            publicDocSet.add(indexedOderedByAclIdThenDoc.get(i).getLeaf());
                        }
                    }
                }
            }
            catch (IOException e)
            {
                log.error("IO Exception while warming searcher", e);
            }

        }


        // transform to readers to acl ids

        endTime = System.nanoTime();
        log.info("Derived caches rebuilt in " + ((endTime - startTime) / (1.0e9)));

        newSearcher.cacheInsert(ALFRESCO_CACHE, KEY_ACL_ID_BY_DOC_ID, aclIdByDocId);
        newSearcher.cacheInsert(ALFRESCO_CACHE, KEY_TX_ID_BY_DOC_ID, txByDocId);
        newSearcher.cacheInsert(ALFRESCO_CACHE, KEY_ACL_TX_ID_BY_DOC_ID, aclTxByDocId);

        // TODO: Make global readers configurable.
        globalReaders.add(PermissionService.OWNER_AUTHORITY);
        globalReaders.add(PermissionService.ADMINISTRATOR_AUTHORITY);
        globalReaders.add(AuthenticationUtil.getSystemUserName());

        newSearcher.cacheInsert(ALFRESCO_CACHE, KEY_GLOBAL_READERS, globalReaders);

        newSearcher.cacheInsert(ALFRESCO_CACHE, KEY_ALL_LEAF_DOCS, allLeafDocs);

        newSearcher.cacheInsert(ALFRESCO_CACHE, KEY_ACL_LOOKUP, aclLookUp);

        newSearcher.cacheInsert(ALFRESCO_CACHE, KEY_OWNER_LOOKUP, ownerLookUp);

        newSearcher.cacheInsert(ALFRESCO_CACHE, KEY_OWNER_ID_MANAGER, ownerIdManager);

        newSearcher.cacheInsert(ALFRESCO_CACHE, KEY_PUBLIC_DOC_SET, publicDocSet);

        try
        {
            if(currentSearcher != null)
            {
                newSearcher.warm(currentSearcher);
            }
        }
        catch (IOException e)
        {
            log.error("IO Exception while warming searcher", e);
        }

    }

    public HashSet<Long> buildReaderAclIds(SolrIndexSearcher searcher, String authority, long[] aclIdByDocId) throws IOException
    {
        HashSet<Long> aclsAsSet = new HashSet<Long>();

        IndexReader reader = searcher.getReader();
        TermEnum termEnum = reader.terms(new Term("READER", authority));
        try
        {
            Term term = termEnum.term();
            if (term == null)
            {
                return aclsAsSet;
            }
            if (term.field().equals("READER") && term.text().equals(authority))
            {
                TermDocs termDocs = reader.termDocs(term);
                try
                {
                    while (termDocs.next())
                    {
                        int currentDoc = termDocs.doc();
                        long acl = aclIdByDocId[currentDoc];
                        aclsAsSet.add(acl);
                    }
                }
                finally
                {

                    termDocs.close();
                }
                return aclsAsSet;
            }
            else
            {
                return aclsAsSet;
            }
        }
        finally
        {
            termEnum.close();
        }

    }





    /**
     * @param hasNew
     * @param before
     * @param after
     * @return
     */
    public LinkedList<CacheMatch> buildCacheUpdateOperations(boolean hasNew, CacheSection[] before, CacheSection[] after, IndexReader[] afterIndexReaders)
    {
        LinkedList<CacheMatch> operations = new LinkedList<CacheMatch>();

        CacheMatch current = null;
        int currentDocCount = 0;

        int iBefore = 0;
        int iAfter = 0;

        while ((iBefore < before.length) || (iAfter < after.length))
        {
            if (iBefore < before.length)
            {
                if (iAfter < after.length)
                {
                    if (before[iBefore].equals(after[iAfter]))
                    {
                        if (current != null)
                        {
                            operations.add(current);
                            current = null;
                            currentDocCount = 0;
                        }

                        if (before[iBefore].getDocCount() == after[iAfter].getDocCount())
                        {
                            Match match = new Match(after[iAfter].getLength(), after[iAfter].getDocCount(), afterIndexReaders == null ? null : afterIndexReaders[iAfter]);
                            match.addToOldCacheSize(before[iBefore].getLength());
                            operations.add(match);
                        }
                        else if (before[iBefore].getDocCount() > after[iAfter].getDocCount())
                        {
                            if (before[iBefore].getDocCount() - before[iBefore].getNewDeletionsCount() == after[iAfter].getDocCount())
                            {
                                Delete delete = new Delete(after[iAfter].getLength(), after[iAfter].getDocCount(), afterIndexReaders == null ? null : afterIndexReaders[iAfter]);
                                delete.addToOldCacheSize(before[iBefore].getLength());
                                operations.add(delete);
                            }
                            else
                            {
                                throw new IllegalStateException("Doc counts and expected deletes do not match");
                            }

                        }
                        else
                        {
                            throw new IllegalStateException("New section has more than old in match ??");
                        }
                        iAfter++;
                    }
                    else
                    {
                        if (current == null)
                        {
                            int thisCount = before[iBefore].getDocCount() - before[iBefore].getNewDeletionsCount();

                            current = new Merge(after[iAfter].getLength(), after[iAfter].getDocCount(), afterIndexReaders == null ? null : afterIndexReaders[iAfter]);
                            current.addToOldCacheSize(before[iBefore].getLength());
                            currentDocCount = thisCount;
                            if (currentDocCount < current.getFinalDocCount())
                            {
                                // more to come
                            }
                            else if (currentDocCount == current.getFinalDocCount())
                            {
                                operations.add(current);
                                current = null;
                                currentDocCount = 0;

                            }
                            else
                            {
                                throw new IllegalStateException("Merged section has too few docs");
                            }
                            iAfter++;

                        }
                        else
                        {
                            currentDocCount += before[iBefore].getDocCount() - before[iBefore].getNewDeletionsCount();
                            current.addToOldCacheSize(before[iBefore].getLength());
                            if (currentDocCount < current.getFinalDocCount())
                            {
                                // more to come
                            }
                            else if (currentDocCount == current.getFinalDocCount())
                            {
                                operations.add(current);
                                current = null;
                                currentDocCount = 0;
                            }
                            else
                            {
                                throw new IllegalStateException("Merged section has too few docs");
                            }

                        }
                    }
                }
                else
                {
                    if (current == null)
                    {
                        throw new IllegalStateException("More docs but no targets");
                    }
                    else
                    {
                        currentDocCount += before[iBefore].getDocCount() - before[iBefore].getNewDeletionsCount();
                        current.addToOldCacheSize(before[iBefore].getLength());
                        if (currentDocCount < current.getFinalDocCount())
                        {
                            // more to come
                        }
                        else if (currentDocCount == current.getFinalDocCount())
                        {
                            operations.add(current);
                            current = null;
                            currentDocCount = 0;
                        }
                        else
                        {
                            throw new IllegalStateException("Merged section has too few docs");
                        }

                    }
                }
                iBefore++;
            }
            else
            {
                if (iAfter < after.length)
                {
                    if (current != null)
                    {
                        if (hasNew)
                        {
                            if (currentDocCount < current.getFinalDocCount())
                            {
                                operations.add(new MergeAndNew(current));
                                current = null;
                                currentDocCount = 0;
                            }
                            else
                            {
                                operations.add(current);
                                current = null;
                                currentDocCount = 0;
                            }
                        }
                        else
                        {
                            operations.add(current);
                            current = null;
                            currentDocCount = 0;
                        }
                    }

                    else if (hasNew)
                    {
                        operations.add(new New(after[iAfter].getLength(), after[iAfter].getDocCount(), afterIndexReaders == null ? null : afterIndexReaders[iAfter]));
                        iAfter++;
                    }
                    else
                    {
                        // illegal state
                        // New reader and no new data ??
                        throw new IllegalStateException("New sub reader but no new docs ??");
                    }


                }
                else
                {
                    throw new IllegalStateException("Violates loop constraint! ??");
                }
            }
        }
        if (current != null)
        {
            if (hasNew)
            {
                if (currentDocCount < current.getFinalDocCount())
                {
                    operations.add(new MergeAndNew(current));
                    current = null;
                    currentDocCount = 0;
                }
                else
                {
                    operations.add(current);
                    current = null;
                    currentDocCount = 0;
                }
            }
            else
            {
                operations.add(current);
                current = null;
                currentDocCount = 0;
            }
        }
        return operations;
    }

    void buildCacheForReader(List<CacheEntry> cache, OpenBitSet allLeafDocs, long[] aclIdByDocId, long[] txIdByDocId, long[] aclTxIdByDocId, IndexReader reader, int start, int length,
            HashMap<Long, CacheEntry> unmatchedByDBID, OwnerIdManager ownerIdManager)
    {
        // reset leaf range

        int current = start - 1;
        while (((current = allLeafDocs.nextSetBit(current + 1)) < start + length) && (current >= 0))
        {
            allLeafDocs.flip(current);
        }

        // clear remaining

        for (int i = start; i < start + length; i++)
        {
            cache.set(i, null);
            aclIdByDocId[i] = -1;
            txIdByDocId[i] = -1;
            aclTxIdByDocId[i] = -1;
        }
        // find all leaf docs

        try
        {
            TermDocs termDocs = reader.termDocs(new Term("ISNODE", "T"));
            while (termDocs.next())
            {
                allLeafDocs.set(start + termDocs.doc());
            }
            termDocs.close();

        }
        catch (IOException e1)
        {
            log.error("Build cache for reader failed to enumerate ISNODE", e1);
        }

        // walk dbids and build leaf-doc links

        try
        {
            TermEnum termEnum = reader.terms(new Term("DBID", ""));
            TermDocs termDocs = null;
            do
            {
                Term term = termEnum.term();
                if (term == null)
                {
                    break;
                }
                if (term.field().equals("DBID"))
                {
                    Long dbid = NumericEncoder.decodeLong(term.text());

                    if (termDocs == null)
                    {
                        termDocs = reader.termDocs(term);
                    }
                    else
                    {
                        termDocs.seek(term);
                    }
                    while (termDocs.next())
                    {
                        int doc = start + termDocs.doc();
                        CacheEntry entry;
                        if (allLeafDocs.get(doc))
                        {
                            entry = unmatchedByDBID.remove(dbid);
                            if ((entry == null) || (entry.getLeaf() > 0))
                            {
                                entry = new CacheEntry(dbid);
                                unmatchedByDBID.put(dbid, entry);
                            }
                            entry.setLeaf(doc);
                        }
                        else
                        {
                            entry = unmatchedByDBID.remove(dbid);
                            if ((entry == null) || (entry.getPath() > 0))
                            {
                                entry = new CacheEntry(dbid);
                                unmatchedByDBID.put(dbid, entry);
                            }
                            entry.setPath(doc);
                        }
                        entry.setAclid(-1);
                        cache.set(doc, entry);

                    }

                }
                else
                {
                    break;
                }
            }
            while (termEnum.next());

            if (termDocs != null)
            {
                termDocs.close();
            }
            termEnum.close();
        }
        catch (IOException e1)
        {
            log.error("Build cache for reader failed to enumerate DBID", e1);
        }

        // walk acls and build lookups

        try
        {
            TermEnum termEnum = reader.terms(new Term("ACLID", ""));
            TermDocs termDocs = null;
            do
            {
                Term term = termEnum.term();
                if (term == null)
                {
                    break;
                }
                if (term.field().equals("ACLID"))
                {
                    Long aclid = Long.valueOf(NumericEncoder.decodeLong(term.text()));
                    if (termDocs == null)
                    {
                        termDocs = reader.termDocs(term);
                    }
                    else
                    {
                        termDocs.seek(term);
                    }
                    while (termDocs.next())
                    {
                        int doc = start + termDocs.doc();
                        CacheEntry entry = cache.get(doc);
                        if (entry == null)
                        {
                            aclIdByDocId[doc] = aclid;
                        }
                        else
                        {
                            entry.setAclid(aclid);
                        }
                    }

                }
                else
                {
                    break;
                }
            }
            while (termEnum.next());
            if (termDocs != null)
            {
                termDocs.close();
            }
            termEnum.close();
        }
        catch (IOException e1)
        {
            log.error("Build cache for reader failed to enumerate ACLID", e1);
        }

        try
        {
            TermEnum termEnum = reader.terms(new Term("TXID", ""));
            TermDocs termDocs = null;
            do
            {
                Term term = termEnum.term();
                if (term == null)
                {
                    break;
                }
                if (term.field().equals("TXID"))
                {
                    Long txid = Long.valueOf(NumericEncoder.decodeLong(term.text()));
                    if (termDocs == null)
                    {
                        termDocs = reader.termDocs(term);
                    }
                    else
                    {
                        termDocs.seek(term);
                    }
                    while (termDocs.next())
                    {
                        int doc = start + termDocs.doc();
                        CacheEntry entry = cache.get(doc);
                        if (entry == null)
                        {
                            txIdByDocId[doc] = txid;
                        }
                        else
                        {
                            throw new IllegalStateException("Laef and Aux should not have TXID field");
                        }
                    }

                }
                else
                {
                    break;
                }
            }
            while (termEnum.next());
            if (termDocs != null)
            {
                termDocs.close();
            }
            termEnum.close();
        }
        catch (IOException e1)
        {
            log.error("Build cache for reader failed to enumerate TXID", e1);
        }

        try
        {
            TermEnum termEnum = reader.terms(new Term("ACLTXID", ""));
            TermDocs termDocs = null;
            do
            {
                Term term = termEnum.term();
                if (term == null)
                {
                    break;
                }
                if (term.field().equals("ACLTXID"))
                {
                    Long acltxid = Long.valueOf(NumericEncoder.decodeLong(term.text()));
                    if (termDocs == null)
                    {
                        termDocs = reader.termDocs(term);
                    }
                    else
                    {
                        termDocs.seek(term);
                    }
                    while (termDocs.next())
                    {
                        int doc = start + termDocs.doc();
                        CacheEntry entry = cache.get(doc);
                        if (entry == null)
                        {
                            aclTxIdByDocId[doc] = acltxid;
                        }
                        else
                        {
                            throw new IllegalStateException("Laef and Aux should not have ACLTXID field");
                        }
                    }

                }
                else
                {
                    break;
                }
            }
            while (termEnum.next());
            if (termDocs != null)
            {
                termDocs.close();
            }
            termEnum.close();
        }
        catch (IOException e1)
        {
            log.error("Build cache for reader failed to enumerate ACLTXID", e1);
        }

        // walk owner and cache

        try
        {
            TermEnum termEnum = reader.terms(new Term("OWNER", ""));
            TermDocs termDocs = null;
            do
            {
                Term term = termEnum.term();
                if (term == null)
                {
                    break;
                }
                if (term.field().equals("OWNER"))
                {
                    String owner = term.text();
                    Integer ownerId = ownerIdManager.getOwnerId(owner);
                    if (termDocs == null)
                    {
                        termDocs = reader.termDocs(term);
                    }
                    else
                    {
                        termDocs.seek(term);
                    }
                    while (termDocs.next())
                    {
                        int doc = start + termDocs.doc();
                        CacheEntry entry = cache.get(doc);
                        entry.setOwner(ownerId.intValue());
                    }

                }
                else
                {
                    break;
                }
            }
            while (termEnum.next());
            if (termDocs != null)
            {
                termDocs.close();
            }
            termEnum.close();
        }
        catch (IOException e1)
        {
            log.error("Build cache for reader failed to enumerate OWNER", e1);
        }
    }



    /*
     * (non-Javadoc)
     * @see org.apache.solr.core.SolrEventListener#postCommit()
     */
    @Override
    public void postCommit()
    {
        throw new UnsupportedOperationException();
    }

    public static class CacheEntry implements Comparator<CacheEntry>
    {
        int leaf;

        int path;

        long dbid;

        long aclid;

        int owner;

        /**
         * @param doc
         * @param dbid
         */
        public CacheEntry(long dbid)
        {
            this.dbid = dbid;
        }

        public int getLeaf()
        {
            return leaf;
        }

        public void setLeaf(int leaf)
        {
            this.leaf = leaf;
        }

        public int getPath()
        {
            return path;
        }

        public void setPath(int path)
        {
            this.path = path;
        }

        public long getDbid()
        {
            return dbid;
        }

        /**
         * @return the alcid
         */
        public long getAclid()
        {
            return aclid;
        }

        /**
         * @param alcid
         *            the alcid to set
         */
        public void setAclid(long aclid)
        {
            this.aclid = aclid;
        }

        /**
         * @return the owner
         */
        public int getOwner()
        {
            return owner;
        }

        /**
         * @param owner
         *            the owner to set
         */
        public void setOwner(int owner)
        {
            this.owner = owner;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (aclid ^ (aclid >>> 32));
            result = prime * result + (int) (dbid ^ (dbid >>> 32));
            result = prime * result + leaf;
            result = prime * result + owner;
            result = prime * result + path;
            return result;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CacheEntry other = (CacheEntry) obj;
            if (aclid != other.aclid)
                return false;
            if (dbid != other.dbid)
                return false;
            if (leaf != other.leaf)
                return false;
            if (owner != other.owner)
                return false;
            if (path != other.path)
                return false;
            return true;
        }

        @Override
        public String toString()
        {
            return "CacheEntry [dbid=" + dbid + ", leaf=" + leaf + ", path=" + path + ", aclid=" + aclid + ", owner=" + owner + "]";
        }

        /*
         * (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(CacheEntry o1, CacheEntry o2)
        {
            if (o1 == null && o2 == null)
                return 0;
            if (o1 == null)
                return 1;
            if (o2 == null)
                return -1;
            return (o1.getDbid() < o2.getDbid() ? -1 : ((o1.getDbid() == o2.getDbid()) ? 0 : 1));
        }
    }

    public static class AclLookUp
    {
        long aclid;

        int start;

        int end;

        public AclLookUp(long aclid)
        {
            this.aclid = aclid;
        }

        public AclLookUp(long aclid, int start)
        {
            this.aclid = aclid;
            this.start = start;
        }

        public void setEnd(int end)
        {
            this.end = end;
        }

        /**
         * @return the aclid
         */
        public long getAclid()
        {
            return aclid;
        }

        /**
         * @return the start
         */
        public int getStart()
        {
            return start;
        }

        /**
         * @return the end
         */
        public int getEnd()
        {
            return end;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            return (int)(aclid ^ (aclid >>> 32));
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (!(obj instanceof AclLookUp))
                return false;
            AclLookUp other = (AclLookUp) obj;
            if (aclid != other.aclid)
                return false;
            return true;
        }    

        public void setAclid(long aclid)
        {
            this.aclid = aclid;
        }
    }


    public static class OwnerLookUp
    {
        int owner;

        int start;

        int end;

        public OwnerLookUp(int owner)
        {
            this.owner = owner;
        }

        public OwnerLookUp(int owner, int start)
        {
            this.owner = owner;
            this.start = start;
        }

        public void setEnd(int end)
        {
            this.end = end;
        }

        /**
         * @return the owner
         */
        public int getOwner()
        {
            return owner;
        }

        /**
         * @return the start
         */
        public int getStart()
        {
            return start;
        }

        /**
         * @return the end
         */
        public int getEnd()
        {
            return end;
        }

    }

    public interface CacheMatch
    {
        public int getFinalCacheSize();

        public int getOldCacheSize();

        public int getNumberOfOldCaches();

        public void addToOldCacheSize(int increment);

        /**
         * @param tracker
         * @param oldIndexedByDocId
         * @param oldAllLeafDocs
         * @param oldAclIdByDocId
         * @param indexedByDocId
         * @param allLeafDocs
         * @param aclIdByDocId
         * @param unmatchedByDBID
         */
        public void updateCache(CacheUpdateTracker tracker, ResizeableArrayList<CacheEntry> oldIndexedByDocId, long[] oldAclIdByDocId, long[] oldTxByDocId, long[] oldAclTxByDocId,
                ResizeableArrayList<CacheEntry> indexedByDocId, OpenBitSet allLeafDocs, long[] aclIdByDocId, long[] txIdByDocId, long[] aclTxIdByDocId, HashMap<Long, CacheEntry> unmatchedByDBID,
                OpenBitSet deleted, SolrIndexReader reader, OwnerIdManager ownerIdManager);

        public int getFinalDocCount();

        public IndexReader getFinalIndexReader();
    }

    public abstract static class AbstractCacheMatch implements CacheMatch
    {
        protected int finalCacheSize;

        protected int finalDocCount;

        protected int oldCacheSize = 0;

        protected int numberOfOldCaches = 0;

        protected IndexReader finalIndexReader;

        AbstractCacheMatch(int finalCacheSize, int finalDocCount, IndexReader finalIndexReader)
        {
            this.finalCacheSize = finalCacheSize;
            this.finalDocCount = finalDocCount;
            this.finalIndexReader = finalIndexReader;
        }

        public int getFinalCacheSize()
        {
            return finalCacheSize;
        }

        public int getOldCacheSize()
        {
            return oldCacheSize;
        }

        public int getFinalDocCount()
        {
            return finalDocCount;
        }

        public IndexReader getFinalIndexReader()
        {
            return finalIndexReader;
        }

        public void addToOldCacheSize(int increment)
        {
            oldCacheSize += increment;
            numberOfOldCaches++;
        }

        public int getNumberOfOldCaches()
        {
            return numberOfOldCaches;
        }


        public void checkCachePosition(CacheUpdateTracker tracker, List<CacheEntry> indexedByDocId, OpenBitSet allLeafDocs, long[] aclIdByDocId, long[] txIdByDocId, long[] aclTxIdByDocId, SolrIndexReader reader, OwnerIdManager ownerIdManager) 
        {
            if(tracker.inNew ==0)
            {
                return;
            }
            if(tracker.inNew > indexedByDocId.size())
            {
                return;
            }
            try
            {
                if (reader.isDeleted((tracker.inNew-1)))
                {
                    boolean failed = false;
                    if(indexedByDocId.get(tracker.inNew-1) != null)
                    {
                        log.error("Entry found for deleted doc at " + (tracker.inNew-1) +" "+indexedByDocId.get(tracker.inNew-1));
                        failed = true;
                    }
                    if( aclIdByDocId[(tracker.inNew-1)] != -1)
                    {
                        log.error("Acl found for deleted doc at " + (tracker.inNew-1) +" "+ aclIdByDocId[(tracker.inNew-1)]);
                        failed = true;
                    }
                    if(txIdByDocId[(tracker.inNew-1)] != -1)
                    {
                        log.error("Tx found for deleted doc at " + (tracker.inNew-1) +" "+txIdByDocId[(tracker.inNew-1)]);
                        failed = true;
                    }
                    if(aclTxIdByDocId[(tracker.inNew-1)] != -1)
                    {
                        log.error("Acl Tx found for deleted doc at " + (tracker.inNew-1) +" "+aclTxIdByDocId[(tracker.inNew-1)]);
                        failed = true;
                    }

                    if (allLeafDocs.get((tracker.inNew-1)))
                    {
                        log.error("Leaf set for deleted doc at " + (tracker.inNew-1));
                        failed = true;
                    }
                    if(failed)
                    {
                        throw new IllegalStateException("Cache position check failed");
                    }
                }
                else
                {
                    Document document = reader.document((tracker.inNew-1));
                    Field field = document.getField("DBID");
                    if (field != null)
                    {
                        // This will skip aux data as it does not have a DBID (eg ACL docs)

                        String string = field.stringValue();
                        long dbid = Long.parseLong(string);

                        field = document.getField("ISNODE");
                        boolean isLeaf;
                        if (field == null)
                        {
                            isLeaf = false;
                        }
                        else
                        {
                            string = field.stringValue();
                            isLeaf = string.equals("T");
                        }

                        long aclId = -1;
                        field = document.getField("ACLID");
                        if (field != null)
                        {
                            string = field.stringValue();
                            aclId = Long.parseLong(string);
                        }

                        String owner = null;
                        field = document.getField("OWNER");
                        if (field != null)
                        {
                            owner = field.stringValue();
                        }

                        boolean failed = false;
                        CacheEntry entry = indexedByDocId.get(tracker.inNew-1);
                        if (entry == null)
                        {
                            log.error("Entry was incorrectly deleted at " + (tracker.inNew-1));
                            throw new IllegalStateException("Cache position check failed");
                        }

                        if(entry.getDbid() != dbid)
                        {
                            log.error("Incorrect DBID " + (tracker.inNew-1)  + " "+entry);
                            failed = true;
                        }

                        if (isLeaf)
                        {
                            if(entry.getLeaf() != (tracker.inNew-1))
                            {
                                log.error("Leaf position not set" + (tracker.inNew-1)  + " "+entry);
                                failed = true;
                            }
                            if (!allLeafDocs.get((tracker.inNew-1)))
                            {
                                log.error("Leaf not set" + (tracker.inNew-1));
                                failed = true;
                            }
                        }
                        else
                        {
                            if(entry.getPath() != (tracker.inNew-1))
                            {
                                log.error("Path position not set" + (tracker.inNew-1)  + " "+entry);
                                failed = true;
                            }
                            if(entry.getAclid() != aclId)
                            {
                                log.error("Incorrect ACL set" + (tracker.inNew-1)  + " "+entry);
                                failed = true;
                            }
                            if(entry.getOwner() != ownerIdManager.getOwnerId(owner))
                            {
                                log.error("Incorrect Owner set" + (tracker.inNew-1)  + " "+entry);
                                failed = true;
                            }
                            if (allLeafDocs.get((tracker.inNew-1)))
                            {
                                log.error("Leaf set" + (tracker.inNew-1));
                                failed = true;
                            }

                        }


                        if( aclIdByDocId[(tracker.inNew-1)] != -1)
                        {
                            log.error("Acl found for deleted doc at " + (tracker.inNew-1) +" "+ aclIdByDocId[(tracker.inNew-1)]);
                            failed = true;
                        }
                        if(txIdByDocId[(tracker.inNew-1)] != -1)
                        {
                            log.error("Tx found for deleted doc at " + (tracker.inNew-1) +" "+txIdByDocId[(tracker.inNew-1)]);
                            failed = true;
                        }
                        if(aclTxIdByDocId[(tracker.inNew-1)] != -1)
                        {
                            log.error("Acl Tx found for deleted doc at " + (tracker.inNew-1) +" "+aclTxIdByDocId[(tracker.inNew-1)]);
                            failed = true;
                        }

                        if(failed)
                        {
                            throw new IllegalStateException("Cache position check failed");
                        }
                    }
                    else
                    {
                        boolean failed = false;

                        // ACL DOC
                        field = document.getField("ACLID");
                        if (field != null)
                        {
                            String string = field.stringValue();
                            long aclId = Long.parseLong(string);

                            if(indexedByDocId.get(tracker.inNew-1) != null)
                            {
                                log.error("Entry found for ACL  " + (tracker.inNew-1) +" "+indexedByDocId.get(tracker.inNew-1));
                                failed = true;
                            }
                            if(aclIdByDocId[(tracker.inNew-1)] != aclId)
                            {
                                log.error("Incorrect acl id for ACL  " + (tracker.inNew-1) +" "+aclIdByDocId[(tracker.inNew-1)]);
                                failed = true;
                            }

                            if(txIdByDocId[(tracker.inNew-1)] != -1)
                            {
                                log.error("Tx found for ACL at " + (tracker.inNew-1) +" "+txIdByDocId[(tracker.inNew-1)]);
                                failed = true;
                            }
                            if(aclTxIdByDocId[(tracker.inNew-1)] != -1)
                            {
                                log.error("Acl Tx found for ACL at " + (tracker.inNew-1) +" "+aclTxIdByDocId[(tracker.inNew-1)]);
                                failed = true;
                            }

                            if (allLeafDocs.get((tracker.inNew-1)))
                            {
                                log.error("Leaf set for ACL at " + (tracker.inNew-1));
                                failed = true;
                            }

                        }
                        else
                        {
                            field = document.getField("TXID");
                            if (field != null)
                            {
                                String string = field.stringValue();
                                long txId = Long.parseLong(string);

                                if(indexedByDocId.get(tracker.inNew-1) != null)
                                {
                                    log.error("Entry found for TX  " + (tracker.inNew-1) +" "+indexedByDocId.get(tracker.inNew-1));
                                    failed = true;
                                }
                                if(aclIdByDocId[(tracker.inNew-1)] != -1)
                                {
                                    log.error("ACL found for TX  " + (tracker.inNew-1) +" "+aclIdByDocId[(tracker.inNew-1)]);
                                    failed = true;
                                }
                                if(txIdByDocId[(tracker.inNew-1)] != txId)
                                {
                                    log.error("Incorrect tx id for TX  " + (tracker.inNew-1) +" "+txIdByDocId[(tracker.inNew-1)]);
                                    failed = true;
                                }
                                if(aclTxIdByDocId[(tracker.inNew-1)] != -1)
                                {
                                    log.error("Acl Tx found for TX at " + (tracker.inNew-1) +" "+aclTxIdByDocId[(tracker.inNew-1)]);
                                    failed = true;
                                }

                                if (allLeafDocs.get((tracker.inNew-1)))
                                {
                                    log.error("Leaf set for TX at " + (tracker.inNew-1));
                                    failed = true;
                                }
                            }
                            else
                            {
                                field = document.getField("ACLTXID");
                                if (field != null)
                                {
                                    String string = field.stringValue();
                                    long aclTxId = Long.parseLong(string);

                                    if(indexedByDocId.get(tracker.inNew-1) != null)
                                    {
                                        log.error("Entry found for ACL TX  " + (tracker.inNew-1) +" "+indexedByDocId.get(tracker.inNew-1));
                                        failed = true;
                                    }
                                    if(aclIdByDocId[(tracker.inNew-1)] != -1)
                                    {
                                        log.error("ACL found for ACL TX  " + (tracker.inNew-1) +" "+aclIdByDocId[(tracker.inNew-1)]);
                                        failed = true;
                                    }
                                    if(txIdByDocId[(tracker.inNew-1)] != -1)
                                    {
                                        log.error("TX found for ACL TX  " + (tracker.inNew-1) +" "+txIdByDocId[(tracker.inNew-1)]);
                                        failed = true;
                                    }
                                    if(aclTxIdByDocId[(tracker.inNew-1)] != aclTxId)
                                    {
                                        log.error("Incorrect acl tx id for ACL TX at " + (tracker.inNew-1) +" "+aclTxIdByDocId[(tracker.inNew-1)]);
                                        failed = true;
                                    }

                                    if (allLeafDocs.get((tracker.inNew-1)))
                                    {
                                        log.error("Leaf set for ACL TX at " + (tracker.inNew-1));
                                        failed = true;
                                    }

                                }
                                else
                                {
                                    if(indexedByDocId.get(tracker.inNew-1) != null)
                                    {
                                        log.error("Entry found for Unkown  " + (tracker.inNew-1) +" "+indexedByDocId.get(tracker.inNew-1));
                                        failed = true;
                                    }
                                    if(aclIdByDocId[(tracker.inNew-1)] != -1)
                                    {
                                        log.error("ACL found for Unknown  " + (tracker.inNew-1) +" "+aclIdByDocId[(tracker.inNew-1)]);
                                        failed = true;
                                    }
                                    if(txIdByDocId[(tracker.inNew-1)] != -1)
                                    {
                                        log.error("TX found for Unkown " + (tracker.inNew-1) +" "+txIdByDocId[(tracker.inNew-1)]);
                                        failed = true;
                                    }
                                    if(aclTxIdByDocId[(tracker.inNew-1)] != -1)
                                    {
                                        log.error("ACL TX found for unknown " + (tracker.inNew-1) +" "+aclTxIdByDocId[(tracker.inNew-1)]);
                                        failed = true;
                                    }

                                    if (allLeafDocs.get((tracker.inNew-1)))
                                    {
                                        log.error("Leaf set for ACL TX at " + (tracker.inNew-1));
                                        failed = true;
                                    }
                                }
                            }
                        }

                        if(failed)
                        {
                            throw new IllegalStateException("Cache position check failed");
                        }
                    }
                }
            }
            catch(IOException e)
            {
                throw new IllegalStateException("Cache position check failed", e);
            }
        }
    }

    public abstract static class RemoveNullEntriesCacheMatch extends AbstractCacheMatch
    {
        /**
         * @param finalCacheSize
         * @param finalDocCount
         */
        RemoveNullEntriesCacheMatch(int finalCacheSize, int finalDocCount, IndexReader finalIndexReader)
        {
            super(finalCacheSize, finalDocCount, finalIndexReader);
            // TODO Auto-generated constructor stub
        }

        /*
         * (non-Javadoc)
         * @see
         * org.alfresco.solr.AlfrescoSolrEventListener.CacheMatch#updateCache(org.alfresco.solr.AlfrescoSolrEventListener
         * .CacheUpdateTracker, org.alfresco.solr.AlfrescoSolrEventListener.CacheEntry[],
         * org.apache.lucene.util.OpenBitSet, long[], org.alfresco.solr.AlfrescoSolrEventListener.CacheEntry[],
         * org.apache.lucene.util.OpenBitSet, long[], java.util.HashMap)
         */
        @Override
        public void updateCache(CacheUpdateTracker tracker, ResizeableArrayList<CacheEntry> oldIndexedByDocId, long[] oldAclIdByDocId, long[] oldTxByDocId, long[] oldAclTxByDocId,
                ResizeableArrayList<CacheEntry> indexedByDocId, OpenBitSet allLeafDocs, long[] aclIdByDocId, long[] txIdByDocId, long[] aclTxIdByDocId, HashMap<Long, CacheEntry> unmatchedByDBID,
                OpenBitSet deleted, SolrIndexReader reader, OwnerIdManager ownerIdManager)
        {
            checkCachePosition(tracker, indexedByDocId, allLeafDocs, aclIdByDocId, txIdByDocId, aclTxIdByDocId, reader, ownerIdManager);

            if((getNumberOfOldCaches() ==1) && (tracker.earlyDeletes > 0))
            {
                throw new IllegalStateException("Early deletes should have been cleared");
            }

            // Deletions appear as merges
            // Merges can have deletions - we have to check if a deletion still exists in the new reader
            boolean targetHasDeletions = finalIndexReader.hasDeletions();

            int firstNew = tracker.inNew;
            int lastNew = tracker.inNew + getFinalCacheSize();
            int lastOld = tracker.inOld + getOldCacheSize();


            while ((tracker.inNew < lastNew) && (tracker.inOld < lastOld))
            {
                if (deleted.get(tracker.inOld))
                {
                    if(tracker.earlyDeletes > 0)
                    {
                        tracker.earlyDeletes--;
                    }
                    else if (targetHasDeletions)
                    {
                        if(finalIndexReader.isDeleted(tracker.inNew - firstNew))
                        {
                            indexedByDocId.set(tracker.inNew, null);
                            aclIdByDocId[tracker.inNew] = -1;
                            txIdByDocId[tracker.inNew] = -1;
                            aclTxIdByDocId[tracker.inNew] = -1;
                            if (allLeafDocs.get(tracker.inNew))
                            {
                                allLeafDocs.flip(tracker.inNew);
                            }

                            tracker.inNew++;
                        }
                    }

                    // nothing to move
                    tracker.inOld++;
                }
                else
                { 
                    if(tracker.earlyDeletes > 0)
                    {
                        throw new IllegalStateException("Early deletes should have been cleared");
                    }

                    CacheEntry old = oldIndexedByDocId.get(tracker.inOld);
                    if (old != null)
                    {
                        // leaf docs
                        if (old.leaf == tracker.inOld)
                        {

                            // we have already created a new entry so we update it
                            CacheEntry newCacheEntry = unmatchedByDBID.remove(old.dbid);
                            if ((newCacheEntry != null) && (newCacheEntry.getLeaf() == 0))
                            {
                                newCacheEntry.setLeaf(tracker.inNew);
                            }
                            else
                            {
                                newCacheEntry = new CacheEntry(old.getDbid());
                                newCacheEntry.setLeaf(tracker.inNew);
                                newCacheEntry.setAclid(old.getAclid());
                                newCacheEntry.setOwner(old.getOwner());
                                newCacheEntry.setPath(0);

                                unmatchedByDBID.put(newCacheEntry.dbid, newCacheEntry);
                            }

                            indexedByDocId.set(tracker.inNew, newCacheEntry);

                            allLeafDocs.set(tracker.inNew);

                            aclIdByDocId[tracker.inNew] = -1;
                            txIdByDocId[tracker.inNew] = -1;
                            aclTxIdByDocId[tracker.inNew] = -1;

                        }
                        // aux docs
                        else
                        {

                            // we have already created a new entry so we update it
                            CacheEntry newCacheEntry = unmatchedByDBID.remove(old.dbid);
                            if ((newCacheEntry != null) && (newCacheEntry.getPath() ==0))
                            {
                                newCacheEntry.setAclid(old.getAclid());
                                newCacheEntry.setOwner(old.getOwner());
                                newCacheEntry.setPath(tracker.inNew);
                            }
                            else
                            {
                                newCacheEntry = new CacheEntry(old.getDbid());
                                newCacheEntry.setLeaf(0);
                                newCacheEntry.setAclid(old.getAclid());
                                newCacheEntry.setOwner(old.getOwner());
                                newCacheEntry.setPath(tracker.inNew);

                                unmatchedByDBID.put(newCacheEntry.dbid, newCacheEntry);
                            }

                            indexedByDocId.set(tracker.inNew, newCacheEntry);

                            if (allLeafDocs.get(tracker.inNew))
                            {
                                allLeafDocs.flip(tracker.inNew);
                            }

                            aclIdByDocId[tracker.inNew] = -1;
                            txIdByDocId[tracker.inNew] = -1;
                            aclTxIdByDocId[tracker.inNew] = -1;

                        }
                        tracker.inNew++;
                        tracker.inOld++;
                    }
                    else if (oldAclIdByDocId[tracker.inOld] >= 0)
                    {
                        indexedByDocId.set(tracker.inNew,null);
                        aclIdByDocId[tracker.inNew] = oldAclIdByDocId[tracker.inOld];
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }
                        txIdByDocId[tracker.inNew] = -1;
                        aclTxIdByDocId[tracker.inNew] = -1;
                        tracker.inNew++;
                        tracker.inOld++;

                    }
                    else if (oldTxByDocId[tracker.inOld] >= 0)
                    {
                        indexedByDocId.set(tracker.inNew, null);
                        txIdByDocId[tracker.inNew] = oldTxByDocId[tracker.inOld];
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }
                        aclIdByDocId[tracker.inNew] = -1;
                        aclTxIdByDocId[tracker.inNew] = -1;
                        tracker.inNew++;
                        tracker.inOld++;
                    }
                    else if (oldAclTxByDocId[tracker.inOld] >= 0)
                    {
                        indexedByDocId.set(tracker.inNew, null);
                        aclTxIdByDocId[tracker.inNew] = oldAclTxByDocId[tracker.inOld];
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }
                        aclIdByDocId[tracker.inNew] = -1;
                        txIdByDocId[tracker.inNew] = -1;
                        tracker.inNew++;
                        tracker.inOld++;
                    }
                    else
                    {

                        indexedByDocId.set(tracker.inNew, null);
                        aclIdByDocId[tracker.inNew] = -1;
                        txIdByDocId[tracker.inNew] = -1;
                        aclTxIdByDocId[tracker.inNew] = -1;
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }

                        if (targetHasDeletions)
                        {
                            if(finalIndexReader.isDeleted(tracker.inNew - firstNew))
                            {
                                tracker.inNew++;
                            }
                        }

                        tracker.inOld++;
                    }
                }
            }

            if (targetHasDeletions)
            {
                while (tracker.inNew < lastNew)
                {
                    if(finalIndexReader.isDeleted(tracker.inNew - firstNew))
                    {
                        indexedByDocId.set(tracker.inNew, null);
                        aclIdByDocId[tracker.inNew] = -1;
                        txIdByDocId[tracker.inNew] = -1;
                        aclTxIdByDocId[tracker.inNew] = -1;
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }

                        tracker.inNew++;
                        tracker.earlyDeletes++;
                    }
                    else
                    {
                        // We're not going to get much further in this loop!
                        break;
                    }
                }                
            }

            // skip and check remaining nulls in the source

            while(tracker.inOld < lastOld)
            {
                if (deleted.get(tracker.inOld))
                {
                    if(tracker.earlyDeletes > 0)
                    {
                        tracker.earlyDeletes--;
                    }
                    // nothing to move
                    tracker.inOld++;
                }
                else
                { 
                    if(tracker.earlyDeletes > 0)
                    {
                        throw new IllegalStateException("Early deletes should have been cleared");
                    }
                    tracker.inOld++;
                }
            }

            if((tracker.inNew != lastNew) || (tracker.inOld != lastOld))
            {
                // force rebuild as cache rebuild failed
                throw new IllegalStateException("RemoveNullEntriesCacheMatch cache update failed");
            }

            checkCachePosition(tracker, indexedByDocId, allLeafDocs, aclIdByDocId, txIdByDocId, aclTxIdByDocId, reader, ownerIdManager);

        }

    }

    public static class Delete extends RemoveNullEntriesCacheMatch
    {

        public Delete(int finalCacheSize, int finalDocCount)
        {
            this(finalCacheSize, finalDocCount, null);
        }

        public Delete(int finalCacheSize, int finalDocCount, IndexReader finalIndexReader)
        {
            super(finalCacheSize, finalDocCount, finalIndexReader);
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return "Delete";
        }

    }

    public static class Match extends AbstractCacheMatch
    {

        public Match(int finalCacheSize, int finalDocCount)
        {
            this(finalCacheSize, finalDocCount, null);
        }

        public Match(int finalCacheSize, int finalDocCount, IndexReader finalIndexReader)
        {
            super(finalCacheSize, finalDocCount, finalIndexReader);
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return "Match";
        }

        /*
         * (non-Javadoc)
         * @see
         * org.alfresco.solr.AlfrescoSolrEventListener.CacheMatch#updateCache(org.alfresco.solr.AlfrescoSolrEventListener
         * .CacheUpdateTracker, org.alfresco.solr.AlfrescoSolrEventListener.CacheEntry[],
         * org.apache.lucene.util.OpenBitSet, long[], org.alfresco.solr.AlfrescoSolrEventListener.CacheEntry[],
         * org.apache.lucene.util.OpenBitSet, long[], java.util.HashMap)
         */
        @Override
        public void updateCache(CacheUpdateTracker tracker, ResizeableArrayList<CacheEntry> oldIndexedByDocId, long[] oldAclIdByDocId, long[] oldTxByDocId, long[] oldAclTxByDocId,
                ResizeableArrayList<CacheEntry> indexedByDocId, OpenBitSet allLeafDocs, long[] aclIdByDocId, long[] txIdByDocId, long[] aclTxIdByDocId, HashMap<Long, CacheEntry> unmatchedByDBID,
                OpenBitSet deleted, SolrIndexReader reader, OwnerIdManager ownerIdManager)
        {
            checkCachePosition(tracker, indexedByDocId, allLeafDocs, aclIdByDocId, txIdByDocId, aclTxIdByDocId, reader, ownerIdManager);

            if(tracker.earlyDeletes > 0)
            {
                throw new IllegalStateException("Early deletes should have been cleared");
            }

            int lastNew = tracker.inNew + getFinalCacheSize();
            int lastOld = tracker.inOld + getOldCacheSize();

            while ((tracker.inNew < lastNew) && (tracker.inOld < lastOld))
            {
                if (deleted.get(tracker.inOld))
                {
                    indexedByDocId.set(tracker.inNew, null);
                    aclIdByDocId[tracker.inNew] = -1;
                    txIdByDocId[tracker.inNew] = -1;
                    aclTxIdByDocId[tracker.inNew] = -1;
                    if (allLeafDocs.get(tracker.inNew))
                    {
                        allLeafDocs.flip(tracker.inNew);
                    }

                    tracker.inNew++;
                    tracker.inOld++;
                }
                else
                {
                    CacheEntry old = oldIndexedByDocId.get(tracker.inOld);
                    if (old != null)
                    {
                        // leaf docs
                        if (old.leaf == tracker.inOld)
                        {

                            // we have already created a new entry so we update it
                            CacheEntry newCacheEntry = unmatchedByDBID.remove(old.dbid);
                            if ((newCacheEntry != null) && (newCacheEntry.getLeaf() == 0))
                            {
                                newCacheEntry.setLeaf(tracker.inNew);
                            }
                            else
                            {
                                newCacheEntry = new CacheEntry(old.getDbid());
                                newCacheEntry.setLeaf(tracker.inNew);
                                newCacheEntry.setAclid(old.getAclid());
                                newCacheEntry.setOwner(old.getOwner());
                                newCacheEntry.setPath(0);

                                unmatchedByDBID.put(newCacheEntry.dbid, newCacheEntry);
                            }

                            indexedByDocId.set(tracker.inNew, newCacheEntry);

                            allLeafDocs.set(tracker.inNew);

                            aclIdByDocId[tracker.inNew] = -1;
                            txIdByDocId[tracker.inNew] = -1;
                            aclTxIdByDocId[tracker.inNew] = -1;
                        }
                        // aux docs
                        else
                        {

                            // we have already created a new entry so we update it
                            CacheEntry newCacheEntry = unmatchedByDBID.remove(old.dbid);
                            if ((newCacheEntry != null) && (newCacheEntry.getPath() == 0))
                            {
                                newCacheEntry.setAclid(old.getAclid());
                                newCacheEntry.setOwner(old.getOwner());
                                newCacheEntry.setPath(tracker.inNew);
                            }
                            else
                            {
                                newCacheEntry = new CacheEntry(old.getDbid());
                                newCacheEntry.setLeaf(0);
                                newCacheEntry.setAclid(old.getAclid());
                                newCacheEntry.setOwner(old.getOwner());
                                newCacheEntry.setPath(tracker.inNew);

                                unmatchedByDBID.put(newCacheEntry.dbid, newCacheEntry);
                            }

                            indexedByDocId.set(tracker.inNew, newCacheEntry);

                            if (allLeafDocs.get(tracker.inNew))
                            {
                                allLeafDocs.flip(tracker.inNew);
                            }

                            aclIdByDocId[tracker.inNew] = -1;
                            txIdByDocId[tracker.inNew] = -1;
                            aclTxIdByDocId[tracker.inNew] = -1;

                        }
                        tracker.inNew++;
                        tracker.inOld++;
                    }
                    else if (oldAclIdByDocId[tracker.inOld] >= 0)
                    {
                        indexedByDocId.set(tracker.inNew, null);
                        aclIdByDocId[tracker.inNew] = oldAclIdByDocId[tracker.inOld];
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }
                        txIdByDocId[tracker.inNew] = -1;
                        aclTxIdByDocId[tracker.inNew] = -1;
                        tracker.inNew++;
                        tracker.inOld++;
                    }
                    else if (oldTxByDocId[tracker.inOld] >= 0)
                    {
                        indexedByDocId.set(tracker.inNew, null);
                        txIdByDocId[tracker.inNew] = oldTxByDocId[tracker.inOld];
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }
                        aclIdByDocId[tracker.inNew] = -1;
                        aclTxIdByDocId[tracker.inNew] = -1;
                        tracker.inNew++;
                        tracker.inOld++;
                    }
                    else if (oldAclTxByDocId[tracker.inOld] >= 0)
                    {
                        indexedByDocId.set(tracker.inNew, null);
                        aclTxIdByDocId[tracker.inNew] = oldAclTxByDocId[tracker.inOld];
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }
                        aclIdByDocId[tracker.inNew] = -1;
                        txIdByDocId[tracker.inNew] = -1;
                        tracker.inNew++;
                        tracker.inOld++;
                    }
                    else
                    {
                        indexedByDocId.set(tracker.inNew, null);
                        aclIdByDocId[tracker.inNew] = -1;
                        txIdByDocId[tracker.inNew] = -1;
                        aclTxIdByDocId[tracker.inNew] = -1;
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }

                        tracker.inNew++;
                        tracker.inOld++;
                    }
                }
            }

            if((tracker.inNew != lastNew) || (tracker.inOld != lastOld))
            {
                // force rebuild as cache rebuild failed
                throw new IllegalStateException("Match cache update failed");
            }

            checkCachePosition(tracker, indexedByDocId, allLeafDocs, aclIdByDocId, txIdByDocId, aclTxIdByDocId, reader, ownerIdManager);
        }

    }

    public static class Merge extends RemoveNullEntriesCacheMatch
    {
        public Merge(int finalCacheSize, int finalDocCount)
        {
            this(finalCacheSize, finalDocCount, null);
        }

        public Merge(int finalCacheSize, int finalDocCount, IndexReader finalIndexReader)
        {
            super(finalCacheSize, finalDocCount, finalIndexReader);
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return "Merge";
        }
    }

    public abstract class RebuildCacheMatch extends AbstractCacheMatch
    {
        public RebuildCacheMatch(int finalCacheSize, int finalDocCount)
        {
            this(finalCacheSize, finalDocCount, null);
        }

        RebuildCacheMatch(int finalCacheSize, int finalDocCount, IndexReader finalIndexReader)
        {
            super(finalCacheSize, finalDocCount, finalIndexReader);
            // TODO Auto-generated constructor stub
        }

        /*
         * (non-Javadoc)
         * @see
         * org.alfresco.solr.AlfrescoSolrEventListener.CacheMatch#updateCache(org.alfresco.solr.AlfrescoSolrEventListener
         * .CacheUpdateTracker, org.alfresco.solr.AlfrescoSolrEventListener.CacheEntry[],
         * org.apache.lucene.util.OpenBitSet, long[], org.alfresco.solr.AlfrescoSolrEventListener.CacheEntry[],
         * org.apache.lucene.util.OpenBitSet, long[], java.util.HashMap)
         */
        @Override
        public void updateCache(CacheUpdateTracker tracker, ResizeableArrayList<CacheEntry> oldIndexedByDocId, long[] oldAclIdByDocIdx, long[] oldTxByDocId, long[] oldAclTxByDocId,
                ResizeableArrayList<CacheEntry> indexedByDocId, OpenBitSet allLeafDocs, long[] aclIdByDocId, long[] txIdByDocId, long[] aclTxIdByDocId, HashMap<Long, CacheEntry> unmatchedByDBID,
                OpenBitSet deleted, SolrIndexReader reader, OwnerIdManager ownerIdManager)
        {
            // delete all existing
            checkCachePosition(tracker, indexedByDocId, allLeafDocs, aclIdByDocId, txIdByDocId, aclTxIdByDocId, reader, ownerIdManager);

            if(tracker.earlyDeletes > 0)
            {
                throw new IllegalStateException("Early deletes should have been cleared");
            }
            buildCacheForReader(indexedByDocId, allLeafDocs, aclIdByDocId, txIdByDocId, aclTxIdByDocId, getFinalIndexReader(), tracker.inNew, getFinalCacheSize(), unmatchedByDBID, ownerIdManager);
            tracker.inNew += getFinalCacheSize();
            tracker.inOld += getOldCacheSize();

            checkCachePosition(tracker, indexedByDocId, allLeafDocs, aclIdByDocId, txIdByDocId, aclTxIdByDocId, reader, ownerIdManager);
        }
    }

    public class MergeAndNew extends AbstractCacheMatch
    {
        public MergeAndNew(int finalCacheSize, int finalDocCount)
        {
            this(finalCacheSize, finalDocCount, null);
        }

        public MergeAndNew(int finalCacheSize, int finalDocCount, IndexReader finalIndexReader)
        {
            super(finalCacheSize, finalDocCount, finalIndexReader);
        }

        public MergeAndNew(CacheMatch cacheMatch)
        {
            super(cacheMatch.getFinalCacheSize(), cacheMatch.getFinalDocCount(), cacheMatch.getFinalIndexReader());
            oldCacheSize = cacheMatch.getOldCacheSize();
            numberOfOldCaches = cacheMatch.getNumberOfOldCaches();
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return "MergeAndNew";
        }

        /*
         * (non-Javadoc)
         * @see
         * org.alfresco.solr.AlfrescoSolrEventListener.CacheMatch#updateCache(org.alfresco.solr.AlfrescoSolrEventListener
         * .CacheUpdateTracker, org.alfresco.solr.AlfrescoSolrEventListener.CacheEntry[],
         * org.apache.lucene.util.OpenBitSet, long[], org.alfresco.solr.AlfrescoSolrEventListener.CacheEntry[],
         * org.apache.lucene.util.OpenBitSet, long[], java.util.HashMap)
         */
        @Override
        public void updateCache(CacheUpdateTracker tracker, ResizeableArrayList<CacheEntry> oldIndexedByDocId, long[] oldAclIdByDocId, long[] oldTxByDocId, long[] oldAclTxByDocId,
                ResizeableArrayList<CacheEntry> indexedByDocId, OpenBitSet allLeafDocs, long[] aclIdByDocId, long[] txIdByDocId, long[] aclTxIdByDocId, HashMap<Long, CacheEntry> unmatchedByDBID,
                OpenBitSet deleted, SolrIndexReader reader, OwnerIdManager ownerIdManager)
        {

            checkCachePosition(tracker, indexedByDocId, allLeafDocs, aclIdByDocId, txIdByDocId, aclTxIdByDocId, reader, ownerIdManager);

            boolean targetHasDeletions = finalIndexReader.hasDeletions();
            int startNew = tracker.inNew;
            int startOld = tracker.inOld;
            int lastNew = tracker.inNew + getFinalCacheSize();
            int lastOld = tracker.inOld + getOldCacheSize();

            while ((tracker.inNew < lastNew) && (tracker.inOld < lastOld))
            {
                if (deleted.get(tracker.inOld))
                {
                    if(tracker.earlyDeletes > 0)
                    {
                        tracker.earlyDeletes--;
                    }
                    else if (targetHasDeletions)
                    {
                        if(finalIndexReader.isDeleted(tracker.inNew - startNew))
                        {
                            indexedByDocId.set(tracker.inNew, null);
                            aclIdByDocId[tracker.inNew] = -1;
                            txIdByDocId[tracker.inNew] = -1;
                            aclTxIdByDocId[tracker.inNew] = -1;
                            if (allLeafDocs.get(tracker.inNew))
                            {
                                allLeafDocs.flip(tracker.inNew);
                            }

                            tracker.inNew++;
                        }
                    }
                    // nothing to move
                    tracker.inOld++;
                }
                else
                {
                    if(tracker.earlyDeletes > 0)
                    {
                        throw new IllegalStateException("Early deletes should have been cleared");
                    }
                    CacheEntry old = oldIndexedByDocId.get(tracker.inOld);
                    if (old != null)
                    {
                        // leaf docs
                        if (old.leaf == tracker.inOld)
                        {

                            // we have already created a new entry so we update it
                            CacheEntry newCacheEntry = unmatchedByDBID.remove(old.dbid);
                            if ((newCacheEntry != null) && (newCacheEntry.getLeaf() == 0))
                            {
                                newCacheEntry.setLeaf(tracker.inNew);
                            }
                            else
                            {
                                newCacheEntry = new CacheEntry(old.getDbid());
                                newCacheEntry.setLeaf(tracker.inNew);
                                newCacheEntry.setAclid(old.getAclid());
                                newCacheEntry.setOwner(old.getOwner());
                                newCacheEntry.setPath(0);

                                unmatchedByDBID.put(newCacheEntry.dbid, newCacheEntry);
                            }

                            indexedByDocId.set(tracker.inNew, newCacheEntry);

                            allLeafDocs.set(tracker.inNew);

                            aclIdByDocId[tracker.inNew] = -1;
                            txIdByDocId[tracker.inNew] = -1;
                            aclTxIdByDocId[tracker.inNew] = -1;

                        }
                        // aux docs
                        else
                        {

                            // we have already created a new entry so we update it
                            CacheEntry newCacheEntry = unmatchedByDBID.remove(old.dbid);
                            if ((newCacheEntry != null) && (newCacheEntry.getPath() == 0))
                            {
                                newCacheEntry.setAclid(old.getAclid());
                                newCacheEntry.setOwner(old.getOwner());
                                newCacheEntry.setPath(tracker.inNew);
                            }
                            else
                            {
                                newCacheEntry = new CacheEntry(old.getDbid());
                                newCacheEntry.setLeaf(0);
                                newCacheEntry.setAclid(old.getAclid());
                                newCacheEntry.setOwner(old.getOwner());
                                newCacheEntry.setPath(tracker.inNew);

                                unmatchedByDBID.put(newCacheEntry.dbid, newCacheEntry);
                            }

                            indexedByDocId.set(tracker.inNew, newCacheEntry);

                            if (allLeafDocs.get(tracker.inNew))
                            {
                                allLeafDocs.flip(tracker.inNew);
                            }

                            aclIdByDocId[tracker.inNew] = -1;
                            txIdByDocId[tracker.inNew] = -1;
                            aclTxIdByDocId[tracker.inNew] = -1;

                        }
                        tracker.inNew++;
                        tracker.inOld++;
                    }
                    else if (oldAclIdByDocId[tracker.inOld] >= 0)
                    {
                        indexedByDocId.set(tracker.inNew,  null);
                        aclIdByDocId[tracker.inNew] = oldAclIdByDocId[tracker.inOld];
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }
                        txIdByDocId[tracker.inNew] = -1;
                        aclTxIdByDocId[tracker.inNew] = -1;
                        tracker.inNew++;
                        tracker.inOld++;
                    }
                    else if (oldTxByDocId[tracker.inOld] >= 0)
                    {
                        indexedByDocId.set(tracker.inNew, null);
                        txIdByDocId[tracker.inNew] = oldTxByDocId[tracker.inOld];
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }
                        aclIdByDocId[tracker.inNew] = -1;
                        aclTxIdByDocId[tracker.inNew] = -1;
                        tracker.inNew++;
                        tracker.inOld++;
                    }
                    else if (oldAclTxByDocId[tracker.inOld] >= 0)
                    {
                        indexedByDocId.set(tracker.inNew, null);
                        aclTxIdByDocId[tracker.inNew] = oldAclTxByDocId[tracker.inOld];
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }
                        aclIdByDocId[tracker.inNew] = -1;
                        txIdByDocId[tracker.inNew] = -1;
                        tracker.inNew++;
                        tracker.inOld++;
                    }
                    else
                    {

                        indexedByDocId.set(tracker.inNew, null);
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }

                        aclIdByDocId[tracker.inNew] = -1;
                        txIdByDocId[tracker.inNew] = -1;
                        aclTxIdByDocId[tracker.inNew] = -1;

                        if (targetHasDeletions)
                        {
                            if(finalIndexReader.isDeleted(tracker.inNew - startNew))
                            {
                                tracker.inNew++;
                            }
                        }

                        tracker.inOld++;
                    }
                }
            }

            // at point to rebuild
            // assume it is fast to here

            if ((((lastNew - tracker.inNew) * 100) / finalCacheSize) > 50)
            {
                buildCacheForReader(indexedByDocId, allLeafDocs, aclIdByDocId, txIdByDocId, aclTxIdByDocId, getFinalIndexReader(), startNew, getFinalCacheSize(), unmatchedByDBID, ownerIdManager);
                tracker.inNew = lastNew;
                tracker.inOld = lastOld;
            }
            else
            {
                while ((tracker.inNew < lastNew))
                {
                    updateCacheByDocId(tracker, indexedByDocId, allLeafDocs, aclIdByDocId, txIdByDocId, aclTxIdByDocId, unmatchedByDBID, deleted, reader, ownerIdManager);
                }
            }

            if((tracker.inNew != lastNew) || (tracker.inOld != lastOld))
            {
                // force rebuild as cache rebuild failed
                throw new IllegalStateException("MergeAndNew cache update failed");
            }

            checkCachePosition(tracker, indexedByDocId, allLeafDocs, aclIdByDocId, txIdByDocId, aclTxIdByDocId, reader, ownerIdManager);
        }

    }

    public class New extends RebuildCacheMatch
    {

        public New(int finalCacheSize, int finalDocCount)
        {
            this(finalCacheSize, finalDocCount, null);
        }

        public New(int finalCacheSize, int finalDocCount, IndexReader finalIndexReader)
        {
            super(finalCacheSize, finalDocCount, finalIndexReader);
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return "New";
        }
    }

    public interface CacheSection
    {
        public int getStart();

        public int getLength();

        public int getDocCount();

        public int getDeletionsCount();

        public int getNewDeletionsCount();

        public void addDeletion(int doc);
    }

    public static class SolrIndexReaderCacheSection implements CacheSection
    {
        SolrIndexReader solrIndexReader;

        int newDeletions = 0;

        SolrIndexReaderCacheSection(SolrIndexReader solrIndexReader)
        {
            this.solrIndexReader = solrIndexReader;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((solrIndexReader == null) ? 0 : solrIndexReader.hashCode());
            return result;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            SolrIndexReaderCacheSection other = (SolrIndexReaderCacheSection) obj;
            if (solrIndexReader == null)
            {
                if (other.solrIndexReader != null)
                    return false;
            }
            else if (!solrIndexReader.equals(other.solrIndexReader))
                return false;
            return true;
        }

        public static CacheSection[] getCacheSections(SolrIndexReader[] readers)
        {
            SolrIndexReaderCacheSection[] sections = new SolrIndexReaderCacheSection[readers.length];
            for (int i = 0; i < readers.length; i++)
            {
                sections[i] = new SolrIndexReaderCacheSection(readers[i]);
            }
            return sections;
        }

        /*
         * (non-Javadoc)
         * @see org.alfresco.solr.AlfrescoSolrEventListener.CacheSection#getDocCount()
         */
        @Override
        public int getDocCount()
        {
            return solrIndexReader.numDocs();
        }

        /*
         * (non-Javadoc)
         * @see org.alfresco.solr.AlfrescoSolrEventListener.CacheSection#getDeletionsCount()
         */
        @Override
        public int getDeletionsCount()
        {
            return solrIndexReader.maxDoc() - solrIndexReader.numDocs();
        }

        /*
         * (non-Javadoc)
         * @see org.alfresco.solr.AlfrescoSolrEventListener.CacheSection#getLength()
         */
        @Override
        public int getLength()
        {
            return solrIndexReader.maxDoc();
        }

        /*
         * (non-Javadoc)
         * @see org.alfresco.solr.AlfrescoSolrEventListener.CacheSection#getNewDeletionsCount()
         */
        @Override
        public int getNewDeletionsCount()
        {
            return newDeletions;
        }

        /*
         * (non-Javadoc)
         * @see org.alfresco.solr.AlfrescoSolrEventListener.CacheSection#getStart()
         */
        @Override
        public int getStart()
        {
            return solrIndexReader.getBase();
        }

        public void addDeletion(int doc)
        {
            newDeletions++;
        }
    }

    private static class CacheUpdateTracker
    {
        int earlyDeletes;

        int inOld;

        int inNew;

        CacheUpdateTracker(int inOld, int inNew, int earlyDeletes)
        {
            this.inOld = inOld;
            this.inNew = inNew;
            this.earlyDeletes = earlyDeletes;
        }
    }

    /**
     * Keep for incremental build
     */

    private void updateCacheByDocId(CacheUpdateTracker tracker, List<CacheEntry> indexedByDocId, OpenBitSet allLeafDocs, long[] aclIdByDocId, long[] txIdByDocId,
            long[] aclTxIdByDocId, HashMap<Long, CacheEntry> unmatchedByDBID, OpenBitSet deleted, SolrIndexReader reader, OwnerIdManager osnerIdManager)
    {
        try
        {

            if (reader.isDeleted(tracker.inNew))
            {
                indexedByDocId.set(tracker.inNew, null);
                aclIdByDocId[tracker.inNew] = -1;
                txIdByDocId[tracker.inNew] = -1;
                aclTxIdByDocId[tracker.inNew] = -1;
                if (allLeafDocs.get(tracker.inNew))
                {
                    allLeafDocs.flip(tracker.inNew);
                }
            }
            else
            {
                Document document = reader.document(tracker.inNew);
                Field field = document.getField("DBID");
                if (field != null)
                {
                    // This will skip aux data as it does not have a DBID (eg ACL docs)

                    String string = field.stringValue();
                    long dbid = Long.parseLong(string);

                    field = document.getField("ISNODE");
                    boolean isLeaf;
                    if (field == null)
                    {
                        isLeaf = false;
                    }
                    else
                    {
                        string = field.stringValue();
                        isLeaf = string.equals("T");
                    }

                    long aclId = -1;
                    field = document.getField("ACLID");
                    if (field != null)
                    {
                        string = field.stringValue();
                        aclId = Long.parseLong(string);
                    }

                    String owner = null;
                    field = document.getField("OWNER");
                    if (field != null)
                    {
                        owner = field.stringValue();
                    }

                    CacheEntry entry;

                    if (isLeaf)
                    {
                        entry = unmatchedByDBID.remove(dbid);
                        if ((entry == null) || (entry.getLeaf() > 0))
                        {
                            entry = new CacheEntry(dbid);
                            unmatchedByDBID.put(dbid, entry);
                        }

                        entry.setLeaf(tracker.inNew);
                        allLeafDocs.set(tracker.inNew);
                    }
                    else
                    {
                        entry = unmatchedByDBID.remove(dbid);
                        if ((entry == null) || (entry.getPath() > 0))
                        {
                            entry = new CacheEntry(dbid);
                            unmatchedByDBID.put(dbid, entry);
                        }

                        entry.setPath(tracker.inNew);
                        entry.setAclid(aclId);
                        entry.setOwner(osnerIdManager.getOwnerId(owner));
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }
                    }

                    indexedByDocId.set(tracker.inNew, entry);
                    aclIdByDocId[tracker.inNew] = -1;
                    txIdByDocId[tracker.inNew] = -1;
                    aclTxIdByDocId[tracker.inNew] = -1;

                }
                else
                {
                    // ACL DOC
                    field = document.getField("ACLID");
                    if (field != null)
                    {
                        String string = field.stringValue();
                        long aclId = Long.parseLong(string);

                        indexedByDocId.set(tracker.inNew, null);
                        aclIdByDocId[tracker.inNew] = aclId;
                        txIdByDocId[tracker.inNew] = -1;
                        aclTxIdByDocId[tracker.inNew] = -1;
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }
                    }
                    else
                    {
                        field = document.getField("TXID");
                        if (field != null)
                        {
                            String string = field.stringValue();
                            long txId = Long.parseLong(string);

                            indexedByDocId.set(tracker.inNew, null);
                            aclIdByDocId[tracker.inNew] = -1;
                            txIdByDocId[tracker.inNew] = txId;
                            aclTxIdByDocId[tracker.inNew] = -1;
                            if (allLeafDocs.get(tracker.inNew))
                            {
                                allLeafDocs.flip(tracker.inNew);
                            }
                        }
                        else
                        {
                            field = document.getField("ACLTXID");
                            if (field != null)
                            {
                                String string = field.stringValue();
                                long aclTxId = Long.parseLong(string);

                                indexedByDocId.set(tracker.inNew, null);
                                aclIdByDocId[tracker.inNew] = -1;
                                txIdByDocId[tracker.inNew] = -1;
                                aclTxIdByDocId[tracker.inNew] = aclTxId;
                                if (allLeafDocs.get(tracker.inNew))
                                {
                                    allLeafDocs.flip(tracker.inNew);
                                }
                            }
                            else
                            {
                                indexedByDocId.set(tracker.inNew, null);
                                aclIdByDocId[tracker.inNew] = -1;
                                txIdByDocId[tracker.inNew] = -1;
                                aclTxIdByDocId[tracker.inNew] = -1;
                                if (allLeafDocs.get(tracker.inNew))
                                {
                                    allLeafDocs.flip(tracker.inNew);
                                }
                            }
                        }
                    }
                }
            }
            tracker.inNew++;
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Update cache by doc id failed", e);
        }
    }

    public static class OwnerIdManager
    {
        ArrayList<String> idToOwner = new ArrayList<String>();
        HashMap<String, Integer> ownerIds = new HashMap<String, Integer>();

        OwnerIdManager()
        {
            idToOwner.add(null);
        }

        /**
         * @param owner
         * @return
         */
        public String get(int owner)
        {
            if(owner < idToOwner.size())
            {
                return idToOwner.get(owner);
            }
            else
            {
                return null;
            }
        }

        /**
         * @param oldOwnerIdManager
         */
        public void addAll(OwnerIdManager oldOwnerIdManager)
        {
            for(int i = 1; i < oldOwnerIdManager.idToOwner.size(); i++)
            {
                Integer ownerId = Integer.valueOf(i); // 0 => no owner so start at 1
                String owner = oldOwnerIdManager.idToOwner.get(i);
                ownerIds.put(owner, ownerId);
                idToOwner.add(owner);
            }

        }

        private Integer getOwnerId(String owner)
        {
            // owner id = 0 => no owner
            if (owner == null)
            {
                return 0;
            }

            Integer ownerId = ownerIds.get(owner);
            if (ownerId == null)
            {
                ownerId = Integer.valueOf(ownerIds.size() + 1); // 0 => no owner so start at 1
                ownerIds.put(owner, ownerId);
                idToOwner.add(owner);
            }
            return ownerId;
        }

        public String toString()
        {
            return idToOwner.toString() + "\n" + ownerIds;
        }
    }
}

