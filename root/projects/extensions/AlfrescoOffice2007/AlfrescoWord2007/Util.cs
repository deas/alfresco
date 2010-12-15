using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Win32;

namespace AlfrescoWord2007
{
   public static class Util
   {
      public static string DefaultIcon(string fileExtn)
      {
         RegistryKey rkExtn = Registry.ClassesRoot.OpenSubKey(fileExtn);
         if (rkExtn == null)
         {
            return "";
         }

         RegistryKey rkIcon = Registry.ClassesRoot.OpenSubKey(rkExtn.GetValue("") + "\\DefaultIcon");
         if (rkIcon == null)
         {
            // Try the CLSID
            RegistryKey rkCLSID = Registry.ClassesRoot.OpenSubKey(rkExtn.GetValue("") + "\\CLSID");
            if (rkCLSID == null)
            {
               return "";
            }
            rkIcon = Registry.ClassesRoot.OpenSubKey("CLSID\\" + rkCLSID.GetValue("") + "\\DefaultIcon");
            if (rkIcon == null)
            {
               return "";
            }
         }
         return rkIcon.GetValue("").ToString();
      }
   }
}
