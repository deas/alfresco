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
package org.alfresco.repo.lotus.ws.impl.auth;

import org.apache.cxf.message.Message;

/**
 * Web authentication fundamental API
 * 
 * @author PavelYur
 *
 */
public interface Authenticator
{

    /**
     * Authenticate user based on information retrieved from client
     * 
     * @param message the incoming message
     * 
     * @return <code>true</code> if user successfully authenticated, otherwise <code>false</code>
     */
    public boolean authenticate(Message message);
    
    /**
     * Indicate whether authenticator is active or not
     * 
     * @return <code>true</code> if authenticator is active, otherwise <code>false</code>
     */
    public boolean isActive();
}
