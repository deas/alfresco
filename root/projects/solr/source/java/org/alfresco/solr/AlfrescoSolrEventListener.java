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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.alfresco.repo.search.impl.lucene.analysis.NumericEncoder;
import org.alfresco.util.EqualsHelper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.util.OpenBitSet;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrEventListener;
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

    public static final String KEY_DELETED_LEAVES = "KEY_DELETED_LEAVES";

    public static final String KEY_DELETED_ACL = "KEY_DELETED_ACL";

    public static final String KEY_DELETED_AUX = "KEY_DELETED_AUX";

    public static final String KEY_UPDATED_LEAVES = "KEY_UPDATED_LEAVES";

    public static final String KEY_UPDATED_ACL = "KEY_UPDATED_ALC";

    public static final String KEY_UPDATED_AUX = "KEY_UPDATED_AUX";

    public static final String KEY_DELETE_ALL = "KEY_DELETE_ALL";

    public static String ALFRESCO_CACHE = "alfrescoCache";

    // Full cache of doc position to DBID, and leaf and path oposition
    public static String KEY_DBID_LEAF_PATH_BY_DOC_ID = "KEY_DBID_LEAF_PATH_BY_DOC_ID";

    // Cache of ACL doc to act id
    public static String KEY_ACL_ID_BY_DOC_ID = "KEY_ACL_ID_BY_DOC_ID";

    public static String KEY_GLOBAL_READERS = "KEY_GLOBAL_READERS";

    public static String KEY_ALL_LEAF_DOCS = "KEY_ALL_LEAF_DOCS";

    public static String KEY_ACL_LOOKUP = "KEY_ACL_LOOKUP";

    public static String KEY_DBID_LEAF_PATH_BY_ACL_ID_THEN_LEAF = "KEY_DBID_LEAF_PATH_BY_ACL_ID_THEN_LEAF";

    public static String KEY_DBID_LEAF_PATH_BY_OWNER_ID_THEN_LEAF = "KEY_DBID_LEAF_PATH_BY_OWNER_ID_THEN_LEAF";

    public static String KEY_OWNER_LOOKUP = "KEY_OWNER_LOOKUP";
    
    public static String KEY_CHECK_CACHE = "KEY_CHECK_CACHE";

    private NamedList args;

    HashMap<String, Integer> ownerIds = new HashMap<String, Integer>();

    ArrayList<String> idToOwner = new ArrayList<String>();

    private boolean forceCheckCache = false;

    /*
     * (non-Javadoc)
     * @see org.apache.solr.core.SolrEventListener#init(org.apache.solr.common.util.NamedList)
     */
    @Override
    public void init(NamedList args)
    {
        this.args = args;
        idToOwner.add(null); // 0 => no owner
    }

    /*
     * (non-Javadoc)
     * @see org.apache.solr.core.SolrEventListener#newSearcher(org.apache.solr.search.SolrIndexSearcher,
     * org.apache.solr.search.SolrIndexSearcher)
     */
    @Override
    public void newSearcher(SolrIndexSearcher newSearcher, SolrIndexSearcher currentSearcher)
    {
        SolrIndexReader newReader = newSearcher.getReader();
        log.info("Max " + newReader.maxDoc());
        log.info("Docs " + newReader.numDocs());
        log.info("Deleted " + newReader.numDeletedDocs());
        
        long startTime = System.nanoTime();

        CacheEntry[] indexedByDocId = new CacheEntry[newReader.maxDoc()];
        HashSet<String> globalReaders = new HashSet<String>();
        OpenBitSet allLeafDocs = new OpenBitSet();
        long[] aclIdByDocId = new long[newReader.maxDoc()];
        for (int i = 0; i < aclIdByDocId.length; i++)
        {
            aclIdByDocId[i] = -1;
        }

        OpenBitSet deleted = new OpenBitSet();

        HashMap<Long, CacheEntry> unmatchedByDBID = new HashMap<Long, CacheEntry>();

        if (currentSearcher != null)
        {
            CacheEntry[] oldIndexedByDocId = (CacheEntry[]) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_DBID_LEAF_PATH_BY_DOC_ID);
            long[] oldAclIdByDocId = (long[]) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_ACL_ID_BY_DOC_ID);
            OpenBitSet oldAllLeafDocs = (OpenBitSet) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_ALL_LEAF_DOCS);

            ConcurrentHashMap<Long, Long> addedLeaves = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_ADDED_LEAVES);
            ConcurrentHashMap<Long, Long> addedAux = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_ADDED_AUX);
            ConcurrentHashMap<Long, Long> addedAcl = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_ADDED_ACL);
            ConcurrentHashMap<Long, Long> updatedLeaves = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_UPDATED_LEAVES);
            ConcurrentHashMap<Long, Long> updatedAux = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_UPDATED_AUX);
            ConcurrentHashMap<Long, Long> updatedAcl = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_UPDATED_ACL);
            ConcurrentHashMap<Long, Long> deletedLeaves = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_DELETED_LEAVES);
            ConcurrentHashMap<Long, Long> deletedAux = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_DELETED_AUX);
            ConcurrentHashMap<Long, Long> deletedAcl = (ConcurrentHashMap<Long, Long>) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_DELETED_ACL);
            AtomicBoolean deleteAll = (AtomicBoolean) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_DELETE_ALL);
            AtomicBoolean checkCache = (AtomicBoolean) currentSearcher.cacheLookup(ALFRESCO_CACHE, KEY_CHECK_CACHE);
            if(checkCache == null)
            {
                checkCache = new AtomicBoolean(false);
            }

            boolean hasNew = (addedLeaves.size() + addedAux.size() + addedAcl.size() + updatedLeaves.size() + updatedAux.size() + updatedAcl.size()) > 0;

            if (newReader.maxDoc() == 0)
            {
                // nothing to do
            }
            else if ((oldIndexedByDocId == null) || (oldAclIdByDocId == null) || (oldAllLeafDocs == null))
            {
                log.warn("Recover from missing cache");
                buildCacheForReader(indexedByDocId, allLeafDocs, aclIdByDocId, newReader, 0, newReader.maxDoc(), unmatchedByDBID);

            }
            else if (deleteAll.get())
            {
                buildCacheForReader(indexedByDocId, allLeafDocs, aclIdByDocId, newReader, 0, newReader.maxDoc(), unmatchedByDBID);
            }
            else
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

                    CacheEntry entry = oldIndexedByDocId[i];
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
                        if ((updatedAcl.get(oldAclIdByDocId[i]) == null) && (deletedAcl.get(oldAclIdByDocId[i]) == null))
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
                LinkedList<CacheMatch> operations = buildCacheUpdateOperations(hasNew, cacheSectionsBefore, cacheSectionsAfter, after);

                log.info("Cache operatoins ...");
                for (CacheMatch match : operations)
                {
                    log.info(match.toString());
                }

                CacheUpdateTracker tracker = new CacheUpdateTracker(0, 0);

                for (CacheMatch match : operations)
                {
                    match.updateCache(tracker, oldIndexedByDocId, oldAclIdByDocId, indexedByDocId, allLeafDocs, aclIdByDocId, unmatchedByDBID, deleted, newReader);
                }

                if (forceCheckCache || checkCache.get())
                {
                    CacheEntry[] checkIndexedByDocId = new CacheEntry[newReader.maxDoc()];
                    OpenBitSet checkAllLeafDocs = new OpenBitSet();
                    long[] checkAclIdByDocId = new long[newReader.maxDoc()];

                    buildCacheForReader(checkIndexedByDocId, checkAllLeafDocs, checkAclIdByDocId, newReader, 0, newReader.maxDoc(), unmatchedByDBID);

                    boolean ok = true;

                    // if(ok)
                    {
                        for (int i = 0; i < checkIndexedByDocId.length; i++)
                        {
                            if (!EqualsHelper.nullSafeEquals(checkIndexedByDocId[i], indexedByDocId[i]))
                            {
                                log.warn("Invalid indexedByDocId at " + i);
                                log.warn(".. found     .. " + indexedByDocId[i]);
                                log.warn(".. expected  .. " + checkIndexedByDocId[i]);
                                ok = false;
                            }
                        }
                    }
                    // if(ok)
                    {
                        if (!checkAllLeafDocs.equals(allLeafDocs))
                        {
                            log.warn("Invalid AllLeafDocs cache");
                            ok = false;
                        }
                    }
                    // if(ok)
                    {
                        for (int i = 0; i < checkAclIdByDocId.length; i++)
                        {
                            if (checkAclIdByDocId[i] != aclIdByDocId[i])
                            {
                                log.warn("Invalid AclIdByDocId cache at " + i);
                                log.warn(".. found    .. " + aclIdByDocId[i]);
                                log.warn(".. expected .. " + checkAclIdByDocId[i]);

                                if (ok)
                                {
                                    try
                                    {
                                        log.warn(".. expected .. " + newSearcher.doc(i));
                                    }
                                    catch (IOException e)
                                    {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                                ok = false;
                            }
                        }
                    }

                    if (!ok)
                    {
                        indexedByDocId = checkIndexedByDocId;
                        allLeafDocs = checkAllLeafDocs;
                        aclIdByDocId = checkAclIdByDocId;

                        log.warn("... Using recomputed cache");
                    }
                    else
                    {
                        log.info("... cache OK");
                    }

                }
            }

        }
        else
        {
            buildCacheForReader(indexedByDocId, allLeafDocs, aclIdByDocId, newReader, 0, newReader.maxDoc(), unmatchedByDBID);
        }
        
        long endTime = System.nanoTime();
        log.info("Core cache rebuilt in "+((endTime-startTime)/(1.0e9)));
        startTime =  System.nanoTime();
        
        CacheEntry[] indexedOderedByAclIdThenDoc = new CacheEntry[(int) allLeafDocs.cardinality()];
        CacheEntry[] indexedOderedByOwnerIdThenDoc = new CacheEntry[(int) allLeafDocs.cardinality()];

        int doc = -1;
        int pos = 0;
        while ((doc = allLeafDocs.nextSetBit(doc + 1)) != -1)
        {
            CacheEntry entry = indexedByDocId[doc];
            indexedOderedByAclIdThenDoc[pos] = entry;
            indexedOderedByOwnerIdThenDoc[pos] = entry;
            pos++;
        }

        Arrays.sort(indexedOderedByAclIdThenDoc, new Comparator<CacheEntry>()
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

        HashMap<Long, AclLookUp> alcLookUp = new HashMap<Long, AclLookUp>();

        AclLookUp currentAclLookUp = null;
        for (int i = 0; i < indexedOderedByAclIdThenDoc.length; i++)
        {
            CacheEntry entry = indexedOderedByAclIdThenDoc[i];
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
                        alcLookUp.put(Long.valueOf(currentAclLookUp.aclid), currentAclLookUp);
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
                    alcLookUp.put(Long.valueOf(currentAclLookUp.aclid), currentAclLookUp);
                }
                break;
            }
        }
        if (currentAclLookUp != null)
        {
            currentAclLookUp.setEnd(indexedOderedByAclIdThenDoc.length);
            alcLookUp.put(Long.valueOf(currentAclLookUp.aclid), currentAclLookUp);
        }

        Arrays.sort(indexedOderedByOwnerIdThenDoc, new Comparator<CacheEntry>()
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
        for (int i = 0; i < indexedOderedByOwnerIdThenDoc.length; i++)
        {
            CacheEntry entry = indexedOderedByOwnerIdThenDoc[i];
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
                        ownerLookUp.put(idToOwner.get(currentOwnerLookUp.owner), currentOwnerLookUp);
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
                    ownerLookUp.put(idToOwner.get(currentOwnerLookUp.owner), currentOwnerLookUp);
                }
                break;
            }
        }
        if (currentOwnerLookUp != null)
        {
            currentOwnerLookUp.setEnd(indexedOderedByOwnerIdThenDoc.length);
            ownerLookUp.put(idToOwner.get(currentOwnerLookUp.owner), currentOwnerLookUp);
        }

        endTime = System.nanoTime();
        log.info("Derived caches rebuilt in "+((endTime-startTime)/(1.0e9)));
        
        newSearcher.cacheInsert(ALFRESCO_CACHE, KEY_DBID_LEAF_PATH_BY_DOC_ID, indexedByDocId);
        newSearcher.cacheInsert(ALFRESCO_CACHE, KEY_ACL_ID_BY_DOC_ID, aclIdByDocId);

        globalReaders.add("ROLE_OWNER");
        globalReaders.add("ROLE_ADMIN");
        newSearcher.cacheInsert(ALFRESCO_CACHE, KEY_GLOBAL_READERS, globalReaders);

        newSearcher.cacheInsert(ALFRESCO_CACHE, KEY_ALL_LEAF_DOCS, allLeafDocs);

        newSearcher.cacheInsert(ALFRESCO_CACHE, KEY_ACL_LOOKUP, alcLookUp);
        newSearcher.cacheInsert(ALFRESCO_CACHE, KEY_DBID_LEAF_PATH_BY_ACL_ID_THEN_LEAF, indexedOderedByAclIdThenDoc);

        newSearcher.cacheInsert(ALFRESCO_CACHE, KEY_OWNER_LOOKUP, ownerLookUp);
        newSearcher.cacheInsert(ALFRESCO_CACHE, KEY_DBID_LEAF_PATH_BY_OWNER_ID_THEN_LEAF, indexedOderedByOwnerIdThenDoc);

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
                            if (thisCount > 0)
                            {
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
                    }
                    else
                    {
                        // illegal state
                        // New reader and no new data ??
                        throw new IllegalStateException("New sub reader but no new docs ??");
                    }

                    iAfter++;
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

    void buildCacheForReader(CacheEntry[] cache, OpenBitSet allLeafDocs, long[] aclIdByDocId, IndexReader reader, int start, int length, HashMap<Long, CacheEntry> unmatchedByDBID)
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
            cache[i] = null;
            aclIdByDocId[i] = -1;
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
                    CacheEntry entry = unmatchedByDBID.remove(dbid);
                    if (entry == null)
                    {
                        entry = new CacheEntry(dbid);
                        unmatchedByDBID.put(dbid, entry);
                    }
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
                        if (allLeafDocs.get(doc))
                        {
                            entry.setLeaf(doc);
                        }
                        else
                        {
                            entry.setPath(doc);
                        }
                        cache[doc] = entry;

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
                    Long aclid = Long.valueOf(term.text());
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
                        CacheEntry entry = cache[doc];
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
                    Integer ownerId = getOwnerId(owner);
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
                        CacheEntry entry = cache[doc];
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
        public void updateCache(CacheUpdateTracker tracker, CacheEntry[] oldIndexedByDocId, long[] oldAclIdByDocId, CacheEntry[] indexedByDocId, OpenBitSet allLeafDocs,
                long[] aclIdByDocId, HashMap<Long, CacheEntry> unmatchedByDBID, OpenBitSet deleted, SolrIndexReader reader);

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
        public void updateCache(CacheUpdateTracker tracker, CacheEntry[] oldIndexedByDocId, long[] oldAclIdByDocId, CacheEntry[] indexedByDocId, OpenBitSet allLeafDocs,
                long[] aclIdByDocId, HashMap<Long, CacheEntry> unmatchedByDBID, OpenBitSet deleted, SolrIndexReader reader)
        {

            // Deletions appear as merges
            boolean deleteOnlyMerge = (numberOfOldCaches == 1) && (oldCacheSize == finalCacheSize) && (finalIndexReader.hasDeletions());

            int lastNew = tracker.inNew + getFinalCacheSize();
            int lastOld = tracker.inOld + getOldCacheSize();

            while ((tracker.inNew < lastNew) && (tracker.inOld < lastOld))
            {
                if (deleted.get(tracker.inOld))
                {
                    if (deleteOnlyMerge)
                    {
                        indexedByDocId[tracker.inNew] = null;
                        aclIdByDocId[tracker.inNew] = -1;
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }

                        tracker.inNew++;
                    }

                    // nothing to move
                    tracker.inOld++;
                }
                else
                {
                    CacheEntry old = oldIndexedByDocId[tracker.inOld];
                    if (old != null)
                    {
                        // leaf docs
                        if (old.leaf == tracker.inOld)
                        {

                            // we have already created a new entry so we update it
                            CacheEntry newCacheEntry = unmatchedByDBID.remove(old.dbid);
                            if (newCacheEntry != null)
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

                            indexedByDocId[tracker.inNew] = newCacheEntry;

                            allLeafDocs.set(tracker.inNew);

                            aclIdByDocId[tracker.inNew] = -1;

                        }
                        // aux docs
                        else
                        {

                            // we have already created a new entry so we update it
                            CacheEntry newCacheEntry = unmatchedByDBID.remove(old.dbid);
                            if (newCacheEntry != null)
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

                            indexedByDocId[tracker.inNew] = newCacheEntry;

                            if (allLeafDocs.get(tracker.inNew))
                            {
                                allLeafDocs.flip(tracker.inNew);
                            }

                            aclIdByDocId[tracker.inNew] = -1;

                        }
                        tracker.inNew++;
                        tracker.inOld++;
                    }
                    else if (oldAclIdByDocId[tracker.inOld] >= 0)
                    {
                        indexedByDocId[tracker.inNew] = null;
                        aclIdByDocId[tracker.inNew] = oldAclIdByDocId[tracker.inOld];
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }
                        tracker.inNew++;
                        tracker.inOld++;
                    }
                    else
                    {

                        indexedByDocId[tracker.inNew] = null;
                        aclIdByDocId[tracker.inNew] = -1;
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }

                        if (deleteOnlyMerge)
                        {
                            tracker.inNew++;
                        }

                        tracker.inOld++;
                    }
                }
            }
            tracker.inNew = lastNew;
            tracker.inOld = lastOld;

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
        public void updateCache(CacheUpdateTracker tracker, CacheEntry[] oldIndexedByDocId, long[] oldAclIdByDocId, CacheEntry[] indexedByDocId, OpenBitSet allLeafDocs,
                long[] aclIdByDocId, HashMap<Long, CacheEntry> unmatchedByDBID, OpenBitSet deleted, SolrIndexReader reader)
        {
            int lastNew = tracker.inNew + getFinalCacheSize();
            int lastOld = tracker.inOld + getOldCacheSize();

            while ((tracker.inNew < lastNew) && (tracker.inOld < lastOld))
            {
                if (deleted.get(tracker.inOld))
                {
                    indexedByDocId[tracker.inNew] = null;
                    aclIdByDocId[tracker.inNew] = -1;
                    if (allLeafDocs.get(tracker.inNew))
                    {
                        allLeafDocs.flip(tracker.inNew);
                    }

                    tracker.inNew++;
                    tracker.inOld++;
                }
                else
                {
                    CacheEntry old = oldIndexedByDocId[tracker.inOld];
                    if (old != null)
                    {
                        // leaf docs
                        if (old.leaf == tracker.inOld)
                        {

                            // we have already created a new entry so we update it
                            CacheEntry newCacheEntry = unmatchedByDBID.remove(old.dbid);
                            if (newCacheEntry != null)
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

                            indexedByDocId[tracker.inNew] = newCacheEntry;

                            allLeafDocs.set(tracker.inNew);

                            aclIdByDocId[tracker.inNew] = -1;
                        }
                        // aux docs
                        else
                        {

                            // we have already created a new entry so we update it
                            CacheEntry newCacheEntry = unmatchedByDBID.remove(old.dbid);
                            if (newCacheEntry != null)
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

                            indexedByDocId[tracker.inNew] = newCacheEntry;

                            if (allLeafDocs.get(tracker.inNew))
                            {
                                allLeafDocs.flip(tracker.inNew);
                            }

                            aclIdByDocId[tracker.inNew] = -1;

                        }
                        tracker.inNew++;
                        tracker.inOld++;
                    }
                    else if (oldAclIdByDocId[tracker.inOld] >= 0)
                    {
                        indexedByDocId[tracker.inNew] = null;
                        aclIdByDocId[tracker.inNew] = oldAclIdByDocId[tracker.inOld];
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }
                        tracker.inNew++;
                        tracker.inOld++;
                    }
                    else
                    {
                        indexedByDocId[tracker.inNew] = null;
                        aclIdByDocId[tracker.inNew] = -1;
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }

                        tracker.inNew++;
                        tracker.inOld++;
                    }
                }
            }
            tracker.inNew = lastNew;
            tracker.inOld = lastOld;

            // assert(tracker.inNew == newStart + getFinalCacheSize());
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
        public void updateCache(CacheUpdateTracker tracker, CacheEntry[] oldIndexedByDocId, long[] oldAclIdByDocId, CacheEntry[] indexedByDocId, OpenBitSet allLeafDocs,
                long[] aclIdByDocId, HashMap<Long, CacheEntry> unmatchedByDBID, OpenBitSet deleted, SolrIndexReader reader)
        {
            // delete all existing

            buildCacheForReader(indexedByDocId, allLeafDocs, aclIdByDocId, getFinalIndexReader(), tracker.inNew, getFinalCacheSize(), unmatchedByDBID);
            tracker.inNew += getFinalCacheSize();
            tracker.inOld += getOldCacheSize();
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
        public void updateCache(CacheUpdateTracker tracker, CacheEntry[] oldIndexedByDocId, long[] oldAclIdByDocId, CacheEntry[] indexedByDocId, OpenBitSet allLeafDocs,
                long[] aclIdByDocId, HashMap<Long, CacheEntry> unmatchedByDBID, OpenBitSet deleted, SolrIndexReader reader)
        {

            int lastNew = tracker.inNew + getFinalCacheSize();
            int lastOld = tracker.inOld + getOldCacheSize();

            while ((tracker.inNew < lastNew) && (tracker.inOld < lastOld))
            {
                if (deleted.get(tracker.inOld))
                {
                    // nothing to move
                    tracker.inOld++;
                }
                else
                {
                    CacheEntry old = oldIndexedByDocId[tracker.inOld];
                    if (old != null)
                    {
                        // leaf docs
                        if (old.leaf == tracker.inOld)
                        {

                            // we have already created a new entry so we update it
                            CacheEntry newCacheEntry = unmatchedByDBID.remove(old.dbid);
                            if (newCacheEntry != null)
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

                            indexedByDocId[tracker.inNew] = newCacheEntry;

                            allLeafDocs.set(tracker.inNew);

                            aclIdByDocId[tracker.inNew] = -1;

                        }
                        // aux docs
                        else
                        {

                            // we have already created a new entry so we update it
                            CacheEntry newCacheEntry = unmatchedByDBID.remove(old.dbid);
                            if (newCacheEntry != null)
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

                            indexedByDocId[tracker.inNew] = newCacheEntry;

                            if (allLeafDocs.get(tracker.inNew))
                            {
                                allLeafDocs.flip(tracker.inNew);
                            }

                            aclIdByDocId[tracker.inNew] = -1;

                        }
                        tracker.inNew++;
                        tracker.inOld++;
                    }
                    else if (oldAclIdByDocId[tracker.inOld] >= 0)
                    {
                        indexedByDocId[tracker.inNew] = null;
                        aclIdByDocId[tracker.inNew] = oldAclIdByDocId[tracker.inOld];
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }
                        tracker.inNew++;
                        tracker.inOld++;
                    }
                    else
                    {

                        indexedByDocId[tracker.inNew] = null;
                        aclIdByDocId[tracker.inNew] = -1;
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }
                        tracker.inOld++;
                    }
                }
            }

            // at point to rebuild
            // assume it is fast to here

            if ((((lastNew - tracker.inNew) * 100) / finalCacheSize) > 50)
            {
                buildCacheForReader(indexedByDocId, allLeafDocs, aclIdByDocId, getFinalIndexReader(), tracker.inNew, getFinalCacheSize(), unmatchedByDBID);
                tracker.inNew += getFinalCacheSize();
                tracker.inOld += getOldCacheSize();
            }
            else
            {
                while ((tracker.inNew < lastNew))
                {
                    updateCacheByDocId(tracker, indexedByDocId, allLeafDocs, aclIdByDocId, unmatchedByDBID, deleted, reader);
                }
            }

            tracker.inNew = lastNew;
            tracker.inOld = lastOld;
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
        int inOld;

        int inNew;

        CacheUpdateTracker(int inOld, int inNew)
        {
            this.inOld = inOld;
            this.inNew = inNew;
        }
    }

    /**
     * Keep for inremental build
     */

    private void updateCacheByDocId(CacheUpdateTracker tracker, CacheEntry[] indexedByDocId, OpenBitSet allLeafDocs, long[] aclIdByDocId,
            HashMap<Long, CacheEntry> unmatchedByDBID, OpenBitSet deleted, SolrIndexReader reader)
    {
        try
        {

            if (reader.isDeleted(tracker.inNew))
            {
                indexedByDocId[tracker.inNew] = null;
                aclIdByDocId[tracker.inNew] = -1;
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

                    CacheEntry entry = unmatchedByDBID.get(dbid);
                    if (entry == null)
                    {
                        entry = new CacheEntry(dbid);
                        unmatchedByDBID.put(dbid, entry);
                    }
                    else
                    {
                        unmatchedByDBID.remove(dbid);
                    }

                    if (isLeaf)
                    {
                        entry.setLeaf(tracker.inNew);
                        allLeafDocs.set(tracker.inNew);
                    }
                    else
                    {
                        entry.setPath(tracker.inNew);
                        entry.setAclid(aclId);
                        entry.setOwner(getOwnerId(owner));
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }
                    }

                    indexedByDocId[tracker.inNew] = entry;
                    aclIdByDocId[tracker.inNew] = -1;

                }
                else
                {
                    // ACL DOC
                    field = document.getField("ACLID");
                    if (field != null)
                    {
                        String string = field.stringValue();
                        long aclId = Long.parseLong(string);

                        indexedByDocId[tracker.inNew] = null;
                        aclIdByDocId[tracker.inNew] = aclId;
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }
                    }
                    else
                    {
                        indexedByDocId[tracker.inNew] = null;
                        aclIdByDocId[tracker.inNew] = -1;
                        if (allLeafDocs.get(tracker.inNew))
                        {
                            allLeafDocs.flip(tracker.inNew);
                        }
                    }
                }
            }
            tracker.inNew++;
        }
        catch (IOException e)
        {

        }
    }

}
