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
*  File    AvmCompare.java
*----------------------------------------------------------------------------*/

package org.alfresco.module.avmCompare;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.SortedMap;

import org.alfresco.repo.avm.AVMNodeType;
import org.alfresco.service.cmr.avm.AVMNodeDescriptor;
import org.alfresco.service.cmr.avmsync.AVMDifference;
import org.alfresco.service.cmr.avmsync.AVMSyncService;
import org.alfresco.service.cmr.remote.AVMRemote;
import org.alfresco.util.NameMatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.WebScriptServletResponse;



/**
*   Webscript driver class that allows two directories to be compared.
*   See also: alfresco-avmCompare.amp
*/
public class AvmCompare extends AbstractWebScript
{
    static boolean IsInit_ = false;

    AVMRemote      avm_;
    AVMSyncService sync_;
    NameMatcher    excluder_;

    WebScriptServletResponse res_;
    PrintWriter              out_;

    private static Log log = LogFactory.getLog(AvmCompare.class);

    public void setAVMSyncService( AVMSyncService sync )  { sync_ = sync;}
    public AVMSyncService getAVMSyncService()             { return sync_;}


    public void setAvmRemote(AVMRemote svc)  { avm_ = svc; }

    public void setExcluderNameMatcher(NameMatcher matcher) 
    { excluder_ = matcher; }

    /**
    *  Called once at startup per instance; however, because there 
    *  can be both a GET and POST bean, this init() method might 
    *  be called more than once.  Hence, one portion of this init()
    *  is devoted to "once per process" initialization, and another
    *  is "once per instance".
    */
    public void init()
    {    
        if ( ! IsInit_  )
        {
            IsInit_  = true;

            // Once per process init
            // ---------------------
        

            if ( log.isInfoEnabled() )
                   log.info("avmCompare webscript initialized");

        }


        // Once per instance init
        // ----------------------

        reset(); 
    }

    /*-------------------------------------------------------------------------
    *------------------------------------------------------------------------*/
    void reset()                       // Called on every request
    {
    }

