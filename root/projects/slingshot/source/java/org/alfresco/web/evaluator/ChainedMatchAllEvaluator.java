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

package org.alfresco.web.evaluator;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Calls multiple evaluators in turn until either the last one is called
 * or one of the evaluators returns false. Effectively becomes a logical
 * AND of the participating evaluators.
 *
 * @author: mikeh
 */
public class ChainedMatchAllEvaluator extends BaseActionEvaluator
{
    private ArrayList<ActionEvaluator> evaluators = null;

    /**
     * Evaluators to participate in the evaluation chain
     *
     * @param evaluators
     */
    public void setEvaluators(ArrayList<ActionEvaluator> evaluators)
    {
        this.evaluators = evaluators;
    }

    /**
     * Run through each given evaluator until we either get to the end or one returns false
     *
     * @param jsonObject The object the action is for
     * @return
     */
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        boolean result = true;

        if (evaluators != null)
        {
            ListIterator<ActionEvaluator> evalIter = evaluators.listIterator();

            while (result && evalIter.hasNext())
            {
                result = evalIter.next().evaluate(jsonObject);
            }
        }

        return result;
    }
}
