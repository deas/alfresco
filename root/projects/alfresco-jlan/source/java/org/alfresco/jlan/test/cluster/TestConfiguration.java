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

package org.alfresco.jlan.test.cluster;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.alfresco.jlan.debug.DebugConfigSection;
import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.element.GenericConfigElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Cluster Test Configuration Class
 *
 * @author gkspencer
 */
public class TestConfiguration {

	// Constants
	//
	// Node type for an Element

	private static final int ELEMENT_TYPE = 1;

	// Maximum number of threads per server
	
	public static final int MaximumThreadsPerServer		=	25;
	
	// Test classes
	
	private static final String[][] _testClasses = {{ "createFile", 		"org.alfresco.jlan.test.cluster.CreateFileTest"},
									   				{ "createFolder", 		"org.alfresco.jlan.test.cluster.CreateFolderTest"},
									   				{ "oplockGrant", 		"org.alfresco.jlan.test.cluster.OplockGrantTest"},
									   				{ "oplockBreak", 		"org.alfresco.jlan.test.cluster.OplockBreakTest"},
									   				{ "deleteFile", 		"org.alfresco.jlan.test.cluster.DeleteFileTest"},
									   				{ "deleteFolder", 		"org.alfresco.jlan.test.cluster.DeleteFolderTest"},
									   				{ "openFile", 			"org.alfresco.jlan.test.cluster.OpenFileTest"},
									   				{ "openFileSharedRead", "org.alfresco.jlan.test.cluster.OpenFileShareReadTest"},
									   				{ "byteRangeLocking", 	"org.alfresco.jlan.test.cluster.ByteRangeLockingTest"},
									   				{ "renameFile", 		"org.alfresco.jlan.test.cluster.RenameFileTest"},
									   				{ "renameFolder", 		"org.alfresco.jlan.test.cluster.RenameFolderTest"},
									   				{ "writeFileSequential","org.alfresco.jlan.test.cluster.WriteSequentialTest"},
									   				{ "writeFileRandom", 	"org.alfresco.jlan.test.cluster.WriteRandomTest"},
									   				{ "folderSearch", 		"org.alfresco.jlan.test.cluster.FolderSearchTest"},
									   				{ "changeNotify", 		"org.alfresco.jlan.test.cluster.ChangeNotifyTest"},
									   				{ "NTCreateFile",		"org.alfresco.jlan.test.cluster.NTCreateFileTest"}
	};

	// List of remote servers
	
	private List<TestServer> m_serverList;
	
	// List of tests
	
	private List<Test> m_testList;

	// Test run options
	//
	// Run tests sequentially or interleaved
	
	private boolean m_runInterleaved;
	
	// Number of test threads to create per server
	
	private int m_threadsPerServer	= 1;
	
	// Debug configuration
	
	private DebugConfigSection m_debugConfig;
	
	/**
	 * Default constructor
	 */
	public TestConfiguration() {
	}
	
	/**
	 * Return the server list
	 * 
	 * @return List<TestServer>
	 */
	public List<TestServer> getServerList() {
		return m_serverList;
	}
	
	/**
	 * Return the test list
	 * 
	 * @return List<Test>
	 */
	public List<Test> getTestList() {
		return m_testList;
	}
	
	/**
	 * Check if the tests should be run interleaved or sequentially
	 * 
	 * @return boolean
	 */
	public final boolean runInterleaved() {
		return m_runInterleaved;
	}
	
	/**
	 * Check how many test threads are to be created per server
	 * 
	 * @return int
	 */
	public final int getThreadsPerServer() {
		return m_threadsPerServer;
	}
	
	/**
	 * Load the configuration from the specified file.
	 * 
	 * @param fname java.lang.String
	 * @exception IOException
	 * @exception InvalidConfigurationException
	 */
	public final void loadConfiguration(String fname)
		throws IOException, InvalidConfigurationException {

		// Open the configuration file

		InputStream inFile = new FileInputStream(fname);
		Reader inRead = new InputStreamReader(inFile);

		// Call the main parsing method

		loadConfiguration(inRead);
	}

	/**
	 * Load the configuration from the specified input stream
	 * 
	 * @param in Reader
	 * @exception IOException
	 * @exception InvalidConfigurationException
	 */
	public final void loadConfiguration(Reader in)
		throws IOException, InvalidConfigurationException {

		// Load and parse the XML configuration document

		try {

			// Load the configuration from the XML file

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			InputSource xmlSource = new InputSource(in);
			Document doc = builder.parse(xmlSource);

			// Parse the document

			loadConfiguration(doc);
		}
		catch (Exception ex) {

			// Rethrow the exception as a configuration exeception

			throw new InvalidConfigurationException("XML error", ex);
		}
		finally {

			// Close the input file

			in.close();
		}
	}

