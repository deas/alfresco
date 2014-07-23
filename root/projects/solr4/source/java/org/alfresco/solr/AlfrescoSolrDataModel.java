/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.alfresco.service.namespace.QName;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.util.BytesRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andy
 *
 */
public class AlfrescoSolrDataModel
{

    protected final static Logger log = LoggerFactory.getLogger(AlfrescoSolrDataModel.class);
    
    private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private static AlfrescoSolrDataModel model;

    private AlfrescoFieldType alfrescoFieldType;
    
    /**
     * @param id
     */
    public AlfrescoSolrDataModel()
    {
        super();
    }

    /**
     * @param id
     * @return
     */
    public static AlfrescoSolrDataModel getInstance()
    {
        readWriteLock.readLock().lock();
        try
        {
            if (model != null)
            {
                return model;
            }
        }
        finally
        {
            readWriteLock.readLock().unlock();
        }

        // not found

        readWriteLock.writeLock().lock();
        try
        {
            if (model == null)
            {
                model = new AlfrescoSolrDataModel();
            }
            return model;
        }
        finally
        {
            readWriteLock.writeLock().unlock();
        }

    }
   
    // Index 
    public List<String> getIndexedFieldNamesForProperty(QName propertyQName)
    {
        return Collections.singletonList(propertyQName.toString());
    }
    
    // FTS   - Term/Phrase/Range/Fuzzy/Prefix/Proximity/Wild
    // ID    - Exact/ExactRange - Comparison, In, Upper, Lower
    // FACET - Field, Range, Query, Stats
    // SORT  - Locale
    // CROSS-LOCALE
    // PHRASE SUGGESTION
    // COMPLETION
    // HIGHLIGHT
    public String getSearchFieldNameForProperty(QName propertyQName)
    {
        return propertyQName.toString();
    }
    
    
//    public SortField getSortField(SchemaField field, boolean reverse)
//    {
//        // MNT-8557 fix, manually replace '%20' with ' '
//        String fieldNameToUse = field.getName().replaceAll("%20", " ");
//        PropertyDefinition propertyDefinition = getPropertyDefinition(fieldNameToUse);
//        if (propertyDefinition != null)
//        {
//            if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.TEXT))
//            {
//                // ignore locale store in the text field
//
//                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
//                {
//                    return new SortField(expandFieldName(fieldNameToUse) + ".sort", new TextSortFieldComparatorSource(), reverse);
//                }
//                else
//                {
//                    throw new UnsupportedOperationException("Ordering not supported for " + fieldNameToUse);
//                }
//            }
//            else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.MLTEXT))
//            {
//                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
//                {
//                    return new SortField(expandFieldName(fieldNameToUse) + ".sort", new MLTextSortFieldComparatorSource(), reverse);
//                }
//                else
//                {
//                    throw new UnsupportedOperationException("Ordering not supported for " + fieldNameToUse);
//                }
//            }
//            else
//            {
//                return Sorting.getStringSortField(expandFieldName(fieldNameToUse), reverse, field.sortMissingLast(), field.sortMissingFirst());
//            }
//        }
//        else
//        { 
//            return Sorting.getStringSortField(fieldNameToUse, reverse, field.sortMissingLast(), field.sortMissingFirst());
//        }
//
//    }
    
//    private PropertyDefinition getPropertyDefinition(String fieldName)
//    {
//        QName rawPropertyName = QName.createQName(expandFieldName(fieldName).substring(1));
//        QName propertyQName = QName.createQName(rawPropertyName.getNamespaceURI(), ISO9075.decode(rawPropertyName.getLocalName()));
//        return getPropertyDefinition(propertyQName);
//    }
    
    
    private String expandFieldName(String fieldName)
    {
        String expandedFieldName = fieldName;
        if (fieldName.startsWith("@"))
        {
            expandedFieldName = expandAttributeFieldName(fieldName);
        }
        else if (fieldName.startsWith("{"))
        {
            expandedFieldName = expandFieldName("@" + fieldName);
        }
        else if (fieldName.contains(":"))
        {
            expandedFieldName = expandFieldName("@" + fieldName);
        }
        return expandedFieldName;

    }

