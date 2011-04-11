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
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using System.Security.Cryptography;
using System.Text;
using System.Windows.Forms;
using Microsoft.Win32;

namespace AlfrescoExcel2003
{
   public class ServerDetails
   {
      private const string HKCU_APP = @"Software\Alfresco\Office2003";
      private const string HKCU_APP_LEGACY = @"Software\Alfresco\Excel2003";
      private const string REG_WINDOWTOP = "ExcelWindowTop";
      private const string REG_WINDOWLEFT = "ExcelWindowLeft";
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

      // Normally we default to CIFS if it's been configured, but this gets cleared if the user had
      // to manually log-in, as it means the CIFS interface couldn't do it automatically
      private bool m_DefaultToCIFS = true;

      public ServerDetails()
      {
         AssemblyCompanyAttribute assemblyCompany = (AssemblyCompanyAttribute)Attribute.GetCustomAttribute(Assembly.GetExecutingAssembly(), typeof(AssemblyCompanyAttribute));
         m_IV = Encoding.ASCII.GetBytes(assemblyCompany.Company.PadRight(8, '*').Substring(0, 8));
         for (int i = 8; i > 0; m_Key[i - 1] = (byte)(m_IV[8 - (i--)] ^ 0x20));
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
         bool copyLegacyEntries = false;
         RegistryKey rootKey = Registry.CurrentUser.OpenSubKey(HKCU_APP, true);

         // Have entries in the registry yet?
         if (rootKey == null)
         {
            // Do we have any legacy entries?
            RegistryKey rootKeyLegacy = Registry.CurrentUser.OpenSubKey(HKCU_APP_LEGACY, false);
            if (rootKeyLegacy != null)
            {
               try
               {
                  string serverNameLegacy = rootKeyLegacy.GetValue("").ToString();
                  string webClientURLLegacy = rootKeyLegacy.OpenSubKey(serverNameLegacy, false).GetValue(REG_WEBCLIENTURL).ToString();
                  if (webClientURLLegacy.Length > 0)
                  {
                     // Got here ok, so let's copy the old entries
                     copyLegacyEntries = true;
                  }
               }
               catch
               {
               }
            }
            if (copyLegacyEntries)
            {
               rootKey = rootKeyLegacy;
            }
            else
            {
               rootKey = Registry.CurrentUser.CreateSubKey(HKCU_APP);
               bResult = false;
            }
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

            if (copyLegacyEntries)
            {
               SaveToRegistry();
            }

            // Try to get rid of legacy entries
            try
            {
               Registry.CurrentUser.DeleteSubKeyTree(HKCU_APP_LEGACY);
            }
            catch
            {
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

         m_AuthenticationTicket = "";
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
               IServerHelper serverHelper = null;

               if (m_DocumentPhysicalPath.StartsWith("http"))
               {
                  // WebDAV path
                  if (this.WebDAVURL.Length > 0)
                  {
                     serverHelper = new WebDAVHelper(this.WebDAVURL);
                  }
               }
               else
               {
                  // CIFS path
                  if (this.CIFSServer.Length > 0)
                  {
                     serverHelper = new CIFSHelper(this.CIFSServer);
                  }
               }

               if (serverHelper != null)
               {
                  m_DocumentAlfrescoPath = serverHelper.GetAlfrescoPath(m_DocumentPhysicalPath);
               }
            }
            return m_DocumentAlfrescoPath;
         }
      }

      public string getFullPath(string relativePath, string currentDocPath, bool omitTicket)
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
            // Default to CIFS if we've been given a server, unless manual log-in earlier
            usingCIFS = (CIFSServer != "") && m_DefaultToCIFS;
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
            string strAuthTicket = getAuthenticationTicket(false);
            fullPath = WebDAVURL + relativePath;
            /* Work out if the ticket can be added or not */
            string ticket = "?ticket=" + Uri.EscapeDataString(strAuthTicket);
            if (!omitTicket && strAuthTicket != "" && strAuthTicket != "ntlm" && (fullPath.Length + ticket.Length) < 255)
            {
               // OK to add ticket onto document path
               fullPath += ticket;
            }
         }

         return fullPath;
      }

      public string getAuthenticationTicket(bool promptUser)
      {
         string strAuthTicket = "";
         if (m_AuthenticationTicket == "")
         {
            // If we've been given a CIFS server then try to authenticate against it
            if (this.CIFSServer.Length > 0)
            {
               // Try CIFS
               IServerHelper myAuthTicket = new CIFSHelper(this.CIFSServer);
               m_AuthenticationTicket = myAuthTicket.GetAuthenticationTicket();
            }

            // Did we get a ticket from the CIFS server?
            if (m_AuthenticationTicket != "")
            {
               m_DefaultToCIFS = true;
            }
            else
            {
               // Try WebDAV
               /* Only reset flag if no CIFS config present */
               m_DefaultToCIFS = (this.CIFSServer.Length > 0);
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
                  // Check whether 'Negotiate' authentication is required and try to log-in with the Default System Credentials (to cover SSO case)
                  else if (EAuthenticationType.NEGOTIATE == myAuthTicket.GetAuthenticationType())
                  {
                      strAuthTicket = myAuthTicket.GetAuthenticationTicket("negotiator", this.Password);
                  }
                  if (strAuthTicket != "401")
                  {
                     m_AuthenticationTicket = strAuthTicket;
                  }
                  else if (promptUser)
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
                                 bRetry = (MessageBox.Show(Properties.Resources.UnableToAuthenticate, Properties.Resources.MessageBoxTitle, MessageBoxButtons.RetryCancel) == DialogResult.Retry);
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

      public void loadWindowPosition(Form theForm)
      {
         int windowTop = -1;
         int windowLeft = -1;

         RegistryKey rootKey = Registry.CurrentUser.OpenSubKey(HKCU_APP, false);
         try
         {
            windowTop = Convert.ToInt32(rootKey.GetValue(REG_WINDOWTOP, -1));
            windowLeft = Convert.ToInt32(rootKey.GetValue(REG_WINDOWLEFT, -1));
         }
         catch
         {
         }

         if ((windowTop != -1) && (windowLeft != -1))
         {
            // Check window is not completely off screen
            bool bPositionOK = false;
            foreach (Screen screen in Screen.AllScreens)
            {
               if (screen.WorkingArea.Contains(windowLeft, windowTop))
               {
                  bPositionOK = true;
                  break;
               }
            }
            // If the top-left corner is in a screen rect, then ok to position the form
            if (bPositionOK)
            {
               theForm.Top = windowTop;
               theForm.Left = windowLeft;
            }
         }
      }

      public void saveWindowPosition(Form theForm)
      {
         RegistryKey rootKey = Registry.CurrentUser.OpenSubKey(HKCU_APP, true);

         try
         {
            rootKey.SetValue(REG_WINDOWTOP, theForm.Top, RegistryValueKind.DWord);
            rootKey.SetValue(REG_WINDOWLEFT, theForm.Left, RegistryValueKind.DWord);
         }
         catch
         {
         }
      }
   }
}
