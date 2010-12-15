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
package org.alfresco.module.mediawikiintegration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.Policy;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.LogUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Roy Wetherall
 */
public class MediaWikiService implements Constants,
                                      NodeServicePolicies.OnCreateNodePolicy,
                                      NodeServicePolicies.BeforeDeleteNodePolicy,
                                      NodeServicePolicies.OnDeleteNodePolicy
{
    private static Log logger = LogFactory.getLog(MediaWikiService.class);
    
    /** Policy component */
    private PolicyComponent policyComponent;
    
    /** Node service */
    private NodeService nodeService;
    
    /** Content service */
    private ContentService contentService;
    
    /** The Authority service */
    private AuthorityService authorityService;
    
    /** The permission service */
    private PermissionService permissionService;
    
    /** List of executed statements */
    private ThreadLocal<StringBuilder> executedStatementsThreadLocal = new ThreadLocal<StringBuilder>();
    
    // Server database credentials 
    private String url;
    private String username;
    private String password;
    private String databaseName = "alfresco";
    private String host = "localhost";
    private String hostPort = "5432";    
    
    /**
     * Sets the policy component
     * 
     * @param policyComponent   the policy component
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }
    
    /**
     * Sets the node service
     * 
     * @param nodeService   the node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * Sets the content service 
     * 
     * @param contentService    the content service
     */
    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }
    
    /**
     * Set the authority service
     * 
     * @param authorityService      the authority service
     */
    public void setAuthorityService(AuthorityService authorityService)
    {
        this.authorityService = authorityService;
    }
    
    /**
     * Set the permission service
     * 
     * @param permissionService     the permission service
     */
    public void setPermissionService(PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }
    
    /**
     * Sets the system DB jdbc URL and extracts details from it.
     * 
     * @param url   the URL
     */
    public void setUrl(String url)
    {
        this.url = url;        
        System.out.println("The connection URL: " + url);
        
        // Parse this to find the component parts of the URL
        // Note:  currently only MySQL is supported
        int index = url.indexOf("jdbc:mysql://");
        if (index == 0)
        {
            String remainder = url.substring(13);
            index = remainder.indexOf("/");
            if (index > 0)
            {
                String host = remainder.substring(0, index);
                this.databaseName = remainder.substring(index + 1);
                
                index = host.indexOf(":");
                if (index >= 0)
                {
                    this.host = host.substring(0, index);
                    this.hostPort = host.substring(index + 1);
                }
                else
                {
                    this.host = host;
                }
            }
        }
        else
        {
            throw new AlfrescoRuntimeException("The media wiki integration only support installation of the wiki db on MySql");
        }
        
    }
    
    /**
     * Sets the system DB user name
     * 
     * @param username  the user name
     */
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    /**
     * Sets the system DB password
     *  
     * @param password  the password
     */
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    /**
     *  Initialise method 
     */
    public void init()
    {
        // Register interest in the the policies
        this.policyComponent.bindClassBehaviour(
                QName.createQName(Policy.NAMESPACE, "onCreateNode"), 
                TYPE_MEDIAWIKI, 
                new JavaBehaviour(this, "onCreateNode", NotificationFrequency.TRANSACTION_COMMIT));
    }

    /**
     * @see org.alfresco.repo.node.NodeServicePolicies.OnCreateNodePolicy#onCreateNode(org.alfresco.service.cmr.repository.ChildAssociationRef)
     */
    public void onCreateNode(ChildAssociationRef childAssocRef)
    {
        // The newly created mediawiki space
        NodeRef mediawiki = childAssocRef.getChildRef();
        
        // Set the default values for the various configuration properties
        String wikiName = (String)this.nodeService.getProperty(mediawiki, ContentModel.PROP_NAME);
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(15);
        properties.put(PROP_SITENAME, wikiName);
        NodeRef config = this.nodeService.createNode(
                mediawiki, 
              ASSOC_CONFIG, 
              QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, wikiName), 
              TYPE_MEDIAWIKI_CONFIG,
              properties).getChildRef();
        
        // Add the configuration webscript as a custom template
        Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
        props.put(ContentModel.PROP_WEBSCRIPT, "/wcs/mediawiki/details/node/" + mediawiki.getId());
        this.nodeService.addAspect(mediawiki, ContentModel.ASPECT_WEBSCRIPTABLE, props);
        
        // Create the user groups for this wiki
        String wikiAdminsGroup = this.authorityService.createAuthority(AuthorityType.GROUP, wikiName + "_WikiAdmins");
        String wikiUsersGroup = this.authorityService.createAuthority(AuthorityType.GROUP, wikiName + "_WikiUsers");
        
        // Set the permissions on the space and the configuration node
        this.permissionService.setInheritParentPermissions(mediawiki, false);
        this.permissionService.setPermission(mediawiki, wikiAdminsGroup, "WikiAdministrator", true);
        this.permissionService.setPermission(mediawiki, wikiUsersGroup, "WikiUser", true);
        permissionService.setPermission(mediawiki, PermissionService.ALL_AUTHORITIES, "WikiUser", true);
        this.permissionService.setInheritParentPermissions(config, false);
        this.permissionService.setPermission(config, wikiAdminsGroup, "WikiAdministrator", true);

        // Install the mediawiki db
        install(mediawiki, config);        
    }

    /** 
     * BeforeDeleteNode policy behaviour
     * 
     * @param nodeRef   node reference to deleting node
     */
    public void beforeDeleteNode(NodeRef nodeRef)
    {
        
    }

    /**
     * OnDeleteNode policy behaviour
     * 
     * @param childAssocRef     child association reference
     * @param isNodeArchived    indicates whether the node is being archived or not
     */
    public void onDeleteNode(ChildAssociationRef childAssocRef,
            boolean isNodeArchived)
    {
        
    }
    
    /**
     * Installs the mediawiki tables for a given wiki space
     * 
     * @param mediaWiki     mediawiki space node reference
     * @param config        corresponding configuration node reference
     */
    private void install(NodeRef mediaWiki, NodeRef config)
    {
        // TODO .. need to figure out who can do this!
        // TODO .. we need to check the permission level here!! 
        // TODO .. must be owner or admin ?!
        
        try 
        {
            // TODO .. currently presumes mySQL driver is loaded!
            // TODO .. support other databases
            
            // Create a connection to the database
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
                
            // Get the database creation script
            InputStream scriptInputStream = getClass().getClassLoader().getResourceAsStream("alfresco/module/org.alfresco.module.mediawikiintegration/sql/mysql-create-tables.sql");
            
            // Create the value map
            String prefix = generatePrefix(mediaWiki);
            Map<String, String> values = new HashMap<String, String>(1);
            values.put("/*$wgDBprefix*/", prefix);
            
            // Execute the script
            LogUtil.info(logger, "Executing MediaWiki table create script");
            List<String> createdTables = executeScriptFile(connection, scriptInputStream, values);
            
            // Create the uninstall db script and store it on the configuration node
            String sqlDropTables = getDropTableSQL(createdTables);
            ContentWriter writer = this.contentService.getWriter(config, Constants.PROP_SQL_DROP_TABLES, true);
            writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            writer.setEncoding("UTF-8");
            writer.putContent(sqlDropTables);
            
            // Set the dB configuration property values
            Map<QName, Serializable> properties = this.nodeService.getProperties(config);            
            // TODO only currently supports mysql
            properties.put(PROP_DB_TYPE, "mysql");
            properties.put(PROP_DB_SERVER, this.host);
            properties.put(PROP_DB_NAME, this.databaseName);
            properties.put(PROP_DB_USER, this.username);
            properties.put(PROP_DB_PASSWORD, this.password);
            properties.put(PROP_DB_PORT, this.hostPort);
            properties.put(PROP_DB_PREFIX, prefix);            
            this.nodeService.setProperties(config, properties);
        } 
        catch (Exception exception)
        {
            // Catch all
            throw new AlfrescoRuntimeException("Unable to create mediaWiki tables.", exception);
        }
    }
    
    /**
     * Generated the standard table prefix for the mediaWiki tables
     * 
     * @param wikiSpace     the wikiSpace
     * @return String       the table prefix string
     */
    private String generatePrefix(NodeRef wikiSpace)
    {
        return "mediawiki_" + wikiSpace.getId().replace("-", "") + "_";
    }
    
    /**
     * Uninstall the mediawiki tables associated with a mediawiki space
     * 
     * @param mediaWiki     the media wiki space node reference
     */
    @SuppressWarnings("unused")
    private void uninstall(NodeRef mediaWiki)
    {
        // Get the configuration node
        List<ChildAssociationRef> assocs = this.nodeService.getChildAssocs(mediaWiki, Constants.ASSOC_CONFIG, RegexQNamePattern.MATCH_ALL);
        if (assocs.size() != 1)
        {
            throw new AlfrescoRuntimeException("Unable to uninstall mediawiki tables as the configuration node could not be found.");
        }
        NodeRef config = assocs.get(0).getChildRef();        
        
        // Get the drop table script
        ContentReader reader = this.contentService.getReader(config, Constants.PROP_SQL_DROP_TABLES);
        String sql = reader.getContentString();
        
        // Execute the script
        try
        {
            // TODO .. we need to take into consideration the case when the mediawiki tables
            //         are not stored in the alfresco database 
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            executeStatement(connection, sql, false, 0);
        }
        catch (Exception exception)
        {
            throw new AlfrescoRuntimeException("Unable to uninstall mediawiki tables.", exception);
        }
    }
    
    /**
     * Executes a script file against the provided database connection
     * 
     * @param connection            the connection
     * @param scriptInputStream     
     * @throws Exception
     */
    private List<String> executeScriptFile(Connection connection, InputStream scriptInputStream, Map<String, String> values) throws Exception
    {
        List<String> createdTables = new ArrayList<String>(15);
        BufferedReader reader = new BufferedReader(new InputStreamReader(scriptInputStream, "UTF8"));
        try
        {        
            try
            {
                int line = 0;
                // loop through all statements
                StringBuilder sb = new StringBuilder(1024);
                while(true)
                {
                    String sql = reader.readLine();
                    line++;
                    
                    if (sql == null)
                    {
                        // nothing left in the file
                        break;
                    }
                    
                    // trim it
                    sql = sql.trim();
                    if (sql.length() == 0 ||
                        sql.startsWith( "--" ) ||
                        sql.startsWith( "//" ) ||
                        sql.startsWith( "/*" ) )
                    {
                        // there has not been anything to execute - it's just a comment line
                        continue;
                    }
                    
                    // process any value substitutions that need to take place
                    sql = valueSubstitution(sql, values);
                    
                    // have we reached the end of a statement?
                    boolean execute = false;
                    boolean optional = false;
                    if (sql.endsWith(";"))
                    {
                        sql = sql.substring(0, sql.length() - 1);
                        execute = true;
                        optional = false;
                    }
                    else if (sql.endsWith(";(optional)"))
                    {
                        sql = sql.substring(0, sql.length() - 11);
                        execute = true;
                        optional = true;
                    }
                    // append to the statement being built up
                    sb.append(" ").append(sql);
                    // execute, if required
                    if (execute)
                    {
                        // Get the sql
                        sql = sb.toString().trim();
                        
                        // Execute the statement
                        executeStatement(connection, sql, optional, line);
                        
                        // Extract the created table name from the SQL
                        if (sql.toLowerCase().startsWith("create table") == true)
                        {
                            int index = sql.indexOf("(");
                            if (index >= 0)
                            {
                                String tableName = sql.substring(13, index-1);
                                createdTables.add(tableName);
                            }
                        }
                        
                        sb = new StringBuilder(1024);
                    }
                }
            }
            finally
            {
                try { reader.close(); } catch (Throwable e) {}
                try { scriptInputStream.close(); } catch (Throwable e) {}                           
            }
        }
        catch (Exception exception)
        {
            // Remove any tables that where created
            if (createdTables.size() > 0)
            {
                // Drop any tables that might have been created
                String deleteSql = getDropTableSQL(createdTables);
                try { executeStatement(connection, deleteSql, false, 0); } catch (Throwable e) {};
            }
            
            throw exception;
        }
       
        return createdTables;        
    }
    
    /**
     * Generate the drop statement for a given list of tables
     * 
     * @param tables    list of tables
     * @return String   the SQL drop statement
     */
    private String getDropTableSQL(List<String> tables)
    {
        StringBuilder deleteSql = new StringBuilder(1024);
        boolean first = true;
        
        deleteSql.append("drop table ");
        for (String table : tables)
        {
            if (first == true)
            {
                first = false;
            }
            else
            {
                deleteSql.append(", ");
            }
            deleteSql.append(table);
        }
        deleteSql.append(";");
        
        return deleteSql.toString();
    }
    
    /**
     * Subsitutes the values in the provided map within the sql string.  Also removes any inline
     * SQL comments
     * 
     * @param string    the sql string
     * @param values    the map of subsitution values
     * @return String   the resulting string
     */
    private String valueSubstitution(String string, Map<String, String> values)
    {
        String result = string;
        if (string.length() != 0)
        {
            // Check for any in-line comments
            int index = result.indexOf("--");
            if (index >= 0)
            {
                // Remove the remainder of the line
                result = result.substring(0, index);
            }
            index = result.indexOf("//");
            if (index >= 0)
            {
                // Remove the remainder of the line
                result = result.substring(0, index);
            }
            
            for (Map.Entry<String, String> entry : values.entrySet())
            {
                result = result.replace(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
    
    /**
     * Execute the given SQL statement, absorbing exceptions that we expect during
     * schema creation or upgrade.
     */
    private void executeStatement(Connection connection, String sql, boolean optional, int line) throws Exception
    {        
        System.out.println("Executing the following statement: '" + sql + "'");
        
        Statement stmt = connection.createStatement();
        try
        {
            if (logger.isDebugEnabled())
            {
                LogUtil.debug(logger, "Error executing statement " + sql);
            }
            stmt.execute(sql);
            // Write the statement to the file, if necessary
            StringBuilder executedStatements = executedStatementsThreadLocal.get();
            if (executedStatements != null)
            {
                executedStatements.append(sql).append(";\n");
            }
        }
        catch (SQLException e)
        {
            if (optional)
            {
                // it was marked as optional, so we just ignore it
                LogUtil.debug(logger, "Optional statment failed: " + sql + " (" + e.getMessage() + ") " + line);
            }
            else
            {
                LogUtil.error(logger, "Statment failed: " + sql + " (" + e.getMessage() + ") " + line);
                throw e;
            }
        }
        finally
        {
            try { stmt.close(); } catch (Throwable e) {}
        }
    }
}
