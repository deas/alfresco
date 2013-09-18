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

package org.alfresco.repo.workflow.jbpm;

import org.mozilla.javascript.NativeObject;

/**
 * @since 3.4
 * @author Nick Smith
 *
 */
public class NullScope extends NativeObject
{
    private static final long serialVersionUID = 423800883354854893L;

    private static final NullScope INSTANCE = new NullScope();
    
    public static NullScope instance()
    {
        return INSTANCE;
    }
    
    @Override public Object getDefaultValue(@SuppressWarnings("rawtypes") Class hint)
    {
        return INSTANCE.toString();
    }
}
