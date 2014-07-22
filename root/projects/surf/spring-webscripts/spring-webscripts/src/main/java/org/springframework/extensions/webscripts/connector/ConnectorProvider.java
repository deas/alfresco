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

package org.springframework.extensions.webscripts.connector;

import org.springframework.extensions.surf.exception.ConnectorProviderException;

/**
 * Contract to be implemented by any object that can provide a Connector to another object.
 * <p>
 * Connectors are quite expensive to instantiate and obtain. Therefore this contract is
 * generally used where you want to lazily provide a connector in a situation where it is
 * not know if a connector is actually required (maybe due to caching etc.) until runtime.
 * 
 * @author Kevin Roast
 */
public interface ConnectorProvider
{
	/**
	 * Builds a Connector object mounted against the given endpoint
	 * 
	 * @param endpoint
	 * 
	 * @return the connector object
	 */
    public Connector provide(String endpoint) throws ConnectorProviderException;
}