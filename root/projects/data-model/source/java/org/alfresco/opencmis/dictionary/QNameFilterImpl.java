package org.alfresco.opencmis.dictionary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.service.namespace.QName;

/**
 * Filters a QName and excludes any
 * that are defined using the excludedTypes parameter.
 * The list of types can either be defined using a property name such as "cm:name" or with a wildcard: "cm:*"
 * It validates the definitions against the DataDictionary.
 *
 * @author Gethin James
 */
public class QNameFilterImpl implements QNameFilter
{

    private Set<QName> excludedQNames;
    private Set<String> excludedModels;
    
    private List<String> excludedTypes;
//    private DictionaryService dictionaryService;
//    private NamespacePrefixResolver namespacePrefixResolver;
    
    /**
     * Filters out any QName defined in the "excludedModels" property
     * 
     * @param typesToFilter - original list
     * @return the filtered list
     */
    public Collection<QName> filterQName(Collection<QName> typesToFilter)
    {
        Collection<QName> filteredTypes = new ArrayList<QName>();
        if (!excludedQNames.isEmpty() || !excludedModels.isEmpty())
        {
            //If we have a exclusion list then loop through and exclude models /types
            //that are in this list.
            for (QName classQName : typesToFilter)
            {
                if (!excludedQNames.contains(classQName) && !excludedModels.contains(classQName.getNamespaceURI()))
                {    
                  //Not excluded so add it
                  filteredTypes.add(classQName); 
                }
            }
        }
        else
        {
            filteredTypes = typesToFilter;
        }
        return filteredTypes;
    }
    
    
    /**
     * Processes the user-defined list of types into valid QNames & models, it validates them
     * against the dictionary and also supports wildcards
     * @param excludeTypeNames
     * @return Set<QName> Valid type QNames
     */
    protected void preprocessExcludedTypes(List<String> excludeTypeNames)
    {
        if (excludeTypeNames == null || excludeTypeNames.isEmpty()) return;

        Set<QName> qNamesToExclude = new HashSet<QName>(excludeTypeNames.size());
        Set<String> modelsToExclude = new HashSet<String>();
        
        for (String typeDefinition : excludeTypeNames)
        {
            final QName typeDef = QName.createQName(typeDefinition);
            if (WILDCARD.equals(typeDef.getLocalName()))
            {
//                Collection<String> prefixes = getNamespaceService().getPrefixes(typeDef.getNamespaceURI());
//                if (prefixes == null || prefixes.isEmpty()) {
//                    throw new InvalidQNameException(
//                                "Invalid Qname wildcard, namespace URI not found for : " + typeDef);             
//                }
//                else
//                {
                   modelsToExclude.add(typeDef.getNamespaceURI());
 //               }
            }
            else
            {
//                TypeDefinition typeD = getDictionaryService().getType(typeDef);
//                if (typeD != null)
//                {
                    qNamesToExclude.add(typeDef); // valid so add it to the list
//                }
//                else
//                {
//                    throw new InvalidQNameException(
//                                "Invalid Qname, not found in the data dictionary : " + typeDef);
//                }

            }

        }
        
        this.excludedModels = modelsToExclude;
        this.excludedQNames = qNamesToExclude;
    }

    /**
     * Indicates that this QName should be excluded.
     * @param typeQName
     * @return boolean true if it is excluded
     */
    public boolean isExcluded(QName typeQName)
    {
        return (excludedQNames.contains(typeQName)  || excludedModels.contains(typeQName.getNamespaceURI()));
    }
    
    @Override
    public void initFilter()
    {
        if (excludedTypes == null || excludedTypes.isEmpty())
        {
            excludedTypes = listOfHardCodedExcludedTypes(); 
        }
        preprocessExcludedTypes(excludedTypes);
    }

    public void setExcludedTypes(List<String> excludedTypes)
    {
        this.excludedTypes = excludedTypes;
    }
//
//    public void setDictionaryService(DictionaryService dictionaryService)
//    {
//        this.dictionaryService = dictionaryService;
//    }
//
//    public void setNamespacePrefixResolver(NamespacePrefixResolver namespacePrefixResolver)
//    {
//        this.namespacePrefixResolver = namespacePrefixResolver;
//    }
//    
    /**
     * I don't like hard code values, but have been persuaded its less pain
     * than having to keep a config file in sync with both sides of SOLR.
     * 
     * @return
     */
    public static List<String> listOfHardCodedExcludedTypes() {
        List<String> hardCodeListOfTypes = new ArrayList<String>();
        
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/imap/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/publishing/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/publishingworkflow/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/publishing/twitter/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/publishing/slideshare/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/publishing/facebook/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/publishing/youtube/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/publishing/linkedin/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/publishing/flickr/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/transfer/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/wcmappmodel/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/wcmmodel/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/emailserver/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/calendar}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/blogintegration/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/linksmodel/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/datalist/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/forum/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/wcmworkflow/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/cloud/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/bpm/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/workflow/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/workflow/invite/moderated/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/workflow/invite/nominated/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/workflow/cloud/resetpassword/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/workflow/cloud/siteinvitation/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/workflow/signup/selfsignup/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/versionstore/2.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/versionstore/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/action/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/application/1.0}*");
        hardCodeListOfTypes.add("{http://www.jcp.org/jcr/mix/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/rule/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/rendition/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/qshare/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/sync/1.0}*");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/content/1.0}thumbnailed");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/content/1.0}failedThumbnailSource");
        hardCodeListOfTypes.add("{http://www.alfresco.org/model/cmis/custom}*");
        return hardCodeListOfTypes;
    }

}
