/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

/**
 * @module alfresco/header/Header
 * @extends module:alfresco/layout/LeftAndRight
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/layout/LeftAndRight"], 
        function(declare, LeftAndRight) {
   
   /**
    * This extends the alfresco/layout/LeftAndRight image to header specific CSS selectors.
    */
   return declare([LeftAndRight], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {{cssFile: string, media: string}[]}
       * @default [{cssFile:"./css/Header.css"}]
       */
      cssRequirements: [{cssFile:"./css/Header.css"}]
   });
});