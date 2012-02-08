/*
 * Copyright (C) 2006-2010 Alfresco Software Limited.
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

package org.alfresco.jlan.ftp;

/**
 * FTP Command Types Class
 *
 * @author gkspencer
 */
public class FTPCommand {

	//	Command ids
	
	public final static int User		= 0;
	public final static int Pass		= 1;
	public final static int Acct		= 2;
	public final static int Cwd			= 3;
	public final static int Cdup		= 4;
	public final static int Smnt		= 5;
	public final static int Rein		= 6;
	public final static int Quit		= 7;
	public final static int Port		= 8;
	public final static int Pasv		= 9;
	public final static int Type		= 10;
	public final static int Stru		= 11;
	public final static int Mode		= 12;
	public final static int Retr		= 13;
	public final static int Stor		= 14;
	public final static int Stou		= 15;
	public final static int Appe		= 16;
	public final static int Allo		= 17;
	public final static int Rest		= 18;
	public final static int Rnfr		= 19;
	public final static int Rnto		= 20;
	public final static int Abor		= 21;
	public final static int Dele		= 22;
	public final static int Rmd			= 23;
	public final static int Mkd			= 24;
	public final static int Pwd			= 25;
	public final static int List		= 26;
	public final static int Nlst		= 27;
	public final static int Site		= 28;
	public final static int Syst		= 29;
	public final static int Stat		= 30;
	public final static int Help		= 31;
	public final static int Noop		= 32;
	public final static int Mdtm		= 33;
	public final static int Size		= 34;
	public final static int Opts		= 35;
	public final static int Feat		= 36;
	public final static int XPwd		= 37;
	public final static int XMkd		= 38;
	public final static int XRmd		= 39;
	public final static int XCup		= 40;
	public final static int XCwd		= 41;
  
	public final static int MLst        = 42;
	public final static int MLsd        = 43;

	public final static int EPrt        = 44;
	public final static int EPsv        = 45;
  
	public final static int Auth        = 46;
	public final static int Pbsz        = 47;
	public final static int Prot        = 48;
	public final static int Ccc         = 49;
	
	public final static int Mfmt		= 50;
  
	public final static int MaxId		= 50;
	
	public final static int InvalidCmd	= -1;
	
	//	Command name strings
	
	private static final String[] _cmds = {	"USER",	"PASS",	"ACCT",	"CWD",
											"CDUP",	"SMNT",	"REIN",	"QUIT",
											"PORT",	"PASV",	"TYPE",	"STRU",
											"MODE",	"RETR",	"STOR",	"STOU",
											"APPE",	"ALLO",	"REST",	"RNFR",
											"RNTO",	"ABOR",	"DELE",	"RMD",
											"MKD",	"PWD",	"LIST",	"NLST",
											"SITE",	"SYST",	"STAT",	"HELP",
											"NOOP",	"MDTM",	"SIZE",	"OPTS",
											"FEAT",	"XPWD",	"XMKD",	"XRMD",
											"XCUP",	"XCWD", "MLST", "MLSD",
											"EPRT", "EPSV", "AUTH", "PBSZ",
											"PROT", "CCC",  "MFMT"
	};
	
	/**
	 * Convert an FTP command to an id
	 * 
	 * @param cmd String
	 * @return int
	 */
	public final static int getCommandId(String cmd) {
		
		//	Check if the command is valid
		
		if ( cmd == null)
			return InvalidCmd;
			
		//	Convert to a command id
		
		for ( int i = 0; i <= MaxId; i++)
			if ( _cmds[i].equalsIgnoreCase(cmd))
				return i;
				
		//	Command not found
		
		return InvalidCmd;
	}
	
	/**
	 * Return the FTP command name for the specified id
	 * 
	 * @param id int
	 * @return String
	 */
	public final static String getCommandName(int id) {
		if ( id < 0 || id > MaxId)
			return null;
		return _cmds[id];
	}
}
