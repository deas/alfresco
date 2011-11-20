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

import java.util.Map;

import org.alfresco.module.vti.metadata.dic.VtiProperty;

/**
 * <p>Bean class that store all meta-information about the Sharepoint item (file or folder)</p>
 * 
 * @author Michael Shavnev
 */
public class DocMetaInfo
{
    private String id;
    private boolean folder;
    private String path;

    // FOLDER & FILES
    private String thicketdir;
    private String timecreated;
    private String timelastmodified;
    private String timelastwritten;

    // FOLDER
    private String dirlateststamp;
    private String hassubdirs;
    private String isbrowsable;
    private String ischildweb;
    private String isexecutable;
    private String isscriptable;
    private String listbasetype;

    // FILE
    private String title;
    private String filesize;
    private String metatags;
    private String sourcecontrolcheckedoutby;
    private String sourcecontroltimecheckedout;
    private String thicketsupportingfile;
    private String sourcecontrollockexpires;
    private String sourcecontrolcookie;
    private String sourcecontrolversion;
    private String author;
    private String modifiedBy;

    // Office 2008/2011 for Mac related properties
    private String listname;
    private String rtag;
    private String etag;

    /**     
     * <p>Identifies the uniqueId of the item.</p>
     * 
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }
    
    /**     
     * @return the unique id 
     */
    public String getId()
    {
        return id;
    }
    
    /**     
     * @return the author 
     */
    public String getAuthor()
    {
        return author;
    }

    /**     
     * <p>Identifies the creator of the item.</p>
     * 
     * @param author the author to set
     */
    public void setAuthor(String author)
    {
        this.author = author;
    }

    /**     
     * @return the modifiedBy 
     */
    public String getModifiedBy()
    {
        return modifiedBy;
    }
    
    /**
     * <p>Identifies the person who last made changes to the item.</p>
     * 
     * @param modifiedBy modifiedBy to set
     */
    public void setModifiedBy(String modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }
    
    /**
     * <p>Constructor</p>
     * 
     * @param folder is folder? to set
     */
    public DocMetaInfo(boolean folder)
    {
        this.folder = folder;
    }

    /**     
     * @return <i>true</i> if is folder and <i>false</i> otherwise 
     */
    public boolean isFolder()
    {
        return folder;
    }

    /**     
     * @return the thicketdir
     */
    public String getThicketdir()
    {
        return thicketdir;
    }
    
    /**
     * 
     * @param thicketdir the thicketdir to set
     */
    public void setThicketdir(String thicketdir)
    {
        this.thicketdir = thicketdir;
    }
    
    /**
     * 
     * @return the timecreated
     */
    public String getTimecreated()
    {
        return timecreated;
    }
    
    /**
     * <p>The time, including date, at which the current item was created.</p>
     * 
     * @param timecreated the timecreated to set
     */
    public void setTimecreated(String timecreated)
    {
        this.timecreated = timecreated;
    }
    
    /**
     * 
     * @return the timelastmodified
     */
    public String getTimelastmodified()
    {
        return timelastmodified;
    }
    
    /**
     * <p>The time, including date, at which the current item last changed.</p>
     * 
     * @param timelastmodified the timelastmodified to set
     */
    public void setTimelastmodified(String timelastmodified)
    {
        this.timelastmodified = timelastmodified;
    }
    
    /**
     * 
     * @return the timelastwritten
     */
    public String getTimelastwritten()
    {
        return timelastwritten;
    }
    
    /**
     * <p>The time, including date, at which the current item was last saved to disk.</p>
     * 
     * @param timelastwritten the timelastwritten to set
     */
    public void setTimelastwritten(String timelastwritten)
    {
        this.timelastwritten = timelastwritten;
    }
    
    /**
     * 
     * @return the dirlateststamp
     */
    public String getDirlateststamp()
    {
        return dirlateststamp;
    }
    
    /**
     * <p>With respect to the metadata for the list folder, the date and time at which the current item last had changes made to it.</p>
     * 
     * @param dirlateststamp the dirlateststamp to set
     */
    public void setDirlateststamp(String dirlateststamp)
    {
        this.dirlateststamp = dirlateststamp;
    }
    
