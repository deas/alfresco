using System;
using System.Windows.Forms;
using Microsoft.VisualStudio.Tools.Applications.Runtime;
using Word = Microsoft.Office.Interop.Word;
using Office = Microsoft.Office.Core;

namespace AlfrescoWord2007
{
   public partial class ThisAddIn
   {
      private const int ALFRESCO_PANE_WIDTH = 296;

      // Object references only used in "single window" mode
      private AlfrescoPane m_AlfrescoPane;
      private Microsoft.Office.Tools.CustomTaskPane m_CustomTaskPane;

      private void ThisAddIn_Startup(object sender, System.EventArgs e)
      {
         // Register event interest with the Word Application
         Application.DocumentBeforeClose += new Word.ApplicationEvents4_DocumentBeforeCloseEventHandler(Application_DocumentBeforeClose);
         Application.DocumentChange += new Microsoft.Office.Interop.Word.ApplicationEvents4_DocumentChangeEventHandler(Application_DocumentChange);
      }

      private void ThisAddIn_Shutdown(object sender, System.EventArgs e)
      {
      }

      #region VSTO generated code

      /// <summary>
      /// Required method for Designer support - do not modify
      /// the contents of this method with the code editor.
      /// </summary>
      private void InternalStartup()
      {
         this.Startup += new System.EventHandler(ThisAddIn_Startup);
         this.Shutdown += new System.EventHandler(ThisAddIn_Shutdown);
      }

      #endregion

      /// <summary>
      /// Fired when active document changes
      /// </summary>
      void Application_DocumentChange()
      {
         // Check Word UI mode
         if (this.Application.ShowWindowsInTaskbar)
         {
            // Multiple window mode
            // Update the ribbon button to reflect the new state
            Microsoft.Office.Tools.CustomTaskPane customTaskPane = this.FindActiveTaskPane();
            if (customTaskPane != null)
            {
               m_Ribbon.ToggleAlfrescoState = customTaskPane.Visible;
            }
            else
            {
               m_Ribbon.ToggleAlfrescoState = false;
            }
            // As recommended by http://msdn2.microsoft.com/en-us/library/bb264456.aspx
            RemoveOrphanedTaskPanes();
         }
         else
         {
            // Single window mode
            if (m_AlfrescoPane != null)
            {
               m_AlfrescoPane.OnDocumentChanged();
            }
         }
      }

      /// <summary>
      /// Fired as a document is being closed
      /// </summary>
      /// <param name="Doc"></param>
      /// <param name="Cancel"></param>
      void Application_DocumentBeforeClose(Word.Document Doc, ref bool Cancel)
      {
         // Check Word UI mode
         if (this.Application.ShowWindowsInTaskbar)
         {
            // Multiple window mode
            // No action
         }
         else
         {
            // Single window mode
            if (m_AlfrescoPane != null)
            {
               m_AlfrescoPane.OnDocumentBeforeClose();
            }
         }
      }

      /// <summary>
      /// Fired on the ribbon toggle button "show" event
      /// </summary>
      void Ribbon_OnAlfrescoShow()
      {
         ShowAlfrescoPane();
      }

      /// <summary>
      /// Fired on the ribbon toggle button "hide" event
      /// </summary>
      void Ribbon_OnAlfrescoHide()
      {
         HideAlfrescoPane();
      }

      /// <summary>
      /// Fired when the CustomTaskPane visibility changes
      /// </summary>
      /// <param name="sender"></param>
      /// <param name="e"></param>
      void CustomTaskPane_VisibleChanged(object sender, EventArgs e)
      {
         // Update the ribbon button to reflect the new state
         Microsoft.Office.Tools.CustomTaskPane customTaskPane = (Microsoft.Office.Tools.CustomTaskPane)sender;
         m_Ribbon.ToggleAlfrescoState = customTaskPane.Visible;
      }

