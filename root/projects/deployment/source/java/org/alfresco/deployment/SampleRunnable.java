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

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.alfresco.deployment.impl.server.DeployedFile;
import org.alfresco.deployment.impl.server.Deployment;
import org.alfresco.util.GUID;

/**
 * Example post filesystem deployment runnable.
 * @author britt
 */
public class SampleRunnable implements FSDeploymentRunnable
{
    private static final long serialVersionUID = -5792264492686730729L;

    // The deployment just completed.
    private Deployment fDeployment = null;
    
    /* (non-Javadoc)
     * @see org.alfresco.deployment.FSDeploymentRunnable#init(org.alfresco.deployment.impl.server.Deployment)
     */
    public void init(Deployment deployment)
    {
        fDeployment = deployment;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        String guid = GUID.generate();
        try
        {
            Writer out = new FileWriter("dep-record-" + guid);
            for (DeployedFile file : fDeployment)
            {
                out.write(file.getType().toString() + " " + file.getPath() + " " + file.getGuid() + "\n");
            }
            out.close();
        }
        catch (IOException e)
        {
            // Do nothing.
        }
    }
}