	/**
	 * Load the configuration from the specified document
	 * 
	 * @param doc Document
	 * @exception IOException
	 * @exception InvalidConfigurationException
	 */
	public void loadConfiguration(Document doc)
		throws IOException, InvalidConfigurationException {

		// Parse the XML configuration document

		try {

			// Access the root of the XML document, get a list of the child nodes

			Element root = doc.getDocumentElement();
			NodeList childNodes = root.getChildNodes();

			// Process the server list element

			procServerList( buildConfigElement( findChildNode("servers", childNodes)));
			
			// Process the test list
			
			procTestList( buildConfigElement( findChildNode("tests", childNodes)));
			
			// Process the run parameters
			
			procRunParameters( buildConfigElement( findChildNode( "run", childNodes)));
			
			// Process the debug output element
			
			procDebugSetup( buildConfigElement( findChildNode( "debug", childNodes)));
		}
		catch (Exception ex) {

			// Rethrow the exception as a configuration exeception

			throw new InvalidConfigurationException("XML error", ex);
		}
	}

	/**
	 * Process the server list
	 * 
	 * @param servers ConfigElement
	 * @exception InvalidConfigurationException
	 */
	protected final void procServerList( ConfigElement servers)
		throws InvalidConfigurationException {
		
		// Check if the server list is valid
		
		if ( servers == null)
			throw new InvalidConfigurationException( "Server list must be specified");
		
		// Allocate the server list
		
		m_serverList = new ArrayList<TestServer>();
		
		// Check if default server settings have been specified
		
		ConfigElement defaultElem = servers.getChild( "default");
		TestServer defaultSrv = null;
		
		if ( defaultElem != null) {
			
			// Check for default values for username, password, share
			
			String defUser = defaultElem.getAttribute( "username");
			String defPass = defaultElem.getAttribute( "password");
			String defShare= defaultElem.getAttribute( "share");
			
			// Create the default server details
			
			defaultSrv = new TestServer( "", defUser, defPass, defShare);
		}
		
		// Process the server list

		List<ConfigElement> srvElemList = servers.getChildren();
		
		for ( int idx = 0; idx < srvElemList.size(); idx++) {
			
			// Get the current server details
			
			ConfigElement curElem = srvElemList.get( idx);
			if ( curElem.getName().equals( "server")) {
				
				// Create the server details
				
				String srvName = curElem.getAttribute( "name");
				if ( srvName == null || srvName.length() == 0)
					throw new InvalidConfigurationException( "Invalid server, name not specified");
				
				String srvUser = curElem.getAttribute( "username");
				String srvPass = curElem.getAttribute( "password");
				String srvShare= curElem.getAttribute( "share");
				
				// Add default values
				
				if ( defaultSrv != null) {
					if ( srvUser == null)
						srvUser = defaultSrv.getUserName();
					if ( srvPass == null)
						srvPass = defaultSrv.getPassword();
					if ( srvShare == null)
						srvShare = defaultSrv.getShareName();
				}
				
				// Create the server details
				
				TestServer testSrv = new TestServer( srvName, srvUser, srvPass, srvShare);
				m_serverList.add( testSrv);
			}
		}
	}
	
