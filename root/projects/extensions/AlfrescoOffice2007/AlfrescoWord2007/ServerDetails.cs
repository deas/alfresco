using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using System.Security.Cryptography;
using System.Text;
using System.Windows.Forms;
using Microsoft.Win32;

namespace AlfrescoWord2007
{
   public class ServerDetails
   {
      private const string HKCU_APP = @"Software\Alfresco\Office2007";
      private const string REG_WEBCLIENTURL = "WebClientURL";
      private const string REG_WEBDAVURL = "WebDAVURL";
      private const string REG_CIFSSERVER = "CIFSServer";
      private const string REG_USERNAME = "Username";
      private const string REG_PASSWORD = "Password";

      // Persisted settings
      private string m_ServerName = "";
      private string m_WebClientURL = "";
      private string m_WebDAVURL = "";
      private string m_CIFSServer = "";
      public string Username = "";
      public string Password = "";
      // Temporary/runtime-only settings
      private string m_AuthenticationTicket = "";
      private string m_DocumentPhysicalPath = "";
      private string m_DocumentAlfrescoPath = "";

      private byte[] m_IV = new byte[8];
      private byte[] m_Key = new byte[8];

      public ServerDetails()
      {
         AssemblyCompanyAttribute assemblyCompany = (AssemblyCompanyAttribute)Attribute.GetCustomAttribute(Assembly.GetExecutingAssembly(), typeof(AssemblyCompanyAttribute));
         m_IV = Encoding.ASCII.GetBytes(assemblyCompany.Company.Substring(0, 8));
         for (int i=8; i>0; m_Key[i-1] = (byte)(m_IV[8-(i--)] ^ 0x20));
      }

      public string ServerName
      {
         get
         {
            return m_ServerName;
         }
      }

      public string WebClientURL
      {
         get
         {
            return m_WebClientURL;
         }
         set
         {
            try
            {
               m_WebClientURL = value;
               if (!m_WebClientURL.EndsWith("/"))
               {
                  m_WebClientURL += "/";
               }
               m_AuthenticationTicket = "";
               Uri webClient = new Uri(value);
               m_ServerName = webClient.Host;
            }
            catch
            {
               m_WebClientURL = "";
               m_ServerName = "";
            }
         }
      }

      public string WebDAVURL
      {
         get
         {
            return m_WebDAVURL;
         }
         set
         {
            if (value == "/") value = "";

            m_WebDAVURL = value;
            if ((value != "") && !m_WebDAVURL.EndsWith("/"))
            {
               m_WebDAVURL += "/";
            }
         }
      }

      public string CIFSServer
      {
         get
         {
            return m_CIFSServer;
         }
         set
         {
            if (value == "\\") value = "";

            m_CIFSServer = value;
            if ((value != "") && !m_CIFSServer.EndsWith("\\"))
            {
               m_CIFSServer += "\\";
            }
         }
      }

      private string EncryptedPassword
      {
         get
         {
            return Convert.ToBase64String(Encrypt(Password));
         }
         set
         {
            Password = Decrypt(Convert.FromBase64String(value));
         }
      }

      private byte[] Encrypt(string PlainText)
      {
         // Create a new DES key
         DESCryptoServiceProvider key = new DESCryptoServiceProvider();
         key.IV = m_IV;
         key.Key = m_Key;

         // Create a CryptoStream using a memory stream and the CSP DES key
         MemoryStream ms = new MemoryStream();
         CryptoStream encStream = new CryptoStream(ms, key.CreateEncryptor(), CryptoStreamMode.Write);
         StreamWriter sw = new StreamWriter(encStream);
         sw.WriteLine(PlainText);
         sw.Close();
         encStream.Close();

         // Get an array of bytes that represents the memory stream
         byte[] buffer = ms.ToArray();

         ms.Close();

         // Return the encrypted byte array.
         return buffer;
      }

