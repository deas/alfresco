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
import java.net.URL;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.extensions.webscripts.Description.RequiredAuthentication;


/**
 * Presentation (web tier) Web Script Container
 * 
 * @author davidc
 */
public class PresentationContainer extends AbstractRuntimeContainer implements BeanNameAware
{
    private static final Log logger = LogFactory.getLog(PresentationContainer.class);
    
    private String beanId;
    
    /** cache of server version properties */
    private ServerModel serverModel = null;
    
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    public void setBeanName(String beanId)
    {
        this.beanId = beanId;
    }
    
	/* (non-Javadoc)
	 * @see org.alfresco.web.scripts.RuntimeContainer#executeScript(org.alfresco.web.scripts.WebScriptRequest,
     *      org.alfresco.web.scripts.WebScriptResponse, org.alfresco.web.scripts.Authenticator)
	 */
    public void executeScript(WebScriptRequest scriptReq, WebScriptResponse scriptRes, Authenticator auth)
        throws IOException
    {
        // Handle authentication of scripts on a case-by-case basis.
        // Currently we assume that if a webscript servlet has any authenticator
        // applied then it must be for some kind of remote user auth as supplied.
        WebScript script = scriptReq.getServiceMatch().getWebScript();
        script.setURLModelFactory(getUrlModelFactory());
        Description desc = script.getDescription();
        RequiredAuthentication required = desc.getRequiredAuthentication();
        if (auth == null || RequiredAuthentication.none == required || auth.authenticate(required, false))
        {
            script.execute(scriptReq, scriptRes);
        }
    }
    
    private URLModelFactory urlModelFactory = new DefaultURLModelFactory();
    
    public URLModelFactory getUrlModelFactory()
    {
        return this.urlModelFactory;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Container#getDescription()
     */
    public ServerModel getDescription()
    {
        if (this.serverModel == null)
        {
            Properties props = null;
            URL url = this.getClass().getClassLoader().getResource("surfversion.properties");
            if (url != null)
            {
                try
                {
                    InputStream io = url.openStream();
                    props = new Properties();
                    try
                    {
                        props.load(url.openStream());
                    }
                    finally
                    {
                        io.close();
                    }
                }
                catch (IOException err)
                {
                    logger.warn("Failed to load version properties: " + err.getMessage(), err);
                }
            }
            this.serverModel = new PresentationServerModel(beanId, props);
        }
        return this.serverModel;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.AbstractRuntimeContainer#reset()
     */
    @Override
    public void reset()
    {
        super.reset();
        this.serverModel = null;
    }
    
    /**
     * Presentation Tier Model
     *
     * @author davidc
     */
    private class PresentationServerModel implements ServerModel
    {
        private Properties props = null;
        private String version = null;
        private String id = null;
        
        public PresentationServerModel(String id, Properties props)
        {
            this.id = id;
            this.props = props;
        }
        
        public String getContainerName()
        {
            return getName();
        }

        public String getId()
        {
            return id;
        }

        public String getName()
        {
            return "Spring Web Scripts";
        }

        public String getEdition()
        {
            return (props != null ? "Spring WebScripts " + props.getProperty("surf.edition") : UNKNOWN);
        }

        public int getSchema()
        {
            return (props != null ? Integer.parseInt(props.getProperty("surf.schema")) : -1);
        }

        public String getVersion()
        {
            if (this.version == null)
            {
                if (props != null)
                {
                    StringBuilder version = new StringBuilder(getVersionMajor());
                    version.append(".");
                    version.append(getVersionMinor());
                    version.append(".");
                    version.append(getVersionRevision());
                    
                    String label = getVersionLabel();
                    String build = getVersionBuild();
                    
                    boolean hasLabel = (label != null && label.length() > 0);
                    boolean hasBuild = (build != null && build.length() > 0);
                    
                    // add opening bracket if either a label or build number is present
                    if (hasLabel || hasBuild)
                    {
                       version.append(" (");
                    }
                    
                    // add label if present
                    if (hasLabel)
                    {
                       version.append(label);
                    }
                    
                    // add build number is present
                    if (hasBuild)
                    {
                       // if there is also a label we need a separating space
                       if (hasLabel)
                       {
                          version.append(" ");
                       }
                       
                       version.append(build);
                    }
                    
                    // add closing bracket if either a label or build number is present
                    if (hasLabel || hasBuild)
                    {
                       version.append(")");
                    }
                    
                    this.version = version.toString();
                }
                else
                {
                    this.version = UNKNOWN;
                }
            }
            return this.version;
        }

        public String getVersionBuild()
        {
            return (props != null ? props.getProperty("buildNumber") : UNKNOWN);
        }

        public String getVersionLabel()
        {
            return (props != null ? props.getProperty("surf.version.label") : UNKNOWN);
        }

        public String getVersionMajor()
        {
            return (props != null ? props.getProperty("surf.version.major") : UNKNOWN);
        }

        public String getVersionMinor()
        {
            return (props != null ? props.getProperty("surf.version.minor") : UNKNOWN);
        }

        public String getVersionRevision()
        {
            return (props != null ? props.getProperty("surf.version.revision") : UNKNOWN);
        }
        
        private final static String UNKNOWN = "<unknown>"; 
    }
}
