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

package org.springframework.extensions.surf.exception;

/**
 * Thrown when a connector was unable to properly authenticate
 * the user's request against a given endpoint.  This exception
 * is thrown after the connector has made its best total effort
 * and cannot properly hand back a valid response.
 * 
 * @author muzquiano
 */
public class AuthenticationException extends Exception
{
    public AuthenticationException(String message)
    {
        super(message);
    }

    public AuthenticationException(String message, Exception ex)
    {
        super(message, ex);
    }
}