    /*-------------------------------------------------------------------------
    *------------------------------------------------------------------------*/
    public void execute(WebScriptRequest req, WebScriptResponse webscript_res)
        throws IOException
    {
        reset();

        if ( log.isDebugEnabled() )
               log.debug("execute()");

        if (!(webscript_res instanceof WebScriptServletResponse))
        {
            if ( log.isErrorEnabled() )
               log.error("AvmCompare must be "  + 
                         "executed in an HTTP Servlet environment");
            return;
        }

        res_ = (WebScriptServletResponse)webscript_res;
        out_ = (PrintWriter) res_.getWriter(); 

        String changeset_path        = req.getParameter("changeset_path");
        String changeset_version_str = req.getParameter("changeset_version");
        int    changeset_version     = -1;

        String baseline_path         = req.getParameter("baseline_path");
        String baseline_version_str  = req.getParameter("baseline_version");
        int    baseline_version      = -2;

        String  header_str           = req.getParameter("show_header");
        boolean show_header          = true;

        String  prune_str            = req.getParameter("prune");
        boolean prune                = true;


        if ( (header_str != null) && (header_str.toLowerCase()).equals("no") )
        {   
            show_header = false;
        }

        if ( (prune_str != null) && (prune_str.toLowerCase()).equals("no") )
        {   
            prune = false;
        }

        res_.setContentType("text/plain");

        if ( changeset_path == null  )
        {
            if ( baseline_path != null ) 
            { 
                changeset_path = baseline_path; 
            }
            else
            {
                emit_error_message( "A valid AVM 'changeset_path' " + 
                                    "or 'baseline_path' argument is required");
                return;
            }
        }

        if ( changeset_version_str != null )
        {
            try 
            { 
                changeset_version = Integer.parseInt( changeset_version_str ); 
            }
            catch (Exception e)
            {
                emit_message("WARN: Ignoring bad changeset_version parameter: " + 
                                    changeset_version_str +  "  (defaulting to: -1)");
            }
        }

        String changeset_store  = get_store( changeset_path ); 
        if (changeset_store == null)
        {
            emit_error_message("Invalid changeset_path: " + changeset_path);
            return;
        }

        int changeset_latest = 0;
        try  
        { 
            changeset_latest = avm_.getLatestSnapshotID( changeset_store ); 
        }
        catch (Exception e) 
        { 
            if ( log.isDebugEnabled() )
               log.debug("Could not getLatestSnapshotID() for changeset: " +
                          changeset_store);

            /* do nothing */ 
        }


        changeset_version = get_version_alias( changeset_version, 
                                               changeset_latest );


        if (changeset_version > changeset_latest )
        {
            emit_error_message(
                "changeset_version > latest snapshot for store '" +
                changeset_store                                   + 
                "':  "                                            +
                Integer.toString( changeset_version )             + 
                " > "                                             +  
                Integer.toString( changeset_latest));

            return;
        }

        if ( baseline_path == null )
        {
            baseline_path = changeset_path;     // changeset_path != null
        }

        int    baseline_latest = 0;
        String baseline_store  = get_store( baseline_path ); 

        if (baseline_store == null)
        {
            emit_error_message("Invalid baseline_path: " + baseline_path);
            return;
        }

        if (baseline_store.toLowerCase().equals( changeset_store.toLowerCase()))
        {
            baseline_latest = changeset_latest;
        }
        else
        {
            try
            { 
                baseline_latest = avm_.getLatestSnapshotID( baseline_store ); 
            }
            catch (Exception e) 
            { 
                if ( log.isDebugEnabled() )
                   log.debug("Could not getLatestSnapshotID() for baseline: " +
                              baseline_store);
    
                /* do nothing */ 
            }
        }

        if ( baseline_version_str != null )
        {
            try 
            { 
                baseline_version = Integer.parseInt( baseline_version_str ); 
            }
            catch (Exception e)
            {
                emit_message("WARN: Ignoring bad baseline_version parameter: " + 
                                    baseline_version_str                       +
                                    "  (defaulting to: "                       +
                                    Integer.toString( baseline_latest )        +
                                    ")");

                baseline_version = baseline_latest;
            }
        }

        baseline_version = get_version_alias( baseline_version, 
                                              baseline_latest );

        if (baseline_version > baseline_latest )
        {
            emit_error_message(
               "baseline_version > latest snapshot for store '" +
                baseline_store                                  + 
                "':  "                                          +
                Integer.toString( baseline_version )            + 
                " > "                                           +  
                Integer.toString( baseline_latest));

            return;
        }

        if (show_header)
        {
            emit_message(
               "AvmCompare\n"                                                          +
               "----------\n"                                                          +
               "\n"                                                                    +
               "           baseline_version:   " + Integer.toString(baseline_version)  + 
               "\n"                                                                    +
               "           baseline_path:      " + baseline_path + "\n"                +
               "\n"                                                                    +
               "           changeset_version:  " + Integer.toString(changeset_version) + 
               "\n"                                                                    +
               "           changeset_path:     " + changeset_path                      +
               "\n"                                                                    +
               "\n"                                                                    +
               "\n"                                                                    +
               "Legend\n"                                                              +
               "------\n"                                                              +
               "\n"                                                                    +
               "           [---]     no such file or directory\n"                      +
               "           [--f]     plain   file\n"                                   +
               "           [--d]     plain   directory\n"                              +
               "           [-lf]     layered file\n"                                   +
               "           [-ld]     layered directory\n"                              +
               "           [x-f]     deleted plain file\n"                             +
               "           [x-d]     deleted plain directory\n"                        +
               "           [xlf]     deleted layered file\n"                           +
               "           [xld]     deleted layered directory\n"                      +
               "           c-status  status of changeset relative to baseline\n"       +
               "           b-meta    baseline  '[...]' metadata (e.g.: [--f])\n"       +
               "           c-meta    changeset '[...]' metadata (e.g.: [--f])\n"       +
               "\n"                                                                    +
               "\n"                                                                    +
               "\n"                                                                    +
               "c-status   b-meta  c-meta  c-path\n"                                   +
               "---------  ------  ------  ------");
        }

        try 
        {
            List<AVMDifference> diff_list = 
                    sync_.compare(changeset_version, changeset_path,
                                  baseline_version, baseline_path,
                                  excluder_);

            for (AVMDifference diff : diff_list )
            {
                // Look up source node, even if it's been deleted
                AVMNodeDescriptor changeset_desc =  null;
                try 
                {
                    changeset_desc = avm_.lookup( diff.getSourceVersion(), 
                                                  diff.getSourcePath(),
                                                  true);
 
                }
                catch (Exception e) { /* do nothing */ }

                String changeset_meta = get_meta_string( changeset_desc );

                // Look up baseline node, even if it's been deleted
                AVMNodeDescriptor baseline_desc =  null;
                try 
                {
                    baseline_desc = avm_.lookup( diff.getDestinationVersion(), 
                                                 diff.getDestinationPath(),
                                                 true);

                }
                catch (Exception e) { /* do nothing */ }
                String baseline_meta = get_meta_string( baseline_desc );

                int diff_code = diff.getDifferenceCode();

                String diff_cmp;
                if (     diff_code ==  AVMDifference.NEWER)    { diff_cmp = "NEWER   "; }
                else if (diff_code ==  AVMDifference.OLDER)    { diff_cmp = "OLDER   "; }
                else if (diff_code ==  AVMDifference.CONFLICT) { diff_cmp = "CONFLICT"; }
                else                                           { diff_cmp = "?       "; }

                emit_message(  diff_cmp       +  "   "  +
                               baseline_meta  +  "   "  +
                               changeset_meta +  "   " +
                               diff.getSourcePath() 
                            );

                if ( ! prune ) 
                {
                    int type =  changeset_desc.isDeleted()
                                ?  changeset_desc.getDeletedType()
                                :  changeset_desc.getType();

                     if ( (type == AVMNodeType.PLAIN_DIRECTORY )   ||
                          (type == AVMNodeType.LAYERED_DIRECTORY )
                        )
                     {
                         expand_tree( diff_cmp, 
                                      changeset_desc.isDeleted(),
                                      diff.getSourceVersion(),
                                      diff.getSourcePath() );
                     }
                }
            }
        }
        catch (Exception e)
        {
            emit_error_message(e.getClass().getName() + "  " + e.getMessage());

        }
        finally
        {
            out_.close();
        }
    }

