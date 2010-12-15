/*
 * Copyright (C) 2006-2010 Alfresco Software Limited.
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

package org.alfresco.jlan.app;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.debug.DebugConfigSection;
import org.alfresco.jlan.ftp.FTPConfigSection;
import org.alfresco.jlan.netbios.server.NetBIOSNameServer;
import org.alfresco.jlan.netbios.win32.Win32NetBIOS;
import org.alfresco.jlan.oncrpc.nfs.NFSConfigSection;
import org.alfresco.jlan.server.NetworkServer;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.alfresco.jlan.smb.server.CIFSConfigSection;
import org.alfresco.jlan.smb.server.SMBServer;
import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;


/**
 * JLAN Server Service Class
 *
 * @author gkspencer
 */
public class JLANServerService implements WrapperListener, Runnable {

	//	Default configuration file name
	
	private static final String DEFAULT_CONFIGFILENAME = "jlanserver.xml";
	
	//	Server shutdown flag
	
	private boolean m_shutdown;

	//	Server configuration
	
	private ServerConfiguration m_config;
	
	//	Thread used to start the various servers
	
	private Thread m_serverThread;
			
	/**
	 * Service start requested
	 * 
	 * @param args String[]
	 * @return Integer 
	 */
	public Integer start(String[] args) {

		//  Command line parameter should specify the configuration file

		PrintStream out = System.out;
		String fileName = null;

		if (args.length < 1) {

			//	Search for a default configuration file in the users home directory
			
			fileName = System.getProperty("user.home") + File.separator + DEFAULT_CONFIGFILENAME;
		}
		else
			fileName = args[0];

		//  Load the configuration

		m_config = null;
		
		try {

			//	Create an XML configuration

			m_config = new XMLServerConfiguration();
			m_config.loadConfiguration(fileName);
		}
		catch (Exception ex) {

			//  Failed to load server configuration

			out.println("%% Failed to load server configuration");
			ex.printStackTrace(out);
			return new Integer(2);
		}

		//	Check if the local IP address returns a valid value, '127.0.0.1' indicates a mis-configuration in the hosts file
		
		try {
			
			//	Get the local address
			
			String localAddr = InetAddress.getLocalHost().getHostAddress();
			if ( localAddr.equals("127.0.0.1")) {
				out.println("%% Local IP address resolves to 127.0.0.1, this may be caused by a mis-configured hosts file");
				return new Integer(3);
			}
		}
		catch (UnknownHostException ex) {
			
			//	Failed to get local host IP address details
			
			out.println("%% Failed to get local IP address details");
			ex.printStackTrace(out);
			return new Integer(4);
		}
		
    //  NetBIOS name server, SMB, FTP and NFS servers

    try {

      //  Create the SMB server and NetBIOS name server, if enabled
      
      if ( m_config.hasConfigSection( CIFSConfigSection.SectionName)) {
        
        // Get the CIFS server configuration
        
        CIFSConfigSection cifsConfig = (CIFSConfigSection) m_config.getConfigSection( CIFSConfigSection.SectionName);
        
        //  Load the Win32 NetBIOS library
        //
        //  For some strange reason the native code loadLibrary() call hangs if done later by the SMBServer.
        //  Forcing the Win32NetBIOS class to load here and run the static initializer fixes the problem.

        if ( cifsConfig.hasWin32NetBIOS())
          Win32NetBIOS.LanaEnumerate();
        
        //  Create the NetBIOS name server if NetBIOS SMB is enabled
        
        if  (cifsConfig.hasNetBIOSSMB())
          m_config.addServer( createNetBIOSServer(m_config));

        //  Create the SMB server
        
        m_config.addServer( createSMBServer(m_config));
      }

      //  Create the FTP server, if enabled
      
      if ( m_config.hasConfigSection( FTPConfigSection.SectionName)) {
        
        //  Create the FTP server
      
        m_config.addServer( createFTPServer( m_config));
      }
        
      //  Create the NFS server and mount server, if enabled
      
      if ( m_config.hasConfigSection( NFSConfigSection.SectionName)) {
        
        //  Get the NFS server configuration
        
        NFSConfigSection nfsConfig = (NFSConfigSection) m_config.getConfigSection( NFSConfigSection.SectionName);
        
        //  Check if the port mapper is enabled
        
        if ( nfsConfig.hasNFSPortMapper())
          m_config.addServer( createNFSPortMapper( m_config));
          
        //  Create the mount server
        
        m_config.addServer( createNFSMountServer( m_config));
        
        //  Create the NFS server
        
        m_config.addServer( createNFSServer( m_config));
      }

			//	Start the configured servers in a seperate thread
			
			m_serverThread = new Thread(this);
			m_serverThread.start();
		}
		catch (Exception ex) {
			out.println("%% Server error");
			ex.printStackTrace(out);
			return new Integer(5);
		}

		//	Indicate that the service started
		
		return null;
	}

	/**
	 * Service stop requested
	 * 
	 * @param exitCode int
	 * @return int 
	 */
	public int stop(int exitCode) {

		//	Set the shutdown flag
		
		m_shutdown = true;
		
    //  Get the debug configuration
    
    DebugConfigSection dbgConfig = (DebugConfigSection) m_config.getConfigSection( DebugConfigSection.SectionName);
    
		//	Check if the server list is valid
		
		if ( m_config.numberOfServers() > 0) {

			//	Shutdown the servers
				
			for ( int i = 0; i < m_config.numberOfServers(); i++) {
					
				//	Indicate that the service is stopping
				
				WrapperManager.signalStopping(5000);
				
				//	Get the current server
						
				NetworkServer server = m_config.getServer(i);
						
				//	DEBUG
						
				if ( Debug.EnableInfo && dbgConfig != null && dbgConfig.hasDebug())
					Debug.println("Shutting server " + server.getProtocolName() + " ...");
							
				//	Start the server
					
				m_config.getServer(i).shutdownServer(false);
			}
		}

		//	Indicate that the service is stopped
		
		WrapperManager.signalStopped(5000);
				
		//	Return the status code
		
		return exitCode;
	}