    /**
     * 
     * @return <i>true</i> if the specified directory includes subdirectories; otherwise, <i>false</i>
     */
    public String getHassubdirs()
    {
        return hassubdirs;
    }
    
    /**
     * <p><i>true</i> if the specified directory includes subdirectories; otherwise, <i>false</i>.</p>
     * 
     * @param hassubdirs the hassubdirs to set
     */
    public void setHassubdirs(String hassubdirs)
    {
        this.hassubdirs = hassubdirs;
    }
    
    /**
     * 
     * @return <i>true</i> if users can browse files in the current directory; otherwise, <i>false</i>
     */
    public String getIsbrowsable()
    {
        return isbrowsable;
    }
    
    /**
     * <p><i>true</i> if users can browse files in the current directory; otherwise, <i>false</i>.</p>
     * 
     * @param isbrowsable the isbrowsable to set
     */
    public void setIsbrowsable(String isbrowsable)
    {
        this.isbrowsable = isbrowsable;
    }
    
    /**      
     * @return <i>true</i> if the folder is the root directory of a subsite; otherwise, <i>false</i>
     */
    public String getIschildweb()
    {
        return ischildweb;
    }
    
    /**
     * <p><i>true</i> if the folder is the root directory of a subsite; otherwise, <i>false</i>.</p>
     * 
     * @param ischildweb the ischildweb to set
     */
    public void setIschildweb(String ischildweb)
    {
        this.ischildweb = ischildweb;
    }
    
    /**  
     * @return  <i>true</i> if the server can execute programs in the current directory; otherwise, <i>false</i>
     */
    public String getIsexecutable()
    {
        return isexecutable;
    }
    
    /**
     * <p><i>true</i> if the server can execute programs in the current directory; otherwise, <i>false</i>.</p>
     * 
     * @param isexecutable the isexecutable to set
     */
    public void setIsexecutable(String isexecutable)
    {
        this.isexecutable = isexecutable;
    }
    
    /**
     * 
     * @return <i>true</i> if the server can execute scripts in the current directory; otherwise, <i>false</i>
     */
    public String getIsscriptable()
    {
        return isscriptable;
    }
    
    /**
     * <p><i>true</i> if the server can execute scripts in the current directory; otherwise, <i>false</i>.</p>
     * 
     * @param isscriptable the isscriptable to set
     */
    public void setIsscriptable(String isscriptable)
    {
        this.isscriptable = isscriptable;
    }
    
    /**     
     * 
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }
    
    /**
     * <p>Can be a string that is the title of the item.</p>
     * 
     * @param title the title to set
     */
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    /**
     * 
     * @return the filesize
     */
    public String getFilesize()
    {
        return filesize;
    }
    
    /**
     * <p>The size of the current file in bytes.</p>
     * 
     * @param filesize the filesize to set
     */
    public void setFilesize(String filesize)
    {
        this.filesize = filesize;
    }
    
    /**
     * 
     * @return the metatags
     */
    public String getMetatags()
    {
        return metatags;
    }
    
    /**
     * <p>Meta-data for the current Web site.</p>
     * 
     * @param metatags to set
     */
    public void setMetatags(String metatags)
    {
        this.metatags = metatags;
    }
    
    /**
     * 
     * @return the sourcecontrolcheckedoutby
     */
    public String getSourcecontrolcheckedoutby()
    {
        return sourcecontrolcheckedoutby;
    }
    
    /**
     * <p>The value is the user name of the user that opened the item under multi-user source control.</p>
     * 
     * @param sourcecontrolcheckedoutby the sourcecontrolcheckedoutby to set
     */
    public void setSourcecontrolcheckedoutby(String sourcecontrolcheckedoutby)
    {
        this.sourcecontrolcheckedoutby = sourcecontrolcheckedoutby;
    }
    
    /**
     * 
     * @return the sourcecontroltimecheckedout
     */
    public String getSourcecontroltimecheckedout()
    {
        return sourcecontroltimecheckedout;
    }
    
