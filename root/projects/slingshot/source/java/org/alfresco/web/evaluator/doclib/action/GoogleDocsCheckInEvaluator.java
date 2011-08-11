/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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

package org.alfresco.web.evaluator.doclib.action;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Checks for the following conditions before the "Check In from Google Docs" action is valid:
 * <pre>
 *     hasAspect("gd:googleResource")
 *     hasAspect("cm:workingcopy")
 *     property "cm:workingCopyOwner" == (currentUser)
 *      <b>-OR-</b>
 *     hasAspect("cm:checkedOut")
 *     workingCopy.googleDocUrl != null
 *     property "cm:lockOwner" == (currentUser)
 * </pre>
 *
 * @author: mikeh
 */
public class GoogleDocsCheckInEvaluator extends BaseEvaluator
{
    private static final String ASPECT_CHECKEDOUT = "cm:checkedOut";
    private static final String ASPECT_GOOGLERESOURCE = "gd:googleResource";
    private static final String ASPECT_WORKINGCOPY = "cm:workingcopy";
    private static final String PROP_LOCKOWNER = "cm:lockOwner";
    private static final String PROP_WORKINGCOPYOWNER = "cm:workingCopyOwner";
    private static final String VALUE_GOOGLEDOCURL = "workingCopy.googleDocUrl";

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        try
        {
            JSONArray nodeAspects = getNodeAspects(jsonObject);
            if (nodeAspects == null)
            {
                return false;
            }
            else
            {
                if (nodeAspects.contains(ASPECT_GOOGLERESOURCE) &&
                        nodeAspects.contains(ASPECT_WORKINGCOPY))
                {
                    return getMatchesCurrentUser(jsonObject, PROP_WORKINGCOPYOWNER);
                }
                else if (nodeAspects.contains(ASPECT_CHECKEDOUT) &&
                        getJSONValue(jsonObject, VALUE_GOOGLEDOCURL) != null)
                {
                    return getMatchesCurrentUser(jsonObject, PROP_LOCKOWNER);
                }
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("JSONException whilst running action evaluator: " + err.getMessage());
        }

        return false;
    }
}
