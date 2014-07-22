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

package org.springframework.extensions.surf.core.scripts;

import org.springframework.extensions.surf.exception.PlatformRuntimeException;

/**
 * @author Kevin Roast
 */
public class ScriptException extends PlatformRuntimeException
{
    private static final long serialVersionUID = 1739480648583299623L;

    /**
     * @param msgId
     */
    public ScriptException(String msgId)
    {
        super(msgId);
    }

    /**
     * @param msgId
     * @param cause
     */
    public ScriptException(String msgId, Throwable cause)
    {
        super(msgId, cause);
    }
    
    /**
     * @param msgId
     * @param params
     */
    public ScriptException(String msgId, Object[] params)
    {
        super(msgId, params);
    }
    
    /**
     * @param msgId
     * @param msgParams
     * @param cause
     */
    public ScriptException(String msgId, Object[] msgParams, Throwable cause)
    {
        super(msgId, msgParams, cause);
    }
}
