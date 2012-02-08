/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
 */
package org.alfresco.service.cmr.repository;

/**
 * This exception should be thrown when a content operation could not be performed due to
 * a transient condition and where it is possible that a subsequent request to execute the
 * same action might succeed, all other things not having changed.
 * <p/>
 * An example of this would be the case where a request to create a thumbnail
 * has failed because the necessary thumbnailing software is not available e.g. because the OpenOffice.org process
 * is not currently running.
 * 
 * @author Neil Mc Erlean
 * @since 4.0.1
 */
public class ContentServiceTransientException extends ContentIOException
{
    private static final long serialVersionUID = 3258130249983276087L;
    
    public ContentServiceTransientException(String msg)
    {
        super(msg);
    }
    
    public ContentServiceTransientException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
