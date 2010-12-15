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
package org.alfresco.module.vti.metadata.dialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Custom type to storing list of the files and folders.
 * Used in dialogview method (FileOpen/Save) to retrieve all items from current folder.</p>
 * 
 * @author PavelYur
 */
public class DialogsMetaInfo implements Serializable
{
    private static final long serialVersionUID = 5024942474191917006L;

    private List<DialogMetaInfo> dialogMetaInfoList = new ArrayList<DialogMetaInfo>();    

    /**
     * @return the dialogMetaInfoList
     */
    public List<DialogMetaInfo> getDialogMetaInfoList()
    {
        return dialogMetaInfoList;
    }    
}
