package org.alfresco.opencmis.mapping;

import org.alfresco.opencmis.dictionary.QNameFilter;
import org.alfresco.service.namespace.QName;

/**
 * CMIS <-> Alfresco mappings, it additionally excludes a list of QNames based
 * on a user defined list
 * 
 * @author Gethin James
 */
public class CMISMappingWithExclusions extends CMISMapping
{
    
    QNameFilter filter;
    
    /**
     * If its not excluded then call super() method
     */
    @Override
    public boolean isValidCmisDocument(QName typeQName)
    {
        if (typeQName == null || filter.isExcluded(typeQName))
        {
            return false;
        }
        return super.isValidCmisDocument(typeQName);
    }

    /**
     * If its not excluded then call super() method
     */
    @Override
    public boolean isValidCmisFolder(QName typeQName)
    {
        if (typeQName == null || filter.isExcluded(typeQName))
        {
            return false;
        }
        return super.isValidCmisFolder(typeQName);
    }

    /**
     * If its not excluded then call super() method
     */
    @Override
    public boolean isValidCmisPolicy(QName typeQName)
    {
        if (typeQName == null || filter.isExcluded(typeQName))
        {
            return false;
        }
        return super.isValidCmisPolicy(typeQName);
    }

    /**
     * If its not excluded then call super() method
     */
    @Override
    public boolean isValidCmisRelationship(QName associationQName)
    {
        if (associationQName == null || filter.isExcluded(associationQName))
        {
            return false;
        }
        return super.isValidCmisRelationship(associationQName);
    }

    public void setFilter(QNameFilter filter)
    {
        this.filter = filter;
    }
    
}
