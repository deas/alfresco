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
*  File    SAXTruncator.java
*  Use     Truncates XML files that aren't well formed just prior
*          to first character that make them non well-formed.
*          
*----------------------------------------------------------------------------*/

package org.alfresco.module.truncateMalformedXml;


import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.OutputStream;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map; 
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.alfresco.service.cmr.avm.AVMNodeDescriptor;
import org.alfresco.service.cmr.avm.AVMNotFoundException;
import org.alfresco.service.cmr.remote.AVMRemote;
import org.alfresco.service.namespace.QName;

/** 
*   Truncates XML files that aren't well formed just prior
*   to first character that make them non well-formed.
*
*   The motivating appliction is fixing XML files that have been
*   corrupted by a bad CIFS client. 
*/
public class SAXTruncator extends DefaultHandler
{
    private static Log log = LogFactory.getLog(SAXTruncator.class);

    static public final byte    LF            = (byte) 10;
    static public final int     BUFSIZE       = 16384;
    static public final String  DECORATOR     = ".__trunc__";
    static int                  Decorator_id_ = -1;
    String                      decorator_    = DECORATOR;

    public byte []              BUF  = new byte[BUFSIZE];

    public static final int     STATUS_OK     = 0;
    public static final int     STATUS_FIXED  = 1;
    public static final int     STATUS_FAILED = 2;
    public static final int     STATUS_SYSERR = 3;

    String    abspath_;
    AVMRemote avm_;


    protected static synchronized int GetDecoratorId() 
    {
        Decorator_id_ ++;
        return Decorator_id_;
    }

    public void init()
    {
        try 
        { 
            decorator_ = DECORATOR + Integer.toString( GetDecoratorId() );
        }
        catch (Exception e) { /* do nothing */ }
    }



    public void setAvmRemote(AVMRemote svc)  { avm_ = svc; }

    public SAXTruncator()              { }
    public SAXTruncator(AVMRemote avm) { setAvmRemote( avm ); }


    /** Capture parser location within document */
    protected Locator locator_;

    /** Receive a Locator object for document events. */
    public void    setDocumentLocator( Locator locator ) { locator_ = locator; }


    void log_parse_error(String msg)
    {
        // This is only treated as an "info" message, because the driver 
        // class (AvmXmlTruncato) takes care of most of the logging already.

        if ( log.isInfoEnabled() )
            log.info("Malformed:  " + abspath_ + "  " +  msg +
                            "  (line: " + locator_.getLineNumber() + 
                            "  col:  " + locator_.getColumnNumber() + ")");
    }

    /** Receive notification of a warning */
    public void warning( SAXParseException e)  throws SAXException 
    {
        log_parse_error( e.getMessage() );
    }

    /** Receive notification of a recoverable error. */
    public void error( SAXParseException e ) throws SAXException
    { 
        log_parse_error( e.getMessage() );
    }

    /** Receive notification of a non-recoverable error. */
    public void fatalError  ( SAXParseException e )  throws SAXException
    {
        log_parse_error( e.getMessage() );
    }

