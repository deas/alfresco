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

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for base description document which maps to a physical XML document
 * 
 * @author drq
 *
 */
public interface BaseDescriptionDocument extends BaseDescription 
{
    /**
     * Gets the root path of the store of this base description document
     * 
     * @return  root path of store
     */
    public String getStorePath();

    /**
     * Gets the path of the description xml document for this base description document
     * 
     * @return  document location (path)
     */
    public String getDescPath();

    /**
     * Gets the description xml document for this base description document
     * 
     * @return  source document
     */
    public InputStream getDescDocument()
        throws IOException;
}
