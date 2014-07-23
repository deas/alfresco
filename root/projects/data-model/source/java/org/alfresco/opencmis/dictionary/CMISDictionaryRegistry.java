package org.alfresco.opencmis.dictionary;

import java.util.Collection;
import java.util.List;

import org.alfresco.repo.dictionary.CompiledModel;
import org.alfresco.service.namespace.QName;

/**
 * 
 * @author sglover
 *
 */
public interface CMISDictionaryRegistry
{
    TypeDefinitionWrapper getTypeDefByTypeId(String typeId);
    TypeDefinitionWrapper getTypeDefByTypeId(String typeId, boolean includeParent);
    TypeDefinitionWrapper getAssocDefByQName(QName qname);
    TypeDefinitionWrapper getTypeDefByQueryName(Object queryName);
    TypeDefinitionWrapper getTypeDefByQName(QName qname);
    PropertyDefinitionWrapper getPropDefByPropId(String propId);
    PropertyDefinitionWrapper getPropDefByQueryName(Object queryName);
    List<TypeDefinitionWrapper> getBaseTypes();
    List<TypeDefinitionWrapper> getBaseTypes(boolean includeParent);
    Collection<AbstractTypeDefinitionWrapper> getTypeDefs();
    Collection<AbstractTypeDefinitionWrapper> getTypeDefs(boolean includeParent);
    Collection<AbstractTypeDefinitionWrapper> getAssocDefs();
    Collection<AbstractTypeDefinitionWrapper> getAssocDefs(boolean includeParent);
    void registerTypeDefinition(AbstractTypeDefinitionWrapper typeDef);
    String getTenant();
    List<TypeDefinitionWrapper> getChildren(String typeId);
    void setChildren(String typeId, List<TypeDefinitionWrapper> children);
    void addChild(String typeId, TypeDefinitionWrapper child);

    void addModel(CompiledModel model);
    void updateModel(CompiledModel model);
    void removeModel(CompiledModel model);
}
