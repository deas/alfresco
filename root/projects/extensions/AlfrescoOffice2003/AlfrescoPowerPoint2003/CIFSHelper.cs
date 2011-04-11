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
using System.Collections;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;

namespace AlfrescoPowerPoint2003
{
   internal class CIFSHelper : IServerHelper
   {
      // Misc Windows constants
      const int NO_ERROR = 0;
      const int MAX_PATH = 260;

      // Various Alfresco constants
      const string UNC_PATH_PREFIX = @"\\";
      const string PATH_SEPARATOR = @"\";
      const string IOSIGNATURE = "ALFRESCO";
	   const int IOSIGNATURE_LEN = 8;

      // CreateFile constants
      readonly static IntPtr INVALID_HANDLE_VALUE = new IntPtr(-1);

      const uint FILE_READ_DATA = 0x00000001;
      const uint FILE_WRITE_DATA = 0x00000002;
      const uint FILE_SHARE_READ = 0x00000001;
      const uint FILE_SHARE_WRITE = 0x00000002;
      const uint FILE_SHARE_DELETE = 0x00000004;
      const uint OPEN_EXISTING = 3;

      const uint GENERIC_READ = (0x80000000);
      const uint GENERIC_WRITE = (0x40000000);

      const uint FILE_FLAG_NO_BUFFERING = 0x20000000;
      const uint FILE_READ_ATTRIBUTES = (0x0080);
      const uint FILE_WRITE_ATTRIBUTES = 0x0100;

      [DllImport("kernel32.dll", SetLastError = true)]
      static extern IntPtr CreateFile(
          string lpFileName,
          uint dwDesiredAccess,
          uint dwShareMode,
          IntPtr lpSecurityAttributes,
          uint dwCreationDisposition,
          uint dwFlagsAndAttributes,
          IntPtr hTemplateFile);

      [DllImport("kernel32.dll", SetLastError = true)]
      static extern int CloseHandle(IntPtr hObject);

      [DllImport("kernel32.dll", SetLastError = true)]
      static extern bool DeviceIoControl(
          IntPtr hDevice,
          uint dwIoControlCode,
          IntPtr lpInBuffer,
          uint nInBufferSize,
          [Out] IntPtr lpOutBuffer,
          uint nOutBufferSize,
          ref uint lpBytesReturned,
          IntPtr lpOverlapped);

      [DllImport("mpr.dll", EntryPoint = "WNetGetConnectionA", SetLastError = true)]
      public static extern int WNetGetConnection(
         string localName,
         StringBuilder remoteName,
         ref int remoteNameLength);

      // The user-entered configuration value
      private string m_CIFSServer = "";
      // The UNC version of the configuration value
      private string m_UNCRootPath = "";
      private IntPtr m_handle = INVALID_HANDLE_VALUE;
      private bool m_IsValidAlfrescoServer = false;

      /// <summary>
      /// CIFS Constructor
      /// </summary>
      /// <param name="AlfrescoServer">UNC path to the Alfresco CIFS server</param>
      public CIFSHelper(string AlfrescoServer)
      {
         m_CIFSServer = AlfrescoServer;
         if (!m_CIFSServer.EndsWith("\\"))
         {
            m_CIFSServer += "\\";
         }
         m_IsValidAlfrescoServer = SetUNCRootPath(AlfrescoServer);
      }

      /// <summary>
      /// IServerHelper interface. Queries the CIFS server at the given UNC path for an authorization ticket
      /// </summary>
      /// <returns>(string) Auth Ticket</returns>
      public string GetAuthenticationTicket()
      {
         return GetAuthenticationTicket("", "");
      }

      /// <summary>
      /// IServerHelper interface. Queries the CIFS server at the given UNC path for an authorization ticket
      /// </summary>
      /// <param name="Username"></param>
      /// <param name="Password"></param>
      /// <returns></returns>
      public string GetAuthenticationTicket(string Username, string Password)
      {
         if (!m_IsValidAlfrescoServer)
         {
            return "";
         }
         return GetAuthTicket();
      }

      /// <summary>
      /// IServerHelper interface. Converts a WebDAV or CIFS path into a relative Alfresco one
      /// </summary>
      /// <param name="documentPath">Full path to the document</param>
      /// <returns>(string) Relative path to Alfresco document</returns>
      public string GetAlfrescoPath(string documentPath)
      {
         string alfrescoPath = "";

         // Referencing a valid Alfresco server?
         if (!m_IsValidAlfrescoServer)
         {
            return "";
         }
         // Does the documentPath belong to the server?
         if (documentPath.ToLower().IndexOf(m_CIFSServer.ToLower()) == 0)
         {
            alfrescoPath = documentPath.Remove(0, m_CIFSServer.Length).Replace("\\", "/");
         }
         else
         {
            // Office likes mapping UNC paths to mapped drive letters
            string path = MappedDriveToUNC(documentPath);
            if (path.ToLower().IndexOf(m_CIFSServer.ToLower()) == 0)
            {
               alfrescoPath = path.Remove(0, m_CIFSServer.Length).Replace("\\", "/");
            }
         }
         return alfrescoPath;
      }

