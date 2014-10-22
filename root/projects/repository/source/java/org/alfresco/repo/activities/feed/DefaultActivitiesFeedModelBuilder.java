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
package org.alfresco.repo.activities.feed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.domain.activities.ActivityFeedEntity;
import org.json.JSONException;

public class DefaultActivitiesFeedModelBuilder implements ActivitiesFeedModelBuilder
{
    private List<Map<String, Object>> activityFeedModels = new ArrayList<Map<String, Object>>();
    
    private long maxFeedId = -1L;


    @Override
    public Map<String, Object> buildModel()
    {
        Map<String, Object> model = new HashMap<String, Object>();
        
        model.put("activities", activityFeedModels);
        model.put("feedItemsCount", activityFeedModels.size());
        
        return model;
    }

    @Override
    public void addActivityFeedEntry(ActivityFeedEntity feedEntry) throws JSONException
    {
        Map<String, Object> map;
        map = feedEntry.getModel();
        activityFeedModels.add(map);
        
        long feedId = feedEntry.getId();
        if (feedId > maxFeedId)
        {
            maxFeedId = feedId;
        }
    }

    @Override
    public int activityCount()
    {
        return activityFeedModels.size();
    }

    @Override
    public long getMaxFeedId()
    {
        return maxFeedId;
    }
}
