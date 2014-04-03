/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.dashlet;

import org.alfresco.webdrone.RenderTime;

/**
 * A dashlet interface.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public interface Dashlet
{
    /**
     * Dashlet renderer verifies the page has rendered
     * by checking java script page loaded status is complete.
     * 
     * @param timer {@link RenderTime}
     * @return Dashlet object response
     */
    <T extends Dashlet> T render(final RenderTime timer);

    /**
     * Dashlet renderer verifies the page has rendered
     * by checking java script page loaded status is complete.
     * 
     * @return Dashlet object response
     */
    <T extends Dashlet> T render();

    /**
     * Dashlet renderer verifies the page has rendered
     * by checking java script page loaded status is complete.
     * 
     * @param timer long
     * @return Dashlet object response
     */
    <T extends Dashlet> T render(long timer);
}
