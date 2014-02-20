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
package org.alfresco.repo.search.impl.lucene.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.impl.lucene.index.CachingIndexReader;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermPositions;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.Weight;

/**
 * Leaf scorer to complete path queries
 * 
 * @author andyh
 */
public class LeafScorer extends Scorer
{
    /**
     * The logger.
     */
    private static Log s_logger = LogFactory.getLog(LeafScorer.class);

    static class Counter
    {
        int count = 0;

        public String toString()
        {
            return "count = " + count;
        }
    }

    private int counter;

    private int countInCounter;

    int min = -1;

    int max = -1;

    boolean more = true;

    Scorer containerScorer;

    StructuredFieldPosition[] sfps;

    float freq = 0.0f;

    HashMap<String, Counter> parentIds = new HashMap<String, Counter>();

    HashMap<String, List<String>> categories = new HashMap<String, List<String>>();

    HashMap<String, Counter> selfIds = null;

    boolean hasSelfScorer;

    IndexReader reader;
    
    BitSet allNodesCandiates = new BitSet();

    TermPositions level0;

    HashSet<String> selfLinks = new HashSet<String>();

    BitSet selfDocs = new BitSet();

    private TermPositions root;

    private int rootDoc;

    private boolean repeat;

    private DictionaryService dictionaryService;

    private int[] parents;

    private int[] self;

    private int[] cats;

    private boolean matchAllLeaves;

    private boolean followParentInLevel0;
    
    /**
     * Constructor - should use an arg object ...
     * 
     * @param weight
     * @param root
     * @param level0
     * @param containerScorer
     * @param sfps
     * @param allNodes
     * @param selfIds
     * @param reader
     * @param similarity
     * @param norms
     * @param dictionaryService
     * @param repeat
     * @param tp
     */
    public LeafScorer(Weight weight, TermPositions root, TermPositions level0, ContainerScorer containerScorer, StructuredFieldPosition[] sfps, boolean followParentInLevel0,
            HashMap<String, Counter> selfIds, IndexReader reader, Similarity similarity, byte[] norms, DictionaryService dictionaryService, boolean repeat)
    {
        super(similarity);
        this.root = root;
        this.containerScorer = containerScorer;
        this.sfps = sfps;
        //A this.allNodes = allNodes;
        // this.tp = tp;
        if (selfIds == null)
        {
            this.selfIds = new HashMap<String, Counter>();
            hasSelfScorer = false;
        }
        else
        {
            this.selfIds = selfIds;
            hasSelfScorer = true;
        }
        this.reader = reader;
        this.level0 = level0;
        this.dictionaryService = dictionaryService;
        this.repeat = repeat;
        this.followParentInLevel0 = followParentInLevel0;
        
        matchAllLeaves = allNodes();
        try
        {
            initialise();
        }
        catch (IOException e)
        {
            throw new LeafScorerException("IO Error:", e);
        }

    }

    private String getPathLinkId(IndexReader reader, int n) throws IOException
    {
        if (reader instanceof CachingIndexReader)
        {
            CachingIndexReader cachingIndexReader = (CachingIndexReader) reader;
            return cachingIndexReader.getPathLinkId(n);
        }
        else
        {
            Document document = reader.document(n);
            Field[] fields = document.getFields("ID");
            if (fields != null)
            {
                Field id = fields[fields.length - 1];
                return (id == null) ? null : id.stringValue();
            }
            else
            {
                return null;
            }
        }
    }

    private String getIsCategory(IndexReader reader, int n) throws IOException
    {
        if (reader instanceof CachingIndexReader)
        {
            CachingIndexReader cachingIndexReader = (CachingIndexReader) reader;
            return cachingIndexReader.getIsCategory(n);
        }
        else
        {
            Document document = reader.document(n);
            Field isCategory = document.getField("ISCATEGORY");
            return (isCategory == null) ? null : isCategory.stringValue();
        }
    }

    private String getPath(IndexReader reader, int n) throws IOException
    {
        if (reader instanceof CachingIndexReader)
        {
            CachingIndexReader cachingIndexReader = (CachingIndexReader) reader;
            return cachingIndexReader.getPath(n);
        }
        else
        {
            Document document = reader.document(n);
            Field path = document.getField("PATH");
            return (path == null) ? null : path.stringValue();
        }
    }

