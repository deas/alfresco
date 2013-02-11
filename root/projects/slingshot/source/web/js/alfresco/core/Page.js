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
define(["alfresco/core/ProcessWidgets",
        "dojo/_base/declare",
        "dojo/dom-construct",
        "dojo/_base/array"], 
        function(ProcessWidgets, declare, domConstruct, array) {
   
   return declare([ProcessWidgets], {
      postCreate: function() {
         
         if (this.services)
         {
            this.processServices(this.services);
         }
         
         if (this.widgets)
         {
            this.processWidgets(this.widgets, this.containerNode);
         }
         
         this.alfLog("log", "Page widgets and service processed", {});
         this.alfPublish("PageReady", {});
      }
   });
});