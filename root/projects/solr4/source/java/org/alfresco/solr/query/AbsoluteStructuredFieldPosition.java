/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.solr.query;

import java.io.IOException;

/**
 * This class patches a term at a specified location.
 * 
 * @author andyh
 */
public class AbsoluteStructuredFieldPosition extends AbstractStructuredFieldPosition
{

    int requiredPosition;

    /**
     * Search for a term at the specified position.
     */

    public AbsoluteStructuredFieldPosition(String termText, int position)
    {
        super(termText, true, true);
        this.requiredPosition = position;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.lucene.extensions.StructuredFieldPosition#matches(int,
     *      org.apache.lucene.index.TermPositions)
     */
    public int matches(int start, int end, int offset) throws IOException
    {
        if (offset >= requiredPosition)
        {
            return -1;
        }

        if (getCachingTermPositions() != null)
        {
            // Doing "termText"
            getCachingTermPositions().reset();
            int count = getCachingTermPositions().freq();
            int realPosition = 0;
            int adjustedPosition = 0;
            for (int i = 0; i < count; i++)
            {
                realPosition = getCachingTermPositions().nextPosition();
                adjustedPosition = realPosition - start;
                if ((end != -1) && (realPosition > end))
                {
                    return -1;
                }
                if (adjustedPosition > requiredPosition)
                {
                    return -1;
                }
                if (adjustedPosition == requiredPosition)
                {
                    return adjustedPosition;
                }

            }
        }
        else
        {
            // Doing "*"
            if ((offset + 1) == requiredPosition)
            {
                return offset + 1;
            }
        }
        return -1;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.lucene.extensions.StructuredFieldPosition#getPosition()
     */
    public int getPosition()
    {
        return requiredPosition;
    }

    public String getDescription()
    {
        return "Absolute Named child";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + requiredPosition;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbsoluteStructuredFieldPosition other = (AbsoluteStructuredFieldPosition) obj;
        if (requiredPosition != other.requiredPosition)
            return false;
        return true;
    }
    
    
}