      private string Decrypt(byte[] CypherText)
      {
         string val = "";
         try
         {
            // Create a new DES key
            DESCryptoServiceProvider key = new DESCryptoServiceProvider();
            key.IV = m_IV;
            key.Key = m_Key;

            // Create a memory stream to the passed buffer
            MemoryStream ms = new MemoryStream(CypherText);

            // Create a CryptoStream using the memory stream and the CSP DES key
            CryptoStream encStream = new CryptoStream(ms, key.CreateDecryptor(), CryptoStreamMode.Read);

            StreamReader sr = new StreamReader(encStream);
            val = sr.ReadLine();

            sr.Close();
            encStream.Close();
            ms.Close();
         }
         catch
         {
            val = "";
         }
         return val;
      }


      public bool LoadFromRegistry()
      {
         bool bResult = true;
         RegistryKey rootKey = Registry.CurrentUser.OpenSubKey(HKCU_APP, true);

         // Have entries in the registry yet?
         if (rootKey == null)
         {
            rootKey = Registry.CurrentUser.CreateSubKey(HKCU_APP);
            bResult = false;
         }

         try
         {
            string serverName = rootKey.GetValue("").ToString();
            using (RegistryKey serverKey = rootKey.OpenSubKey(serverName, true))
            {
               this.WebClientURL = serverKey.GetValue(REG_WEBCLIENTURL).ToString();
               this.WebDAVURL = serverKey.GetValue(REG_WEBDAVURL).ToString();
               this.CIFSServer = serverKey.GetValue(REG_CIFSSERVER).ToString();
               this.Username = serverKey.GetValue(REG_USERNAME).ToString();
               string regPassword = serverKey.GetValue(REG_PASSWORD).ToString();
               try
               {
                  this.EncryptedPassword = regPassword;
               }
               catch
               {
                  // Possible this is an unencrypted password from an early version
                  if (regPassword.Length > 0)
                  {
                     this.Password = regPassword;
                     serverKey.SetValue(REG_PASSWORD, this.EncryptedPassword);
                  }
               }
            }
         }
         catch
         {
            bResult = false;
         }

         m_AuthenticationTicket = "";

         return bResult;
      }

      public void SaveToRegistry()
      {
         RegistryKey rootKey = Registry.CurrentUser.OpenSubKey(HKCU_APP, true);

         if (rootKey == null)
         {
            rootKey = Registry.CurrentUser.CreateSubKey(HKCU_APP);
         }

         try
         {
            rootKey.DeleteSubKey(this.ServerName);
         }
         catch
         {
         }

         RegistryKey serverKey = rootKey.CreateSubKey(this.ServerName);
         serverKey.SetValue(REG_WEBCLIENTURL, this.WebClientURL);
         serverKey.SetValue(REG_WEBDAVURL, this.WebDAVURL);
         serverKey.SetValue(REG_CIFSSERVER, this.CIFSServer);
         serverKey.SetValue(REG_USERNAME, this.Username);
         serverKey.SetValue(REG_PASSWORD, this.EncryptedPassword);
         rootKey.SetValue("", this.ServerName);
      }

      public string DocumentPath
      {
         set
         {
            m_DocumentPhysicalPath = value;
            m_DocumentAlfrescoPath = "";
         }
         get
         {
            if (m_DocumentAlfrescoPath == "")
            {
               IServerHelper serverHelper;

               if (m_DocumentPhysicalPath.StartsWith("http"))
               {
                  // WebDAV path
                  serverHelper = new WebDAVHelper(this.WebDAVURL);
               }
               else
               {
                  // CIFS path
                  serverHelper = new CIFSHelper(this.CIFSServer);
               }

               m_DocumentAlfrescoPath = serverHelper.GetAlfrescoPath(m_DocumentPhysicalPath);
            }
            return m_DocumentAlfrescoPath;
         }
      }

