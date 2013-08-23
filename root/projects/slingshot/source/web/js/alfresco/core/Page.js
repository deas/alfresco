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
 * This is used as the default root object when instantiating a page. There should be no need
 * to ever instantiate this widget directly.
 * 
 * @module alfresco/core/Page
 * @extends module:alfresco/core/ProcessWidgets
 * @author Dave Draper
 */
define(["alfresco/core/ProcessWidgets",
        "dojo/_base/declare",
        "dojo/dom-construct",
        "dojo/_base/array",
        "dojo/_base/lang"], 
        function(ProcessWidgets, declare, domConstruct, array, lang) {
   
   return declare([ProcessWidgets], {
      
      /**
       * Overrides the superclass implementation to call [processServices]{@link module:alfresco/core/Core#processServices}
       * and [processWidgets]{@link module:alfresco/core/Core#processWidgets} as applicable.
       * 
       * @instance
       */
      postCreate: function alfresco_core_Page__postCreate() {
         
         if (this.services)
         {
            this.processServices(this.services);
         }
         
         if (this.widgets)
         {
            this.processWidgets(this.widgets, this.containerNode);
         }
      },
      
      /**
       * @instance
       */
      allWidgetsProcessed: function alfresco_core_Page__allWidgetsProcessed(widgets) {
         this.alfLog("log", "All page widgets processed");
         // TODO: Need to be able to notify widgets that they can start publications in the knowledge that other widgets are available
         // to respond...
         
         if (this.publishOnReady != null)
         {
            array.forEach(this.publishOnReady, lang.hitch(this, "onReadyPublish"));
         }
      },
      
      /**
       * @instance
       */
      onReadyPublish: function alfresco_core_Page__onReadyPublish(publicationDetails) {
         if (publicationDetails != null && 
             publicationDetails.publishTopic != null &&
             publicationDetails.publishTopic != "")
         {
            this.alfLog("log", "Onload publication", publicationDetails);
            this.alfPublish(publicationDetails.publishTopic, publicationDetails.publishPayload);
         }
         else
         {
            this.alfLog("warn", "The page was configured with an onload publication but no 'publishTopic' was provided", publicationDetails, this);
         }
      }
   });
});