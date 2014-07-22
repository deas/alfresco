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

package org.springframework.extensions.surf.util;

import java.io.Serializable;

/**
 * Utility class for containing two things that aren't like each other
 */
public final class Pair<F, S> implements Serializable
{
    private static final long serialVersionUID = -7906248421185630612L;
    
    @SuppressWarnings("unchecked")
    public static final Pair NULL_PAIR = new Pair(null, null);
    
    @SuppressWarnings("unchecked")
    public static final <X, Y> Pair<X, Y> nullPair()
    {
        return NULL_PAIR;
    }
    
    /**
     * The first member of the pair.
     */
    private F first;
    
    /**
     * The second member of the pair.
     */
    private S second;
    
    /**
     * Make a new one.
     * 
     * @param first The first member.
     * @param second The second member.
     */
    public Pair(F first, S second)
    {
        this.first = first;
        this.second = second;
    }
    
    /**
     * Get the first member of the tuple.
     * @return The first member.
     */
    public F getFirst()
    {
        return first;
    }
    
    /**
     * Get the second member of the tuple.
     * @return The second member.
     */
    public S getSecond()
    {
        return second;
    }
    
    public void setFirst(F first)
    {
        this.first = first;
    }
    
    public void setSecond(S second)
    {
        this.second = second;
    }
    
    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (other == null || !(other instanceof Pair<?, ?>))
        {
            return false;
        }
        Pair<?, ?> o = (Pair<?, ?>)other;
        return (this.first == o.first) || (this.first != null && o.first != null && this.first.equals(o.first)) &&
               (this.second == o.second) || (this.second != null && o.second != null && this.second.equals(o.second));
    }
    
    @Override
    public int hashCode()
    {
        return (first == null ? 0 : first.hashCode()) + (second == null ? 0 : second.hashCode());
    }

    @Override
    public String toString()
    {
        return "(" + first + ", " + second + ")";
    }
}
