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
package org.alfresco.repo.transfer.fsr;

import org.springframework.extensions.webscripts.AbstractBasicHttpAuthenticatorFactory;
import org.springframework.extensions.webscripts.Description.RequiredAuthentication;

public class WebscriptAuthenticatorFactoryImpl extends AbstractBasicHttpAuthenticatorFactory
{
    private String permittedUsername;
    private String permittedPassword;

    public void setPermittedUsername(String permittedUsername)
    {
        this.permittedUsername = permittedUsername;
    }

    public void setPermittedPassword(String permittedPassword)
    {
        this.permittedPassword = permittedPassword;
    }

    @Override
    public boolean doAuthenticate(String username, String password)
    {
        return (username != null && username.equals(permittedUsername) && password != null && 
                password.equals(permittedPassword));
    }

    @Override
    public boolean doAuthorize(String username, RequiredAuthentication role)
    {
        return username != null && username.equals(permittedUsername);
    }

}
