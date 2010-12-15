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
using System.Text;
using Microsoft.Win32;

namespace AlfrescoWord2003
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