    /**
     * <p>The time, including date, at which the item was opened under source control.</p>
     * 
     * @param sourcecontroltimecheckedout the sourcecontroltimecheckedout to set
     */
    public void setSourcecontroltimecheckedout(String sourcecontroltimecheckedout)
    {
        this.sourcecontroltimecheckedout = sourcecontroltimecheckedout;
    }
    
    /**
     * 
     * @return the thicketsupportingfile
     */
    public String getThicketsupportingfile()
    {
        return thicketsupportingfile;
    }
    
    /**
     *  
     * @param thicketsupportingfile the thicketsupportingfile to set
     */
    public void setThicketsupportingfile(String thicketsupportingfile)
    {
        this.thicketsupportingfile = thicketsupportingfile;
    }

    /**
     * 
     * @return path
     */
    public String getPath()
    {
        return path;
    }

    /**
     * <p>The site relative path of the item.</p>
     * 
     * @param path the path to set
     */
    public void setPath(String path)
    {
        this.path = path;
    }

    /**
     * @return the listbasetype
     */
    public String getListbasetype()
    {
        return listbasetype;
    }

    /**
     *
     * Specifies which of several supported base List types is used for the List
     * associated with this folder.
     *
     * @param listbasetype the listbasetype to set
     */
    public void setListbasetype(String listbasetype)
    {
        this.listbasetype = listbasetype;
    }

    /**
     * <p>Sets all properties from given map.</p>
     * 
     * @param properties map of the propperties to set
     */
    public void setDocInfoProperties(Map<String, String> properties)
    {
        for (String key : properties.keySet())
        {
            // set properties common for FOLDER and FILE
            if (key.equals(VtiProperty.FILE_TIMELASTMODIFIED.toString()))
            {
                this.setTimelastmodified(properties.get(key));
            }
            else if (key.equals(VtiProperty.FILE_TIMELASTWRITTEN.toString()))
            {
                this.setTimelastwritten(properties.get(key));
            }
            else if (key.equals(VtiProperty.FILE_TIMECREATED.toString()))
            {
                this.setTimecreated(properties.get(key));
            }
            else if (key.equals(VtiProperty.FILE_THICKETDIR.toString()))
            {
                this.setThicketdir(properties.get(key));
            }
            else if (key.equals(VtiProperty.FILE_LISTNAME.toString()))
            {
                this.setListName(properties.get(key));
            }
            else if (key.equals(VtiProperty.FILE_RTAG.toString()))
            {
                this.setRtag(properties.get(key));
            }
            else if (key.equals(VtiProperty.FILE_ETAG.toString()))
            {
                this.setEtag(properties.get(key));
            }

            // set FOLDER properties
            if (this.isFolder())
            {
                if (key.equals(VtiProperty.FOLDER_DIRLATESTSTAMP.toString()))
                {
                    this.setDirlateststamp(properties.get(key));
                }
                else if (key.equals(VtiProperty.FOLDER_HASSUBDIRS.toString()))
                {
                    this.setHassubdirs(properties.get(key));
                }
                else if (key.equals(VtiProperty.FOLDER_ISBROWSABLE.toString()))
                {
                    this.setIsbrowsable(properties.get(key));
                }
                else if (key.equals(VtiProperty.FOLDER_ISCHILDWEB.toString()))
                {
                    this.setIschildweb(properties.get(key));
                }
                else if (key.equals(VtiProperty.FOLDER_ISEXECUTABLE.toString()))
                {
                    this.setIsexecutable(properties.get(key));
                }
                else if (key.equals(VtiProperty.FOLDER_ISEXECUTABLE.toString()))
                {
                    this.setIsexecutable(properties.get(key));
                }
                else if (key.equals(VtiProperty.FOLDER_ISSCRIPTABLE.toString()))
                {
                    this.setIsscriptable(properties.get(key));
                }
            }
            else
            {   // set FILE properties
                if (key.equals(VtiProperty.FILE_FILESIZE.toString()))
                {
                    this.setFilesize(properties.get(key));
                }
                else if (key.equals(VtiProperty.FILE_METATAGS.toString()))
                {
                    this.setMetatags(properties.get(key));
                }
                else if (key.equals(VtiProperty.FILE_SOURCECONTROLCHECKEDOUTBY.toString()))
                {
                    this.setSourcecontrolcheckedoutby(properties.get(key));
                }
                else if (key.equals(VtiProperty.FILE_SOURCECONTROLTIMECHECKEDOUT.toString()))
                {
                    this.setSourcecontroltimecheckedout(properties.get(key));
                }
                else if (key.equals(VtiProperty.FILE_TITLE.toString()))
                {
                    this.setTitle(properties.get(key));
                }
                else if (key.equals(VtiProperty.FILE_THICKETSUPPORTINGFILE.toString()))
                {
                    this.setThicketsupportingfile(properties.get(key));
                }
            }
        }
    }

