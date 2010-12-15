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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * A name matcher that matches any of a list of regular expressions.
 * @author britt
 */
public class RegexNameMatcher implements NameMatcher, Serializable
{
    private static final long serialVersionUID = 2686220370729761489L;

    /**
     * The regular expressions that can match.
     */
    private List<Pattern> fPatterns;

    /**
     * Default constructor.
     */
    public RegexNameMatcher()
    {
        fPatterns = new ArrayList<Pattern>();
    }
    
    /**
     * Set the patterns.  
     * @param patterns
     */
    public void setPatterns(List<String> patterns)
    {
        for (String regex : patterns)
        {
            fPatterns.add(Pattern.compile(regex));
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.util.NameMatcher#matches(java.lang.String)
     */
    public boolean matches(String name) 
    {
        for (Pattern pattern : fPatterns)
        {
            if (pattern.matcher(name).matches())
            {
                return true;
            }
        }
        return false;
    }
}
