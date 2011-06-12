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
package org.alfresco.wcm.client.util.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.alfresco.wcm.client.impl.WebScriptCaller;
import org.apache.chemistry.opencmis.client.bindings.spi.StandardAuthenticationProvider;

public class AlfrescoTicketCmisAuthenticationProvider extends StandardAuthenticationProvider
{
    private static final long serialVersionUID = -4750134562719908905L;
    private String ticket = null;
    private WebScriptCaller webscriptCaller;
    private ReentrantLock ticketLock = new ReentrantLock(true);
    private long refetchTicketNotBefore = System.currentTimeMillis();

    public void setWebscriptCaller(WebScriptCaller webscriptCaller)
    {
        this.webscriptCaller = webscriptCaller;
    }

    @Override
    protected String getPassword()
    {
        return getTicket();
    }

    @Override
    protected String getUser()
    {
        return "";
    }

    private String getTicket()
    {
        if (ticket == null)
        {
            ticketLock.lock();
            try
            {
                if (ticket == null)
                {
                    ticket = webscriptCaller.getTicket(super.getUser(), super.getPassword());
                    refetchTicketNotBefore = System.currentTimeMillis() + 10000L;
                }
            }
            finally
            {
                ticketLock.unlock();
            }
        }
        return ticket;
    }

    @Override
    public void putResponseHeaders(String url, int statusCode, Map<String, List<String>> headers)
    {
        super.putResponseHeaders(url, statusCode, headers);
        if (statusCode == 401 || statusCode == 403)
        {
            ticketLock.lock();
            try
            {
                if (System.currentTimeMillis() >= refetchTicketNotBefore)
                {
                    ticket = null;
                }
            }
            finally
            {
                ticketLock.unlock();
            }
        }
    }
}