      EAuthenticationType IServerHelper.GetAuthenticationType()
      {
          return EAuthenticationType.NTLM;
      }

      /// <summary>
      /// Set the UNC root path to be used as the working directory
      /// </summary>
      /// <param name="rootPath">(string) Path to be used</param>
      /// <returns>(bool) true=Success</returns>
      private bool SetUNCRootPath(string rootPath)
      {
         if (m_handle != INVALID_HANDLE_VALUE)
         {
            CloseHandle(m_handle);
         }

         string uncPath = rootPath;

         // See if the path was a valid drive mapping
         string path = MappedDriveToUNC(uncPath);
         if (path != "")
         {
            uncPath = path;
         }

         // Check if the UNC path is valid
         if (uncPath.StartsWith(UNC_PATH_PREFIX))
         {
            // Strip any trailing separator from the path
            if (uncPath.EndsWith(PATH_SEPARATOR))
            {
               uncPath = uncPath.Substring(0, uncPath.Length - 1);
            }

            // Make sure the path is to a folder
            try
            {
               if ((File.GetAttributes(uncPath) & FileAttributes.Directory) == FileAttributes.Directory)
               {
                  // Open the path for read access
                   m_handle = CreateFile(uncPath, FILE_READ_DATA, FILE_SHARE_READ | FILE_SHARE_WRITE, IntPtr.Zero, OPEN_EXISTING, 0x02000000, IntPtr.Zero);
               }
            }
            catch
            {
               m_handle = INVALID_HANDLE_VALUE;
            }

            // Set the root path
            int pos = uncPath.IndexOf(PATH_SEPARATOR, 2);
            if (pos != -1)
            {
               pos = uncPath.IndexOf(PATH_SEPARATOR, pos + 1);
               if (pos == -1)
               {
                  m_UNCRootPath = uncPath;
               }
               else
               {
                  m_UNCRootPath = uncPath.Substring(0, pos);
               }
            }
         }

         return this.IsAlfrescoFolder();
      }

      private string MappedDriveToUNC(string mappedPath)
      {
         string uncPath = mappedPath;

         // Convert the drive mapping to a UNC path
         if ((uncPath.Length >= 3) && (uncPath.Substring(1, 2).Equals(":\\")))
         {
            // Try and convert the local path to a UNC path
            string mappedDrive = uncPath.Substring(0, 2);

            // Create a string buffer
            int BufferSize = MAX_PATH;
            StringBuilder Buffer = new StringBuilder(BufferSize);

            // Call the windows API
            int ret = WNetGetConnection(mappedDrive, Buffer, ref BufferSize);
            if (ret != NO_ERROR)
            {
               return "";
            }

            // Build the UNC path to the folder
            uncPath = Buffer.ToString();
            if (!uncPath.EndsWith(PATH_SEPARATOR))
            {
               uncPath += PATH_SEPARATOR;
            }

            if (mappedPath.Length > 3)
            {
               uncPath += mappedPath.Substring(3);
            }
         }
         else
         {
            uncPath = "";
         }

         return uncPath;
      }

      // Define a structure suitable to receive the output of the FSCTL_ALFRESCO_PROBE request
      [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Unicode)]
      private struct ioctlAlfrescoProbe
      {
         [MarshalAs(UnmanagedType.I4)]
         public Int32 actionSts;
         [MarshalAs(UnmanagedType.I4)]
         public Int32 version;
      }

      /// <summary>
      /// Check if the path is a folder on an Alfresco CIFS server
      /// </summary>
      /// <returns>(bool) true=valid folder</returns>
      private bool IsAlfrescoFolder()
      {
         // Check if the handle is valid, if not then the path is not valid
         if (m_handle == INVALID_HANDLE_VALUE)
         {
            return false;
         }

         // Send a special I/O control to the Alfresco share to check that it is an Alfresco CIFS server
         ioctlAlfrescoProbe alfrescoProbeStruct = new ioctlAlfrescoProbe();
         bool alfFolder = false;

         try
         {
            // Copy output string into generic object
            object objOutput = alfrescoProbeStruct;
            // Check if the remote server is an Alfresco CIFS server
            uint len = SendIOControl(FSConstants.FSCTL_ALFRESCO_PROBE, ref objOutput, (uint)Marshal.SizeOf(alfrescoProbeStruct));

            alfFolder = true;
         }
         catch (Exception e)
         {
            System.Diagnostics.Debug.Print(e.Message);
         }

         // If the folder is not an Alfresco CIFS folder then close the folder
         if (!alfFolder)
         {
            CloseHandle(m_handle);
            m_handle = INVALID_HANDLE_VALUE;
         }

         // Return the folder status
         return alfFolder;
      }

