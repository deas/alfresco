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
*  File    ByteArrayServletOutputStream.java
*----------------------------------------------------------------------------*/


package org.alfresco.filter;
import java.io.ByteArrayOutputStream;
import javax.servlet.ServletOutputStream;

public class ByteArrayServletOutputStream extends ServletOutputStream 
{
    protected ByteArrayOutputStream buf_  = null;
    public ByteArrayServletOutputStream() { buf_ = new ByteArrayOutputStream();}
    public byte[] toByteArray()           { return buf_.toByteArray(); }
    public void write(int b)              { buf_.write(b); }
}

