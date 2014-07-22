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
import java.io.Serializable;
import java.util.Map;
import java.util.Set;


/**
 * Web Script Description
 * 
 * @author davidc
 */
public interface Description extends BaseDescriptionDocument
{
    /**
     * Enumeration of "required" Authentication level
     */
    public enum RequiredAuthentication
    {
        none,
        guest,
        user,
        admin
    }
    
    /**
     * Enumeration of "required" Transaction level
     */
    public enum RequiredTransaction
    {
        none,
        required,
        requiresnew
    }

    /**
     * Enumeration of transaction capability
     */
    public enum TransactionCapability
    {
        readonly,
        readwrite
    }
    
    /**
     * Transaction Requirements
     */
    public interface RequiredTransactionParameters
    {
        /**
         * Get required transaction semantics
         * 
         * @return  required transaction
         */
        public RequiredTransaction getRequired();
        
        /**
         * Get transaction capability (readonly,readwrite)
         * 
         * @return
         */
        public TransactionCapability getCapability();
        
        /**
         * Get buffer size (for caching transactional response)
         * 
         * @return
         */
        public int getBufferSize();
    }
    
    /**
     * Caching requirements
     */
    public interface RequiredCache
    {
        /**
         * Determine if Web Script should ever be cached
         * 
         * @return  true => do not cache, false => caching may or not occur
         */
        public boolean getNeverCache();

        /**
         * Determine if Web Script content is for public caching
         * 
         * @return  true => content is public, so allow cache
         */
        public boolean getIsPublic();

        /**
         * Must cache re-validate to ensure content is fresh
         *  
         * @return  true => must re-validate
         */
        public boolean getMustRevalidate();
    }

    /**
     * Enumeration of ways to specify format 
     */
    public enum FormatStyle
    {
        any,          // any of the following styles
        extension,    // /a/b/c.x
        argument      // /a/b/c?format=x
    }
    
    /**
     * Gets the root path of the store of this web script
     * 
     * @return  root path of store
     */
    public String getStorePath();
        
    /**
     * Gets the path within the store of this web script
     * 
     * @return  path within store
     */
    public String getScriptPath();
    
    /**
     * Gets the package  (Path version of getScriptPath)
     * 
     */
    public Path getPackage();
    
    /**
     * Gets the path of the description xml document for this web script
     * 
     * @return  document location (path)
     */
    public String getDescPath();

    /**
     * Gets the description xml document for this web script
     * 
     * @return  source document
     */
    public InputStream getDescDocument()
        throws IOException;
    
    /**
     * Gets the kind of service.
     * 
     * A Web Script may explicitly define its backing-bean implementation. The backing bean
     * implementation is identified by a Spring bean whose id is:
     * 
     * webscript.<kind>
     * 
     * The <kind> may be dot separated.
     * 
     * @return  the kind of service (or null, for a vanilla declarative web script)
     */
    public String getKind();
            
    /**
     * Gets the family(s) of this service in the order that they are defined
     */
    public Set<String> getFamilys();
    
    /**
     * Gets the required authentication level for execution of this service
     * 
     * @return  the required authentication level 
     */
    public RequiredAuthentication getRequiredAuthentication();
    
    /**
     * Gets the ID of the user that the service should be run as or <code>null</code> if the service is to be run as the
     * authenticated user.
     * 
     * @return a user name or <code>null</code> if not applicable
     */
    public String getRunAs();

    /**
     * Gets the required transaction level 
     * 
     * @return  the required transaction level
     */
    public RequiredTransaction getRequiredTransaction();
    
    /**
     * Gets the transaction parameters
     * 
     * @return  the transaction parameters
     */
    public RequiredTransactionParameters getRequiredTransactionParameters();
    
    /**
     * Gets the required level of caching
     * @return
     */
    public RequiredCache getRequiredCache();
    
    /**
     * Gets the HTTP method this service is bound to
     * 
     * @return  HTTP method
     */
    public String getMethod();

    /**
     * Gets the URIs this service supports
     * 
     * @return  array of URIs in order specified in service description document
     */
    public String[] getURIs();

    /**
     * Gets the style of Format discriminator supported by this web script
     * 
     * @return  format style
     */
    public FormatStyle getFormatStyle();

    /**
     * Gets the default response format
     * 
     * Note: the default response format is the first listed in the service
     *       description document
     * 
     * @return  default response format (or null, if format not known until run-time)
     */
    public String getDefaultFormat();
    
    /**
     * Gets the formats available for negotiation
     * 
     * @return  negotiated formats
     */
    public NegotiatedFormat[] getNegotiatedFormats();
    
    /**
     * Gets web script specific extensions
     * 
     * @return  map of extensions by name
     */
    public Map<String, Serializable> getExtensions();
    
    /**
     * Enumeration of lifecycle which indicates the status of this web script
     */
    public enum Lifecycle
    {
    	none,
    	sample,
        draft,
        draft_public_api,
        public_api,
        deprecated,
        internal
    }
    
    /**
     * Get the lifecycle 
     */
    public Lifecycle getLifecycle();
    
    /**
     * @return true if automatic multipart formdata processing is enabled
     */
    public boolean getMultipartProcessing();
    
    /**
     * @param multipartProcessing true if automatic multipart formdata processing is enabled
     */
    public void setMultipartProcessing(boolean multipartProcessing);
    
    /**
     * Gets webscript argument descriptions
     * 
     * @return argument descriptions
     */
    public ArgumentTypeDescription[] getArguments();
    
    /**
     * Gets webscript request types
     * 
     * @return request types
     */
    public TypeDescription[] getRequestTypes();
    
    /**
     * Gets webscript response types
     * 
     * @return response types
     */
    public TypeDescription[] getResponseTypes();
}
