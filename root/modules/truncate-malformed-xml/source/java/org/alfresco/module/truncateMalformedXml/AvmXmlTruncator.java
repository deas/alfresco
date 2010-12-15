/*-----------------------------------------------------------------------------
*  Copyright 2005-2010 Alfresco Software Limited.
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
*  File    AvmXmlTruncator.java
*----------------------------------------------------------------------------*/

package org.alfresco.module.truncateMalformedXml;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.alfresco.service.cmr.avm.AVMNodeDescriptor;
import org.alfresco.service.cmr.remote.AVMRemote;
import org.alfresco.util.NameMatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.WebScriptServletResponse;




/**
*   Webscript driver class that allows XML files that have 
*   "traling garbage" to be repaired.
*
*   The origin of the "trailing garbage" problem is that on Linux 
*   machines that used smbclient (rather than  /sbin/mount.cifs )
*   the different code path taken by smbclient triggered a bug in 
*   the Alfresco CIFS server that made files edited via CIFS
*   not get properly truncated if the were made shorter.
*
*   In the general case, this problem is not correctable, because
*   information is lost regarding the "real" end of file.  
*   
*   However, XML files are correctable because the XML file format
*   implies a logical end of file:  the tag that closes the root
*   level node in the leading XML tree.
*
*   This utility will then re-parse corrupted files to verify the fix. 
*   When complete, a terse 1-line sumary report will be issued to the
*   webserver's logfile (sorry, this is a no-frills utility for now).
*
*   See also: alfresco-truncateMalformedXml.amp
*/
public class AvmXmlTruncator extends AbstractWebScript
{
    AVMRemote    avm_;
    SAXTruncator truncator_;

    boolean      take_snapshots_;
    int          xml_checked_;
    int          xml_wasok_;
    int          xml_fixed_;
    int          xml_still_broken_;
    int          xml_syserr_;

    int          nodes_visited_;
    int          visit_verbosity_;
    int          trunc_verbosity_;
    int          incremental_snapshot_freq_;
    NameMatcher  xml_name_matcher_;
    String       store_;

    WebScriptServletResponse res_;
    PrintWriter              out_;


    /** Snapshot every N repaired files */
    public static final int DEFAULT_INCREMENTAL_SNAPSHOT_FREQ = 5000;

    /** By default, log every N nodes visited */
    public static final int DEFAULT_VISIT_REPORT_VERBOSITY  = 1000;

    /** By default, log every N nodes truncated */
    public static final int DEFAULT_TRUNC_REPORT_VERBOSITY  = 1;

    private static Log log = LogFactory.getLog(AvmXmlTruncator.class);

    public void setAvmRemote(AVMRemote svc)  { avm_ = svc; }

    public void setXmlNameMatcher(NameMatcher matcher) { xml_name_matcher_ = matcher; }

    public void init()
    {
        truncator_ = new SAXTruncator( avm_ );
        reset();
    }

    /*-------------------------------------------------------------------------
    *------------------------------------------------------------------------*/
    void reset()
    {
        xml_checked_                = 0;
        xml_wasok_                  = 0;
        xml_fixed_                  = 0;
        xml_still_broken_           = 0;
        xml_syserr_                 = 0;
        nodes_visited_              = 0;
        store_                      = null;

        visit_verbosity_            = DEFAULT_VISIT_REPORT_VERBOSITY;
        trunc_verbosity_            = DEFAULT_TRUNC_REPORT_VERBOSITY;
        incremental_snapshot_freq_  = DEFAULT_INCREMENTAL_SNAPSHOT_FREQ;
        take_snapshots_             = true;

        truncator_.init();         // ensure distinct truncator decorator
    }


