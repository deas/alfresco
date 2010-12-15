using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.IO;
using System.Runtime.InteropServices;
using System.Security.Permissions;
using System.Text;
using System.Windows.Forms;
using Microsoft.VisualStudio.Tools.Applications.Runtime;
using Word = Microsoft.Office.Interop.Word;
using Office = Microsoft.Office.Core;

namespace AlfrescoWord2007
{
   [PermissionSet(SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public partial class AlfrescoPane : UserControl
   {
      private Word.Application m_WordApplication;
      private ServerDetails m_ServerDetails;
      private string m_TemplateRoot = "";
      private bool m_ShowPaneOnActivate = false;
      private bool m_ManuallyHidden = false;

      public Word.Application WordApplication
      {
         set
         {
            m_WordApplication = value;
         }
      }

      public string DefaultTemplate
      {
         set
         {
            m_TemplateRoot = value;
         }
      }

      public ServerDetails CurrentServer
      {
         get
         {
            return m_ServerDetails;
         }
      }

      public AlfrescoPane()
      {
         InitializeComponent();

         m_ServerDetails = new ServerDetails();
         LoadSettings();
      }

      public void OnDocumentChanged()
      {
         try
         {
            if ((m_WordApplication.ActiveDocument != null) && (m_ServerDetails.getAuthenticationTicket(false) != ""))
            {
               m_ServerDetails.DocumentPath = m_WordApplication.ActiveDocument.FullName;
               this.showDocumentDetails(m_ServerDetails.DocumentPath);
            }
            else
            {
               m_ServerDetails.DocumentPath = "";
               this.showHome(false);
            }
            if (!m_ManuallyHidden)
            {
               this.Show();
            }
         }
         catch
         {
         }
      }

      public void OnDocumentBeforeClose()
      {
         m_ServerDetails.DocumentPath = "";
         this.showHome(true);
      }

      public void showHome(bool isClosing)
      {
         // Do we have a valid web server address?
         if (m_ServerDetails.WebClientURL == "")
         {
            // No - show the configuration UI
            PanelMode = PanelModes.Configuration;
         }
         else
         {
            // Yes - navigate to the home template
            string theURI = string.Format(@"{0}{1}myAlfresco?p=&e=doc", m_ServerDetails.WebClientURL, m_TemplateRoot);
            // We don't prompt the user if the document is closing
            string strAuthTicket = m_ServerDetails.getAuthenticationTicket(!isClosing);
            if (strAuthTicket != "")
            {
               theURI += "&ticket=" + strAuthTicket;
            }
            if (!isClosing || (strAuthTicket != ""))
            {
               webBrowser.ObjectForScripting = this;
               UriBuilder uriBuilder = new UriBuilder(theURI);
               webBrowser.Navigate(uriBuilder.Uri.AbsoluteUri);
               PanelMode = PanelModes.WebBrowser;
            }
         }
      }

      public void showDocumentDetails(string relativePath)
      {
         // Do we have a valid web server address?
         if (m_ServerDetails.WebClientURL == "")
         {
            // No - show the configuration UI
            PanelMode = PanelModes.Configuration;
         }
         else
         {
            if (relativePath.Length > 0)
            {
               if (!relativePath.StartsWith("/"))
               {
                  relativePath = "/" + relativePath;
               }
               // Strip off any additional parameters
               int paramPos = relativePath.IndexOf("?");
               if (paramPos != -1)
               {
                  relativePath = relativePath.Substring(0, paramPos);
               }
            }
            string theURI = string.Format(@"{0}{1}documentDetails?p={2}&e=doc", m_ServerDetails.WebClientURL, m_TemplateRoot, relativePath);
            string strAuthTicket = m_ServerDetails.getAuthenticationTicket(true);
            if (strAuthTicket != "")
            {
               theURI += "&ticket=" + strAuthTicket;
            }
            webBrowser.ObjectForScripting = this;
            UriBuilder uriBuilder = new UriBuilder(theURI);
            webBrowser.Navigate(uriBuilder.Uri.AbsoluteUri);
            PanelMode = PanelModes.WebBrowser;
         }
      }

      public void openDocument(string documentPath)
      {
         object missingValue = Type.Missing;
         // WebDAV or CIFS?
         string strFullPath = m_ServerDetails.getFullPath(documentPath, "");
         object file = strFullPath;
         try
         {
            Word.Document doc = m_WordApplication.Documents.Open(
               ref file, ref missingValue, ref missingValue, ref missingValue, ref missingValue, ref missingValue,
               ref missingValue, ref missingValue, ref missingValue, ref missingValue,
               ref missingValue, ref missingValue, ref missingValue, ref missingValue, ref missingValue, ref missingValue);

         }
         catch (Exception e)
         {
            MessageBox.Show("Unable to open the document from Alfresco: " + e.Message, "Alfresco Problem", MessageBoxButtons.OK, MessageBoxIcon.Error);
         }
      }

      public void compareDocument(string relativeURL)
      {
         object missingValue = Type.Missing;

         if (relativeURL.StartsWith("/"))
         {
            relativeURL = relativeURL.Substring(1);
         }

         m_WordApplication.ActiveDocument.Compare(
            m_ServerDetails.WebClientURL + relativeURL, ref missingValue, ref missingValue, ref missingValue, ref missingValue,
            ref missingValue, ref missingValue, ref missingValue);
      }

      public void insertDocument(string relativePath)
      {
         object missingValue = Type.Missing;
         object trueValue = true;
         object falseValue = false;

         // Create a new document if no document currently open
         if (m_WordApplication.Selection == null)
         {
            m_WordApplication.Documents.Add(ref missingValue, ref missingValue, ref missingValue, ref missingValue);
         }

         object range = m_WordApplication.Selection.Range;

         // WebDAV or CIFS?
         string strFullPath = m_ServerDetails.getFullPath(relativePath, m_WordApplication.ActiveDocument.FullName);
         string strExtn = Path.GetExtension(relativePath).ToLower();

         if (".bmp .gif .jpg .jpeg .png".IndexOf(strExtn) != -1)
         {
            m_WordApplication.ActiveDocument.InlineShapes.AddPicture(strFullPath, ref falseValue, ref trueValue, ref range);
         }
         else if (".doc".IndexOf(strExtn) != -1)
         {
            m_WordApplication.Selection.InsertFile(strFullPath, ref missingValue, ref trueValue, ref missingValue, ref missingValue);
         }
         else
         {
            object filename = strFullPath;
            object iconFilename = Type.Missing;
            object iconIndex = Type.Missing;
            object iconLabel = Path.GetFileName(strFullPath);
            string defaultIcon = Util.DefaultIcon(Path.GetExtension(strFullPath));
            if (defaultIcon.Contains(","))
            {
               string[] iconData = defaultIcon.Split(new char[] { ',' });
               iconFilename = iconData[0];
               iconIndex = iconData[1];
            }
            m_WordApplication.ActiveDocument.InlineShapes.AddOLEObject(ref missingValue, ref filename, ref falseValue, ref trueValue,
               ref iconFilename, ref iconIndex, ref iconLabel, ref range);
         }
      }

      public bool docHasExtension()
      {
         return (m_WordApplication.ActiveDocument.Name.EndsWith(".doc"));
      }

      public void saveToAlfresco(string documentPath)
      {
         saveToAlfrescoAs(documentPath, m_WordApplication.ActiveDocument.Name);
      }

      public void saveToAlfrescoAs(string relativeDirectory, string documentName)
      {
         object missingValue = Type.Missing;

         string currentDocPath = m_WordApplication.ActiveDocument.FullName;
         // Ensure last separator is present
         if (!relativeDirectory.EndsWith("/"))
         {
            relativeDirectory += "/";
         }

         // Have the correct file extension already?
         if (!documentName.EndsWith(".doc"))
         {
            documentName += ".doc";
         }
         // Add the Word filename
         relativeDirectory += documentName;

         // CIFS or WebDAV path?
         string savePath = m_ServerDetails.getFullPath(relativeDirectory, currentDocPath);

         // Box into object - Word requirement
         object file = savePath;
         try
         {
            m_WordApplication.ActiveDocument.SaveAs(
               ref file, ref missingValue, ref missingValue, ref missingValue, ref missingValue, ref missingValue,
               ref missingValue, ref missingValue, ref missingValue, ref missingValue,
               ref missingValue, ref missingValue, ref missingValue, ref missingValue, ref missingValue, ref missingValue);

            this.OnDocumentChanged();
         }
         catch (Exception e)
         {
            MessageBox.Show("Unable to save the document to Alfresco: " + e.Message, "Alfresco Problem", MessageBoxButtons.OK, MessageBoxIcon.Error);
         }
      }

      private enum PanelModes
      {
         WebBrowser,
         Configuration
      }

      private PanelModes PanelMode
      {
         set
         {
            pnlWebBrowser.Visible = (value == PanelModes.WebBrowser);
            pnlConfiguration.Visible = (value == PanelModes.Configuration);
         }
      }

      #region Settings Management
      /// <summary>
      /// Settings Management
      /// </summary>
      private bool m_SettingsChanged = false;

      private void LoadSettings()
      {
         m_ServerDetails.LoadFromRegistry();
         txtWebClientURL.Text = m_ServerDetails.WebClientURL;
         txtWebDAVURL.Text = m_ServerDetails.WebDAVURL;
         txtCIFSServer.Text = m_ServerDetails.CIFSServer;
         if (m_ServerDetails.Username != "")
         {
            txtUsername.Text = m_ServerDetails.Username;
            txtPassword.Text = m_ServerDetails.Password;
            chkRememberAuth.Checked = true;
         }
         else
         {
            txtUsername.Text = "";
            txtPassword.Text = "";
            chkRememberAuth.Checked = false;
         }
         m_SettingsChanged = false;
      }

      private void btnDetailsOK_Click(object sender, EventArgs e)
      {
         m_ServerDetails.WebClientURL = txtWebClientURL.Text;
         m_ServerDetails.WebDAVURL = txtWebDAVURL.Text;
         m_ServerDetails.CIFSServer = txtCIFSServer.Text;
         if (chkRememberAuth.Checked)
         {
            m_ServerDetails.Username = txtUsername.Text;
            m_ServerDetails.Password = txtPassword.Text;
         }
         else
         {
            m_ServerDetails.Username = "";
            m_ServerDetails.Password = "";
         }

         m_ServerDetails.SaveToRegistry();

         this.OnDocumentChanged();
      }

      private void btnDetailsCancel_Click(object sender, EventArgs e)
      {
         LoadSettings();
      }

      private void txtWebClientURL_TextChanged(object sender, EventArgs e)
      {
         m_SettingsChanged = true;

         // Build autocomplete string for the WebDAV textbox
         try
         {
            string strWebDAV = txtWebClientURL.Text;
            if (!strWebDAV.EndsWith("/"))
            {
               strWebDAV += "/";
            }
            strWebDAV += "webdav/";
            txtWebDAVURL.AutoCompleteCustomSource.Clear();
            txtWebDAVURL.AutoCompleteCustomSource.Add(strWebDAV);
         }
         catch
         {
         }

         // Build autocomplete string for the CIFS textbox
         try
         {
            Uri clientUri = new Uri(txtWebClientURL.Text);
            string strCIFS = "\\\\" + clientUri.Host + "_a\\alfresco\\";
            txtCIFSServer.AutoCompleteCustomSource.Clear();
            txtCIFSServer.AutoCompleteCustomSource.Add(strCIFS);
         }
         catch
         {
         }
      }

      private void txtWebDAVURL_TextChanged(object sender, EventArgs e)
      {
         m_SettingsChanged = true;
      }

      private void txtCIFSServer_TextChanged(object sender, EventArgs e)
      {
         m_SettingsChanged = true;
      }

      private void txtUsername_TextChanged(object sender, EventArgs e)
      {
         m_SettingsChanged = true;
      }

      private void txtPassword_TextChanged(object sender, EventArgs e)
      {
         m_SettingsChanged = true;
      }

      private void lnkBackToBrowser_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
      {
         PanelMode = PanelModes.WebBrowser;
      }
      private void lnkShowConfiguration_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
      {
         PanelMode = PanelModes.Configuration;
      }
      #endregion

      private void webBrowser_Navigated(object sender, WebBrowserNavigatedEventArgs e)
      {
         if (webBrowser.Url.ToString().EndsWith("login.jsp"))
         {
            m_ServerDetails.clearAuthenticationTicket();
            showHome(false);
         }
      }
   }
}
