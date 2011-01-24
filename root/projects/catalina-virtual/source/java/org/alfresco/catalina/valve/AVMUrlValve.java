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
*
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    AVMUrlValve.java
*----------------------------------------------------------------------------*/

package org.alfresco.catalina.valve;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import org.alfresco.catalina.context.AVMStandardContext;
import org.alfresco.catalina.host.AVMHost;
import org.alfresco.catalina.host.AVMHostConfig;
import org.alfresco.catalina.host.AVMHostMatch;
import org.alfresco.catalina.host.AVMResourceBinding;
import org.alfresco.config.JNDIConstants;
import org.alfresco.filter.CacheControlFilter;
import org.apache.catalina.Container;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.LifecycleSupport;
import org.apache.catalina.valves.ValveBase;
import org.apache.tomcat.util.buf.MessageBytes;


/**
* Rewrites requests to make them easy for Alfresco to virtualize.
* <pre>
*     RFCs regarding hostnames & fully qualified domain names (FQDN):
*     608, 810, 952, 1035, and 1123.   The following PCRE-style
*     regex defines a valid label within a FQDN:
*
*          ^[a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]$
*
*     Less formally:
*
*          o  Case insensitive
*          o  First/last character:  alphanumeric
*          o  Interior characters:   alphanumeric plus hyphen
*          o  Minimum length:        2  characters
*          o  Maximum length:        63 characters
*
*     The minimum 2-char length does not seem to be enforced by
*     nameservers/resovlers in the real world.  For example, djbdns
*     uses nameserver names like:   a.ns.example.com,  b.ns.example.com, etc.
*
*     The FQDN (fully qualified domain name) is the following constraints:
*
*          o  Maximum 255 characters          (e.g.: www.foo.example.com)
*          o  Must contain at least one alpha (i.e.: [a-z])
*
*     Thus, the following FQDN would be illegal because it contains
*     a hostname label that is too long (64 > 63):
*
*        aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.com
*
*     But the following FQDN would be ok because no host label is > 63 chars,
*     and the total length is less than 255 chars:
*
*     moo.cow.aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.com
*
*     Ultimately, I18N-encoded domain names will be supported via the IDNA 
*     (Internationalizing Domain Names In Applications) standard.   
*     IDNA encodes host labels that would normally contain I18N chars using 
*     a "xn--" prefix.  Apps present IDNA URLs in decoded form, but only
*     the "traditional" DNS characters ever go over the wire.  For details,
*     see RFC-3490 (http://www.ietf.org/rfc/rfc3490.txt)
*     and RFC-3492 (http://www.ietf.org/rfc/rfc3492.txt).
*
*     The encoding scheme used for virtualization has been designed to
*     be IDNA-friendly; when GUI support becomes available, no changes
*     will be needed as far as the virtualization logic is concnered.
*
* </pre>
*/
public class AVMUrlValve extends ValveBase implements Lifecycle
{
    protected static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog( AVMUrlValve.class );
    
    static Pattern first_seg_pattern_ =  Pattern.compile( "^/([^/]*)" );

    /**
    * Lifecycle event support.
    */
    protected LifecycleSupport lifecycle = new LifecycleSupport(this);

    /**
    *  @exclude
    *
    *  <a href='http://www-128.ibm.com/developerworks/library/j-threads3.html'>
    *  ThreadLocal</a> flag that indicates whether this is a subrequest.
    *  <p>
    *  Usage:
    *  <pre>
    *           if ( AVMUrlValve_invoked_.get() == Boolean.TRUE )
    *           {
    *               ... it's a subrequest...
    *           }
    *  </pre>
    *  To understand how ThreadLocal really works,
    *  <a href='
    *http://www.koders.com/java/fid6DE846DE6F718A19D7B5F6883CE1343C3AAA08CD.aspx
    *'>click here</a>.
    */
    protected static ThreadLocal AVMUrlValve_invoked_ = new ThreadLocal();


    public void addLifecycleListener(LifecycleListener listener)
    {
        lifecycle.addLifecycleListener(listener);
    }

    public LifecycleListener[] findLifecycleListeners()
    {
        return lifecycle.findLifecycleListeners();
    }

