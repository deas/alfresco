package org.alfresco.repo.lotus.rs.impl.providers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;

@Produces( { "application/atom+xml", "application/atom+xml;type=entry", "application/json", "text/html" })
@Consumes( { "application/atom+xml", "application/atom+xml;type=entry" })
@Provider
public class AtomEntryProvider extends org.apache.cxf.jaxrs.provider.AtomEntryProvider
{
    private static final Abdera ATOM_ENGINE = new Abdera();
    

    @SuppressWarnings("unchecked")
    @Override
    public void writeTo(Entry entry, Class clazz, Type type, Annotation a[], MediaType mt, MultivaluedMap headers, OutputStream os) throws IOException
    {
        if (MediaType.TEXT_HTML.equals(mt.toString()) || MediaType.APPLICATION_JSON.equals(mt.toString()))
        {
            org.apache.abdera.writer.Writer w = ATOM_ENGINE.getWriterFactory().getWriter("json");
            entry.writeTo(w, os);
        }
        else
        {
            entry.writeTo(os);
        }
    }

}
