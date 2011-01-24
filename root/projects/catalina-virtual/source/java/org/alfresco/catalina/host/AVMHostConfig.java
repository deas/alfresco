/*-----------------------------------------------------------------------------
 *  Copyright 2007-2010 Alfresco Software Limited.
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
*
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    AVMHostConfig.java
*----------------------------------------------------------------------------*/

package org.alfresco.catalina.host;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.alfresco.catalina.context.AVMStandardContext;
import org.alfresco.catalina.loader.AVMWebappLoader;
import org.alfresco.catalina.valve.AVMUrlValve;
import org.alfresco.config.JNDIConstants;
import org.alfresco.jndi.AVMFileDirContext;
import org.alfresco.repo.domain.PropertyValue;
import org.alfresco.service.cmr.avm.AVMNodeDescriptor;
import org.alfresco.service.cmr.remote.AVMRemote;
import org.alfresco.service.namespace.QName;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.ContainerBase;
import org.apache.catalina.startup.HostConfig;


/**
*  Configures an {@link AVMHost} at startup time.<br>
*  Note: applications never use AVMHostConfig directly.
*
*  <pre>
*
*               The Repository:  A forest of DAG structures
*               -------------------------------------------
*
*             mysite:   mysite--bob:  yoursite:   yoursite--alice:
*                /           /               /            /
*                |           |               |            |
*               www   <~~~  www             www   <~~~   www
*                |                           |
*            <strong>avm_webapps                avm_webapps
*             /  |  \                    /  |  \
*      my_webapp    ROOT          my_webapp     ROOT
*           |                          |
*       moo.txt                    moo.txt</strong>
*
*  </pre>
*
*  At startup time, the AVMHostConfig will create a "virtualized"
*  version of <tt>avm_webapps</tt> by name-mangling the webapps within
*  each store containing a .dns.{...} property key.    The associated
*  value is the path within this store where webapps are installed
*  (e.g.: "mysite:/www/avm_webapps")
*
*/
public class AVMHostConfig extends HostConfig
{
    protected static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog( AVMHostConfig.class );

    /**
    *  @exclude
    *
    *  Store association between webapp and the classloader it used.
    */
    protected Hashtable<String, ClassLoader> context_classloader_registry_ =
          new Hashtable<String, ClassLoader>();


    // Because deployApps() requires that the AVMRemote has been initialized,
    // force the AVMFileDirContext class to be loaded by calling a cheap
    // static method.  This will ensure that static init for the AVMRemote
    // within AVMFileDirContext runs prior to the deployApps() callback.

    /**
    *  @exclude (hide from javadoc)
    */
    static protected AVMRemote AVMRemote_ =
           AVMFileDirContext.getAVMRemote();


    // Here's where the virual Host's relative appBase parameter
    // is stored for this AVMHost.
    //
    // e.g.: "avm_webapps"
    //
    String AVMHostRelativeAppBase_ = JNDIConstants.DIR_DEFAULT_APPBASE;

    /**
    * @exclude
    *
    * The Java class name of the Context implementation we should use.
    */
    protected String contextClass =
        "org.alfresco.catalina.context.AVMStandardContext";


    /**
    *  Creates an object that initializes an AVMHost
    *
    * @param AVMHostRelativeAppBase
    *           When an AVMHost is created in
    *           $CATALINA_HOME/conf/server.xml,
    *           the value for AVMHostRelativeAppBase
    *           is taken from the appBase attribute
    *           of the &lt;Host&gt; tag (e.g.: "avm_webapps").
    *           Note: even if the value provied within server.xml
    *           is an absolute path, it will be coerced into
    *           a relative path.
    *  <pre>
    *
    *   &lt;Host name="avm.localhost"
    *         className         ="org.alfresco.catalina.host.AVMHost"
    *         appBase           ="avm_webapps"
    *         unpackWARs        ="true"
    *         autoDeploy        ="false"
    *         xmlValidation     ="false"
    *         xmlNamespaceAware ="false"&gt;
    *   &lt;/Host&gt;
    *  </pre>
    */
    //-------------------------------------------------------------------------
    public AVMHostConfig(String AVMHostRelativeAppBase )
    {
        super();

        if (log.isDebugEnabled())
        {
            log.debug("AVMHostConfig: initial AVMHostRelativeAppBase: " +
                      AVMHostRelativeAppBase);
        }
        
        if (AVMHostRelativeAppBase == null )
        {
            // e.g.: "avm_webapps";
            AVMHostRelativeAppBase = JNDIConstants.DIR_DEFAULT_APPBASE;
        }
        if ( AVMHostRelativeAppBase.startsWith("/") )
        {
            AVMHostRelativeAppBase = AVMHostRelativeAppBase.substring(1);
        }
        if ( AVMHostRelativeAppBase.equals("") )
        {
            AVMHostRelativeAppBase =
                JNDIConstants.DIR_DEFAULT_APPBASE;   // "avm_webapps";
        }

        AVMHostRelativeAppBase_ = AVMHostRelativeAppBase;

        if (log.isDebugEnabled())
        {
            log.debug("AVMHostConfig: initial AVMHostRelativeAppBase_: " +
                      AVMHostRelativeAppBase_);
        }
    }


    public void start()
    {

        // deployApps() is called by start() in HostConfig
        super.start();
    }

    public void stop()
    {
        // undeployApps() is called by stop() in HostConfig
        super.stop();

        context_classloader_registry_.clear();
    }



    /**
    *  Does a soft webapp undeploy (removes webapp entries from host).
    */
    protected void undeployApps()
    {
        super.undeployApps();
    }

    /**
     * Deploy applications for any directories or WAR files that are found
     * in our "application root" directory.
     */
    protected void deployApps()
    {
        // This function is only called by HostConfig.
        // Now that the check() function is a no-op,
        // there's no preiodic re-deployment of anything at all.
        // Live updates only occur when done via the custom MBean server.
        // To see this, check out: new Exception("").printStackTrace();

        // Example appBase:
        //    /opt/apache-tomcat-5.5.15/avm_webapps
        File appBase = appBase();

        // Example configBase:
        //    /opt/apache-tomcat-5.5.15/conf/Catalina/avm.localhost

        File configBase = configBase();

        // Deploy XML descriptors from configBase
        deployDescriptors(configBase, configBase.list());

        // Deploy WARs, and loop if additional descriptors are found
        // deployWARs(appBase, appBase.list());

        deployAllAVMwebappsInRepository();
    }


