/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

import java.util.Collection;

import org.alfresco.opencmis.mapping.CMISMapping;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.namespace.QName;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.CmisVersion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * CMIS Dictionary which provides Types that strictly conform to the CMIS
 * specification.
 * 
 * That is, only maps types to one of root Document, Folder, Relationship &
 * Policy.   
 * 
 * And Item which is pretty much anything that is not a Document, Folder, Relationship or Policy.
 * 
 * @author steveglover
 * @author davidc
 * @author mrogers
 */
public class CMISStrictDictionaryService extends CMISAbstractDictionaryService
{
	private Log logger = LogFactory.getLog(CMISStrictDictionaryService.class);
    
    public static final String DEFAULT = "DEFAULT_DICTIONARY";

	@Override
    protected void createDefinitions(DictionaryRegistry registry)
    {
        createTypeDefs(registry, dictionaryService.getAllTypes());
        createAssocDefs(registry, dictionaryService.getAllAssociations());
        createTypeDefs(registry, dictionaryService.getAllAspects());
    }

    /**
     * Create Type Definitions
     * 
     * @param registry
     * @param classQNames
     */
    private void createTypeDefs(DictionaryRegistry registry, Collection<QName> classQNames)
    {
        for (QName classQName : classQNames)
        {
            // skip items that are remapped to CMIS model
            if (cmisMapping.isRemappedType(classQName))
                continue;

            // create appropriate kind of type definition
            ClassDefinition classDef = dictionaryService.getClass(classQName);
            String typeId = null;
            AbstractTypeDefinitionWrapper objectTypeDef = null;
            if (cmisMapping.isValidCmisDocument(classQName))
            {
                typeId = cmisMapping.getCmisTypeId(BaseTypeId.CMIS_DOCUMENT, classQName);
                objectTypeDef = new DocumentTypeDefinitionWrapper(cmisMapping, accessorMapping, luceneBuilderMapping, typeId, dictionaryService, classDef);
            }
            else if (cmisMapping.isValidCmisFolder(classQName))
            {
                typeId = cmisMapping.getCmisTypeId(BaseTypeId.CMIS_FOLDER, classQName);
                objectTypeDef = new FolderTypeDefintionWrapper(cmisMapping, accessorMapping, luceneBuilderMapping, typeId, dictionaryService, classDef);
            }
            else if (cmisMapping.getCmisVersion().equals(CmisVersion.CMIS_1_1) && cmisMapping.isValidCmisSecondaryType(classQName))
            {
                typeId = cmisMapping.getCmisTypeId(BaseTypeId.CMIS_SECONDARY, classQName);
                objectTypeDef = new SecondaryTypeDefinitionWrapper(cmisMapping, accessorMapping, luceneBuilderMapping, typeId, dictionaryService, classDef);
            }
            else if (cmisMapping.isValidCmisPolicy(classQName))
            {
                typeId = cmisMapping.getCmisTypeId(BaseTypeId.CMIS_POLICY, classQName);
                objectTypeDef = new PolicyTypeDefintionWrapper(cmisMapping, accessorMapping, luceneBuilderMapping, typeId, dictionaryService, classDef);
            }
            else if (cmisMapping.isValidCmisItem(classQName))
            {
                typeId = cmisMapping.getCmisTypeId(BaseTypeId.CMIS_ITEM, classQName);
                objectTypeDef = new ItemTypeDefinitionWrapper(cmisMapping, accessorMapping, luceneBuilderMapping, typeId, dictionaryService, classDef);
            }

            if (objectTypeDef != null)
            {
                registry.registerTypeDefinition(objectTypeDef);
            }
        }
    }

    /**
     * Create Relationship Definitions
     * 
     * @param registry
     * @param classQNames
     */
    private void createAssocDefs(DictionaryRegistry registry, Collection<QName> classQNames)
    {
        // register base type
        String typeId = cmisMapping.getCmisTypeId(BaseTypeId.CMIS_RELATIONSHIP, CMISMapping.RELATIONSHIP_QNAME);
        ClassDefinition classDef = dictionaryService.getClass(CMISMapping.RELATIONSHIP_QNAME);

        // from Thor
        if (classDef == null)
        {
            if (classQNames.size() != 0)
            {
                logger.warn("Unexpected - no class for "+CMISMapping.RELATIONSHIP_QNAME+" - cannot create assocDefs for: "+classQNames);
            }
            return;
        }
        
        RelationshipTypeDefintionWrapper objectTypeDef = new RelationshipTypeDefintionWrapper(cmisMapping,
                accessorMapping, luceneBuilderMapping, typeId, dictionaryService, classDef);

        registry.registerTypeDefinition(objectTypeDef);

        // register all other relationships
        for (QName classQName : classQNames)
        {
            if (!cmisMapping.isValidCmisRelationship(classQName))
                continue;

            // create appropriate kind of type definition
            AssociationDefinition assocDef = dictionaryService.getAssociation(classQName);
            typeId = cmisMapping.getCmisTypeId(BaseTypeId.CMIS_RELATIONSHIP, classQName);
            objectTypeDef = new RelationshipTypeDefintionWrapper(cmisMapping, accessorMapping, luceneBuilderMapping, 
                    typeId, dictionaryService, assocDef);

            registry.registerTypeDefinition(objectTypeDef);
        }
    }
}
