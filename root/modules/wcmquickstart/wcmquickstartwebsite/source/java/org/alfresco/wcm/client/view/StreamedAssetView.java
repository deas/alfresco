/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.wcm.client.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.wcm.client.impl.StreamUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * Stream an asset for the view
 * 
 * @author Chris Lack
 * 
 */
public class StreamedAssetView extends AbstractUrlBasedView
{
    private static final Log log = LogFactory.getLog(StreamedAssetView.class.getName());
    private InputStream stream;

    /**
     * Construct the view with the image details
     * 
     * @param stream
     *            the stream of data which represents the image
     * @param mimeType
     *            the mime type of the image
     */
    public StreamedAssetView(String url, InputStream stream, String mimeType)
    {
        super(url);
        this.stream = stream;
        setContentType(mimeType);
    }

    /**
     * @see org.springframework.web.servlet.view.AbstractView#renderMergedOutputModel(java.util.Map,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        ServletOutputStream out = null;

        if (stream == null)
        {
            log.debug("Asset contents are not available!");
            return;
        }

        try
        {
            // Write the InputStream to the servlet OutputStream
            out = response.getOutputStream();
            StreamUtils.output(stream, out);
        }
        catch (IOException ex)
        {
            log.error("Unable to stream asset data!", ex);
        }
        finally
        {
            if (out != null)
                out = null;
        }
    }
}
