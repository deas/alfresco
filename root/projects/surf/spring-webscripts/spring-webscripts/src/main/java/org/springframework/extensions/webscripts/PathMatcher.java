/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.webscripts;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

/**
 * Custom Spring MVC PathMatcher class - extends the AntPathMatcher to fix
 * undesirable features with URI path tokenization. The AntPathMatcher breaks the
 * path down into tokens and then performs a trim() on each. But it is perfectly
 * legal to expect a decoded path such as "/abc/ something /foldername".
 * 
 * @author Kevin Roast
 */
public class PathMatcher extends AntPathMatcher
{
    /* (non-Javadoc)
     * @see org.springframework.util.AntPathMatcher#extractPathWithinPattern(java.lang.String, java.lang.String)
     */
    @Override
    public String extractPathWithinPattern(String pattern, String path)
    {
        String[] patternParts = StringUtils.tokenizeToStringArray(pattern, DEFAULT_PATH_SEPARATOR);
        String[] pathParts = StringUtils.tokenizeToStringArray(path, DEFAULT_PATH_SEPARATOR, false, false);
        
        StringBuilder builder = new StringBuilder(128);
        
        // Add any path parts that have a wildcarded pattern part.
        int puts = 0;
        for (int i = 0; i < patternParts.length; i++)
        {
            String patternPart = patternParts[i];
            if ((patternPart.indexOf('*') > -1 || patternPart.indexOf('?') > -1) && pathParts.length >= i + 1)
            {
                if (puts > 0 || (i == 0 && !pattern.startsWith(DEFAULT_PATH_SEPARATOR)))
                {
                    builder.append(DEFAULT_PATH_SEPARATOR);
                }
                builder.append(pathParts[i]);
                puts++;
            }
        }
        
        // Append any trailing path parts.
        for (int i = patternParts.length; i < pathParts.length; i++)
        {
            if (puts > 0 || i > 0)
            {
                builder.append(DEFAULT_PATH_SEPARATOR);
            }
            builder.append(pathParts[i]);
        }
        
        return builder.toString();
    }
}
