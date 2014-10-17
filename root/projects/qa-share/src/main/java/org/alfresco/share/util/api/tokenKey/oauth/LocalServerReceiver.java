/*
 * Copyright (c) 2011 Google Inc.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.alfresco.share.util.api.tokenKey.oauth;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Runs a Jetty server on a free port, waiting for OAuth to redirect to it with the verification
 * code.
 */
public final class LocalServerReceiver implements VerificationCodeReceiver
{

    private final String CALLBACK_PATH = "/oauthsample/mycallback.html";
    private static final String LOCALHOST = "localhost";
    private static final int PORT = 8181;

    /** Server or {@code null} before {@link #getRedirectUri()}. */
    public static Server server;

    /** Verification code or {@code null} before received. */
    volatile String code;

    @Override
    public String getRedirectUri() throws Exception
    {

        return "http://" + LOCALHOST + ":" + PORT + CALLBACK_PATH;
    }

    private boolean isJettyStarted(int port)
    {
        boolean isPortUsed = true;
        Socket s = null;
        try
        {
            s = new Socket("localhost", port);

            // If the code makes it this far without an exception it means
            // something is using the port and has responded.
            isPortUsed = true;
            try
            {
                Thread.sleep(5000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        catch (IOException e)
        {
            isPortUsed = false;
        }
        finally
        {
            if (s != null)
            {
                try
                {
                    s.close();
                }
                catch (IOException e)
                {
                    throw new RuntimeException("You should handle this error.", e);
                }
            }
        }
        return isPortUsed;

    }

    @Override
    public void startJetty()
    {
        if (!isJettyStarted(PORT))
        {
            server = new Server(PORT);
            for (Connector c : server.getConnectors())
            {
                c.setHost(LOCALHOST);
            }
            server.addHandler(new CallbackHandler());
            try
            {
                server.start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() throws Exception
    {
        if (server != null)
        {
            server.stop();
            server = null;
        }
    }

    /**
     * Jetty handler that takes the verifier token passed over from the OAuth provider and stashes it
     * will find it.
     */
    class CallbackHandler extends AbstractHandler
    {

        @Override
        public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException
        {
            if (!CALLBACK_PATH.equals(target))
            {
                return;
            }
            writeLandingHtml(response);
            response.flushBuffer();
            ((Request) request).setHandled(true);
            String error = request.getParameter("error");
            if (error != null)
            {
                System.out.println("Authorization failed. Error=" + error);
                System.out.println("Quitting.");
                System.exit(1);
            }
            code = request.getParameter("code");
            synchronized (LocalServerReceiver.this)
            {
                LocalServerReceiver.this.notify();
            }
        }

        private void writeLandingHtml(HttpServletResponse response) throws IOException
        {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html");

            PrintWriter doc = response.getWriter();
            doc.println("<html>");
            doc.println("<head><title>OAuth 2.0 Authentication Token Recieved</title></head>");
            doc.println("<body>");
            doc.println("Received verification code. Closing...");
            doc.println("<script type='text/javascript'>");
            // We open "" in the same window to trigger JS ownership of it, which lets
            // us then close it via JS, at least in Chrome.
            doc.println("window.setTimeout(function() {");
            doc.println("    window.open('', '_self', ''); window.close(); }, 1000);");
            doc.println("if (window.opener) { window.opener.checkToken(); }");
            doc.println("</script>");
            doc.println("</body>");
            doc.println("</HTML>");
            doc.flush();
        }
    }
}