      /// <summary>
      /// Show the Alfresco pane
      /// </summary>
      /// <param name="Show"></param>
      private void ShowAlfrescoPane()
      {
         // Try to get active document
         Word.Document activeDoc = null;
         try
         {
            activeDoc = this.Application.ActiveDocument;
         }
         catch
         {
            activeDoc = null;
         }

         // Check Word UI mode
         if (this.Application.ShowWindowsInTaskbar)
         {
            // Multiple window mode
            Microsoft.Office.Tools.CustomTaskPane customTaskPane = this.FindActiveTaskPane();
            if (customTaskPane == null)
            {
               AlfrescoPane alfrescoPane = new AlfrescoPane();
               alfrescoPane.WordApplication = this.Application;
               alfrescoPane.DefaultTemplate = "wcservice/office/";
               customTaskPane = CustomTaskPanes.Add(alfrescoPane, "Alfresco");
               try
               {
                  if (Application.ActiveDocument != null)
                  {
                     alfrescoPane.OnDocumentChanged();
                  }
                  else
                  {
                     alfrescoPane.showHome(false);
                  }
               }
               catch
               {
                  // Almost certainlty as a result of no active document
                  alfrescoPane.showHome(false);
               }
            }
            customTaskPane.Visible = true;
            customTaskPane.Width = ALFRESCO_PANE_WIDTH;
            customTaskPane.VisibleChanged += new EventHandler(CustomTaskPane_VisibleChanged);
         }
         else
         {
            // Single window mode
         }

         /*
         if (m_CustomTaskPane == null)
         {
            m_CustomTaskPane = CustomTaskPanes.Add(m_AlfrescoPane, "Alfresco");
            m_CustomTaskPane.VisibleChanged += new EventHandler(CustomTaskPane_VisibleChanged);
         }

         if (m_AlfrescoPane == null)
         {
            m_AlfrescoPane = new AlfrescoPane();
            m_AlfrescoPane.WordApplication = Application;
            m_AlfrescoPane.DefaultTemplate = "wcservice/office/";
         }

         if (Show)
         {
            AddCustomTaskPane();
            m_CustomTaskPane.Visible = true;
            m_CustomTaskPane.Width = ALFRESCO_PANE_WIDTH;
            m_AlfrescoPane.Show();
            if (Application.ActiveDocument != null)
            {
               m_AlfrescoPane.OnDocumentChanged();
            }
            else
            {
               m_AlfrescoPane.showHome(false);
            }
         }
         */
      }

      private void HideAlfrescoPane()
      {
         // Check Word UI mode
         if (this.Application.ShowWindowsInTaskbar)
         {
            // Multiple window mode
            Microsoft.Office.Tools.CustomTaskPane customTaskPane = this.FindActiveTaskPane();
            if (customTaskPane != null)
            {
               customTaskPane.Visible = false;
            }
         }
         else
         {
            // Single window mode
            if (m_CustomTaskPane != null)
            {
               m_CustomTaskPane.Visible = false;
            }
         }
      }

      private Microsoft.Office.Tools.CustomTaskPane FindActiveTaskPane()
      {
         try
         {
            // Check if this window already has an AlfrescoPane
            if (CustomTaskPanes.Count > 0)
            {
               foreach (Microsoft.Office.Tools.CustomTaskPane ctp in CustomTaskPanes)
               {
                  try
                  {
                     if (ctp.Window == this.Application.ActiveWindow)
                     {
                        return ctp;
                     }
                  }
                  catch (Exception e)
                  {
                     // Likely due to no active window
                     if (ctp.Window == null)
                     {
                        // This is the one
                        return ctp;
                     }
                  }
               }
            }
         }
         catch
         {
         }
         return null;
      }

      private void RemoveOrphanedTaskPanes()
      {
         Microsoft.Office.Tools.CustomTaskPane ctp;

         for (int i = this.CustomTaskPanes.Count; i > 0; i--)
         {
            ctp = this.CustomTaskPanes[i - 1];
            if (ctp.Window == null)
            {
               this.CustomTaskPanes.Remove(ctp);
            }
         }
      }
   }
}
