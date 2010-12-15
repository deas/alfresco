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
package org.alfresco.webservice.util;


/**
 * Constants class used by the web service client samples and tests
 * 
 * @author Roy Wetherall
 */
public class Constants
{
    /** Space Schemes */
    public static final String WORKSPACE_STORE = "workspace";
    
    /** Query language names */
    public static final String QUERY_LANG_LUCENE = "lucene";
    
    /** Namespace constants */
    public static final String NAMESPACE_SYSTEM_MODEL   = "http://www.alfresco.org/model/system/1.0";
    public static final String NAMESPACE_CONTENT_MODEL  = "http://www.alfresco.org/model/content/1.0";
    
    /** Useful model constants */
    public static final String ASSOC_CHILDREN =         createQNameString(NAMESPACE_SYSTEM_MODEL, "children");    
    public static final String TYPE_CMOBJECT =          createQNameString(NAMESPACE_CONTENT_MODEL, "cmobject");
    public static final String PROP_NAME =              createQNameString(NAMESPACE_CONTENT_MODEL, "name");
    public static final String TYPE_CONTENT =           createQNameString(NAMESPACE_CONTENT_MODEL, "content");
    public static final String PROP_CONTENT =           createQNameString(NAMESPACE_CONTENT_MODEL, "content");
    public static final String ASSOC_CONTAINS =         createQNameString(NAMESPACE_CONTENT_MODEL, "contains");
    public static final String ASPECT_VERSIONABLE =     createQNameString(NAMESPACE_CONTENT_MODEL, "versionable");
    public static final String ASPECT_TITLED =          createQNameString(NAMESPACE_CONTENT_MODEL, "titled");
    public static final String PROP_CREATED =           createQNameString(NAMESPACE_CONTENT_MODEL, "created");
    public static final String PROP_DESCRIPTION =       createQNameString(NAMESPACE_CONTENT_MODEL, "description");    
    public static final String PROP_TITLE =             createQNameString(NAMESPACE_CONTENT_MODEL, "title");    
    public static final String TYPE_FOLDER =            createQNameString(NAMESPACE_CONTENT_MODEL, "folder");
    public static final String ASPECT_CLASSIFIABLE =    createQNameString(NAMESPACE_CONTENT_MODEL, "classifiable"); 
    
    /** Person property constants */
    public static final String PROP_USERNAME =          createQNameString(NAMESPACE_CONTENT_MODEL, "userName");
    public static final String PROP_USER_HOMEFOLDER =   createQNameString(NAMESPACE_CONTENT_MODEL, "homeFolder");
    public static final String PROP_USER_FIRSTNAME =    createQNameString(NAMESPACE_CONTENT_MODEL, "firstName");
    public static final String PROP_USER_MIDDLENAME =   createQNameString(NAMESPACE_CONTENT_MODEL, "middleName");
    public static final String PROP_USER_LASTNAME =     createQNameString(NAMESPACE_CONTENT_MODEL, "lastName");
    public static final String PROP_USER_EMAIL =        createQNameString(NAMESPACE_CONTENT_MODEL, "email");
    public static final String PROP_USER_ORGID =        createQNameString(NAMESPACE_CONTENT_MODEL, "organizationId");
    
    /** Mime types */
    public static final String MIMETYPE_TEXT_PLAIN  = "text/plain";
    public static final String MIMETYPE_TEXT_CSS    = "text/css";  
    public static final String MIMETYPE_XML = "text/xml";
    
    /** Permission prefixes for role's and group's */
    public static final String ROLE_PREFIX      = "ROLE_";    
    public static final String GROUP_PREFIX     = "GROUP_";
    
    /** Standard authorities */
    public static final String ALL_AUTHORITIES          = "GROUP_EVERYONE";
    public static final String OWNER_AUTHORITY          = "ROLE_OWNER";
    public static final String LOCK_OWNER_AUTHORITY     = "ROLE_LOCK_OWNER";
    public static final String ADMINISTRATOR_AUTHORITY  = "ROLE_ADMINISTRATOR";

    /** Common permissions */
    public static final String ALL_PERMISSIONS          = "All";
    public static final String FULL_CONTROL             = "FullControl";
    public static final String READ                     = "Read";
    public static final String WRITE                    = "Write";
    public static final String DELETE                   = "Delete";
    public static final String ADD_CHILDREN             = "AddChildren";
    public static final String READ_PROPERTIES          = "ReadProperties";
    public static final String READ_CHILDREN            = "ReadChildren";
    public static final String WRITE_PROPERTIES         = "WriteProperties";
    public static final String DELETE_NODE              = "DeleteNode";
    public static final String DELETE_CHILDREN          = "DeleteChildren";
    public static final String CREATE_CHILDREN          = "CreateChildren";
    public static final String LINK_CHILDREN            = "LinkChildren";
    public static final String DELETE_ASSOCIATIONS      = "DeleteAssociations";
    public static final String READ_ASSOCIATIONS        = "ReadAssociations";
    public static final String CREATE_ASSOCIATIONS      = "CreateAssociations";
    public static final String READ_PERMISSIONS         = "ReadPermissions";
    public static final String CHANGE_PERMISSIONS       = "ChangePermissions";
    public static final String EXECUTE                  = "Execute";
    public static final String READ_CONTENT             = "ReadContent";
    public static final String WRITE_CONTENT            = "WriteContent";
    public static final String EXECUTE_CONTENT          = "ExecuteContent";
    public static final String TAKE_OWNERSHIP           = "TakeOwnership";
    public static final String SET_OWNER                = "SetOwner";
    public static final String COORDINATOR              = "Coordinator";
    public static final String CONTRIBUTOR              = "Contributor";
    public static final String EDITOR                   = "Editor";
    public static final String GUEST                    = "Guest";
    public static final String LOCK                     = "Lock";   
    public static final String UNLOCK                   = "Unlock";
    public static final String CHECK_OUT                = "CheckOut";
    public static final String CHECK_IN                 = "CheckIn";
    public static final String CANCEL_CHECK_OUT         = "CancelCheckOut";

    /**
     * Helper function to create a QName string from a namespace URI and name
     * 
     * @param namespace     the namespace URI
     * @param name          the name
     * @return              QName string
     */
    public static String createQNameString(String namespace, String name)
    {
        return "{" + namespace + "}" + name;
    }
}
