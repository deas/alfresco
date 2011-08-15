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
package org.alfresco.module.vti.web.actions;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.web.VtiAction;
import org.alfresco.module.vti.web.ws.VtiEndpoint;
import org.alfresco.module.vti.web.ws.VtiSoapRequest;
import org.alfresco.module.vti.web.ws.VtiSoapResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.dom4j.QName;

/**
* <p>VtiSoapAction is processor of Web service requests. It provides 
* the back-end controller for dispatching among set of VtiEndpoints. 
* It selects and invokes a realization of {@link VtiEndpoint} to perform 
* the requested Web service method. In addition it is an adapter for
* wrapping HttpServletRequest and HttpServletResponse in {@link VtiSoapRequest}
* and {@link VtiSoapResponse}.</p>
*
* @author Stas Sokolovsky
*
*/
public class VtiSoapAction implements VtiAction
{

    private Map<String, VtiEndpoint> endpointsMapping; 
    
    private final static Log logger = LogFactory.getLog(VtiSoapAction.class);
    
    /**
     * <p>Process Web service request, dispatch among set of VtiEndpoints. 
     * Select and invoke a realization of {@link VtiEndpoint} to perform the
     * requested Web service method</p> 
     *
     * @param request HTTP request
     * @param response HTTP response
     */
    public void execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
    {
        VtiSoapRequest soapRequest = new VtiSoapRequest(httpServletRequest);
        VtiSoapResponse soapResponse = new VtiSoapResponse(httpServletResponse);
        VtiEndpoint endpoint = dispatchRequest(soapRequest);
        if (endpoint != null)
        {
            try
            {
                endpoint.execute(soapRequest, soapResponse);
            }
            catch (Exception e)
            {
                logger.debug(e.getMessage(), e);
                soapResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                createFaultSOAPResponse(soapResponse.getDocument(), e, endpoint);
                logger.warn("Failure executing Vti request", e);
            }
            try
            {
                soapResponse.flushBuffer();
            }
            catch (IOException e)
            {
                // doesn't matter
            }
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Target endpoint wasn't found for SOAP Action '" + httpServletRequest.getHeader("SOAPAction") + "'");
            }
        }

    }
    
    /**
     * <p>Endpoints mapping setter.</p>
     *
     * @param endpointsMapping describe mapping from name of Web service method to
     * its realization.    
     */
    public void setEndpointsMapping(Map<String, VtiEndpoint> endpointsMapping)
    {
        this.endpointsMapping = endpointsMapping;
    }
    
    /**
     * Returns the SOAP Action that was requested, or NULL if the request
     *  isn't a SOAP action.
     * This method handles any de-quoting that is required.
     */
    public static String getSOAPAction(HttpServletRequest request)
    {
       // Fetch, and de-quote if needed the action
       String soapAction = request.getHeader("SOAPAction");
       if (soapAction != null)
       {
          // SOAP 1.1, de-quote
          if (soapAction.startsWith("\"") && soapAction.endsWith("\""))
          {
             soapAction = soapAction.substring(1, soapAction.length()-1);
          }
       }
       else
       {
          // SOAP 1.2
          if (request.getContentType() != null)
          {
             Pattern p = Pattern.compile("action=\"(.*?)\"");
             Matcher m = p.matcher(request.getContentType());
             if(m.find())
             {
                soapAction = m.group(1);
             }
          }
       }
       
       return soapAction;
    }
    
    /**
     * <p>
     * Dispatch among set of VtiEndpoints.
     * 
     * @param request {@link VtiSoapRequest}
     */
    protected VtiEndpoint dispatchRequest(VtiSoapRequest request)
    {
        String soapAction = getSOAPAction(request);
        VtiEndpoint result = endpointsMapping.get(soapAction);
        return result;
    }

    private void createFaultSOAPResponse(Element responsElement, Exception e, VtiEndpoint vtiEndpoint)
    {

        if (e instanceof VtiHandlerException)
        {
            VtiHandlerException handlerException = (VtiHandlerException)e;
            Element resultElement = responsElement.addElement(vtiEndpoint.getName() + "Response", vtiEndpoint.getNamespace()).addElement(vtiEndpoint.getName() + "Result");
            
            String errorMessage = handlerException.getMessage();
            String errorCode;
            if(handlerException.getErrorCode() > -1)
            {
               errorCode = Integer.toString(handlerException.getErrorCode());
            }
            else
            {
                errorCode = "13";
            }
            // TODO Is this correct? Should we really be returning a different
            //  error code in this case to the other SOAP ones?
            resultElement.addElement("Error").addAttribute("ID", errorCode);
        }
        else
        {
            String errorMessage = e.getMessage();
            if (errorMessage == null)
            {
                errorMessage = "Unknown error";
            }
            Element fault = responsElement.addElement(QName.get("Fault", "s", VtiSoapResponse.NAMESPACE));
            Element faultCode = fault.addElement("faultcode");
            faultCode.addText("s:Server");
            Element faultString = fault.addElement("faultstring");
            faultString.addText(e.getClass().toString());
            Element detail = fault.addElement("detail");
            Element errorstring = detail.addElement(QName.get("errorstring", "", "http://schemas.microsoft.com/sharepoint/soap/"));
            errorstring.addText(errorMessage);
        }
    }
}
