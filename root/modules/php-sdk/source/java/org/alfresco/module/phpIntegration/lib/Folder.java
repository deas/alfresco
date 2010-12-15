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
package org.alfresco.module.phpIntegration.lib;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.module.phpIntegration.PHPProcessorException;
import org.alfresco.service.cmr.repository.NodeRef;

import com.caucho.quercus.annotation.Optional;

/**
 * Folder object, represents a node of the standard cm_folder type.
 * 
 * @author Roy Wetherall
 */
public class Folder extends Node
{
    /** Script name */
    private static final String SCRIPT_OBJECT_NAME = "Folder";
    
    /** Model constants */
    private static final String TYPE_FOLDER = ContentModel.TYPE_FOLDER.toString();
    private static final String TYPE_FILE = ContentModel.TYPE_CONTENT.toString();
    private static final String PROP_NAME = ContentModel.PROP_NAME.toString();
    private static final String ASSOC_CONTAINS = ContentModel.ASSOC_CONTAINS.toString();
    
    /** List of file */
    private List<File> files;
    
    /** List of folders */
    private List<Folder> folders;
    
    /**
     * Constructor 
     * 
     * @param session   the session
     * @param nodeRef   the node reference
     */
    public Folder(Session session, NodeRef nodeRef)
    {
        super(session, nodeRef);
    }
    
    /**
     * Constructor
     * 
     * @param session   the session
     * @param store     the store
     * @param id        the id
     */
    public Folder(Session session, Store store, String id)
    {
        super(session, store, id);
    }
    
    /**
     * Constructor
     * 
     * @param session   the session
     * @param store     the store
     * @param id        the id
     * @param type      the type
     */
    public Folder(Session session, Store store, String id, String type)
    {
        super(session, store, id, type);        
    }
    
    /**
     * @see org.alfresco.module.phpIntegration.lib.ScriptObject#getScriptObjectName()
     */
    @Override
    public String getScriptObjectName()
    {
        return SCRIPT_OBJECT_NAME;
    }
    
    /**
     * Gets all the files contained within this folder
     * 
     * @return  list of files
     */
    public List<File> getFiles()
    {
        if (this.files == null)
        {
            this.files = new ArrayList<File>(10);
            for(ChildAssociation assoc : getChildren())
            {
                if (assoc.getChild() instanceof File)
                {
                    this.files.add((File)assoc.getChild());
                }
            }            
        }
        
        return this.files;
    }
    
    /**
     * Gets all the folders contained within this folder
     * 
     * @return  list of folders
     */
    public List<Folder> getFolders()
    {
        if (this.folders == null)
        {
            this.folders = new ArrayList<Folder>(10);
            for(ChildAssociation assoc : getChildren())
            {
                if (assoc.getChild() instanceof Folder)
                {
                    this.folders.add((Folder)assoc.getChild());
                }
            } 
        }
        
        return this.folders;
    }

    public File createFile(String fileName, @Optional("") String type)
    {
        if (type == null || type.length() == 0)
        {
            type = TYPE_FILE;
        }
        else
        {
            // Check that the type is a sub type of cm_content
            if (this.session.getDataDictionary().isSubTypeOf(type, TYPE_FILE) == false)
            {
                throw new PHPProcessorException("Can not create file since " + type + " is not a sub type of cm_content.");
            }
        }
        
        // Create the file and set the name
        File file = (File)createChild(type, ASSOC_CONTAINS, "cm_" + fileName);
        file.setProperty(PROP_NAME, fileName);
              
        return file;
    }
    
    public Folder createFolder(String folderName, String type)
    {
        if (type == null)
        {
            type = TYPE_FOLDER;
        }
        else
        {
            // Check that the type is a sub type of cm_content
            if (this.session.getDataDictionary().isSubTypeOf(type, TYPE_FOLDER) == false)
            {
                throw new PHPProcessorException("Can not create folder since " + type + " is not a sub type of cm_folder.");
            }
        }
        
        // Create the folder and set the name
        Folder folder = (Folder)createChild(type, ASSOC_CONTAINS, "cm_" + folderName);
        folder.setProperty(PROP_NAME, folderName);
        
        return folder;
    }    
    
    @Override
    protected void cleanNode()
    {
        super.cleanNode();
        cleanFolder();
    }
    
    @Override
    public Node createChild(String type, String associationType, String associationName)
    {
        Node node = super.createChild(type, associationType, associationName);
        cleanFolder();
        return node;
    }
    
    @Override
    public void addChild(Node node, String associationType, String associationName)
    {
        super.addChild(node, associationType, associationName);
        cleanFolder();
    }
    
    @Override
    public void removeChild(ChildAssociation childAssociation)
    {
        super.removeChild(childAssociation);
        cleanFolder();
    }
    
    private void cleanFolder()
    {
        this.files = null;
        this.folders = null;
    }
}
