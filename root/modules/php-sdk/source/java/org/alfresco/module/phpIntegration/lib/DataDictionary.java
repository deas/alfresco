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
package org.alfresco.module.phpIntegration.lib;

import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.QName;

/**
 * The PHP dictionary service API.
 * 
 * @author Roy Wetherall
 */
public class DataDictionary implements ScriptObject
{
    /** Script extension name */
    private static final String SCRIPT_OBJECT_NAME = "DataDictionary";
    
    /** The session */
    private Session session;
    
    /** The dictionary service */
    private DictionaryService dictionaryService;
    
    /**
     * Constructor
     * 
     * @param session   the session
     */
    public DataDictionary(Session session)
    {
        this.session = session;
        this.dictionaryService = this.session.getServiceRegistry().getDictionaryService();
    }
    
    /**
     * @see org.alfresco.module.phpIntegration.lib.ScriptObject#getScriptObjectName()
     */
    public String getScriptObjectName()
    {
        return SCRIPT_OBJECT_NAME;
    }

    /**
     * Determines whether one class is a sub type of an other.  Returns true if it is, false otherwise.
     * 
     * @param clazz         the class to test
     * @param subTypeOf     test whether the class is a sub-type of this class
     * @return boolean      true if it is a sub-class, false otherwise
     */
    public boolean isSubTypeOf(final String clazz, final String subTypeOf)
    {
    	Boolean result = this.session.doSessionWork(new SessionWork<Boolean>()
    	{
			public Boolean doWork() 
			{
		        // Convert to full names if required
		        String fullClazz = DataDictionary.this.session.getNamespaceMap().getFullName(clazz);
		        String fullSubTypeOf = DataDictionary.this.session.getNamespaceMap().getFullName(subTypeOf);
		        
		        // Create the QNames for the passes classes
		        QName className = QName.createQName(fullClazz);
		        QName ofClassName = QName.createQName(fullSubTypeOf);
		        
		        // Return the result
		        return new Boolean(DataDictionary.this.dictionaryService.isSubClass(className, ofClassName));
			}
    	});
    	
    	return result.booleanValue();
    }
    
}
