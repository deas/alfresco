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

package org.alfresco.util.collections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Nick Smith
 * @since 4.0
 */
public abstract class CollectionUtils
{
    public static final Function<Object, String> TO_STRING_TRANSFORMER = new Function<Object, String>()
    {
        public String apply(Object value)
        {
            return value.toString();
        }
    };

    /**
     * Converts a {@link Collection} of values of type F to a {@link Serializable} {@link List} of values of type T.
     * @param <F> From type
     * @param <T> To type
     * @param values the values to convert.
     * @param transformer Used to convert values.
     * @return
     */
    public static <F, T> List<T> transform(Collection<F> values, Function<? super F, ? extends T> transformer)
    {
        List<T> results = new ArrayList<T>(values.size());
        for (F value : values)
        {
            results.add(transformer.apply(value));
        }
        return results;
    }
    
    public static List<String> toListOfStrings(Collection<?> values)
    {
        return transform(values, TO_STRING_TRANSFORMER);
    }

}
