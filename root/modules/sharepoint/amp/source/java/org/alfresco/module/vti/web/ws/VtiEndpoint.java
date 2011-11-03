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

package org.alfresco.module.vti.web.ws;


/**
* Interface that must implement all the Vti endpoints realizations
*   
* @author Stas Sokolovsky
*
*/
public interface VtiEndpoint
{
    /**
     * Executes target endpoint method
     * 
     * @param soapRequest Vti Soap Request ({@link VtiSoapRequest})
     * @param soapResponse Vti Soap Response ({@link VtiSoapResponse})
     */
    public void execute(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse) throws Exception;
    
    /**
     * @return the name of the endpoint
     */
    public String getName();
    
    /**
     * @return the namespace of the endpoint
     */
    public String getNamespace();
    
    public String getResponseTagName();

    public String getResultTagName();
}