    protected void deployAllAVMwebappsInRepository()
    {
        HashMap<String, AVMWebappDescriptor> webapp_descriptors =
                   new HashMap<String, AVMWebappDescriptor>();

        LinkedList<String>     avm_webapp_paths = new LinkedList<String>();

        long split = System.currentTimeMillis();
        
        try
        {
            // Fetch map of store_name values of the form:
            //
            //       store_name   => { dns_name           =>
            //                         dns_store_path
            //                       },
            //       store_name   => { dns_name           =>
            //                         dns_store_path
            //                       },
            //        ...
            //
            // Example of data:
            //
            //      "mysite--bob" => { ".dns.bob.mysite"  =>
            //                         "mysite--bob:/www/avm_webapps"
            //                       },
            //       ...
            
            Map<String, Map<QName, PropertyValue>> store_dns_entries =
                AVMRemote_.queryStoresPropertyKey(
                    QName.createQName(null,".dns.%"));
            
            if (log.isInfoEnabled())
            {
                log.info("deployAllAVMwebappsInRepository: found "+store_dns_entries.size()+" store dns entries in "+(System.currentTimeMillis()-split)+" ms");
            }
            
            split = System.currentTimeMillis();
            
            for (Map.Entry<String, Map<QName, PropertyValue>>
                 store_dns_entry  : store_dns_entries.entrySet() )
            {
                String  store_name  = store_dns_entry.getKey();

                // Note:  Because the '%' wildcard is used in the query,
                //        and the queryStoresPropertyKey() function is rather
                //        generic, the value associated with store_dns_entry
                //        is a Map, not a simple tuple object.   Therefore, the
                //        somewhat ugly getValue().entrySet().iterator().next()
                //        expression is needed (a bare getValue()won't do).
                //        Because of how the dns names are structured, you'll
                //        always get a map of 1 element, or nothing.

                Map.Entry<QName, PropertyValue> dns_map =
                    store_dns_entry.getValue().entrySet().iterator().next();

                // String dns_name       = dns_map.getKey().getLocalName();
                String    dns_store_path = dns_map.getValue().getStringValue();

                //  Example values:
                //    store_name     =  "mysite--bob"
                //    dns_name       =  ".dns.bob.mysite"
                //    dns_store_path =  "mysite--bob:/www/avm_webapps"

                if (  (dns_store_path == null) ||
                     ! dns_store_path.endsWith( AVMHostRelativeAppBase_ ) )
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("DNS mount point " + dns_store_path +
                                   " does not end with: "             +
                                   AVMHostRelativeAppBase_            +
                                   " ...skipping on this host.");
                    }
                    
                    continue;
                }

                Map<String, AVMNodeDescriptor> webapp_entries = null;
                try
                {
                    // e.g.:   -1, "mysite:/www/avm_webapps"
                    webapp_entries =
                       AVMRemote_.getDirectoryListing(-1, dns_store_path );
                }
                catch (Exception e)     // TODO: just AVMNotFoundException ?
                {
                    continue;
                }
                
                for ( Map.Entry<String, AVMNodeDescriptor> webapp_entry  :
                      webapp_entries.entrySet()
                    )
                {
                    String webapp_name = webapp_entry.getKey();    //  my_webapp
                    
                    if (log.isDebugEnabled())
                    {
                        log.debug("webapp: " + webapp_name);
                    }
                    
                    if ( webapp_name.equalsIgnoreCase("META-INF")  ||
                         webapp_name.equalsIgnoreCase("WEB-INF")
                       )
                    {
                        // Note that:
                        //
                        // [1]   Webapps named META-INF or WEB-INF
                        //       aren't loaded by Tomcat.  See
                        //       references to "META-INF" and "WEB-INF"
                        //       within HostConfig.java for details.
                        //
                        // [2]   The servlet 2.4 spec says that it's
                        //       illegal to serve any content from:
                        //
                        //             <webapp-name>/{META|WEB}-INF
                        //
                        // Are the Tomcat implementors just trying to
                        // make life easier for folks who blindly look
                        // for {META|WEB}-INF *anywhere* in a path?
                        // If so, this is probably ok because these
                        // are rather strange webapp names... but...
                        // is this webapp name restriction actually
                        // mandated by the 2.4 servlet spec?
                        // Review & find out.


                        if (log.isWarnEnabled())
                            log.warn("AVMHostConfig disallows webapps named: " +
                                     webapp_name);

                        continue;
                    }

                    // TODO:  Request some changes/additions to AVMRemote
                    //
                    //        Suppose A overlays B overlays C
                    //
                    //        Let 'x' denote content
                    //            ' ' denote no conent
                    //            '?' denote don't care
                    //
                    //                                                      Cases
                    //                                                     1   2   3
                    //                                                   +---+---+---+
                    //  A  mysite--alice--preview:/www/avm_webapps/foo   |   |   | x |
                    //  B           mysite--alice:/www/avm_webapps/foo   |   | x | ? |
                    //  C                  mysite:/www/avm_webapps/foo   | x | ? | ? |
                    //                                                   -------------
                    //
                    //                                                      Answers
                    //                                                     1   2   3
                    //                                                   +-----------+
                    //     Function P :  What  am I really fetching?     | C | B | A |
                    //     Function Q :  What  am I directly overlaying? | B | B | B |
                    //                                                   +-----------+

                    String webapp_entry_path = 
                               webapp_entry.getValue().getPath();


                    if (log.isDebugEnabled())
                    {
                        log.debug("webapp_entry_path: " + webapp_entry_path);
                    }

                    String webapp_entry_indirection_path = 
                               AVMRemote_.getIndirectionPath( -1, webapp_entry_path );

                    if (log.isDebugEnabled())
                    {
                        log.debug( "AVMWebappDescriptor: -1"      + "," +
                                    store_name                    + "," + 
                                    webapp_entry_indirection_path + "," + 
                                    dns_store_path                + "," +
                                    webapp_name);
                    }

                    AVMWebappDescriptor webapp_desc =
                        new AVMWebappDescriptor(
                        -1,             // version
                        store_name,     // mysite
                        webapp_entry_indirection_path,
                            // this gets the indirection path even if,
                            // physically, the path is not a layered
                            // directory, as long as the
                            // path is in a layered context.
                        dns_store_path, // mysite:/www/avm_webapps
                        webapp_name     // my_webapp
                    );

                    webapp_descriptors.put( webapp_desc.getContextPath(),
                                            webapp_desc);
                }
            }

