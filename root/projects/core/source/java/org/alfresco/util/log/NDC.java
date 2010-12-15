/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.util.log;

import java.lang.reflect.Method;

/**
 * A stand in for the org.apache.log4j.NDC class that avoids introducing runtime dependencies against the otherwise
 * optional log4j.
 * 
 * @author dward
 */
public class NDC
{
    /** Log4J reflection to clear the NDC */
    private static Method ndcRemoveMethod;

    /** Log4J reflection to push the NDC */
    private static Method ndcPushMethod;

    static
    {
        try
        {
            Class<?> ndc = Class.forName("org.apache.log4j.NDC");
            ndcRemoveMethod = ndc.getMethod("remove");
            ndcPushMethod = ndc.getMethod("push", String.class);
        }
        catch (Throwable e)
        {
            // We just ignore it
        }
    }

    /**
     * Push new diagnostic context information for the current thread.
     * 
     * @param message
     *            The new diagnostic context information.
     */
    public static void push(String message)
    {
        try
        {
            ndcPushMethod.invoke(null, message);
        }
        catch (Throwable e)
        {
            // We just ignore it
        }
    }

    /**
     * Remove the diagnostic context for this thread.
     */
    static public void remove()
    {
        try
        {
            ndcRemoveMethod.invoke(null);
        }
        catch (Throwable e)
        {
            // We just ignore it
        }
    }
}
