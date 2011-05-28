/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.solr;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.admin.CoreAdminHandler;

/**
 * @author Andy
 *
 */
public class AlfrescoCoreAdminHandler extends CoreAdminHandler
{
    private static Timer timer = new Timer(true);

    /**
     * 
     */
    public AlfrescoCoreAdminHandler()
    {
        super();
    }

    /**
     * @param coreContainer
     */
    public AlfrescoCoreAdminHandler(CoreContainer coreContainer)
    {
        super(coreContainer);
        
     // Run the cleaner around every 20 secods - this just makes the request to the thread pool
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                for(SolrCore core : AlfrescoCoreAdminHandler.this.coreContainer.getCores())
                {
                    System.out.println(core.getName() + " tracks -> " + core.getSolrConfig().getBool("alfresco/track", false));
                }
            }
        }, 0, 20000);
       
    }

}