	/**
	 * Handle control events
	 * 
	 * @param event int
	 */
	public void controlEvent(int event) {
		
		//	Check if the wrapper manager is handling events
		
		if ( WrapperManager.isControlledByNativeWrapper() == false) {
			
			//	The wrapper manager is not handling events, handle it here
			
			if ( event == WrapperManager.WRAPPER_CTRL_C_EVENT ||
					 event == WrapperManager.WRAPPER_CTRL_CLOSE_EVENT ||
					 event == WrapperManager.WRAPPER_CTRL_SHUTDOWN_EVENT) {
					 	
				//	Stop the service
			
				WrapperManager.stop(0);
		  }
		}
	}

	/**
	 * Main application startup
	 * 
	 * @param args String[]
	 */
	public static void main(String[] args) {
		
		//	Start the main JLAN Server application via the service wrapper
		
		WrapperManager.start( new JLANServerService(), args);
	}

	/**
	 * Create the SMB server
	 * 
	 * @param config ServerConfiguration
	 * @return NetworkServer
	 * @exception Exception
	 */
	protected final static NetworkServer createSMBServer(ServerConfiguration config)
		throws Exception {
			
		//	Create an SMB server
		
		return new SMBServer(config);
	}
	
	/**
	 * Create the NetBIOS name server
	 * 
	 * @param config ServerConfiguration
	 * @return NetworkServer
	 * @exception Exception
	 */
	protected final static NetworkServer createNetBIOSServer(ServerConfiguration config)
		throws Exception {
			
		//	Create a NetBIOS name server
		
		return new NetBIOSNameServer(config);
	}
	
	/**
	 * Create the FTP server
	 * 
	 * @param config ServerConfiguration
	 * @return NetworkServer
	 * @exception Exception
	 */
	protected final static NetworkServer createFTPServer(ServerConfiguration config)
		throws Exception {
			
		//	Create an FTP server
		
		return createServer( "org.alfresco.jlan.ftp.FTPServer", config);
	}
	
	/**
	 * Create the NFS server
	 * 
	 * @param config ServerConfiguration
	 * @return NetworkServer
	 * @exception Exception
	 */
	protected final static NetworkServer createNFSServer(ServerConfiguration config)
		throws Exception {
			
    //  Create the NFS server instance
    
    return createServer( "org.alfresco.jlan.oncrpc.nfs.NFSServer", config);
	}
	
	/**
	 * Create the NFS mount server
	 * 
	 * @param config ServerConfiguration
	 * @return NetworkServer
	 * @exception Exception
	 */
	protected final static NetworkServer createNFSMountServer(ServerConfiguration config)
		throws Exception {
			
    //  Create the mount server instance
    
    return createServer( "org.alfresco.jlan.oncrpc.mount.MountServer", config);
	}

	/**
	 * Create the NFS port mapper server
	 * 
	 * @param config ServerConfiguration
	 * @return NetworkServer
	 */
	protected final static NetworkServer createNFSPortMapper(ServerConfiguration config)
		throws Exception {
			
    //  Create the port mapper server instance
    
    return createServer( "org.alfresco.jlan.oncprc.portmap.PortMapperServer", config);
	}
	
	/**
	 * Create a network server using reflection
	 * 
	 * @param className String
	 * @param config ServerConfiguration
	 * @return NetworkServer
	 * @exception Exception
	 */
	protected final static NetworkServer createServer(String className, ServerConfiguration config)
		throws Exception {

		//	Create the server instance using reflection
	
		NetworkServer srv = null;
	
		//	Find the server constructor
	
		Class<?>[] classes = new Class[1];
		classes[0] = ServerConfiguration.class;
		Constructor<?> srvConstructor = Class.forName(className).getConstructor(classes);
	
		//	Create the network server
	
		Object[] args = new Object[1];
		args[0] = config;
		srv = (NetworkServer) srvConstructor.newInstance(args);

		//	Return the network server instance
		
		return srv;
	}

	/**
	 * Thread method 
	 */
	public void run() {

		//	Check if there are any servers configured
		
		if ( m_config.numberOfServers() > 0) {

			//	Clear the shutdown flag
			
			m_shutdown = false;
			
      //  Get the debug configuration
      
      DebugConfigSection dbgConfig = (DebugConfigSection) m_config.getConfigSection( DebugConfigSection.SectionName);
      
			//	Start the servers
			
			for ( int i = 0; i < m_config.numberOfServers(); i++) {
					
				//	Indicate that the servers are starting
					
				WrapperManager.signalStarting(10000);
					
				//	Get the current server
					
				NetworkServer server = m_config.getServer(i);
					
				//	DEBUG
					
				if ( Debug.EnableInfo && dbgConfig != null && dbgConfig.hasDebug())
					Debug.println("Starting server " + server.getProtocolName() + " ...");
						
				//	Start the server
					
				m_config.getServer(i).startServer();
			}
			
			//	Wait for shutdown request
			
			while ( m_shutdown == false) {
				try {
					Thread.sleep(250);
				}
				catch (Exception ex) {
				}
			}
		}
	}
}
