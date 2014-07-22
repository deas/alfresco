/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.webscripts;

import org.springframework.extensions.webscripts.Description.RequiredTransaction;
import org.springframework.extensions.webscripts.Description.TransactionCapability;


/**
 * Web Script Transaction Parameters
 *
 * Records the desired transaction requirements for the Web Script
 */
public class TransactionParameters implements Description.RequiredTransactionParameters
{
    private RequiredTransaction required;
    private TransactionCapability capability;
    private int bufferSize = 4096;

    
    /**
     * Construct
     */
    public TransactionParameters()
    {
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.web.scripts.Description.RequiredTransactionParameters#getCapbility()
     */
    public TransactionCapability getCapability()
    {
        return capability;
    }

    /**
     * Sets capability
     * 
     * @param capability
     */
    public void setCapability(TransactionCapability capability)
    {
        this.capability = capability;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.web.scripts.Description.RequiredTransactionParameters#getRequired()
     */
    public RequiredTransaction getRequired()
    {
        return required;
    }
    
    /**
     * Sets required
     * 
     * @param required
     */
    public void setRequired(RequiredTransaction required)
    {
        this.required = required;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.web.scripts.Description.RequiredTransactionParameters#getBufferSize()
     */
    public int getBufferSize()
    {
        return bufferSize;
    }

    /**
     * Sets buffer size
     * 
     * @param bufferSize
     */
    public void setBufferSize(int bufferSize)
    {
        this.bufferSize = bufferSize;
    }

}