    private String getType(IndexReader reader, int n) throws IOException
    {
        if (reader instanceof CachingIndexReader)
        {
            CachingIndexReader cachingIndexReader = (CachingIndexReader) reader;
            return cachingIndexReader.getType(n);
        }
        else
        {
            Document document = reader.document(n);
            Field path = document.getField("TYPE");
            return (path == null) ? null : path.stringValue();
        }
    }

    private String[] getParents(IndexReader reader, int n) throws IOException
    {
        if (reader instanceof CachingIndexReader)
        {
            CachingIndexReader cachingIndexReader = (CachingIndexReader) reader;
            return cachingIndexReader.getParents(n);
        }
        else
        {
            Document document = reader.document(n);
            Field[] fields = document.getFields("PARENT");
            if (fields != null)
            {
                String[] answer = new String[fields.length];
                int i = 0;
                for (Field field : fields)
                {
                    answer[i++] = (field == null) ? null : field.stringValue();
                }
                return answer;
            }
            else
            {
                return null;
            }
        }
    }

    private String[] getlinkAspects(IndexReader reader, int n) throws IOException
    {
        if (reader instanceof CachingIndexReader)
        {
            CachingIndexReader cachingIndexReader = (CachingIndexReader) reader;
            return cachingIndexReader.getLinkAspects(n);
        }
        else
        {
            Document document = reader.document(n);
            Field[] fields = document.getFields("LINKASPECT");
            if (fields != null)
            {
                String[] answer = new String[fields.length];
                int i = 0;
                for (Field field : fields)
                {
                    answer[i++] = (field == null) ? null : field.stringValue();
                }
                return answer;
            }
            else
            {
                return null;
            }
        }
    }

