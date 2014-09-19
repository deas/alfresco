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
import java.text.Collator;
import java.util.Locale;

import org.alfresco.service.cmr.repository.MLText;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.schema.StrField;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * @author Andy
 *
 */
public class AlfrescoCollatableTextFieldType extends StrField
{
    

    /* (non-Javadoc)
     * @see org.apache.solr.schema.StrField#getSortField(org.apache.solr.schema.SchemaField, boolean)
     */
    @Override
    public SortField getSortField(SchemaField field, boolean reverse)
    {
        return new SortField(field.getName(), new TextSortFieldComparatorSource(), reverse);
    }


    public static class TextSortFieldComparatorSource extends FieldComparatorSource
    {

        /*
         * (non-Javadoc)
         * @see org.apache.lucene.search.FieldComparatorSource#newComparator(java.lang.String, int, int, boolean)
         */
        @Override
        public FieldComparator newComparator(String fieldname, int numHits, int sortPos, boolean reversed) throws IOException
        {
            return new TextSortFieldComparator(numHits, fieldname, I18NUtil.getLocale());
        }

    }


    /*
     * Adapted from org.apache.lucene.search.FieldComparator.TermValComparator<T>
     */
    public static final class TextSortFieldComparator extends FieldComparator<String>
    {

        private final String[] values;

        private BinaryDocValues docTerms;
        
        private Bits docsWithField;

        private final String field;

        final Collator collator;

        private String bottom;
        
        private String top;

        Locale collatorLocale;

        TextSortFieldComparator(int numHits, String field, Locale collatorLocale)
        {
            values = new String[numHits];
            this.field = field;
            this.collatorLocale = collatorLocale;
            collator = Collator.getInstance(collatorLocale);
        }

        public int compare(int slot1, int slot2)
        {
            final String val1 = values[slot1];
            final String val2 = values[slot2];
            return compareValues(val1, val2);
        }

        public void setBottom(final int bottom)
        {
            this.bottom = values[bottom];
        }

        public int compareBottom(int doc)
        {
            final String comparableString = findBestValue(doc, docTerms.get(doc));
            return compareValues(bottom, comparableString);
           
        }

        public void copy(int slot, int doc)
        {
            values[slot] = findBestValue(doc, docTerms.get(doc));
        }

        public String value(int slot)
        {
            return values[slot];
        }

        private String findBestValue(int doc, BytesRef term)
        {
            if (term.length == 0 && docsWithField.get(doc) == false) {
                return null;
            }
            
            String withLocale = term.utf8ToString();
            
            // split strin into MLText object
            if (withLocale == null)
            {
                return withLocale;
            }
            else if (withLocale.startsWith("\u0000"))
            {
                String[] parts = withLocale.split("\u0000");
                return parts[1];
            }
            else
            {
                return withLocale;
            }
        }

        /* (non-Javadoc)
         * @see org.apache.lucene.search.FieldComparator#setTopValue(java.lang.Object)
         */
        @Override
        public void setTopValue(String value)
        {
            this.top = value;
        }

        /* (non-Javadoc)
         * @see org.apache.lucene.search.FieldComparator#compareTop(int)
         */
        @Override
        public int compareTop(int doc) throws IOException
        {
            final String comparableString = findBestValue(doc, docTerms.get(doc));
            return compareValues(top, comparableString);
        }

        /* (non-Javadoc)
         * @see org.apache.lucene.search.FieldComparator#setNextReader(org.apache.lucene.index.AtomicReaderContext)
         */
        @Override
        public FieldComparator<String> setNextReader(AtomicReaderContext context) throws IOException
        {
            docTerms = FieldCache.DEFAULT.getTerms(context.reader(), field, true);
            docsWithField = FieldCache.DEFAULT.getDocsWithField(context.reader(), field);
            return this;
        }
        
        @Override
        public int compareValues(String val1, String val2) 
        {
            if (val1 == null)
            {
                if (val2 == null)
                {
                    return 0;
                }
                return -1;
            }
            else if (val2 == null)
            {
                return 1;
            }
            return collator.compare(val1, val2);
        }
    }

}
