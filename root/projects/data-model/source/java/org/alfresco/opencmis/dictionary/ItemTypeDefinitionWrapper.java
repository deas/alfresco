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
package org.alfresco.opencmis.dictionary;

import org.alfresco.opencmis.CMISUtils;
import org.alfresco.opencmis.mapping.CMISMapping;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO9075;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ItemTypeDefinitionImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ItemTypeDefinitionWrapper extends ShadowTypeDefinitionWrapper
{
    private static final long serialVersionUID = 1L;

    private ItemTypeDefinitionImpl typeDef;
    private ItemTypeDefinitionImpl typeDefInclProperties;

    private Log logger = LogFactory.getLog(ItemTypeDefinitionWrapper.class);
    
    public ItemTypeDefinitionWrapper(CMISMapping cmisMapping, PropertyAccessorMapping accessorMapping, 
            PropertyLuceneBuilderMapping luceneBuilderMapping, String typeId, DictionaryService dictionaryService, ClassDefinition cmisClassDef)
    {
        alfrescoName = cmisClassDef.getName();
        alfrescoClass = cmisMapping.getAlfrescoClass(alfrescoName);

        typeDef = new ItemTypeDefinitionImpl();

        typeDef.setBaseTypeId(BaseTypeId.CMIS_ITEM);
        typeDef.setId(typeId);
        typeDef.setLocalName(alfrescoName.getLocalName());
        typeDef.setLocalNamespace(alfrescoName.getNamespaceURI());

        if (BaseTypeId.CMIS_ITEM.value().equals(typeId) )
        {
            typeDef.setQueryName(ISO9075.encodeSQL(typeId));
            typeDef.setParentTypeId(null);
        }  
        else
        {
            typeDef.setQueryName(ISO9075.encodeSQL(cmisMapping.buildPrefixEncodedString(alfrescoName)));
            QName parentQName = cmisMapping.getCmisType(cmisClassDef.getParentName());
            if(parentQName != null)
            {         
                typeDef.setParentTypeId(cmisMapping.getCmisTypeId(BaseTypeId.CMIS_ITEM, parentQName));
            }
        }

        typeDef.setDisplayName((cmisClassDef.getTitle(dictionaryService) != null) ? cmisClassDef.getTitle(dictionaryService) : typeId);
        typeDef.setDescription(cmisClassDef.getDescription(dictionaryService) != null ? cmisClassDef.getDescription(dictionaryService) : typeDef
                .getDisplayName());

        typeDef.setIsCreatable(true);
        typeDef.setIsQueryable(true);
        typeDef.setIsFulltextIndexed(true);
        typeDef.setIsControllablePolicy(false);
        typeDef.setIsControllableAcl(true);
        typeDef.setIsIncludedInSupertypeQuery(cmisClassDef.getIncludedInSuperTypeQuery());
        typeDef.setIsFileable(false);

        typeDefInclProperties = CMISUtils.copy(typeDef);
        setTypeDefinition(typeDef, typeDefInclProperties);

        createOwningPropertyDefinitions(cmisMapping, accessorMapping, luceneBuilderMapping, dictionaryService, cmisClassDef);
        createActionEvaluators(accessorMapping, BaseTypeId.CMIS_ITEM);
    }
}