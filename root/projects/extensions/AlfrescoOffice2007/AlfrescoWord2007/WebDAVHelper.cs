using System;
using System.IO;
using System.Net;
using System.Text;
using System.Xml;
using System.Xml.XPath;

namespace AlfrescoWord2007
{
   internal class WebDAVHelper : IServerHelper
   {
      private string m_AlfrescoServer = "";
      private string m_WebAuthenticationHeader = "";

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
         if (xmlResponse.InnerXml.Contains("(401) Unauth"))
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
            webRequest.Method = "PROPFIND";
            webRequest.Timeout = 10000;
            webRequest.Headers.Add("Translate", "f");
            webRequest.Headers.Add("Depth", "0");
            webRequest.CookieContainer = new CookieContainer(1);

            // Credentials
            if (username.Length > 0)
            {
               NetworkCredential myCred = new NetworkCredential(username, password);
               CredentialCache myCache = new CredentialCache();
               if (m_WebAuthenticationHeader.ToLower().StartsWith("basic"))
               {
                  myCache.Add(new Uri(url), "BASIC", myCred);
               }
               else
               {
                  myCache.Add(new Uri(url), "DIGEST", myCred);
               }
               webRequest.Credentials = myCache;
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
               m_WebAuthenticationHeader = e.Response.Headers["WWW-Authenticate"];
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
         }

         return responseStreamXml;
      }
   }
}
