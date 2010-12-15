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
package org.alfresco.module.mediawikiintegration.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.extensions.config.xml.XMLConfigService;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.springframework.extensions.surf.util.URLEncoder;

/**
 * Action that calls back to the embedded mediaWiki installation and executes
 * an action.
 * 
 * @author Roy Wetherall
 */
public class MediaWikiActionExecuter extends ActionExecuterAbstractBase
{
    /** Action constants */
    public static final String NAME = "mediawiki-action-executer";
    public static final String PARAM_PAGE_TITLE = "title";
    public static final String PARAM_MEDIAWIKI_ACTION = "mediawiki-action";
    public static final String PARAM_PARAMS = "params";
    
    /** MediaWiki URL Args */
    public static final String PARAM_ALFTICKET = "alfTicket";
    public static final String PARAM_ALFUSER = "alfUser";
    public static final String PARAM_ACTION = "action";
    public static final String PARAM_TITLE = "title";
    
    /** URL constants */
    private static final String DEFAULT_SCHEME = "http";
    private static final String DEFAULT_HOSTNAME = "localhost";
    private static final String DEFAULT_PORT = "8080";
    private static final String DEFAULT_PATH = "alfresco";
    private static final String MW_URL = "/php/wiki/index.php";
    
    /** URL to index.php */
    private String url;
    
    /** Authentication service */
    private AuthenticationService authenticationService;
    
    /** Node service */
    private NodeService nodeService;
    
    /** Config service */
    private XMLConfigService configService;
    
    /**
     * Sets the authentication service
     * 
     * @param authenticationService authentication service
     */
    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }
    
    /**
     * Sets the node service
     * 
     * @param nodeService   node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * Set the config service
     * 
     * @param configService     configService
     */
    public void setConfigService(XMLConfigService configService)
    {
        this.configService = configService;
    }
    
    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action, org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        if (this.nodeService.exists(actionedUponNodeRef) == true)
        {
            // Get the parameter values
            String title = (String)action.getParameterValue(PARAM_PAGE_TITLE);
            String mediawikiAction = (String)action.getParameterValue(PARAM_MEDIAWIKI_ACTION);
            Map<String, String> params = (Map<String, String>)action.getParameterValue(PARAM_PARAMS);
            
            // Make the call to execute the mediswiki action
            executeMediaWikiAction(mediawikiAction, title, params);            
        }        
    }
    
    /**
     * Execute a mediawiki action by calling back to the server's mediawiki implementation.
     * 
     * @param action        action name
     * @param pageTitle     page title
     * @param properties    any additional properties to add to the URL
     */
    private void executeMediaWikiAction(String action, String pageTitle, Map<String, String> parameters)
    {
        Map<String, String> postParams = new HashMap<String, String>(5);
        postParams.put(PARAM_ALFTICKET, this.authenticationService.getCurrentTicket());
        postParams.put(PARAM_ALFUSER, this.authenticationService.getCurrentUserName());
        postParams.put(PARAM_ACTION, URLEncoder.encode(action));
        postParams.put(PARAM_TITLE, URLEncoder.encode(pageTitle.replace(" ", "_")));
        for (Map.Entry<String, String> entry : parameters.entrySet())
        {
            postParams.put(entry.getKey(), URLEncoder.encode(entry.getValue()));
        }
        
        try
        {
            String result = post(getIndexURL() , postParams);            
            // TODO check the result for errors
        }
        catch (IOException exception)
        {
            throw new AlfrescoRuntimeException("Unable to execute mediaWiki action", exception);
        }
    }    
    
    /**
     * Gets the mediawiki index.php URL
     * 
     * @return  String  URL
     */
    private String getIndexURL()
    {
        if (url == null)
        {
            // Get the components of the URL
            String scheme = configService.getConfig("Server").getConfigElementValue("scheme");
            if (scheme == null)
            {
                scheme = DEFAULT_SCHEME;
            }
            String hostname = configService.getConfig("Server").getConfigElementValue("hostname");
            if (hostname == null)
            {
                hostname = DEFAULT_HOSTNAME;
            }
            String port = configService.getConfig("Server").getConfigElementValue("port");
            if (port == null)
            {
                port = DEFAULT_PORT;
            }
            String path = configService.getConfig("Server").getConfigElementValue("path");
            if (path == null)
            {
                path = DEFAULT_PATH;
            }
            
            // Construct the URL
            StringBuffer buff = new StringBuffer(64);
            buff.append(scheme)
                .append("://")
                .append(hostname)
                .append(":")
                .append(port)
                .append("/")
                .append(path)
                .append(MW_URL);                   
            this.url = buff.toString();
        }
        return this.url;
    }

    /**
     * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        paramList.add(new ParameterDefinitionImpl(PARAM_PAGE_TITLE, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_PAGE_TITLE)));
        paramList.add(new ParameterDefinitionImpl(PARAM_MEDIAWIKI_ACTION, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_MEDIAWIKI_ACTION)));
        paramList.add(new ParameterDefinitionImpl(PARAM_PARAMS, DataTypeDefinition.ANY, false, getParamDisplayLabel(PARAM_PARAMS)));
    }
    
    /**
     * Post to a given url with the provided parameters
     * 
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    private static String post(String url, Map<String, String> params) throws IOException 
    {
        StringBuffer response = new StringBuffer();
        URL host = new URL(url);
        URLConnection urlConnection;
        BufferedReader inputStream = null;

        StringBuilder data = new StringBuilder();
        for (Map.Entry keyValues : params.entrySet()) 
        {
             data.append(keyValues.getKey() + "=" + URLEncoder.encode(String.valueOf(keyValues.getValue())) + "&");
        }
        OutputStreamWriter outputStream = null;

        try 
        {
            urlConnection = host.openConnection();
            urlConnection.setDoOutput(true);
            outputStream = new OutputStreamWriter(urlConnection.getOutputStream());
            outputStream.write(data.toString());
            outputStream.flush();

            inputStream = readResponse(urlConnection, inputStream, response);
        } 
        catch (IOException e) 
        {
            throw e;
        } 
        finally 
        {
            outputStream.close();
            inputStream.close();
        }

        return response.toString();
    }
    
    /**
     * Read the response from the post
     * 
     * @param urlConnection
     * @param inputStream
     * @param response
     * @return
     * @throws IOException
     */
    private static BufferedReader readResponse(URLConnection urlConnection, BufferedReader inputStream, StringBuffer response) throws IOException 
    {
        inputStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String line;
        while ((line = inputStream.readLine()) != null) 
        {
            response.append(line);
        }
        return inputStream;
    }

}
