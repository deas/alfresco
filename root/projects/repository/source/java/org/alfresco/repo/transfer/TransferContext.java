/*
 * Copyright (C) 2009-2010 Alfresco Software Limited.
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
package org.alfresco.repo.transfer;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.transfer.manifest.ManifestCategory;
import org.alfresco.service.cmr.repository.NodeRef;

public class TransferContext
{

    private Map<NodeRef, ManifestCategory> categoriesCache = new HashMap<NodeRef, ManifestCategory>();
    /**
      * 
      * @return
      */
    public Map<NodeRef, ManifestCategory> getManifestCategoriesCache()
    {
      	return this.categoriesCache;
    }
}
