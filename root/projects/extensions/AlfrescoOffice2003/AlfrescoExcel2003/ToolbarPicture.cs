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
using System.Drawing;
using System.Runtime.InteropServices;
using stdole;

namespace AlfrescoExcel2003
{
   /// <summary>
   /// Implements IPictureDisp so we can use images on the Office toolbar
   /// </summary>
   public class ToolbarPicture : IPictureDisp, IPicture
   {
      Bitmap _image = null;
      IntPtr _handle = IntPtr.Zero;

      public ToolbarPicture(Bitmap image)
      {
         _image = image;
      }

      ~ToolbarPicture()
      {
         if (_handle != IntPtr.Zero)
         {
            DeleteObject(_handle);
         }
      }

      [DllImport("gdi32.dll")]
      static extern void DeleteObject(IntPtr _handle);

      public int Width
      {
         get
         {
            return _image.Width;
         }
      }

      public int Height
      {
         get
         {
            return _image.Height;
         }
      }

      public short Type
      {
         get
         {
            return 1;
         }
      }

      public int Handle
      {
         get
         {
            if (_handle == IntPtr.Zero)
            {
               _handle = _image.GetHbitmap();
            }
            return _handle.ToInt32();
         }
      }

      public int hPal
      {
         get
         {
            return 0;
         }
         set
         {
         }
      }

      public void Render(int hdc, int x, int y, int cx, int cy, int xSrc, int ySrc, int cxSrc, int cySrc, IntPtr prcWBounds)
      {
         Graphics graphics = Graphics.FromHdc(new IntPtr(hdc));
         graphics.DrawImage(_image, new Rectangle(x, y, cx, cy), xSrc, ySrc, cxSrc, cySrc, GraphicsUnit.Pixel);
      }

      #region IPicture Members
      public int Attributes
      {
         get
         {
            return 0;
         }
      }

      public int CurDC
      {
         get
         {
            return 0;
         }
      }

      public bool KeepOriginalFormat
      {
         get
         {
            return false;
         }
         set
         {
         }
      }

      public void PictureChanged()
      {
      }

      public void SaveAsFile(IntPtr pstm, bool fSaveMemCopy, out int pcbSize)
      {
         pcbSize = 0;
      }     
 
      public void SelectPicture(int hdcIn, out int phdcOut, out int phbmpOut)
      {
         phdcOut = 0;
         phbmpOut = 0;
      }

      public void SetHdc(int hdc)
      {
      }
      #endregion
   }
}
