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

package org.alfresco.repo.lotus.ws.impl.helper;

/**
 * <p>
 * Enum used to indicate document status
 * </p>
 * 
 * @author Eugene Zheleznyakov
 */
public enum DocumentStatus
{
    /**
     * Document isn't checked out and readonly
     */
    NORMAL,

    /**
     * Document isn't checked out, but it is readonly
     */
    READONLY,

    /**
     * Document is short-term checked out and current user isn't checkout owner
     */
    SHORT_CHECKOUT,

    /**
     * Document is short-term checked out and current user is checkout owner
     */
    SHORT_CHECKOUT_OWNER,

    /**
     * Document is long-term checked out and current user isn't checkout owner
     */
    LONG_CHECKOUT,

    /**
     * Document is long-term checked out and current user is checkout owner
     */
    LONG_CHECKOUT_OWNER
}
