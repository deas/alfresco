package org.alfresco.opencmis.dictionary;

import java.util.Collection;

import org.alfresco.service.namespace.QName;

/**
 * Filters QNames and excludes any
 * that are in a predefined list.
 *
 * @author steveglover
 * @author Gethin James
 */
public interface QNameFilter
{
    public static final String WILDCARD = "*";
    
    /**
     * Filters out any QName defined in the "excludedTypes" property
     * 
     * @param typesToFilter - original list
     * @return the filtered list
     */
    Collection<QName> filterQName(Collection<QName> typesToFilter);
    
    /**
     * Indicates that this QName should be excluded.
     * @param typeQName
     * @return boolean true if it is excluded
     */
    boolean isExcluded(QName typeQName);
    
    /**
     * Exclude the type
     * 
     * @param typeQName the type to exclude
     * 
     */
    void setExcluded(QName typeQName, boolean excluded);
    
    void initFilter();
}
