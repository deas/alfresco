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
*  File    AVMFileDirContext.java
*
*
*  NOTE:
*       I would have preferred to derive from BaseDirContext directly, 
*       but the StandardContext only thinks that Resources are file 
*       system based (c.f.:  isFileSystemBased() ) if they derive 
*       from FileDirContext.
*
*
* CLASSPATH=$CLASSPATH:~/wcm-dev2/root/projects/3rd-party/lib/naming-resources.jar:
*     ../repository/build/dist/repository.jar
*     javac -Xlint:unchecked 
*     source/java/org/alfresco/jndi/AVMFileDirContext.java
*     source/java/org/alfresco/jndi/NamingContextBindingsEnumeration.java
*
*----------------------------------------------------------------------------*/

// A JSP request looks like this:
// 
//  ---------------------------------------------------------------
//  AVMFileDirContext:  getAttributes(): /xxx.jsp
//  AVMFileDirContext:  lookup(): /xxx.jsp
//  AVMFileDirContext:  getAttributes(): /<path-to-webapp>/my_webapp/xxx.jsp
//  AVMFileDirContext:  lookup(): /WEB-INF/classes/org/apache/jasper/runtime/JspSourceDependent.class
//  AVMFileDirContext:  lookup(): /WEB-INF/classes/org/apache/jasper/runtime/HttpJspBase.class
//  AVMFileDirContext:  lookup(): /WEB-INF/classes/javax/servlet/Servlet.class
//  AVMFileDirContext:  lookup(): /WEB-INF/classes/javax/servlet/ServletRequest.class
//  AVMFileDirContext:  lookup(): /WEB-INF/classes/javax/servlet/jsp/JspFactory.class
//  AVMFileDirContext:  lookup(): /WEB-INF/classes/javax/servlet/http/HttpServletResponse.class
//  AVMFileDirContext:  lookup(): /WEB-INF/classes/javax/servlet/jsp/PageContext.class
//  AVMFileDirContext:  lookup(): /WEB-INF/classes/javax/servlet/jsp/JspWriter.class
//  ---------------------------------------------------------------
//
//
// A request for an html file looks like this:
//
//  AVMFileDirContext:  getAttributes(): /blah.html
//  AVMFileDirContext:  lookup(): /blah.html
//
//
// Oddly, Tomcat makes the "name" passed be relative in the case of WEB-INF/lib/
// so you can't just concatinate this.base + name.   For example, look at the 2nd line of the trace:
//
// AVMFileDirContext:  listBindings():  /opt/apache-tomcat-5.5.15/avm_webapps/my_webapp + /META-INF/
// AVMFileDirContext:  listBindings():  /opt/apache-tomcat-5.5.15/avm_webapps/my_webapp + WEB-INF/lib/
// AVMFileDirContext:  getAttributes(): /opt/apache-tomcat-5.5.15/avm_webapps/my_webapp + /WEB-INF/classes
//
// If you say file.getAbsolutePath(), the problem is covered up because
// the File ctor is imlemented like this:
//
//    public File(String path, String name) 
//    {
//        if (name == null) { throw new NullPointerException(); }
//        if (path != null) 
//        {
//            if (path.endsWith(separator)) { this.path = path + name; }
//            else { this.path = path + separator + name; }
//        } 
//        else { this.path = name; }
//    }
//
//  Actually, it does a bit more -- it removes duplicate consecutive '/' chars
// (but does not deal with '..').
//




package org.alfresco.jndi;
import  org.apache.naming.resources.*;

import java.io.File;
import java.util.Map;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingEnumeration;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;

import org.apache.naming.NamingContextEnumeration;
import org.apache.naming.NamingEntry;
import org.alfresco.filter.CacheControlFilter;

import org.alfresco.repo.avm.AVMNodeType;
import org.alfresco.service.cmr.avm.AVMNodeDescriptor;
import org.alfresco.service.cmr.remote.AVMRemote;
import org.alfresco.util.JNDIPath;

// Had to use:   new Exception("Stack trace").printStackTrace();
// and read a lot of tomcat source to figure out what was actually 
// happening.  Hopefully, I won't be guilty of the same thing! ;)

