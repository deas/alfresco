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
 *
 *  Author  Jon Cox  <jcox@alfresco.com>
 *  File    JNDIConstants.java
 *----------------------------------------------------------------------------*/

package org.alfresco.config;

import java.util.regex.Pattern;

/**
 * Constants to create proper JNDI names for the directories
 * that contain www content.
 * <p>
 * Ultimately, the constants in this fill will go away entirely.
 * This is a stop-gap until we have support multiple virtual AVMHost
 * instances, and a full Spring config (with associated sync to virt server).
 */
public final class JNDIConstants 
{
    public final static String DIR_DEFAULT_WWW_APPBASE = "/" + JNDIConstants.DIR_DEFAULT_WWW + "/" + JNDIConstants.DIR_DEFAULT_APPBASE;
    
    /**
     * Directory used for virtualized web content.
     * Typically, this directory is a transparent overlay 
     * on a shared staging area.
     */
    public final static String  DIR_DEFAULT_WWW     = "www";

    /**
     * Directory in which virtualized webapps reside (e.g.: "ROOT").
     */
    public final static String  DIR_DEFAULT_APPBASE  = "avm_webapps";
    
    /** 
    * Patern to detect if an AVM webapp asset is in META-INF or WEB-INF dir.
    *
    * To use this pattern, you'd do something like this:
    * <pre>
    *
    *      if ( DEFAULT_INF_PATTERN.matcher( yourfile ).find() ) 
    *      { 
    *           //  yourfile is inside an AVM webapp's META-INF or WEB-INF dir
    *      } 
    * </pre>
    *  
    */
    public static final Pattern DEFAULT_INF_PATTERN = 
                  Pattern.compile(
                            "[^:]+:"                          + "/"  +
                            JNDIConstants.DIR_DEFAULT_WWW     + "/"  +
                            JNDIConstants.DIR_DEFAULT_APPBASE + "/"  +
                            "[^/]+"                           + "/"  +
                            "(?:WEB-INF|META-INF)"            + "(?:$|/)",
                            Pattern.CASE_INSENSITIVE);
    
    /**
     * Default virtualization server IP address
     * 
     * @deprecated see VirtualisationServerPreviewURIService.DEFAULT_VSERVER_IP
     */
    public final static String DEFAULT_VSERVER_IP = "127-0-0-1.ip.alfrescodemo.net";
    
    /**
     * Default virtualization server port number
     * 
     * @deprecated see VirtualisationServerPreviewURIService.DEFAULT_VSERVER_PORT
     */
    public final static int DEFAULT_VSERVER_PORT = 8180;
    
    /**
     * Virtualization server sandbox URL pattern
     * 
     * @deprecated see VirtualisationServerPreviewURIService.PREVIEW_SANDBOX_URL
     */
    public final static String PREVIEW_SANDBOX_URL = "http://{0}.www--sandbox.{1}:{2}";
    
    /**
     * Virtualization server asset URL pattern
     * 
     * @deprecated see VirtualisationServerPreviewURIService.PREVIEW_ASSET_URL
     */
    public final static String PREVIEW_ASSET_URL   = "http://{0}.www--sandbox.{1}:{2}{3}";
}
