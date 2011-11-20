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

import org.alfresco.module.vti.handler.DwsException;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.web.VtiAction;
import org.alfresco.module.vti.web.VtiUtilBase;
import org.alfresco.module.vti.web.ws.VtiEndpoint;
import org.alfresco.module.vti.web.ws.VtiSoapException;
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
public class VtiSoapAction extends VtiUtilBase implements VtiAction
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
                createFaultSOAPResponse(soapRequest, soapResponse.getDocument(), e, endpoint);
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
                String action = getSOAPAction(httpServletRequest);
                logger.debug("Target endpoint wasn't found for SOAP Action '" + action + "'");
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
     * Returns the SOAP Action that was requested, or NULL if the request isn't a SOAP action.
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

    /**
     * Creates an appropriate SOAP Fault Response, in the appropriate
     *  of the two different formats
     */
    private void createFaultSOAPResponse(VtiSoapRequest request, Element responseElement, Exception e, VtiEndpoint vtiEndpoint)
    {
        // Is it something like
        //  <FooResponse><FooResult><Error ID="123"/></FooResult></FooResponse> ?
        if (e instanceof VtiHandlerException)
        {
            VtiHandlerException handlerException = (VtiHandlerException)e;
            Element endpointResponseE = responseElement.addElement(vtiEndpoint.getName() + "Response", vtiEndpoint.getNamespace());
            Element endpointResultE = endpointResponseE.addElement(vtiEndpoint.getName() + "Result");
            
            //String errorMessage = handlerException.getMessage();
            String errorCode;
            if(handlerException.getErrorCode() > -1)
            {
               errorCode = Long.toString(handlerException.getErrorCode());
            }
            else
            {
                errorCode = "13";
            }

            // Return it as an ID based error, without the message
            endpointResultE.addElement("Error").addAttribute("ID", errorCode);
        }
        else if (e instanceof DwsException)
        {
           // <FooResponse><FooResult><Error>[ID]</Error></FooResult></FooResponse>
           DwsException handlerException = (DwsException)e;
           Element endpointResponseE = responseElement.addElement(vtiEndpoint.getResponseTagName(), vtiEndpoint.getNamespace());
           Element endpointResultE = endpointResponseE.addElement(vtiEndpoint.getResultTagName());
           
           String errorCode = handlerException.getError().toCode();

           // Return it as an Coded ID based error, without the message
           endpointResultE.setText(processTag("Error", errorCode).toString());
        }
        else
        {
            // We need to return a regular SOAP Fault
            String errorMessage = e.getMessage();
            if (errorMessage == null)
            {
                errorMessage = "Unknown error";
            }
            
            // Manually generate the Soap error response
            Element fault = responseElement.addElement(QName.get("Fault", "s", VtiSoapResponse.NAMESPACE));
            
            if("1.2".equals(request.getVersion()))
            {
               // SOAP 1.2 is Code + Reason
               Element faultCode = fault.addElement(QName.get("Code", "s", VtiSoapResponse.NAMESPACE));
               Element faultCodeText = faultCode.addElement(QName.get("Value", "s", VtiSoapResponse.NAMESPACE));
               faultCodeText.addText("s:Server");
               
               Element faultReason = fault.addElement(QName.get("Reason", "s", VtiSoapResponse.NAMESPACE));
               Element faultReasonText = faultReason.addElement(QName.get("Text", "s", VtiSoapResponse.NAMESPACE));
               faultReasonText.addText("Exception of type '"+e.getClass().getName()+"' was thrown.");
            }
            else
            {
               // SOAP 1.1 is Code + String
               Element faultCode = fault.addElement("faultcode");
               faultCode.addText("s:Server");
               Element faultString = fault.addElement("faultstring");
               faultString.addText(e.getClass().getName());
            }
            
            // Both versions get the detail
            Element detail = fault.addElement("detail");
            Element errorstring = detail.addElement(QName.get("errorstring", "", "http://schemas.microsoft.com/sharepoint/soap/"));
            errorstring.addText(errorMessage);
            
            // Include the code if known
            if(e instanceof VtiSoapException)
            {
               VtiSoapException soapException = (VtiSoapException)e;
               if(soapException.getErrorCode() > 0)
               {
                  Element errorcode = detail.addElement(QName.get("errorcode", "", "http://schemas.microsoft.com/sharepoint/soap/"));
                  errorcode.addText( "0x"+Long.toHexString(soapException.getErrorCode()) );
               }
            }
        }
    }
}
