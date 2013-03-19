package org.alfresco.opencmis.dictionary;

import java.util.Collection;

import org.alfresco.repo.dictionary.DictionaryComponent;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.namespace.QName;

/**
 * A DictionaryComponent that uses a QNameFilter to constrain what is returned.
 *
 * @author Gethin James
 */
public class FilteredDictionaryComponent extends DictionaryComponent
{
    QNameFilter filter;

    @Override
    public Collection<QName> getAllTypes()
    {
        return filter.filterQName(super.getAllTypes());
    }

    @Override
    public Collection<QName> getSubTypes(QName superType, boolean follow)
    {
        return filter.filterQName(super.getSubTypes(superType, follow));
    }

    @Override
    public Collection<QName> getAllAspects()
    {
        return filter.filterQName(super.getAllAspects());
    }

    @Override
    public Collection<QName> getAllAssociations()
    {
        return filter.filterQName(super.getAllAssociations());
    }

    @Override
    public Collection<QName> getSubAspects(QName superAspect, boolean follow)
    {
        return filter.filterQName(super.getSubAspects(superAspect, follow));
    }

    @Override
    public TypeDefinition getType(QName name)
    {
        if (filter.isExcluded(name)) return null;  //Don't return an excluded type
        return super.getType(name);
    }
    
    @Override
    public AspectDefinition getAspect(QName name)
    {
        if (filter.isExcluded(name)) return null;  //Don't return an excluded type
        return super.getAspect(name);
    }
    
    public void setFilter(QNameFilter filter)
    {
        this.filter = filter;
    }

}