    public void removeLifecycleListener(LifecycleListener listener)
    {
        lifecycle.removeLifecycleListener(listener);
    }

    public void start() throws LifecycleException
    {
    }

    public void stop() throws LifecycleException
    {
    }


    /**
    *  Transforms a version (e.g.: -1), a storeName (e.g.:  "mysite--alice"),
    *  and a webappName (e.g.: "ROOT") into a virtualized context name
    *  (e.g.:  "/$-1$mysite--alice$ROOT")
    */
    public static String GetContextNameFromStoreName( int    version, 
                                                      String storeName, 
                                                      String webappName
                                                    )
    {
        return  "/"         +    // context paths start with "/"
                "$"         +    // delimiter
                version     +    // TODO: ".version--vXXXXX."
                "$"         +    // delimiter
                storeName   +    // (...).www--sandbox.
                "$"         +    // delimiter
                webappName;
    }


    /**
    *  Transforms a version (e.g.: -1), and a storePath 
    *  (e.g.:  "mysite--alice:/www/avm_webapps/ROOT")
    *  into a virtualized context name (e.g.:  "/$-1$mysite--alice$ROOT").
    *  On failure, null is returned.
    */
    public static String GetContextNameFromStorePath( int    version, 
                                                      String storePath
                                                    )
    {
        int store_tail  = storePath.indexOf(':');
        int webapp_head = storePath.lastIndexOf('/') + 1;

        if ( (store_tail < 0 )   || 
             (webapp_head <= 0 ) || 
             (webapp_head >= storePath.length())
           ) 
        { 
            return null; 
        }

        String store_name   = storePath.substring(0, store_tail);
        String webapp_name  = storePath.substring(webapp_head, storePath.length() );
        return  "/$" + version + "$" + store_name + "$" + webapp_name; 
    }

    void sendErrorPageResponse(Response response, String html_error_msg)
    {
        try 
        {
            response.setStatus(404);
            response.setContentType("text/html");
            response.setCharacterEncoding("utf-8");
            Writer writer = response.getReporter();

            // A null writer indicates a hard commit has already 
            // taken place...  which should never happen here.

            if (writer != null) { writer.write( html_error_msg); }
        }
        catch (Exception e) { /* nothing to do */ }
    }



    /**
    * This method is called by Tomcat's pipeline mechanism on every new request.
    *
    * Within <pre>
    *           $TOMCAT_HOME/conf/server.xml
    * </pre>
    *
    * this valve is directly contained by the &lt;Engine&gt; node, thereby
    * enabling it to view every request to localhost prior to any other part
    * of the servlet engine. For example:
    * <pre>
    *
    *   &lt;Engine name="Catalina" defaultHost="localhost"&gt;
    *         &lt;Valve className="org.alfresco.catalina.valve.AVMUrlValve"/&gt;
    *         ...
    *   &lt;/Engine&gt;
    * </pre>
    *
    * @throws IOException
    * @throws ServletException
    *
    * @param request  The request, prior to the servlet engine seeing it.
    * @param response The reqponse, after the servlet engine is done with it.
    */