      // Define a structure suitable to receive the output of the FSCTL_ALFRESCO_GETAUTHTICKET request
      [StructLayout(LayoutKind.Sequential, CharSet=CharSet.Unicode)]
      private struct ioctlAuthTicket
      {
         [MarshalAs(UnmanagedType.I4)]
         public Int32 actionSts;
         [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 256)]
         public string authTicket;
      }

      /// <summary>
      /// Get the authentication ticket by sending an I/O control request
      /// </summary>
      /// <returns>(string) AuthTicket</returns>
      private string GetAuthTicket()
      {
         string strAuthTicket = "";

         // Check if the handle is valid, if not then shortcut return
         if (m_handle == INVALID_HANDLE_VALUE)
         {
            return strAuthTicket;
         }

         // Send a special I/O control to the Alfresco share to get the Authentication Ticket
         ioctlAuthTicket authTicketStruct = new ioctlAuthTicket();

         try
         {
            // Copy output structure to generic object
            object objOutput = authTicketStruct;
            // Send the query to the Alfresco CIFS server
            uint len = SendIOControl(FSConstants.FSCTL_ALFRESCO_GETAUTHTICKET, ref objOutput, (uint)Marshal.SizeOf(authTicketStruct));
            // Retrieve the output
            authTicketStruct = (ioctlAuthTicket)objOutput;
            strAuthTicket = authTicketStruct.authTicket;
         }
         catch (Exception e)
         {
            System.Diagnostics.Debug.Print(e.Message);
         }
         
         return strAuthTicket;
      }

      /// <summary>
      /// Send the I/O control request, receive the response
      /// </summary>
      /// <param name="ctlCode">The control code for the operation</param>
      /// <param name="objOutput">Managed object to receive the output data</param>
      /// <param name="sizeOutput">The size (bytes) of the managed output object</param>
      /// <returns>Length of valid data returned</returns>
      private uint SendIOControl(uint ctlCode, ref object objOutput, uint sizeOutput)
      {
         // IOSIGNATURE needs to be sent
         uint sizeInput = (uint)IOSIGNATURE_LEN;
         IntPtr input = Marshal.AllocHGlobal(new IntPtr(sizeInput));
         Marshal.Copy(ASCIIEncoding.ASCII.GetBytes(IOSIGNATURE), 0, input, IOSIGNATURE_LEN);

         // Allow room for the IOSIGNATURE to be returned in front of the output data
         sizeOutput += (uint)IOSIGNATURE_LEN;
         IntPtr output = Marshal.AllocHGlobal(new IntPtr(sizeOutput));
         Marshal.Copy(ASCIIEncoding.ASCII.GetBytes(IOSIGNATURE), 0, output, IOSIGNATURE_LEN);

         // Will contain the actual length of the output data
         uint size = 0;

         // Kernel32.dll -> DeviceIoControl()
         bool fResult = DeviceIoControl(
            m_handle,
            ctlCode,
            input, sizeInput,
            output, sizeOutput,
            ref size, IntPtr.Zero);

         if (fResult)
         {
            // Validate the reply signature
            if (size >= IOSIGNATURE_LEN)
            {
               string strSig = Marshal.PtrToStringAnsi(output, IOSIGNATURE_LEN);
               if (strSig != IOSIGNATURE)
               {
                  throw new Exception("Invalid I/O control signature received");
               }

               // Skip over the IOSIGNATURE string
               output = (IntPtr)((Int64)output + IOSIGNATURE_LEN);

               // Convert the returned byte array into managed code
               if (objOutput.GetType().ToString() == "System.String")
               {
                  objOutput = Marshal.PtrToStringAuto(output);
               }
               else
               {
                  objOutput = Marshal.PtrToStructure(output, objOutput.GetType());
               }
            }
         }
         else
         {
            throw new Exception("Send I/O control error. " + Marshal.GetLastWin32Error().ToString());
         }

         return size;
      }
   }

   /// <summary>
   /// Constants imported from winioctl.h and Alfresco.hpp
   /// </summary>
   internal class FSConstants
   {
      const uint FILE_DEVICE_FILE_SYSTEM = 0x00000009;

      const uint METHOD_BUFFERED = 0;

      const uint FILE_ANY_ACCESS = 0;
      const uint FILE_WRITE_DATA = 0x0002;

      public static uint FSCTL_ALFRESCO_PROBE = CTL_CODE(FILE_DEVICE_FILE_SYSTEM, 0x800, METHOD_BUFFERED, FILE_ANY_ACCESS);
      public static uint FSCTL_ALFRESCO_FILESTS = CTL_CODE(FILE_DEVICE_FILE_SYSTEM, 0x801, METHOD_BUFFERED, FILE_ANY_ACCESS);
      public static uint FSCTL_ALFRESCO_GETACTIONINFO = CTL_CODE(FILE_DEVICE_FILE_SYSTEM, 0x804, METHOD_BUFFERED, FILE_WRITE_DATA);
      public static uint FSCTL_ALFRESCO_RUNACTION = CTL_CODE(FILE_DEVICE_FILE_SYSTEM, 0x805, METHOD_BUFFERED, FILE_WRITE_DATA);
      public static uint FSCTL_ALFRESCO_GETAUTHTICKET = CTL_CODE(FILE_DEVICE_FILE_SYSTEM, 0x806, METHOD_BUFFERED, FILE_ANY_ACCESS);

      static uint CTL_CODE(uint DeviceType, uint Function, uint Method, uint Access)
      {
         return ((DeviceType) << 16) | ((Access) << 14) | ((Function) << 2) | (Method);
      }
   }
}