    /**
     * 
     * @return the sourcecontrollockexpires
     */
    public String getSourcecontrollockexpires()
    {
        return sourcecontrollockexpires;
    }

    /**
     * <p>Returns the time at which the internal light weight source control (LWSC) lock 
     * of Microsoft FrontPage expires. If a file is checked out using LWSC with 
     * a client other than FrontPage, the short term lock is set for 10 minutes. 
     * The lock is automatically renewed until the file is closed.</p>
     * 
     * @param sourcecontrollockexpires the sourcecontrollockexpires to set
     */
    public void setSourcecontrollockexpires(String sourcecontrollockexpires)
    {
        this.sourcecontrollockexpires = sourcecontrollockexpires;
    }

    /**
     * 
     * @return the sourcecontrolcookie
     */
    public String getSourcecontrolcookie()
    {
        return sourcecontrolcookie;
    }

    /**
     * <p>Returns the contents of a small text file, called a cookie, that can 
     * only be accessed by the page that creates it. In the example, the cookie
     *  contains a string that identifies the type of source control in use by the current site item.</p>
     * 
     * @param sourcecontrolcookie the sourcecontrolcookie to set
     */
    public void setSourcecontrolcookie(String sourcecontrolcookie)
    {
        this.sourcecontrolcookie = sourcecontrolcookie;
    }

    /**
     * 
     * @return the sourcecontrolversion
     */
    public String getSourcecontrolversion()
    {
        return sourcecontrolversion;
    }

    /**
     * <p>The value is the version number of the source control system used by the current Web site.</p>
     * 
     * @param sourcecontrolversion the sourcecontrolversion to set
     */
    public void setSourcecontrolversion(String sourcecontrolversion)
    {
        this.sourcecontrolversion = sourcecontrolversion;
    }
    
    /**
     * <p>Sets the "name" of the list this item is part of</p>
     * 
     * Note that in Alfresco, the "list name" is actually based
     *  on the ID of the list node, rather than the name property. 
     * 
     * @param listname The List Name to set
     */
    public void setListName(String listname)
    {
        this.listname = listname;
    }
    
    /**
     * Returns the name of the list that this item is part of.
     * 
     * Note that for Alfresco, this is actually based on the ID 
     *  of the list node, rather than the name property of the 
     *  list node, to both ensure uniqueness and better match
     *  what SharePoint itself does. 
     * 
     * @return the "name" (ID) of the list this belongs to 
     */
    public String getListName()
    {
        return listname;
    }
    
    /**
     * <p>The resource tag of the item</p>
     * 
     * @param rtag the resource tag to set
     */
    public void setRtag(String rtag)
    {
        this.rtag = rtag;
    }
    
    /**
     * 
     * @return the resource tag of the item
     */
    public String getRtag()
    {
        return rtag;
    }
    
    /**
     * <p>The entity tag of the item</p>
     * 
     * @param etag the entity tag to set
     */
    public void setEtag(String etag)
    {
        this.etag = etag;
    }
    
    /**
     * 
     * @return the entity tag of the item
     */
    public String getEtag()
    {
        return etag;
    }
}
