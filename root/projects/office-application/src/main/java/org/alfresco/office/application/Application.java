package org.alfresco.office.application;
/**
* Enums to hold all the applications name.
* 
* @author Subashni Prasanna
*/
public enum Application
{
        WORD("Word") ,
        EXCEL("Excel"),
        POWERPOINT("PowerPoint"),
        OUTLOOK("Outlook");
           
       private  String application;
        
        private Application(String type) 
        {
            application = type;
        }
        
        public String getApplication()
        {
            return application;
        }
}