    private void initialise() throws IOException
    {
        if (containerScorer != null)
        {
            parentIds.clear();
            while (containerScorer.next())
            {
                int doc = containerScorer.doc();

                String id = getPathLinkId(reader, doc);
                Counter counter = parentIds.get(id);
                if (counter == null)
                {
                    counter = new Counter();
                    parentIds.put(id, counter);
                }
                counter.count++;

                if (!hasSelfScorer)
                {
                    counter = selfIds.get(id);
                    if (counter == null)
                    {
                        counter = new Counter();
                        selfIds.put(id, counter);
                    }
                    counter.count++;
                }

                String isCategory = getIsCategory(reader, doc);
                if (isCategory != null)
                {
                    String pathString = getPath(reader, doc);
                    if ((pathString.length() > 0) && (pathString.charAt(0) == '/'))
                    {
                        pathString = pathString.substring(1);
                    }
                    List<String> list = categories.get(id);
                    if (list == null)
                    {
                        list = new ArrayList<String>();
                        categories.put(id, list);
                    }
                    list.add(pathString);
                }
            }
        }
        else if (level0 != null)
        {
            parentIds.clear();
            while (level0.next())
            {
                int doc = level0.doc();
                String id = getPathLinkId(reader, doc);
                if (id != null)
                {
                    Counter counter = parentIds.get(id);
                    if (counter == null)
                    {
                        counter = new Counter();
                        parentIds.put(id, counter);
                    }
                    counter.count++;

                    if (!hasSelfScorer)
                    {
                        counter = selfIds.get(id);
                        if (counter == null)
                        {
                            counter = new Counter();
                            selfIds.put(id, counter);
                        }
                        counter.count++;
                    }
                }
            }
            if (parentIds.size() > 1)
            {
                throw new LeafScorerException("More than one root node in index: " + parentIds.size());
            }
            else if (parentIds.size() == 0)
            {
                if (s_logger.isWarnEnabled())
                {
                    s_logger.warn("Index has no root node.  Check that the correct index locations are being used.");
                }
            }
        }

        if (matchAllLeaves)
        {
            int position = 0;
            parents = new int[10000];
            ArrayList<String> ordered = new ArrayList<String>(parentIds.size());
            ordered.addAll(parentIds.keySet());
            Collections.sort(ordered);
            for (String parent : ordered)
            {
                Counter counter = parentIds.get(parent);
                // tp.seek(new Term("PARENT", parent));
                TermPositions tp = reader.termPositions(new Term("PARENT", parent));
                while (tp.next())
                {
                    if((level0 == null) || followParentInLevel0)
                    {
                        allNodesCandiates.set(tp.doc());
                    }
                    
                    for (int i = 0, l = tp.freq(); i < l; i++)
                    {
                        for (int j = 0; j < counter.count; j++)
                        {
                            parents[position++] = tp.doc();
                            if (position == parents.length)
                            {
                                int[] old = parents;
                                parents = new int[old.length * 2];
                                System.arraycopy(old, 0, parents, 0, old.length);
                            }
                        }

                    }
                }
                tp.close();

            }
            int[] old = parents;
            parents = new int[position];
            System.arraycopy(old, 0, parents, 0, position);
            Arrays.sort(parents);

            position = 0;
            self = null;
            ordered = new ArrayList<String>(selfIds.size());
            ordered.addAll(selfIds.keySet());
            Collections.sort(ordered);
            TermDocs leafTp = null;
            for (String id : ordered)
            {
                boolean found = false;
                
                // Optimization: jump straight to leaves using leafid. Can't use this if level0 or using self axis
                if (level0 == null && !sfps[sfps.length - 1].linkSelf())
                {
                    TermDocs td = reader.termDocs(new Term("LEAFID", id));
                    while (td.next())
                    {
                        found = true;
                        allNodesCandiates.set(td.doc());
                    }
                    td.close();
                }

                // General case plus fallback for indexes without LEAFID
                if (!found)
                {
                    if (self == null)
                    {
                        self = new int[10000];
                    }

                    // tp.seek(new Term("ID", id));
                    TermPositions tp = reader.termPositions(new Term("ID", id));
                    while (tp.next())
                    {
                        int target = tp.doc();
                        // should order and then check leafyness after
                        if(leafTp == null)
                        {
                            leafTp = reader.termDocs(new Term("ISNODE", "T"));
                        }
                        else
                        {
                            leafTp.seek(new Term("ISNODE", "T"));
                        }
                        if((leafTp.skipTo(target) && leafTp.doc() == target)||(level0 != null))
                        {
                            allNodesCandiates.set(tp.doc());
                        }
                        Counter counter = selfIds.get(id);
                        for (int i = 0; i < counter.count; i++)
                        {
                            self[position++] = tp.doc();
                            if (position == self.length)
                            {
                                old = self;
                                self = new int[old.length * 2];
                                System.arraycopy(old, 0, self, 0, old.length);
                            }
                        }
                    }
                    tp.close();
                }
            }
            if(leafTp != null)
            {
                leafTp.close();
            }
            if (self != null)
            {
                old = self;
                self = new int[position];
                System.arraycopy(old, 0, self, 0, position);
                Arrays.sort(self);
            }

            position = 0;
            cats = new int[10000];
            ordered = new ArrayList<String>(categories.size());
            ordered.addAll(categories.keySet());
            Collections.sort(ordered);
            for (String catid : ordered)
            {
                for (QName apsectQName : dictionaryService.getAllAspects())
                {
                    AspectDefinition aspDef = dictionaryService.getAspect(apsectQName);
                    if (isCategorised(aspDef))
                    {
                        for (PropertyDefinition propDef : aspDef.getProperties().values())
                        {
                            if (propDef.getDataType().getName().equals(DataTypeDefinition.CATEGORY))
                            {
                                // tp.seek(new Term("@" + propDef.getName().toString(), catid));
                                TermPositions tp = reader.termPositions(new Term("@" + propDef.getName().toString(), catid));
                                while (tp.next())
                                {
                                    allNodesCandiates.set(tp.doc());
                                    for (int i = 0, l = tp.freq(); i < l; i++)
                                    {
                                        cats[position++] = tp.doc();
                                        if (position == cats.length)
                                        {
                                            old = cats;
                                            cats = new int[old.length * 2];
                                            System.arraycopy(old, 0, cats, 0, old.length);
                                        }
                                    }
                                }
                                tp.close();
                            }
                        }
                    }
                }

            }
            old = cats;
            cats = new int[position];
            System.arraycopy(old, 0, cats, 0, position);
            Arrays.sort(cats);
            
            // always consider the root node
            TermPositions tp = reader.termPositions(new Term("ISROOT", "T"));
            while (tp.next())
            {
                allNodesCandiates.set(tp.doc());
            }
            tp.close();
        }
    }

