/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.repo.search.impl.lucene;

/**
 * @author Andy
 *
 */
public interface LuceneQueryParserExpressionAdaptor<Q, E extends Throwable>
{
    public void addRequired(Q q) throws E;
    public void addExcluded(Q q) throws E;
    public void addOptional(Q q) throws E;
    public void addRequired(Q q, float boost) throws E;
    public void addExcluded(Q q, float boost) throws E;
    public void addOptional(Q q, float boost) throws E;
    public Q getQuery() throws E;
    public Q getNegatedQuery() throws E;
}
