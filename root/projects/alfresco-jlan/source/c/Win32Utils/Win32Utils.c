/*
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
 
#include <stdio.h>
#include <windows.h>
#include <winbase.h>
#include <winnetwk.h>

#include "org_alfresco_jlan_util_win32_Win32Utils.h"

/*
 * Class:     org_alfresco_jlan_util_win32_Win32Utils
 * Method:    SetWorkingSetSize
 * Signature: (JJ)Z
 */
JNIEXPORT jboolean JNICALL Java_org_alfresco_jlan_util_win32_Win32Utils_SetWorkingSetSize
	(JNIEnv* jnienv, jclass jthis, jlong jminBytes, jlong jmaxBytes) {

	/*
	 * Get the current process handle
	 */
	
	HANDLE thisProcess = GetCurrentProcess();

	/*
	 * Set the working set size for the Java process
	 */

	return (jboolean) SetProcessWorkingSetSize(thisProcess, (DWORD) jminBytes, (DWORD) jmaxBytes);
}

/*
 * Class:     org_alfresco_jlan_util_win32_Win32Utils
 * Method:    MapNetworkDrive
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZ)Z
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_util_win32_Win32Utils_MapNetworkDrive
  (JNIEnv* jnienv, jclass jthis, jstring jremPath, jstring jlocalPath, jstring juserName, jstring jpassword,
  jboolean jinteractive, jboolean jprompt) {

	const jchar* remPath   = NULL;
	const jchar* localPath = NULL;
	const jchar* userName  = NULL;
	const jchar* password  = NULL;

	int flags = 0;
	int sts;

	NETRESOURCE netRsc;

	/*
	 * Access the string values
	 */

	if ( jremPath != NULL)
		remPath = (*jnienv)->GetStringChars(jnienv, jremPath, 0);

	if ( jlocalPath != NULL)
		localPath = (*jnienv)->GetStringChars(jnienv, jlocalPath, 0);

	if ( juserName != NULL)
		userName = (*jnienv)->GetStringChars(jnienv, juserName, 0);

	if ( jpassword != NULL)
		password = (*jnienv)->GetStringChars(jnienv, jpassword, 0);

	/*
	 *	Populate the NETRESOURCE structure
	 */

	memset(&netRsc, 0, sizeof(netRsc));

	netRsc.dwType       = RESOURCETYPE_DISK;
	netRsc.lpLocalName  = (LPWSTR) localPath;
	netRsc.lpRemoteName = (LPWSTR) remPath;
	netRsc.lpProvider   = NULL;

	/*
	 * Set various option flags for the connection
	 */

	if ( jinteractive)
		flags += CONNECT_INTERACTIVE;

	if ( jprompt)
		flags += CONNECT_PROMPT;

	/*
	 * Map the network drive
	 */

	sts = WNetAddConnection2(&netRsc, password, userName, flags);

	/*
	 * Release JNI resources
	 */

	if ( jremPath != NULL)
		(*jnienv)->ReleaseStringChars(jnienv, jremPath, remPath);

	if ( jlocalPath != NULL)
		(*jnienv)->ReleaseStringChars(jnienv, jlocalPath, localPath);

	if ( juserName != NULL)
		(*jnienv)->ReleaseStringChars(jnienv, juserName, userName);

	if ( jpassword != NULL)
		(*jnienv)->ReleaseStringChars(jnienv, jpassword, password);

	/*
	 * Return the drive map status
	 */

	return sts;
}

/*
 * Class:     org_alfresco_jlan_util_win32_Win32Utils
 * Method:    DeleteNetworkDrive
 * Signature: (Ljava/lang/String;ZZ)I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_util_win32_Win32Utils_DeleteNetworkDrive
(JNIEnv* jnienv, jclass jthis, jstring jdevName, jboolean jupdProfile, jboolean jforce) {

	const jchar* devName = NULL;

	int flags = 0;
	int sts;

	if ( jdevName != NULL)
		devName = (*jnienv)->GetStringChars(jnienv, jdevName, 0);

	/*
	 * Set the flags
	 */

	if ( jupdProfile)
		flags += CONNECT_UPDATE_PROFILE;

	/*
	 * Disconnect the mapped drive
	 */

	sts = WNetCancelConnection2(devName, flags, jforce);

	/*
	 * Release JNI resources
	 */

	if (jdevName != NULL)
		(*jnienv)->ReleaseStringChars(jnienv, jdevName, devName);

	/*
	 * Return the unmap drive status
	 */

	return sts;
}

