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
 */

package org.alfresco.repo.lotus.rs.impl.providers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.alfresco.repo.lotus.rs.error.QuickrError;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.protocol.error.Error;

import freemarker.core.Environment;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author EugeneZh
 */
@Produces( { "application/atom+xml", "application/atom+xml;type=feed", "application/json" })
@Consumes( { "application/atom+xml", "application/atom+xml;type=feed" })
@Provider
public class AtomErrorProvider implements MessageBodyWriter<Error>, MessageBodyReader<Error>
{
    private static final Abdera ATOM_ENGINE = new Abdera();

    private Template template = null;

    private static final String ERROR_CODE = "errorCode";
    private static final String ERROR_MESSAGE = "errorMessage";

    private static final String TEMPLATE_NAME = "faultResponse";
    private static final String TEMPLATE_FILE_NAME = "faultResponse.ftl";

    public void writeTo(Error error, Class<?> clazz, Type type, Annotation[] annotations, MediaType mt, MultivaluedMap<String, Object> headers, OutputStream os)
            throws IOException, WebApplicationException
    {
        Map<String, Object> freeMarkerMap = new HashMap<String, Object>();
        freeMarkerMap.put(ERROR_CODE, QuickrError.getErrorIdByErrorCode(error.getCode()));
        freeMarkerMap.put(ERROR_MESSAGE, error.getMessage());

        OutputStreamWriter osw = null;
        try
        {
            if (template == null)
            {
                template = new Template(TEMPLATE_NAME, new InputStreamReader(getClass().getResourceAsStream(TEMPLATE_FILE_NAME)), null, AtomConstants.TEMPLATE_ENCODING);
            }
            osw = new OutputStreamWriter(os);
            Environment env = template.createProcessingEnvironment(freeMarkerMap, osw);
            env.setOutputEncoding(AtomConstants.TEMPLATE_ENCODING);
            env.process();
        }
        catch (TemplateException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            if (osw != null)
            {
                osw.flush();
                osw.close();
            }
        }
    }

    public Error readFrom(Class<Error> clazz, Type t, Annotation[] a, MediaType mt, MultivaluedMap<String, String> headers, InputStream is) throws IOException,
            WebApplicationException
    {
        Document<Error> doc = ATOM_ENGINE.getParser().parse(is);
        return doc.getRoot();
    }

    public long getSize(Error arg0, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4)
    {
        return -1;
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] a, MediaType mt)
    {
        return Error.class.isAssignableFrom(type);
    }

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] a, MediaType mt)
    {
        return Error.class.isAssignableFrom(type);
    }
}