	/**
	 * Process the test list
	 * 
	 * @param tests ConfigElement
	 * @exception InvalidConfigurationException
	 */
	protected final void procTestList(ConfigElement tests)
		throws InvalidConfigurationException {
		
		// Check if the test list is valid
		
		if ( tests == null)
			throw new InvalidConfigurationException( "Test list must be specified");
		
		// Allocate the test list
		
		m_testList = new ArrayList<Test>();
		
		// Check if default test settings have been specified
		
		ConfigElement defaultElem = tests.getChild( "default");

		String defPath = null;
		int defIterations = -1;
		boolean defVerbose = false;
		boolean defCleanup = true;
		
		if ( defaultElem != null) {
			
			// Check for default values for path, iterations, verbose logging and test cleanup
			
			defPath = defaultElem.getAttribute( "path");
			String defValue = defaultElem.getAttribute( "iterations");
			if ( defValue != null) {
				try {
					defIterations = Integer.parseInt( defValue);
				}
				catch ( NumberFormatException ex) {
					throw new InvalidConfigurationException( "Invalid iterations value");
				}
			}
			
			defValue = defaultElem.getAttribute( "verbose");
			if ( defValue != null)
				defVerbose = Boolean.parseBoolean( defValue);
			
			defValue = defaultElem.getAttribute( "cleanup");
			if ( defValue != null)
				defCleanup = Boolean.parseBoolean( defValue);
		}
		
		// Process the test list

		List<ConfigElement> testElemList = tests.getChildren();
		
		for ( int idx = 0; idx < testElemList.size(); idx++) {
			
			// Get the current test details
			
			ConfigElement curElem = testElemList.get( idx);
			if ( curElem.getName().equals( "default") == false) {
				
				// Get the test name and parameters
				
				String testName = curElem.getName();
				String testPath = curElem.hasAttribute( "path") ? curElem.getAttribute( "path") : defPath;
				int testIter = 1;
				boolean testVerbose = false;
				boolean testCleanup = true;
				
				if ( curElem.hasAttribute( "iterations")) {
					String iterStr = curElem.getAttribute( "iterations"); 
					try {
						testIter = Integer.parseInt( iterStr);
					}
					catch ( NumberFormatException ex) {
						throw new InvalidConfigurationException( "Invalid 'iterations' attribute " + iterStr);
					}
				}
				else if ( defIterations != -1)
					testIter = defIterations;

				if ( curElem.hasAttribute( "verbose")) {
					String boolStr = curElem.getAttribute( "verbose");
					testVerbose = Boolean.parseBoolean( boolStr);
				}
				else
					testVerbose = defVerbose;
				
				if ( curElem.hasAttribute( "cleanup")) {
					String boolStr = curElem.getAttribute( "cleanup");
					testCleanup = Boolean.parseBoolean( boolStr);
				}
				else
					testCleanup = defCleanup;
				
				// Create the test class
				
				Test testClass = getTestClass( testName, testPath, testIter, testVerbose);
				if ( testClass != null) {

					// Set the test cleanup flag
					
					testClass.setTestCleanup( testCleanup);
					
					// Run the per test configuration
				
					testClass.configTest( curElem);
					
					// Add to the list of tests
					
					m_testList.add( testClass);
				}
				else
					throw new InvalidConfigurationException( "Invalid test name '" + curElem.getName() + "'");
			}
		}
		
		// Check that there are tests configured
		
		if ( m_testList.size() == 0)
			throw new InvalidConfigurationException( "No tests configured");
	}
	
	/**
	 * Process the run parameters
	 * 
	 * @param runParams ConfigElement
	 * @exception InvalidConfigurationException
	 */
	protected final void procRunParameters( ConfigElement runParams)
		throws InvalidConfigurationException {
		
		// Check if custom run parameters have been specified
		
		if ( runParams == null)
			return;
		
		// Check if tests should be run interleaved, or sequentially
		
		if ( runParams.getChild( "interleaved") != null)
			m_runInterleaved = true;
		
		// Check if multiple threads per server should be used
		
		ConfigElement elem = runParams.getChild( "perServer");
		if ( elem != null) {
			
			// Parse the thread per server count
			
			String srvThreads = elem.getAttribute( "threads");
			if ( srvThreads != null) {
				
				// Parse the threads per server, and validate
				
				try {
					m_threadsPerServer = Integer.parseInt( srvThreads);
					
					// Range check the threads per server value
					
					if ( m_threadsPerServer < 1 || m_threadsPerServer > MaximumThreadsPerServer)
						throw new InvalidConfigurationException( "Invalid threads per server value, " + srvThreads + ", valid range 1-" + MaximumThreadsPerServer);
				}
				catch ( NumberFormatException ex) {
					throw new InvalidConfigurationException( "Invalid threads per server value, " + srvThreads);
				}
			}
		}
	}
	
	/**
	 * Process the debug setup
	 * 
	 * @param debug ConfigElement
	 * @exception InvalidConfigurationException
	 */
	protected final void procDebugSetup( ConfigElement debug)
		throws InvalidConfigurationException {
		
		// Check if the debug section has been specified

		if ( debug == null)
			return;

		// Create the debug configuration section

		m_debugConfig = new DebugConfigSection( null);

		// Get the debug output class and parameters

		ConfigElement elem = debug.getChild( "output");
		if ( elem == null)
			throw new InvalidConfigurationException("Output class must be specified to enable debug output");

		// Get the debug output class

		ConfigElement debugClass = elem.getChild( "class");
		if ( debugClass == null)
			throw new InvalidConfigurationException("Class must be specified for debug output");

		// Get the parameters for the debug class

		m_debugConfig.setDebug( debugClass.getValue(), elem);
	}