    public boolean next() throws IOException
    {

        if (repeat && (countInCounter < counter))
        {
            countInCounter++;
            return true;
        }
        else
        {
            countInCounter = 1;
            counter = 0;
        }

        if (matchAllLeaves)
        {
            while (more)
            {
                max = allNodesCandiates.nextSetBit(max+1);
                if (max != -1)
                {
                    if (check())
                    {
                        return true;
                    }
                }
                else
                {
                    doClose();
                    more = false;
                    return false;
                }
            }
        }

        if (!more)
        {
            // One of the search terms has no more docuements
            return false;
        }

        if (max == -1)
        {
            // We need to initialise
            // Just do a next on all terms and check if the first doc matches
            doNextOnAll();
            if (found())
            {
                return true;
            }
        }

        return findNext();
    }

    private void doClose() throws IOException
    {
        if(sfps != null)
        {
            for(StructuredFieldPosition sfp : sfps)
            {
                CachingTermPositions ctp = sfp.getCachingTermPositions();
                if(ctp != null)
                {
                    ctp.close();
                }
            }
         }
        if(level0 != null)
        {
            level0.close();
        }
        if(root != null)
        {
            root .close();
        }
    }
    
    private boolean allNodes()
    {
        if (sfps.length == 0)
        {
            return true;
        }
        for (StructuredFieldPosition sfp : sfps)
        {
            if (sfp.getCachingTermPositions() != null)
            {
                return false;
            }
        }
        return true;
    }

    private boolean findNext() throws IOException
    {
        // Move to the next document

        while (more)
        {
            move(); // may set more to false
            if (found())
            {
                return true;
            }
        }

        doClose();
        // If we get here we must have no more documents
        return false;
    }

    private void skipToMax() throws IOException
    {
        // Do the terms
        int current;
        for (int i = 0, l = sfps.length; i < l; i++)
        {
            if (i == 0)
            {
                min = max;
            }
            if (sfps[i].getCachingTermPositions() != null)
            {
                if (sfps[i].getCachingTermPositions().doc() < max)
                {
                    if (sfps[i].getCachingTermPositions().skipTo(max))
                    {
                        current = sfps[i].getCachingTermPositions().doc();
                        adjustMinMax(current, false);
                    }
                    else
                    {
                        more = false;
                        return;
                    }
                }
            }
        }

        // Do the root
        if (root.doc() < max)
        {
            if (root.skipTo(max))
            {
                rootDoc = root.doc();
            }
            else
            {
                more = false;
                return;
            }
        }
    }

    private void move() throws IOException
    {
        if (min == max)
        {
            // If we were at a match just do next on all terms
            doNextOnAll();
        }
        else
        {
            // We are in a range - try and skip to the max position on all terms
            skipToMax();
        }
    }

    private void doNextOnAll() throws IOException
    {
        // Do the terms
        int current;
        boolean first = true;
        for (int i = 0, l = sfps.length; i < l; i++)
        {
            if (sfps[i].getCachingTermPositions() != null)
            {
                if (sfps[i].getCachingTermPositions().next())
                {
                    current = sfps[i].getCachingTermPositions().doc();
                    adjustMinMax(current, first);
                    first = false;
                }
                else
                {
                    more = false;
                    return;
                }
            }
        }

        // Do the root term
        if (root.next())
        {
            rootDoc = root.doc();
        }
        else
        {
            more = false;
            return;
        }
        if (root.doc() < max)
        {
            if (root.skipTo(max))
            {
                rootDoc = root.doc();
            }
            else
            {
                more = false;
                return;
            }
        }
    }

    private void adjustMinMax(int doc, boolean setMin)
    {

        if (max < doc)
        {
            max = doc;
        }

        if (setMin)
        {
            min = doc;
        }
        else if (min > doc)
        {
            min = doc;
        }
    }

