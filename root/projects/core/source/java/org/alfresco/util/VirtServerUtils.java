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
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.*  
*  
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    VirtServerUtils.java
*----------------------------------------------------------------------------*/

package org.alfresco.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.alfresco.config.JNDIConstants;


public class VirtServerUtils
{
    // patterns for WEB-INF files that require virtualisation server reload

    private final static Pattern WEB_INF_PATH_PATTERN = 
        Pattern.compile( 
            "[^:]+"                                 +   // valid store name
            ":"                                     +   // store delim
            "/" + JNDIConstants.DIR_DEFAULT_WWW     +   // overlay dir ("/www")
            "/" + JNDIConstants.DIR_DEFAULT_APPBASE +   // the host app base
            "/" + "[^/]+"                           +   // the webapp name
            "/WEB-INF"                              +   // jars,classes,web.xml
            "("                                     +   // 
               "/"                                  +   // Trigger on submit of
                "("                                 +   // classes, jars in the
                     "(classes(/.*)?)"              +   // lib dir and/or the
                     "|"                            +   // entire contents of
                     "(lib(/.*)?)"                  +   // these dirs.  Also,
                     "|"                            +   // trigger on web.xml
                     "(web\\.xml)"                  +   // within WEB-INF, and
                ")"                                 +   // the entire WEB-INF
            ")?"                                        // directory itself.
           , Pattern.CASE_INSENSITIVE);                 // Whew!

   /**
    * @param path    Path to match against
    * 
    * @return true if the path should require a virtualisation 
    *              server reload, false otherwise
    */
   public static boolean requiresUpdateNotification(String path)
   {
      if (path == null || path.length() == 0)
      {
         throw new IllegalArgumentException("Path value is mandatory.");
      }
      
      return WEB_INF_PATH_PATTERN.matcher(path).matches();
   }
}

