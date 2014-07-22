/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.webscripts;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.config.ServerConfigElement;
import org.springframework.extensions.config.ServerProperties;
import org.springframework.extensions.surf.util.URLDecoder;
import org.springframework.extensions.webscripts.servlet.ServletAuthenticatorFactory;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRuntime;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Stand-alone Web Script Test Server
 * 
 * @author davidc
 */
@Ignore public class TestWebScriptServer implements ApplicationContextAware, InitializingBean
{
    /** The application context */
    protected ApplicationContext applicationContext;
    
    // dependencies
    protected ConfigService configService;	
    protected RuntimeContainer container;
    protected ServletAuthenticatorFactory authenticatorFactory;
    
    /** Server Configuration */
    protected ServerProperties serverProperties;
    
    /** The reader for interaction. */
    protected BufferedReader fIn;
    
    /** Last command issued */
    protected String lastCommand = null;

    /** Current user */
    protected String username = null;
    
    /** Current headers */
    protected Map<String, String> headers = new HashMap<String, String>();
    
    /** I18N Messages */
    protected MessageSource m_messages;    
    
    
    /**
     * @param configService
     */
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }
    
    /**
     * Sets the Web Script Runtime Context
     * 
     * @param container
     */
    public void setContainer(RuntimeContainer container)
    {
        this.container = container;
    }

    /**
     * @param authenticatoFactory
     */
    public void setServletAuthenticatorFactory(ServletAuthenticatorFactory authenticatorFactory)
    {
        this.authenticatorFactory = authenticatorFactory;
    }

    /**
     * Sets the Messages resource bundle
     * 
     * @param messages
     * @throws IOException
     */
    public void setMessages(MessageSource messages)
        throws IOException
    {
        this.m_messages = messages;
    }
    
    /**
     * Sets the application context 
     * 
     * @param applicationContext    the application context
     * @throws BeansException
     */
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception
    {
        username = getDefaultUserName();
    }

    /**
     * Get default user name
     */
    protected String getDefaultUserName()
    {
        return "admin";
    }

    /**
     * Gets the server properties
     * 
     * @return  server properties
     * @throws Exception
     */
	public ServerProperties getServerProperties()
	{
		if (serverProperties == null)
		{
			Config config = configService.getConfig("Server");
			serverProperties = (ServerConfigElement)config.getConfigElement(ServerConfigElement.CONFIG_ELEMENT_ID);
		}
		return serverProperties;
	}
    
    /**
     * Gets the application context
     * 
     * @return  ApplicationContext  the application context
     */
    public ApplicationContext getApplicationContext()
    {
        return this.applicationContext;
    }

    
    /**
     * Main entry point.
     */
    public static void main(String[] args)
    {
        try
        {
            TestWebScriptServer testServer = getTestServer();
            testServer.rep();
        }
        catch(Throwable e)
        {
            StringWriter strWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(strWriter);
            e.printStackTrace(printWriter);
            System.out.println(strWriter.toString());
        }
        finally
        {
            System.exit(0);
        }
    }

    
    /**
     * Retrieve an instance of the TestWebScriptServer
     *  
     * @return  Test Server
     */
    public static TestWebScriptServer getTestServer()
    {
        String[] CONFIG_LOCATIONS = new String[]
        {
            "classpath:org/springframework/extensions/webscripts/spring-webscripts-application-context.xml", 
            "classpath:org/springframework/extensions/webscripts/spring-webscripts-application-context-test.xml"
        };
        ApplicationContext context = new ClassPathXmlApplicationContext(CONFIG_LOCATIONS);
        TestWebScriptServer testServer = (TestWebScriptServer)context.getBean("webscripts.test");
        
        return testServer;
    }
    
    /**
     * Submit a Web Script Request.
     * 
     * @param req  request
     * @return  response
     * @throws IOException
     */
    public Response submitRequest(Request req)
        throws IOException
    {
        return submitRequest(req.getMethod(), req.getUri(), req.getHeaders(), req.getBody(), req.getEncoding(), req.getType());
    }
    
    /**
     * Submit a Web Script Request. 
     * <p>
     * Can specifiy content and content type
     * 
     * @param method        http method
     * @param uri           web script (relative to /alfresco/service)
     * @param headers       headers
     * @param body          body of request content (can be null)
     * @param contentType   content type (eg "multipart/form-data") (can be null)
     * @return              response           
     * @throws IOException
     */
    public Response submitRequest(String method, String uri, Map<String, String> headers, byte[] body, String encoding, String contentType)
        throws IOException
    {
        MockHttpServletRequest req = createMockServletRequest(method, uri);
        
        // Set the headers
        if (headers != null)
        {
            for (Map.Entry<String, String> header: headers.entrySet())
            {
                req.addHeader(header.getKey(), header.getValue());
            }
        }        

        // Set the body of the request
        if (body != null)
        {            
            req.setContent(body);
        }
        
        if (encoding != null)
        {
            req.setCharacterEncoding(encoding);
        }
        
        // Set the content type
        if (contentType != null && contentType.length() != 0)
        {
            req.setContentType(contentType);
            req.addHeader("Content-Type", contentType);
        }
        
        MockHttpServletResponse res = new MockHttpServletResponse();
        AbstractRuntime runtime = new WebScriptServletRuntime(container, authenticatorFactory, req, res, getServerProperties());
        runtime.executeScript();
        return new MockHttpServletResponseResponse(res);
    }
    
    /**
     * Create a Mock HTTP Servlet Request
     * 
     * @param method
     * @param uri
     * @return  mock http servlet request
     * @throws UnsupportedEncodingException 
     * @throws MalformedURLException 
     */
    private MockHttpServletRequest createMockServletRequest(String method, String uri)
        throws UnsupportedEncodingException, MalformedURLException
    {
        // extract only path portions of URI, ignore host & port
        URL url = new URL(new URL("http://localhost"), uri);
        String path = url.getPath();
        
        if (!(path.startsWith("/alfresco/service") || path.startsWith("/a/s")))
        {
            path = "/alfresco/service" + path;
        }
        
        MockHttpServletRequest req = new MockHttpServletRequest(method, uri);
        req.setContextPath("/alfresco");
        req.setServletPath("/service");

        if (uri != null)
        {
            String queryString = url.getQuery();
            if (queryString != null && queryString.length() > 0)
            {
                String[] args = queryString.split("&");
                for (String arg : args)
                {
                    String[] parts = arg.split("=");
                    req.addParameter(parts[0], (parts.length == 2) ? URLDecoder.decode(parts[1]) : null);
                }
                req.setQueryString(queryString);
            }
            String requestURI = path;
            req.setRequestURI(requestURI);
        }
        
        return req;
    }

    /**
     * A Read-Eval-Print loop.
     */
    public void rep()
    {
        // accept commands
        fIn = new BufferedReader(new InputStreamReader(System.in));
        while (true)
        {
            System.out.print("ok> ");
            try
            {
                // get command
                final String line = fIn.readLine();
                if (line == null || line.equals("exit") || line.equals("quit"))
                {
                    return;
                }
                                
                // execute command in context of currently selected user
                long startTime = System.nanoTime();
                System.out.print(interpretCommand(line));
                System.out.println("" + (System.nanoTime() - startTime)/1000000f + "ms");
            }
            catch (Exception e)
            {
                e.printStackTrace(System.err);
                System.out.println("");
            }
        }
    }
    
    /**
     * Interpret a single command using the BufferedReader passed in for any data needed.
     * 
     * @param line The unparsed command
     * @return The textual output of the command.
     */
    protected String interpretCommand(final String line)
        throws IOException
    {
        return executeCommand(line);
    }
    
    /**
     * Execute a single command using the BufferedReader passed in for any data needed.
     * 
     * TODO: Use decent parser!
     * 
     * @param line The unparsed command
     * @return The textual output of the command.
     */
    protected String executeCommand(String line)
        throws IOException
    {
        String[] command = line.split(" ");
        if (command.length == 0)
        {
            command = new String[1];
            command[0] = line;
        }
        
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bout);

        // repeat last command?
        if (command[0].equals("r"))
        {
            if (lastCommand == null)
            {
                return "No command entered yet.";
            }
            
            if (command.length > 1 && command[1].equals("show"))
            {
                return lastCommand + "\n\n";
            }
            else
            {
                return "repeating command " + lastCommand + "\n\n" + interpretCommand(lastCommand);
            }
        }
        
        // remember last command
        lastCommand = line;

        // execute command
        if (command[0].equals("help"))
        {
            String helpFile = m_messages.getMessage("testserver.help", null, null);
            ClassPathResource helpResource = new ClassPathResource(helpFile);
            byte[] helpBytes = new byte[500];
            InputStream helpStream = helpResource.getInputStream();
            try
            {
                int read = helpStream.read(helpBytes);
                while (read != -1)
                {
                    bout.write(helpBytes, 0, read);
                    read = helpStream.read(helpBytes);
                }
            }
            finally
            {
                helpStream.close();
            }
        }
        
        else if (command[0].equals("user"))
        {
            if (command.length == 2)
            {
                username = command[1];
            }
            out.println("using user " + username);
        }
        
        else if (command[0].equals("get") ||
                 command[0].equals("delete"))
        {
            String uri = (command.length > 1) ? command[1] : null;
            Response res = submitRequest(command[0], uri, headers, null, null, null);
            bout.write(("Response status: " + res.getStatus()).getBytes());
            out.println();
            bout.write(res.getContentAsByteArray());
            out.println();
        }

        else if (command[0].equals("post"))
        {
            String uri = (command.length > 1) ? command[1] : null;
            String contentType = (command.length > 2) ? command[2] : null;
            String body = "";
            for (int i = 3; i < command.length; i++)
            {
                body += command[i] + " ";
            }
            Response res = submitRequest(command[0], uri, headers, body.getBytes(), null, contentType);
            bout.write(("Response status: " + res.getStatus()).getBytes());
            out.println();
            bout.write(res.getContentAsByteArray());
            out.println();
        }

        else if (command[0].equals("put"))
        {
            String uri = (command.length > 1) ? command[1] : null;
            String contentType = (command.length > 2) ? command[2] : null;
            String body = "";
            for (int i = 3; i < command.length; i++)
            {
                body += command[i] + " ";
            }
            Response res = submitRequest(command[0], uri, headers, body.getBytes(), null, contentType);
            bout.write(("Response status: " + res.getStatus()).getBytes());
            out.println();
            bout.write(res.getContentAsByteArray());
            out.println();
        }
        
        else if (command[0].equals("tunnel"))
        {
            if (command.length < 4)
            {
                return "Syntax Error.\n";
            }
            
            if (command[1].equals("param"))
            {
                String uri = command[3];
                if (uri.indexOf('?') == -1)
                {
                    uri += "?alf:method=" + command[2];
                }
                else
                {
                    uri += "&alf:method=" + command[2];
                }
                Response res = submitRequest("post", uri, headers, null, null, null);
                bout.write(res.getContentAsByteArray());
                out.println();
            }
            
            else if (command[1].equals("header"))
            {
                Map<String, String> tunnelheaders = new HashMap<String, String>();
                tunnelheaders.putAll(headers);
                tunnelheaders.put("X-HTTP-Method-Override", command[2]);
                Response res = submitRequest("post", command[3], tunnelheaders, null, null, null);
                bout.write(res.getContentAsByteArray());
                out.println();
            }
                
            else
            {
                return "Syntax Error.\n";
            }
        }

        else if (command[0].equals("header"))
        {
            if (command.length == 1)
            {
                for (Map.Entry<String, String> entry : headers.entrySet())
                {
                    out.println(entry.getKey() + " = " + entry.getValue());
                }
            }
            else if (command.length == 2)
            {
                String[] param = command[1].split("=");
                if (param.length == 0)
                {
                    return "Syntax Error.\n";
                }
                if (param.length == 1)
                {
                    headers.remove(param[0]);
                    out.println("deleted header " + param[0]);
                }
                else if (param.length == 2)
                {
                    headers.put(param[0], param[1]);
                    out.println("set header " + param[0] + " = " + headers.get(param[0]));
                }
                else
                {
                    return "Syntax Error.\n";
                }
            }
            else
            {
                return "Syntax Error.\n";
            }
        }            

        else if (command[0].equals("reset"))
        {
            container.reset();
            out.println("Runtime context '" + container.getName() + "' reset.");
        }
        
        else
        {
            return "Syntax Error.\n";
        }
 
        out.flush();
        String retVal = new String(bout.toByteArray());
        out.close();
        return retVal;
    }

    
    /**
     * A Web Script Test Request
     */
    public static class Request
    {
        private String method;
        private String uri;
        private Map<String, String> args;
        private Map<String, String> headers;
        private byte[] body;
        private String encoding = "UTF-8";
        private String contentType;
        
        public Request(Request req)
        {
            this.method = req.method;
            this.uri= req.uri;
            this.args = req.args;
            this.headers = req.headers;
            this.body = req.body;
            this.encoding = req.encoding;
            this.contentType = req.contentType;
        }
        
        public Request(String method, String uri)
        {
            this.method = method;
            this.uri = uri;
        }
        
        public String getMethod()
        {
            return method;
        }
        
        public String getUri()
        {
            return uri;
        }
        
        public String getFullUri()
        {
            // calculate full uri
            String fullUri = uri == null ? "" : uri;
            if (args != null && args.size() > 0)
            {
                char prefix = (uri.indexOf('?') == -1) ? '?' : '&';
                for (Map.Entry<String, String> arg : args.entrySet())
                {
                    fullUri += prefix + arg.getKey() + "=" + (arg.getValue() == null ? "" : arg.getValue());
                    prefix = '&';
                }
            }
            
            return fullUri;
        }
        
        public Request setArgs(Map<String, String> args)
        {
            this.args = args;
            return this;
        }
        
        public Map<String, String> getArgs()
        {
            return args;
        }

        public Request setHeaders(Map<String, String> headers)
        {
            this.headers = headers;
            return this;
        }
        
        public Map<String, String> getHeaders()
        {
            return headers;
        }
        
        public Request setBody(byte[] body)
        {
        	this.body = body;
            return this;
        }
        
        public byte[] getBody()
        {
            return body;
        }
        
        public Request setEncoding(String encoding)
        {
            this.encoding = encoding;
            return this;
        }
        
        public String getEncoding()
        {
            return encoding;
        }

        public Request setType(String contentType)
        {
            this.contentType = contentType;
            return this;
        }
        
        public String getType()
        {
            return contentType;
        }
    }
    
    /**
     * Test GET Request
     */
    public static class GetRequest extends Request
    {
        public GetRequest(String uri)
        {
            super("get", uri);
        }
    }

    /**
     * Test POST Request
     */
    public static class PostRequest extends Request
    {
        public PostRequest(String uri, String post, String contentType)
            throws UnsupportedEncodingException 
        {
            super("post", uri);
            setBody(getEncoding() == null ? post.getBytes() : post.getBytes(getEncoding()));
            setType(contentType);
        }

        public PostRequest(String uri, byte[] post, String contentType)
        {
            super("post", uri);
            setBody(post);
            setType(contentType);
        }
    }

    /**
     * Test PUT Request
     */
    public static class PutRequest extends Request
    {
        public PutRequest(String uri, String put, String contentType)
            throws UnsupportedEncodingException
        {
            super("put", uri);
            setBody(getEncoding() == null ? put.getBytes() : put.getBytes(getEncoding()));
            setType(contentType);
        }
        
        public PutRequest(String uri, byte[] put, String contentType)
        {
            super("put", uri);
            setBody(put);
            setType(contentType);
        }
    }

    /**
     * Test DELETE Request
     */
    public static class DeleteRequest extends Request
    {
        public DeleteRequest(String uri)
        {
            super("delete", uri);
        }
    }

    /**
     * Test PATCH Request
     */
    public static class PatchRequest extends Request
    {
        public PatchRequest(String uri, String put, String contentType)
            throws UnsupportedEncodingException
        {
            super("patch", uri);
            setBody(getEncoding() == null ? put.getBytes() : put.getBytes(getEncoding()));
            setType(contentType);
        }
        
        public PatchRequest(String uri, byte[] put, String contentType)
        {
            super("patch", uri);
            setBody(put);
            setType(contentType);
        }
    }
    
    /**
     * A Web Script Test Response
     */
    public interface Response
    {
        public byte[] getContentAsByteArray();
        
        public String getContentAsString()
            throws UnsupportedEncodingException;
        
        public String getHeader(String name);
        
        public String getContentType();
        
        public int getContentLength();
        
        public int getStatus();
    }
    
    /**
     * Test Response wrapping a MockHttpServletResponse
     */
    public static class MockHttpServletResponseResponse
        implements Response
    {
        private MockHttpServletResponse res;
        
        public MockHttpServletResponseResponse(MockHttpServletResponse res)
        {
            this.res = res;
        }

        public byte[] getContentAsByteArray()
        {
            return res.getContentAsByteArray();
        }

        public String getContentAsString()
            throws UnsupportedEncodingException
        {
            return res.getContentAsString();
        }

        public String getHeader(String name)
        {
            return (String)res.getHeader(name);
        }
        
        public String getContentType()
        {
            return res.getContentType();
        }

        public int getContentLength()
        {
            return res.getContentLength();
        }

        public int getStatus()
        {
            return res.getStatus();
        }
    }

}
