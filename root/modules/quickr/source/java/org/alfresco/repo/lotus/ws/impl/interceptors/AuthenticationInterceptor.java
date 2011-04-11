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

package org.alfresco.repo.lotus.ws.impl.interceptors;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.lotus.ws.impl.auth.Authenticator;
import org.apache.cxf.binding.soap.interceptor.SoapHeaderInterceptor;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.ws.addressing.EndpointReferenceType;

/**
 * @author Eugene Zheleznyakov
 */
public class AuthenticationInterceptor extends SoapHeaderInterceptor
{
    public final static String HEADER_WWW_AUTHENTICATE = "WWW-Authenticate";

    private Authenticator authenticator;

    /**
     * Sets authenticator
     * 
     * @param authenticator the authenticator to set
     */   
    public void setAuthenticator(Authenticator authenticator)
    {
        this.authenticator = authenticator;
    }

    @Override
    public void handleMessage(final Message message) throws Fault
    {
        if (!authenticator.authenticate(message))
        {
            sendErrorResponse(message, HttpURLConnection.HTTP_UNAUTHORIZED);
        }
    }

    private void sendErrorResponse(Message message, int responseCode)
    {
        Message outMessage = getOutMessage(message);
        outMessage.put(Message.RESPONSE_CODE, responseCode);

        @SuppressWarnings("unchecked")
        Map<String, List<String>> responseHeaders = (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);
        if (responseHeaders != null)
        {
            responseHeaders.put(HEADER_WWW_AUTHENTICATE, Arrays.asList(new String[] { "Basic realm=\"Alfresco Server\"" }));
        }
        message.getInterceptorChain().abort();
        try
        {
            getConduit(message).prepare(outMessage);
            close(outMessage);
        }
        catch (IOException e)
        {
            // TODO
        }
    }

    private Message getOutMessage(Message inMessage)
    {
        Exchange exchange = inMessage.getExchange();
        Message outMessage = exchange.getOutMessage();
        if (outMessage == null)
        {
            Endpoint endpoint = exchange.get(Endpoint.class);
            outMessage = endpoint.getBinding().createMessage();
            exchange.setOutMessage(outMessage);
        }
        outMessage.putAll(inMessage);
        return outMessage;
    }

    private Conduit getConduit(Message inMessage) throws IOException
    {
        Exchange exchange = inMessage.getExchange();
        EndpointReferenceType target = exchange.get(EndpointReferenceType.class);
        Conduit conduit = exchange.getDestination().getBackChannel(inMessage, null, target);
        exchange.setConduit(conduit);
        return conduit;
    }

    private void close(Message outMessage) throws IOException
    {
        OutputStream os = outMessage.getContent(OutputStream.class);
        os.flush();
        os.close();
    }
}
