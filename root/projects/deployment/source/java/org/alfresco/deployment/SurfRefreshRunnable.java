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

package org.alfresco.deployment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.alfresco.deployment.impl.server.Deployment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.net.www.protocol.http.HttpURLConnection;

/**
 * Post-deployment runnable that refreshes the cache on a Surf application.
 * 
 * This opens a URL connection to the cache control servlet from the core Web
 * Framework package.
 * 
 * @author muzquiano
 */
public class SurfRefreshRunnable implements FSDeploymentRunnable
{
    private static final long serialVersionUID = -5792264392686730729L;

    private static Log logger = LogFactory.getLog(SurfRefreshRunnable.class);

    // The deployment object
    private Deployment deployment = null;

    // Location of the Surf instance
    private String surfLocation = null;

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.deployment.FSDeploymentRunnable#init(org.alfresco.deployment.impl.server.Deployment)
     */
    public void init(Deployment deployment)
    {
        this.deployment = deployment;
    }

    /**
     * Sets the location of the Surf application
     * 
     * @param surfLocation the location of the Surf application
     */
    public void setSurfLocation(String surfLocation)
    {
        this.surfLocation = surfLocation;
    }

    /**
     * Gets the location of the Surf application
     * 
     * @return the surf application location
     */
    public String getSurfLocation()
    {
        if (this.surfLocation == null)
        {
            this.surfLocation = "http://localhost:8080/surf";
        }

        return this.surfLocation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        String url = this.getSurfLocation() + "/control/cache/invalidate";

        try
        {
            if (logger.isDebugEnabled())
                logger.warn("Refreshing cache with URL: " + url);

            fireGet(url);
        }
        catch (MalformedURLException e)
        {
            if (logger.isWarnEnabled())
                logger.warn("Unable to refresh Surf cache for URL: " + url, e);
        }
        catch (ProtocolException e)
        {
            if (logger.isWarnEnabled())
                logger.warn("Unable to refresh Surf cache for URL: " + url, e);
        }
        catch (IOException e)
        {
            if (logger.isWarnEnabled())
                logger.warn("Unable to refresh Surf cache for URL: " + url, e);
        }
    }

    /**
     * Fires an HTTP GET to a given url
     * 
     * @param urlString the url string
     */
    private void fireGet(String urlString) throws MalformedURLException,
            ProtocolException, IOException
    {
        URL url = null;
        HttpURLConnection conn = null;
        BufferedReader rd = null;
        StringBuilder sb = null;
        String line = null;

        try
        {
            url = new URL(urlString);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setReadTimeout(10000);

            conn.connect();

            // read back into throwaway buffer
            rd = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            sb = new StringBuilder();
            line = null;

            while ((line = rd.readLine()) != null)
            {
                sb.append(line + '\n');
            }
        }
        finally
        {
            if (conn != null)
            {
                conn.disconnect();
            }

            rd = null;
            sb = null;
            conn = null;
        }
    }
}