    /*-------------------------------------------------------------------------
    *------------------------------------------------------------------------*/
    public void execute(WebScriptRequest req, WebScriptResponse webscript_res)
        throws IOException
    {
        reset();

        if (!(webscript_res instanceof WebScriptServletResponse))
        {
            if ( log.isErrorEnabled() )
               log.error("TruncateMalformedXml must be "  + 
                         "executed in an HTTP Servlet environment");
            return;
        }

        res_ = (WebScriptServletResponse)webscript_res;

        // TODO: find out why WebScriptServletResponse.getWriter() returns a 
        //       Writer, and not a PrintWriter.   Is it an API bug?
        //
        out_ = (PrintWriter) res_.getWriter(); 


        String dir         = req.getParameter("path");                      // mand.
        String v_verbosity = req.getParameter("visit_verbosity");           // (1000)
        String t_verbosity = req.getParameter("trunc_verbosity");           // (1) 
        String inc_snap    = req.getParameter("incremental_snapshot_freq"); // (5000)
        String snap        = req.getParameter("take_snapshots");            // (yes)|no

        // Implementation note:
        //
        //   There's no need for templatized output yet.  If the need arises,
        //   it would be nice if this class could be reimplmented as a deriver
        //   from DeclarativeWebScript.java;  then all the logic could go into:
        //
        //             executeImpl(WebScriptRequest req, 
        //                         WebScriptStatus status, 
        //                         WebScriptCache cache)
        //
        //   Unfortunately, there's a big problem with that for long-running
        //   programs like this one:   webscripts don't provide a method for
        //   registering templates to deal with streaming output.
        //
        //   DeclarativeWebScript wants you to construct your *entire* data
        //   model first, then (and only then) is any of that model rendered.
        //
        //   What's needed is an enhanced version of DeclarativeWebScript 
        //   that can accept a collection of callback templates registered 
        //   for various stream events.
        //
        //   For these reasons, this class derives from
        //   AbstractWebScript, not DeclarativeWebScript.
        //   So much for templates, for now.
        //
        //   Therefore, there's no need for stuff like:
        //   
        //        String format = req.getFormat();
        //        if (format == null || format.length() == 0)
        //        {   
        //            format = getDescription().getDefaultFormat();
        //        }
        //
        //   I'm just hard-coding text/plain.


        res_.setContentType("text/plain");


        if ( dir == null )
        {
            emit_error_message("A valid AVM 'path' argument is required");
            return;
        }

        if ( v_verbosity != null )
        {
            try { visit_verbosity_ = Integer.parseInt( v_verbosity ); }
            catch (Exception e)
            {
                if ( log.isErrorEnabled() )
                    log.error("Ignoring bad visit report verbosity parameter: " + 
                               v_verbosity);

                visit_verbosity_ = DEFAULT_VISIT_REPORT_VERBOSITY;
            }

            if  ( visit_verbosity_ <= 0 )
            {
                if ( log.isErrorEnabled() )
                    log.error("Ignoring bad visit report verbosity parameter: " + 
                               v_verbosity);

                visit_verbosity_ = DEFAULT_VISIT_REPORT_VERBOSITY;
            }
        }

        if ( t_verbosity != null )
        {
            try { trunc_verbosity_ = Integer.parseInt( t_verbosity ); }
            catch (Exception e)
            {
                if ( log.isErrorEnabled() )
                    log.error("Ignoring bad trunc report verbosity parameter: " + 
                               t_verbosity);

                trunc_verbosity_ = DEFAULT_VISIT_REPORT_VERBOSITY;
            }

            if  ( trunc_verbosity_ <= 0 )
            {
                if ( log.isErrorEnabled() )
                    log.error("Ignoring bad trunc report verbosity parameter: " + 
                               t_verbosity);

                trunc_verbosity_ = DEFAULT_TRUNC_REPORT_VERBOSITY;
            }
        }


        if ( inc_snap != null )
        {
            try { incremental_snapshot_freq_ = Integer.parseInt( inc_snap ); }
            catch (Exception e)
            {
                if ( log.isErrorEnabled() )
                    log.error("Ignoring bad incremental_snapshot_freq parameter: " + 
                               inc_snap);

                incremental_snapshot_freq_ = DEFAULT_INCREMENTAL_SNAPSHOT_FREQ;
            }

            if  (  incremental_snapshot_freq_  <= 0 )
            {
                if ( log.isErrorEnabled() )
                    log.error("Ignoring non-positive incremental_snapshot_freq " + 
                              "parameter: " + incremental_snapshot_freq_ );

                incremental_snapshot_freq_ = DEFAULT_INCREMENTAL_SNAPSHOT_FREQ;
            }
        }


        if ( (snap != null) && (snap.toLowerCase()).equals("no") )
        {
            take_snapshots_ = false;
        }

        try
        {
            AVMNodeDescriptor dir_node = avm_.lookup(-1, dir);
            if ( ! dir_node.isDirectory() )
            {
                emit_error_message("Invalid 'path' parameter " +
                                   "(not a directory): " + dir);
                return;
            }
        }
        catch (Exception e)
        {
            emit_error_message("Invalid 'path' parameter: " + dir);
            return;
        }

        emit_message("TruncateMalformedXml attempting to fix " +
                     "malformed XML files in:  " + dir);

        int colon_index = dir.indexOf(':');

        if ( colon_index < 0 )
        {
            emit_error_message("Invalid 'path' parameter.  " + 
                               "Should be:  storeName:/... " +
                               "but your path is: " + dir);
            return;
        }
        if ( colon_index == 0 )
        {
            emit_error_message(
                "Invalid 'path' parameter.  "  + 
                "Should be:  storeName:/...  " +
                "but your storename has 0 length: " + dir);

            return;
        }

        String relpath = dir.substring(colon_index +1, dir.length());
        if ( relpath.charAt(0) != '/' ) 
        {
                emit_error_message(
                        "Invalid 'path' parameter.  " + 
                        "Should be:  storeName:/... " +
                        "but your path lacks a leading slash: " + dir);

            return;
        }

        store_ = dir.substring(0,colon_index);

        if ( take_snapshots_ )
        {
            try 
            {
                avm_.createSnapshot(store_, 
                                    "TruncateMalformedXml (pre)", 
                                    "TruncateMalformedXml about to check for malformed XML files");
            }
            catch (Exception e)
            {
                emit_error_message(
                    "Snapshot: 'TruncateMalformedXml (pre)' failed.  Aborting.");

                return;
            }
        }

        truncate_xml_files_in_directory( dir );

        emit_report("TruncateMalformedXml complete.");

        if ( take_snapshots_ )
        {
            try 
            {
                avm_.createSnapshot(store_, 
                                    "TruncateMalformedXml (post)", 
                                    "TruncateMalformedXml completed:  " +
                                    get_one_line_report_string() );
            }
            catch (Exception e)
            {
                if ( log.isErrorEnabled() )
                    log.error("Snapshot: 'TruncateMalformedXml (post)' failed.");


                // TODO - emit some HTTP error status.
            }
        }

        out_.close();
    }

