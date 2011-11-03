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

package org.alfresco.module.vti.web;

import java.util.Map;

/**
 * @author Nick Smith
 * @since 4.0
 *
 */
public class VtiUtilBase
{

    private static final StringBuilder LBRACKET = new StringBuilder("<");
    private static final StringBuilder RBRACKET = new StringBuilder(">");
    private static final StringBuilder LCLOSEBRACKET = new StringBuilder("</");
    private static final StringBuilder RCLOSEBRACKET = new StringBuilder("/>");

    /**
     * Create xml tag presentation  
     * 
     * @param tagName name of tag      
     */
    protected StringBuilder startTag(String tagName)
    {
        StringBuilder result = new StringBuilder("");
        return result.append(LBRACKET).append(tagName).append(RBRACKET);
    }

    /**
     * Create xml tag presentation with attributes
     * 
     * @param tagName name of tag
     * @param attributes map or the attributes for the tag     
     */
    protected StringBuilder startTag(String tagName, Map<String, Object> attributes)
    {
        StringBuilder result = new StringBuilder("");
        result.append(LBRACKET).append(tagName).append(" ");
        for (String key : attributes.keySet())
        {
            if (attributes.get(key) != null)
            {
                if (!attributes.get(key).equals(""))
                {
                    result.append(key).append("=\"").append(attributes.get(key)).append("\" ");
                }
            }
        }
        result.append(RBRACKET);
        return result;
    }

    /**
     * Creates xml closing tag presentation
     * 
     * @param tagName name of the closing tag    
     */
    protected StringBuilder endTag(String tagName)
    {
        StringBuilder result = new StringBuilder("");
        return result.append(LCLOSEBRACKET).append(tagName).append(RBRACKET);
    }

    /**
     * Creates xml tag presentation without body
     * 
     * @param tagName name of tag
     */
    protected StringBuilder singleTag(String tagName)
    {
        StringBuilder result = new StringBuilder("");
        return result.append(LBRACKET).append(tagName).append(RCLOSEBRACKET);
    }

    /**
     * Creates xml tag with attributes presentation without body
     * 
     * @param tagName name of tag
     * @param attributes map of tag attributes
     */
    protected StringBuilder singleTag(String tagName, Map<String, Object> attributes)
    {
        StringBuilder result = new StringBuilder("");
        result.append(LBRACKET).append(tagName).append(" ");
        for (String key : attributes.keySet())
        {
            if (attributes.get(key) != null)
            {
                if (!attributes.get(key).equals(""))
                {
                    result.append(key).append("=\"").append(attributes.get(key)).append("\" ");
                }
            }
        }
        result.append(RCLOSEBRACKET);
        return result;
    }

    /**
     * Creates xml tag presentation with body that contain <code>value</code> parameter
     * 
     * @param tagName name of tag
     * @param value that will be placed to the body of the tag
     */
    protected StringBuilder processTag(String tagName, Object value)
    {
        StringBuilder result = new StringBuilder("");
    
        if (value == null)
        {
            return result;
        }
        else if (value.toString().equals(""))
        {
            return result.append(singleTag(tagName));
        }
        else
        {
            return result.append(startTag(tagName)).append(value).append(endTag(tagName));
        }
    }

}