    private boolean found() throws IOException
    {
        if (sfps.length == 0)
        {
            return true;
        }

        // no more documents - no match
        if (!more)
        {
            return false;
        }

        // min and max must point to the same document
        if (min != max)
        {
            return false;
        }

        if (rootDoc != max)
        {
            return false;
        }

        return check();
    }

    private boolean check() throws IOException
    {
        if (matchAllLeaves)
        {
            this.counter = 0;
            int position;

            StructuredFieldPosition last = sfps[sfps.length - 1];

            if (last.linkSelf())
            {
                if ((self != null) && sfps[1].linkSelf() && ((position = Arrays.binarySearch(self, max)) >= 0))
                {
                    if (!selfDocs.get(max))
                    {
                        selfDocs.set(max);
                        while (position > -1 && self[position] == max)
                        {
                            position--;
                        }
                        for (int i = position + 1, l = self.length; ((i < l) && (self[i] == max)); i++)
                        {
                            this.counter++;
                        }
                    }
                }
            }
            if (!selfDocs.get(max) && last.linkParent())
            {
                if ((parents != null) && ((position = Arrays.binarySearch(parents, max)) >= 0))
                {
                    while (position > -1 && parents[position] == max)
                    {
                        position--;
                    }
                    for (int i = position + 1, l = parents.length; ((i < l) && (parents[i] == max)); i++)
                    {
                        this.counter++;
                    }
                }

                if ((cats != null) && ((position = Arrays.binarySearch(cats, max)) >= 0))
                {
                    while (position > -1 && cats[position] == max)
                    {
                        position--;
                    }
                    for (int i = position + 1, l = cats.length; ((i < l) && (cats[i] == max)); i++)
                    {
                        this.counter++;
                    }
                }
            }
            return counter > 0;
        }

        // String name = reader.document(doc()).getField("QNAME").stringValue();
        // We have duplicate entries
        // The match must be in a known term range
        int count = root.freq();
        int start = 0;
        int end = -1;
        for (int i = 0; i < count; i++)
        {
            if (i == 0)
            {
                // First starts at zero
                start = 0;
                end = root.nextPosition();
            }
            else
            {
                start = end + 1;
                end = root.nextPosition();
            }

            check(start, end, i);

        }
        // We had checks to do and they all failed.
        return this.counter > 0;
    }

    private void check(int start, int end, int position) throws IOException
    {
        int offset = 0;
        for (int i = 0, l = sfps.length; i < l; i++)
        {
            offset = sfps[i].matches(start, end, offset);
            if (offset == -1)
            {
                return;
            }
        }
        // Last match may fail
        if (offset == -1)
        {
            return;
        }
        else
        {
            if ((sfps[sfps.length - 1].isTerminal()) && (offset != 2))
            {
                return;
            }
        }

        // Document doc = reader.document(doc());
        String[] parentFields = getParents(reader, doc());

        String[] linkFields = null;
        if (categories.size() > 0)
        {
            linkFields = getlinkAspects(reader, doc());
        }

        String parentID = null;
        String linkAspect = null;
        if ((parentFields != null) && (parentFields.length > position) && (parentFields[position] != null))
        {
            parentID = parentFields[position];
        }
        if ((linkFields != null) && (linkFields.length > position) && (linkFields[position] != null))
        {
            linkAspect = linkFields[position];
        }

        containersIncludeCurrent(parentID, linkAspect);

    }

