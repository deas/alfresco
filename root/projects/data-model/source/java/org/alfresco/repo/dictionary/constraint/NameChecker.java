/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.repo.dictionary.constraint;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.dictionary.Constraint;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.springframework.beans.factory.InitializingBean;

/**
 * Loads cm:filename constraint from dictionary to use it during batch jobs
 * 
 * @see <a href=https://issues.alfresco.com/jira/browse/MNT-9414>MNT-9414</a>
 * 
 * @author Viachaslau Tsikhanovich *
 */
public class NameChecker implements InitializingBean
{
    private DictionaryService dictionaryService;

    private Constraint nameConstraint;
    
    public DictionaryService getDictionaryService()
    {
        return dictionaryService;
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    /**
     * Loads filename constraint from dictionary
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        PropertyCheck.mandatory(this, "dictionaryService", dictionaryService);

        QName qNameConstraint = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "filename");
        ConstraintDefinition constraintDef = dictionaryService.getConstraint(qNameConstraint);
        if (constraintDef == null)
        {
            throw new AlfrescoRuntimeException("Constraint definition does not exist: " + qNameConstraint);
        }
        nameConstraint = constraintDef.getConstraint();
        if (nameConstraint == null)
        {
            throw new AlfrescoRuntimeException("Constraint does not exist: " + qNameConstraint);
        }
    }

    public void evaluate(Object value)
    {
        nameConstraint.evaluate(value);
    }

}