/**
* AVMFileDirContext corresponds to a directory within a webapp.
*/
public class AVMFileDirContext extends  
             org.apache.naming.resources.FileDirContext  
{
    // AVMFileDirMountPoint_  is used by AVMHost as a prefix
    // for the host appBase.  This makes it easy for JNDI
    // to recognize all paths (from all AVMHost-based 
    // virtual hosts) that belong to it.
    //
    // Because of how StandardContext.getBasePath() works,
    // if the following dir isn't "absolute", then the application base
    // gets prepended (e.g.: on windows "c:/alfresco-.../virtual-tomcat").
    //
    // A little extra fancy footwork is needed here because of the
    // order in which various services come up.  For this resson, 
    // AVMFileDirMountPoint_ is actually set via a call to 
    // setAVMFileDirMountPoint() within VirtServerRegistrationThread.
    //
    //
    // Examples:
    //                UNIX:  "/media/alfresco/cifs/v"
    //
    //            Windows :  "v" or "v:" or "v:/"
    //                       (where 'v' is any drive letter)
    //
    static protected String   AVMFileDirMountPoint_; 


    public static final String getAVMFileDirMountPoint() { return AVMFileDirMountPoint_; }


    /** 
    *  @exclude
    *  Sets the base dir for JNDI paths.   
    *  Typically, this function is only called at startup time
    *  by the VirtServerRegistrationThread.  After the first
    *  call to this function, this function becomes a no-op.
    *
    * @return true iff sucessful
    *
    */
    public static final void setAVMFileDirMountPoint( String mount_point)
    { 
        if ( AVMFileDirMountPoint_ == null ) { AVMFileDirMountPoint_ = mount_point; }
    }

    // setDocBase() examples:
    //
    //   Unix:
    //     /media/alfresco/cifs/v/mysite--guest/VERSION/v-1/DATA/www/avm_webapps/ROOT
    //
    //   Windows
    //     v:/mysite--guest/VERSION/v-1/DATA/www/avm_webapps/ROOT
    //
    // Either case, the avmDocBase_ == "mysite--guest:/www/avm_webapps/xyz" 
    //              and avmVersion_ == -1
    //
    String avmDocBase_;
    int    avmRootVersion_;


    //  Defined in super:
    private static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog( AVMFileDirContext.class );

    // A single AVMRemote object is used for 
    // all queries to the AVM repository.

    static AVMRemote Service_;
    static int Service_refcount_ = 0; 



    static public synchronized 
    void InitAVMRemote(AVMRemote service)
    { 
        Service_ = service;

    }

    /**
    *  Decrements the reference count on the AVMRemote.
    *  This method is called by an AVMHost at the end of
    *  its lifetime.  When the reference count reaches 0, 
    *  the conection to the AVMRemote is shutdown.  
    */
    static public synchronized 
    void ReleaseAVMRemote()
    {
        Service_refcount_ -- ;

        log.debug("AVMFileDirContext.ReleaseAVMRemote() refcount now: " +  Service_refcount_ );

        if ( Service_refcount_ == 0 )
        {
            log.debug("AVMFileDirContext.ReleaseAVMRemote() closing " +
                      "FileSystemXmlApplicationContext (refcount dropped to 0)");

            // lost in time, like tears in rain... 
            Service_ = null;
        }
    }

    /**
    *  Fetches the AVMRemote used to fetch data from the AVM repository.
    *  Requires that InitAVMRemote() has been called previously.
    */
    static public AVMRemote  getAVMRemote()  { return Service_; }

    /**
    *  When true, fetch files/dirs using AMVService to access repository;
    *  otherwise, fetch files/dirs using file system (via FileDirContext).
    */
    protected boolean use_AVMRemote_ = false;

    protected void setUseAVMRemote( boolean tf)
    { 
        use_AVMRemote_ = tf;
    }

    protected boolean getUseAVMRemote()
    { 
        return use_AVMRemote_;
    }


    /**
    *  Only AVMFileDirContext objects constructed from within the Tomcat
    *  framework need to look at the docBase to figure out whether to use
    *  the file system or AVMRemote to access resources when setDocBase()
    *  is invoked.
    */
    protected boolean infer_webresources_from_docBase_ = false;


    //  Defined in super:
    //
    //    /**
    //     * The descriptive information string for this implementation.
    //     */
    //    protected static final int BUFFER_SIZE = 2048;


    /** 
    *  Constructs an AVMFileDirContext.
    */
    public AVMFileDirContext() 
    {
        super();

        log.debug("AVMFileDirContext:  AVMFileDirContext()");

        // filedir pre Spring Class/Method Name Here: parent classLoader == org.apache.catalina.loader.StandardClassLoader@c5c3ac
        // filedir pre Spring Class/Method Name Here: parent classLoader == sun.misc.Launcher$AppClassLoader@1858610
        // filedir pre Spring Class/Method Name Here: parent classLoader == sun.misc.Launcher$ExtClassLoader@12498b5
        // filedir pre Spring Class/Method Name Here: parent classLoader == null



        // This AVMFileDirContext corresponds to a top-level web application
        // directory; it is being constructed within the Tomcat framework,
        // and not within any method defined by AVMFileDirContext itself.
        //
        // Therefore, when setDocBase() is called on this object by Tomcat
        // later on (immediately after construction, actually), the parent 
        // dir of the docBase given will always correspond to the appDir 
        // of the owning host if this is a webapp that's fetching its contents
        // via AVMRemote.
        //
        // For example, within $CATALINA_HOME/conf/server.xml
        // the virtual host avm.alfresco.localdomain sets
        // its appBase to be avm_webapps like this:
        //
        //        <Host name              = "avm.alfresco.localdomain"
        //              className         = "org.alfresco.catalina.host.AVMHost"
        //              appBase           = "avm_webapps"
        //              unpackWARs        = "true" 
        //              autoDeploy        = "true"
        //              xmlValidation     = "false" 
        //              xmlNamespaceAware = "false">
        //        </Host>
        //
        //
        // Thus, when the AVMFileDirContext is created for "my_webapp",
        // when Tomcat calls setDocBase() the path it will provide will
        // look something like this:
        //
        //         /<cifs-mount>/www/avm_webapps
        //
        // Only in cases like this do we need to infer whether to fetch files 
        // from the file system or AVMRemote by looking at the docBase path, 
        // because if a method within AVMFileDirContext creates a new 
        // AVMFileDirContext, it always calls setUseAVMRemote()
        // on the newly created subcontext explicitly.
        //
        // Thus we guarantee the invariants:
        //
        //      o  A toplevel AVMFileDirContext uses AVMRemote
        //         iff it's in an appBase named  avm_webapps;
        //         otherwise, the file system is used. 
        //
        //      o  All sub-AVMFileDirContext objects use AVMRemote
        //         iff their parent AVMFileDirContext does.
        //        
        //      o  All sub-AVMFileDirContext objects use the file system
        //         iff their parent AVMFileDirContext does.


                                                   // Quite a song and dance
                                                   // for just 1 line of code.
        infer_webresources_from_docBase_ = true;   // :)
    }


    /**
     * Builds an  AVMFileDirContext using the given environment.
     *
     * Invoked by the methods: lookup(Name name) and avm_list(...)
     * to create sub-contexts.
     */
    public AVMFileDirContext(Hashtable env) 
    {
        super(env);

        // When setDocBase() is called on AVMFileDirContext objects 
        // created using this constructor, the docBase won't be the
        // top-level webapp directory like "/opt/tomcat/webapps/my_webapp".
        //
        // Instead it will look something in a deeper directory.

        log.debug("AVMFileDirContext:  AVMFileDirContext(env)");
    }


    // ----------------------------------------------------- Instance Variables


    // Defined in super
    //
    //     /**
    //      * The document base directory.
    //      */
    //     protected File base = null;
    //
    //
    //    /**
    //     * Absolute normalized filename of the base.
    //     */
    //    protected String absoluteBase = null;
    //
    //
    //    /**
    //     * Case sensitivity.
    //     */
    //    protected boolean caseSensitive = true;
    //
    //
    //    /**
    //     * Allow linking.
    //     */
    //    protected boolean allowLinking = false;
    //
    //
    // ------------------------------------------------------------- Properties


    /**
     * Set the document root.
     *
     * @param docBase The new document root
     *
     * @exception IllegalArgumentException if the specified value is not
     *  supported by this implementation
     * @exception IllegalArgumentException if this would create a
     *  malformed URL
     */
    public void setDocBase(String docBase) 
    {
        // Called once for every virtual webapp
        //
        // The docBase will look like this:
        //
        // <mount>/<store-name>/VERSION/v<version>/DATA/www/avm_webapps/ROOT
        // /foo/cifs/mysite--xx/VERSION/v123456789/DATA/www/avm_webapps/ROOT
        // ~~~~~~~~~ ~~~~~~~~~          ~~~~~~~~       ~~~ ~~~~~~~~~~~ ~~~~
        //    ^         ^                   ^           ^        ^       ^
        //    |         |                   |           |        |       |
        // <mount>  <store-name>         <version>  <www_base> <app_base> <doc_base>
        //    ^
        //    |
        // AVMFileDirMountPoint_
        //
        //
        // Note that care is taken here to make the docBase line up
        // with the CIFS <mount> point.  This enables servlet/JSP
        // functions like getRealPath() to work properly (assuming
        // the CIFS mount itself is up).
        //
        //
        //  The convoluted way this function is called at startup can be see via:
        //     new Exception("AVMFileDirContext setDocBase Stack trace: " + 
        //                    docBase).printStackTrace();
        //
        //        at org.alfresco.jndi.AVMFileDirContext.setDocBase(AVMFileDirContext.java:496)
        //        at org.apache.catalina.core.StandardContext.resourcesStart(StandardContext.java:3812)
        //        at org.apache.catalina.core.StandardContext.start(StandardContext.java:3983)
        //        at org.alfresco.catalina.context.AVMStandardContext.start(AVMStandardContext.java:64)
        //        at org.apache.catalina.core.ContainerBase.addChildInternal(ContainerBase.java:759)
        //        at org.apache.catalina.core.ContainerBase.addChild(ContainerBase.java:739)
        //        at org.apache.catalina.core.StandardHost.addChild(StandardHost.java:524)
        //        at org.alfresco.catalina.host.AVMHostConfig.deployAVMdirectory(AVMHostConfig.java:788)
        //        at org.alfresco.catalina.host.AVMHostConfig.deployAVMwebapp(AVMHostConfig.java:609)
        //        at org.alfresco.catalina.host.AVMHostConfig.deployAVMWebappDescriptorTree(AVMHostConfig.java:546)
        //        at org.alfresco.catalina.host.AVMHostConfig.deployAVMWebappDescriptorTree(AVMHostConfig.java:556)
        //        at org.alfresco.catalina.host.AVMHostConfig.deployAVMWebappsInDependencyOrder(AVMHostConfig.java:539)
        //        at org.alfresco.catalina.host.AVMHostConfig.deployAllAVMwebappsInRepository(AVMHostConfig.java:414)
        //        at org.alfresco.catalina.host.AVMHostConfig.deployApps(AVMHostConfig.java:323)
        //        at org.apache.catalina.startup.HostConfig.start(HostConfig.java:1118)
        //        at org.alfresco.catalina.host.AVMHostConfig.start(AVMHostConfig.java:281)
        //        at org.apache.catalina.startup.HostConfig.lifecycleEvent(HostConfig.java:310)
        //        at org.apache.catalina.util.LifecycleSupport.fireLifecycleEvent(LifecycleSupport.java:119)
        //        at org.apache.catalina.core.ContainerBase.start(ContainerBase.java:1020)
        //        at org.apache.catalina.core.StandardHost.start(StandardHost.java:718)
        //        at org.alfresco.catalina.host.AVMHost.start(AVMHost.java:586)
        //        at org.apache.catalina.core.ContainerBase.start(ContainerBase.java:1012)
        //        at org.apache.catalina.core.StandardEngine.start(StandardEngine.java:442)
        //        at org.apache.catalina.core.StandardService.start(StandardService.java:450)
        //        at org.apache.catalina.core.StandardServer.start(StandardServer.java:700)
        //        at org.apache.catalina.startup.Catalina.start(Catalina.java:551)
        //        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        //        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
        //        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
        //        at java.lang.reflect.Method.invoke(Method.java:585)
        //        at org.apache.catalina.startup.Bootstrap.start(Bootstrap.java:275)
        //        at org.apache.catalina.startup.Bootstrap.main(Bootstrap.java:413)
        //
        //
        // Given  /opt/tomcat -> /opt/apache-tomcat-5.5.15/
        // Here's an example of the docBase values seen:
        //
        //      /opt/tomcat/avm_webapps/ROOT
        //      /opt/apache-tomcat-5.5.15/avm_webapps/ROOT/WEB-INF/lib
        //      /opt/tomcat/avm_webapps/jcox.alfresco
        //      /opt/apache-tomcat-5.5.15/avm_webapps/jcox.alfresco/WEB-INF/classes
        //      /opt/tomcat/avm_webapps/my_webapp
        //      /opt/apache-tomcat-5.5.15/avm_webapps/my_webapp/WEB-INF/classes
        //      /opt/tomcat/avm_webapps/tomcat-docs
        //      /opt/tomcat/avm_webapps/webdav
        //      /opt/apache-tomcat-5.5.15/server/webapps/manager
        //      /opt/apache-tomcat-5.5.15/server/webapps/host-manager


        log.debug("AVMFileDirContext:  setDocBase(): " + docBase);

        // Validate the format of the proposed document root
        if (docBase == null)
        {
            throw new IllegalArgumentException(sm.getString("resources.null"));
        }

        if ( infer_webresources_from_docBase_  && 
             docBase.startsWith( AVMFileDirMountPoint_ )
           )
        {
            log.debug("AVMFileDirContext:  USING AVM for: " + docBase );

            use_AVMRemote_ = true;
        }

        if (! use_AVMRemote_ ) 
        { 
            super.setDocBase( docBase ); 
            return; 
        }

        // Because java does not let me say:  super.super
        // it's hard to reuse code from BaseDirContext.
        // Therefore, I have to set this by hand here via
        // cut/paste.  Actually, this is a JVM constraint,
        // so there's no way to do it within *any* JVM-based
        // language; this is due to the way 'invokespecial' is
        // treated by the JVM spec).   Quite annoying.
        //
        // Think about this for a moment:
        //      A 3rd party product (tomcat) forces
        //      me to derive from FileDirContext, 
        //      not BaseDirContext.   I need to invoke
        //      methods of BaseDirContext from AVMFileDirContext
        //      but if I do so through the middle-level 
        //      class FileDirContext (which i cannot control)
        //      then illegal operations are performed
        //      (i.e.: file system access).   I don't have
        //      the option to refactor here because it's third
        //      party code.   Thus, the Java-oid advice 
        //      "dude...just refactor" simply isn't an option.
        //      
        //      Now because the JVM designers wanted for force
        //      a refactor down my throat, I can't re-use
        //      the super.super method, and I have a maintenence
        //      issue on top of it all.  As they say,  "bitter Java"!
        //
        //
        // Grudgingly copied from BaseDirContext: 
        //
        //    Change the document root property
        this.docBase = docBase;


        // Implementation note
        // -------------------
        //
        // The reason for preferrring VERSION paths over HEAD-style
        // JNDI paths is that al you have to do to shift to another
        // version is change the v-... value.
        //
        // For example:
        //   /media/alfresco/cifs/v/mysite--guest/VERSION/v-1/DATA/www/avm_webapps/ROOT
        //
        // can become:
        //   /media/alfresco/cifs/v/mysite--guest/VERSION/v-99/DATA/www/avm_webapps/ROOT
        //
        // The only transformation requires was "v-1" -->  "v-99". 
        //
        // Compare that to what you'd need to do in order to re-write a HEAD path
        // (which is equivalent to a "v-1" VERSION path to version 99:
        //
        //   /media/alfresco/cifs/v/mysite--guest/HEAD/DATA/www/avm_webapps/ROOT
        //   --->
        //   /media/alfresco/cifs/v/mysite--guest/VERSION/v-99/DATA/www/avm_webapps/ROOT
        //
        // Thus, while HEAD-style paths are nice for interactive use,
        // VERSION-style paths are better for programmatic access.
        // because they're fully general.
        //
        // 
        // On Unix, setDocBase() will get called with docBase values like:
        //   /media/alfresco/cifs/v/mysite--guest/VERSION/v-1/DATA/www/avm_webapps/ROOT
        //
        // On Windows, docBase values will look more like:
        //   v:\mysite--guest\VERSION\v-1\DATA\www\avm_webapps\ROOT
        //
        // In either case, we want:
        //     avmDocBase      == "mysite--guest:/www/avm_webapps/ROOT"
        //     and avmVersion_ == -1
        // 
        // JNDIPath encapsulates the logic to do this, but assumes
        // VERSION path to DATA  (i.e.: it's not written for HEAD-paths.
        // This is ok, because that's all we ever do here.

        try 
        {
            JNDIPath jndi_path = new JNDIPath(  AVMFileDirMountPoint_, docBase);

            avmDocBase_     = jndi_path.getAvmPath();
            avmRootVersion_ = jndi_path.getAvmVersion();

            log.debug("AVMFileDirContext.setDocBase avmDocBase_    : " + avmDocBase_);
            log.debug("AVMFileDirContext.setDocBase avmRootVersion_: " + avmRootVersion_);
        }
        catch (Exception path_exception)
        {
            throw new IllegalArgumentException( 
                sm.getString("fileResources.base", docBase), path_exception);
        }
    }


     
    /**
    *  Allows the docBase to be set when the version and repoPath
    *  are already known  (this saves the trouble of having to 
    *  assemble a name-mangled request URI and parse the data you
    *  already have back out).
    *        
    * @param rootVersion  The version of the root repository node
    *                     mentioned in repoPath.
    *        
    * @param repoPath     A path into the AVMRemote repository of the form:
    *                     "reponame:/...path..."
    */
    void setDocBase(int rootVersion, String repoPath )
    {
        avmRootVersion_ = rootVersion;
        avmDocBase_     = repoPath;
    }

    // --------------------------------------------------------- Public Methods

    public void allocate() 
    {
        log.debug("AVMFileDirContext:  allocate()");
        if ( use_AVMRemote_ ) 
        {
            // TODO: ensure we got an AVMRemote connection
        }


        super.allocate();               // a no-op for now
    }


    /**
     * Release any resources allocated for this directory context.
     */
    public void release() 
    {
        log.debug("AVMFileDirContext:  release()");

        if ( use_AVMRemote_ ) 
        {
            // TODO
            // Ensure we get rid of AVMRemote connection
            // probably via synchronized refcount
        }

        // Because FileDirContext does early allocation,
        // we've got to call super.release() no matter what.
        super.release();
    }


    // -------------------------------------------------------- Context Methods


    /**
     * Retrieves the named object.
     *
     * @param name the name of the object to look up
     * @return the object bound to name
     * @exception NamingException if a naming exception is encountered
     */
    public Object lookup(String name) throws NamingException 
    {
        // Example:   /opt/apache-tomcat-5.5.15/avm_webapps/servlets-examples + 
        //                /WEB-INF/classes/RequestInfoExample.class
        //
        // where this.base was set by setDocBase()

        if (! use_AVMRemote_ ) 
        { 
            log.debug("AVMFileDirContext:  lookup(): " + this.base + " + " + name);
            log.debug("    AVMFileDirContext: using file system");
            return super.lookup( name ); 
        }

        // Remove the following prefix from "name", if it exists:
        //         /avm.alfresco.localhost/<virtwebapp>
        //
        // There's no need to worry about which <virtwebapp> it is,
        // just make sure it starts with the '$' character.
        //
        if ( name.startsWith("/avm.alfresco.localhost/$") )
        {
            int name_index = name.indexOf('/', "/avm.alfresco.localhost/$".length() );
            if (name_index >=0) { name = name.substring( name_index); }
        }

        String repo_path;
        if (  name.charAt(0) != '/') { repo_path = avmDocBase_ + "/" + name; }
        else                         { repo_path = avmDocBase_ + name; }

        log.debug("AVMFileDirContext:  AVM lookup(): " + repo_path );

        AVMNodeDescriptor avm_node = null;

        try 
        { 
            // Lookup dependencies are set here and in lastModified() 
            // The reason you need them here is that someone might
            // try to lookup a file that does not exist.  If this occurs,
            // there won't be a valide avm_node to query for its 
            // canonical name.
            //
            // The reason dependencies are also set in lastModified()
            // as to do with caching.  If Tomcat has cached the item,
            // the call to lookup() never occurs... but the lastModified()
            // call always does.  Tricky, ey?

            CacheControlFilter.AddLookupDependency( repo_path );

            avm_node = Service_.lookup(avmRootVersion_, repo_path); 
            if (avm_node == null)
            {
                log.debug("AVMFileDirContext:  lookup() not found: " +  repo_path);
                throw new NamingException(sm.getString("resources.notFound", repo_path));
            }
        }
        catch (Exception e)
        {
            // TODO: emit message in exception e

            log.debug("AVMFileDirContext:  lookup() not found: " +  repo_path);
            throw new NamingException(sm.getString("resources.notFound", repo_path));
        }

        Object result;

        if ( avm_node.isDirectory() ) 
        {
            log.debug("AVMFileDirContext:  lookup creating AVMFileDirContext(env) for dir: " +  avm_node.getPath() );

            AVMFileDirContext tempContext = new AVMFileDirContext(env);
            tempContext.setUseAVMRemote( use_AVMRemote_ );
            tempContext.setDocBase( avmRootVersion_,  avm_node.getPath() );

            tempContext.setAllowLinking(getAllowLinking());
            tempContext.setCaseSensitive(isCaseSensitive());
            result = tempContext;
        } 
        else 
        {
            log.debug("AVMFileDirContext:  lookup creating AVMFileResource for file: " +   avm_node.getPath() );

            // The goal here is to create the AVMFileResource 
            // using an object that will be sufficient to stream
            // the content back later.  For AVM, this should 
            // be a node descriptor, not a file.

            result = new AVMFileResource( avmRootVersion_, avm_node.getPath() );
        }

        return result;
    }


    /**
     * Unbinds the named object. Removes the terminal atomic name in name
     * from the target context--that named by all but the terminal atomic
     * part of name.
     * <p>
     * This method is idempotent. It succeeds even if the terminal atomic
     * name is not bound in the target context, but throws
     * NameNotFoundException if any of the intermediate contexts do not exist.
     *
     * @param name the name to bind; may not be empty
     * @exception NameNotFoundException if an intermediate context does not
     * exist
     * @exception NamingException if a naming exception is encountered
     */
    public void unbind(String name)
        throws NamingException 
    {
        log.debug("AVMFileDirContext:  unbind(): " + name);

        if ( ! use_AVMRemote_ ) 
        { 
            log.debug("    AVMFileDirContext: using file system");
            super.unbind( name ); 
            return;
        }

        String repo_path;
        if (  name.charAt(0) != '/') 
        { 
            repo_path = avmDocBase_ + "/" + name; 
        }
        else                         
        { 
            repo_path = avmDocBase_ + name; 
            name      = name.substring(1);
        }

        AVMNodeDescriptor avm_node = null;

        try 
        { 
            avm_node = Service_.lookup(avmRootVersion_, repo_path );
            if (avm_node == null)
            {
                throw new NamingException
                (sm.getString("resources.notFound", repo_path));                
            }
        }
        catch (Exception e)
        {
            // TODO:  log avm specific error 

            throw new NamingException
                (sm.getString("resources.notFound", repo_path));
        }

        if ( avmRootVersion_ != -1 )
        {
            throw new NamingException
                (sm.getString("resources.unbindFailed", repo_path));
        }

        try { Service_.removeNode( avmDocBase_ , name ); }
        catch (Exception e)
        {
            // TODO:  log avm specific error 
            throw new NamingException
                (sm.getString("resources.unbindFailed", repo_path));
        }
    }


    /**
     * Binds a new name to the object bound to an old name, and unbinds the
     * old name. Both names are relative to this context. Any attributes
     * associated with the old name become associated with the new name.
     * Intermediate contexts of the old name are not changed.
     *
     * @param oldName the name of the existing binding; may not be empty
     * @param newName the name of the new binding; may not be empty
     * @exception NameAlreadyBoundException if newName is already bound
     * @exception NamingException if a naming exception is encountered
     */
    public void rename(String oldName, String newName)
        throws NamingException 
    {

        log.debug("AVMFileDirContext:  rename(): " + oldName + " " + newName);

        if ( ! use_AVMRemote_ ) 
        {
            log.debug("    AVMFileDirContext: using file system");
            super.rename(oldName, newName); 
            return;
        }


        // TODO:
        //   replace all code below with something appropriate for AVMRemote
        //
        //        File file = file(oldName);
        //
        //        if (file == null)
        //            throw new NamingException
        //                (sm.getString("resources.notFound", oldName));
        //
        //        File newFile = new File(this.base, newName);
        //
        //        file.renameTo(newFile);

        throw new OperationNotSupportedException();
    }


    /**
     * Enumerates the names bound in the named context, along with the class
     * names of objects bound to them. The contents of any subcontexts are
     * not included.
     * <p>
     * If a binding is added to or removed from this context, its effect on
     * an enumeration previously returned is undefined.
     *
     * @param name the name of the context to list
     * @return an enumeration of the names and class names of the bindings in
     * this context. Each element of the enumeration is of type NameClassPair.
     * @exception NamingException if a naming exception is encountered
     */
    @SuppressWarnings("unchecked")
    public NamingEnumeration<javax.naming.NameClassPair> 
    list(String name) throws NamingException 
    {
        if ( ! use_AVMRemote_ ) 
        {
            log.debug("    AVMFileDirContext: using file system for list(): " + name);

            // The following line is what makes me need to suppress "unchecked":
            return  super.list( name );
        }

        String repo_path;
        if (  name.charAt(0) != '/') { repo_path = avmDocBase_ + "/" + name; }
        else                         { repo_path = avmDocBase_ + name; }

        log.debug("    AVMFileDirContext list() using AVMRemote for: " + repo_path);

        AVMNodeDescriptor avm_node = null;

        try 
        { 
            avm_node = Service_.lookup(avmRootVersion_, repo_path );
            if (avm_node == null)
            {
                throw new NamingException
                (sm.getString("resources.notFound", repo_path));
            }
        }
        catch( Exception e)
        {
            throw new NamingException
            (sm.getString("resources.notFound", repo_path));
        }

        return new NamingContextEnumeration(avm_list( avm_node, true ).iterator());
    }


    /**
     * Enumerates the names bound in the named context, along with the
     * objects bound to them. The contents of any subcontexts are not
     * included.
     * <p>
     * If a binding is added to or removed from this context, its effect on
     * an enumeration previously returned is undefined.
     *
     * @param name the name of the context to list
     * @return an enumeration of the bindings in this context.
     * Each element of the enumeration is of type Binding.
     * @exception NamingException if a naming exception is encountered
     */ 
    @SuppressWarnings("unchecked")
    public NamingEnumeration<javax.naming.Binding> 
    listBindings(String name) throws NamingException 
    {
        return listBindings(name, true);
    }


    @SuppressWarnings("unchecked")
    public NamingEnumeration<javax.naming.Binding> 
    listBindings(String name, boolean include_background) throws NamingException 
    {
        // this.base:
        //      /opt/tomcat/avm_webapps/ROOT
        //      /opt/apache-tomcat-5.5.15/avm_webapps/ROOT/WEB-INF/lib
        //      /opt/tomcat/avm_webapps/jcox.alfresco
        //      /opt/apache-tomcat-5.5.15/avm_webapps/jcox.alfresco/WEB-INF/classes
        //      /opt/tomcat/avm_webapps/my_webapp
        //      /opt/apache-tomcat-5.5.15/avm_webapps/my_webapp/WEB-INF/classes
        //      /opt/tomcat/avm_webapps/tomcat-docs
        //      /opt/tomcat/avm_webapps/webdav
        //      /opt/apache-tomcat-5.5.15/server/webapps/manager
        //      /opt/apache-tomcat-5.5.15/server/webapps/host-manager
        //
        // Irritatingly enough, name can have or lack the leading "/"
        // Hence the use by the tomcat crew of File.


        if ( ! use_AVMRemote_ ) 
        {
            log.debug("AVMFileDirContext:  listBindings() file system: " + this.base + " + " + name);

            // The following line is what makes me need to suppress "unchecked":
            return super.listBindings(name);
        }

        String repo_path;
        if (  name.charAt(0) != '/') { repo_path = avmDocBase_ + "/" + name; }
        else                         { repo_path = avmDocBase_ + name; }

        AVMNodeDescriptor avm_node = null;

        log.debug("AVMFileDirContext:  listBindings() AVM: " + repo_path);

        try 
        { 
            avm_node = Service_.lookup(avmRootVersion_, repo_path ); 
            if (avm_node == null)
            {
                throw new NamingException
                (sm.getString("resources.notFound", repo_path));                
            }
        }
        catch( Exception e)
        {
            throw new NamingException
                (sm.getString("resources.notFound", repo_path));
        }

        return new NamingContextBindingsEnumeration( 
                       avm_list(avm_node, include_background).iterator(),
                       this);
    }


    /**
     * Destroys the named context and removes it from the namespace. Any
     * attributes associated with the name are also removed. Intermediate
     * contexts are not destroyed.
     * <p>
     * This method is idempotent. It succeeds even if the terminal atomic
     * name is not bound in the target context, but throws
     * NameNotFoundException if any of the intermediate contexts do not exist.
     *
     * In a federated naming system, a context from one naming system may be
     * bound to a name in another. One can subsequently look up and perform
     * operations on the foreign context using a composite name. However, an
     * attempt destroy the context using this composite name will fail with
     * NotContextException, because the foreign context is not a "subcontext"
     * of the context in which it is bound. Instead, use unbind() to remove
     * the binding of the foreign context. Destroying the foreign context
     * requires that the destroySubcontext() be performed on a context from
     * the foreign context's "native" naming system.
     *
     * @param name the name of the context to be destroyed; may not be empty
     * @exception NameNotFoundException if an intermediate context does not
     * exist
     * @exception NotContextException if the name is bound but does not name
     * a context, or does not name a context of the appropriate type
     */
    public void destroySubcontext(String name) throws NamingException 
    {
        log.debug("AVMFileDirContext:  destroySubcontext(): " + name );

        if ( ! use_AVMRemote_ ) 
        {
            log.debug("    AVMFileDirContext: using file system");
            super.destroySubcontext(name);
            return;
        }

        unbind(name);
    }


    /**
     * Retrieves the named object, following links except for the terminal
     * atomic component of the name. If the object bound to name is not a
     * link, returns the object itself.
     *
     * @param name the name of the object to look up
     * @return the object bound to name, not following the terminal link
     * (if any).
     * @exception NamingException if a naming exception is encountered
     */
    public Object lookupLink(String name) throws NamingException 
    {
        // Note : Links are not supported
        log.debug("AVMFileDirContext:  lokupLink(): " + name );

        if ( ! use_AVMRemote_ ) 
        {
            log.debug("    AVMFileDirContext: using file system");
            return super.lookupLink( name );
        }

        return lookup(name);
    }


    /**
     * Retrieves the full name of this context within its own namespace.
     * <p>
     * Many naming services have a notion of a "full name" for objects in
     * their respective namespaces. For example, an LDAP entry has a
     * distinguished name, and a DNS record has a fully qualified name. This
     * method allows the client application to retrieve this name. The string
     * returned by this method is not a JNDI composite name and should not be
     * passed directly to context methods. In naming systems for which the
     * notion of full name does not make sense,
     * OperationNotSupportedException is thrown.
     *
     * @return this context's name in its own namespace; never null
     * @exception OperationNotSupportedException if the naming system does
     * not have the notion of a full name
     * @exception NamingException if a naming exception is encountered
     */
    public String getNameInNamespace() throws NamingException 
    {
        log.debug("AVMFileDirContext:  getNameInNamespace()");

        if ( ! use_AVMRemote_ ) 
        {
            log.debug("    AVMFileDirContext: using file system");
            return super.getNameInNamespace();
        }

        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote

        return docBase;   // NEON - fix as a part of making AVMRemote distrib.
    }


    // ----------------------------------------------------- DirContext Methods


    /**
     * Retrieves selected attributes associated with a named object.
     * See the class description regarding attribute models, attribute type
     * names, and operational attributes.
     *
     * @return the requested attributes; never null
     * @param name the name of the object from which to retrieve attributes
     * @param attrIds the identifiers of the attributes to retrieve. null
     * indicates that all attributes should be retrieved; an empty array
     * indicates that none should be retrieved
     * @exception NamingException if a naming exception is encountered
     */
    public Attributes getAttributes(String   name, 
                                    String[] attrIds
                                   ) throws  NamingException 
    {
        //  new Exception("Stack trace").printStackTrace();
        //  Example:
        //   /opt/apache-tomcat-5.5.15/avm_webapps/ROOT + /a/b/fun.html

        if ( ! use_AVMRemote_ ) 
        {
            log.debug("    AVMFileDirContext.getAttributes(): using file system for: " + name);
            return super.getAttributes( name, attrIds );
        }

        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote


        String repo_path;
        if (  name.charAt(0) != '/') 
        { 
            repo_path = avmDocBase_ + "/" + name; 
        }
        else                         
        { 
            repo_path = avmDocBase_ + name; 
            name      = name.substring(1);
        }

        log.debug("AVMFileDirContext: getAttributes(): " + repo_path );

        AVMNodeDescriptor avm_node = null;

        try 
        { 
            avm_node = Service_.lookup(avmRootVersion_, repo_path); 
            if (avm_node == null)
            {
                log.debug("AVMFileDirContext:  lookup() not found: " +  repo_path);
                throw new NamingException(sm.getString("resources.notFound", repo_path));
            }
        }
        catch (Exception e)
        {
            // TODO: emit message in exception e

            log.debug("AVMFileDirContext:  lookup() not found: " +  repo_path);
            throw new NamingException(sm.getString("resources.notFound", repo_path));
        }

        return new AVMFileResourceAttributes( avm_node, name );
    }


    /**
     * Modifies the attributes associated with a named object. The order of
     * the modifications is not specified. Where possible, the modifications
     * are performed atomically.
     *
     * @param name the name of the object whose attributes will be updated
     * @param mod_op the modification operation, one of: ADD_ATTRIBUTE,
     * REPLACE_ATTRIBUTE, REMOVE_ATTRIBUTE
     * @param attrs the attributes to be used for the modification; may not
     * be null
     * @exception AttributeModificationException if the modification cannot be
     * completed successfully
     * @exception NamingException if a naming exception is encountered
     */
    public void modifyAttributes(String name, int mod_op, Attributes attrs)
        throws NamingException {

        if ( ! use_AVMRemote_ ) 
        {
            log.debug("    AVMFileDirContext: using file system");
            super.modifyAttributes( name, mod_op, attrs );
            return;
        }


        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote




    }


    /**
     * Modifies the attributes associated with a named object using an an
     * ordered list of modifications. The modifications are performed in the
     * order specified. Each modification specifies a modification operation
     * code and an attribute on which to operate. Where possible, the
     * modifications are performed atomically.
     *
     * @param name the name of the object whose attributes will be updated
     * @param mods an ordered sequence of modifications to be performed; may
     * not be null
     * @exception AttributeModificationException if the modification cannot be
     * completed successfully
     * @exception NamingException if a naming exception is encountered
     */
    public void modifyAttributes(String name, ModificationItem[] mods)
        throws NamingException {


        if ( ! use_AVMRemote_ ) 
        {
            log.debug("    AVMFileDirContext: using file system");
            super.modifyAttributes( name, mods );
            return;
        }


        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote



    }


    /**
     * Binds a name to an object, along with associated attributes. If attrs
     * is null, the resulting binding will have the attributes associated
     * with obj if obj is a DirContext, and no attributes otherwise. If attrs
     * is non-null, the resulting binding will have attrs as its attributes;
     * any attributes associated with obj are ignored.
     *
     * @param name the name to bind; may not be empty
     * @param obj the object to bind; possibly null
     * @param attrs the attributes to associate with the binding
     * @exception NameAlreadyBoundException if name is already bound
     * @exception InvalidAttributesException if some "mandatory" attributes
     * of the binding are not supplied
     * @exception NamingException if a naming exception is encountered
     */
    public void bind(String name, Object obj, Attributes attrs)
        throws NamingException {

        log.debug("AVMFileDirContext:  bind(): " + name );

        if ( ! use_AVMRemote_ ) 
        {
            log.debug("    AVMFileDirContext: using file system");
            super.bind( name, obj, attrs );
            return;
        }

        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote


        // Note: No custom attributes allowed

        File file = new File(this.base, name);
        if (file.exists())
            throw new NameAlreadyBoundException
                (sm.getString("resources.alreadyBound", name));

        rebind(name, obj, attrs);

    }


    /**
     * Binds a name to an object, along with associated attributes,
     * overwriting any existing binding. If attrs is null and obj is a
     * DirContext, the attributes from obj are used. If attrs is null and obj
     * is not a DirContext, any existing attributes associated with the object
     * already bound in the directory remain unchanged. If attrs is non-null,
     * any existing attributes associated with the object already bound in
     * the directory are removed and attrs is associated with the named
     * object. If obj is a DirContext and attrs is non-null, the attributes
     * of obj are ignored.
     *
     * @param name the name to bind; may not be empty
     * @param obj the object to bind; possibly null
     * @param attrs the attributes to associate with the binding
     * @exception InvalidAttributesException if some "mandatory" attributes
     * of the binding are not supplied
     * @exception NamingException if a naming exception is encountered
     */
    public void rebind(String name, Object obj, Attributes attrs)
        throws NamingException {

        log.debug("AVMFileDirContext:  rebind(): " + name );


        if ( ! use_AVMRemote_ ) 
        {
            log.debug("    AVMFileDirContext: using file system");
            super.rebind( name, obj, attrs );
            return;
        }

        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote




        // Note: No custom attributes allowed
        // Check obj type

        File file = new File(this.base, name);

        InputStream is = null;
        if (obj instanceof Resource) {
            try {
                is = ((Resource) obj).streamContent();
            } catch (IOException e) {
            }
        } else if (obj instanceof InputStream) {
            is = (InputStream) obj;
        } else if (obj instanceof DirContext) {
            if (file.exists()) {
                if (!file.delete())
                    throw new NamingException
                        (sm.getString("resources.bindFailed", name));
            }
            if (!file.mkdir())
                throw new NamingException
                    (sm.getString("resources.bindFailed", name));
        }
        if (is == null)
            throw new NamingException
                (sm.getString("resources.bindFailed", name));

        // Open os

        try {
            FileOutputStream os = null;
            byte buffer[] = new byte[BUFFER_SIZE];
            int len = -1;
            try {
                os = new FileOutputStream(file);
                while (true) {
                    len = is.read(buffer);
                    if (len == -1)
                        break;
                    os.write(buffer, 0, len);
                }
            } finally {
                if (os != null)
                    os.close();
                is.close();
            }
        } catch (IOException e) {
            throw new NamingException
                (sm.getString("resources.bindFailed", e));
        }

    }


    /**
     * Creates and binds a new context, along with associated attributes.
     * This method creates a new subcontext with the given name, binds it in
     * the target context (that named by all but terminal atomic component of
     * the name), and associates the supplied attributes with the newly
     * created object. All intermediate and target contexts must already
     * exist. If attrs is null, this method is equivalent to
     * Context.createSubcontext().
     *
     * @param name the name of the context to create; may not be empty
     * @param attrs the attributes to associate with the newly created context
     * @return the newly created context
     * @exception NameAlreadyBoundException if the name is already bound
     * @exception InvalidAttributesException if attrs does not contain all
     * the mandatory attributes required for creation
     * @exception NamingException if a naming exception is encountered
     */
    public DirContext createSubcontext(String name, Attributes attrs)
        throws NamingException {


        log.debug("AVMFileDirContext:  createSubcontext(): " + name );


        if ( ! use_AVMRemote_ ) 
        {
            log.debug("    AVMFileDirContext: using file system");
            return super.createSubcontext( name, attrs );
        }


        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote







        File file = new File(this.base, name);
        if (file.exists())
            throw new NameAlreadyBoundException
                (sm.getString("resources.alreadyBound", name));
        if (!file.mkdir())
            throw new NamingException
                (sm.getString("resources.bindFailed", name));
        return (DirContext) lookup(name);

    }


    /**
     * Retrieves the schema associated with the named object. The schema
     * describes rules regarding the structure of the namespace and the
     * attributes stored within it. The schema specifies what types of
     * objects can be added to the directory and where they can be added;
     * what mandatory and optional attributes an object can have. The range
     * of support for schemas is directory-specific.
     *
     * @param name the name of the object whose schema is to be retrieved
     * @return the schema associated with the context; never null
     * @exception OperationNotSupportedException if schema not supported
     * @exception NamingException if a naming exception is encountered
     */
    public DirContext getSchema(String name)
        throws NamingException 
    {
    
        if ( ! use_AVMRemote_ ) 
        {
            log.debug("    AVMFileDirContext: using file system");
            return super.getSchema( name );
        }

        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote





        throw new OperationNotSupportedException();
    }


    /**
     * Retrieves a context containing the schema objects of the named
     * object's class definitions.
     *
     * @param name the name of the object whose object class definition is to
     * be retrieved
     * @return the DirContext containing the named object's class
     * definitions; never null
     * @exception OperationNotSupportedException if schema not supported
     * @exception NamingException if a naming exception is encountered
     */
    public DirContext getSchemaClassDefinition(String name)
        throws NamingException {

        if ( ! use_AVMRemote_ ) 
        {
            log.debug("    AVMFileDirContext: using file system");
            return super.getSchemaClassDefinition( name );
        }

        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote


        throw new OperationNotSupportedException();
    }


    /**
     * Searches in a single context for objects that contain a specified set
     * of attributes, and retrieves selected attributes. The search is
     * performed using the default SearchControls settings.
     *
     * @param name the name of the context to search
     * @param matchingAttributes the attributes to search for. If empty or
     * null, all objects in the target context are returned.
     * @param attributesToReturn the attributes to return. null indicates
     * that all attributes are to be returned; an empty array indicates that
     * none are to be returned.
     * @return a non-null enumeration of SearchResult objects. Each
     * SearchResult contains the attributes identified by attributesToReturn
     * and the name of the corresponding object, named relative to the
     * context named by name.
     * @exception NamingException if a naming exception is encountered
     */
    @SuppressWarnings("unchecked")
    public NamingEnumeration<javax.naming.directory.SearchResult>
    search(String     name, 
           Attributes matchingAttributes,
           String[]   attributesToReturn)
            throws    NamingException 
    {
        if ( ! use_AVMRemote_ ) 
        {
            log.debug("    AVMFileDirContext: using file system");

            // The following line is what makes me need to suppress "unchecked":
            return super.search( name, matchingAttributes, attributesToReturn );
        }

        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote


        return null;
    }


    /**
     * Searches in a single context for objects that contain a specified set
     * of attributes. This method returns all the attributes of such objects.
     * It is equivalent to supplying null as the atributesToReturn parameter
     * to the method search(Name, Attributes, String[]).
     *
     * @param name the name of the context to search
     * @param matchingAttributes the attributes to search for. If empty or
     * null, all objects in the target context are returned.
     * @return a non-null enumeration of SearchResult objects. Each
     * SearchResult contains the attributes identified by attributesToReturn
     * and the name of the corresponding object, named relative to the
     * context named by name.
     * @exception NamingException if a naming exception is encountered
     */
    @SuppressWarnings("unchecked")
    public NamingEnumeration<javax.naming.directory.SearchResult>
    search(String     name, 
           Attributes matchingAttributes)
           throws     NamingException 
    {

        if ( ! use_AVMRemote_ ) 
        {
            log.debug("    AVMFileDirContext: using file system");

            // The following line is what makes me need to suppress "unchecked":
            return super.search( name, matchingAttributes );
        }

        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote



        return null;
    }


    /**
     * Searches in the named context or object for entries that satisfy the
     * given search filter. Performs the search as specified by the search
     * controls.
     *
     * @param name the name of the context or object to search
     * @param filter the filter expression to use for the search; may not be
     * null
     * @param cons the search controls that control the search. If null,
     * the default search controls are used (equivalent to
     * (new SearchControls())).
     * @return an enumeration of SearchResults of the objects that satisfy
     * the filter; never null
     * @exception InvalidSearchFilterException if the search filter specified
     * is not supported or understood by the underlying directory
     * @exception InvalidSearchControlsException if the search controls
     * contain invalid settings
     * @exception NamingException if a naming exception is encountered
     */
    @SuppressWarnings("unchecked")
    public NamingEnumeration<javax.naming.directory.SearchResult>
    search(String         name, 
           String         filter,
           SearchControls cons)
           throws         NamingException 
    {
        if ( ! use_AVMRemote_ ) 
        {
            log.debug("    AVMFileDirContext: using file system");

            // The following line is what makes me need to suppress "unchecked":
            return super.search( name, filter, cons );
        }

        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote




        return null;
    }


    /**
     * Searches in the named context or object for entries that satisfy the
     * given search filter. Performs the search as specified by the search
     * controls.
     *
     * @param name the name of the context or object to search
     * @param filterExpr the filter expression to use for the search.
     * The expression may contain variables of the form "{i}" where i is a
     * nonnegative integer. May not be null.
     * @param filterArgs the array of arguments to substitute for the
     * variables in filterExpr. The value of filterArgs[i] will replace each
     * occurrence of "{i}". If null, equivalent to an empty array.
     * @param cons the search controls that control the search. If null, the
     * default search controls are used (equivalent to (new SearchControls())).
     * @return an enumeration of SearchResults of the objects that satisy the
     * filter; never null
     * @exception ArrayIndexOutOfBoundsException if filterExpr contains {i}
     * expressions where i is outside the bounds of the array filterArgs
     * @exception InvalidSearchControlsException if cons contains invalid
     * settings
     * @exception InvalidSearchFilterException if filterExpr with filterArgs
     * represents an invalid search filter
     * @exception NamingException if a naming exception is encountered
     */
    @SuppressWarnings("unchecked")
    public NamingEnumeration<javax.naming.directory.SearchResult>
    search(String         name, 
           String         filterExpr,
           Object[]       filterArgs, 
           SearchControls cons)
           throws         NamingException 
    {
        if ( ! use_AVMRemote_ ) 
        {
            log.debug("    AVMFileDirContext: using file system");

            // The following line is what makes me need to suppress "unchecked":
            return super.search( name, filterExpr, filterArgs, cons);
        }

        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote


        return null;
    }


    // The following override methods in BaseDirContext and do exactly
    // the same thing;  however, the proper java-generic return type
    // is used to suppress "unchecked conversion" errors.
    // Without this, the build shows a spurious warning:
    //
    //       Note: Recompile with -Xlint:unchecked for details.
    //
    // The only other alternative was to set a SuppressWarnings annotation 
    // on the entire class, which seemed wrong also.

    /**
    * Searches in the named context or object for entries that satisfy the 
    * given search filter. Performs the search as specified by the search 
    * controls.
    * 
    * @param name the name of the context or object to search
    * @param filterExpr the filter expression to use for the search. 
    * The expression may contain variables of the form "{i}" where i is a 
    * nonnegative integer. May not be null.
    * @param filterArgs the array of arguments to substitute for the 
    * variables in filterExpr. The value of filterArgs[i] will replace each 
    * occurrence of "{i}". If null, equivalent to an empty array.
    * @param cons the search controls that control the search. If null, the 
    * default search controls are used (equivalent to (new SearchControls())).
    * @return an enumeration of SearchResults of the objects that satisy the 
    * filter; never null
    * @exception ArrayIndexOutOfBoundsException if filterExpr contains {i} 
    * expressions where i is outside the bounds of the array filterArgs
    * @exception InvalidSearchControlsException if cons contains invalid 
    * settings
    * @exception InvalidSearchFilterException if filterExpr with filterArgs 
    * represents an invalid search filter
    * @exception NamingException if a naming exception is encountered
    */
    public NamingEnumeration<javax.naming.directory.SearchResult>
    search(Name           name, 
           String         filterExpr, 
           Object[]       filterArgs, 
           SearchControls cons)
           throws         NamingException 
    {
        return search(name.toString(), filterExpr, filterArgs, cons);
    }

    /**
    * Searches in the named context or object for entries that satisfy the 
    * given search filter. Performs the search as specified by the search 
    * controls.
    * 
    * @param name the name of the context or object to search
    * @param filter the filter expression to use for the search; may not be 
    * null
    * @param cons the search controls that control the search. If null, 
    * the default search controls are used (equivalent to 
    * (new SearchControls())).
    * @return an enumeration of SearchResults of the objects that satisfy 
    * the filter; never null
    * @exception InvalidSearchFilterException if the search filter specified 
    * is not supported or understood by the underlying directory
    * @exception InvalidSearchControlsException if the search controls 
    * contain invalid settings
    * @exception NamingException if a naming exception is encountered
    */
    public NamingEnumeration<javax.naming.directory.SearchResult>
    search(Name           name, 
           String         filter, 
           SearchControls cons)
           throws         NamingException 
    {
        return search(name.toString(), filter, cons);
    }

    /**
    * Searches in a single context for objects that contain a specified set 
    * of attributes. This method returns all the attributes of such objects. 
    * It is equivalent to supplying null as the atributesToReturn parameter 
    * to the method search(Name, Attributes, String[]).
    * 
    * @param name the name of the context to search
    * @param matchingAttributes the attributes to search for. If empty or 
    * null, all objects in the target context are returned.
    * @return a non-null enumeration of SearchResult objects. Each 
    * SearchResult contains the attributes identified by attributesToReturn 
    * and the name of the corresponding object, named relative to the 
    * context named by name.
    * @exception NamingException if a naming exception is encountered
    */
    public NamingEnumeration<javax.naming.directory.SearchResult>
    search(Name name, Attributes matchingAttributes) throws NamingException 
    {
        return search(name.toString(), matchingAttributes);
    }

    /**
    * Searches in a single context for objects that contain a specified set 
    * of attributes, and retrieves selected attributes. The search is 
    * performed using the default SearchControls settings.
    * 
    * @param name the name of the context to search
    * @param matchingAttributes the attributes to search for. If empty or 
    * null, all objects in the target context are returned.
    * @param attributesToReturn the attributes to return. null indicates 
    * that all attributes are to be returned; an empty array indicates that 
    * none are to be returned.
    * @return a non-null enumeration of SearchResult objects. Each 
    * SearchResult contains the attributes identified by attributesToReturn 
    * and the name of the corresponding object, named relative to the 
    * context named by name.
    * @exception NamingException if a naming exception is encountered
    */
    public NamingEnumeration<javax.naming.directory.SearchResult>
    search(Name       name, 
           Attributes matchingAttributes,
           String[]   attributesToReturn)
           throws     NamingException 
    {
        return search(name.toString(), matchingAttributes, attributesToReturn);
    }

    /**
    * Enumerates the names bound in the named context, along with the 
    * objects bound to them. The contents of any subcontexts are not 
    * included.
    * <p>
    * If a binding is added to or removed from this context, its effect on 
    * an enumeration previously returned is undefined.
    * 
    * @param name the name of the context to list
    * @return an enumeration of the bindings in this context. 
    * Each element of the enumeration is of type Binding.
    * @exception NamingException if a naming exception is encountered
    */
    public NamingEnumeration<javax.naming.Binding>
    listBindings(Name name) throws NamingException 
    {
        return listBindings(name.toString());
    }

    /**
    * Enumerates the names bound in the named context, along with the class 
    * names of objects bound to them. The contents of any subcontexts are 
    * not included.
    * <p>
    * If a binding is added to or removed from this context, its effect on 
    * an enumeration previously returned is undefined.
    * 
    * @param name the name of the context to list
    * @return an enumeration of the names and class names of the bindings in 
    * this context. Each element of the enumeration is of type NameClassPair.
    * @exception NamingException if a naming exception is encountered
    */
    public NamingEnumeration<javax.naming.NameClassPair>
    list(Name name) throws NamingException 
    {
        return list(name.toString());
    }


    /**
     * List the resources which are members of a collection
     * (not a part of BaseDirContext).
     *
     * @return Vector containg NamingEntry objects
     */
    protected ArrayList<NamingEntry> 
    avm_list( AVMNodeDescriptor avm_node,
              boolean           include_background
            )
    {
        // Called by:
        //      listBindings(String name)
        //      list(String name)

        ArrayList<NamingEntry> entries = new ArrayList<NamingEntry>();

        if (! avm_node.isDirectory()) { return entries; }

        Map<String, AVMNodeDescriptor> avm_entries = null;

        try 
        { 
            if ( include_background )
            {
                avm_entries = Service_.getDirectoryListing( avm_node ); 
            }
            else
            {
                // Only fetch directly contained (foreground) objects
                String fg_path =  avm_node.getPath();

                log.debug("AVMFileDirContext getDirectoryListingDirect: " + fg_path);

                avm_entries = Service_.getDirectoryListingDirect( 
                                  avmRootVersion_, fg_path);
            }
        }
        catch (Exception e)
        {
            // TODO - emit message?
            return entries;
        }

        NamingEntry entry = null;

        for ( Map.Entry<String, AVMNodeDescriptor> avm_entry :
              avm_entries.entrySet()
            )
        {
            String            child_name = avm_entry.getKey();
            AVMNodeDescriptor child_node = avm_entry.getValue();
            Object            object     = null;

            if ( child_node.isDirectory() )
            {
                AVMFileDirContext tempContext = new AVMFileDirContext(env);
                tempContext.setDocBase( avmRootVersion_, child_node.getPath() );
                tempContext.setAllowLinking(getAllowLinking());
                tempContext.setCaseSensitive(isCaseSensitive());
                object = tempContext;
            }
            else
            {
                object = new AVMFileResource( avmRootVersion_, 
                                              child_node.getPath()
                                            );
            }

            entry = new NamingEntry( child_name, object, NamingEntry.ENTRY );
            entries.add(entry);
        }
        return entries;
    }




    /**
     * Return a File object representing the specified normalized
     * context-relative path if it exists and is readable.  Otherwise,
     * return <code>null</code>.
     *
     * @param name Normalized context-relative path (with leading '/')
     */
    protected File file(String name) {

        File file = new File(this.base, name);
        if (file.exists() && file.canRead()) 
        {
            if (allowLinking) {return file; }
        	
            // Check that this file belongs to our root path
            String canPath = null;

            try                   { canPath = file.getCanonicalPath(); }
            catch (IOException e) { }

            if (canPath == null) { return null; } 

            // Bugzilla 38154: after release() the absoluteBase is null, leading to an NPE
            if (this.absoluteBase == null) { return null; }


            // Check to see if going outside of the web application root
            if (!canPath.startsWith(this.absoluteBase)) { return null; } 

            // Case sensitivity check
            if (this.caseSensitive) 
            {
                String fileAbsPath = file.getAbsolutePath();

                if (fileAbsPath.endsWith(".")) { fileAbsPath = fileAbsPath + "/"; }

                String absPath = normalize(fileAbsPath);

                if (canPath != null) { canPath = normalize(canPath);}

                if ((this.absoluteBase.length() < absPath.length()) && 
                    (this.absoluteBase.length() < canPath.length())
                   ) 
                {
                    absPath = absPath.substring(this.absoluteBase.length() + 1);
                    if ((canPath == null) || (absPath == null)) { return null;}

                    if (absPath.equals("")) { absPath = "/";}

                    canPath = canPath.substring(this.absoluteBase.length() + 1);

                    if (canPath.equals("")) { canPath = "/"; }

                    if (!canPath.equals(absPath)) { return null;}
                }
            }

        } 
        else { return null; }

        return file;

    }


    /**
     * This specialized resource attribute implementation does some lazy
     * reading (to speed up simple checks, like checking the last modified
     * date).
     */
    protected class AVMFileResourceAttributes extends ResourceAttributes 
    {
        AVMNodeDescriptor avm_node_;
        String            name_;
        String            type_;

        public AVMFileResourceAttributes(AVMNodeDescriptor avm_node, String name)
        {
            avm_node_ = avm_node;
            name_     = name;
        }

        protected boolean accessed = false;
        protected String canonicalPath = null;

        /**
        *   Fetches all the attribute IDs.
        *   Currently, this is the list:
        *  <pre>
        *     "creationdate"
        *     "getlastmodified"
        *     "displayname"
        *     "resourcetype"
        *     "getcontentlength"
        *  </pre>
        */
        @SuppressWarnings("unchecked")
        public NamingEnumeration<java.lang.String> 
        getIDs()
        {
            log.debug("AVMFileResourceAttributes.getIDs()");
            return super.getIDs();
        }

        /**
        *   Returns a NamingEnumeration of type BasicAttribute,
        *   (each BasicAttribute the tuple of an attribute listed 
        *   in getIDs(), and its corresponding value).
        */
        @SuppressWarnings("unchecked")
        public NamingEnumeration<? extends javax.naming.directory.Attribute> 
        getAll()
        {
            log.debug("AVMFileResourceAttributes.getAll()");
            return super.getAll();    // erasure annoyance
        }


        /**
         * Is collection.
         */
        public boolean isCollection() 
        {
            log.debug("AVMFileResourceAttributes.isCollection()");
            return ( avm_node_.isDirectory() );
        }


        /**
         * Get content length.
         *
         * @return content length value
         */
        public long getContentLength() 
        {
            log.debug("AVMFileResourceAttributes.getContentLength()");
            return avm_node_.getLength();
        }


        /**
         * Get creation time.
         *
         * @return creation time value
         */
        public long getCreation() 
        {
            log.debug("AVMFileResourceAttributes.getCreation()");
            this.creation = avm_node_.getCreateDate();
            return this.creation;
        }


        /**
         * Get creation date.
         *
         * @return Creation date value
         */
        public Date getCreationDate() 
        {
            log.debug("AVMFileResourceAttributes.getCreationDate()");
            if ( this.creationDate == null )
            {
                this.creationDate = new Date(  avm_node_.getCreateDate() );
            }
            return this.creationDate;
        }


        /**
         * Get last modified time.
         *
         * @return lastModified time value
         */
        public long getLastModified() 
        {
            log.debug("AVMFileResourceAttributes.getLastModified()");

            // Tracking lookup dependencies in getLastModified()
            // rather than in lookup() to avoid problems with 
            // missing dependencies due to caching effects.
            //
            // This means you sometimes get "dependencies" on WEB-INF/...
            // but these are easy to filter out.  While getLastModified
            // is invoked multiple times per URI request on the same
            // dependent assets, duplicate entries on the same file
            // are suppressed, so redundant calls are harmless.

            CacheControlFilter.AddLookupDependency(getCanonicalPath());

            return avm_node_.getModDate();
        }

        /**
         * Get lastModified date.
         *
         * @return LastModified date value
         */
        public Date getLastModifiedDate() 
        {
            log.debug("AVMFileResourceAttributes.getLastModifiedDate()");

            if ( this.lastModifiedDate == null )
            {
                this.lastModifiedDate = new Date( avm_node_.getModDate() );
            }
            return this.lastModifiedDate;
        }


        /**
         * Get name.
         *
         * @return Name value
         */
        public String getName() 
        { 
            return name_; 
        }

        /**
         * Get resource type.
         *
         * @return String resource type
         */
        public String getResourceType() 
        {
            // TODO: I think I'm free to use any typenames at all,
            //       but that needs to be verified. 
            //

            if ( type_ == null )
            {
                switch ( avm_node_.getType() )
                {
                    case AVMNodeType.PLAIN_FILE:
                        type_ = "PLAIN_FILE";
                        break;

                    case AVMNodeType.PLAIN_DIRECTORY:
                        type_ = "PLAIN_DIRECTORY";
                        break;

                    case AVMNodeType.LAYERED_FILE:
                        type_ = "LAYERED_FILE";
                        break;

                    case AVMNodeType.LAYERED_DIRECTORY:
                        type_ = "LAYERED_DIRECTORY";
                        break;

                    default:
                        // TODO: I'm not thrilled with this way of handling 
                        //       unknown types, but it's what the base class 
                        //       implementation does.  Perhaps there's a reason
                        //       why, so until I dig deeper, just mimic it:
                        type_ = "";
                }
            }
            return type_;
        }
        
        /**
         * Get canonical path.
         * 
         * @return String the file's canonical path
         */
        public String getCanonicalPath() 
        {
            log.debug("AVMFileResourceAttributes.getCannonicalPath(): " + avm_node_.getPath());

            // TODO:  should this include name mangling for version numbers, 
            //        or would that mess things up elsewhere?   

            return avm_node_.getPath();
        }
    }

    /**
    * This specialized resource implementation avoids opening the IputStream
    * to the file right away (which would put a lock on the file).
    */
    protected class AVMFileResource extends Resource 
    {
        /** File length.  */
        protected long length = -1L;

        protected int    root_version_;
        protected String resource_path_;


        public AVMFileResource( int root_version, String resource_path )
        {
            root_version_  = root_version;
            resource_path_ = resource_path;
        }


        /**
         * Content accessor.
         *
         * @return InputStream
         */
        public InputStream streamContent() throws IOException 
        {
            // TODO:
            //    During the create of AVMFileResource, you give it
            //    all the data it will ever need to be able to 
            //    streamContent() back later on.  Here, a file
            //    is being used, but for AVMRemote, it should
            //    be an node descriptor..

            return AVMFileDirContext.Service_.getFileInputStream(root_version_,
                                                                 resource_path_);
        }
    }
}