            if (log.isInfoEnabled())
            {
                log.info("deployAllAVMwebappsInRepository: found "+webapp_descriptors.size()+" webapp descriptors in "+(System.currentTimeMillis()-split)+" ms");
            }
        }
        catch (Exception e)
        {
            // TODO:  figure out if there's anything more that can be done here
            if (log.isErrorEnabled())
                log.error("deployAllAVMwebappsInRepository failed: " +
                                    e.getMessage() );
        }
        
        // Do topo sort of webapps according to layering config, and deploy
        deployAVMWebappsInDependencyOrder( webapp_descriptors );
        
        return;
    }

    /**
    *  Updates a virtual webapps; if the isRecursive flag is set,
    *  all dependent webapps are also updated (i.e.: webapps that use
    *  the one being updated as their "background" via transparency).
    *  <p>
    *  For example, pathToWebapp might look something like this:
    *  <tt>mysite--bob:/www/avm_webapps/ROOT</tt>.  The value
    *  of 'version' is typically -1 (which corresponds to HEAD).
    */
    public boolean  updateVirtualWebapp( int     version,
                                         String  pathToWebapp,
                                         boolean isRecursive)
    {
        long start = System.currentTimeMillis();
        
        if (log.isDebugEnabled())
        {
            log.debug("updateVirtualWebapp: " + version + ", " + pathToWebapp + " (isRecursive=" + isRecursive + ")");
        }

        String context_name = AVMUrlValve.GetContextNameFromStorePath(
                                   version, pathToWebapp );

        if ( context_name == null )
        {
            if (log.isWarnEnabled())
                log.warn("webapp update failed; bad webapp path: " +
                          pathToWebapp );

            return false;
        }

        this.deployed.remove( context_name );

        AVMStandardContext context =
           (AVMStandardContext) host.findChild( context_name );

        if ( context != null )
        {
            host.removeChild( context );

            if (log.isInfoEnabled())
            {
                log.info("updateVirtualWebapp: temporarily removed webapp: " + context_name);
            }
        }


        // Determine appBase and webapp_name from the path

        int first_colon = pathToWebapp.indexOf(':');
        int last_slash  = pathToWebapp.lastIndexOf('/');
        if ((first_colon < 0) ||  (last_slash < 0))
        {
            if (log.isErrorEnabled())
                log.error("Not a valid path to a webapp: " + pathToWebapp);

            return false;
        }

        String store_name    = pathToWebapp.substring(0, first_colon);
        String store_relpath = pathToWebapp.substring(first_colon + 1);
        String avm_appBase   = pathToWebapp.substring(0, last_slash);
        String webapp_name   = pathToWebapp.substring(last_slash + 1);


        // If this webapp is the child of some other webapp,
        // calculate the name of the associated parent context.

        String indirection_path = null;
        try
        {
            indirection_path = AVMRemote_.getIndirectionPath(
                                          version,
                                          pathToWebapp);
        }
        catch (Exception e)
        {
            if (log.isErrorEnabled())
                log.error("webapp update failed; bad webapp path: " +
                           pathToWebapp);

            return false;
        }


        // Only call a webapp our "parent" if we're shadowing it, **and**
        // it's in a different virtual store; otherwise, our parent is null.
        String parent_context_name = null;

        if (indirection_path != null)
        {
            int parent_index_store_tail = indirection_path.indexOf(':');
            if ( parent_index_store_tail > 0 )
            {
                String parent_store_name =
                    indirection_path.substring(0,parent_index_store_tail);

                if ( ! parent_store_name.equals( store_name ) )
                {
                    parent_context_name =
                        AVMUrlValve.GetContextNameFromStoreName(
                            version,
                            parent_store_name,
                            webapp_name
                        );
                }
            }
        }

        deployAVMWebapp(
           version,               // -1
           avm_appBase,           // store-3:/www/avm_webapps
           webapp_name,           // my_webapp
           context_name,          // e.g.:   /$-1$store-3$my_webapp
           parent_context_name);  // parent_context_path possibly null



        boolean is_sucessful = true;

        if (isRecursive)
        {
           ArrayList< LinkedList<String> >  store_hierarchy =  // never null
                getDependentWebappStores( store_name );

            // Walk the hierarchy from lowest to highest
            // This ensures that by the time any classloader
            // is reloaded, it's parent/background classloader
            // has *already* been reloaded.  Therefore, a user
            // can't accidentally poison the fresh classloader
            // by requesting somthing from a stale background.

            for ( LinkedList<String> store_list : store_hierarchy )
            {
                if ( store_list == null )  { break; }

                for (String dep_store : store_list )
                {
                    is_sucessful =
                        updateVirtualWebapp(
                                version,
                                dep_store + ":" + store_relpath,
                                false
                        )
                        && is_sucessful;
                }
            }
        }
        
        if (log.isInfoEnabled())
        {
            log.info("updateVirtualWebapp: " + version + ", " + pathToWebapp + " (isRecursive=" + isRecursive + ") updated in "+(System.currentTimeMillis()-start)+" ms");
        }
        
        return is_sucessful;
    }




    /**
    *  Updates all virtual webapps within an AVM store; if the isRecursive
    *  flat is set, all dependent webapps are also updated (i.e.: webapps
    *  that use the one being updated as their "background" via transparency).
    *  <p>
    *  For example, storePath might look something like this:
    *  <tt>mysite--bob:/www/avm_webapps/ROOT</tt>.  The value
    *  of 'version' is typically -1 (which corresponds to HEAD).
    *  If the store has other webapps besides ROOT, they are
    *  updated as well.
    */
    public boolean  updateAllVirtualWebapps( int     version,
                                             String  storePath,
                                             boolean isRecursive
                                           )
    {
        long start = System.currentTimeMillis();
        
        if (log.isDebugEnabled())
        {
            log.debug("updateAllVirtualWebapps: " + version + ", " + storePath + " (isRecursive=" + isRecursive + ")");
        }

        String store_name;     // e.g.: mysite--bob
        int index_store_tail = storePath.indexOf(':');

        if ( index_store_tail > 0 )
        {
            store_name = storePath.substring(0,index_store_tail);
        }
        else
        {
            if (log.isErrorEnabled())
               log.error("webapp update failed; bad store path: " +
                         storePath );

           return false;
        }

        // e.g.:  /www/avm_webapps/ROOT
        String store_relpath =
               storePath.substring(index_store_tail+1, storePath.length());


        // TODO: When the GUI supports inviting a user to a single webapp,
        // there will be no need to update *all* webapps in a web project.
        // However, there's no way in the GUI right now to limit the
        // scope of the inviation, so updating one webapp in a project
        // means *all* webapps must be updated in the store containing
        // the webapp mentioned by storePath.


        // The app base is always just below the www dir, and the www dir
        // is a child of the root  "/" dir.  Use this invariant rather than
        // relying upon the client handing this function a webapp name or
        // the app base dir;  it might give something longer such as the
        // absolute path to a changed file in a submit.
        // For example:   mysite:/www/avm_webapps/ROOT/WEB-INF/web.xml


        // Note: index_store_tail+1 points at final slash in:  "mysite:/"
        // Skip this and point at final slash in:              "mysite:/www/"
        // by using searching for / at index_store_tail+2
        //
        int index_www_tail = storePath.indexOf('/', index_store_tail + 2);

        if ( index_www_tail < 0 )
        {
            if (log.isErrorEnabled())
                log.error("webapp update failed; bad store path: " + storePath);

            return false;
        }

        // Point at final slash in:  "mysite:/www/avm_webapps/"
        int index_app_base_tail =  storePath.indexOf('/', index_www_tail + 1 );

        // Allow a raw app_base to be provided as the storePath
        if (index_app_base_tail < 0) {index_app_base_tail = storePath.length();}

        if ( index_app_base_tail == index_www_tail )
        {
            if (log.isErrorEnabled())
                log.error("webapp update failed; bad store path: " + storePath);

            return false;
        }

        // If storePath is:  mysite--bob:/www/avm_webapps
        //              or:  mysite--bob:/www/avm_webapps/ROOT/
        //              or:  mysite--bob:/www/avm_webapps/ROOT/WEB-INF/...
        // the app_base is:  mysite--bob:/www/avm_webapps

        String avm_appBase = storePath.substring(0,index_app_base_tail);
        Map<String, AVMNodeDescriptor> webapp_entries = null;

        try
        {
            webapp_entries =
                AVMRemote_.getDirectoryListing( version, avm_appBase );
        }
        catch (Exception e)
        {
            if (log.isErrorEnabled())
            {
                log.error("webapp update failed; could not list: " +
                           avm_appBase );
                log.error( e.getMessage() );
            }

            return false;
        }

        for ( Map.Entry<String, AVMNodeDescriptor> webapp_entry  :
              webapp_entries.entrySet()
            )
        {
            String webapp_name = webapp_entry.getKey();   // my_webapp

            if (log.isDebugEnabled())
                log.debug("webapp found: " + webapp_name);

            String webapp_storePath =  avm_appBase + "/" + webapp_name;

            String context_name = AVMUrlValve.GetContextNameFromStorePath(
                                       version, webapp_storePath );

            if ( context_name == null )
            {
                if (log.isWarnEnabled())
                    log.warn("webapp update failed; bad store path: " +
                              webapp_storePath );

                return false;
            }

            this.deployed.remove( context_name );

            AVMStandardContext context =
                (AVMStandardContext) host.findChild( context_name );

            if ( context != null )
            {
                host.removeChild( context );

                if (log.isInfoEnabled())
                    log.info("temporarily removed webapp: " + context_name);
            }

            // If this webapp is the child of some other webapp,
            // calculate the name of the associated parent context.

            String indirection_path = AVMRemote_.getIndirectionPath(
                                          version,
                                          webapp_entry.getValue().getPath()
                                      );

            // Only call a webapp our "parent" if we're shadowing it, **and**
            // it's in a different virtual store; otherwise, our parent is null.
            String parent_context_name = null;

            if (indirection_path != null)
            {
                int parent_index_store_tail = indirection_path.indexOf(':');
                if ( parent_index_store_tail > 0 )
                {
                    String parent_store_name =
                        indirection_path.substring(0,parent_index_store_tail);

                    if ( ! parent_store_name.equals( store_name ) )
                    {
                        parent_context_name =
                            AVMUrlValve.GetContextNameFromStoreName(
                                version,
                                parent_store_name,
                                webapp_name
                            );
                    }
                }
            }

            deployAVMWebapp(
               version,               // -1
               avm_appBase,           // store-3:/www/avm_webapps
               webapp_name,           // my_webapp
               context_name,          // e.g.:   /$-1$store-3$my_webapp
               parent_context_name);  // parent_context_path possibly null
        }

        boolean is_sucessful = true;

        if (isRecursive)
        {
           ArrayList< LinkedList<String> >  store_hierarchy =  // never null
                getDependentWebappStores( store_name );

            // Walk the hierarchy from lowest to highest
            // This ensures that by the time any classloader
            // is reloaded, it's parent/background classloader
            // has *already* been reloaded.  Therefore, a user
            // can't accidentally poison the fresh classloader
            // by requesting somthing from a stale background.
           
            for ( LinkedList<String> store_list : store_hierarchy )
            {
                if ( store_list == null )  { break; }
                
                for (String dep_store : store_list )
                {
                    is_sucessful =
                        updateAllVirtualWebapps(
                                version,
                                dep_store + ":" + store_relpath,
                                false
                        )
                        && is_sucessful;
                }
            }
        }
        
        if (log.isInfoEnabled())
        {
            log.info("updateAllVirtualWebapps: " + version + ", " + storePath + " (isRecursive=" + isRecursive + ") updated in "+(System.currentTimeMillis()-start)+" ms");
        }
        
        return is_sucessful;
    }


    /**
    * Fetches an array of lists of stores that are dependent upon 'store_name'.
    * The initial element in this array are all the stores that are 1 hop away
    * (e.g.: "mysite--bob" is 1 hop away from "mysite"), the subsequent element
    * containts all stores that are 2 hops away (e.g.: "mysite--preview" is
    * two hops away from "mysite"... and so on.
    */
    protected ArrayList< LinkedList<String> >
    getDependentWebappStores( String store_name)
    {
        long split = System.currentTimeMillis();
        
        ArrayList< LinkedList<String>> store_hierarchy =
            new ArrayList< LinkedList<String> >(8);       // overkill, but cheap
        
        boolean lazyDeploy = true;
        if (host instanceof AVMHost)
        {
            lazyDeploy = ((AVMHost)host).getLazyDeploy();
        }
        
        // It's nice to see IBM publish an article like this:
        // http://www-128.ibm.com/developerworks/java/library/j-jtp01255.html
        // Java generics leave a lot to be desired.
        //
        //  DUKE: I know a life of crime has led me to this sorry fate,
        //        and yet, I blame society.  Society made me what I am.
        //
        //        -- "Repo Man" (1984, by Alex Cox... no relation, I think)


        // Figure out which stores list this one as a background store
        //
        //   child_store_name   => { .background-layer.<this_store_name>   =>
        //                            <distance>
        //                         },
        //        ...
        //
        // Example of data:
        //
        //      "mysite--bob" =>          { ".background-layer.mysite"  =>
        //                                  1
        //                                },
        //      "mysite--bob--preview" => { ".background-layer.mysite"  =>
        //                                  2
        //                                },

        Map<String, Map<QName, PropertyValue>> store_child_entries =
            AVMRemote_.queryStoresPropertyKey(
                QName.createQName(null,".background-layer." + store_name)
            );
        
        if (log.isDebugEnabled())
        {
            log.debug("getDependentWebappStores: for '"+store_name+"' found "+store_child_entries.size()+" store child entries in "+(System.currentTimeMillis()-split)+" ms");
        }
        
        split = System.currentTimeMillis();
        
        for ( Map.Entry<String, Map<QName, PropertyValue>> store_child_entry  :
              store_child_entries.entrySet()
            )
        {
            String  child_store_name  = store_child_entry.getKey();
            
            boolean isStoreDeployed = true; // assume deployed
            if (lazyDeploy)
            {
                isStoreDeployed = false; // note: only checks AVM store here (not webapps)
                
                Container children[] = host.findChildren();
                for (int i = 0; i < children.length; i++) 
                {
                    if (children[i] instanceof AVMStandardContext)
                    {
                        AVMStandardContext sc = (AVMStandardContext)children[i];
                        String ctxName = sc.getName();
                        if (ctxName.startsWith(AVMUrlValve.GetContextNameFromStoreName(-1, child_store_name, "")))
                        {
                            isStoreDeployed = true;
                        }
                    }
                }
            }
            
            if (! isStoreDeployed)
            {
                continue;
            }
            
            Map.Entry<QName, PropertyValue> child_map =
                store_child_entry.getValue().entrySet().iterator().next();

            int distance = (int) child_map.getValue().getLongValue();

            LinkedList<String> store_list;

            if (  distance > store_hierarchy.size() )
            {
                for (int i= store_hierarchy.size(); i<distance; i++)
                {
                    store_hierarchy.add( new LinkedList<String>() );
                }
            }
            // Index in store_hierarchy is 0-based (distance 1 == index 0)
            store_list = store_hierarchy.get( distance -1 );
            store_list.add( child_store_name );
        }
        
        if (log.isInfoEnabled())
        {
            log.info("getDependentWebappStores: for '"+store_name+"' found "+store_hierarchy.size()+" stores "+(lazyDeploy ? "(lazy deploy is enabled) " : "")+"in "+(System.currentTimeMillis()-split)+" ms");
        }
        
        return store_hierarchy;
    }

    /**
    *  Removes virtual webapp from the store, and removes the
    *  corresponding work directory.  This function is the logical
    *  compliment of updateVirtualWebapp.
    */
    public boolean  removeVirtualWebapp( int     version,
                                         String  pathToWebapp,
                                         boolean isRecursive
                                       )
    {
        long start = System.currentTimeMillis();
        
        if (log.isDebugEnabled())
        {
            log.debug("removeVirtualWebapp: " + version + ", " + pathToWebapp + " (isRecursive=" + isRecursive + ")");
        }

        int first_colon = pathToWebapp.indexOf(':');
        int last_slash  = pathToWebapp.lastIndexOf('/');
        if ((first_colon < 0) ||  (last_slash < 0))
        {
            if (log.isErrorEnabled())
                log.error("Not a valid path to a webapp: " + pathToWebapp);

            return false;
        }
        String store_name    = pathToWebapp.substring(0, first_colon);
        String store_relpath = pathToWebapp.substring(first_colon + 1);
        String avm_appBase   = pathToWebapp.substring(0, last_slash);
        String webapp_name   = pathToWebapp.substring(last_slash + 1);

        boolean is_sucessful = true;

        if (isRecursive)
        {
           ArrayList< LinkedList<String> >  store_hierarchy =   // never null
                getDependentWebappStores( store_name );

            // Walk the hierarchy from highest to lowest.
            // This prevents someone from accessing a store
            // that has had its background removed.

            for (int i= store_hierarchy.size() -1; i>=0; i--)
            {
                LinkedList<String> store_list =
                   store_hierarchy.get(i);

                if ( store_list == null )  { continue; }

                for (String dep_store : store_list )
                {
                    is_sucessful =
                        removeVirtualWebapp(
                                version,
                                dep_store + ":" + store_relpath,
                                false
                        )
                        && is_sucessful;
                }
            }
        }


        String context_name = AVMUrlValve.GetContextNameFromStorePath(
                                   version, pathToWebapp );

        if ( context_name == null )
        {
            if (log.isWarnEnabled())
                log.warn("remove webapp failed; bad webapp path: " +
                          pathToWebapp );

            return false;
        }

        this.deployed.remove( context_name );

        if (log.isDebugEnabled())
            log.debug("remove webapp removed: " + context_name);

        AVMStandardContext context =
            (AVMStandardContext) host.findChild( context_name );

        if ( context != null )
        {
            ServletContext servletContext =
                context.getServletContext();

            File workDir =
                (File) servletContext.getAttribute(Globals.WORK_DIR_ATTR);

            host.removeChild( context );

            boolean clean_ok = CleanDir(workDir, true);

            if ( ! clean_ok )
            {
                if (log.isWarnEnabled())
                    log.warn("Could not remove entire work directory: " +
                              workDir.getAbsolutePath());
            }
            else
            {
                if (log.isDebugEnabled())
                    log.debug("Remove work directory: " +
                              workDir.getAbsolutePath());
            }

            if (log.isInfoEnabled())
                log.info("removed webapp: " + context_name);
        }
        
        if (log.isInfoEnabled())
        {
            log.info("removeVirtualWebapp: " + version + ", " + pathToWebapp + " (isRecursive=" + isRecursive + ") removed in "+(System.currentTimeMillis()-start)+" ms");
        }

        return is_sucessful;
    }



    /**
    *  Removes all virtual webapps from the store, and removes the
    *  corresponding work directory.  This function is the logical
    *  compliment of updateAllVirtualWebapps.
    */
    public boolean  removeAllVirtualWebapps( int     version,
                                             String  storePath,
                                             boolean isRecursive
                                            )
    {
        long start = System.currentTimeMillis();
        
        if (log.isDebugEnabled())
        {
            log.debug("removeAllVirtualWebapps: " + version + ", " + storePath + " (isRecursive=" + isRecursive + ")");
        }

        boolean is_sucessful = true;
        String  store_name;                           // e.g.: mysite--bob

        int index_store_tail = storePath.indexOf(':');
        if ( index_store_tail > 0 )
        {
            store_name = storePath.substring(0,index_store_tail);
        }
        else
        {
           if (log.isErrorEnabled())
               log.error("remove webapp failed; bad store path: " + storePath );

           return false;
        }

        // e.g.:  /www/avm_webapps/ROOT
        String store_relpath =
               storePath.substring(index_store_tail+1, storePath.length());

        // Note: index_store_tail+1 points at final slash in:  "mysite:/"
        // Skip this and point at final slash in:              "mysite:/www/"
        // by using searching for / at index_store_tail+2
        //
        int index_www_tail = storePath.indexOf('/', index_store_tail + 2);

        if ( index_www_tail < 0 )
        {
            if (log.isErrorEnabled())
                log.error("remove webapp failed; bad store path: " + storePath);

            return false;
        }

        // Point at final slash in:  "mysite:/www/avm_webapps/"
        int index_app_base_tail =  storePath.indexOf('/', index_www_tail + 1 );

        // Allow a raw app_base to be provided as the storePath
        if (index_app_base_tail < 0) {index_app_base_tail = storePath.length();}

        if ( index_app_base_tail == index_www_tail )
        {
            if (log.isErrorEnabled())
                log.error("remove webapp failed; bad store path: " + storePath);

            return false;
        }


        if (isRecursive)
        {
           ArrayList< LinkedList<String> >  store_hierarchy =  // never null
                getDependentWebappStores( store_name );

            // Walk the hierarchy from highest to lowest.
            // This prevents someone from accessing a store
            // that has had its background removed.

            for (int i= store_hierarchy.size() -1; i>=0; i--)
            {
                LinkedList<String> store_list =
                   store_hierarchy.get(i);

                if ( store_list == null )  { continue; }

                for (String dep_store : store_list )
                {
                    is_sucessful =
                        removeAllVirtualWebapps(
                                version,
                                dep_store + ":" + store_relpath,
                                false
                        )
                        && is_sucessful;
                }
            }
        }

        // If    storePath looks like:   mysite--bob:/www/avm_webapps/ROOT
        // then  app_base  looks like:   mysite--bob:/www/avm_webapps

        String avm_appBase = storePath.substring(0,index_app_base_tail);
        Map<String, AVMNodeDescriptor> webapp_entries = null;

        if (log.isDebugEnabled())
            log.debug("remove webapp listing: " + avm_appBase);

        try
        {
            webapp_entries =
                AVMRemote_.getDirectoryListing( version, avm_appBase );
        }
        catch (Exception e)
        {
            if (log.isErrorEnabled())
            {
                log.error("remove webapp failed; could not list: " +
                           avm_appBase );
                log.error( e.getMessage() );
            }

            return false;
        }

        for ( Map.Entry<String, AVMNodeDescriptor> webapp_entry  :
              webapp_entries.entrySet()
            )
        {
            String webapp_name = webapp_entry.getKey();   // my_webapp

            if (log.isDebugEnabled())
                log.debug("remove webapp found: " + webapp_name);

            String webapp_storePath =  avm_appBase + "/" + webapp_name;

            String context_name = AVMUrlValve.GetContextNameFromStorePath(
                                       version, webapp_storePath );

            if ( context_name == null )
            {
                if (log.isWarnEnabled())
                    log.warn("remove webapp failed; bad store path: " +
                              webapp_storePath );

                return false;
            }

            this.deployed.remove( context_name );

            if (log.isDebugEnabled())
                log.debug("remove webapp removed: " + context_name);

            AVMStandardContext context =
                (AVMStandardContext) host.findChild( context_name );

            if ( context != null )
            {
                ServletContext servletContext =
                    context.getServletContext();

                File workDir =
                    (File) servletContext.getAttribute(Globals.WORK_DIR_ATTR);

                host.removeChild( context );

                boolean clean_ok = CleanDir(workDir, true);

                if ( ! clean_ok )
                {
                    if (log.isWarnEnabled())
                        log.warn("Could not remove entire work directory: " +
                                  workDir.getAbsolutePath());
                }
                else
                {
                    if (log.isDebugEnabled())
                        log.debug("Remove work directory: " +
                                  workDir.getAbsolutePath());
                }

                if (log.isInfoEnabled())
                    log.info("removed webapp: " + context_name);
            }
        }
        
        if (log.isInfoEnabled())
        {
            log.info("removeAllVirtualWebapps: " + version + ", " + storePath + " (isRecursive=" + isRecursive + ") removed in "+(System.currentTimeMillis()-start)+" ms");
        }
        
        return is_sucessful;
    }


    /**
     * Remove all files and subdirs of dir.
     * If deleteDir is true, dir itself is deleted.
     *
     * @param dir File object representing the directory to be cleaned
     */
    static boolean CleanDir(File dir, boolean deleteDir)
    {
        boolean overall_status = true;
        boolean status;

        String files[] = dir.list();
        if (files == null) { files = new String[0]; }

        for (int i = 0; i < files.length; i++)
        {
            File file = new File(dir, files[i]);

            if (file.isDirectory()) { status = CleanDir(file, true); }
            else                    { status = file.delete(); }

            overall_status = overall_status && status;
        }

        if  ( deleteDir )
        {
            overall_status = dir.delete() && overall_status;
        }
        return overall_status;
    }




    protected void
    deployAVMWebappsInDependencyOrder( HashMap<String,
                                       AVMWebappDescriptor> webapp_descriptors)
    {
        long split = System.currentTimeMillis();
        
        boolean lazyDeploy = false;
        if (host instanceof AVMHost)
        {
            lazyDeploy = ((AVMHost)host).getLazyDeploy();
        }
        
        boolean deployDependentsRecursively = (! lazyDeploy);
        
        // First, gather information regarding webapp dependency.
        // If webapp 'A' overlays webapp 'B', then 'A' depends on 'B'.
        //
        for ( AVMWebappDescriptor desc : webapp_descriptors.values() )
        {
            int    version    = desc.version_;
            String store_path = desc.avm_appBase_ + "/" + desc.webapp_leafname_;

            if ( desc.indirection_name_ != null )
            {
                if( log.isDebugEnabled() )
                {
                    log.debug("Indirection name for:  " +  
                               store_path  + " is: "+ desc.indirection_name_ );
                }

                // This webapp dir is shadowing something in another layer.
                // By convention, webapp overlays always span 2 different
                // AVM stores because each AVM store is associated with
                // a distinct DNS name (for webapp virtualization purposes).
                //
                // Therfore, once the invariant condition (different stores)
                // is verified, an inter-webapp dependency is set by
                // making this webapp a "child" of the "parent" it is
                // overlaying in the other repository.

                int index = desc.indirection_name_.indexOf(':');
                if ( index > 0 )
                {
                    String parent_store =
                        desc.indirection_name_.substring(0,index);

                    if( log.isDebugEnabled() )
                    {
                        log.debug("parent_store for:  " +  
                                   desc.store_name_  + " is: "+ parent_store);
                    }

                    if ( ! parent_store.equals( desc.store_name_ ) )
                    {
                        // See comment in previous invocation of
                        // setWebappDependency.

                        desc.setWebappDependency( webapp_descriptors,
                                                  parent_store);
                    }
                }
            }
            else
            {
                if( log.isDebugEnabled() )
                {
                    log.debug("Indirection name is null for: " +  store_path);
                }
            }
        }
        
        if (log.isInfoEnabled())
        {
            log.info("deployAllAVMwebappsInRepository: get "+webapp_descriptors.size()+" webapp descriptors (w/ dependencies) in "+(System.currentTimeMillis()-split)+" ms");
        }
        
        split = System.currentTimeMillis();
        
        // Now each descriptor has a (possibly empty) set of children
        // that depend upon it, and a (possibly null) getParentContextPath()
        // indicating the context path corresponding to the webapp that
        // that it depends on.
        
        int noDepsCnt = 0;
        
        for ( AVMWebappDescriptor desc : webapp_descriptors.values() )
        {
            if ( desc.getParentContextPath() != null ) { continue; }

            // This is a webapp with no dependencies on any other.
            // For example, a baseline "staging" webapp.
            //
            // The following function will call each
            // non-dependent webapp, and deploy its set
            // of dependent webapps recursively.
            //
            // Therefore, webapps can register their classloader
            // with the Host, so that dependents can look it up
            // without requring any forward refs.
            
            deployAVMWebappDescriptorTree(desc, deployDependentsRecursively);
            
            noDepsCnt++;
        }
        
        if (log.isInfoEnabled())
        {
            log.info("deployAllAVMwebappsInRepository: deployed "+(deployDependentsRecursively ? webapp_descriptors.size() : " (not dependents - lazy deploy is enabled) "+noDepsCnt)+" webapp descriptors in "+(System.currentTimeMillis()-split)+" ms");
        }
    }

    protected void
    deployAVMWebappDescriptorTree( AVMWebappDescriptor desc)
    {
        deployAVMWebappDescriptorTree(desc, true);
    }
    
    private void
    deployAVMWebappDescriptorTree( AVMWebappDescriptor desc, boolean deployDependentsRecursively)
    {
        deployAVMWebapp( desc.version_,
                         desc.avm_appBase_,
                         desc.webapp_leafname_,
                         desc.getContextPath(),
                         desc.getParentContextPath()
                       );
        
        if (deployDependentsRecursively)
        {
            for ( AVMWebappDescriptor dependent :  desc.dependents_)
            {
                deployAVMWebappDescriptorTree( dependent );
            }
        }
    }


    /**
    * Deploy an AVM-based webapp.
    *
    * This is a webapp-validating wrapper for the lower-level
    * deployment function deployAVMdirectory.
    */
    protected void deployAVMWebapp( int     version,
                                    String  avm_appBase,
                                    String  webapp_leafname,
                                    String  context_path,
                                    String  parent_context_path
                                  )
    {
        // Examle params:
        //     version:         -1
        //     avm_appBase:     mysite--bob:/www/avm_webapps
        //     webapp_leafname  my_webapp
        //     context_path     /$-1$mysite--bob$my_webapp


        // Don't deploy if serviced elsewhere.
        if (isServiced(context_path)) { return; }


        // TODO:  Clustering, failover, and distributed management
        //
        // General idea:
        //        Make all virt servers register a list of regexes
        //        corresponding to what they will & won't be willing
        //        to virtualize.   The GUI then hands out links to a
        //        servlet that does redirects to the proper virt server
        //        for the user's context.  This can include webapp
        //        specified rules for load balancing and/or work
        //        partitioning by regex type. It can also base decision
        //        on who the user is and/or what preference the user
        //        has specified (if any).  By default, all virt servers
        //        that register with the alfresco webapp can go in a global
        //        pool (this way, at least you get some load balancing
        //        by default).  If the servlet periodically does a
        //        heartbeat check on registered virt servers, then you
        //        can do graceful corse-grained failover (at the ip level)
        //        when the user clicks on the asset's "eyeball" icon in
        //        the GUI (as well as type-specific work routing).  From
        //        there, normal failover/clustering  could be done.
        //        This 2-stage failover avoids having a huge number
        //        of users proxy content through a single server.


        if ( webapp_leafname.equalsIgnoreCase("META-INF")  ||
             webapp_leafname.equalsIgnoreCase("WEB-INF")
           )
        {
            // Note that:
            //
            // [1]   Webapps named META-INF or WEB-INF
            //       aren't loaded by Tomcat.  See
            //       references to "META-INF" and "WEB-INF"
            //       within HostConfig.java for details.
            //
            // [2]   The servlet 2.4 spec says that it's
            //       illegal to serve any content from:
            //
            //             <webapp-name>/{META|WEB}-INF
            //
            // Are the Tomcat implementors just trying to
            // make life easier for folks who blindly look
            // for {META|WEB}-INF *anywhere* in a path?
            // If so, this is probably ok because these
            // are rather strange webapp names... but...
            // is this webapp name restriction actually
            // mandated by the 2.4 servlet spec?
            // Review & find out.

            if (log.isWarnEnabled())
                log.warn("Disallow webapps named: " +
                          webapp_leafname);

            return;
        }


        // TODO:   Determine the best policy for how strict we
        //         should be when it comes to validating a webapp's
        //         structure.   For example, we could insist on
        //         the webapp having a META-INF and WEB-INF subdir
        //         right here, or defer it.  Explore the tradeoffs.
        //
        // For now, AVMHostConfig will be non-strict
        // Because of when this function is called,
        // webapp_entries is always empty.  Ugh.

        // Here's an example of something we might do to be "strict":
        // Note however that it costs an extra remote function call
        // per webapp (times the number of virtual webapps, in the
        // event of a reload)... so it's not cheap either.
        //
        // Ensure that the webapp has as META-INF  and a WEB-INF subdir
        // Get a directory listing of webapp
        //
        //   boolean saw_meta_inf = false;
        //   boolean saw_web_inf  = false;
        //
        //   Map<String, AVMNodeDescriptor> webapp_entries = null;
        //   try
        //   {
        //       webapp_entries = AVMRemote_.getDirectoryListing(
        //                           version,
        //                           avm_appBase + "/" + webapp_leafname );
        //   }
        //   catch (Exception e) { return; }
        //
        //
        //
        //   for ( Map.Entry<String, AVMNodeDescriptor> entry  :
        //         webapp_entries.entrySet()
        //       )
        //   {
        //       String            entry_name  = entry.getKey();   //  my_webapp
        //       AVMNodeDescriptor entry_value = entry.getValue();
        //
        //       System.out.println("Entry name: -->" + entry_name + "<----");
        //       System.out.println("Is dir: " +  entry_value.isDirectory()  );
        //
        //       if  ( entry_name.equalsIgnoreCase("META-INF") &&
        //             entry_value.isDirectory()
        //           )
        //       {
        //           saw_meta_inf = true;
        //       }
        //       else if  ( entry_name.equalsIgnoreCase("WEB-INF") &&
        //                  entry_value.isDirectory()
        //                )
        //       {
        //           saw_web_inf = true;
        //       }
        //
        //       if ( saw_meta_inf && saw_web_inf ) { break ; }
        //   }
        //
        //   System.out.println("Status for: " + avm_appBase +
        //                      "/" + webapp_leafname );
        //
        //   System.out.println("    Saw META-INF: " + saw_meta_inf );
        //   System.out.println("    Saw WEB-INF: "  + saw_web_inf );
        //
        //   if ( saw_meta_inf && saw_web_inf )
        //   {
        //       deployAVMdirectory(version,
        //                          avm_appBase,
        //                          webapp_leafname,
        //                          context_path,
        //                          parent_context_path);
        //   }
        //   else
        //   {
        //       log.warn("Not deploying webapp: " + webapp_leafname +
        //                "  ( No META-INF and/or WEB-INF in: " +
        //                avm_appBase + "/" + webapp_leafname + " )");
        //   }

        deployAVMdirectory(version,
                           avm_appBase,
                           webapp_leafname,
                           context_path,
                           parent_context_path);
    }

    /**
     * Deploys AVM directory.  Requires that any directory
     * that this dir depends on has already been deployed.
     *
     * If a webapp has already been deployed, treat the
     * current deployment as a failure.
     */
    @SuppressWarnings("unchecked")
    protected boolean deployAVMdirectory(
       int    version,              // -1
       String avmAppBase,           // mysite--bob:/www/avm_webapps
       String webapp_leafname,      // my_webapp
       String contextPath,          // e.g.:   /$-1$mysite--bob$my_webapp
       String parent_context_path)  // possibly null
    {
        if (log.isDebugEnabled())
        {
            log.debug("deployAVMdirectory: webapp ["+version+", "+avmAppBase+", "+webapp_leafname+"]");
        }
        
        // mysite--bob:/www/avm_webapps/my_webapp
        String webapp_fullpath = avmAppBase + "/" + webapp_leafname;

        // Example params:
        //   version:         -1
        //   webapp_fullpath: mysite--bob:/www/avm_webapps/my_webapps
        //   webapp_leafname: my_webapps
        //   contextPath      /$-1$mysite--bob$my_webapp

        // Don't deploy something that's already deployed
        if (deploymentExists(contextPath))
        {
            return false;
        }

        AVMDeployedApplication deployedApp =
                new AVMDeployedApplication(contextPath, avmAppBase);

        // Deploy the application in this directory
        if( log.isDebugEnabled() )
            log.debug(sm.getString("hostConfig.deployDir", webapp_leafname));

        try
        {
            // The host is about to get an AVMStandardContext object
            // (representing a directory/webapp) as a "child":
            //
            //
            //                       Container
            //                           |
            //                     ContainerBase
            //                    /             \
            //      StandardContext         StandardHost
            //             |                     |
            //     AVMStandardContext         AVMHost
            //       (a webapp)           (a webapp container)
            //
            //
            // The AVMStandardContext object "context" corresponds to a
            // <Context> in Tomcat's configuration files.  In Tomcat 5.5:
            // http://tomcat.apache.org/tomcat-5.5-doc/config/context.html
            //
            //  AVMHost contains AVMStandardContext "children" webapps
            //  (the Context element represents a web application).
            //  Thus, think of a host as a "webapp container".


            // contextClass is:
            //         "org.alfresco.catalina.context.AVMStandardContext"
            //    (was "org.apache.catalina.core.StandardContext")
            //
            //  Context context =
            //      (Context) Class.forName(contextClass).newInstance();
            //
            // Just instantiate directly:
            //
            AVMStandardContext context = new AVMStandardContext();


            if (context instanceof Lifecycle)   // yes, it's a Lifecycle
            {
                //  By default, getConfigClass() returns:
                //         "org.apache.catalina.startup.ContextConfig"

                Class clazz = Class.forName(host.getConfigClass());

                LifecycleListener listener =
                        (LifecycleListener) clazz.newInstance();

                ((Lifecycle) context).addLifecycleListener(listener);
            }

            // Within a <Context> (i.e.: web application), the class
            // that accesses static resources can be set via <Resources>.
            //
            // For example, in $TOMCAT_HOME/context.xml you could
            // (but should not) say this:
            //
            //    <Context>
            //      <WatchedResource>WEB-INF/web.xml</WatchedResource>
            //      <Resources className="org.alfresco.jndi.AVMFileDirContext"/>
            //    </Context>
            //
            // Such a configuration would force all webapps in all virtual hosts
            // to fetch resources via AVMFileDirContext (rather than the default
            // FileDirContext).  This is too invasive because even though
            // AVMFileDirContext can act like a wrapper for FileDirContext,
            // someone might want to have different <Resources> for their
            // own custom host types.
            //
            // Instead, just make all webapps in AVM-based virtual hosts
            // fetch their resources from AVMFileDirContext:
            //
            // Calling context.setResources(...) must be done prior to
            // context.start(), so this is as good a place as any:
            //
            context.setResources( new AVMFileDirContext() );

            // The parent_cl of "host" is the "Shared" classloader:
            //
            //                Bootstrap
            //                      |
            //                   System
            //                      |
            //                   Common
            //                  /      \
            //             Catalina   Shared
            //                         /   \
            //                    Webapp1  Webapp2

            ClassLoader parent_cl = host.getParentClassLoader();


            AVMWebappLoader webappLoader =
                new AVMWebappLoader(
                      parent_cl,
                      context_classloader_registry_,
                      contextPath,
                      parent_context_path,

                      // AVM path version
                      // Usually, it's -1 which corresponds to HEAD
                      version,

                      // AVM path to the webapp
                      // Example:   mysite--bob:/www/avm_webapps/my_webapp
                      avmAppBase + "/" + webapp_leafname
                );

            webappLoader.setDelegate( false );  // false == check local 1st

            // Set custom loader
            //   This ultimately calls down to StandardContext.setLoader
            //   which calls stop() on the  webappLoader if necessary,
            //   then calls  start() on it.  Within start(), webappLoader
            //   will create its class loader.
            //
            context.setLoader(webappLoader);

            context.setPath(contextPath);  // e.g.: /$-1$store-3$my_webapp


            // CIFS-style JNDI path:
            //    /media/alfresco/cifs/v/mysite--bob/VERSION
            //        /v-1/DATA/www/avm_webapps/ROOT
            //
            // Example of webapp_fullpath:
            //      "mysite--bob:/www/avm_webapps/my_webapp"
            //
            // context.setDocBase( "$" + version + "$" + webapp_fullpath );

            int store_delim = webapp_fullpath.indexOf(':');

            context.setDocBase(
                webapp_fullpath.substring(0,store_delim)  +
                "/VERSION/v" + version                    +
                "/DATA"                                   +
                webapp_fullpath.substring(store_delim +1 ,
                                          webapp_fullpath.length()));


            // Make Constants.ApplicationContextXml == "META-INF/context.xml";
            // on all platforms, because we're reaching into AVM, not native
            // file system.
            //
            // Example configFile:
            //   "mysite--bob:/www/avm_webapps/my_webapp" +
            //               "/META-INF/context.xml"
            //
            String configFile = webapp_fullpath + "/" + "META-INF/context.xml";

            if (deployXML)
            {
                context.setConfigFile(configFile);
            }

            long split = System.currentTimeMillis();
            
            // The next line starts the webapp.
            //
            //     host.addChild(context)  calls context.start() inside
            //     ContainerBase, the grandfather class of AVMHost.
            //
            host.addChild(context);
            
            if (log.isInfoEnabled())
            {
                log.info("deployAVMdirectory: started web app ["+version+", "+avmAppBase+", "+webapp_leafname+"] in "+(System.currentTimeMillis()-split)+" ms");
            }
            
            AVMNodeDescriptor desc          = null;
            Long              last_modified = null;

            try
            {
                desc = AVMRemote_.lookup( version, webapp_fullpath );
                if (desc != null)
                {
                    last_modified = new Long( desc.getModDate() );
                }
                else
                {
                    last_modified = new Long( 0L );
                }
            }
            catch (Exception e)
            {
                last_modified = new Long( 0L );
            }

            // put() forces SuppressWarnings, due to map def in base class.
            deployedApp.redeployResources.put( webapp_fullpath, last_modified );


            if (deployXML)
            {
                try
                {
                    desc = AVMRemote_.lookup( version, configFile );
                    if (desc != null)
                    {
                        last_modified = new Long( desc.getModDate() );
                    }
                    else
                    {
                        last_modified = new Long( 0L );
                    }
                }
                catch (Exception e)
                {
                    last_modified = new Long( 0L );
                }

                // put() forces SuppressWarnings, due to map def in base class.
                deployedApp.redeployResources.put( configFile, last_modified);
            }

            // TODO:  get rid of this?
            addWatchedResources(deployedApp, webapp_fullpath, context);
        }
        catch (Throwable t)
        {
            if( log.isErrorEnabled() )
                log.error(sm.getString("hostConfig.deployDir.error",
                                        webapp_leafname), t);

            return false;
        }

        //  Prevent app from being deployed on top of itself
        this.deployed.put(contextPath, deployedApp);

        return true;
    }


    @SuppressWarnings("unchecked")
    protected void addWatchedResources( AVMDeployedApplication app,
                                        String                 webapp_fullpath,
                                        Context                context)
    {
        // Example params:
        //    app:   contextPath,  {avm_path,timestamp},{avm_path,timestamp},...
        //    webapp_fullpath: mysite--bob:/www/avm_webapps/my_webapps

        String[] watchedResources = context.findWatchedResources();

        // A webapp might do something like this:
        //
        //  <Context reloadable="true">
        //      <WatchedResource>WEB-INF/web.xml</WatchedResource>
        //       ...
        //  </Context>



        for (int i = 0; i < watchedResources.length; i++)
        {
            if (log.isDebugEnabled())
                log.debug("watched resource: " + watchedResources[i]);

            Long last_modified = null;

            String resource = watchedResources[i];
            if ( !resource.startsWith( webapp_fullpath ) )
            {
                // PORTING NOTE:
                //      The ugly hack below deals with Unix vs windows paths.
                //      There are better ways to do this.

                if ( File.separatorChar == '/' )           // Unix
                {
                    if ( ! (resource.charAt(0) == '/') )   // Windows
                    {
                        resource = webapp_fullpath + "/" + resource;

                        if (log.isDebugEnabled())
                            log.debug("relative watched resource " +
                                      "put into webapp_fullpath: " + resource);
                    }
                    else { continue; }
                }
                else
                {
                    if ( ! resource.startsWith(":\\", 1) )  // not absolute
                    {
                        // This is an AVM path.
                        // Because AVM uses '/' on all platforms,
                        // make resource absolute using '/' as separatorChar,
                        // even though this is Windows.
                        //
                        resource = webapp_fullpath + "/" + resource;

                        if (log.isDebugEnabled())
                            log.debug("relative watched resource " +
                                      "put into webapp_fullpath: " + resource);
                    }
                    else { continue; }
                }
            }

            AVMNodeDescriptor resource_desc = null;
            try
            {
                resource_desc = AVMRemote_.lookup( -1, resource );
                if (resource_desc != null)
                {
                    last_modified = new Long( resource_desc.getModDate() );
                }
                else
                {
                    last_modified = new Long( 0L );
                }
            }
            catch (Exception e)
            {
                last_modified = new Long( 0L );
            }

            if (log.isDebugEnabled())
                log.debug("adding watched resource: " +
                          resource  + "  modtime:" + last_modified);

            // put() forces SuppressWarnings, due to map def in base class.
            app.reloadResources.put( resource, last_modified);
        }
    }


    /**
    *  The autoDepoy="true" option is a performance killer because it triggers
    *  a lot of needless calls to checkResources() and deployApps(); therefore,
    *  this null check implementation removes all possibility of this occuring.
    */
    protected void check() { }



    @SuppressWarnings("unchecked")
    protected synchronized void checkResources(DeployedApplication app)
    {
        // AVMHostConfig should never have periodic checks via autoDeploy="true"
        // If someone has misconfigured things, nip it in the bud here.
        //
        // Note:
        //     This can't happen anymore due to the no-op check()
        //     function (see above).  Anyway, I'm leaving it in
        //     place for now.
        //
        // TODO: remove this entire checkResources function someday.
        //
        if ( 1 == 1 ) { return; }


        // resource_desc
        //
        // The resources fetched within app look like this:
        //      store-3:/www/avm_webapps/my_webapp
        //      store-3:/www/avm_webapps/my_webapp/META-INF/context.xml
        //      ...

        new Exception("debug stack trace for checkResources").printStackTrace();


        String avm_appBase = "";

        if (app instanceof AVMDeployedApplication)
        {
            //
            // AVMHostConfig checking AVMDeployedApplication:
            //          /$-1$alfresco-staging$my_webapp
            //

            if (log.isDebugEnabled())
                log.debug("checking AVMDeployedApplication: " +
                           ((AVMDeployedApplication)app).getName() );

            avm_appBase = ((AVMDeployedApplication)app).getAvmAppBase();
        }

        //         AVMHostConfig checkResources using appBase:
        //              alfresco-staging:/www/avm_webapps


        if (log.isDebugEnabled())
            log.debug("checkResources using appBase: "  + avm_appBase);

    	// Any modification of the specified (static) resources will cause a
    	// redeployment of the application. If any of the specified resources is
    	// removed, the application will be undeployed. Typically, this will
    	// contain resources like the context.xml file, a compressed WAR path.

        // keySet() forces SuppressWarnings, due to map def in base class.
        String[] resources =
            (String[]) app.redeployResources.keySet().toArray(new String[0]);

        for (int i = 0; i < resources.length; i++)
        {
            if (log.isDebugEnabled())
                log.debug("AVMHost config checking: " + resources[i]);

            String resource = resources[i];

            if (log.isDebugEnabled())
                log.debug("Checking context[" + app.name +
                                          "] redeploy resource " + resource);

            AVMNodeDescriptor resource_desc = null;

            try
            {
                // TODO:  This should check to see if resource is fetched from
                //        the file system or the AVM before assuming AVM;
                //        otherwise, you just keep undeploying/redeploying
                //
                //        <VIRTUAL_TOMCAT_HOME>/conf/Catalina/
                //              avm.alfresco.localhost/host-manager.xml

                resource_desc = AVMRemote_.lookup( -1, resource );
            }
            catch (Exception e)  { /* nothing to do */ }


            if ( resource_desc != null )        // file or dir exists
            {

                long lastModified =
                    ((Long) app.redeployResources.get( resource )).longValue();

                if (log.isDebugEnabled())
                    log.debug("AVMHost config check non-null resource_desc. " +
                              "Mod date: " +  resource_desc.getModDate()      +
                              "  Last mod: " + lastModified);

                if ( (!resource_desc.isDirectory()) &&
                     resource_desc.getModDate() > lastModified
                   )
                {
                    if (log.isDebugEnabled())
                        log.debug("config check mod date > last mod, " +
                                  "so undeploy app");

                    // Undeploy application
                    if (log.isInfoEnabled())
                        log.info(sm.getString("hostConfig.undeploy", app.name));

                    ContainerBase context =
                        (ContainerBase) host.findChild(app.name);

                    try
                    {
                        host.removeChild(context);
                    }
                    catch (Throwable t)
                    {
                        if (log.isWarnEnabled())
                            log.warn(sm.getString
                                     ("hostConfig.context.remove", app.name),t);
                    }

                    try
                    {
                        context.destroy();
                    }
                    catch (Throwable t)
                    {
                        if (log.isWarnEnabled())
                            log.warn(sm.getString
                                     ("hostConfig.context.destroy",app.name),t);
                    }

                    // Delete other redeploy resources
                    for (int j = i + 1; j < resources.length; j++)
                    {
                        //  For example:
                        //    resources[j] ==
                        //        "store-3:/www/avm_webapps/my_webapp"

                        try
                        {
                            String current = resources[j];
                            if ((current.startsWith(  avm_appBase  )) ||
                                (current.startsWith(
                                    configBase().getAbsolutePath()))
                               )
                            {
                                if (log.isDebugEnabled())
                                    log.debug("Delete " + current);

                                // NEON TODO:  figure out what to do here
                                // ExpandWar.delete(current);

                                if (log.isDebugEnabled())
                                    log.debug("AVMHostConfig should un-deploy " +
                                              "resource (but does not): "       +
                                              current );
                            }
                        }
                        catch (Exception e)
                        {
                            log.warn(sm.getString
                                    ("hostConfig.canonicalizing", app.name), e);
                        }
                    }
                    deployed.remove(app.name);
                    return;
                }
            }
            else        // file or dir no longer exists!
            {
                long lastModified =
                    ((Long) app.redeployResources.get( resource )).longValue();

                if (log.isDebugEnabled())
                    log.debug("AVMHost config check null resource_desc, " +
                              "so file/dir no longer exists.   "          +
                              "Last mod: "+ lastModified);

                if (lastModified == 0L) { continue; }

                // Undeploy application
                if (log.isInfoEnabled())
                    log.info(sm.getString("hostConfig.undeploy", app.name));

                ContainerBase context = (ContainerBase)host.findChild(app.name);

                try
                {
                    host.removeChild(context);
                }
                catch (Throwable t)
                {
                    if (log.isWarnEnabled())
                        log.warn(sm.getString
                                 ("hostConfig.context.remove", app.name), t);
                }

                try
                {
                    context.destroy();
                }
                catch (Throwable t)
                {
                    if (log.isWarnEnabled())
                        log.warn(sm.getString
                                 ("hostConfig.context.destroy", app.name), t);
                }
                // Delete all redeploy resources
                for (int j = i + 1; j < resources.length; j++)
                {
                    try
                    {
                        String current = resources[j];

                        // NEON TODO: again, what do I do with configBase?

                        if ( current.startsWith( avm_appBase  ) ||
                             current.startsWith( configBase().getAbsolutePath())
                           )
                        {
                            if (log.isDebugEnabled())
                                log.debug("Delete " + current);

                            // NEON TODO:  figure out what to do here
                            // ExpandWar.delete(current);

                            if (log.isDebugEnabled())
                                log.debug("AVMHostConfig should un-deploy " +
                                          "resource (but does not): "       +
                                          current );
                        }
                    }
                    catch (Exception e)
                    {
                        if (log.isWarnEnabled())
                            log.warn(sm.getString
                                    ("hostConfig.canonicalizing", app.name), e);
                    }
                }

                // Delete reload resources as well
                // (to remove any remaining .xml descriptor)
                // keySet() forces SuppressWarnings, due to map def
                // in base class.
                String[] resources2 =
                 (String[]) app.reloadResources.keySet().toArray(new String[0]);

                for (int j = 0; j < resources2.length; j++)
                {
                    try
                    {
                        String current = resources2[j];

                        // NEON TODO:  again, how should I handle configBase?
                        //
                        if ( current.startsWith( avm_appBase )
                             ||
                             ( (current.startsWith(
                                   configBase().getAbsolutePath())
                               &&
                               (current.endsWith(".xml")))
                             )
                           )
                        {
                            if (log.isDebugEnabled())
                                log.debug("Delete " + current);

                            // NEON TODO:  figure out what to do here
                            // ExpandWar.delete(current);

                            if (log.isDebugEnabled())
                                log.debug("AVMHostConfig should un-deploy " +
                                          "resource (but does not): "       +
                                          current );
                        }
                    }
                    catch (Exception e)
                    {
                        if (log.isWarnEnabled())
                            log.warn(sm.getString
                                    ("hostConfig.canonicalizing", app.name), e);
                    }
                }
                deployed.remove(app.name);
                return;
            }
        }

    	
        // Any modification of the specified (static) resources will cause
        // a reload of the application. This will typically contain resources
        // such as the web.xml of a webapp, but can be configured to contain
        // additional descriptors.

        // keySet() forces SuppressWarnings, due to map def in base class.
        resources =
           (String[]) app.reloadResources.keySet().toArray(new String[0]);

        for (int i = 0; i < resources.length; i++)
        {
            String resource = resources[i];

            if (log.isDebugEnabled())
                log.debug("Checking context[" + app.name +
                                           "] reload resource " + resource);


            AVMNodeDescriptor resource_desc = null;
            long lastModified =
                 ((Long) app.reloadResources.get(resource)).longValue();

            long    current_lastModified = 0L;
            boolean file_exists          = false;

            if ( ! resource.startsWith( avm_appBase ) )
            {
                // We're fetching something out of the normal file system.
                // For example:  /opt/tomcat/conf/context.xml

                File resourceFile = new File(resource);
                if ( resourceFile.exists() )
                {
                    current_lastModified = resourceFile.lastModified();
                    file_exists = true;
                }
            }
            else
            {
                try
                {
                    resource_desc        = AVMRemote_.lookup( -1, resource );
                    current_lastModified = resource_desc.getModDate();
                    file_exists          = true;
                }
                catch (Exception e)
                {
                    if (log.isDebugEnabled())
                        log.debug("Exception looking up: "  + resource +
                                  "  " + e.getMessage() +
                                  " ...and lastModified was: " +
                                  lastModified);
                }
            }

            if (log.isDebugEnabled())
                log.debug("check on: " + resource                           +
                          "\n            exists:          " + file_exists   +
                          "\n            lastMod:         " + lastModified  +
                          "\n            current_lastMod: " +
                          current_lastModified );


            if ( ( !file_exists && lastModified != 0L) ||
                 ( current_lastModified != lastModified)
               )
            {
                if (log.isDebugEnabled())
                    log.debug("       Reloading app: " + app.name );

                // Reload application
                if(log.isInfoEnabled())
                    log.info(sm.getString("hostConfig.reload", app.name));

                Container context = host.findChild(app.name);
                try
                {
                    ((Lifecycle) context).stop();
                }
                catch (Exception e)
                {
                    if(log.isWarnEnabled())
                        log.warn(sm.getString
                                 ("hostConfig.context.restart", app.name), e);
                }
                // If the context was not started (for example an error
                // in web.xml) we'll still get to try to start
                try
                {
                    ((Lifecycle) context).start();
                }
                catch (Exception e)
                {
                    if(log.isWarnEnabled())
                        log.warn(sm.getString
                                 ("hostConfig.context.restart", app.name), e);
                }
                // Update times
                app.reloadResources.put(resource,
                                        new Long(  current_lastModified  ));

                app.timestamp = System.currentTimeMillis();
                return;
            }
        }
    }

    /**
    *  Used to do a topological sort of webapp dependency based
    *  on transparent overlay configuration.  Sorting is necessary
    *  because you can't change a classloader's parent classloader
    *  once it has been created.
    */
    class AVMWebappDescriptor
    {                                  // Example of params:
        int     version_;              // -1
        String  store_name_;           // "mysite--bob"
        String  indirection_name_;     // not null iff neither overlay nor layer
        String  avm_appBase_;          // "mysite--bob:/www/avm_webapps"
        String  webapp_leafname_;      // "my_webapp"

        String  context_path_;         // "/$-1$mysite--bob$my_webapp"
        String  parent_store_name_;    // "mysite"
        String  parent_context_path_;  // "/$-1$mysite$my_webapp"

        // List of of webapp decriptors that are layered on top
        List<AVMWebappDescriptor> dependents_ =
                new LinkedList<AVMWebappDescriptor>();

        void addDependentWebappDescriptor( AVMWebappDescriptor d)
        {
            dependents_.add( d);
        }

        AVMWebappDescriptor(int     version,
                            String  store_name,
                            String  indirection_name,
                            String  avm_appBase,
                            String  webapp_leafname)
        {
            version_          = version;
            store_name_       = store_name;
            indirection_name_ = indirection_name;
            avm_appBase_      = avm_appBase;
            webapp_leafname_  = webapp_leafname;
        }

        String getContextPath()
        {
            if ( context_path_ == null )
            {
                context_path_ = AVMUrlValve.GetContextNameFromStoreName(
                                   version_,
                                   store_name_,
                                   webapp_leafname_
                                );
            }
            return context_path_;
        }

        void setWebappDependency(
                HashMap<String, AVMWebappDescriptor> webapp_descriptors,
                String                               parent_store_name)
        {
            parent_store_name_         = parent_store_name;
            String parent_context_path = getParentContextPath();
            AVMWebappDescriptor parent_desc =
                webapp_descriptors.get( parent_context_path );

            if ( parent_desc != null )
            {
                parent_desc.addDependentWebappDescriptor( this );
                
                if (log.isDebugEnabled())
                {
                    log.debug("Virtual context: " +
                               getContextPath()   +
                              " has parent: "     +
                               parent_context_path);
                }
            }
            else
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Virtual context: " +
                               getContextPath()   +
                              " has no parent: "  +
                               parent_context_path );
                }
            }
        }

        String getParentContextPath()
        {
            if ( parent_store_name_ == null ) { return null; }

            if ( parent_context_path_ == null )
            {
                parent_context_path_ = AVMUrlValve.GetContextNameFromStoreName(
                                           version_,
                                           parent_store_name_,
                                           webapp_leafname_
                                       );
            }
            return parent_context_path_;
        }
    }


    class AVMDeployedApplication extends HostConfig.DeployedApplication
    {
        String avmAppBase_;
        public AVMDeployedApplication( String name ) { super( name ); }

        public AVMDeployedApplication( String name, String avmAppBase )
        {
            super( name );
            avmAppBase_ = avmAppBase;
        }

        public String getName()                      {return this.name;}
        public String getAvmAppBase()                {return avmAppBase_;}
        public void setAvmAppBase(String avmAppBase) {avmAppBase_ = avmAppBase;}
    }
}
