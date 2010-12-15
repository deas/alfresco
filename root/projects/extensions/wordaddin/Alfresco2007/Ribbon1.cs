using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Text;
using System.Reflection;
using System.Runtime.InteropServices;
using System.Windows.Forms;
using Office = Microsoft.Office.Core;
using stdole;

namespace Alfresco2007
{
    // TODO:
    // This is an override of the RequestService method in the ThisAddIn class.
    // To hook up your custom ribbon uncomment this code.
    public partial class ThisAddIn
    {
        private Ribbon1 ribbon;
    
        protected override object RequestService(Guid serviceGuid)
        {
            if (serviceGuid == typeof(Office.IRibbonExtensibility).GUID)
            {
                if (ribbon == null)
                    ribbon = new Ribbon1();
                return ribbon;
            }
    
            return base.RequestService(serviceGuid);
        }
    }


    internal class PictureConverter : AxHost
    {
        private PictureConverter() : base(String.Empty) { }

        static public stdole.IPictureDisp ImageToPictureDisp(System.Drawing.Image image)
        {
            return (stdole.IPictureDisp)GetIPictureDispFromPicture(image);
        }

        static public stdole.IPictureDisp IconToPictureDisp(System.Drawing.Icon icon)
        {
            return ImageToPictureDisp(icon.ToBitmap());
        }

        static public System.Drawing.Image PictureDispToImage(stdole.IPictureDisp picture)
        {
            return GetPictureFromIPicture(picture);
        }
    }


    [ComVisible(true)]
    public class Ribbon1 : Office.IRibbonExtensibility
    {
        private Office.IRibbonUI ribbon;

        public Ribbon1()
        {
        }

        public void AlfrescoShowClick(Office.IRibbonControl control)
        {
            Globals.ThisAddIn.AddAlfrescoTaskPane();
        }

        public void AlfrescoHideClick(Office.IRibbonControl control)
        {
            Globals.ThisAddIn.RemoveAlfrescoTaskPane();
        }

        public stdole.IPictureDisp GetImage(string imageName)
        {
            return
              PictureConverter.IconToPictureDisp(Properties.Resources.alfresco);
        }

        #region IRibbonExtensibility Members

        public string GetCustomUI(string ribbonID)
        {
            return GetResourceText("Alfresco2007.Ribbon1.xml");
        }

        #endregion

        #region Ribbon Callbacks

        public void OnLoad(Office.IRibbonUI ribbonUI)
        {
            this.ribbon = ribbonUI;
        }

        public void OnToggleButton1(Office.IRibbonControl control, bool isPressed)
        {
            if (isPressed)
                MessageBox.Show("Pressed");
            else
                MessageBox.Show("Released");
        }

        #endregion

        #region Helpers

        private static string GetResourceText(string resourceName)
        {
            Assembly asm = Assembly.GetExecutingAssembly();
            string[] resourceNames = asm.GetManifestResourceNames();
            for (int i = 0; i < resourceNames.Length; ++i)
            {
                if (string.Compare(resourceName, resourceNames[i], StringComparison.OrdinalIgnoreCase) == 0)
                {
                    using (StreamReader resourceReader = new StreamReader(asm.GetManifestResourceStream(resourceNames[i])))
                    {
                        if (resourceReader != null)
                        {
                            return resourceReader.ReadToEnd();
                        }
                    }
                }
            }
            return null;
        }

        #endregion
    }
}