    @SuppressWarnings("unchecked")
    public void invoke( Request  request,
                        Response response
                      ) throws IOException, ServletException
    {
        // Request/Response implement HttpServlet{Request|Response}
        // Concretely, Request is:    org.apache.catalina.connector.Request

        if (AVMUrlValve_invoked_.get() == Boolean.TRUE)
        {
            // A reverse proxy name-mangled the request URI
            // to virtualize webapp/version info, and then
            // performed a recursive subrequest.  See the comment:
            // "Reinvoke the whole request recursively" below.
            //
            // That subrequest is being handled right here.
            //
            // While URI-mangling in the initial request
            // forces Catalina's Mapper algorithm to select
            // the proper webapp context, it's important
            // to present an un-mangled URI to servlets
            // because they might construct links based on it,
            // and embed them within web pages.  Therefore,
            // it's time to unmangle.  This is safe because
            // the Mapper is done, and won't ever see the
            // de-virtualized request.
            //
            org.apache.coyote.Request req = request.getCoyoteRequest();

            MessageBytes decoded_uri_MB = req.decodedURI();
            MessageBytes request_uri_MB = req.requestURI();

            String decoded_uri = req.decodedURI().toString();
            String request_uri = req.requestURI().toString();

            decoded_uri_MB.recycle();
            request_uri_MB.recycle();

            // Example of AVM-mangled subreq URI:
            //   /$-1$store-1$servlets-examples/servlet/RequestInfoExample
            //
            // After unmangling:
            //   /servlets-examples/servlet/RequestInfoExample
            //
            // Note that it's critical that the mangled URI
            // contains all the repository & version information
            // needed within its first "/"-delimited segment.
            // This allows J2EE Mapper logic to work as-is.


            decoded_uri = unMangleAVMuri( decoded_uri );
            request_uri = unMangleAVMuri( request_uri );

            decoded_uri_MB.setString( decoded_uri );
            request_uri_MB.setString( request_uri );

            // Create the same illusion for getContextPath()
            // that was just done for       getRequestURI()
            // so that servlets won't get confused.

            MessageBytes context_path_MB = request.getContextPathMB();
            String       context_path    = request.getContextPath();
            context_path_MB.recycle();

            try
            {
               context_path = unMangleAVMuri( context_path );
            }
            catch (Exception bad_context)
            {
                // Handle corrupted or null context paths gracefully.
                // The context path can be null if the store is present,
                // but the webapp itself is not.

                AVMUrlValve_invoked_.set(null);   

                // TODO:  Sometimes, a 503 error is sent instead of this page.

                sendErrorPageResponse( response,
                    "<html>\n"                                                  +
                    "  <head><title>Virtual website not found</title></head>\n" +
                    "  <body>\n"                                                +
                    "     <p>\n"                                                +
                    "     <h2>Virtual website not found</h2>\n"                 + 
                    "     <br>\n"                                               +
                    "     &nbsp;&nbsp;&nbsp;&nbsp;No such webapp"               + 
                    "  </body>\n" +
                    "</html>" 
                );
                return;
            }

            context_path_MB.setString( context_path );

            // Create the same illusion for getRequestURL()
            // by resetting the AVM server name to the
            // name of the reverse proxy its servicing.

            int          port_colon_offset;
            String       reverse_proxy = request.getHeader("Host");
            MessageBytes server_MB     = req.serverName();

            if ( (port_colon_offset= reverse_proxy.indexOf(':')) >= 0 )
            {
                reverse_proxy = reverse_proxy.substring(0,port_colon_offset);
            }
            server_MB.recycle();
            server_MB.setString( reverse_proxy );


            //---------------------------------------------
            // Do the real work of servicing the subrequest
            //---------------------------------------------
            try 
            {
                getNext().invoke(request, response);
            }
            catch (Exception e)  
            { 
                /* nothing to do here */ 
            }
            finally  
            {
                // no matter what, turn off subrequest flag
                AVMUrlValve_invoked_.set(null);
            }

            return;
        }

        AVMUrlValve_invoked_.set(Boolean.TRUE);   // flag: invoke() got called

        boolean in_lookup_dependency = false;

        if  ( null != 
              request.getHeader( CacheControlFilter.LOOKUP_DEPENDENCY_HEADER)
            )
        {  
            CacheControlFilter.StartLookupDependency();
            in_lookup_dependency = true;
        }

        org.apache.coyote.Request req = request.getCoyoteRequest();

        MessageBytes  server_MB       = req.serverName();
        String        server_name     = server_MB.toString();
        Host          host            = request.getHost();
        int           avm_port        = req.getServerPort();

        AVMHostMatch hostmatch =  AVMHost.getAVMHostMatch( server_name );

        if ( hostmatch == null )                  // Note that at this point
        {                                         // AVMUrlValve_invoked_
            getNext().invoke(request, response);  // is true. Therefore,
            AVMUrlValve_invoked_.set(null);       // this filter can be nested.

            if ( in_lookup_dependency )
            {
                 CacheControlFilter.StopLookupDependency();
            }

            return;
        }

        // A particular AVMHost got selected via a reverseProxyBinding.
        // Retrieve the Matcher used, along with the AVMResourceBinding
        // employed by this virtual host.
        //
        // Use the AVMResourceBinding object and the Matcher to
        // infer the virtual repository name and resource version.
        // This will be used later to fetch the InputStream of
        // of a resource via JNDI calls.

        Matcher            rproxy_match = hostmatch.getMatch();
        AVMHost            avm_host     = hostmatch.getHost();
        AVMResourceBinding binding      = avm_host.getResourceBinding();
        String   store_name  = binding.getRepositoryName(rproxy_match);
        String   version_str = binding.getVersion(       rproxy_match);

        int version = 0;

        try    { version = Integer.parseInt( version_str ); }
        catch  (Exception num_ex)
        {
            // The regex for this AVMHost will prevent most badly 
            // formateed strings from ever getting this far.  Checking 
            // for a badly formatted string is an extra safety measure.
            // 
            // The request will fail because the version is bad.
            // Therefore, there won't be any subrequest, so unset
            // the subrequest flag.

            if ( version_str == null ) { version_str = ""; }

            AVMUrlValve_invoked_.set(null);   
            if ( in_lookup_dependency )
            {
                 CacheControlFilter.StopLookupDependency();
            }


            sendErrorPageResponse( response,
                "<html>\n"                                                    +
                "  <head><title>Virtual website not found</title></head>\n"   +
                "  <body>\n"                                                  +
                "     <p>\n"                                                  +
                "     <h2>Virtual website not found</h2>\n"                   + 
                "     <br>\n"                                                 +
                "     &nbsp;&nbsp;&nbsp;&nbsp;Bad version:&nbsp;&nbsp;"       + 
                "     &nbsp;&nbsp;<tt>" + version_str + "</tt>\n"             +
                "  </body>\n" +
                "</html>" 
            );

            return;
        }


        // Handle requests for bad virtual hosts gracefully

        if ( (store_name == null) || store_name.equals("") )
        {
            // When the request has failed due to no mapping
            // between virtual host and AVM store, there
            // won't be any subrequest.  Therefore, unset
            // the subrequest flag.

            AVMUrlValve_invoked_.set(null);   
            if ( in_lookup_dependency )
            {
                 CacheControlFilter.StopLookupDependency();
            }

            sendErrorPageResponse( response,
                "<html>\n"                                                  +
                "  <head><title>Virtual website not found</title></head>\n" +
                "  <body>\n"                                                +
                "     <p>\n"                                                +
                "     <h2>Virtual website not found</h2>\n"                 + 
                "     <br>\n"                                               +
                "     &nbsp;&nbsp;&nbsp;&nbsp;Bad host name:&nbsp;&nbsp;"   + 
                "     &nbsp;&nbsp;<tt>" + server_name + "</tt>\n"           +
                "  </body>\n" +
                "</html>" 
            );

            return;
        }

        host = avm_host;

        // Transform requests to the reverse proxy into to the configured
        // Tomcat AVM-based <Host> (e.g.: "avm.alfresco.localhost")
        // that the reverseProxyBinding regex matched.

        String avm_hostname =   host.getName();

        server_MB.recycle();
        server_MB.setString( avm_hostname );


        // *Not* setting the "Host:" header because servlets really want
        // to see the reverse proxy name, not the AVMHost name.


        MessageBytes decoded_uri_MB = req.decodedURI();
        MessageBytes request_uri_MB = req.requestURI();

        String decoded_uri = req.decodedURI().toString();
        String request_uri = req.requestURI().toString();

        // Need to remove buffered Chars representation
        // so that when postParseRequest invokes toChars()
        // we won't just pick up the old values.

        decoded_uri_MB.recycle();
        request_uri_MB.recycle();

        // During bootstrap, when the AVMHost was initialized by
        // AVMHostConfig, the repository was scanned repositories
        // with metadata of the form:  
        //
        //         .dns.<hostname> = <store-path> 
        //
        // Suppose the avm has the following layout:
        //
        //           <repoName>:
        //               /
        //               |
        //              www
        //               |
        //            avm_webapps
        //                      |
        //                      +-- ROOT
        //                      |
        //                      +-- my_webapp
        //                      |
        //                      ...
        //
        //  The AVMHost has a single appBase "avm_webapps"
        //  achieves virtualization by name-mangling the webapps
        //  during AVMHostConfig's auto deployment.  For example,
        //
        //  Let:   <storeName> =  store-3
        //         <version>  =  -1
        //
        //  Then   my_webapp is added to the AVMHost as:  /$-1$store-3$my_webapp
        //  and    ROOT      is added to the AVMHost as:  /$-1$store-3$ROOT
        //
        //  Put another way, the default  HostConfig class would
        //  register webapps with names like this:
        //
        //              ""                       ("ROOT" webapp context path)
        //              "/balancer"
        //              "/host-manager"
        //              "/jsp-examples"
        //              "/manager"
        //              "/my_webapp"
        //              "/servlets-examples"
        //              "/tomcat-docs"
        //
        //  By contrast, the AVMHostConfig would register the following
        //  webapps for http://alice.mysite.www--sandbox.*/ :
        //
        //              "/$-1$mysite--alice$ROOT"        ("ROOT" webapp context path)
        //              "/$-1$mysite--alice$balancer"
        //              "/$-1$mysite--alice$host-manager"
        //              "/$-1$mysite--alice$jsp-examples"
        //              "/$-1$mysite--alice$manager"
        //              "/$-1$mysite--alice$my_webapp"
        //              "/$-1$mysite--alice$servlets-examples"
        //              "/$-1$mysite--alice$tomcat-docs"
        //
        // The character '$' was chosen as the name mangling delimiter
        // for the following reasons:
        //
        //      o  Legal in URLs without the need to %HH-encode
        //      o  Legal in file name on Unix/Windows
        //      o  It's an oddball character; people won't be too upset
        //         if their <storeName> can't include '$'.  Actually, it
        //         could appear, if I were willing to encode '$'
        //         (but that just seems like overkill at the moment).
        //
        // The request seen at runtime needs to be rewritten to match this
        // scheme so that the proper Context is fetched by "path"


        // Give a fake webapp name for now: ""
        // This lets us append it later when we know the real value.
        //
        String uri_prefix = GetContextNameFromStoreName( version, 
                                                         store_name,
                                                         ""
                                                       );
        
        if (avm_host.getLazyDeploy())
        {
            boolean isStoreDeployed = false; // note: only checks AVM store here (not webapps)
            Container children[] = avm_host.findChildren();
            for (int i = 0; i < children.length; i++) 
            {
                if (children[i] instanceof AVMStandardContext)
                {
                    AVMStandardContext sc = (AVMStandardContext)children[i];
                    String ctxName = sc.getName();
                    if (ctxName.startsWith(uri_prefix))
                    {
                        isStoreDeployed = true;
                    }
                }
            }
            
            if (! isStoreDeployed)
            {
                // try to deploy here and now ...
                String storePath = store_name+":"+JNDIConstants.DIR_DEFAULT_WWW_APPBASE;
                
                if (log.isDebugEnabled())
                {
                    log.debug("invoke: lazily deploy "+version+", "+storePath+" (lazy deploy is enabled)");
                }
                
                AVMHostConfig avm_host_config = avm_host.getAVMHostConfig();
                if (avm_host_config != null)
                {
                    avm_host_config.updateAllVirtualWebapps(version, storePath, true);
                }
            }
        }
        
        // In the URI:  /moo/cow/egg.html, the first_segment is
        // captured as: "moo" by the pattern's regex.

        Matcher first_segment_match = first_seg_pattern_.matcher(decoded_uri);
        String  first_segment       = "";

        if ( first_segment_match.find() )
        {
            first_segment = first_segment_match.group(1);
            if ( first_segment == null) { first_segment = "";}   // paranoia
        }

        // There are two cases that name mangling needs to handle:
        //
        //    (1)  The AVMHost has a /<storeVersion>$<storeName>$<first_segment>
        //    (2)  The context is mapped to the empty path ""  (i.e.: ROOT).
        //
        // Fortuantely, the entire path --> servlet context
        // mapping doesn't need to be done here.   The only
        // issue is this:  does the request correspond to
        // a "normal" webapp, or to the one mapped to the
        // empty path (i.e.: the "ROOT" webapp).
        //
        // Therefore, let's have a peek at the StandardContext
        // objects (which are of type Container) that are owned
        // by our host:

        if ( host.findChild(  uri_prefix + first_segment ) != null )
        {
            // The host has a context path of the form:
            //       /<storeVersion>$<storeName>$<first_segment>
            //
            decoded_uri = uri_prefix + decoded_uri.substring(1);
            request_uri = uri_prefix + request_uri.substring(1);
        }
        else
        {
            // This is a request for something within the context mapped to ""
            // (i.e.: ROOT).  Therefore, create name-mangled ROOT webapp path:
            //       /<storeVersion>$<storeName>$ROOT<uri>

            decoded_uri = uri_prefix + "ROOT" + decoded_uri;
            request_uri = uri_prefix + "ROOT" + request_uri;
        }


        // At this point, the simple-but-wrong thing to do is:
        //
        //    decoded_uri_MB.setString( decoded_uri );
        //    request_uri_MB.setString( request_uri );
        //
        // Here's why:
        //    If the value of decoded_uri contains space, 
        //    it gets automatically %HH-encoded within the 
        //    subrequest.  In a normal request, the MessageBytes 
        //    buffer is of type T_BYTES, because it's data that 
        //    has just been pulled off a TCP connection.  
        //    If you say  decoded_uri_MB.setString( decoded_uri )
        //    then decoded_uri_MB is left in state T_STR, which 
        //    causes postParseRequest to skip %HH decoding,
        //    because it assumes decoding has already been done.
        //
        // Therefore, it's critical here to use setBytes(), not
        // setString().  Sub-request Powaqqatsi at its very finest.

        byte [] uri_bytes =  decoded_uri.getBytes();
        decoded_uri_MB.setBytes( uri_bytes, 0, uri_bytes.length);

        uri_bytes =  request_uri.getBytes();
        request_uri_MB.setBytes( uri_bytes, 0, uri_bytes.length);

        // Remember what adapter we're using, so we can do a subrequest

        org.apache.coyote.Adapter  adapter =
            request.getConnector().getProtocolHandler().getAdapter();

        // Clear the state of the high-level Catalina request
        // The req is attached to this object, so we don't want
        // residual crud from the 1st pass leaking through.

        request.recycle();


        try
        {
            // Reinvoke the whole request recursively
            //

            org.apache.coyote.Response  resp = response.getCoyoteResponse();

            // If you try to do set a header here:
            //   if (...) { resp.setHeader("Cache-Control","max=4");}
            // the service might set a different value for the header;
            // this would override your setting here.
            //
            // The %HH decoding of the decoded_URI takes place in 
            // CoyoteAdapter around line 212
            // 
            // The req (CoyoteAdapter has an internal UDecoder 
            // fetchable via  req.getURLDecoder().convert(decodedURI, false)  (where false==not query)
            // 

            adapter.service(req, resp );

            //
            // If you try to set headers afterwards, the isCommitted()
            // flag might be set, and the output already sent to the 
            // any browser.   Therefore, it's better do fiddle with 
            // most output headers in a Filter (other than Location).
            // Filters are more portable anyhow, because they're part 
            // of the J2EE spec (sadly, the Valve construct isn't).


            // The Mapper will redirect a path to a DirContext
            // that does not end in '/' to one that does.
            //
            // Here's what happens:
            //
            //     CoyoteAdapter.service() ->
            //       CoyoteAdapter.postParseRequest() ->
            //          response.sendRedirect(redirectPath);
            //
            // Consider following request:
            //
            //  http://alice.mysite.www-sandbox.ip.localdomain.lan:8180/my_webapp
            //
            // This can Generates a 302 (SC_FOUND) response with
            // a Location header along the lines of:
            //
            //Location: http://avm.alfresco.localhost:8080/$-1$alice$my_webapp/
            //
            // This isn't what we really want, because proper virtualization
            // relies upon preserving the reverse proxy name on the client's
            // browser, not the AVMHost name.  Therefore, transform the
            // AVMHost request path cannonicalization generated by Tomcat's
            // internal Mapper into a reverse proxy request cannonicalization.

            String redirection = response.getHeader("Location");

            if ( redirection != null )
            {
                String avm_hostport   = avm_hostname;
                String proxy_hostport = server_name;

                // Only append a port number if not implied by the protocol.
                // Otherwise, you can end up dropping cookies and getting
                // warnings about domains/ports not matching certs *exactly*.
                // Keep browsers from making the distinction so everybody
                // gets along!

                if ( ( (avm_port != 80)  ||
                        ! redirection.substring(0,5).equalsIgnoreCase("http:"))
                     &&
                     ((avm_port != 443)  ||
                        ! redirection.substring(0,6).equalsIgnoreCase("https:"))
                   )
                {
                    avm_hostport   = avm_hostport   +  ":" + avm_port;
                    proxy_hostport = proxy_hostport +  ":" + avm_port;
                }


                redirection = reverseProxyRedirect( redirection,
                                                    avm_hostport,
                                                    proxy_hostport,
                                                    uri_prefix
                                                  );

                // Sanitized redirection, for your protection...

                response.setHeader("Location", redirection );
            }
        }
        catch (Exception e) { }
        AVMUrlValve_invoked_.set(null);
        if ( in_lookup_dependency )
        {
             CacheControlFilter.StopLookupDependency();
        }
    }


