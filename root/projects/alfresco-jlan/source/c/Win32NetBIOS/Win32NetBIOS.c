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

#ifndef UNICODE
 #define UNICODE
#endif

// Define the maximum number of sockets per select for asynchronous I/O
//
// Note: Must be done before winsock2.h is included

#define MaxSocketsPerSelect	256

// Includes

#include <stdio.h>
#include <winsock2.h>
#include <windows.h>
#include <wsnetbs.h>
#include <lm.h>
#include <nb30.h>
#include <jni.h>
#include <IPHlpApi.h>

#include "org_alfresco_jlan_netbios_win32_Win32NetBIOS.h"

// Define the receive error flag, added to the receive length for a partial read

#define ReceiveErrorMask 0x80000000

// Define the socket write status to indicate that the socket would block

#define SocketWouldBlock -2

// Internal functions

void parseMultiSz(const wchar_t*, wchar_t*);
void throwWinsockException(JNIEnv* jnienv, int winsockErr, const char* msg);
void throwException(JNIEnv* jnienv, int stsCode, const char* msg);

// Event triggered when the Winsock interface is closed down

WSAEVENT _shutdownEvent = 0;

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    AddName
 * Signature: (ILjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_AddName
  (JNIEnv* jnienv, jclass jthis, jint lana, jbyteArray name) {

	jbyte* nameBuf = (*jnienv)->GetByteArrayElements(jnienv, name, 0);

	/*
	 * Allocate an NCB
	 */

	NCB ncb;
	memset(&ncb, 0, sizeof(ncb));

	/*
	 * Build the add name request
	 */

	ncb.ncb_command  = NCBADDNAME;
	ncb.ncb_lana_num = (unsigned char) lana;

	memcpy(ncb.ncb_name, nameBuf, NCBNAMSZ);

	(*jnienv)->ReleaseByteArrayElements(jnienv, name, nameBuf, 0);

	/*
	 * Add the NetBIOS name to the local name table
	 */

	Netbios(&ncb);

	/*
	 * Return the name number if successful, or the error status code as a negative
	 * number
	 */

	if ( ncb.ncb_retcode == NRC_GOODRET)
		return ncb.ncb_num;
	return -ncb.ncb_retcode;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    AddGroupName
 * Signature: (ILjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_AddGroupName
  (JNIEnv* jnienv, jclass jthis, jint lana, jbyteArray name) {

	jbyte* nameBuf = (*jnienv)->GetByteArrayElements(jnienv, name, 0);

	/*
	 * Allocate an NCB
	 */

	NCB ncb;
	memset(&ncb, 0, sizeof(ncb));

	/*
	 * Build the add group name request
	 */

	ncb.ncb_command  = NCBADDGRNAME;
	ncb.ncb_lana_num = (unsigned char) lana;

	memcpy(ncb.ncb_name, nameBuf, NCBNAMSZ);

	(*jnienv)->ReleaseByteArrayElements(jnienv, name, nameBuf, 0);

	/*
	 * Add the NetBIOS group name to the local name table
	 */

	Netbios(&ncb);

	/*
	 * Return the name number if successful, or the error status code as a negative
	 * number
	 */

	if ( ncb.ncb_retcode == NRC_GOODRET)
		return ncb.ncb_num;
	return -ncb.ncb_retcode;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    FindName
 * Signature: (I[B[B)I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_FindNameRaw
	(JNIEnv* jnienv, jclass jthis, jint lana, jbyteArray name, jbyteArray nameBuf, jint bufLen) {

	jbyte* namePtr = (*jnienv)->GetByteArrayElements(jnienv, name, 0);
	jbyte* bufPtr  = (*jnienv)->GetByteArrayElements(jnienv, nameBuf, 0);


	/*
	 * Allocate an NCB
	 */

	NCB ncb;
	memset(&ncb, 0, sizeof(ncb));

	/*
	 * Build the find name request
	 */

	ncb.ncb_command  = NCBFINDNAME;
	ncb.ncb_lana_num = (unsigned char) lana;

	memcpy(ncb.ncb_callname, namePtr, NCBNAMSZ);
	(*jnienv)->ReleaseByteArrayElements(jnienv, name, namePtr, 0);

	ncb.ncb_buffer = bufPtr;
	ncb.ncb_length = (unsigned short) bufLen;

	/*
	 * Find the NetBIOS name details
	 */

	Netbios(&ncb);

	/*
	 * Release the buffer
	 */

	(*jnienv)->ReleaseByteArrayElements(jnienv, nameBuf, bufPtr, 0);

	/*
	 * Return the status code
	 */

	return ncb.ncb_retcode;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    DeleteName
 * Signature: (ILjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_DeleteName
  (JNIEnv* jnienv, jclass jthis, jint lana, jbyteArray name) {
	return -1;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    LanaEnum
 * Signature: (Ljava/util/Vector;)I
 */
JNIEXPORT jintArray JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_LanaEnum
  (JNIEnv* jnienv, jclass jthis) {

	jsize numLANAs;
	jintArray lanaArray;
	int i;

	/*
	 * Allocate an NCB and LANA enum structure
	 */

	NCB ncb;
	LANA_ENUM lanaEnum;

	memset(&ncb, 0, sizeof(ncb));

	/*
	 * Build the LANA Enum request
	 */

	ncb.ncb_command = NCBENUM;
	ncb.ncb_buffer  = (PUCHAR) &lanaEnum;
	ncb.ncb_length  = sizeof(lanaEnum);

	/*
	 * Enumerate the available LANAs
	 */

	Netbios(&ncb);

	/*
	 * Build a Java int[] with the LANA numbers
	 */

	if ( ncb.ncb_retcode == NRC_GOODRET)
		numLANAs = (jsize) lanaEnum.length;

	lanaArray = (*jnienv)->NewIntArray(jnienv, numLANAs);

	if ( numLANAs > 0) {
		jint* pArray = (*jnienv)->GetIntArrayElements(jnienv, lanaArray, 0);

		for ( i = 0; i < numLANAs; i++) {
			pArray[i] = lanaEnum.lana[i];
		}

		(*jnienv)->ReleaseIntArrayElements(jnienv, lanaArray, pArray, 0);
	}

	/*
	 * Return the LANA list
	 */

	return lanaArray;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    Reset
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_Reset
  (JNIEnv* jnienv, jclass jthis, jint lana) {

	/*
	 * Allocate an NCB
	 */

	NCB ncb;
	memset(&ncb, 0, sizeof(ncb));

	/*
	 * Build the Reset request
	 */

	ncb.ncb_command  = NCBRESET;
	ncb.ncb_lsn      = 0;
	ncb.ncb_lana_num = (unsigned char) lana;

	ncb.ncb_callname[0] = 20;
	ncb.ncb_callname[2] = 30;

	/*
	 * Reset the LANA
	 */

	Netbios(&ncb);

	/*
	 * Return the status code
	 */

	return ncb.ncb_retcode;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    Listen
 * Signature: (ILjava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_Listen
  (JNIEnv* jnienv, jclass jthis, jint lana, jbyteArray toName, jbyteArray fromName, jbyteArray callerName) {


	jbyte* toNameBuf   = (*jnienv)->GetByteArrayElements(jnienv, toName, 0);
	jbyte* fromNameBuf = (*jnienv)->GetByteArrayElements(jnienv, fromName, 0);

	jbyte* pBuffer;

	/*
	 * Allocate an NCB
	 */

	NCB ncb;

	memset(&ncb, 0, sizeof(ncb));

	/*
	 * Build the listen request
	 */

	ncb.ncb_command  = NCBLISTEN;
	ncb.ncb_lana_num = (unsigned char) lana;

	ncb.ncb_rto = 0;
	ncb.ncb_sto = 0;

	memcpy(ncb.ncb_name, toNameBuf, NCBNAMSZ);

	/*
	 * Set the accepted client name, '*' for any client
	 */

	memcpy(ncb.ncb_callname, fromNameBuf, NCBNAMSZ);

	/*
	 * Release the Java buffers
	 */

	(*jnienv)->ReleaseByteArrayElements(jnienv, toName, toNameBuf, 0);
	(*jnienv)->ReleaseByteArrayElements(jnienv, fromName, fromNameBuf, 0);

	/*
	 * Wait for an incoming session request
	 */

	Netbios(&ncb);

	/*
	 * Return the session id if successful or -1 to indicate the listen failed
	 */

	if ( ncb.ncb_retcode == NRC_GOODRET) {

		/*
		 * Return the callers name, if available
		 */

		if ( ncb.ncb_callname[0] != '\0') {
			pBuffer = (*jnienv)->GetByteArrayElements(jnienv, callerName, 0);
			memcpy(pBuffer, ncb.ncb_callname, NCBNAMSZ);
			(*jnienv)->ReleaseByteArrayElements(jnienv, callerName, pBuffer, 0);
		}

		/*
		 * Return the session id
		 */

		return ncb.ncb_lsn;
	}

	/*
	 * Return the error status code as a negative number
	 */

	return -ncb.ncb_retcode;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    Receive
 * Signature: (I[BII)I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_Receive
  (JNIEnv* jnienv, jclass jthis, jint lana, jint lsn, jbyteArray jbuf, jint off, jint maxLen) {

	jbyte* pBuffer;
	int sts;

    /*
	 * Allocate an NCB
	 */

	NCB ncb;
	memset(&ncb, 0, sizeof(ncb));

	/*
	 * Build the receive request
	 */

	ncb.ncb_command  = NCBRECV;
	ncb.ncb_lana_num = (unsigned char) lana;
	ncb.ncb_lsn      = (unsigned char) lsn;

	ncb.ncb_rto = 0;
	ncb.ncb_sto = 0;

	pBuffer = (*jnienv)->GetByteArrayElements(jnienv, jbuf, 0);

	ncb.ncb_buffer = pBuffer + off;
	ncb.ncb_length = (unsigned short) (maxLen - off);

	/*
	 * Receive a packet of data
	 */

	Netbios(&ncb);

	/*
	 * Release the Java buffer and return the received data length, or a negative error
	 * code
	 */

	(*jnienv)->ReleaseByteArrayElements(jnienv, jbuf, pBuffer, 0);

	if ( ncb.ncb_retcode == NRC_GOODRET)
		return ncb.ncb_length;

	/*
	 * Return the error status code plus the received data length if its
	 * an incomplete read
	 */

	sts = ncb.ncb_retcode << 24;
	if ( ncb.ncb_retcode == NRC_INCOMP)
		sts += ncb.ncb_length;
	return sts;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    Send
 * Signature: (I[BII)I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_Send
  (JNIEnv* jnienv, jclass jthis, jint lana, jint lsn, jbyteArray jbuf, jint off, jint len) {

	jbyte* pBuffer;

  /*
	 * Allocate an NCB
	 */

	NCB ncb;
	memset(&ncb, 0, sizeof(ncb));

	/*
	 * Build the send request
	 */

	ncb.ncb_command  = NCBSEND;
	ncb.ncb_lana_num = (unsigned char) lana;
	ncb.ncb_lsn      = (unsigned char) lsn;

	ncb.ncb_rto = 0;
	ncb.ncb_sto = 0;

	pBuffer = (*jnienv)->GetByteArrayElements(jnienv, jbuf, 0);

	ncb.ncb_buffer = pBuffer + off;
	ncb.ncb_length = (unsigned short) len;

	/*
	 * Send a packet of data
	 */

	Netbios(&ncb);

	/*
	 * Release the Java buffer and return the sent data length, or -1
	 * for an error
	 */

	(*jnienv)->ReleaseByteArrayElements(jnienv, jbuf, pBuffer, 0);

	if ( ncb.ncb_retcode == NRC_GOODRET)
		return ncb.ncb_length;

	/*
	 * Return the error status code as a negative number
	 */

	return -ncb.ncb_retcode;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    SendDatagram
 * Signature: (IILjava/lang/String;[BII)I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_SendDatagram
	(JNIEnv* jnienv, jclass jthis, jint lana, jint srcNum, jbyteArray destName, jbyteArray jbuf, jint off, jint len) {

	jbyte* destNameBuf = (*jnienv)->GetByteArrayElements(jnienv, destName, 0);
	jbyte* pBuffer;

  /*
	 * Allocate an NCB
	 */

	NCB ncb;
	memset(&ncb, 0, sizeof(ncb));

	/*
	 * Build the send request
	 */

	ncb.ncb_command  = NCBDGSEND;
	ncb.ncb_lana_num = (unsigned char) lana;
	ncb.ncb_num      = (unsigned char) srcNum;

	ncb.ncb_rto = 0;
	ncb.ncb_sto = 0;

	memcpy(ncb.ncb_callname, destNameBuf, NCBNAMSZ);
	(*jnienv)->ReleaseByteArrayElements(jnienv, destName, destNameBuf, 0);

	pBuffer = (*jnienv)->GetByteArrayElements(jnienv, jbuf, 0);

	ncb.ncb_buffer = pBuffer + off;
	ncb.ncb_length = (unsigned short) len;

	/*
	 * Send a broadcast datagram packet
	 */

	Netbios(&ncb);

	/*
	 * Release the Java buffer and return the sent data length, or -1
	 * for an error
	 */

	(*jnienv)->ReleaseByteArrayElements(jnienv, jbuf, pBuffer, 0);

	if ( ncb.ncb_retcode == NRC_GOODRET)
		return ncb.ncb_length;

	/*
	 * Return the error status code as a negative number
	 */

	return -ncb.ncb_retcode;

}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    SendBroadcastDatagram
 * Signature: (II[BII)I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_SendBroadcastDatagram
  (JNIEnv* jnienv, jclass jthis, jint lana, jbyteArray jbuf, jint off, jint len) {

	jbyte* pBuffer;

  /*
	 * Allocate an NCB
	 */

	NCB ncb;
	memset(&ncb, 0, sizeof(ncb));

	/*
	 * Build the send request
	 */

	ncb.ncb_command  = NCBDGSENDBC;
	ncb.ncb_lana_num = (unsigned char) lana;
	ncb.ncb_num      = (unsigned char) 0xFF;

	ncb.ncb_rto = 0;
	ncb.ncb_sto = 0;

	pBuffer = (*jnienv)->GetByteArrayElements(jnienv, jbuf, 0);

	ncb.ncb_buffer = pBuffer + off;
	ncb.ncb_length = (unsigned short) len;

	/*
	 * Send a broadcast datagram packet
	 */

	Netbios(&ncb);

	/*
	 * Release the Java buffer and return the sent data length, or -1
	 * for an error
	 */

	(*jnienv)->ReleaseByteArrayElements(jnienv, jbuf, pBuffer, 0);

	if ( ncb.ncb_retcode == NRC_GOODRET)
		return ncb.ncb_length;

	/*
	 * Return the error status code as a negative number
	 */

	return -ncb.ncb_retcode;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    ReceiveDatagram
 * Signature: (II[BII)I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_ReceiveDatagram
	(JNIEnv* jnienv, jclass jthis, jint lana, jint nameNum, jbyteArray jbuf, jint off, jint len) {
	return -1;
}


/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    ReceiveBroadcastDatagram
 * Signature: (II[BII)I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_ReceiveBroadcastDatagram
  (JNIEnv* jnienv, jclass jthis, jint lana, jint nameNum, jbyteArray jbuf, jint off, jint maxLen) {

	jbyte* pBuffer;

  /*
	 * Allocate an NCB
	 */

	NCB ncb;
	memset(&ncb, 0, sizeof(ncb));

	/*
	 * Build the receive request
	 */

	ncb.ncb_command  = NCBDGRECVBC;
	ncb.ncb_lana_num = (unsigned char) lana;
	ncb.ncb_num      = (unsigned char) nameNum;
	ncb.ncb_rto = 0;
	ncb.ncb_sto = 0;

	pBuffer = (*jnienv)->GetByteArrayElements(jnienv, jbuf, 0);

	ncb.ncb_buffer = pBuffer + off;
	ncb.ncb_length = (unsigned short) (maxLen - off);

	/*
	 * Receive a datagram packet
	 */

	Netbios(&ncb);

	/*
	 * Release the Java buffer and return the received data length, or -1
	 * for an error
	 */

	(*jnienv)->ReleaseByteArrayElements(jnienv, jbuf, pBuffer, 0);

	if ( ncb.ncb_retcode == NRC_GOODRET)
		return ncb.ncb_length;

	/*
	 * Return the error status code as a negative number
	 */

	return -ncb.ncb_retcode;
}


/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    Hangup
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_Hangup
  (JNIEnv* jnienv, jclass jthis, jint lana, jint lsn) {

	/*
	 * Allocate an NCB
	 */

	NCB ncb;
	memset(&ncb, 0, sizeof(ncb));

	/*
	 * Build the hangup request
	 */

	ncb.ncb_command  = NCBHANGUP;
	ncb.ncb_lana_num = (unsigned char) lana;
	ncb.ncb_lsn      = (unsigned char) lsn;

	/*
	 * Hangup the session
	 */

	Netbios(&ncb);

	/*
	 * Return the status code
	 */

	return ncb.ncb_retcode;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    GetLocalNetBIOSName
 * Signature: ([BI)I
 */
JNIEXPORT jstring JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_GetLocalNetBIOSName
	(JNIEnv* jnienv, jclass jthis) {

	BOOL sts;
	wchar_t nameBuf[MAX_COMPUTERNAME_LENGTH + 1];
	unsigned int nameLen;

	/*
	 *	Get the local Windows NetBIOS name
	 */
	nameLen = sizeof(nameBuf);
	sts = GetComputerName((LPTSTR) nameBuf, (LPDWORD) &nameLen);

	/*
	 *	If the Win32 call was successful create a Java string from the name
	 */

	if ( sts == TRUE) {
		nameBuf[nameLen] = '\0';
		return (*jnienv)->NewString(jnienv, nameBuf, (jsize) nameLen);
	}
	return NULL;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    GetLocalDomainName
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_GetLocalDomainName
	(JNIEnv* jnienv, jclass jthis) {

	NET_API_STATUS sts = 0;
	DWORD dwLevel = 100;
	LPWKSTA_INFO_100 pWkstaInfo = NULL;
	jstring domainName = NULL;

	/*
	 *	Get the workstation information for the local system
	 */
	sts = NetWkstaGetInfo( NULL, dwLevel, (LPBYTE*) &pWkstaInfo);

	/*
	 *	If the request was successful get the local domain/workgroup name
	 */
	if ( sts == NERR_Success) {

		/*
		 * Allocate the return Java string
		 */
		domainName = (*jnienv)->NewString(jnienv, (const jchar*) pWkstaInfo->wki100_langroup,
			(jsize) wcslen((const wchar_t*) pWkstaInfo->wki100_langroup));
	}

	/*
	 *	Free the buffer allocated by the network API call
	 */
	if ( pWkstaInfo != NULL)
		NetApiBufferFree( pWkstaInfo);

	/*
	 *	Return the domain name
	 */
	return domainName;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    getWINSServerList
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_getWINSServerList
	(JNIEnv* jnienv, jclass jthis) {

		/*
		 *  Key/value names
		 */

		wchar_t* netbtKeyName   = L"System\\CurrentControlSet\\Services\\NetBT\\Parameters\\Interfaces";
		wchar_t* ifaceKeyPrefix = L"Tcpip_";
		wchar_t* winsListName   = L"NameServerList";
		wchar_t* dhcpListName   = L"DhcpNameServerList";

		/*
		 *	Key/value details
		 */

		HKEY netbtKey;
		HKEY ifaceKey;
		DWORD sts;
		DWORD keySts;
		DWORD keyIndex;

		wchar_t keyName[64];
		DWORD keyNameLen;

		BYTE valueBuf[128];
		DWORD valueLen;

		FILETIME lastWrite;

		wchar_t addrBuf[256];
		jstring addrList = NULL;

		/*
		 *	Clear the WINS server address list
		 */

		addrBuf[0] = 0;

		/*
		 *	Open the top-level registry key for the NetBT settings
		 */

		if ( RegOpenKeyEx( HKEY_LOCAL_MACHINE, netbtKeyName, 0, KEY_ENUMERATE_SUB_KEYS, &netbtKey) == ERROR_SUCCESS) {

			/*
			 *	Enumerate the interfaces
			 */

			sts = ERROR_SUCCESS;
			keyIndex = 0;

			while ( sts == ERROR_SUCCESS) {

				/*
				 *	Get sub-key information
				 */

				keyNameLen = sizeof(keyName);

				sts = RegEnumKeyEx( netbtKey, keyIndex++, keyName, &keyNameLen, NULL, NULL, NULL, &lastWrite);

				if ( sts != ERROR_SUCCESS)
					continue;

				/*
				 *	Check if it's a TCP/IP interface
				 */

				if ( keyNameLen > 0 && wcsncmp( keyName, ifaceKeyPrefix, sizeof(ifaceKeyPrefix)) == 0) {

					/*
					 *	Open the interface key
					 */

					if ( RegOpenKeyEx( netbtKey, keyName, 0, KEY_READ, &ifaceKey) == ERROR_SUCCESS) {

						/*
						 *	Get the WINS name server list, if available
						 */

						valueLen = sizeof( valueBuf);
						keySts = RegQueryValueEx( ifaceKey, winsListName, 0, NULL, valueBuf, &valueLen);

						if ( keySts == ERROR_SUCCESS && valueLen > 2)
							parseMultiSz(( const wchar_t*) valueBuf, addrBuf);

						/*
						 *	Get the DHCP server list, if available
						 */

						valueLen = sizeof( valueBuf);
						keySts = RegQueryValueEx( ifaceKey, dhcpListName, 0, NULL, valueBuf, &valueLen);

						if ( keySts == ERROR_SUCCESS && valueLen > 2)
							parseMultiSz(( const wchar_t*) valueBuf, addrBuf);

						/*
						 *	Close the interface key
						 */

						RegCloseKey( ifaceKey);
					}
				}
			}

			/*
			 *	Close the registry key
			 */

			RegCloseKey( netbtKey);
		}

		/*
		 * If the address list is not empty create the Java string to return
		 */

		if ( addrBuf[0] != 0) {

		  /*
			 * Allocate the return Java string
			 */
			addrList = (*jnienv)->NewString(jnienv, addrBuf, (jsize) wcslen(addrBuf));
		}

		/*
		 * Return the comma delimited address list, or null if no addresses were found
		 */

		return addrList;
	}

/**
 * Parse a REG_MULTI_SZ string value
 */
void parseMultiSz( const wchar_t* buf, wchar_t* outbuf) {

	unsigned int bufpos = 0;

	/*
	 *	Check if the input buffer is valid
	 */

	if ( buf == NULL || outbuf == NULL)
		return;

	/*
	 *	Buffer contains one or more null terminated strings with a null marking the end of the list
	 */

	while ( buf[bufpos] != 0) {

		/*
		 *	Append the current string to the output buffer
		 */

		wcscat_s( outbuf, wcslen( &buf[bufpos]), &buf[bufpos]);
		wcscat_s( outbuf, 1, L",");

		/*
		 *	Move the buffer pointer to the next string, or end of string list marker
		 */

		bufpos += (unsigned int) wcslen( &buf[bufpos]) + 1;
	}
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    InitializeSockets
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_InitializeSockets
	(JNIEnv* jnienv, jclass jthis)
{
	/*
	 * Initialize Winsock interface
	 */

	int sts = 0;
	WORD versionReq = MAKEWORD( 2, 2);
	WSADATA wsaData;

	sts = WSAStartup( versionReq, &wsaData);
	if ( sts != 0)
		throwWinsockException( jnienv, sts, "InitializeSockets");

	/*
	 * Allocate the shutdown event, release any previously allocated event
	 */

	if ( _shutdownEvent != 0)
		WSACloseEvent(_shutdownEvent);
	_shutdownEvent = WSACreateEvent();
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    ShutdownSockets
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_ShutdownSockets
	(JNIEnv* jnienv, jclass jthis)
{
	/*
	 * Set the shutdown event to unblock address change listeners
	 */

	SetEvent(_shutdownEvent);

	/*
	 * Cleanup Winsock
	 */

	WSACleanup();
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    CreateSocket
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_CreateSocket
	(JNIEnv* jnienv, jclass jthis, jint lana)
{
	/*
	 * Create a NetBIOS socket
	 */

	SOCKET nbSocket = socket(AF_NETBIOS, SOCK_SEQPACKET, -lana);

	/*
	 * Check for an error
	 */

	if ( nbSocket == INVALID_SOCKET)
		throwWinsockException(jnienv, WSAGetLastError(), "CreateSocket");

	/*
	 * Return the new socket
	 */

	return (jint) nbSocket;
}

/*
* Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
* Method:    CreateDatagramSocket
* Signature: (I)I
*/
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_CreateDatagramSocket
	(JNIEnv* jnienv, jclass jthis, jint lana)
{
	/*
	 * Create a NetBIOS datagram socket
	 */

	SOCKET nbSocket = socket(AF_NETBIOS, SOCK_DGRAM, -lana);

	/*
	 * Check for an error
	 */

	if ( nbSocket == INVALID_SOCKET)
		throwWinsockException(jnienv, WSAGetLastError(), "CreateDatagramSocket");

	/*
	 * Return the new socket
	 */

	return (jint) nbSocket;
}

/*
* Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
* Method:    BindSocket
* Signature: (I[B)I
*/
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_BindSocket
	(JNIEnv* jnienv, jclass jthis, jint sockPtr, jbyteArray nbname, jboolean fastAddName)
{
	jbyte* namePtr = (*jnienv)->GetByteArrayElements(jnienv, nbname, 0);
	int sts = 0;

	SOCKET sock = (SOCKET) sockPtr;
	SOCKADDR_NB localNbAddr;

	/*
	 * Bind the socket to a local NetBIOS name
	 */

	memset(&localNbAddr, 0, sizeof(SOCKADDR_NB));

	if ( fastAddName == 0) {
		SET_NETBIOS_SOCKADDR(&localNbAddr, NETBIOS_UNIQUE_NAME, namePtr, namePtr[15]);
	}
	else {
		SET_NETBIOS_SOCKADDR(&localNbAddr, NETBIOS_TYPE_QUICK_UNIQUE, namePtr, namePtr[15]);
	}

	(*jnienv)->ReleaseByteArrayElements(jnienv, nbname, namePtr, 0);

	/*
	 * Bind the socket to the NetBIOS name
	 */

	sts = bind( sock, (struct sockaddr*) &localNbAddr, sizeof(localNbAddr));
	if ( sts == SOCKET_ERROR)
		throwWinsockException(jnienv, WSAGetLastError(), "BindSocket (bind)");

	/*
	 * Listen for incoming connections
	 */

	sts = listen( sock, SOMAXCONN);
	if ( sts == SOCKET_ERROR)
		throwWinsockException(jnienv, WSAGetLastError(), "BindSocket (listen)");

	/*
	 * Return a success status
	 */

	return 0;
}

	/*
	* Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
	* Method:    ListenSocket
	* Signature: (I[B)I
	*/
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_ListenSocket
	(JNIEnv* jnienv, jclass jthis, jint sockPtr, jbyteArray callerName)
{
	jbyte* pBuffer = NULL;

	/*
	 * Accept the incoming connection, get the connection details
	 */

	SOCKADDR_NB remNbAddr;
	int addrLen = sizeof(remNbAddr);

	SOCKET sessSock = accept(( SOCKET) sockPtr, (struct sockaddr*) &remNbAddr, &addrLen);

	if ( sessSock == INVALID_SOCKET)
		throwWinsockException(jnienv, WSAGetLastError(), "ListenSocket (accept)");

	if ( addrLen > 0) {
		pBuffer = (*jnienv)->GetByteArrayElements(jnienv, callerName, 0);
		memcpy(pBuffer, remNbAddr.snb_name, NCBNAMSZ);
		(*jnienv)->ReleaseByteArrayElements(jnienv, callerName, pBuffer, 0);
	}

	/*
		* Return the new session socket
		*/
	return (jint) sessSock;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    ConnectSocket
 * Signature: (I[B)V
 */
JNIEXPORT void JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_ConnectSocket
  (JNIEnv* jnienv, jclass jthis, jint sockPtr, jbyteArray remoteName)
{
	jbyte* pBuffer = NULL;
	SOCKADDR_NB remNbAddr;

	/*
	 * Access the remote server name
	 */

	pBuffer = (*jnienv)->GetByteArrayElements(jnienv, remoteName, 0);

	/*
	 * Fill in the remote server details
	 */

	memset( &remNbAddr, 0, sizeof(SOCKADDR_NB));
	SET_NETBIOS_SOCKADDR( &remNbAddr, NETBIOS_UNIQUE_NAME, pBuffer, pBuffer[15]);
	(*jnienv)->ReleaseByteArrayElements(jnienv, remoteName, pBuffer, 0);

	/*
	 * Connect to the remote NetBIOS file server
	 */
	if ( connect(( SOCKET) sockPtr, (const struct sockaddr*) &remNbAddr, sizeof( remNbAddr)) == SOCKET_ERROR)
		throwWinsockException(jnienv, WSAGetLastError(), "ConnectSocket");
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    CloseSocket
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_CloseSocket
	(JNIEnv* jnienv, jclass jthis, jint sockPtr)
{
	/*
	 * Close the socket
	 */

	closesocket((SOCKET) sockPtr);
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    SendSocket
 * Signature: (I[BII)I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_SendSocket
	(JNIEnv* jnienv, jclass jthis, jint sockPtr, jbyteArray jbuf, jint off, jint len)
{
	/*
	 * Access the send buffer
	 */

	int sts = 0;
	jbyte* pBuffer = (*jnienv)->GetByteArrayElements(jnienv, jbuf, 0);

	/*
	 * Send a packet of data
	 */

	SOCKET sock = (SOCKET) sockPtr;
	sts = send( sock, (const char*) (pBuffer + off), len, 0);

	/*
	 * Release the Java buffer and return the sent data length
	 */

	(*jnienv)->ReleaseByteArrayElements(jnienv, jbuf, pBuffer, 0);

	if ( sts == SOCKET_ERROR) {

		/*
		 * Check if the error indicates that a non-blocking socket would block
		 */
		int wsts = WSAGetLastError();
		if ( wsts == WSAEWOULDBLOCK) {

			/*
			 * Return a status value to indicate the data could not be sent without the socket blocking
			 */
			return SocketWouldBlock;
		}
		else
			throwWinsockException(jnienv, wsts, "SendSocket");
	}

	/*
	 * Return the actual length of data written
	 */

	return sts;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    ReceiveSocket
 * Signature: (I[BII)I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_ReceiveSocket
	(JNIEnv* jnienv, jclass jthis, jint sockPtr, jbyteArray jbuf, jint off, jint maxlen)
{
	/*
	 * Access the receive buffer
	 */

	int sts = 0;
	jbyte* pBuffer = (*jnienv)->GetByteArrayElements(jnienv, jbuf, 0);

	/*
	 * Receive a packet of data
	 */

	SOCKET sock = (SOCKET) sockPtr;
	sts = recv( sock, (char*) (pBuffer + off), maxlen, 0);

	/*
	 * Release the Java buffer and return the received data length
	 */

	(*jnienv)->ReleaseByteArrayElements(jnienv, jbuf, pBuffer, 0);

	if ( sts == SOCKET_ERROR) {
		int wsts = WSAGetLastError();
		if ( wsts != WSAEMSGSIZE)
			throwWinsockException(jnienv, WSAGetLastError(), "ReceiveSocket");
		else {
			if ( off > 4)
				throwWinsockException(jnienv, WSAGetLastError(), "ReceiveSocket");
			else
				sts = ReceiveErrorMask;
		}
	}

	/*
	 * Return the actual length of data received
	 */

	return sts;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    ReceiveLengthSocket
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_ReceiveLengthSocket
  (JNIEnv* jnienv, jclass jthis, jint sockPtr)
{
	long rxLen;

	/*
	 * Get the available read data length for the socket
	 */
	if ( ioctlsocket((SOCKET) sockPtr, FIONREAD, (u_long*) &rxLen) == 0)
		return rxLen;
	else {

		/*
		 * Return the error
		 */
		throwWinsockException(jnienv, WSAGetLastError(), "ReceiveLengthSocket");
		return 0;
	}
}

/*
* Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
* Method:    SendSocketDatagram
* Signature: (I[B[BII)I
*/
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_SendSocketDatagram
	(JNIEnv* jnienv, jclass jthis, jint sockPtr, jbyteArray jtoName, jbyteArray jbuf, jint off, jint len)
{
	jbyte* pName = NULL;
	jbyte* pBuffer = NULL;
	int sts = 0;
	SOCKADDR_NB groupAddr;
	SOCKET sock = 0;

	/*
	 * Access the to name
	 */

	pName = (*jnienv)->GetByteArrayElements(jnienv, jtoName, 0);
	SET_NETBIOS_SOCKADDR(&groupAddr, NETBIOS_GROUP_NAME, pName, pName[15]);

	(*jnienv)->ReleaseByteArrayElements(jnienv, jtoName, pName, 0);

	/*
	 * Access the send buffer
	 */

	pBuffer = (*jnienv)->GetByteArrayElements(jnienv, jbuf, 0);

	/*
	 * Send a datagram
	 */

	sock = (SOCKET) sockPtr;
	sts = sendto( sock, (const char*) (pBuffer + off), len, 0, (struct sockaddr*) &groupAddr, sizeof(groupAddr));

	/*
	 * Release the Java buffer and return the sent data length
	 */

	(*jnienv)->ReleaseByteArrayElements(jnienv, jbuf, pBuffer, 0);

	if ( sts == SOCKET_ERROR)
		throwWinsockException(jnienv, WSAGetLastError(), "SendSocketDatagram");

	/*
	 * Return the actual length of data written
	 */

	return sts;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    SetNonBlockingSocket
 * Signature: (IZ)I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_SetNonBlockingSocket
  (JNIEnv* jnienv, jclass jthis, jint sockPtr, jboolean nonBlocking)
{
	SOCKET sock = (SOCKET) sockPtr;
	int sockMode = 0;
	int sts = 0;

	/*
	 * Set the blocking/non-blocking mode
	 */

	if ( nonBlocking == 0)
		sockMode = 1;

	/*
	 * Set the socket non-blocking I/O mode
	 */

	sts = ioctlsocket( sock, FIONBIO, (u_long*) &sockMode);
	if ( sts == SOCKET_ERROR)
		throwWinsockException(jnienv, WSAGetLastError(), "SetNonBlockingSocket");

	/*
	 * Return the new non-blocking mode
	 */

	return sockMode;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    SelectReceiveSockets
 * Signature: (I[I[I)I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_SelectReceiveSockets
  (JNIEnv* jnienv, jclass jthis, jint jsockCnt, jintArray jreadSocksIn, jintArray jreadSocksOut)
{
	jint* pReadSocksIn = NULL;
	jsize readArrayLen = 0;
	int sts = 0;
	fd_set readfds;
    int readCnt = 0;
	unsigned int idx = 0;

	/*
	 * Access the list of sockets to wait for a read event
	 */

	pReadSocksIn = (*jnienv)->GetIntArrayElements( jnienv, jreadSocksIn, 0);
	readArrayLen = (*jnienv)->GetArrayLength( jnienv, jreadSocksIn);

	/*
	 * Copy the read socket details to the FS_SET structure
	 */
	for ( idx = 0; idx < (unsigned int) jsockCnt; idx++) {

		/*
		 * Check if the current read socket is valid, add it to the set of sockets to wait for read events
		 */

		readfds.fd_array[ readCnt++] = pReadSocksIn[ idx];
	}

	/*
	 * Release the input array
	 */

	(*jnienv)->ReleaseIntArrayElements(jnienv, jreadSocksIn, pReadSocksIn, 0);

	/*
	 * Set the cound of read sockets to monitor for events
	 */

	readfds.fd_count = readCnt;

	/*
	 * Wait for a read event on one or more sockets
	 */

	sts = select( 0, &readfds, NULL, NULL, NULL);
	if ( sts == SOCKET_ERROR)
		throwWinsockException(jnienv, WSAGetLastError(), "SelectSockets");

	/*
	 * Copy the list of triggered sockets to the output array
	 */

	(*jnienv)->SetIntArrayRegion( jnienv, jreadSocksOut, 0, ( jsize) readfds.fd_count, (jint*) &readfds.fd_array[0]);

	/*
	 * Return the count of triggered sockets
	 */

	return readfds.fd_count;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    GetMaximumSocketsPerSelect
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_GetMaximumSocketsPerSelect
  (JNIEnv* jnienv, jclass jthis)
{
	return MaxSocketsPerSelect;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    Win32CreateEvent
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_Win32CreateEvent
  (JNIEnv* jnienv, jclass jthis)
{
	/*
	 * Create a new Win32 event
	 */

	jint eventHandle = (jint) CreateEvent( NULL, FALSE, FALSE, NULL);

	if ( eventHandle == (jint) INVALID_HANDLE_VALUE)
		throwException( jnienv, GetLastError(), "CreateEvent");

	/*
	 * Return the new event handle
	 */

	return eventHandle;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    Win32CloseEvent
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_Win32CloseEvent
  (JNIEnv* jnienv, jclass jthis, jint eventHandle)
{
	/*
	 * Close a Win32 event
	 */

	if ( CloseHandle((HANDLE) eventHandle) == 0) {

		/*
		 * Error closing the event handle
		 */

		throwException( jnienv, GetLastError(), "CloseEvent");
	}
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    Win32SetEvent
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_Win32SetEvent
  (JNIEnv* jnienv, jclass jthis, jint eventHandle)
{
	/*
	 * Set a Win32 event state to signalled
	 */

	if ( SetEvent((HANDLE) eventHandle) != 0)
		return TRUE;
	return FALSE;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    Win32ResetEvent
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_Win32ResetEvent
  (JNIEnv* jinenv, jclass jthis, jint eventHandle)
{
	/*
	 * Reset a Win32 event state to unsignalled
	 */

	if ( ResetEvent((HANDLE) eventHandle) != 0)
		return TRUE;
	return FALSE;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    WinsockCreateEvent
 * Signature: ()J
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_WinsockCreateEvent
  (JNIEnv* jnienv, jclass jthis)
{
	/*
	 * Create a Winsock event
	 */

	jint eventHandle = (jint) WSACreateEvent();
	if ( eventHandle == (jint) WSA_INVALID_EVENT)
		throwWinsockException( jnienv, WSAGetLastError(), "WinsockCreateEvent");

	/*
	 * Return the event handle
	 */

	return eventHandle;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    WinsockSetEvent
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_WinsockSetEvent
  (JNIEnv* jnienv, jclass jthis, jint eventHandle)
{
	/*
	 * Set a Winsock event state to signalled
	 */

	if ( WSASetEvent((HANDLE) eventHandle) != 0)
		return TRUE;
	return FALSE;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    WinsockResetEvent
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_WinsockResetEvent
  (JNIEnv* jnienv, jclass jthis, jint eventHandle)
{
	/*
	 * Reset a Winsock event state to unsignalled
	 */

	if ( WSAResetEvent((HANDLE) eventHandle) != 0)
		return TRUE;
	return FALSE;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    WinsockCloseEvent
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_WinsockCloseEvent
  (JNIEnv* jnienv, jclass jthis, jint eventHandle)
{
	/*
	 * Close a Winsock event handle
	 */

	if ( WSACloseEvent((HANDLE) eventHandle) == 0)
		throwWinsockException( jnienv, WSAGetLastError(), "WinsockCloseEvent");
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    WinsockWaitForMultipleEvents
 * Signature: (I[IZIZ)I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_WinsockWaitForMultipleEvents
  (JNIEnv* jnienv, jclass jthis, jint eventCnt, jintArray jevents, jboolean waitAll, jint timeout, jboolean alertable)
{
	jint* pEvents = NULL;
	jint eventId = 0;

	/*
	 * Access the list of events
	 */

	pEvents = (*jnienv)->GetIntArrayElements( jnienv, jevents, 0);

	/*
	 * Wait for an event to be triggered
	 */

	do {

		/*
		 * Wait for an event
		 */

		eventId = WSAWaitForMultipleEvents( eventCnt, (HANDLE*) pEvents, waitAll, timeout, alertable);

	} while ( eventId == WSA_WAIT_IO_COMPLETION);

	/*
	 * Release the input array
	 */

	(*jnienv)->ReleaseIntArrayElements(jnienv, jevents, pEvents, 0);

	/*
	 * Check if an event signalled or error occurred
	 */

	if ( eventId == WSA_WAIT_FAILED)
		throwWinsockException( jnienv, WSAGetLastError(), "WinsockWaitForMultipleEvents");

	/*
	 * Return the index of the event that triggered
	 */

	return eventId;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    WinsockEventSelect
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_WinsockEventSelect
  (JNIEnv* jnienv, jclass jthis, jint sockPtr, jint eventHandle, jint eventMask)
{
	if ( WSAEventSelect( sockPtr, (HANDLE) eventHandle, eventMask) == SOCKET_ERROR)
		throwWinsockException( jnienv, WSAGetLastError(), "WinsockEventSelect");
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    WinsockEnumNetworkEvents
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_WinsockEnumNetworkEvents
  (JNIEnv* jnienv, jclass jthis, jint sockPtr, jint eventHandle)
{
	WSANETWORKEVENTS netEvents;
	jint eventMask = 0;
	int idx = 0;

	/*
	 * Get the event data for the socket
	 */

	if ( WSAEnumNetworkEvents( sockPtr, (HANDLE) eventHandle, &netEvents) == 0) {

		/*
		 * Get the network event mask for the socket
		 */

		eventMask = netEvents.lNetworkEvents;

		/*
		 * Check if there are any errors, report the first found via an exception
		 */

		for ( idx = 0; idx < FD_MAX_EVENTS; idx++) {
			if (( eventMask & ( 1 << idx)) != 0 && netEvents.iErrorCode[idx] != 0)
				throwWinsockException( jnienv, netEvents.iErrorCode[ idx], "EnumSocketError");
		}
	}
	else
		throwWinsockException( jnienv, WSAGetLastError(), "WinsockEnumNetworkEvents");

	/*
	 * Return the triggered event(s) mask
	 */

	return eventMask;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    getSocketReceiveBufferSize
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_getSocketReceiveBufferSize
  (JNIEnv* jnienv, jclass jthis, jint sockPtr)
{
	int bufSize = 0;
	int optLen = sizeof( bufSize);

	/*
	 * Get the NetBIOS socket receive buffer size
	 */
	if ( getsockopt( sockPtr, SOL_SOCKET, SO_RCVBUF, (char*) &bufSize, &optLen) == SOCKET_ERROR)
		throwWinsockException( jnienv, WSAGetLastError(), "getSocketReceiveBufferSize");

	/*
	 * Return the buffer size
	 */

	return bufSize;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    setSocketReceiveBufferSize
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_setSocketReceiveBufferSize
  (JNIEnv* jnienv, jclass jthis, jint sockPtr, jint bufSize)
{
	int optLen = sizeof( bufSize);

	/*
	 * Set the NetBIOS socket receive buffer size
	 */
	if ( setsockopt( sockPtr, SOL_SOCKET, SO_RCVBUF, (char*) &bufSize, optLen) == SOCKET_ERROR)
		throwWinsockException( jnienv, WSAGetLastError(), "setSocketReceiveBufferSize");
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    getSocketSendBufferSize
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_getSocketSendBufferSize
  (JNIEnv* jnienv, jclass jthis, jint sockPtr)
{
	int bufSize = 0;
	int optLen = sizeof( bufSize);

	/*
	 * Get the NetBIOS socket send buffer size
	 */
	if ( getsockopt( sockPtr, SOL_SOCKET, SO_SNDBUF, (char*) &bufSize, &optLen) == SOCKET_ERROR)
		throwWinsockException( jnienv, WSAGetLastError(), "getSocketSendBufferSize");

	/*
	 * Return the buffer size
	 */
	return bufSize;
}

/*
 * Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
 * Method:    setSocketSendBufferSize
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_setSocketSendBufferSize
  (JNIEnv* jnienv, jclass jthis, jint sockPtr, jint bufSize)
{
	int optLen = sizeof( bufSize);

	/*
	 * Set the NetBIOS socket send buffer size
	 */
	if ( setsockopt( sockPtr, SOL_SOCKET, SO_SNDBUF, (char*) &bufSize, optLen) == SOCKET_ERROR)
		throwWinsockException( jnienv, WSAGetLastError(), "setSocketSendBufferSize");
}

/*
* Class:     org_alfresco_jlan_netbios_win32_Win32NetBIOS
* Method:    waitForNetworkAddressChange
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_org_alfresco_jlan_netbios_win32_Win32NetBIOS_waitForNetworkAddressChange
  (JNIEnv* jnienv, jclass jthis) {

		DWORD sts = 0;
		OVERLAPPED overLap;
		WSAEVENT events[2];
		HANDLE handle;

		/*
		 * Register for the network address change notifications
		 */

		memset(&overLap, 0, sizeof(overLap));

		events[0] = _shutdownEvent;
		events[1] = WSACreateEvent();

		overLap.hEvent = events[1];

		sts = NotifyAddrChange( &handle, &overLap);

		/*
		 * Wait for either a network address change or shutdown event
		 */

		sts = WaitForMultipleObjects(2, &events[0], FALSE, INFINITE);

		/*
		 * Delete the address change event
		 */

		WSACloseEvent( events[1]);
}

/**
 * Create a WinsockNetBIOSException with the specified status code
 */
void throwWinsockException(JNIEnv* jnienv, int winsockErr, const char* msg) {

	char msgbuf[64];
	jclass exceptionClass = NULL;

	/*
	 * Create the error message using the status code
	 */

	sprintf_s(msgbuf, sizeof( msgbuf), "%u:%s", winsockErr, msg);

	/*
	 * Create the Winsock NetBIOS exception object
	 */

	exceptionClass = (*jnienv)->FindClass(jnienv, "org/alfresco/jlan/netbios/win32/WinsockNetBIOSException");
	if ( exceptionClass == NULL)
		return;

	/*
	 * Throw the Java exception
	 */

	(*jnienv)->ThrowNew( jnienv, exceptionClass, msgbuf);
}

/**
 * Create an Exception with the specified status message
 */
void throwException(JNIEnv* jnienv, int stsCode, const char* msg) {

	char msgbuf[64];
	jclass exceptionClass = NULL;

	/*
	 * Create the error message using the status code
	 */

	sprintf_s(msgbuf, sizeof( msgbuf), "%u:%s", stsCode, msg);

	/*
	 * Create the exception object
	 */

	exceptionClass = (*jnienv)->FindClass(jnienv, "java/lang/Exception");
	if ( exceptionClass == NULL)
		return;

	/*
	 * Throw the Java exception
	 */

	(*jnienv)->ThrowNew( jnienv, exceptionClass, msgbuf);
}
