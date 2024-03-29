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

package org.springframework.extensions.surf.render;

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.exception.RendererExecutionException;

public interface Processor
{
    /**
     * Executes the given focus of the processor output using the
     * given processor context
     *
     * @param processorContext
     * @param focus
     *
     * @throws RendererExecutionException
     */
    public void execute(ProcessorContext processorContext, ModelObject object, RenderFocus focus)
        throws RendererExecutionException;

    /**
     * Executes the "body" of the processor output using the given
     * processor context.
     *
     * @param processorContext processorContext
     *
     * @throws RendererExecutionException
     */
    public void executeBody(ProcessorContext processorContext, ModelObject object)
        throws RendererExecutionException;

    /**
     * Executes the "header" of the processor output using the given
     * processor context.
     *
     * @param processorContext processorContext
     *
     * @throws RendererExecutionException
     */
    public void executeHeader(ProcessorContext processorContext, ModelObject object)
        throws RendererExecutionException;

    /**
     * Indicates whether the engine responsible for processing
     * the body of the processor exists.
     *
     * @param processorContext
     * @return
     */
    public boolean exists(ProcessorContext processorContext, ModelObject object);
}
