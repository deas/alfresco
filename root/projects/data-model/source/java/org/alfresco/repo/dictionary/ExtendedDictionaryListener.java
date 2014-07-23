/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.repo.dictionary;

/**
 * A dictionary listener that allows listeners to listen for models being added to
 * the dictionary. Used by the OpenCMIS dictionary to refresh its registry when new models
 * are added to the core dictionary.
 * 
 * @author sglover
 *
 */
public interface ExtendedDictionaryListener extends DictionaryListener
{
    void modelAdded(CompiledModel model, String tenant);
}
