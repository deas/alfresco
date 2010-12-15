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
*  File    AVMStandardContext.java
*----------------------------------------------------------------------------*/



package org.alfresco.catalina.context;

import org.apache.catalina.core.StandardContext;
import org.apache.catalina.LifecycleException;


public class   AVMStandardContext 
       extends    StandardContext
{
    private String encoded_path_ = null;

    /**
     * Create a new StandardContext component with the default basic Valve.
     */
    public AVMStandardContext() {
        super();
    }

    /**
     * The descriptive information string for this implementation.
     */
    private static final String info =
        "org.apache.catalina.context.AVMStandardContext/1.0";



    /**
     * Stop this Context component.
     *
     * @exception LifecycleException if a shutdown error occurs
     */
    public synchronized void stop() throws LifecycleException 
    {
        super.stop();
    }

    /**
     * Start this Context component.
     *
     * @exception LifecycleException if a startup error occurs
     */
    public synchronized void start() throws LifecycleException 
    {
        super.start();
    }

    /**
     * Reload this web application, if reloading is supported.
     * <p>
     * <b>IMPLEMENTATION NOTE</b>:  This method is designed to deal with
     * reloads required by changes to classes in the underlying repositories
     * of our class loader.  It does not handle changes to the web application
     * deployment descriptor.  If that has occurred, you should stop this
     * Context and create (and start) a new Context instance instead.
     *
     * @exception IllegalStateException if the <code>reloadable</code>
     *  property is set to <code>false</code>.
     */
    public synchronized void reload() 
    {
        // jcox TODO RESUME
        // This might be where I'd do the fancy footwork necessary 
        // to deal with recursive reload.
        //
        // The StandardContextValve does a 1 second like this
        // to avoid talking to webapps that are in the process
        // of reloading:
        //
        //        // Wait if we are reloading
        //        while (context.getPaused()) 
        //        {
        //            try   { Thread.sleep(1000); } 
        //            catch (InterruptedException e) { ; }
        //        }
        //

        super.reload();   // setPaused(true) ...stop/start...setPaused(false)

        // jcox TODO: 
        //      Consider doing a recursive reload() on kids here.


    }
        
    //-------------------------------------------------------------------------
    /**
    *   Returns the URL-encoded path of this context, stripping away any
    *   context path mangling created within a virtualized subrequest.
    *
    *   The value returned by getEncodedPath() is typically used when 
    *   generating cookies  (e.g.:  session cookies).   It's important
    *   to strip away context path manging info because the end user's
    *   browser doesn't know anything about this, and won't send
    *   path/scope-restricted cookies properly if it's concept of 
    *   the context path differs from the context path announced by 
    *   the cookie.
    *
    *   <pre>
    *
    *   Example 1: 
    *        URL:  http://alice.mysite.www--sandbox.<...>:8180/hello.txt
    *        webapp:           "ROOT "
    *        getPath()         "/$-1$mysite--alice$ROOT"
    *        getEncodedPath()  "/"
    *
    *   Example 2: 
    *        URL:  http://alice.mysite.www--sandbox.<...>:8180/moo/hello.txt
    *        webapp:           "moo"
    *        getPath()         "/$-1$mysite--alice$moo"
    *        getEncodedPath()  "/moo"
    *   </pre>
    */
    //-------------------------------------------------------------------------
    public String getEncodedPath() 
    {
        if ( encoded_path_ != null ) { return encoded_path_; }
        encoded_path_ = getPath();
        
        int index =  encoded_path_.lastIndexOf('$');
        if ( index < 0 || ! encoded_path_.startsWith("/$") )
        { 
            return encoded_path_;
        }

        encoded_path_ =  encoded_path_.substring(index + 1);
        if ( encoded_path_.equals("ROOT") ) { encoded_path_ = ""; }

        encoded_path_ = urlEncoder.encode( "/" + encoded_path_ );

        return encoded_path_ ;
    }

    

    /**
     * Return a String representation of this component.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer();
        if (getParent() != null) {
            sb.append(getParent().toString());
            sb.append(".");
        }
        sb.append("AVMStandardContext[");
        sb.append(getName());
        sb.append("]");
        return (sb.toString());

    }
}
