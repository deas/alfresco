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
 * <p>Enum of the standard meta-keys of the frontpage protocol that is used in alfresco implementation.</p> 
 *
 * @author Michael Shavnev
 *
 */
public enum VtiProperty
{
    // FOLDER & FILES
    FILE_THICKETDIR ("vti_thicketdir"),
    FILE_TIMECREATED ("vti_timecreated"),
    FILE_TIMELASTMODIFIED ("vti_timelastmodified"),
    FILE_TIMELASTWRITTEN  ("vti_timelastwritten"),

    // FOLDER
    FOLDER_DIRLATESTSTAMP ("vti_dirlateststamp"),
    FOLDER_HASSUBDIRS     ("vti_hassubdirs"),
    FOLDER_ISBROWSABLE    ("vti_isbrowsable"),
    FOLDER_ISCHILDWEB     ("vti_ischildweb"),
    FOLDER_ISEXECUTABLE   ("vti_isexecutable"),
    FOLDER_ISSCRIPTABLE   ("vti_isscriptable"),

    /**
     * Specifies which of several supported base List types is used for the List
     * associated with this folder.
     */
    FOLDER_LISTBASETYPE   ("vti_listbasetype"),

    // FILE
    FILE_TITLE ("vti_title"),
    FILE_FILESIZE ("vti_filesize"),
    FILE_METATAGS  ("vti_metatags"),
    FILE_SOURCECONTROLCHECKEDOUTBY ("vti_sourcecontrolcheckedoutby"),
    FILE_SOURCECONTROLTIMECHECKEDOUT ("vti_sourcecontroltimecheckedout"),
    FILE_THICKETSUPPORTINGFILE ("vti_thicketsupportingfile"),
    FILE_SOURCECONTROLLOCKEXPIRES ("vti_sourcecontrollockexpires"),    
    FILE_SOURCECONTROLVERSION ("vti_sourcecontrolversion"),
    FILE_AUTHOR("vti_author"),
    FILE_MODIFIEDBY("vti_modifiedby"),
    
    // Required by the Office 2008/2011 for Mac implementation
    FILE_RTAG ("vti_rtag"),
    FILE_ETAG ("vti_etag"),
    FILE_LISTNAME("vti_listname"),
    
    // SERVICE
    SERVICE_CASESENSITIVEURLS ("vti_casesensitiveurls"),
    SERVICE_LONGFILENAMES ("vti_longfilenames"),
    SERVICE_SHOWHIDDENPAGES ("vti_showhiddenpages"),
    SERVICE_TITLE ("vti_title"),
    SERVICE_WELCOMENAMES ("vti_welcomenames"),
    SERVICE_USERNAME ("vti_username"),
    SERVICE_SERVERTZ ("vti_servertz"),
    SERVICE_SOURCECONTROLSYSTEM ("vti_sourcecontrolsystem"),
    SERVICE_SOURCECONTROLVERSION ("vti_sourcecontrolversion"),
    SERVICE_DOCLIBWEBVIEWENABLED ("vti_doclibwebviewenabled"),
    SERVICE_SOURCECONTROLCOOKIE ("vti_sourcecontrolcookie"),
    SERVICE_SOURCECONTROLPROJECT ("vti_sourcecontrolproject");

    private final String value;

    VtiProperty(String value)
    {
        this.value = value;
    }

    public String toString()
    {
        return value;
    }
}
