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
*  File    DefaultAVMResourceBinding.java
*----------------------------------------------------------------------------*/


package org.alfresco.catalina.host;
import java.util.Map;
import java.util.regex.Matcher;
import org.alfresco.jndi.AVMFileDirContext;
import org.alfresco.repo.domain.PropertyValue;
import org.alfresco.service.cmr.remote.AVMRemote;
import org.alfresco.service.namespace.QName;

public class      DefaultAVMResourceBinding 
       implements        AVMResourceBinding
{

    /**
    *  @exclude (hide from javadoc)
    */
    static protected AVMRemote AVMRemote_;

    /**
    *  Fetch the name of the virtual repository indicated 
    *  by data obtained when the reverseProxyBinding regex 
    *  was matched within the AVMUrlValve.  The default
    *  regex binding specified by AVMHost is:
    *  <pre>
    *   "^(.+)\\.www--sandbox\\.(?:version--v(-?[\\d]+)\\.)?.*$"
    *  </pre>
    */
    public String getRepositoryName(Matcher match)
    {
        String host_info = match.group(1);         // "^(.+)\\.www--sandbox\\."
        if (host_info == null) { host_info = ""; }

        String repo = getRepoNameFromHostInfo( host_info );

        // System.out.println("Mapped host: " + host_info + " to repo: " + repo);

        return repo;
    }

    /**
    *  Fetch the version of the resource indicated by
    *  data obtained when the reverseProxyBinding 
    *  regex was matched within the AVMUrlValve.
    *  <p>
    *  Note: "-1" corresponds to the HEAD version.
    */
    public String getVersion(Matcher match)
    {
        String version       = match.group(2);    //   v-(...)
        if (version == null) { version = "-1"; }  // Default:  -1 == HEAD
        return version;
    }


    /**
    *  @exclude (hide from javadoc)
    */
    static String getRepoNameFromHostInfo( String host_info ) 
    {
        // Lazy init is necessary here due to library loading order
        if ( AVMRemote_ == null ) { AVMRemote_ = AVMFileDirContext.getAVMRemote(); }

        // Fetch the repo corresponding to the   ".dns.<host_info>" key 
        //
        String repo_name;
        try
        {
            Map<String, Map<QName, PropertyValue>> repo_dns_entries = 
               AVMRemote_.queryStoresPropertyKey(
               QName.createQName(null,".dns." + host_info));
               repo_name = repo_dns_entries.keySet().iterator().next(); 
        }
        catch (Exception e) { repo_name = ""; }

        return repo_name;
    }
}
