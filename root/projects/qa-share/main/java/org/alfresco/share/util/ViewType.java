package org.alfresco.share.util;

/**
*Enum to contain all the view Type 
* @author Subashni Prasanna
*/
public enum ViewType
{
    SIMPLE("Simple View"),
    DETAILED("Detailed View"),
    NONE("Default");
       
   private  String viewType ;
    
    private ViewType(String type) 
    {
        viewType = type;
    }
    
    public String getViewType()
    {
        return viewType;
    }
}
