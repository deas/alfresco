using System;
using System.Windows.Forms;
using Microsoft.VisualStudio.Tools.Applications.Runtime;
using Word = Microsoft.Office.Interop.Word;
using Office = Microsoft.Office.Core;

namespace Alfresco2007
{
    public partial class ThisAddIn
    {
        private UserControl1 myUserControl1;
        private Microsoft.Office.Tools.CustomTaskPane myCustomTaskPane = null;
        private String strAlfServer = "http://localhost:8080/alfresco/webdav";
//       private String strAlfServer = "\\\\paulhh02_a\\Alfresco";
//        private String strAlfServer = "http://paulhh02_a/Alfresco";
        private Boolean bNoDocuments = false;
        
        private void ThisAddIn_Startup(object sender, System.EventArgs e)
        {
            Application.DocumentOpen += new Microsoft.Office.Interop.Word.ApplicationEvents4_DocumentOpenEventHandler(Application_DocumentOpen);
            Application.DocumentBeforeClose += new Microsoft.Office.Interop.Word.ApplicationEvents4_DocumentBeforeCloseEventHandler(Application_DocumentBeforeClose);
        }

        void Application_DocumentBeforeClose(Microsoft.Office.Interop.Word.Document Doc, ref bool Cancel)
        {
            // If just about to close the last doc/window, keep the pane open at home
            if (Application.Windows.Count == 1)
            {
                myUserControl1.showHome(Application);
                bNoDocuments = true;
            }
        }

        void Application_DocumentOpen(Microsoft.Office.Interop.Word.Document Doc)
        {
            String strAlfPath = null;
            int nPosition;

            nPosition = Doc.FullName.IndexOf(strAlfServer);
            if (nPosition == 0)
            {
                AddAlfrescoTaskPane(Doc);
                strAlfPath = Doc.FullName.Substring(strAlfServer.Length + 1);
                myUserControl1.showDocumentDetails(strAlfPath);
                bNoDocuments = false;
            }
        }

        public void AddAlfrescoTaskPane()
        {
            AddAlfrescoTaskPane(null);
            myUserControl1.showHome(Application);
        }

        public void AddAlfrescoTaskPane(Microsoft.Office.Interop.Word.Document Doc)
        {
            if (bNoDocuments == false)
            {
                myUserControl1 = new UserControl1();
                if (Doc != null)
                {
                    myCustomTaskPane = this.CustomTaskPanes.Add(myUserControl1, "Alfresco Task Pane");
                }
                else
                {
                    myCustomTaskPane = this.CustomTaskPanes.Add(myUserControl1, "Alfresco Task Pane");
                }
                myCustomTaskPane.Width = 300;
                myCustomTaskPane.Visible = true;
                myUserControl1.AlfServer = strAlfServer;
            }
        }

        public void RemoveAlfrescoTaskPane()
        {
            this.CustomTaskPanes.Remove(myCustomTaskPane);
            myCustomTaskPane = null;
            if (bNoDocuments == true)
            {
                bNoDocuments = false;
            }
        }

        private void ThisAddIn_Shutdown(object sender, System.EventArgs e)
        {
            RemoveAlfrescoTaskPane();
        }

        private void InternalStartup()
        {
            this.Startup += new System.EventHandler(ThisAddIn_Startup);
            this.Shutdown += new System.EventHandler(ThisAddIn_Shutdown);
        }


    }
}
