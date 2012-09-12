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
package org.alfresco.solr.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.BasicPermissions;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AccessControlEntryImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AccessControlPrincipalDataImpl;

/**
 * @author Andy
 *
 */
public class CMISDataCreator
{
    public static void main(String[] args)
    {
        String folderName = "Folder-"+System.currentTimeMillis();
        
        String user = getArg(args, "user", String.class, "admin");
        String pwd = getArg(args, "pwd", String.class, "admin");
        String url = getArg(args, "url", String.class, "http://localhost:8080/alfresco/cmisatom");
        
        SessionFactory factory = SessionFactoryImpl.newInstance();
        Map<String, String> parameter = new HashMap<String, String>();
        parameter.put(SessionParameter.USER, user);
        parameter.put(SessionParameter.PASSWORD, pwd);
        parameter.put(SessionParameter.ATOMPUB_URL, url);
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        
        List<Repository> repositories = factory.getRepositories(parameter);
        Session session = repositories.get(0).createSession();
        
        Folder root = session.getRootFolder();
        
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        properties.put(PropertyIds.NAME, folderName);

        // create the folder
        Folder newFolder = root.createFolder(properties);
        
        for(int i = 0; i < 1000; i++)
        {
            AccessControlPrincipalDataImpl principal = new AccessControlPrincipalDataImpl("user"+i);
            List<String> permissions = new ArrayList<String>(1);
            permissions.add(BasicPermissions.READ);
            List<Ace> addAces = new ArrayList<Ace>(1);
            addAces.add(new AccessControlEntryImpl(principal, permissions));
            newFolder.addAcl(addAces, AclPropagation.PROPAGATE);
            
            Map<String, Object> updateProperties = new HashMap<String, Object>();
            updateProperties.put("cm:title", "Update title "+i);
            newFolder.updateProperties(properties);
            
            if(i % 10 == 0)
            {
                System.out.println("@ "+i);
            }
        }
        
    }
    
    public static <T> T getArg(String[] args, String key, Class<T> c, T defaultValue)
    {
        String target = "-" + key;
        int i = 0;
        while (i < args.length)
        {
            if (args[i].equals(target))
            {
                i++;
                if (i == args.length || args[i].length() == 0)
                {
                    throw new AlfrescoRuntimeException("The value <"+key+"> for the option -"+key+" must be specified");
                }
                return DefaultTypeConverter.INSTANCE.convert(c, args[i]);
            }
            i++;
        }
        return defaultValue;
    }
    
}
