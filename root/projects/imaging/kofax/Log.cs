using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Text;

namespace KofaxAlfrescoRelease_v1
{
    class Log
    {

        private string sLogFormat;
        private string sErrorTime;

        public Log()
        {
            //sLogFormat used to create log files format :
            // dd/mm/yyyy hh:mm:ss AM/PM ==> Log Message
            sLogFormat = DateTime.Now.ToShortDateString().ToString() + " " + DateTime.Now.ToLongTimeString().ToString() + " ==> ";

            //this variable used to create log filename format "
            //for example filename : ErrorLogYYYYMMDD
            string sYear = DateTime.Now.Year.ToString();
            string sMonth = DateTime.Now.Month.ToString();
            string sDay = DateTime.Now.Day.ToString();
            sErrorTime = sYear + sMonth + sDay + ".log";
        }

        public void ErrorLog(string sPathName, string sErrMsg, String stackTrace)
        {

            if (!Directory.Exists(sPathName))
            {
                Directory.CreateDirectory(sPathName);
            }

            StreamWriter sw = new StreamWriter(sPathName + " " + sErrorTime, true);
            sw.WriteLine(sLogFormat + sErrMsg + "\n" + stackTrace);
            sw.Flush();
            sw.Close();
        }


    }
}
