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
package org.alfresco.repo.lotus.rs.error;

/**
 * @author EugeneZh
 */
public enum QuickrError
{
    ITEM_NOT_FOUND(404, 1, "ItemNotFound"),

    UNKNOWN(500, 2, "Unknown"),
    
    COSNTRAIN_TVIOLATION(500, 3, "ConstraintViolation"),

    ITEM_EXISTS(409, 4, "ItemExists"),
    
    ACCESS_DENIED(403, 5, "AccessDenied");

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