    /*-------------------------------------------------------------------------
    *------------------------------------------------------------------------*/
    void truncate_xml_files_in_directory( String dir )
    {
        nodes_visited_ ++;

        if ( (nodes_visited_ % visit_verbosity_ ) == 0 )
        {
            emit_report("TruncateMalformedXml progress:");
        }

        Map<String, AVMNodeDescriptor> entries = null;

        try
        {
            // e.g.:   42, "mysite:/www/avm_webapps/ROOT/moo"
            entries = avm_.getDirectoryListing( -1 , dir);
        }
        catch (Exception e)
        {
            emit_error_message(
                "Could not list directory: " + dir + "  " +
                e.getMessage());

            return;
        }

        for ( Map.Entry<String, AVMNodeDescriptor> entry  : entries.entrySet() )
        {
            String            entry_name = entry.getKey();
            AVMNodeDescriptor avm_node   = entry.getValue();
            String            avm_path   = dir +  "/" + entry_name;

            if  ( avm_node.isDirectory() )
            {
                truncate_xml_files_in_directory( avm_path );
            }
            else
            {
                if ( xml_name_matcher_.matches( entry_name ) )
                {
                    int status = truncator_.checkXmlFile(dir, entry_name, null, 0);

                    if ( status == SAXTruncator.STATUS_OK ) { xml_wasok_ ++; }
                    else if ( status == SAXTruncator.STATUS_FIXED )   
                    { 
                        xml_fixed_  ++; 

                        if ( (xml_fixed_ + xml_still_broken_) % trunc_verbosity_ == 0 )
                        {
                            emit_message("TruncateMalformedXml fixed: " + avm_path);
                            emit_report("TruncateMalformedXml progress:");
                        }

                        // Take incremental snapshots if necessary.

                        if ( take_snapshots_ &&  
                             ((xml_fixed_ % incremental_snapshot_freq_ ) == 0 )
                           )
                        {
                            try 
                            {
                                avm_.createSnapshot(store_, 
                                                    "TruncateMalformedXml (incremental)", 
                                                    "TruncateMalformedXml incremental:  " +
                                                    get_one_line_report_string() );
                            }
                            catch (Exception e)
                            {
                                if ( log.isErrorEnabled() )
                                    log.error("Snapshot: 'TruncateMalformedXml (incremental)' failed.");
                            }
                        }
                    }
                    else if ( status == SAXTruncator.STATUS_FAILED )  
                    { 
                        xml_still_broken_ ++; 

                        if ( (xml_fixed_ + xml_still_broken_) % trunc_verbosity_ == 0 )
                        {
                            emit_message("TruncateMalformedXml could not fix: " + avm_path);
                            emit_report("TruncateMalformedXml progress:");
                        }
                    }
                    else if ( status == SAXTruncator.STATUS_SYSERR )
                    {
                        xml_syserr_ ++;
                        emit_message("TruncateMalformedXml syserr: " + avm_path);
                    }
                    xml_checked_ ++;
                }
                nodes_visited_ ++;

                if ( ((nodes_visited_ % visit_verbosity_ ) == 0) &&
                     (((xml_fixed_ + xml_still_broken_) % trunc_verbosity_) != 0)
                   )
                {
                    emit_report("TruncateMalformedXml progress:");
                }
            }
        }
    }


    /*-------------------------------------------------------------------------
    *------------------------------------------------------------------------*/
    void emit_message(String msg)
    {
        out_.println(msg);
    }

    void emit_error_message(String msg)
    {
        res_.setStatus(400);
        emit_message( "ERROR:  " + msg );
    }



    /*-------------------------------------------------------------------------
    *------------------------------------------------------------------------*/
    void emit_report(String msg)
    {
        msg = msg + "   " + get_one_line_report_string();
        out_.println(msg);
    }

    /*-------------------------------------------------------------------------
    *------------------------------------------------------------------------*/
    String get_one_line_report_string()
    {
        return  "visited: "    + nodes_visited_    +  "   " +
                "checked: "    + xml_checked_      +  "   " + 
                "ok: "         + xml_wasok_        +  "   " + 
                "fixed: "      + xml_fixed_        +  "   " + 
                "unfixable: "  + xml_still_broken_ +  "   " + 
                "syserr: "     + xml_syserr_ ;
    }
}

