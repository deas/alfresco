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
package org.alfresco.util;

import java.io.Serializable;
import java.util.List;

/**
 * A composite name matcher that matches if any of its member
 * matchers match.
 * @author britt
 */
public class OrCompositeNameMatcher implements NameMatcher, Serializable
{
    private static final long serialVersionUID = 8751285104404230814L;

    /**
     * The NameMatchers this is composed of.
     */
    List<NameMatcher> fMatchers;
    
    /**
     * Default constructor.
     */
    public OrCompositeNameMatcher()
    {
    }

    public void setMatchers(List<NameMatcher> matchers)
    {
        fMatchers = matchers;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.util.NameMatcher#matches(java.lang.String)
     */
    public boolean matches(String name) 
    {
        for (NameMatcher matcher : fMatchers)
        {
            if (matcher.matches(name))
            {
                return true;
            }
        }
        return false;
    }
}
