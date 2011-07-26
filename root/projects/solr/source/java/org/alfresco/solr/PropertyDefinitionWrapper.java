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

import java.util.List;
import java.util.Locale;

import org.alfresco.repo.dictionary.IndexTokenisationMode;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.ModelDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.namespace.QName;

/**
 * @author Andy
 *
 */
public class PropertyDefinitionWrapper implements PropertyDefinition
{
    PropertyDefinition delegate;
    
    PropertyDefinitionWrapper(PropertyDefinition delegate)
    {
        this.delegate = delegate;
    }

    public ModelDefinition getModel()
    {
        return delegate.getModel();
    }

    public QName getName()
    {
        return delegate.getName();
    }

    public String getTitle()
    {
        return delegate.getTitle();
    }

    public String getDescription()
    {
        return delegate.getDescription();
    }

    public String getDefaultValue()
    {
        return delegate.getDefaultValue();
    }

    public DataTypeDefinition getDataType()
    {
        return delegate.getDataType();
    }

    public ClassDefinition getContainerClass()
    {
        return delegate.getContainerClass();
    }

    public boolean isOverride()
    {
        return delegate.isOverride();
    }

    public boolean isMultiValued()
    {
        return delegate.isMultiValued();
    }

    public boolean isMandatory()
    {
        return delegate.isMandatory();
    }

    public boolean isMandatoryEnforced()
    {
        return delegate.isMandatoryEnforced();
    }

    public boolean isProtected()
    {
        return delegate.isProtected();
    }

    public boolean isIndexed()
    {
        return delegate.isIndexed();
    }

    public boolean isStoredInIndex()
    {
        return delegate.isStoredInIndex();
    }

    public IndexTokenisationMode getIndexTokenisationMode()
    {
        return IndexTokenisationMode.BOTH;
    }

    public boolean isIndexedAtomically()
    {
        return delegate.isIndexedAtomically();
    }

    public List<ConstraintDefinition> getConstraints()
    {
        return delegate.getConstraints();
    }

    public String getAnalyserResourceBundleName()
    {
        return delegate.getAnalyserResourceBundleName();
    }

    public String resolveAnalyserClassName(Locale locale)
    {
        return delegate.resolveAnalyserClassName(locale);
    }

    public String resolveAnalyserClassName()
    {
        return delegate.resolveAnalyserClassName();
    }

  
}
