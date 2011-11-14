/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.util.schemacomp.model;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.util.schemacomp.DbObjectVisitor;
import org.alfresco.util.schemacomp.DbProperty;
import org.alfresco.util.schemacomp.DiffContext;
import org.alfresco.util.schemacomp.Result.Strength;

/**
 * Represents an index on a table.
 * 
 * @author Matt Ward
 */
public class Index extends AbstractDbObject
{
    private final List<String> columnNames = new ArrayList<String>();
    private boolean unique;
    
    public Index(String name)
    {
        super(null, name);
    }
    
    /**
     * @param table the parent table
     * @param columnNames
     */
    public Index(Table table, String name, List<String> columnNames)
    {
        super(table, name);
        this.columnNames.addAll(columnNames);
        setNameStrength(Strength.WARN);
    }

    /**
     * @return the columnNames
     */
    public List<String> getColumnNames()
    {
        return this.columnNames;
    }

    /**
     * @param columnNames the columnNames to set
     */
    public void setColumnNames(List<String> columnNames)
    {
        this.columnNames.clear();
        this.columnNames.addAll(columnNames);
    }

    /**
     * Does this index have the unique attribute?
     * 
     * @return unique
     */
    public boolean isUnique()
    {
        return this.unique;
    }

    /**
     * @see #isUnique()
     * @param unique
     */
    public void setUnique(boolean unique)
    {
        this.unique = unique;
    }


    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.columnNames == null) ? 0 : this.columnNames.hashCode());
        result = prime * result + (this.unique ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        Index other = (Index) obj;
        if (this.columnNames == null)
        {
            if (other.columnNames != null) return false;
        }
        else if (!this.columnNames.equals(other.columnNames)) return false;
        if (this.unique != other.unique) return false;
        return true;
    }

    @Override
    public boolean sameAs(DbObject o)
    {
        if (o != null && o instanceof Index)
        {
            Index other = (Index) o;
            
            // An index can only be 'the same' if it belongs to the correct table.
            if (!getParent().sameAs(other.getParent()))
            {
                return false;
            }
            
            // If it has the same name, then it is intended to be the same index
            if (getName() != null && getName().equals(other.getName()))
            {
                return true;
            }
            else
            {
                // The name may be different, but if it has the same parent table (see above)
                // and indexes the same columns, then it is the same index.
                return columnNames.equals(other.getColumnNames());
            }
        
        }
        
        return false;
    }

    
    @Override
    protected void doDiff(DbObject right, DiffContext ctx, Strength strength)
    {
        Index rightIndex = (Index) right;
        comparisonUtils.compareSimpleCollections(
                    new DbProperty(this, "columnNames"),
                    new DbProperty(rightIndex, "columnNames"),
                    ctx,
                    strength);
        comparisonUtils.compareSimple(
                    new DbProperty(this, "unique"),
                    new DbProperty(rightIndex, "unique"),
                    ctx);
    }

    
    public void validate(DiffContext ctx)
    {
        System.out.println("INVALID INDEX, name is not allowed:" + getName());
    }
    
    
    @Override
    public void accept(DbObjectVisitor visitor)
    {
        visitor.visit(this);
    }
}
