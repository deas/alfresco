/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.repo.lotus.rs.error;

/**
 * @author EugeneZh
 */
public enum QuickrError
{
    ITEM_NOT_FOUND(404, 3, "ItemNotFound"),

    ITEM_EXISTS(409, 4, "ItemExists");

    private int responseStatus;

    private int errorCode;

    private String errorId;

    QuickrError(int responseStatus, int errorCode, String errorId)
    {
        this.responseStatus = responseStatus;
        this.errorCode = errorCode;
        this.errorId = errorId;
    }

    public int getResponseStatus()
    {
        return responseStatus;
    }

    public int getErrorCode()
    {
        return errorCode;
    }

    public String getErrorId()
    {
        return errorId;
    }

    public static String getErrorIdByErrorCode(int errorCode)
    {
        QuickrError errors[] = values();
        for (QuickrError quickrError : errors)
        {
            if (quickrError.errorCode == errorCode)
            {
                return quickrError.errorId;
            }
        }
        return "";
    }
}
