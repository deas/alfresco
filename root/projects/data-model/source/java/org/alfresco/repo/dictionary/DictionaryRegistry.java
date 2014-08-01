package org.alfresco.repo.dictionary;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.namespace.QName;

/**
 *
 * @author sglover
 *
 */
public interface DictionaryRegistry
{
	void init();
	void remove();
	CompiledModel getModel(QName name);
	boolean modelExists(QName name);
    Map<String, List<CompiledModel>> getUriToModels();
    Map<QName, CompiledModel> getCompiledModels(boolean includeInherited);
    QName putModel(CompiledModel model);
    void removeModel(QName modelName);
	String getTenantDomain();
	void clear();
	List<CompiledModel> getModelsForUri(String uri);
	AspectDefinition getAspect(QName aspectName);
	AssociationDefinition getAssociation(QName assocName);
	ClassDefinition getClass(QName className);
	PropertyDefinition getProperty(QName propertyName);
	TypeDefinition getType(QName typeName);
	ConstraintDefinition getConstraint(QName constraintQName);
	DataTypeDefinition getDataType(QName typeName);
	@SuppressWarnings("rawtypes")
	DataTypeDefinition getDataType(Class javaClass);
	boolean isModelInherited(QName modelName);
	Map<String, String> getPrefixesCache();
    List<String> getUrisCache();
    Collection<String> getPrefixes(String URI);
    void addURI(String uri);
    boolean hasURI(String uri);
    void addPrefix(String prefix, String uri);
    boolean hasPrefix(String prefix);
    void removeURI(String uri);
    void removePrefix(String prefix);
    Collection<QName> getTypes(boolean includeInherited);
    Collection<QName> getAssociations(boolean includeInherited);
    Collection<QName> getAspects(boolean includeInherited);
    String getNamespaceURI(String prefix);
}
