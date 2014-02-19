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
package org.alfresco.share.site.document;

import java.util.Set;

import org.alfresco.po.share.site.document.DocumentAspect;

/**
 * The Property holds information to be compared during the add & remove aspects.
 * @author Shan Nagarajan
 * @since  1.1
 */
public class AspectTestProptery
{

    private String testName;
    
    private DocumentAspect aspect;
    
    private int sizeBeforeAspectAdded;
    
    private int sizeAfterAspectAdded;
    
    private Set<String> expectedProprtyKey;

    public String getTestName()
    {
        return testName;
    }

    public void setTestName(String testName)
    {
        this.testName = testName;
    }

    public DocumentAspect getAspect()
    {
        return aspect;
    }

    public void setAspect(DocumentAspect aspect)
    {
        this.aspect = aspect;
    }

    public int getSizeBeforeAspectAdded()
    {
        return sizeBeforeAspectAdded;
    }

    public void setSizeBeforeAspectAdded(int sizeBeforeAspectAdded)
    {
        this.sizeBeforeAspectAdded = sizeBeforeAspectAdded;
    }

    public int getSizeAfterAspectAdded()
    {
        return sizeAfterAspectAdded;
    }

    public void setSizeAfterAspectAdded(int sizeAfterAspectAdded)
    {
        this.sizeAfterAspectAdded = sizeAfterAspectAdded;
    }

    public Set<String> getExpectedProprtyKey()
    {
        return expectedProprtyKey;
    }

    public void setExpectedProprtyKey(Set<String> expectedProprtyKey)
    {
        this.expectedProprtyKey = expectedProprtyKey;
    }
    
}
