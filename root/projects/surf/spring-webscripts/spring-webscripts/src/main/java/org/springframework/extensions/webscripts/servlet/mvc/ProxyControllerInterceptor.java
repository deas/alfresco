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

package org.springframework.extensions.webscripts.servlet.mvc;

import org.springframework.extensions.config.RemoteConfigElement.EndpointDescriptor;

/**
 * @author Kevin Roast
 */
public interface ProxyControllerInterceptor
{
    /**
     * @param endpoint  EndpointDescriptor for the request
     * @param uri       Path uri for the request
     * 
     * @return true if the Proxy Controller should allow HTTP Basic Authentication to be used for
     *         the given endpoint and URL, false to simply return a plain 401 response.
     */
    boolean allowHttpBasicAuthentication(EndpointDescriptor endpoint, String uri);
    
    /**
     * @param endpoint  EndpointDescriptor for the request
     * @param uri       Path uri for the request
     * 
     * @return true to throw an exception on 500 server error response, else just return the response code.
     */
    boolean exceptionOnError(EndpointDescriptor endpoint, String uri);
}