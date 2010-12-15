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

package org.alfresco.module.vti.metadata.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PavelYur
 *
 */
public class MeetingsInformation
{
    
    private boolean allowCreate = true;
    
    private List<Integer> templateLanguages;
    
    private List<MwsTemplate> templates;
    
    private MwsStatus status;
    
    public MeetingsInformation()
    {   
    }
    
    public void setAllowCreate(boolean allowCreate)
    {
        this.allowCreate = allowCreate;
    }
    
    public boolean isAllowCreate()
    {
        return allowCreate;
    }
    
    public List<Integer> getTemplateLanguages()
    {
        if (templateLanguages == null)
        {
            templateLanguages = new ArrayList<Integer>();
        }
        
        return templateLanguages;
    }
    
    public List<MwsTemplate> getTemplates()
    {
        if (templates == null)
        {
            templates = new ArrayList<MwsTemplate>();
        }
        return templates;
    }
    
    public void setStatus(MwsStatus status)
    {
        this.status = status;
    }
    
    public MwsStatus getStatus()
    {
        return status;
    }
}