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
package org.alfresco.share.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

/**
 * Helper class to manage HttpClient based actions.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class HttpUtil
{
    private static final String UTF_8_ENCODING = "UTF-8";

    private static Log logger = LogFactory.getLog(HttpUtil.class);

    private static final String MIME_TYPE_JSON = "application/json";

    private HttpUtil()
    {
    }

    /**
     * Reads input stream.
     * 
     * @param stream {@link InputStream}
     * @return String message if input
     */
    public static String readStream(final InputStream stream)
    {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null)
            {
                builder.append(line).append("\n");
            }
        }
        catch (IOException ex)
        {
            logger.error("reading input error", ex);
            throw new RuntimeException("Error parsing from json response", ex);
        }
        finally
        {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(stream);
        }
        return builder.toString();
    }

    /**
     * Populate HTTP message call with given content.
     * 
     * @param content String content
     * @return {@link StringEntity} content.
     * @throws UnsupportedEncodingException if unsupported
     */
    public static StringEntity setMessageBody(final String content) throws UnsupportedEncodingException
    {
        if (content == null || content.isEmpty())
        {
            throw new UnsupportedOperationException("Content is required.");
        }
        return new StringEntity(content, UTF_8_ENCODING);
    }

    /**
     * Populate HTTP message call with given content.
     * 
     * @param json {@link JSONObject} content
     * @return {@link StringEntity} content.
     * @throws UnsupportedEncodingException if unsupported
     */
    public static StringEntity setMessageBody(final JSONObject json) throws UnsupportedEncodingException
    {
        if (json == null || json.toString().isEmpty())
        {
            throw new UnsupportedOperationException("JSON Content is required.");
        }

        StringEntity se = setMessageBody(json.toString());
        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, MIME_TYPE_JSON));
        if (logger.isDebugEnabled())
        {
            logger.debug("Json string value: " + se);
        }
        return se;
    }

}
