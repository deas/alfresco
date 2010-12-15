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
using System;
using System.Net;
using System.Net.Security;
using System.ServiceModel;
using System.Collections.Generic;
using System.Security.Cryptography.X509Certificates;
using System.Configuration;
using System.Reflection;
using WcfCmisWSTests.CmisServices;
using System.IO;

///
/// author: Stas Sokolovsky
///
namespace WcfCmisWSTests
{
    public class CmisLogger
    {
        private const int MAX_COUNT_IN_LINE = 76;

        private const string LEVEL_PREFIX = " |";

        private static Dictionary<string, CmisLogger> loggers = new Dictionary<string, CmisLogger>();

        private int currentLoggingLevel = 1;

        private static int generalLoggingLevel = 1;

        private static string filename = null;

        private string objectname;

        private CmisLogger(string objectname)
        {
            this.objectname = objectname;
        }

        public static CmisLogger getInstance()
        {
            return getInstance("");
        }

        public static CmisLogger getInstance(string objectname)
        {
            CmisLogger result = null;
            if (!loggers.TryGetValue(objectname, out result))
            {
                result = new CmisLogger(objectname);
                loggers.Add(objectname, result);
            }
            return (CmisLogger)result;
        }

        public static CmisLogger getInstance(int level)
        {
            string objectname = "";
            for (int i = 1; i < level; i++) objectname += LEVEL_PREFIX;
            CmisLogger logger = getInstance(objectname);
            logger.CurrentLoggingLevel = level;
            return logger;
        }

        public void log(string s)
        {
            if (currentLoggingLevel > generalLoggingLevel) return;
            if (!"".Equals(objectname) && !s.StartsWith(objectname))
            {
                s = objectname + s;
            }
            if (s.Length >= MAX_COUNT_IN_LINE)
            {
                log(s.Substring(0, MAX_COUNT_IN_LINE - 1));
                log(s.Substring(MAX_COUNT_IN_LINE - 1, s.Length - MAX_COUNT_IN_LINE + 1));
            }
            else
            {
                writeLine("  " + s);
            }
        }

        public void log()
        {
            if (currentLoggingLevel > generalLoggingLevel) return;
            writeLine("\r\n");
        }

        private void writeLine(string s)
        {
            Console.WriteLine(s);

            if (filename != null)
            {
                StreamWriter sw = File.AppendText(filename);
                sw.WriteLine(s);
                sw.Flush();
                sw.Close();
            }
        }

        public int CurrentLoggingLevel
        {
            get { return currentLoggingLevel; }
            set { currentLoggingLevel = value; }
        }

        public static int GeneralLoggingLevel
        {
            get { return CmisLogger.generalLoggingLevel; }
            set { CmisLogger.generalLoggingLevel = value; }
        }

        public static string Filename
        {
            get { return CmisLogger.filename; }
            set
            {
                CmisLogger.filename = value;
                FileInfo file = new FileInfo(CmisLogger.filename);
                if (file.Exists)
                {
                    file.Delete();
                }
            }
        }

    }
}