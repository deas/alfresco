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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.extensions.surf.util.Pair;
import org.springframework.extensions.surf.util.StringBuilderWriter;


/**
 * Abstract store class provided as a convenience for developers
 * who wish to build their own custom Store implementations for use
 * with the Web Script framework.
 * 
 * @author muzquiano
 */
public abstract class AbstractStore implements Store
{
    public static final String DESC_PATH_PATTERN = "*" + DeclarativeRegistry.WEBSCRIPT_DESC_XML;
    
    private PreviewContextProvider previewContextProvider = null;    
    private boolean readOnly = false;
    
    /**
     * Sets whether the class path store is to operate in read-only mode.
     * 
     * Read only prevents users from performing creates, updates and removes
     * 
     * @param readOnly
     */
    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }
    
    /**
     * Whether the store is in read-only mode
     * 
     * @return
     */
    public boolean isReadOnly()
    {
        return this.readOnly;
    }
    
    /**
     * Sets the preview context provider
     * 
     * @param previewContextProvider
     */
    public void setPreviewContextProvider(PreviewContextProvider previewContextProvider)
    {
        this.previewContextProvider = previewContextProvider;
    }
    
    /**
     * Gets the preview context
     * 
     * @return preview context
     */
    public PreviewContext getPreviewContext()
    {
        PreviewContext previewContext = null;
        
        if (this.previewContextProvider != null)
        {
            previewContext = this.previewContextProvider.provide();
        }
        
        return previewContext;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#createDocuments(java.util.List)
     */
    public void createDocuments(List<Pair<String, Document>> pathContents) throws IOException
    {
        for (Pair<String, Document> pathContent : pathContents)
        {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setSuppressDeclaration(false);
            
            StringBuilderWriter writer = new StringBuilderWriter(1024);
            XMLWriter xmlWriter = new XMLWriter(writer, format);
            xmlWriter.write(pathContent.getSecond());
            xmlWriter.flush();
            writer.close();
            createDocument(pathContent.getFirst(), writer.toString());
        }
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#getDocumentPaths(java.lang.String, java.lang.String)
     */
    public String[] getDocumentPaths(String path, String filePathPattern)
    {
        /**
         * An in-memory full-path pattern matching implementation of the
         * getDocumentPaths method for file path pattern matching.
         * 
         * Inheriting classes may wish to override this to provide more
         * efficient lookups. 
         */

        ArrayList<String> array = new ArrayList<String>(64);
        
        // convert to a regular expression
        String regexPattern = filePathPattern;
        if (regexPattern.endsWith("*.*"))
        {
            // TODO: handle this wildcard
            regexPattern = regexPattern.substring(0, regexPattern.length() - 3);
            regexPattern = regexPattern + "*\\..*";
        }
        
        // compile regular expression
        Pattern pattern = Pattern.compile(regexPattern);
        
        // get all document paths
        String[] allDocumentPaths = this.getAllDocumentPaths();
        
        // process matches
        for (int i = 0; i < allDocumentPaths.length; i++)
        {
            String documentPath = allDocumentPaths[i];
            
            // fix up document paths so they match /a/b/c.gif
            
            documentPath = documentPath.replace("\\", "/");
            if (!documentPath.startsWith("/"))
            {
                documentPath = "/" + documentPath;
            }
            
            if (documentPath.startsWith(path))
            {
            	documentPath = documentPath.substring(path.length());
            	if (!documentPath.startsWith("/"))
            	{
            	    documentPath = "/" + documentPath;
            	}
            	
	            if (pattern.matcher(documentPath).matches())
	            {
	                if (documentPath.startsWith("/"))
	                {
	                    documentPath = documentPath.substring(1);
	                }
	                array.add(documentPath);
	            }
            }
        }
        
        return array.toArray(new String[array.size()]);        
    }
}