      public string getFullPath(string relativePath, string currentDocPath)
      {
         // CIFS or WebDAV path?
         string fullPath = "";
         // Default to CIFS
         bool usingCIFS = true;

         // Remove leading "/"
         if (relativePath.StartsWith("/"))
         {
            relativePath = relativePath.Substring(1);
         }

         if (MatchWebDAVURL(currentDocPath))
         {
            // Looks like a WebDAV path, so use that
            usingCIFS = false;
         }
         else
         {
            // No match - what config have we been given?
            // Default to CIFS if we've been given a server
            usingCIFS = (CIFSServer != "");
         }

         // Build the path depending on which method
         if (usingCIFS)
         {
            // Use CIFS
            fullPath = CIFSServer + relativePath.Replace("/", "\\");
            fullPath = Uri.UnescapeDataString(fullPath);
         }
         else
         {
            // Use WebDAV
            fullPath = WebDAVURL + relativePath;
            fullPath += "?ticket=" + getAuthenticationTicket(false);
         }

         return fullPath;
      }

      public string getAuthenticationTicket(bool promptUser)
      {
         string strAuthTicket = "";
         if (m_AuthenticationTicket == "")
         {
            // Do we recognise the path as belonging to an Alfresco server?
            if (this.MatchCIFSServer(m_DocumentPhysicalPath))
            {
               // Try CIFS
               IServerHelper myAuthTicket = new CIFSHelper(this.CIFSServer);
               m_AuthenticationTicket = myAuthTicket.GetAuthenticationTicket();
            }
            else
            {
               // Try WebDAV
               IServerHelper myAuthTicket = new WebDAVHelper(this.WebDAVURL);
               strAuthTicket = myAuthTicket.GetAuthenticationTicket();
               if (strAuthTicket != "401")
               {
                  m_AuthenticationTicket = strAuthTicket;
               }
               else
               {
                  // Authentication failed - do we have a saved username/password?
                  if ((this.Username.Length > 0) && (this.Password.Length > 0))
                  {
                     strAuthTicket = myAuthTicket.GetAuthenticationTicket(this.Username, this.Password);
                  }
                  if (strAuthTicket != "401")
                  {
                     m_AuthenticationTicket = strAuthTicket;
                  }
                  else
                  {
                     // Last option - pop up the login form
                     using (Login myLogin = new Login())
                     {
                        bool bRetry = true;

                        // Pre-populate with values already configured
                        myLogin.Username = this.Username;
                        myLogin.Password = this.Password;

                        // Retry loop for typos
                        while (bRetry)
                        {
                           if (myLogin.ShowDialog() == DialogResult.OK)
                           {
                              // Try to authenticate with entered credentials
                              strAuthTicket = myAuthTicket.GetAuthenticationTicket(myLogin.Username, myLogin.Password);
                              if ((strAuthTicket == "401") || (strAuthTicket == ""))
                              {
                                 // Retry?
                                 bRetry = (MessageBox.Show("Couldn't authenticate with Alfresco server.", "Alfresco Authentication", MessageBoxButtons.RetryCancel) == DialogResult.Retry);
                              }
                              else
                              {
                                 // Successful login
                                 m_AuthenticationTicket = strAuthTicket;
                                 bRetry = false;
                              }
                           }
                           else
                           {
                              // Cancel or close chosen on login dialog
                              bRetry = false;
                           }
                        }
                     }
                  }
               }
            }
         }

         return m_AuthenticationTicket;
      }

      public void clearAuthenticationTicket()
      {
         m_AuthenticationTicket = "";
      }

      public bool MatchWebDAVURL(string urlToMatch)
      {
         bool bMatch = false;
         if (this.WebDAVURL.Length > 0)
         {
            bMatch = urlToMatch.ToLower().IndexOf(this.WebDAVURL.ToLower()) == 0;
         }
         return bMatch;
      }

      public bool MatchCIFSServer(string serverToMatch)
      {
         bool bMatch = false;
         if (this.CIFSServer.Length > 0)
         {
            bMatch = serverToMatch.ToLower().IndexOf(this.CIFSServer.ToLower()) == 0;
         }
         return bMatch;
      }
   }
}
