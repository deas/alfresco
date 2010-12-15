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
*  File    CacheControlFilter.java
*----------------------------------------------------------------------------*/

package org.alfresco.filter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.alfresco.config.JNDIConstants;

/**
*   Sets "Cache-Control" http header values in response 
*   depending upon which virtual server is being accessed.
*   
*/
public class CacheControlFilter implements Filter 
{
    static protected CacheControlFilterInfoBean FilterInfo_;

    protected FilterConfig      config   = null;
    protected static String  [] CacheControlHeader_;
    protected static Pattern [] HostPattern_;

    public static final String LOOKUP_DEPENDENCY_HEADER = 
                               "X-Alfresco-Lookup";


    // To track the dependencies of URLs on files, the files accessed are 
    // sometimes tracked by making AVMFileDirContext.lastModified call back
    // and report file access is taking place.  There's no easy means of 
    // communication between file access and filters so a thread local 
    // (LookupDependency_) is used, and gets set/unset by AVMUrlValve, 
    // depending on whether or not a special header is present.

    private static ThreadLocal< HashMap<String,String> >  LookupDependency_ = 
                                new ThreadLocal< HashMap<String,String> >();

    public static void StartLookupDependency()
    {
        if ( LookupDependency_.get() == null )
        { 
            LookupDependency_.set( new HashMap<String,String>() ); 
        }
    }

    public static void StopLookupDependency()
    {
        LookupDependency_.set( null );
    }

    public static void AddLookupDependency(String file)
    {
        HashMap<String,String> lookup_dependency = (HashMap<String,String>) 
                                                   LookupDependency_.get();

        // Not sending back lookup dependencies
        if ( lookup_dependency == null ) 
        { 
            return; 
        }
        lookup_dependency.put( file , null );
    }
 

    public void init(FilterConfig config) throws ServletException 
    {
    	this.config = config;

        // Because this filter is in $VIRTUAL_TOMCAT_HOME/conf/web.xml,
        // it's pushed into every webapp.  While a different Filter
        // instances is created for each virtual webapp, every filter
        // shares the same immutable configuration.   Thus, perform
        // a 1-time init for the class if this hasn't happened yet.

        if ( HostPattern_ == null ) { Init(); } 
    }


    public static void InitInfo(CacheControlFilterInfoBean  info)
    {
        FilterInfo_ = info;
    }


    protected static void Init()
    {
        // A normal filter would fetch config from the  
        // $VIRTUAL_TOMCAT_HOME/conf/web.xml file like this:
        //
        //        String value = null;
        //        try 
        //        { 
        //            value    = config.getInitParameter("max-age"); 
        //            max_age_ = Long.valueOf(value).longValue();
        //        } 
        //        catch (NumberFormatException e) 
        //        {
        //            max_age_= -1;
        //            config.getServletContext().log(
        //                "Invalid format for max-age initParam; expected integer (seconds)");
        //        }
        //        catch (Throwable t) { max_age_= -1; }
        //
        // 
        // However, this filter applies the same rules to all virtual webapps
        // and draws its rule set from a Spring application context 
        // (AVMHost reads the Spring config, then sets FilterInfo).

        Set<Map.Entry<String,String>> cacheControlRulesEntrySet =  
            FilterInfo_.getCacheControlRulesEntrySet();

        int len = cacheControlRulesEntrySet.size();
        CacheControlHeader_ = new String[ len ];
        HostPattern_        = new Pattern [ len ];

        int i=0;
        for ( Map.Entry<String,String> e : cacheControlRulesEntrySet )
        {
            String key = e.getKey();
            try 
            {
                CacheControlHeader_[i] = e.getValue();
                HostPattern_[i]        = Pattern.compile( 
                                             key, Pattern.CASE_INSENSITIVE );
            }
            catch (java.util.regex.PatternSyntaxException pe)
            {
                // TODO:  create logging for webapp that won't get in the way of
                //        class unloading. Log a message along the lines of:
                //        "Bad cacheControlRule regex: " + key)
                //            
                //        For now, just propagate the exception.
                //              
                throw pe;
            }
            i++;
        }
    }

    public void doFilter( ServletRequest  request, 
                          ServletResponse response,
                          FilterChain     chain
                        ) throws IOException, ServletException 
    {
         ByteArrayServletOutputStream byte_stream =
                new ByteArrayServletOutputStream();

        HttpServletRequest  req     = (HttpServletRequest)  request;
        HttpServletResponse res     = (HttpServletResponse) response;
        ByteResponseWrapper wrapper = new ByteResponseWrapper(res,byte_stream);

        chain.doFilter(request, wrapper); 

        wrapper.flushOutputStreamOrWriter();

        byte[] bytes = byte_stream.toByteArray();

        String serverName  = request.getServerName();

        for (int i=0; i< HostPattern_.length; i++)
        {
            // Use find(), not match().
            // We don't want implicit '^' and '$' anchors in regex.
           
            if ( HostPattern_[i].matcher( serverName  ).find() )
            {
                wrapper.setHeader("Cache-Control", CacheControlHeader_[i] );
                break;
            }
        }

        HashMap<String,String> lookup_dependency = (HashMap<String,String>)
                                                    LookupDependency_.get();
        if ( lookup_dependency != null )
        {
            StringBuilder hdr = new StringBuilder();
            String delim = "";

            for (String file : lookup_dependency.keySet() )
            {
                // Make sure this isn't a file in META-INF or WEB-INF
                //
                // When the webapp first starts up, there's an initial 
                // access of web.xml and pr.tld, for example:
                // 
                //    mysite:/www/avm_webapps/ROOT/WEB-INF/web.xml
                //    mysite:/www/avm_webapps/ROOT/WEB-INF/pr.tld
                //
                // In general, you can't predict exactly what is or will be
                // dependent upon the contents of the WEB-INF & META-INF
                // directories, so it's best to handle that issue seperately.
                // Hence, such files will be omitted from the dep list.
                //
                if (  JNDIConstants.DEFAULT_INF_PATTERN.matcher( file ).find() ) 
                { 
                    continue; 
                } 

                try 
                { 
                    file = java.net.URLEncoder.encode( file , "UTF-8");
                    hdr.append( delim + file );

                    delim = ", ";
                }
                catch (Exception e) 
                {
                    /* UTF-8 is always OK */
                }
            }

            String header =  hdr.toString();

            if ( (header != null) && header.length() != 0)
            {
                wrapper.setHeader(LOOKUP_DEPENDENCY_HEADER, header );
            }
        }


        // Write the output
        //
        //      Figuring out how to do a write in order to handle all 
        //      cases properly required a lot of digging.  The example:
        //      http://java.sun.com/products/servlet/Filters.html
        //      is incorrect.  You really need to deal with both 
        //      the OutputStream and PrintWriter case, not just the
        //      PrintWriter (if you do that, images will be messed up),  
        //      probably due to confusion over chunked transfer sizes.

        try 
        { 
            OutputStream out = res.getOutputStream(); 
            out.write(bytes); 
        } 
        catch (Throwable t) 
        { 
            PrintWriter out = res.getWriter(); 
            out.write(new String(bytes)); 
        }
    }

    public void destroy() { }
}
