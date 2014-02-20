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
package org.alfresco.repo.search.impl.querymodel.impl.lucene;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.repo.search.adaptor.lucene.AnalysisMode;
import org.alfresco.repo.search.adaptor.lucene.LuceneFunction;
import org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor;
import org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserExpressionAdaptor;
import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser;
import org.alfresco.repo.search.impl.lucene.LuceneUtils;
import org.alfresco.repo.search.impl.lucene.analysis.DateTimeAnalyser;
import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.Order;
import org.alfresco.repo.search.impl.querymodel.Ordering;
import org.alfresco.repo.search.impl.querymodel.PropertyArgument;
import org.alfresco.repo.search.impl.querymodel.impl.functions.PropertyAccessor;
import org.alfresco.repo.search.impl.querymodel.impl.functions.Score;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.search.SearchParameters;
import org.apache.lucene.index.IndexReader.FieldOption;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * @author Andy
 *
 */
public class LegacyLuceneQueryParserAdaptor implements LuceneQueryParserAdaptor<Query, Sort, ParseException>
{
    private AbstractLuceneQueryParser lqp;

    /**
     * @param lqp
     */
    public LegacyLuceneQueryParserAdaptor(AbstractLuceneQueryParser lqp)
    {
        this.lqp = lqp;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor#getFieldQuery(java.lang.String, java.lang.String, org.alfresco.repo.search.impl.lucene.AnalysisMode, org.alfresco.repo.search.impl.lucene.LuceneFunction)
     */
    @Override
    public Query getFieldQuery(String field, String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
    {
        return lqp.getFieldQuery(field, queryText, analysisMode, luceneFunction);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor#getRangeQuery(java.lang.String, java.lang.String, java.lang.String, boolean, boolean, org.alfresco.repo.search.impl.lucene.AnalysisMode, org.alfresco.repo.search.impl.lucene.LuceneFunction)
     */
    @Override
    public Query getRangeQuery(String field, String part1, String part2, boolean includeLower, boolean includeUpper, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
    {
        return lqp.getRangeQuery(field, part1, part2, includeLower, includeUpper, analysisMode, luceneFunction);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor#getMatchAllQuery()
     */
    @Override
    public Query getMatchAllQuery()
    {
        return new MatchAllDocsQuery();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor#getMatchNoneQuery()
     */
    @Override
    public Query getMatchNoneQuery()
    {
        return new TermQuery(new Term("NO_TOKENS", "__"));
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor#getLikeQuery(java.lang.String, java.lang.String, org.alfresco.repo.search.impl.lucene.AnalysisMode)
     */
    @Override
    public Query getLikeQuery(String field, String sqlLikeClause, AnalysisMode analysisMode) throws ParseException
    {
        return lqp.getLikeQuery(field, sqlLikeClause, analysisMode);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor#getSearchParameters()
     */
    @Override
    public SearchParameters getSearchParameters()
    {
        return lqp.getSearchParameters();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor#getSortField(java.lang.String)
     */
    @Override
    public String getSortField(String field) throws ParseException
    {
        
      Locale sortLocale;
      List<Locale> locales = lqp.getSearchParameters().getLocales();
      if (((locales == null) || (locales.size() == 0)))
      {
          locales = Collections.singletonList(I18NUtil.getLocale());
      }

      if (locales.size() > 1)
      {
          throw new ParseException("Order on text/mltext properties with more than one locale is not curently supported");
      }

      sortLocale = locales.get(0);
      // find best field match

      HashSet<String> allowableLocales = new HashSet<String>();
      MLAnalysisMode analysisMode = lqp.getDefaultSearchMLAnalysisMode();
      for (Locale l : MLAnalysisMode.getLocales(analysisMode, sortLocale, false))
      {
          allowableLocales.add(l.toString());
      }

      String sortField = field;

      for (Object current : lqp.getIndexReader().getFieldNames(FieldOption.INDEXED))
      {
          String currentString = (String) current;
          if (currentString.startsWith(field) && currentString.endsWith(QueryConstants.FIELD_SORT_SUFFIX))
          {
              String fieldLocale = currentString.substring(field.length() + 1, currentString.length() - QueryConstants.FIELD_SORT_SUFFIX.length());
              if (allowableLocales.contains(fieldLocale))
              {
                  if (fieldLocale.equals(sortLocale.toString()))
                  {
                      sortField = currentString;
                      break;
                  }
                  else if (sortLocale.toString().startsWith(fieldLocale))
                  {
                      if (sortField.equals(field) || (currentString.length() < sortField.length()))
                      {
                          sortField = currentString;
                      }
                  }
                  else if (fieldLocale.startsWith(sortLocale.toString()))
                  {
                      if (sortField.equals(field) || (currentString.length() < sortField.length()))
                      {
                          sortField = currentString;
                      }
                  }
              }
          }
      }

      field = sortField;
      return field;
        
        
        
        
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor#getIdentifierQuery(java.lang.String, java.lang.String, org.alfresco.repo.search.impl.lucene.AnalysisMode, org.alfresco.repo.search.impl.lucene.LuceneFunction)
     */
    @Override
    public Query getIdentifierQuery(String field, String stringValue, AnalysisMode identifier, LuceneFunction luceneFunction) throws ParseException
    {
      String[] split = stringValue.split(";");
      if(split.length == 1)
      {
          return lqp.getFieldQuery(field, stringValue, AnalysisMode.IDENTIFIER, luceneFunction);
      }
      else
      {
          if(split[1].equalsIgnoreCase("PWC"))
          {
              return getMatchNoneQuery();
          }
          
          BooleanQuery query = new BooleanQuery();
          BooleanQuery part1 = new BooleanQuery();
          part1.add(lqp.getFieldQuery(field, split[0], AnalysisMode.IDENTIFIER, luceneFunction), Occur.MUST);
          part1.add(lqp.getFieldQuery("@"+ContentModel.PROP_VERSION_LABEL.toString(), split[1], AnalysisMode.IDENTIFIER, luceneFunction), Occur.MUST);
          query.add(part1, Occur.SHOULD);
          
          if(split[1].equals("1.0"))
          {
              BooleanQuery part2 = new BooleanQuery();
              part2.add(lqp.getFieldQuery(field, split[0], AnalysisMode.IDENTIFIER, luceneFunction), Occur.MUST);
              part2.add(lqp.getFieldQuery(QueryConstants.FIELD_ASPECT, ContentModel.ASPECT_VERSIONABLE.toString(), AnalysisMode.IDENTIFIER, luceneFunction), Occur.MUST_NOT);
              query.add(part2, Occur.SHOULD);
          }
          return query;
      }
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor#getIdentifieLikeQuery(java.lang.String, java.lang.String, org.alfresco.repo.search.impl.lucene.AnalysisMode)
     */
    @Override
    public Query getIdentifieLikeQuery(String field, String sqlLikeClause, AnalysisMode analysisMode) throws ParseException
    {
        return getLikeQuery(field, sqlLikeClause, analysisMode);
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor#sortFieldExists(java.lang.String)
     */
    @Override
    public boolean sortFieldExists(String noLocalField)
    {
        for (Object current : lqp.getIndexReader().getFieldNames(FieldOption.INDEXED))
        {
            String currentString = (String) current;
            if (currentString.equals(noLocalField))
            {
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor#getFieldQuery(java.lang.String, java.lang.String)
     */
    @Override
    public Query getFieldQuery(String field, String queryText) throws ParseException
    {
        return lqp.getFieldQuery(field, queryText);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor#buildSort()
     */
    @Override
    public Sort buildSort(List<Ordering> orderings, FunctionEvaluationContext functionContext) throws ParseException
    {
        int index = 0;
        SortField[] fields = new SortField[orderings.size()];

        for (Ordering ordering : orderings)
        {
            if (ordering.getColumn().getFunction().getName().equals(PropertyAccessor.NAME))
            {
                PropertyArgument property = (PropertyArgument) ordering.getColumn().getFunctionArguments().get(PropertyAccessor.ARG_PROPERTY);

                if (property == null)
                {
                    throw new IllegalStateException();
                }

                String propertyName = property.getPropertyName();

                String luceneField = functionContext.getLuceneSortField(this, propertyName);

                if (luceneField != null)
                {
                    if (LuceneUtils.fieldHasTerm(lqp.getIndexReader(), luceneField))
                    {
                        Locale locale = this.getSearchParameters().getSortLocale();
                        fields[index++] = new SortField(luceneField, locale, (ordering.getOrder() == Order.DESCENDING));
                    }
                    else
                    {
                        fields[index++] = new SortField(null, SortField.DOC, (ordering.getOrder() == Order.DESCENDING));
                    }
                }
                else
                {
                    throw new IllegalStateException();
                }
            }
            else if (ordering.getColumn().getFunction().getName().equals(Score.NAME))
            {
                fields[index++] = new SortField(null, SortField.SCORE, !(ordering.getOrder() == Order.DESCENDING));
            }
            else
            {
                throw new IllegalStateException();
            }

        }

        return new Sort(fields);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor#getFuzzyQuery(java.lang.String, java.lang.String, java.lang.Float)
     */
    @Override
    public Query getFuzzyQuery(String luceneFieldName, String term, Float minSimilarity) throws ParseException
    {
        return lqp.getFuzzyQuery(luceneFieldName, term, minSimilarity);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor#getField()
     */
    @Override
    public String getField()
    {
        return lqp.getField();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor#getPhraseSlop()
     */
    @Override
    public int getPhraseSlop()
    {
        return lqp.getPhraseSlop();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor#getFieldQuery(java.lang.String, java.lang.String, org.alfresco.repo.search.impl.lucene.AnalysisMode, java.lang.Integer, org.alfresco.repo.search.impl.lucene.LuceneFunction)
     */
    @Override
    public Query getFieldQuery(String luceneFieldName, String queryText, AnalysisMode analysisMode, Integer slop, LuceneFunction luceneFunction) throws ParseException
    {
        return lqp.getFieldQuery(luceneFieldName, queryText, analysisMode, slop, luceneFunction);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor#getPrefixQuery(java.lang.String, java.lang.String, org.alfresco.repo.search.impl.lucene.AnalysisMode)
     */
    @Override
    public Query getPrefixQuery(String luceneFieldName, String term, AnalysisMode analysisMode) throws ParseException
    {
        return lqp.getPrefixQuery(luceneFieldName, term, analysisMode);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor#getSpanQuery(java.lang.String, java.lang.String, java.lang.String, int, boolean)
     */
    @Override
    public Query getSpanQuery(String field, String first, String last, int slop, boolean inOrder) throws ParseException
    {
        return lqp.getSpanQuery(field, first, last, slop, inOrder);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor#getWildcardQuery(java.lang.String, java.lang.String, org.alfresco.repo.search.impl.lucene.AnalysisMode)
     */
    @Override
    public Query getWildcardQuery(String luceneFieldName, String term, AnalysisMode analysisMode) throws ParseException
    {
        return lqp.getWildcardQuery(luceneFieldName, term, analysisMode);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor#getNegatedQuery(java.lang.Object)
     */
    @Override
    public Query getNegatedQuery(Query query) throws ParseException
    {
        LuceneQueryParserExpressionAdaptor<Query, ParseException> expressionAdaptor = getExpressionAdaptor();
        expressionAdaptor.addRequired(getMatchAllQuery());
        expressionAdaptor.addExcluded(query);
        return expressionAdaptor.getQuery();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor#getExpressionAdaptor()
     */
    @Override
    public LuceneQueryParserExpressionAdaptor<Query, ParseException> getExpressionAdaptor()
    {
        return new LegacyLuceneQueryParserExpressionAdaptor();
    }

    
    private class LegacyLuceneQueryParserExpressionAdaptor implements LuceneQueryParserExpressionAdaptor<Query, ParseException>
    {
        BooleanQuery booleanQuery = new BooleanQuery();

        /* (non-Javadoc)
         * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserExpressionAdaptor#addRequired(java.lang.Object)
         */
        @Override
        public void addRequired(Query q)
        {
            booleanQuery.add(q, Occur.MUST);
        }

        /* (non-Javadoc)
         * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserExpressionAdaptor#addExcluded(java.lang.Object)
         */
        @Override
        public void addExcluded(Query q)
        {
            booleanQuery.add(q, Occur.MUST_NOT);
        }

        /* (non-Javadoc)
         * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserExpressionAdaptor#addOptoinal(java.lang.Object)
         */
        @Override
        public void addOptional(Query q)
        {
            booleanQuery.add(q, Occur.SHOULD);
        }

        /* (non-Javadoc)
         * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserExpressionAdaptor#getQuery()
         */
        @Override
        public Query getQuery()  throws ParseException
        {
            if(booleanQuery.getClauses().length == 0)
            {
                return getMatchNoneQuery();
            }
            else if (booleanQuery.getClauses().length == 1)
            {
                BooleanClause clause = booleanQuery.getClauses()[0];
                if(clause.isProhibited())
                {
                    booleanQuery.add(getMatchAllQuery(), Occur.MUST);
                    return booleanQuery;
                }
                else
                {
                    return clause.getQuery();
                }
            }
            else
            {
                return booleanQuery;
            }
        }
        
        public Query getNegatedQuery() throws ParseException
        {
            if(booleanQuery.getClauses().length == 0)
            {
                return getMatchAllQuery();
            }
            else if (booleanQuery.getClauses().length == 1)
            {
                BooleanClause clause = booleanQuery.getClauses()[0];
                if(clause.isProhibited())
                {
                    return clause.getQuery();
                }
                else
                {
                    return LegacyLuceneQueryParserAdaptor.this.getNegatedQuery(getQuery());
                }
            }
            else
            {
                return LegacyLuceneQueryParserAdaptor.this.getNegatedQuery(getQuery());
            }
        }

        /* (non-Javadoc)
         * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserExpressionAdaptor#addRequired(java.lang.Object, float)
         */
        @Override
        public void addRequired(Query q, float boost) throws ParseException
        {
            q.setBoost(boost);
            addRequired(q);
        }

        /* (non-Javadoc)
         * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserExpressionAdaptor#addExcluded(java.lang.Object, float)
         */
        @Override
        public void addExcluded(Query q, float boost) throws ParseException
        {
            q.setBoost(boost);
            addExcluded(q);
        }

        /* (non-Javadoc)
         * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserExpressionAdaptor#addOptional(java.lang.Object, float)
         */
        @Override
        public void addOptional(Query q, float boost) throws ParseException
        {
            q.setBoost(boost);
            addOptional(q);
            
        }
        
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor#getMatchAllNodesQuery()
     */
    @Override
    public Query getMatchAllNodesQuery()
    {
        return new TermQuery(new Term("ISNODE", "T"));
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#getDatetimeSortField(org.alfresco.service.cmr.dictionary.PropertyDefinition)
     */
    @Override
    public String getDatetimeSortField(String field, PropertyDefinition propertyDef)
    {
        String analyserClassName = propertyDef.resolveAnalyserClassName();
        if (analyserClassName.equals(DateTimeAnalyser.class.getCanonicalName()))
        {
            field = field + QueryConstants.FIELD_SORT_SUFFIX;
        }
        return field;
    }
}
