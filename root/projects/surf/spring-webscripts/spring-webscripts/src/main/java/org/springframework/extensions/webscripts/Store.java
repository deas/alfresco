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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.dom4j.Document;
import org.springframework.extensions.surf.util.Pair;

import freemarker.cache.TemplateLoader;


/**
 * Store for holding Web Script Definitions and Implementations
 *
 * @author davidc
 */
public interface Store
{
	/**
	 * Initialise Store (called once)
	 */
	public void init();

    /**
     * Determines whether the store actually exists
     *
     * @return  true => it does exist
     */
    public boolean exists();

    /**
     * Gets the base path of the store
     *
     * @return base path
     */
    public String getBasePath();

    /**
     * Returns true if this store is considered secure - i.e. on the app-server classpath. Scripts in secure stores can
     * be run under the identity of a declared user (via the runas attribute) rather than the authenticated user.
     *
     * @return true if this store is considered secure
     */
    public boolean isSecure();

    /**
     * Gets the paths of given document pattern within given path/sub-paths in this store
     *
     * @param path             start path
     * @param includeSubPaths  if true, include sub-paths
     * @param documentPattern  document name, allows wildcards, eg. *.ftl or my*.ftl
     * @return  array of document paths
     */
    public String[] getDocumentPaths(String path, boolean includeSubPaths, String documentPattern) throws IOException;

    /**
     * Gets the paths matching a given file path pattern in this store.
     *
     * @param path             start path
     * @param filePathPattern  file path pattern string, allows wildcards, eg. *.ftl or my*.ftl or *\/a\/*.xml
     * @return  array of document paths
     */
    public String[] getDocumentPaths(String path, String filePathPattern) throws IOException;

    /**
     * Gets the paths of all Web Script description documents in this store
     *
     * @return array of description document paths
     */
    public String[] getDescriptionDocumentPaths() throws IOException;

    /**
     * Gets the paths of all implementation files for a given Web Script
     *
     * @param script  web script
     * @return  array of implementation document paths
     */
    public String[] getScriptDocumentPaths(WebScript script) throws IOException;

    /**
     * Gets the paths of all documents in this store
     *
     * @return array of all document paths
     */
    public String[] getAllDocumentPaths();

    /**
     * Gets the last modified timestamp for the document.
     *
     * @param documentPath  document path to an existing document
     * @return  last modified timestamp
     *
     * @throws IOException if the document does not exist in the store
     */
    public long lastModified(String documentPath)
        throws IOException;

    /**
     * Determines if the document exists.
     * 
     * @param documentPath
     *            document path
     * @return true => exists, false => does not exist
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public boolean hasDocument(String documentPath)
        throws IOException;

    /**
     * Gets a document. Note a raw InputStream to the content is returned and
     * must be closed by the accessing method.
     *
     * @param documentPath  document path
     * @return  input stream onto document
     *
     * @throws IOException if the document does not exist in the store
     */
    public InputStream getDocument(String documentPath)
        throws IOException;

    /**
     * Creates a document.
     *
     * @param documentPath  document path
     * @param content       content of the document to write
     *
     * @throws IOException if the document already exists or the create fails
     */
    public void createDocument(String documentPath, String content)
        throws IOException;

    /**
     * Creates multiple XML documents at the specified paths.
     * 
     * @param paths     list of path, document pairs
     * 
     * @throws IOException if a document already exists or a create fails
     */
    public void createDocuments(List<Pair<String, Document>> paths)
        throws IOException;

    /**
     * Updates an existing document.
     *
     * @param documentPath  document path
     * @param content       content to update the document with
     *
     * @throws IOException if the document does not exist or the update fails
     */
    public void updateDocument(String documentPath, String content)
        throws IOException;

    /**
     * Removes an existing document.
     *
     * @param documentPath  document path
     * @return  whether the operation succeeded
     *
     * @throws IOException if the document does not exist or the remove fails
     */
    public boolean removeDocument(String documentPath)
        throws IOException;

    /**
     * Gets the template loader for this store
     *
     * @return  template loader
     */
    public TemplateLoader getTemplateLoader();

    /**
     * Gets the script loader for this store
     *
     * @return  script loader
     */
    public ScriptLoader getScriptLoader();
    
    /**
     * Indicates whether or not this store can be written to.
     * 
     * @return <code>true</code> If the store cannot be written to and <code>false</code> otherwise.
     */
    public boolean isReadOnly();
}