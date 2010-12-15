using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Text;
using System.Reflection;
using System.Runtime.InteropServices;
using System.Windows.Forms;
using Office = Microsoft.Office.Core;

namespace AlfrescoWord2007
{
   // This is an override of the RequestService method in the ThisAddIn class.
   public partial class ThisAddIn
   {
      private Ribbon m_Ribbon;

      protected override object RequestService(Guid serviceGuid)
      {
         if (serviceGuid == typeof(Office.IRibbonExtensibility).GUID)
         {
            if (m_Ribbon == null)
            {
               m_Ribbon = new Ribbon();
               m_Ribbon.OnAlfrescoShow += new Ribbon.AlfrescoShow(Ribbon_OnAlfrescoShow);
               m_Ribbon.OnAlfrescoHide += new Ribbon.AlfrescoHide(Ribbon_OnAlfrescoHide);
            }
            return m_Ribbon;
         }

         return base.RequestService(serviceGuid);
      }
   }

   [ComVisible(true)]
   public class Ribbon : Office.IRibbonExtensibility
   {
      // Event delegate definitions
      public delegate void AlfrescoShow();
      public delegate void AlfrescoHide();

      // Public events
      public event AlfrescoShow OnAlfrescoShow;
      public event AlfrescoHide OnAlfrescoHide;

      private Office.IRibbonUI ribbon = null;
      private bool m_isToggleAlfrescoPressed = false;

      public Ribbon()
      {
      }

      /// <summary>
      /// Allows external code to update the button state
      /// </summary>
      public bool ToggleAlfrescoState
      {
         set
         {
            m_isToggleAlfrescoPressed = value;
            ribbon.InvalidateControl("toggleAlfresco");
         }
         get
         {
            return m_isToggleAlfrescoPressed;
         }
      }

      /*
       *** IRibbonExtensibility Members
       */
      /// <summary>
      /// Request for the ribbon UI XML
      /// </summary>
      /// <param name="ribbonID"></param>
      /// <returns></returns>
      public string GetCustomUI(string ribbonID)
      {
         return Properties.Resources.Ribbon;
      }

      /*
       *** Ribbon Callbacks
       */
      /// <summary>
      /// Fires after ribbon UI has loaded
      /// </summary>
      /// <param name="ribbonUI"></param>
      public void OnLoad(Office.IRibbonUI ribbonUI)
      {
         this.ribbon = ribbonUI;
      }

      /// <summary>
      /// Returns button images for ribbon UI
      /// </summary>
      /// <param name="imageName"></param>
      /// <returns></returns>
      public stdole.IPictureDisp LoadImage(string imageName)
      {
         stdole.IPictureDisp image = null;

         switch (imageName)
         {
            case "Alfresco":
               image = PictureConverter.IconToPictureDisp(Properties.Resources.Alfresco);
               break;
         }
         return image;
      }

      /*
       *** ToggleAlfresco button callbacks
       */
      /// <summary>
      /// ToggleButton has been clicked
      /// </summary>
      /// <param name="control"></param>
      /// <param name="isPressed"></param>
      public void OnToggleAlfresco(Office.IRibbonControl control, bool isPressed)
      {
         m_isToggleAlfrescoPressed = isPressed;

         if (isPressed)
         {
            if (OnAlfrescoShow != null)
            {
               OnAlfrescoShow();
            }
         }
         else
         {
            if (OnAlfrescoHide != null)
            {
               OnAlfrescoHide();
            }
         }
      }

      /// <summary>
      /// Callback from ribbon controls to get label
      /// </summary>
      /// <param name="control"></param>
      /// <returns></returns>
      public string OnGetLabel(Office.IRibbonControl control)
      {
         switch (control.Id)
         {
            case "toggleAlfresco":
               return (m_isToggleAlfrescoPressed ? "Hide Alfresco" : "Show Alfresco");
         }
         return "";
      }

      // Callback from ribbon controls to determine pressed state
      public bool OnGetPressed(Office.IRibbonControl control)
      {
         switch (control.Id)
         {
            case "toggleAlfresco":
               return m_isToggleAlfrescoPressed;
         }
         return false;
      }

   }

   /// <summary>
   /// PictureConverter class used to supply images in correct format
   /// </summary>
   internal class PictureConverter : AxHost
   {
      private PictureConverter() : base(String.Empty) { }

      static public stdole.IPictureDisp ImageToPictureDisp(Image image)
      {
         return (stdole.IPictureDisp)GetIPictureDispFromPicture(image);
      }

      static public stdole.IPictureDisp IconToPictureDisp(Icon icon)
      {
         return ImageToPictureDisp(icon.ToBitmap());
      }

      static public Image PictureDispToImage(stdole.IPictureDisp picture)
      {
         return GetPictureFromIPicture(picture);
      }
   }
}
