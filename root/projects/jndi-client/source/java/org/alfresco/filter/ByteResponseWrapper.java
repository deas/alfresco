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
*  File    ByteResponseWrapper.java
*----------------------------------------------------------------------------*/

package org.alfresco.filter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
*  Wrapper class for modifying output via a filter.
*/
@SuppressWarnings("deprecation")
// 
// Get rid of annoying warnings about deprecation of base
// class methods setStatus, encodeRedirect, encodeURL
//
public class ByteResponseWrapper extends HttpServletResponseWrapper 
{
    protected ServletOutputStream captured_output_;
    protected ServletOutputStream servlet_output_;
    protected PrintWriter         print_writer_;

    /*-------------------------------------------------------------------------
    *  ByteResponseWrapper --
    *------------------------------------------------------------------------*/
    public ByteResponseWrapper(HttpServletResponse response, 
                               ServletOutputStream captured_output)
    {
        super(response);
        captured_output_ = captured_output;
    }

    /*-------------------------------------------------------------------------
    *  getWriter --
    *------------------------------------------------------------------------*/
    public PrintWriter getWriter() throws java.io.IOException 
    {
        if (servlet_output_ == null) 
        {
            if (print_writer_ == null) 
            {
                setCharacterEncoding(getCharacterEncoding());     // voodoo?

                print_writer_ = 
                    new PrintWriter( 
                        new OutputStreamWriter(captured_output_, 
                                               getCharacterEncoding()));
            }
            return print_writer_;
        }
        throw new IllegalStateException();
    }

    /*-------------------------------------------------------------------------
    *  getOutputStream --
    *------------------------------------------------------------------------*/
    public ServletOutputStream getOutputStream() throws java.io.IOException 
    {
        if (print_writer_ == null) 
        {
            if (servlet_output_ == null) {servlet_output_ = captured_output_;}
            return servlet_output_;
        }
        throw new IllegalStateException();
    }

    /*-------------------------------------------------------------------------
    *  flushOutputStreamOrWriter --
    *------------------------------------------------------------------------*/
    public void flushOutputStreamOrWriter() throws IOException 
    {
        if (servlet_output_ != null) { servlet_output_.flush(); }
        if (print_writer_ != null)   { print_writer_.flush();   }
    }
}
