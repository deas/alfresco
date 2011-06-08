package org.alfresco.solr.client;

import java.util.Locale;

/**
 * Represents a content property value, including locale, length, content id, encoding, mime type
 * 
 * @since 4.0
 */
public class ContentPropertyValue extends PropertyValue
{
    private Locale locale;
    private long length;
    private long id;
    private String encoding;
    private String mimetype;
    
    public ContentPropertyValue(Locale locale, long length, long id, String encoding, String mimetype)
    {
        super();
        this.locale = locale;
        this.length = length;
        this.id = id;
        this.encoding = encoding;
        this.mimetype = mimetype;
    }

    public String getEncoding()
    {
        return encoding;
    }

    public String getMimetype()
    {
        return mimetype;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public long getLength()
    {
        return length;
    }

    public long getId()
    {
        return id;
    }

    @Override
    public String toString()
    {
        return "ContentPropertyValue [locale=" + locale + ", length=" + length + ", id=" + id + ", encoding="
                + encoding + ", mimetype=" + mimetype + "]";
    }
}
