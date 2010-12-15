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
using System.ComponentModel;
using System.Runtime.InteropServices;
using System.Threading;

namespace AlfrescoPowerPoint2003
{
   public delegate void WindowHasFocus(int hwnd);
   public delegate void WindowLostFocus(int hwnd);

   public class AppWatcher
   {
      // Public events
      public event WindowHasFocus OnWindowHasFocus;
      public event WindowLostFocus OnWindowLostFocus;

      // Private member variables
      private static BackgroundWorker m_Worker;
      private int m_hwnd = -1;
      private int m_hwndAlfresco = -1;
      private bool m_shouldHaveFocus = true;

      // Win32 SDK functions
      [DllImport("user32.dll")]
      public static extern int GetForegroundWindow();

      public void Start(int hwndApp)
      {
         m_hwnd = hwndApp;
         m_Worker = new BackgroundWorker();
         m_Worker.WorkerSupportsCancellation = true;
         m_Worker.DoWork += new DoWorkEventHandler(AppWatcherProc);
         m_Worker.RunWorkerCompleted += new RunWorkerCompletedEventHandler(WindowFocusChanged);
         m_Worker.RunWorkerAsync(hwndApp);
      }

      public int AlfrescoWindow
      {
         set
         {
            m_hwndAlfresco = value;
         }
      }

      ~AppWatcher()
      {
         if ((m_Worker != null) && (m_Worker.IsBusy))
         {
            m_Worker.CancelAsync();
         }
      }

      void WindowFocusChanged(object sender, RunWorkerCompletedEventArgs e)
      {
         if (!e.Cancelled)
         {
            // Store what state we think the focus should be for next time
            m_shouldHaveFocus = (bool)e.Result;

            if (m_shouldHaveFocus)
            {
               // Window now has focus
               if (OnWindowHasFocus != null)
               {
                  OnWindowHasFocus(m_hwnd);
               }
            }
            else
            {
               // Window has lost focus
               if (OnWindowLostFocus != null)
               {
                  OnWindowLostFocus(m_hwnd);
               }
            }

            // Restart the worker
            m_Worker.RunWorkerAsync(m_hwnd);
         }
      }

      void AppWatcherProc(object sender, DoWorkEventArgs e)
      {
         int hwndFocus = GetForegroundWindow();
         bool initialFocus = ((m_hwnd == hwndFocus) || (m_hwndAlfresco == hwndFocus));

         // Does the focus look how we expect it to?
         if (initialFocus != m_shouldHaveFocus)
         {
            // No - return the actual focus state
            e.Result = initialFocus;
            return;
         }

         while (!m_Worker.CancellationPending)
         {
            // Does watched window have focus?
            hwndFocus = GetForegroundWindow();
            if ((m_hwnd == hwndFocus) || (m_hwndAlfresco == hwndFocus))
            {
               // Yes - has it changed from initial state
               if (!initialFocus)
               {
                  // App now has focus - return true
                  e.Result = true;
                  return;
               }
            }
            else
            {
               // No - did it used to have focus?
               if (initialFocus)
               {
                  // App just lost focus - return false
                  e.Result = false;
                  return;
               }
            }
            Thread.Sleep(250);
         }

         e.Cancel = m_Worker.CancellationPending;
         return;
      }
   }
}