    private String expandAttributeFieldName(String field)
    {
        String fieldName = field;
        // Check for any prefixes and expand to the full uri
        if (field.charAt(1) != '{')
        {
            int colonPosition = field.indexOf(':');
            if (colonPosition == -1)
            {
                // use the default namespace
                //fieldName = "@{" + getNamespaceDAO().getNamespaceURI("") + "}" + field.substring(1);
            }
            else
            {
                // find the prefix
                //fieldName = "@{" + getNamespaceDAO().getNamespaceURI(field.substring(1, colonPosition)) + "}" + field.substring(colonPosition + 1);
            }
        }
        return fieldName;
    }
    
//    public PropertyDefinition getPropertyDefinition(QName propertyQName)
//    {
//        PropertyDefinition propertyDef = getDictionaryService(CMISStrictDictionaryService.DEFAULT).getProperty(propertyQName);
//        if ((propertyDef != null) && (propertyDef.getName().equals(ContentModel.PROP_AUTHOR)))
//        {
//            return new PropertyDefinitionWrapper(propertyDef);
//        }
//        else if ((propertyDef != null) && (propertyDef.getName().equals(ContentModel.PROP_CREATOR)))
//        {
//            return new PropertyDefinitionWrapper(propertyDef);
//        }
//        else if ((propertyDef != null) && (propertyDef.getName().equals(ContentModel.PROP_MODIFIER)))
//        {
//            return new PropertyDefinitionWrapper(propertyDef);
//        }
//        return propertyDef;
//    }
    
//    public static class TextSortFieldComparatorSource extends FieldComparatorSource
//    {
//
//        /*
//         * (non-Javadoc)
//         * @see org.apache.lucene.search.FieldComparatorSource#newComparator(java.lang.String, int, int, boolean)
//         */
//        @Override
//        public FieldComparator newComparator(String fieldname, int numHits, int sortPos, boolean reversed) throws IOException
//        {
//            return new TextSortFieldComparator(numHits, fieldname, I18NUtil.getLocale());
//        }
//
//    }

    /** Sorts by field's natural Term sort order, using
     *  ordinals.  This is functionally equivalent to {@link
     *  org.apache.lucene.search.FieldComparator.TermValComparator}, but it first resolves the string
     *  to their relative ordinal positions (using the index
     *  returned by {@link FieldCache#getTermsIndex}), and
     *  does most comparisons using the ordinals.  For medium
     *  to large results, this comparator will be much faster
     *  than {@link org.apache.lucene.search.FieldComparator.TermValComparator}.  For very small
     *  result sets it may be slower. */
    public static final class TermOrdValComparator extends FieldComparator<BytesRef> {
      /* Ords for each slot.
         @lucene.internal */
      final int[] ords;

      /* Values for each slot.
         @lucene.internal */
      final BytesRef[] values;

      /* Which reader last copied a value into the slot. When
         we compare two slots, we just compare-by-ord if the
         readerGen is the same; else we must compare the
         values (slower).
         @lucene.internal */
      final int[] readerGen;

      /* Gen of current reader we are on.
         @lucene.internal */
      int currentReaderGen = -1;

      /* Current reader's doc ord/values.
         @lucene.internal */
      SortedDocValues termsIndex;

      private final String field;

      /* Bottom slot, or -1 if queue isn't full yet
         @lucene.internal */
      int bottomSlot = -1;

      /* Bottom ord (same as ords[bottomSlot] once bottomSlot
         is set).  Cached for faster compares.
         @lucene.internal */
      int bottomOrd;

      /* True if current bottom slot matches the current
         reader.
         @lucene.internal */
      boolean bottomSameReader;

      /* Bottom value (same as values[bottomSlot] once
         bottomSlot is set).  Cached for faster compares.
        @lucene.internal */
      BytesRef bottomValue;

      /** Set by setTopValue. */
      BytesRef topValue;
      boolean topSameReader;
      int topOrd;

      private int docBase;

      final BytesRef tempBR = new BytesRef();

      /** -1 if missing values are sorted first, 1 if they are
       *  sorted last */
      final int missingSortCmp;
      
      /** Which ordinal to use for a missing value. */
      final int missingOrd;

      /** Creates this, sorting missing values first. */
      public TermOrdValComparator(int numHits, String field) {
        this(numHits, field, false);
      }

