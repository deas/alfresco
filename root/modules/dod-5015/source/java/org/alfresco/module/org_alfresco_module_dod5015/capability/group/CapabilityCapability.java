/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_dod5015.capability.group;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * @author andyh
 *
 */
public class CapabilityCapability extends AbstractGroupCapability
{

    /* (non-Javadoc)
     * @see org.alfresco.module.org_alfresco_module_dod5015.capability.impl.AbstractCapability#hasPermissionImpl(org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected int hasPermissionImpl(NodeRef nodeRef)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.alfresco.module.org_alfresco_module_dod5015.capability.Capability#getName()
     */
    public String getName()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