    private void containersIncludeCurrent(String parentID, String aspectQName) throws IOException
    {
        if ((containerScorer != null) || (level0 != null))
        {
            if (sfps.length == 0)
            {
                return;
            }
            String id = getPathLinkId(reader, doc());
            StructuredFieldPosition last = sfps[sfps.length - 1];
            if ((last.linkSelf() && selfIds.containsKey(id)))
            {
                Counter counter = selfIds.get(id);
                if (counter != null)
                {
                    if (!selfLinks.contains(id))
                    {
                        this.counter += counter.count;
                        selfLinks.add(id);
                        return;
                    }
                }
            }
            if ((parentID != null) && (parentID.length() > 0) && last.linkParent())
            {
                if (!selfLinks.contains(id))
                {
                    if (categories.containsKey(parentID))
                    {
                        String type = getType(reader, doc());
                        if (type != null)
                        {
                            QName typeRef = QName.createQName(type);
                            if (isCategory(typeRef))
                            {
                                Counter counter = parentIds.get(parentID);
                                if (counter != null)
                                {
                                    this.counter += counter.count;
                                    return;
                                }
                            }
                        }

                        if (aspectQName != null)
                        {
                            QName classRef = QName.createQName(aspectQName);
                            AspectDefinition aspDef = dictionaryService.getAspect(classRef);
                            if (isCategorised(aspDef))
                            {
                                for (PropertyDefinition propDef : aspDef.getProperties().values())
                                {
                                    if (propDef.getDataType().getName().equals(DataTypeDefinition.CATEGORY))
                                    {
                                        // get field and compare to ID
                                        // Check in path as QName
                                        // somewhere
                                        Document document = reader.document(doc());
                                        Field[] categoryFields = document.getFields("@" + propDef.getName());
                                        if (categoryFields != null)
                                        {
                                            for (Field categoryField : categoryFields)
                                            {
                                                if ((categoryField != null) && (categoryField.stringValue() != null))
                                                {
                                                    if (categoryField.stringValue().endsWith(parentID))
                                                    {
                                                        int count = 0;
                                                        List<String> paths = categories.get(parentID);
                                                        if (paths != null)
                                                        {
                                                            for (String path : paths)
                                                            {
                                                                if (path.indexOf(aspectQName) != -1)
                                                                {
                                                                    count++;
                                                                }
                                                            }
                                                        }
                                                        this.counter += count;
                                                        return;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                    else
                    {
                        Counter counter = parentIds.get(parentID);
                        if (counter != null)
                        {
                            this.counter += counter.count;
                            return;
                        }
                    }

                }
            }

            return;
        }
        else
        {
            return;
        }
    }

    private boolean isCategory(QName classRef)
    {
        if (classRef == null)
        {
            return false;
        }
        TypeDefinition current = dictionaryService.getType(classRef);
        while (current != null)
        {
            if (current.getName().equals(ContentModel.TYPE_CATEGORY))
            {
                return true;
            }
            else
            {
                QName parentName = current.getParentName();
                if (parentName == null)
                {
                    break;
                }
                current = dictionaryService.getType(parentName);
            }
        }
        return false;
    }

    private boolean isCategorised(AspectDefinition aspDef)
    {
        if(aspDef == null)
        {
            return false;
        }
        AspectDefinition current = aspDef;
        while (current != null)
        {
            if (current.getName().equals(ContentModel.ASPECT_CLASSIFIABLE))
            {
                return true;
            }
            else
            {
                QName parentName = current.getParentName();
                if (parentName == null)
                {
                    break;
                }
                current = dictionaryService.getAspect(parentName);
            }
        }
        return false;
    }

    public int doc()
    {
        return max;
    }

    public float score() throws IOException
    {
        return repeat ? 1.0f : counter;
    }

    public boolean skipTo(int target) throws IOException
    {

        countInCounter = 1;
        counter = 0;

        if (matchAllLeaves)
        {
            max = allNodesCandiates.nextSetBit(target > max ? target : max+1);
            if (max != -1)
            {
                root.skipTo(max); // must match
                if (check())
                {
                    return true;
                }
                while (more)
                {
                    max = allNodesCandiates.nextSetBit(max+1);
                    if (max != -1)
                    {
                        root.skipTo(max); // must match
                        if (check())
                        {
                            return true;
                        }
                    }
                    else
                    {
                        more = false;
                        return false;
                    }
                }
            }
            else
            {
                more = false;
                return false;
            }
        }

        max = target;
        return findNext();
    }

    public Explanation explain(int doc) throws IOException
    {
        Explanation tfExplanation = new Explanation();

        while (next() && doc() < doc)
        {
        }

        float phraseFreq = (doc() == doc) ? freq : 0.0f;
        tfExplanation.setValue(getSimilarity().tf(phraseFreq));
        tfExplanation.setDescription("tf(phraseFreq=" + phraseFreq + ")");

        return tfExplanation;
    }

}
