using System;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading;

namespace AlfrescoExcel2003
{
   public class CBTHook
   {
      public delegate IntPtr CBTHookProc(int nCode, IntPtr wParam, IntPtr lParam);

      // Declare the hook handle as an int
      static IntPtr hHook = IntPtr.Zero;
      private CBTHookProc hookFunction;

      // Declare the CBT hook constants
      public const int WH_CBT = 5;
      public const long HCBT_ACTIVATE = 5;

      //Declare the wrapper managed CBTActivateStruct class.
      [StructLayout(LayoutKind.Sequential)]
      public class CBTActivateStruct
      {
         public bool fMouse;
         public int hWndActive;
      }

      static FormDebug m_Debug;

      // Win32 SDK functions
      [DllImport("user32.dll")]
      public static extern IntPtr SetWindowsHookEx(int idHook, System.Delegate lpfn, IntPtr hInstance, IntPtr threadId);

      [DllImport("user32.dll")]
      public static extern int UnhookWindowsHookEx(IntPtr idHook);

      [DllImport("user32.dll")]
      public static extern IntPtr CallNextHookEx(IntPtr idHook, int nCode, IntPtr wParam, IntPtr lParam);

      [DllImport("kernel32.dll")]
      public static extern int GetCurrentThreadId();
      
      public bool InstallHook(FormDebug frmDebug)
      {
         m_Debug = frmDebug;
         if (hHook.ToInt32() == 0)
         {
            // Create an instance of HookProc
            hookFunction = new CBTHookProc(CBTHookProcFn);

            hHook = SetWindowsHookEx(WH_CBT,
                     hookFunction,
                     IntPtr.Zero,
                     new IntPtr(GetCurrentThreadId())); //Thread.CurrentThread.ManagedThreadId
         }

         // Return success status of SetWindowsHookEx
         return (hHook.ToInt32() != 0);
      }

      public void UninstallHook()
      {
         if (hHook.ToInt32() != 0)
         {
            UnhookWindowsHookEx(hHook);
            hHook = IntPtr.Zero;
         }
      }

      public static IntPtr CBTHookProcFn(int nCode, IntPtr wParam, IntPtr lParam)
      {
         if (nCode < 0)
         {
            return CallNextHookEx(hHook, nCode, wParam, lParam);
         }
         else if (nCode == HCBT_ACTIVATE)
         {
            //Marshall the data from the callback.
            CBTActivateStruct cbtActivateStruct = (CBTActivateStruct)Marshal.PtrToStructure(lParam, typeof(CBTActivateStruct));
            m_Debug.DebugPrint(String.Format("hWnd = {0}", cbtActivateStruct.hWndActive));
         }
         else if (nCode == 9) //HCBT_SETFOCUS)
         {
            m_Debug.DebugPrint(String.Format("hWndFocus = {0}, hWndLose = {1}", wParam.ToInt32(), lParam.ToInt32()));
         }
         else
         {
            return CallNextHookEx(hHook, nCode, wParam, lParam);
         }
         return CallNextHookEx(hHook, nCode, wParam, lParam);
      }
   }
}
