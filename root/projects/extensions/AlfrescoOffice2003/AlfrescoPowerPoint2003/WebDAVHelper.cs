/**
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
using System;
using System.IO;
using System.Net;
using System.Text;
using System.Xml;
using System.Xml.XPath;

namespace AlfrescoPowerPoint2003
{
   internal class WebDAVHelper : IServerHelper
   {
      private string m_AlfrescoServer = "";
      private EAuthenticationType m_AuthType = EAuthenticationType.BASIC;

      /// <summary>
      /// WebDAV Constructor
      /// </summary>
      /// <param name="AlfrescoServer">Address of the Alfresco WebDAV server</param>
      public WebDAVHelper(string AlfrescoServer)
      {
         m_AlfrescoServer = AlfrescoServer;
         if (m_AlfrescoServer.EndsWith("/"))
         {
            m_AlfrescoServer = m_AlfrescoServer.Remove(m_AlfrescoServer.Length - 1);
         }
      }

      /// <summary>
      /// IServerHelper interface. Queries the CIFS server at the given UNC path for an authorization ticket
      /// </summary>
      /// <returns>(string) Auth Ticket</returns>
      public string GetAuthenticationTicket()
      {
         return GetAuthenticationTicket("", "");
      }

      public string GetAuthenticationTicket(string Username, string Password)
      {
         string strTicket = "";

         XmlDocument xmlResponse = new XmlDocument();
         xmlResponse.InnerXml = SendWebDAVRequest(m_AlfrescoServer, "", Username, Password);

         // Did we get an HTTP 401 error?
         if (m_AuthType == EAuthenticationType.NTLM)
         {
            return "ntlm";
         }
         else if (xmlResponse.InnerXml.Contains("(401) Unauth") || (xmlResponse.InnerXml.Contains("<error>")))
         {
            strTicket = "401";
         }
         else
         {
            try
            {
               XmlNamespaceManager xmlNS = new XmlNamespaceManager(new NameTable());
               xmlNS.AddNamespace("D", "DAV:");
               XmlNode xmlTicket = xmlResponse.SelectSingleNode("/D:multistatus/D:response/D:propstat/D:prop/D:authticket", xmlNS);
               strTicket = xmlTicket.InnerText;
            }
            catch
            {
               strTicket = "";
            }
         }

         return strTicket;
      }

      public string GetAlfrescoPath(string documentPath)
      {
         return documentPath.Remove(0, m_AlfrescoServer.Length);
      }

      EAuthenticationType IServerHelper.GetAuthenticationType()
      {
         return m_AuthType;
      }

      private string SendWebDAVRequest(string url, string webdavRequest, string username, string password)
      {
         HttpWebRequest webRequest = null;
         HttpWebResponse webResponse = null;
         Stream responseStream = null;
         string responseStreamXml = "";

         try
         {
            // Create the web request
            webRequest = (HttpWebRequest)WebRequest.Create(url);
            // Configure HTTP headers
            webRequest.ContentType = "text/xml; charset=\"UTF-8\"";
            webRequest.ProtocolVersion = HttpVersion.Version11;
            webRequest.KeepAlive = false;
            webRequest.Method = "PROPFIND";
            webRequest.Timeout = 10000;
            webRequest.Headers.Add("Translate", "f");
            webRequest.Headers.Add("Depth", "0");
            webRequest.CookieContainer = new CookieContainer(1);

            // Credentials
            NetworkCredential myCred = new NetworkCredential(username, password);
            CredentialCache myCache = new CredentialCache();
            if (username.Length > 0)
            {
               if (EAuthenticationType.BASIC == m_AuthType)
               {
                  myCache.Add(new Uri(url), "Basic", myCred);
               }
               else if (EAuthenticationType.NTLM == m_AuthType)
               {
                  return "<ntlm />";
               }
               else if (EAuthenticationType.NEGOTIATE == m_AuthType)
               {
                  myCache.Add(new Uri(url), "Negotiate", (NetworkCredential)CredentialCache.DefaultCredentials);
               }
               else
               {
                  myCache.Add(new Uri(url), "Digest", myCred);
               }
               webRequest.Credentials = myCache;
               webRequest.PreAuthenticate = true;
            }

            // The body of the HTTP request contains the WebDAV request
            byte[] queryData = Encoding.UTF8.GetBytes(webdavRequest);

            // Set the Content Length
            webRequest.ContentLength = queryData.Length;

            // Send the request
            Stream requestStream = webRequest.GetRequestStream();
            requestStream.Write(queryData, 0, queryData.Length);
            requestStream.Close();

            // Get the HTTP Web Response
            webResponse = (HttpWebResponse)webRequest.GetResponse();

            // Get the repsonse stream
            responseStream = webResponse.GetResponseStream();

            // Pipes response stream to a UTF-8 stream reader
            StreamReader readerResponseStream = new StreamReader(responseStream, Encoding.UTF8);

            responseStreamXml = readerResponseStream.ReadToEnd();

            // Release the resources of the response
            readerResponseStream.Close();
         }
         catch (WebException e)
         {
            responseStreamXml = "<error>" + e.Message + "</error>";
            if (e.Message.Contains("401"))
            {
               string authenticationHeader = e.Response.Headers["WWW-Authenticate"];
               authenticationHeader = (null != authenticationHeader) ? (authenticationHeader.ToLower()) : ("");
               if (("" == authenticationHeader) || authenticationHeader.StartsWith("basic"))
               {
                  m_AuthType = EAuthenticationType.BASIC;
               }
               else if (authenticationHeader.StartsWith("ntlm"))
               {
                  m_AuthType = EAuthenticationType.NTLM;
               }
               else if ("negotiate" == authenticationHeader)
               {
                  m_AuthType = EAuthenticationType.NEGOTIATE;
               }
            }
         }
         catch (Exception e)
         {
            responseStreamXml = "<error>WebDAV error from Alfresco: " + e.Message + "</error>";
         }
         finally
         {
            if (webResponse != null)
            {
               webResponse.Close();
            }

            if (responseStream != null)
            {
               responseStream.Close();
            }

            if (webRequest != null)
            {
               webRequest.Abort();
            }
         }

         return responseStreamXml;
      }
   }
}
