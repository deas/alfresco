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
package org.alfresco.module.phpIntegration.lib;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.namespace.InvalidQNameException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;


/**
 * PHP Object extension, namespace map deals with mapping short names to and from their 
 * full name represenation.
 * 
 * @author Roy Wetherall
 */
public class NamespaceMap implements ScriptObject
{
    /** Script extension name */
    private static final String SCRIPT_OBJECT_NAME = "NamespaceMap";
    
    /** Short name delimiter */
    private static final String NAME_DELIMITER = "_";
    
    /** The session */
    private Session session;
    
    /** The namespace service */
    private NamespaceService namespaceService;
    
    /** Map containing escaped prefixes and full URLS */
    private Map<String, String> prefixToUrlMap;
    private Map<String, String> urlToPrefixMap;
    
    /**
     * Constructor
     * 
     * @param session   the session
     */
    public NamespaceMap(Session session)
    {
        this.session = session;
        
        // Build the maps for prefixes and full urls
        this.namespaceService = this.session.getServiceRegistry().getNamespaceService();
        Collection<String> prefixes = this.namespaceService.getPrefixes();
        this.prefixToUrlMap = new HashMap<String, String>(prefixes.size());
        this.urlToPrefixMap = new HashMap<String, String>(prefixes.size());
        for (String prefix : prefixes)
        {
            //String escapedPrefix = escapePrefix(prefix);
            String url = this.namespaceService.getNamespaceURI(prefix);
            this.prefixToUrlMap.put(prefix, url);
            this.urlToPrefixMap.put(url, prefix);           
        }
    }
    
    /**
     * @see org.alfresco.module.phpIntegration.lib.ScriptObject#getScriptObjectName()
     */
    public String getScriptObjectName()
    {
        return SCRIPT_OBJECT_NAME;
    }
    
    /**
     * Indicates whether the passed string is indeed a valid short name
     * 
     * @param shortName     the short name
     * @return boolean      true if the passed string is a valid short name, false otherwise
     */
    public boolean isShortName(String shortName)
    {
        boolean result = false;
        
        String prefix = getPrefixFromShort(shortName);
        if (prefix != null)
        {
            String url = this.prefixToUrlMap.get(prefix);
            String name = getNameFromShort(shortName);
            if (url != null && name != null && name.length() != 0)
            {
                result = true;
            }
        }
            
        return result;
    }

    /**
     * Returns the full name for a given short string.  Returns the origional short name if it is not valid
     * full name is found.
     * 
     * @param shortName     the short name
     * @return String       the full name
     */
    public String getFullName(String shortName)
    {
        String result = shortName;
        
        String prefix = getPrefixFromShort(shortName);
        if (prefix != null)
        {
            String url = this.prefixToUrlMap.get(prefix);
            String name = getNameFromShort(shortName);
            if (url != null && name != null && name.length() != 0)
            {
                name = name.replace('_', '-');
                result = "{" + url + "}" + name;
            }
        }
            
        return result;
    }
    
    /**
     * Returns the short name for a full name.  Returns the oridional full name is it is not valid.
     * 
     * @param fullName  the full name
     * @return String   the short name
     */
    public String getShortName(String fullName)
    {
        String result = fullName;
        
        try
        {
            QName fullQName = QName.createQName(fullName);
            String url = fullQName.getNamespaceURI();
            if (url != null && url.length() != 0)
            {
                String prefix = this.urlToPrefixMap.get(url);
                String name = fullQName.getLocalName();
                if (prefix != null && name != null && name.length() != 0)
                {
                    name = name.replace('-', '_');
                    result = prefix + NAME_DELIMITER + name;
                }
            }
        }
        catch (InvalidQNameException exception)
        {
            // Ignore and return the full name
        }
        
        return result;
    }
    
    /**
     * Gets the prefix from a short name
     * 
     * @param shortName     the short name
     * @return String       the prefix, null if none found
     */
    private String getPrefixFromShort(String shortName)
    {
        String result = null;
        int index = shortName.indexOf(NAME_DELIMITER);
        if (index > 0)
        {
            result = shortName.substring(0, index);
        }
        return result;
    }
    
    /**
     * Get the name from a short name
     * 
     * @param shortName     the short name
     * @return String       the name part
     */
    private String getNameFromShort(String shortName)
    {
        String result = null;
        int index = shortName.indexOf(NAME_DELIMITER);
        if (index > 0)
        {
            result = shortName.substring(index+1);
        }
        return result;
    }
    
}
