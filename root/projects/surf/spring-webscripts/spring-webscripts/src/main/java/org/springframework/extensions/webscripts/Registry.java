/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.webscripts;

import java.util.Collection;
import java.util.Map;



/**
 * Web Scripts Registry
 * 
 * @author davidc
 */
public interface Registry
{
    /**
     * Gets a Web Script Package
     * 
     * @param packagePath
     * @return  web script path representing package
     */
    public Path getPackage(String packagePath);
    
    /**
     * Gets a Web Script URL
     * 
     * @param uriPath
     * @return  web script path representing uri
     */
    public Path getUri(String uriPath);
    
    /**
     * Gets a Web Script Family
     * 
     * NOTE:
     * - To get all families, pass /
     * - To get a specific family, pass /{familyName}
     * 
     * @param familyPath
     * @return  web script path representing family
     */
    public Path getFamily(String familyPath);
    
    /**
     * Gets a Lifecycle Family, for example, all deprecated web scripts
     * 
     * NOTE:
     * - To get all lifecycles, pass /
     * - To get a specific lifecycle, pass /{lifecycleName}
     * 
     * @param lifecyclePath
     * @return  web script path representing family
     */
    public Path getLifecycle(String lifecyclePath);
    
    /**
     * Gets all Web Scripts
     * 
     * @return  web scripts
     */
    public Collection<WebScript> getWebScripts();

    /**
     * Gets all Web Script definitions that failed to register
     * 
     * @return map of error by web script definition file path
     */
    public Map<String, String> getFailures();

    /**
     * Gets a Web Script by Id
     * 
     * @param id  web script id
     * @return  web script
     */
    public WebScript getWebScript(String id);

    /**
     * Gets a Web Script given an HTTP Method and URI
     * 
     * @param method  http method
     * @param uri  uri
     * @return  script match (pair of script and uri that matched)
     */
    public Match findWebScript(String method, String uri);

    /**
     * Resets the Web Script Registry
     */
    public void reset();
    
	/**
	 * Gets a package description document given a webscript package
	 * 
	 * @param scriptPackage webscript package
	 * @return matched package description document
	 */
	public PackageDescriptionDocument getPackageDescriptionDocument(String scriptPackage);
	
	/**
	 * Gets a schema description document given a schema id
	 * 
	 * @param schemaId schema id
	 * @return matched schema description document
	 */
	public SchemaDescriptionDocument getSchemaDescriptionDocument(String schemaId);
	
	/**
	 * Returns the whole list of package description documents
	 * 
	 * @return list of package description documents
	 */
	public Collection<PackageDescriptionDocument> getPackageDescriptionDocuments();
	
	/**
	 * Returns the whole list of schema description documents
	 * 
	 * @return list of schema description documents
	 */
	public Collection<SchemaDescriptionDocument> getSchemaDescriptionDocuments();
	
	/**
	 * Gets a schema type description given a schema type id
	 * 
	 * @param typeId schema type id
	 * @return matched schema type description
	 */
	public TypeDescription getSchemaTypeDescriptionById(String typeId);
	
	/**
	 * Gets all package description documents that fail to register
	 * 
	 * @return map of failed package description documents by path
	 */
	public Map<String, String> getFailedPackageDescriptionsByPath();
	
	/**
	 * Gets all schema description documents that fail to register
	 * 
	 * @return map of failed schema description documents by path
	 */
	public Map<String, String> getFailedSchemaDescriptionsByPath();
    
}
