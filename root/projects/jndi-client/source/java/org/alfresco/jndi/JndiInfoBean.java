/*-----------------------------------------------------------------------------
*  Copyright 2007-2010 Alfresco Software Limited.
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
*  
*  
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    JndiInfoBean.java
*----------------------------------------------------------------------------*/


package org.alfresco.jndi;

/**
 * An informational bean for jndi-client configuration. Currently
 * it holds the user name and password for an Admin user on the 
 * Alfresco server.
 * @author britt
 */
public class JndiInfoBean 
{
    /**
     * The user to log into the Alfresco server as.
     */
    private String alfrescoServerUser_;
    
    /**
     * The password for the user to login to the Alfresco server as.
     */
    private String alfrescoServerPassword_;

    /**
     * A Default constructor.
     */
    public JndiInfoBean()
    {
    }

    /**
     * Getter for the password.
     */
    public String getAlfrescoServerPassword() 
    {
        return alfrescoServerPassword_;
    }

    /**
     * Setter for the password.
     */
    public void setAlfrescoServerPassword(String alfrescoServerPassword) 
    {
        alfrescoServerPassword_ = alfrescoServerPassword;
    }

    /**
     * Getter for the user name.
     */
    public String getAlfrescoServerUser() 
    {
        return alfrescoServerUser_;
    }

    /**
     * Setter for the user name.
     */
    public void setAlfrescoServerUser(String alfrescoServerUser)
    {
        alfrescoServerUser_ = alfrescoServerUser;
    }
}