      /** Creates this, with control over how missing values
       *  are sorted.  Pass sortMissingLast=true to put
       *  missing values at the end. */
      public TermOrdValComparator(int numHits, String field, boolean sortMissingLast) {
        ords = new int[numHits];
        values = new BytesRef[numHits];
        readerGen = new int[numHits];
        this.field = field;
        if (sortMissingLast) {
          missingSortCmp = 1;
          missingOrd = Integer.MAX_VALUE;
        } else {
          missingSortCmp = -1;
          missingOrd = -1;
        }
      }

      @Override
      public int compare(int slot1, int slot2) {
        if (readerGen[slot1] == readerGen[slot2]) {
          return ords[slot1] - ords[slot2];
        }

        final BytesRef val1 = values[slot1];
        final BytesRef val2 = values[slot2];
        if (val1 == null) {
          if (val2 == null) {
            return 0;
          }
          return missingSortCmp;
        } else if (val2 == null) {
          return -missingSortCmp;
        }
        return val1.compareTo(val2);
      }

      @Override
      public int compareBottom(int doc) {
        assert bottomSlot != -1;
        int docOrd = termsIndex.getOrd(doc);
        if (docOrd == -1) {
          docOrd = missingOrd;
        }
        if (bottomSameReader) {
          // ord is precisely comparable, even in the equal case
          return bottomOrd - docOrd;
        } else if (bottomOrd >= docOrd) {
          // the equals case always means bottom is > doc
          // (because we set bottomOrd to the lower bound in
          // setBottom):
          return 1;
        } else {
          return -1;
        }
      }

      @Override
      public void copy(int slot, int doc) {
        int ord = termsIndex.getOrd(doc);
        if (ord == -1) {
          ord = missingOrd;
          values[slot] = null;
        } else {
          assert ord >= 0;
          if (values[slot] == null) {
            values[slot] = new BytesRef();
          }
          termsIndex.lookupOrd(ord, values[slot]);
        }
        ords[slot] = ord;
        readerGen[slot] = currentReaderGen;
      }
      
      @Override
      public FieldComparator<BytesRef> setNextReader(AtomicReaderContext context) throws IOException {
        docBase = context.docBase;
        termsIndex = FieldCache.DEFAULT.getTermsIndex(context.reader(), field);
        currentReaderGen++;

        if (topValue != null) {
          // Recompute topOrd/SameReader
          int ord = termsIndex.lookupTerm(topValue);
          if (ord >= 0) {
            topSameReader = true;
            topOrd = ord;
          } else {
            topSameReader = false;
            topOrd = -ord-2;
          }
        } else {
          topOrd = missingOrd;
          topSameReader = true;
        }
        //System.out.println("  setNextReader topOrd=" + topOrd + " topSameReader=" + topSameReader);

        if (bottomSlot != -1) {
          // Recompute bottomOrd/SameReader
          setBottom(bottomSlot);
        }

        return this;
      }
      
      @Override
      public void setBottom(final int bottom) {
        bottomSlot = bottom;

        bottomValue = values[bottomSlot];
        if (currentReaderGen == readerGen[bottomSlot]) {
          bottomOrd = ords[bottomSlot];
          bottomSameReader = true;
        } else {
          if (bottomValue == null) {
            // missingOrd is null for all segments
            assert ords[bottomSlot] == missingOrd;
            bottomOrd = missingOrd;
            bottomSameReader = true;
            readerGen[bottomSlot] = currentReaderGen;
          } else {
            final int ord = termsIndex.lookupTerm(bottomValue);
            if (ord < 0) {
              bottomOrd = -ord - 2;
              bottomSameReader = false;
            } else {
              bottomOrd = ord;
              // exact value match
              bottomSameReader = true;
              readerGen[bottomSlot] = currentReaderGen;            
              ords[bottomSlot] = bottomOrd;
            }
          }
        }
      }

      @Override
      public void setTopValue(BytesRef value) {
        // null is fine: it means the last doc of the prior
        // search was missing this value
        topValue = value;
        //System.out.println("setTopValue " + topValue);
      }

      @Override
      public BytesRef value(int slot) {
        return values[slot];
      }