    public static SAXParser getNonValidatingSAXParser()
    {
        SAXParser result = null;
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance() ;
            factory.setValidating( false );
            factory.setFeature( "http://xml.org/sax/features/namespaces", false);
            result = factory.newSAXParser();
        }
        catch  ( Exception e ) { /* TODO - croak here */ }
        return result;
    }

    public int checkXmlFile(String path, String name, String orig_name, int attempt)
    {
        int status  = STATUS_OK;

        InputStream istream = null;
        InputSource isource = null;
        boolean     trunc   = false;
        abspath_ =  path + "/" + name;

        try 
        {
            istream =  avm_.getFileInputStream(-1, abspath_ );

            isource = new InputSource( istream );
            SAXParser parser = getNonValidatingSAXParser();

            if ( log.isDebugEnabled() )
                log.debug("about to parse:  " + abspath_);

            parser.parse( isource, this );
        }
        catch ( SAXParseException e ) 
        { 
            if ( log.isDebugEnabled() )
                log.debug("malformed:  " + abspath_);

            trunc  = true; 
        }
        catch ( Exception e )         { status = STATUS_FAILED; }

        if (istream != null ) 
        {
            try                 { istream.close(); } 
            catch (Exception e) { status = STATUS_SYSERR ; }
        }

        if ( trunc ) 
        {
            if (attempt != 0 ) { status = STATUS_FAILED; }
            else
            {
                if ( log.isDebugEnabled() )
                    log.debug("trying to truncate:  " + abspath_);


                boolean trunc_failure = truncateXmlFile( path, 
                                                         name,
                                                         locator_.getLineNumber()   - 1 ,
                                                         locator_.getColumnNumber() - 1
                                                       );

                if  ( !trunc_failure && (attempt == 0 ) )
                {
                    if ( log.isDebugEnabled() )
                        log.debug("trying to revalidate:  " + abspath_);

                    status = checkXmlFile(path, name + decorator_, name, attempt +1 );

                    if ( log.isDebugEnabled() )
                        log.debug("revalidate status:  " + status );
                }
            }
        }

        if ( attempt != 0)
        {
            if ( ! trunc )
            {
                // remove original file, and replace it with the tmpfile
                try 
                { 
                    try                  { avm_.removeNode( path, orig_name ); }
                    catch (Exception e)  { /* do nothing */ }

                    avm_.rename(path, name, path, orig_name ); 
                    status = STATUS_FIXED;

                    if ( log.isDebugEnabled() )
                        log.debug("fixed:  " + path + "/" + name );

                }
                catch (Exception e)
                {
                    status = STATUS_SYSERR;

                    if ( log.isDebugEnabled() )
                        log.debug("syserror:  " + path + "/" + name + "    " + e.getMessage());
                }
            }
            else
            {
                try 
                {
                    if ( log.isDebugEnabled() )
                        log.debug("could not fix (so cleaning up):  " + path + "/" + name );

                    avm_.removeNode( path, name );
                }
                catch (Exception e)
                {
                    status = STATUS_SYSERR;
                }
            }
        }

        return status;
    }

    public boolean truncateXmlFile( String path, String name, int line, int col)
    {
        InputStream  istream       = null;
        OutputStream ostream       = null;
        boolean      trunc_failure = false;

        try
        { 
            istream = avm_.getFileInputStream( -1, abspath_ ); 

            if ( log.isDebugEnabled() )
                log.debug("opened for read: " + abspath_ );

        }
        catch ( Exception e ) 
        { 
            if ( log.isDebugEnabled() )
                log.debug("failed avm_.getFileInputStream: " + abspath_ );

            return true; 
        }

        try 
        { 
            ostream = avm_.createFile( path, name + decorator_ ); 
        }
        catch ( Exception e ) 
        { 
            try { istream.close(); } catch (Exception ex)  { /* do nothing */ }

            if ( log.isDebugEnabled() )
                log.debug("could not create file: " + path + "/" + name + decorator_ );

            return true; 
        }

        int rv;
        int lcount   = 0;
        int lindex   = 0;
        int i        = 0;

        try
        {
            while ( (rv = istream.read(BUF , 0, BUFSIZE )) >= 0 )
            {
                i=0;
                while ( (i< rv) && (lcount < line))  
                { 
                    if ( BUF[ i ] == LF ) { lcount ++; lindex = i;}
                    i++ ;
                }

                if ( lcount != line ) 
                { 
                    ostream.write(BUF, 0, rv); 
                }        
                else
                {
                    int must_emit = lindex + col + 1;

                    while ( must_emit > 0 )
                    {
                        int can_emit = (rv < must_emit) ? rv : must_emit; 

                        ostream.write(BUF, 0, can_emit );

                        must_emit -= can_emit;

                        int can_read = (BUFSIZE< must_emit)? BUFSIZE: must_emit;

                        rv = istream.read(BUF , 0, can_read);

                        if ( rv < 0)
                        {
                            trunc_failure = true;

                            if ( log.isDebugEnabled() )
                                log.debug("unexpected end of file: " + abspath_ );

                            break;
                        }
                    }
                    break;
                }
            }
        }
        catch (Exception e)  
        { 
            if ( log.isDebugEnabled() )
                log.debug("truncateXmlFile " + e.getMessage());

            trunc_failure = true;      // java.io.IOException
        }

        try
        { 
            istream.close(); 
        } 
        catch (Exception e) 
        { 
            if ( log.isDebugEnabled() )
                log.debug("truncateXmlFile close error: " + abspath_);

            trunc_failure = true; 
        }

        try 
        { 
            ostream.close(); 
        } 
        catch (Exception e) 
        { 
            if ( log.isDebugEnabled() )
                log.debug("truncateXmlFile close error: " + path + "/" + name + decorator_ ); 

            trunc_failure = true; 
        }

        return trunc_failure;
    }
}
