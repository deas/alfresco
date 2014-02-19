package org.alfresco.po.share.enums;

import org.apache.commons.lang3.StringUtils;

/**
*Enum to contain all the view Type 
* @author Chiran
* @author Shan Nagarajan
*/
public enum ViewType
{
    SIMPLE_VIEW("Simple"),
    DETAILED_VIEW("Detailed"),
    GALLERY_VIEW("Gallery"),
    FILMSTRIP_VIEW("Filmstrip"),
    TABLE_VIEW("Table"),
    AUDIO_VIEW("Audio"),
    MEDIA_VIEW("Media");
       
   private  String name;
    
    private ViewType(String name) 
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    /**
     * Finds the view type based on the name passed.
     * 
     * @param name
     * @return {@link ViewType}
     */
    public static ViewType getViewType(String name)
    {
    	if(StringUtils.isEmpty(name))
    	{
    		throw new IllegalArgumentException("Name can't be empty.");
    	}
    	
    	if (name.contains(SIMPLE_VIEW.getName()))
    	{
    		return SIMPLE_VIEW;
    	}
    	else if (name.contains("Detailed"))
    	{
    		return DETAILED_VIEW;
    	}
    	else if (name.contains("Gallery"))
    	{
    		return GALLERY_VIEW;
    	}
    	else if (name.contains("Filmstrip"))
    	{
    		return FILMSTRIP_VIEW;
    	}
    	else if (name.contains("Table"))
    	{
    		return TABLE_VIEW;
    	}
    	else if (name.contains("Audio"))
    	{
    		return AUDIO_VIEW;
    	}
    	else if (name.contains("Media"))
    	{
    		return MEDIA_VIEW;
    	}
    	
    	throw new IllegalArgumentException("Not able to find the view type for give name: " + name);
    }
    
}