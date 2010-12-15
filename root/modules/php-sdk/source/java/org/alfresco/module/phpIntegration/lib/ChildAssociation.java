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
 * Child association object.
 * 
 * @author Roy Wetherall
 */
public class ChildAssociation implements ScriptObject
{
    /** The name of the script extension */
    private static final String SCRIPT_OBJECT_NAME = "ChildAssociation";
    
    /** The parent node */
    private Node parent;
    
    /** The child node */
    private Node child;
    
    /** The type of the child association */
    private String type;
    
    /** The name of the child association */
    private String name;
    
    /** Indicates whether the child association is primary or not */
    private boolean isPrimary;
    
    /** The sibling order */
    private int nthSibling;
    
    /**
     * Constructor
     * 
     * @param parent        the parent node
     * @param child         the child node
     * @param type          the association type
     * @param name          the association name
     * @param isPrimary     indicates whether the association is primary or not
     * @param nthSibling    the sibling oreder
     */
    public ChildAssociation(Node parent, Node child, String type, String name, boolean isPrimary, int nthSibling)
    {
        this.parent = parent;
        this.child = child;
        this.type = type;
        this.name = name;
        this.isPrimary = isPrimary;
        this.nthSibling = nthSibling;
    }
    
    /**
     * @see org.alfresco.module.phpIntegration.lib.ScriptObject#getScriptObjectName()
     */
    public String getScriptObjectName()
    {
        return SCRIPT_OBJECT_NAME;
    }
    
    /**
     * Gets the parent node
     * 
     * @return  Node    the parent node
     */
    public Node getParent()
    {
        return this.parent;
    }
    
    /**
     * Get the child node
     * 
     * @return  Node    the child node
     */
    public Node getChild()
    {
        return this.child;
    }
    
    /**
     * Get the type of the association
     * 
     * @return  Stirng  the type of the association
     */
    public String getType()
    {
        return this.type;
    }
    
    /**
     * Get the name of the association
     * 
     * @return  String  the name of the association
     */
    public String getName()
    {
        return this.name;
    }
    
    /**
     * Indicates whehter the association is primary
     * 
     * @return  boolean     true if assocaition is primary, false otherwise
     */
    public boolean getIsPrimary()
    {
        return this.isPrimary;
    }
    
    /**
     * The sibling order
     * 
     * @return  int     the sibling order
     */
    public int getNthSibling()
    {
        return this.nthSibling;
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
        if (!(o instanceof ChildAssociation))
        {
            return false;
        }
        ChildAssociation other = (ChildAssociation) o;

        return (EqualsHelper.nullSafeEquals(this.type, other.type)
                && EqualsHelper.nullSafeEquals(this.parent, other.parent)
                && EqualsHelper.nullSafeEquals(this.name, other.name)
                && EqualsHelper.nullSafeEquals(this.child, other.child));
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        int hashCode = ((this.type == null) ? 0 : this.type.hashCode());
        hashCode = 37 * hashCode + ((this.parent == null) ? 0 : this.parent.hashCode());
        hashCode = 37 * hashCode + ((this.name == null) ? 0 : this.name.hashCode());
        hashCode = 37 * hashCode + this.child.hashCode();
        return hashCode;
    }
}
