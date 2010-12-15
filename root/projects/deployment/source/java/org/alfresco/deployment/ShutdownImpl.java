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

import org.alfresco.deployment.impl.DeploymentException;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;


/**
 * Class to shutdown this instance of the deployment server.
 * @author mrogers
 *
 */
public class ShutdownImpl {
   
    private int registryPort = 44100;
    private String serviceName = "deployment";
    private String hostName = "localhost";
    private String user = "admin";
    private char[] password = "password".toCharArray();
	
    public void init() {
	 		
	   // lookup service
	   DeploymentReceiverTransport service = getTransport();
	   
	   try {
		   // Do the shutdown
		   service.shutDown(getUser(), getPassword());
	   
		   // how to dispose of the service ?
		   service = null;
	   
	   }  catch (Exception e) {
		   // Do nothing - the remote service should have terminated
		   service=null;
	   }
	   
   }
   
   private DeploymentReceiverTransport getTransport()
   {
       try
       {
           RmiProxyFactoryBean factory = new RmiProxyFactoryBean();
           factory.setRefreshStubOnConnectFailure(true);
           factory.setServiceInterface(DeploymentReceiverTransport.class);
           factory.setServiceUrl("rmi://" + hostName + ":" + registryPort + "/" + serviceName);
           factory.afterPropertiesSet();
           DeploymentReceiverTransport transport = (DeploymentReceiverTransport)factory.getObject();
           return transport;
       }
       catch (Exception e)
       {
           throw new DeploymentException("Could not connect to " + hostName + " at " + registryPort, e);
       }
   }

    public void setRegistryPort(int servicePort) 
    {
	    this.registryPort = servicePort;
    }

    public int getRegistryPort() 
    {
	    return registryPort;
    }

    public void setServiceName(String serviceName) {
	    this.serviceName = serviceName;
    }

    public String getServiceName() {
	    return serviceName;
    }
    
    public void setHostName(String hostName) {
	    this.hostName = hostName;
    }

    public String getHostName() {
	    return hostName;
    }

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public char[] getPassword() {
		return password;
	}
}