      @Override
      public int compareTop(int doc) {

        int ord = termsIndex.getOrd(doc);
        if (ord == -1) {
          ord = missingOrd;
        }

        if (topSameReader) {
          // ord is precisely comparable, even in the equal
          // case
          //System.out.println("compareTop doc=" + doc + " ord=" + ord + " ret=" + (topOrd-ord));
          return topOrd - ord;
        } else if (ord <= topOrd) {
          // the equals case always means doc is < value
          // (because we set lastOrd to the lower bound)
          return 1;
        } else {
          return -1;
        }
      }

      @Override
      public int compareValues(BytesRef val1, BytesRef val2) {
        if (val1 == null) {
          if (val2 == null) {
            return 0;
          }
          return missingSortCmp;
        } else if (val2 == null) {
          return -missingSortCmp;
        }
        return val1.compareTo(val2);
      }
    }

    /**
     * @param schema
     */
    public void setAlfrescoFieldType(AlfrescoFieldType alfrescoFieldType)
    {
        this.alfrescoFieldType = alfrescoFieldType;
    }
    
    
//    public static final class TextSortFieldComparator extends FieldComparator<String>
//    {
//
//        private final String[] values;
//
//        private String[] currentReaderValues;
//
//        private final String field;
//
//        final Collator collator;
//
//        private String bottom;
//        
//        private String top;
//
//        TextSortFieldComparator(int numHits, String field, Locale locale)
//        {
//            values = new String[numHits];
//            this.field = field;
//            collator = Collator.getInstance(locale);
//        }
//
//        /*
//         * (non-Javadoc)
//         * @see org.apache.lucene.search.FieldComparator#compare(int, int)
//         */
//        @Override
//        public int compare(int slot1, int slot2)
//        {
//            final String val1 = values[slot1];
//            final String val2 = values[slot2];
//            if (val1 == null)
//            {
//                if (val2 == null)
//                {
//                    return 0;
//                }
//                return -1;
//            }
//            else if (val2 == null)
//            {
//                return 1;
//            }
//            return collator.compare(val1, val2);
//        }
//
//        /*
//         * (non-Javadoc)
//         * @see org.apache.lucene.search.FieldComparator#compareBottom(int)
//         */
//        @Override
//        public int compareBottom(int doc)
//        {
//            final String val2 = stripLocale(currentReaderValues[doc]);
//            if (bottom == null)
//            {
//                if (val2 == null)
//                {
//                    return 0;
//                }
//                return -1;
//            }
//            else if (val2 == null)
//            {
//                return 1;
//            }
//            return collator.compare(bottom, val2);
//        }
//
//        /*
//         * (non-Javadoc)
//         * @see org.apache.lucene.search.FieldComparator#copy(int, int)
//         */
//        @Override
//        public void copy(int slot, int doc)
//        {
//            values[slot] = stripLocale(currentReaderValues[doc]);
//        }
//
////        public void setNextReader(IndexReader reader, int docBase) throws IOException
////        {
////            currentReaderValues = FieldCache.DEFAULT.getStrings(reader, field);
////        }
//
//        /*
//         * (non-Javadoc)
//         * @see org.apache.lucene.search.FieldComparator#setBottom(int)
//         */
//        @Override
//        public void setBottom(final int bottom)
//        {
//            this.bottom = values[bottom];
//        }
//
////        public Comparable value(int slot)
////        {
////            return values[slot];
////        }
//
//        private String stripLocale(String withLocale)
//        {
//            if (withLocale == null)
//            {
//                return withLocale;
//            }
//            else if (withLocale.startsWith("\u0000"))
//            {
//                return withLocale.substring(withLocale.indexOf('\u0000', 1) + 1);
//            }
//            else
//            {
//                return withLocale;
//            }
//        }
//
//        /* (non-Javadoc)
//         * @see org.apache.lucene.search.FieldComparator#setTopValue(java.lang.Object)
//         */
//        @Override
//        public void setTopValue(String value)
//        {
//           this.top = value;
//            
//        }
//
//        /* (non-Javadoc)
//         * @see org.apache.lucene.search.FieldComparator#compareTop(int)
//         */
//        @Override
//        public int compareTop(int doc) throws IOException
//        {
//            final String val2 = stripLocale(currentReaderValues[doc]);
//            if (top == null)
//            {
//                if (val2 == null)
//                {
//                    return 0;
//                }
//                return -1;
//            }
//            else if (val2 == null)
//            {
//                return 1;
//            }
//            return collator.compare(top, val2);
//        }
//
//        /* (non-Javadoc)
//         * @see org.apache.lucene.search.FieldComparator#setNextReader(org.apache.lucene.index.AtomicReaderContext)
//         */
//        @Override
//        public FieldComparator setNextReader(AtomicReaderContext context) throws IOException
//        {
//            currentReaderValues = FieldCache.DEFAULT.getTerms(reader, field, setDocsWithField)getStrings(context, field);
//        }
//    }

//    public static class MLTextSortFieldComparatorSource extends FieldComparatorSource
//    {
//
//        /*
//         * (non-Javadoc)
//         * @see org.apache.lucene.search.FieldComparatorSource#newComparator(java.lang.String, int, int, boolean)
//         */
//        @Override
//        public FieldComparator newComparator(String fieldname, int numHits, int sortPos, boolean reversed) throws IOException
//        {
//            return new MLTextSortFieldComparator(numHits, fieldname, I18NUtil.getLocale());
//        }
//
//    }

//    public static final class MLTextSortFieldComparator extends FieldComparator
//    {
//
//        private final String[] values;
//
//        private String[] currentReaderValues;
//
//        private final String field;
//
//        final Collator collator;
//
//        private String bottom;
//
//        Locale collatorLocale;
//
//        MLTextSortFieldComparator(int numHits, String field, Locale collatorLocale)
//        {
//            values = new String[numHits];
//            this.field = field;
//            this.collatorLocale = collatorLocale;
//            collator = Collator.getInstance(collatorLocale);
//        }
//
//        public int compare(int slot1, int slot2)
//        {
//            final String val1 = values[slot1];
//            final String val2 = values[slot2];
//            if (val1 == null)
//            {
//                if (val2 == null)
//                {
//                    return 0;
//                }
//                return -1;
//            }
//            else if (val2 == null)
//            {
//                return 1;
//            }
//            return collator.compare(val1, val2);
//        }
//
//        public int compareBottom(int doc)
//        {
//            final String val2 = findBestValue(currentReaderValues[doc]);
//            if (bottom == null)
//            {
//                if (val2 == null)
//                {
//                    return 0;
//                }
//                return -1;
//            }
//            else if (val2 == null)
//            {
//                return 1;
//            }
//            return collator.compare(bottom, val2);
//        }
//
//        public void copy(int slot, int doc)
//        {
//            values[slot] = findBestValue(currentReaderValues[doc]);
//        }
//
//        public void setNextReader(IndexReader reader, int docBase) throws IOException
//        {
//            currentReaderValues = FieldCache.DEFAULT.getStrings(reader, field);
//        }
//
//        public void setBottom(final int bottom)
//        {
//            this.bottom = values[bottom];
//        }
//
//        public Comparable value(int slot)
//        {
//            return values[slot];
//        }
//
//        private String findBestValue(String withLocale)
//        {
//            // split strin into MLText object
//            if (withLocale == null)
//            {
//                return withLocale;
//            }
//            else if (withLocale.startsWith("\u0000"))
//            {
//                MLText mlText = new MLText();
//                String[] parts = withLocale.split("\u0000");
//                for (int i = 0; (i + 2) <= parts.length; i += 3)
//                {
//                    Locale locale = null;
//                    String[] localeParts = parts[i + 1].split("_");
//                    if (localeParts.length == 1)
//                    {
//                        locale = new Locale(localeParts[0]);
//                    }
//                    else if (localeParts.length == 2)
//                    {
//                        locale = new Locale(localeParts[0], localeParts[1]);
//                    }
//                    else if (localeParts.length == 3)
//                    {
//                        locale = new Locale(localeParts[0], localeParts[1], localeParts[2]);
//                    }
//                    if (locale != null)
//                    {
//                        if (i + 2 == parts.length)
//                        {
//                            mlText.addValue(locale, "");
//                        }
//                        else
//                        {
//                            mlText.addValue(locale, parts[i + 2]);
//                        }
//                    }
//                }
//                return mlText.getClosestValue(collatorLocale);
//            }
//            else
//            {
//                return withLocale;
//            }
//        }
//    }

}