	/**
	 * Find the test class
	 * 
	 * @param name String
	 * @param path String
	 * @param iter int
	 * @param verbose boolean
	 * @return Test
	 */
	private Test getTestClass( String name, String path, int iter, boolean verbose) {

		// Validate the test name and return the test class
		
		Test test = null;
		
		try {
			
			// Validate the test name, find the associated test class
			
			String testClass = null;
			int idx = 0;
			
			while ( idx < _testClasses.length && testClass == null) {
				if ( _testClasses[idx][0].equalsIgnoreCase( name))
					testClass = _testClasses[idx][1];
				else
					idx++;
			}
			
			if ( testClass == null)
				return null;
			
			// Load the test class, and validate
			
			Object testClassObj = Class.forName( testClass).newInstance();
			if ( testClassObj != null && testClassObj instanceof Test) {
				test = (Test) testClassObj;
				
				// Set the basic test settings
				
				test.setPath( path);
				test.setIterations( iter);
				test.setVerbose( verbose);
			}
		}
		catch ( Exception ex) {
			ex.printStackTrace();
		}
		
		// Return the test, or null if not found
		
		return test;
	}
	
	/**
	 * Find the specified child node in the node list
	 * 
	 * @param name String
	 * @param list NodeList
	 * @return Element
	 */
	protected final Element findChildNode(String name, NodeList list) {

		// Check if the list is valid

		if ( list == null)
			return null;

		// Search for the required element

		for (int i = 0; i < list.getLength(); i++) {

			// Get the current child node

			Node child = list.item(i);
			if ( child.getNodeName().equals(name) && child.getNodeType() == ELEMENT_TYPE)
				return (Element) child;
		}

		// Element not found

		return null;
	}

	/**
	 * Get the value text for the specified element
	 * 
	 * @param elem Element
	 * @return String
	 */
	protected final String getText(Element elem) {

		// Check if the element has children

		NodeList children = elem.getChildNodes();
		String text = "";

		if ( children != null && children.getLength() > 0 && children.item(0).getNodeType() != ELEMENT_TYPE)
			text = children.item(0).getNodeValue();

		// Return the element text value

		return text;
	}

	/**
	 * Build a configuration element list from an elements child nodes
	 * 
	 * @param root Element
	 * @return GenericConfigElement
	 */
	protected final GenericConfigElement buildConfigElement(Element root) {
		return buildConfigElement(root, null);
	}

	/**
	 * Build a configuration element list from an elements child nodes
	 * 
	 * @param root Element
	 * @param cfgElem GenericConfigElement
	 * @return GenericConfigElement
	 */
	protected final GenericConfigElement buildConfigElement(Element root, GenericConfigElement cfgElem) {

		// Create the top level element, if not specified

		GenericConfigElement rootElem = cfgElem;

		if ( rootElem == null) {

			// Create the root element

			rootElem = new GenericConfigElement(root.getNodeName());

			// Add any attributes

			NamedNodeMap attribs = root.getAttributes();
			if ( attribs != null) {
				for (int i = 0; i < attribs.getLength(); i++) {
					Node attribNode = attribs.item(i);
					rootElem.addAttribute(attribNode.getNodeName(), attribNode.getNodeValue());
				}
			}
		}

		// Get the child node list

		NodeList nodes = root.getChildNodes();
		if ( nodes == null)
			return rootElem;

		// Process the child node list

		GenericConfigElement childElem = null;

		for (int i = 0; i < nodes.getLength(); i++) {

			// Get the current node

			Node node = nodes.item(i);

			if ( node.getNodeType() == ELEMENT_TYPE) {

				// Access the Element

				Element elem = (Element) node;

				// Check if the element has any child nodes

				NodeList children = elem.getChildNodes();

				if ( children != null && children.getLength() > 1) {

					// Add the child nodes as child configuration elements

					childElem = buildConfigElement(elem, null);
				}
				else {

					// Create a normal name/value

					if ( children.getLength() > 0) {
						childElem = new GenericConfigElement(elem.getNodeName());
						childElem.setValue(children.item(0).getNodeValue());
					}
					else
						childElem = new GenericConfigElement(elem.getNodeName());

					// Add any attributes

					NamedNodeMap attribs = elem.getAttributes();
					if ( attribs != null) {
						for (int j = 0; j < attribs.getLength(); j++) {
							Node attribNode = attribs.item(j);
							childElem.addAttribute(attribNode.getNodeName(), attribNode.getNodeValue());
						}
					}
				}

				// Add the child configuration element

				rootElem.addChild(childElem);
			}
		}

		// Return the configuration element

		return rootElem;
	}
}
