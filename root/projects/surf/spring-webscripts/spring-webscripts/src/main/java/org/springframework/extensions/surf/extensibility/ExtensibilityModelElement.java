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
package org.springframework.extensions.surf.extensibility;

import java.util.Collection;

/**
 * <p>Any classes that implement this interface can be legitimately added to the extensibility model. The interface
 * itself is relatively loose in that only getter methods for id and type need to be provided. In reality this only
 * provides a common interface to support the use of generics in the model which is essentially just a {@link Collection}.
 * 
 * @author David Draper
 *
 */
public interface ExtensibilityModelElement
{
    public String getId();
    public String getType();
    public String getDirectiveName();
}