    /**
    *  Returns an un AVM-mangled version of uri.
    *  <p>
    *   Example of AVM-mangled subreq URI:
    *   <pre>
    *     /$-1$store-1$servlets-examples/servlet/RequestInfoExample
    *   </pre>
    *
    *   After unmangling:
    *   <pre>
    *     /servlets-examples/servlet/RequestInfoExample
    *   </pre>
    *
    *  If this were for a non-HEAD version (e.g.: '4'), the mangled
    *  subrequest URI might look like this:
    *   <pre>
    *     /$4$store-1$servlets-examples/servlet/RequestInfoExample
    *   </pre>
    *
    *  The URI would still contain the webapp name after unmangling:
    *   <pre>
    *     /servlets-examples/servlet/RequestInfoExample
    *   </pre>
    *
    *  However, if this is the ROOT webapp, because the context path
    *  is "", a mangle name like this:
    *  <pre>
    *     /$-1$store-1$ROOT/servlet/RequestInfoExample
    *  </pre>
    *  becomes:
    *  <pre>
    *     /servlet/RequestInfoExample
    *  </pre>
    */
    public static String unMangleAVMuri( String uri )
    {

        int offset;

        if ( ! uri.startsWith("/$") ) { return uri; }

        offset = uri.indexOf('$',2);

        if ( offset < 0 ) { return uri; }
        //     |
        //     V
        // /$-1$store-1$servlets-examples/servlet/RequestInfoExample



        offset = uri.indexOf('$', offset +1 );
        if ( offset < 0 ) { return uri; }
        //     |
        //     `-------.
        //             V
        // /$-1$store-1$servlets-examples/servlet/RequestInfoExample

        offset++;

        if ( uri.startsWith( "ROOT/", offset ))
        {
            offset += "ROOT/".length();
            //  |
            //  `----------------.
            //                   V
            // /$-1$store-1$ROOT/moo/cow/...
        }

        String unmangled_uri =  "/" + uri.substring( offset, uri.length() );

        return unmangled_uri.equals("/ROOT")  
               ? ""                           // handle the "empty path"
               : unmangled_uri;
    }



    /**
    *   Transform a cannonicalizing redirect to an AVMHost generated
    *   by Tomcat's Mapper into the equivalent cannonicalizing redirect
    *   to the appropriate reverse proxy.
    */
    String reverseProxyRedirect( String avm_location,
                                 String avm_hostport,
                                 String reverse_proxy,
                                 String uri_prefix
                               )
    {
        int host_start = avm_location.indexOf(':');
        if (host_start <  0 ) { return avm_location; }

        if ( ! avm_location.startsWith("//", host_start + 1 ) )
        {
            return avm_location;
        }
        host_start += 3;

        if ( ! avm_location.startsWith( avm_hostport, host_start ) )
        {
            return avm_location;
        }

        int host_end = host_start + avm_hostport.length();

        if ( ! avm_location.startsWith( uri_prefix, host_end ) )
        {
            return avm_location;
        }

        int prefix_end = host_end + uri_prefix.length();

        if ( avm_location.startsWith( "ROOT/", prefix_end ) )
        {
            prefix_end += "ROOT/".length();
        }

        return avm_location.substring(0,host_start) +
               reverse_proxy                        +
               "/"                                  +
               avm_location.substring(prefix_end, avm_location.length() );
    }
}
