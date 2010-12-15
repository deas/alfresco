using System;
using System.Collections.Generic;
using System.Text;

namespace AlfrescoWord2007
{
   public interface IServerHelper
   {
      string GetAlfrescoPath(string documentPath);
      string GetAuthenticationTicket();
      string GetAuthenticationTicket(string Username, string Password);
   }
}