    void expand_tree(String diff_cmp, boolean is_deleted, int version, String path)
    {
        // This dir has been emitted, so get its children and emit them,
        // creating AVMDifference-like labels along the way. 

        // TODO - punt for now on showing deleted trees, but fix it later

        if (is_deleted) { return ; } 

        try 
        {
            SortedMap<String, AVMNodeDescriptor> child_desc_map = 
                avm_.getDirectoryListing( version, path );

            for ( AVMNodeDescriptor desc :  child_desc_map.values() )
            {
                if (desc.isFile() )
                {
                    emit_non_deleted_file( diff_cmp, desc); 
                }
                else
                {
                    emit_non_deleted_dir( diff_cmp, desc); 
                }
            }
        }
        catch (Exception e) 
        { 
            // TODO - make sure this is the right thing to do.
            return;
        }
    }

    void emit_non_deleted_dir(  String            diff_cmp, 
                                AVMNodeDescriptor desc)
    {
        String layer_meta     = desc.isLayeredDirectory()
                                ? "l"
                                : "-";

        String baseline_meta  = "[---]";
        String changeset_meta = "[-" + layer_meta + "d]";

        emit_message(  diff_cmp       +  "   "  +
                       baseline_meta  +  "   "  +
                       changeset_meta +  "   "  +
                       desc.getPath()
                    );

        try 
        {
            SortedMap<String, AVMNodeDescriptor> child_desc_map = 
                avm_.getDirectoryListing( desc );

            for ( AVMNodeDescriptor child_desc :  child_desc_map.values() )
            {
                if (child_desc.isFile() )
                {
                    emit_non_deleted_file( diff_cmp, child_desc); 
                }
                else
                {
                    emit_non_deleted_dir( diff_cmp, child_desc); 
                }
            }
        }
        catch (Exception e)
        {
            // TODO - make sure this is the right thing to do.
            return;
        }
    }


    void emit_non_deleted_file( String diff_cmp, AVMNodeDescriptor desc)
    {
        String layer_meta     = desc.isLayeredFile()
                                ? "l"
                                : "-";

        String baseline_meta  = "[---]";
        String changeset_meta = "[-" + layer_meta + "f]";

        emit_message(  diff_cmp       +  "   "  +
                       baseline_meta  +  "   "  +
                       changeset_meta +  "   "  +
                       desc.getPath()
                    );
    }




    String get_meta_string( AVMNodeDescriptor desc )
    {
        if (desc == null) { return "[---]"; }

        String deleted_meta;
        int    type;

        if ( ! desc.isDeleted())
        {
            deleted_meta = "-";
            type         = desc.getType();
        }
        else
        {
            deleted_meta = "x";
            type         = desc.getDeletedType();
        }

        String node_meta;
        if      ( type == AVMNodeType.PLAIN_FILE )        { node_meta = "-f"; }
        else if ( type == AVMNodeType.PLAIN_DIRECTORY )   { node_meta = "-d"; }
        else if ( type == AVMNodeType.LAYERED_DIRECTORY ) { node_meta = "ld"; }
        else if ( type == AVMNodeType.LAYERED_FILE )      { node_meta = "lf"; }
        else                                              { node_meta = "??"; }

        return  "[" + deleted_meta + node_meta + "]";
    }
    
    String get_store( String path )
    {
        int colon_index = path.indexOf(':');
        if ( colon_index < 0 ) { return null; }
        
        String relpath = path.substring(colon_index +1, path.length());
        return path.substring(0,colon_index);
    }

    int get_version_alias( int value, int latest )
    {
        if ( value >= -1 ) { return value ; }
        value = -value;
        if ( value > (latest + 2) ) { value = latest + 2; }
        return latest + 2 - value ; 
    }

    void emit_error_message(String msg)
    {
        msg = "ERROR:  " + msg;

        if ( log.isDebugEnabled() )
               log.debug("Sending error to client: " + msg);

        res_.setStatus(400);
        emit_message( msg );
    }

    void emit_message(String msg)
    {
        out_.println(msg);
    }
}

