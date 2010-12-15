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
*  File    AVMRequestDumperValve.java
*----------------------------------------------------------------------------*/


package org.alfresco.catalina.valve;
import  org.apache.catalina.valves.*;


import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.StringManager;
import org.apache.commons.logging.Log;

import org.apache.tomcat.util.buf.CharChunk;
import org.apache.tomcat.util.buf.MessageBytes;


/**
 * This class is nearly identical to Catalina's "RequestDumperValve";<br>
 * it provides information in a slightly nicer format.
 *
 * <p>Valve that logs interesting contents from the
 * specified Request (before processing) and the corresponding Response
 * (after processing).  It is especially useful in debugging problems
 * related to headers and cookies.</p>
 *
 * <p>This Valve may be attached to any Container, depending on the granularity
 * of the logging you wish to perform.</p>
 *
 * @author Craig R. McClanahan  (further hacked by Jon Cox)
 * @version $Revision: 303133 $ $Date: 2004-08-29 12:46:15 -0400 (Sun, 29 Aug 2004) $
 */

public class AVMRequestDumperValve
    extends ValveBase {


    // ----------------------------------------------------- Instance Variables


    /**
     * The descriptive information related to this implementation.
     */
    private static final String info =
        "org.alfresco.catalina.valve.AVMRequestDumperValve/1.0";


    /**
     * The StringManager for this package.
     */
    protected static StringManager sm =
        StringManager.getManager(Constants.Package);


    // ------------------------------------------------------------- Properties


    /**
     * Return descriptive information about this Valve implementation.
     */
    public String getInfo() {

        return (info);

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Log the interesting request parameters, invoke the next Valve in the
     * sequence, and log the interesting response parameters.
     *
     * @param request The servlet request to be processed
     * @param response The servlet response to be created
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void invoke(Request request, Response response)
        throws IOException, ServletException {

        Log log = container.getLogger();
        
        // Log pre-service information
        System.out.println("REQUEST URI       =" + request.getRequestURI());
        System.out.println("          authType=" + request.getAuthType());
        System.out.println(" characterEncoding=" + request.getCharacterEncoding());
        System.out.println("     contentLength=" + request.getContentLength());
        System.out.println("       contentType=" + request.getContentType());
        System.out.println("       contextPath=" + request.getContextPath());


        MessageBytes urlMB_req_path         = request.getRequestPathMB();
        MessageBytes urlMB_req_path_decoded = request.getDecodedRequestURIMB();

        if ( request.getContextPath() != null )
        {
            urlMB_req_path.toChars();
            urlMB_req_path_decoded.toChars();
            System.out.println("       RequestPath=" +  urlMB_req_path.getCharChunk().toString() );
            System.out.println("    decoded ReqPath=" +  urlMB_req_path_decoded.getCharChunk().toString() );
        }
        else
        {
            // This can only happen when a re-write has created a lookup for a reqeust
            // path that does not exist, and that can't failover to the ROOT context
            // (e.g.:  the  AVMHostConfig didn't bother to add a StandardContext 
            // with a name of "").

            System.out.println(
                "       WARNING: Context is NULL so can't fetch RequestPath or decoded ReqPath");
        }

        Cookie cookies[] = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++)
                System.out.println("            cookie=" + cookies[i].getName() + "=" +
                    cookies[i].getValue());
        }
        Enumeration hnames = request.getHeaderNames();
        while (hnames.hasMoreElements()) {
            String hname = (String) hnames.nextElement();
            Enumeration hvalues = request.getHeaders(hname);
            while (hvalues.hasMoreElements()) {
                String hvalue = (String) hvalues.nextElement();
                System.out.println("            header=" + hname + "=" + hvalue);
            }
        }
        System.out.println("            locale=" + request.getLocale());
        System.out.println("            method=" + request.getMethod());
        Enumeration pnames = request.getParameterNames();
        while (pnames.hasMoreElements()) {
            String pname = (String) pnames.nextElement();
            String pvalues[] = request.getParameterValues(pname);
            StringBuffer result = new StringBuffer(pname);
            result.append('=');
            for (int i = 0; i < pvalues.length; i++) {
                if (i > 0)
                    result.append(", ");
                result.append(pvalues[i]);
            }
            System.out.println("         parameter=" + result.toString());
        }
        System.out.println("          pathInfo=" + request.getPathInfo());
        System.out.println("          protocol=" + request.getProtocol());
        System.out.println("       queryString=" + request.getQueryString());
        System.out.println("        remoteAddr=" + request.getRemoteAddr());
        System.out.println("        remoteHost=" + request.getRemoteHost());
        System.out.println("        remoteUser=" + request.getRemoteUser());
        System.out.println("requestedSessionId=" + request.getRequestedSessionId());
        System.out.println("            scheme=" + request.getScheme());
        System.out.println("        serverName=" + request.getServerName());
        System.out.println("        serverPort=" + request.getServerPort());
        System.out.println("       servletPath=" + request.getServletPath());
        System.out.println("          isSecure=" + request.isSecure());
        System.out.println("---------------------------------------------------------------");

        // Perform the request
        getNext().invoke(request, response);

        // Log post-service information
        System.out.println("---------------------------------------------------------------");
        System.out.println("Post service info:");
        System.out.println("          authType=" + request.getAuthType());
        System.out.println("     contentLength=" + response.getContentLength());
        System.out.println("       contentType=" + response.getContentType());
        Cookie rcookies[] = response.getCookies();
        for (int i = 0; i < rcookies.length; i++) {
            System.out.println("            cookie=" + rcookies[i].getName() + "=" +
                rcookies[i].getValue() + "; domain=" +
                rcookies[i].getDomain() + "; path=" + rcookies[i].getPath());
        }
        String rhnames[] = response.getHeaderNames();
        for (int i = 0; i < rhnames.length; i++) {
            String rhvalues[] = response.getHeaderValues(rhnames[i]);
            for (int j = 0; j < rhvalues.length; j++)
                System.out.println("            header=" + rhnames[i] + "=" + rhvalues[j]);
        }
        System.out.println("           message=" + response.getMessage());
        System.out.println("        remoteUser=" + request.getRemoteUser());
        System.out.println("            status=" + response.getStatus());
        System.out.println("===============================================================");

    }


    /**
     * Return a String rendering of this object.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("RequestDumperValve[");
        if (container != null)
            sb.append(container.getName());
        sb.append("]");
        return (sb.toString());

    }


}
