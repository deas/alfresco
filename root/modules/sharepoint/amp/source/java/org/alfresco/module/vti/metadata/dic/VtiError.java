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

package org.alfresco.module.vti.metadata.dic;

/**
 * <p>Enum of the standard errors that may be raised by the protocol usage.</p>
 * 
 * @author Michael Shavnev
 * @author Dmitry Lazurkin
 */
public enum VtiError
{
    NOT_FOUND (10, "Not found."),
    ITEM_NOT_FOUND (-1, "Could not find the specified item"),
    LIST_NOT_FOUND (-1, "Could not find the specified list"),
    NO_PERMISSIONS (3, "You do not have permissions to that"),
    ALREADY_EXISTS (-1, "The object already exists"),
    DOES_NOT_EXIST (-1, "The object doesn't exist"),

    WRITE_ERROR (0x0002000C, "Write error on file."),
    CANNOT_RENAME_DEST_EXISTS (0x00020019, "Cannot rename : destination already exists."),
    FILE_ALREADY_EXISTS (0x00090002, "A file already exists."),
    V_BAD_URL (0x00090005, "The provided URL is invalid."),
    V_URL_NOT_FOUND (0x00090006, "There is no file with URL in this Web."),
    PRIMARY_PARENT_NOT_EXIST (0x00090007, "The folder that would hold URL does not exist on the server."),
    FOLDER_ALREADY_EXISTS (0x0009000D, "A folder already exisits."),
    V_DOC_CHECKED_OUT (0x0009000E, "The file is checked out or locked for editing."),
    V_DOC_NOT_CHECKED_OUT (0x0009000F, "The file is not checked out."),
    SOME_FILES_AUTO_CHECKEDOUT (0x0009001E, "Some files have been automatically checked out from the source control repository."),
    V_URL_DIR_NOT_FOUND (0x00090007, "The folder does not exist. Please create the folder and then retry the operation."),
    V_DOC_IS_LOCKED (0x00090040, "The specified file is currently in use."),
    V_FILE_OPEN_FOR_WRITE (0x00020002, "The file cannot be opened for writing."),
    V_REMOVE_FILE (0x00020007, "The file could not be removed. "),
    V_REMOVE_DIRECTORY (0x00020004, "The directory could not be removed."),
    V_HAS_ILLEGAL_CHARACTERS (0x00090070, "The file or folder name contains characters that are not permitted. Only alphanumeric characters and underscore allowed."),

    V_OWSSVR_ERRORACCESSDENIED (0x001E0002, "Access denied."),
    V_OWSSVR_ERRORSERVERINCAPABLE (0x001E0006, "The server does not support this capability. "),
    
    V_VERSION_NOT_FOUND (0x80131600 , "The version could not be found."),
    
    V_UNDEFINED (0x0000000, "Undefined error");

    private final String message;
    private final int errorCode;

    VtiError(int errorCode, String message)
    {
        this.errorCode = errorCode;
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

    public int getErrorCode()
    {
        return errorCode;
    }

}
