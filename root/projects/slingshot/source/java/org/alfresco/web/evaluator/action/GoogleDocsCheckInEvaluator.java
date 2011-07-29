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

package org.alfresco.web.evaluator.action;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Checks for the following conditions before the "Check In from Google Docs" action is valid:
 * - node is locked
 * - node has neither cm:workingcopy nor trx:transferred aspects applied
 * - node has aspect gd:googleResource
 * - lock owner is current user
 *
 * @author: mikeh
 */
public class GoogleDocsCheckInEvaluator extends BaseEvaluator
{
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        try
        {
            JSONObject node = (JSONObject) jsonObject.get("node");
            if (node != null)
            {
                if ((Boolean) node.get("isLocked"))
                {
                    JSONArray nodeAspects = getNodeAspects(jsonObject);
                    if (nodeAspects != null)
                    {
                        boolean hasGoogleResourceAspect = false;
                        for (Object objAspect : nodeAspects)
                        {
                            String nodeAspect = objAspect.toString();
                            if (nodeAspect.equalsIgnoreCase("cm:workingcopy") || nodeAspect.equalsIgnoreCase("trx:transferred"))
                            {
                                return false;
                            }
                            else if (nodeAspect.equalsIgnoreCase("gd:googleResource"))
                            {
                                hasGoogleResourceAspect = true;
                            }
                        }
                        if (hasGoogleResourceAspect)
                        {
                            JSONObject lockOwner = getProperty(jsonObject, "cm:lockOwner");
                            if (lockOwner != null)
                            {
                                if (lockOwner.get("userName").toString().equalsIgnoreCase(getUserId()))
                                {
                                    return true;
                                }
                            }
                        }
                    }
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
