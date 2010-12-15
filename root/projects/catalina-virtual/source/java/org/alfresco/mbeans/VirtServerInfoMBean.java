/*-----------------------------------------------------------------------------
*  Copyright 2007-2010 Alfresco Software Limited.
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
*  
*  
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    VirtServerInfoMBean.java
*----------------------------------------------------------------------------*/


package org.alfresco.mbeans;

/**
* Interface for helper bean containing information used to create 
* peer-to-peer registration of virtualization server with remote
* Alfresco server.
*/
public interface VirtServerInfoMBean
{
    // Local virtualization server info
    public String   getVirtServerDomain(); 
    public void     setVirtServerDomain(String host);
    public int      getVirtServerHttpPort();
    public void     setVirtServerHttpPort(int port);
    public int      getVirtServerJmxRmiPort();
    public void     setVirtServerJmxRmiPort(int port);

    /** 
    *  Get the number of milliseconds the virtualization server will
    *  wait before retrying a failed connection to the Alfresco server.
    *  This allows the virtualization server to recover from a temporary
    *  network outage, a restart of the Alfresco webapp, the vagaries
    *  of daemon startup order, etc.
    */
    public int      getVirtServerConnectionRetryInterval();

    /** 
    *  Set the number of milliseconds the virtualization server will
    *  wait before retrying a failed connection to the Alfresco server.
    *  This allows the virtualization server to recover from a temporary
    *  network outage, a restart of the Alfresco webapp, the vagaries
    *  of daemon startup order, etc.
    */
    public void     setVirtServerConnectionRetryInterval(int milliseconds);



    /** Get the CIFS version tree automount path on Windows-based platforms. */
    public String   getVirtServerCifsAvmVersionTreeWin();

    /** Set the CIFS version tree automount path on Windows-based platforms. */
    public void     setVirtServerCifsAvmVersionTreeWin(String mountPoint);

    /** True if attempt is made to automount CIFS on Windows. */
    public boolean  getVirtServerCifsAvmVersionTreeWinAutomount();

    /** Set whether attempt is made to automount CIFS on Windows. */
    public void     setVirtServerCifsAvmVersionTreeWinAutomount(boolean doAutomount);


    /** Get the CIFS version tree automount path on UNIX-style platforms. */
    public String   getVirtServerCifsAvmVersionTreeUnix();

    /** Set the CIFS version tree automount path on UNIX-style platforms. */
    public void     setVirtServerCifsAvmVersionTreeUnix(String mountPoint);

    /** True if attempt is made to automount CIFS on UNIX-style platforms.. */
    public boolean  getVirtServerCifsAvmVersionTreeUnixAutomount();

    /** Set whether attempt is made to automount CIFS on UNIX-style platforms.. */
    public void     setVirtServerCifsAvmVersionTreeUnixAutomount(boolean doAutomount);

    /** 
    *   Fetches the value of os.name on the Virtualization server.
    *   Note:  all Windows-style platforms start with the string "Windows".
    *   In case you're curious, here's a list of common values for "os.name":
    *
    *   <pre>
    *        AIX
    *        Digital Unix
    *        FreeBSD
    *        HP-UX
    *        Irix
    *        Linux
    *        Mac OS
    *        Mac OS X
    *        MPE/iX
    *        NetWare
    *        OpenVMS
    *        OS/2
    *        OS/390
    *        OSF1
    *        Solaris
    *        SunOS
    *        Windows 2000
    *        Windows 2003
    *        Windows 95
    *        Windows 98
    *        Windows CE
    *        Windows Me
    *        Windows NT
    *        Windows XP
    *   </pre>
    */
    public String   getVirtServerOsName();

    /** Sets the OS name of the machine hosting the virtualization server */
    public void     setVirtServerOsName(String osName);


    // Remote Alfresco server info
    public String   getAlfrescoJmxRmiHost(); 
    public void     setAlfrescoJmxRmiHost(String host);
    public int      getAlfrescoJmxRmiPort();
    public void     setAlfrescoJmxRmiPort(int port);

}
