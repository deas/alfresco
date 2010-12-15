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

import org.alfresco.util.EqualsHelper;

/**
 * Association object.
 * 
 * @author Roy Wetherall
 */
public class Association implements ScriptObject
{
    /** The name of the script extension */
    private static final String SCRIPT_OBJECT_NAME = "Association";
    
    /** The from node */
    private Node from;
    
    /** The to node */
    private Node to;
    
    /** The type of the association */
    private String type;
    
    /**
     * Constructor
     * 
     * @param from  the from node
     * @param to    the to node
     * @param type  the association type
     */
    public Association(Node from, Node to, String type)
    {
        this.from = from;
        this.to = to;
        this.type = type;
    }
    
    /**
     * @see org.alfresco.module.phpIntegration.lib.ScriptObject#getScriptObjectName()
     */
    public String getScriptObjectName()
    {
        return SCRIPT_OBJECT_NAME;
    }
    
    /**
     * The from node
     * 
     * @return  Node    the from node
     */
    public Node getFrom()
    {
        return from;
    }
    
    /**
     * The to node
     * 
     * @return  Node    the to node
     */
    public Node getTo()
    {
        return to;
    }
    
    /**
     * The type of the association
     * 
     * @return  String  the type of the association
     */
    public String getType()
    {
        return type;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Association))
        {
            return false;
        }
        Association other = (Association) o;

        return (EqualsHelper.nullSafeEquals(this.type, other.type)
                && EqualsHelper.nullSafeEquals(this.from, other.from)
                && EqualsHelper.nullSafeEquals(this.to, other.to));
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        int hashCode = ((this.type == null) ? 0 : this.type.hashCode());
        hashCode = 37 * hashCode + ((this.from == null) ? 0 : this.from.hashCode());
        hashCode = 37 * hashCode + ((this.to == null) ? 0 : this.to.hashCode());
        return hashCode;
    }
}
