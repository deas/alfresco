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
 * @module alfresco/kickstart/ReviewStep
 * @extends module:alfresco/kickstart/Step
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/kickstart/Step",
        "dojo/_base/lang",
        "alfresco/layout/SlideOverlay",
        "alfresco/layout/HorizontalWidgets",
        "alfresco/creation/DropAndPreview",
        "alfresco/creation/DragWidgetPalette",
        "alfresco/creation/WidgetConfig"], 
        function(declare, Step, lang, SlideOverlay, HorizontalWidgets, DropAndPreview, DragWidgetPalette, WidgetConfig) {
   
   return declare([Step], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/ReviewStep.css"}],
      
      /**
       * This is intended to be overridden to create the actual initial content for the widget.
       * 
       * @instance
       * @returns {object} The content to be placed in the collapsing section.
       */
      getInitialContent: function alfresco_kickstart_ReviewStep__getInitialContent() {
         this.rootWidget = new SlideOverlay({
            pubSubScope: this.id,
            dataScope: this.dataScope,
            showTopics: ["ALF_CONFIGURE_WIDGET"],
            hideTopics: ["ALF_UPDATE_RENDERED_WIDGET","ALF_CLEAR_CONFIGURE_WIDGET"],
            adjustHeightTopics: ["ALF_CONFIGURE_WIDGET"],
            widgets: [
               {
                  name: "alfresco/layout/HorizontalWidgets",
                  align: "underlay",
                  assignTo: "layoutWidget",
                  config: {
                     widgets: [
                        {
                           name: "alfresco/creation/DropAndPreview",
                           assignTo: "previewWidget",
                           config: {
                           }
                        },
                        {
                           name: "alfresco/creation/DragWidgetPalette",
                           config: {
                           }
                        }
                     ]
                  }
               },
               {
                  name: "alfresco/creation/WidgetConfig",
                  align: "overlay",
                  assignTo: "configWidget",
                  config: {
                     width: "50%"
                  }
               }
            ]
         });
         return this.rootWidget;
      },
      
      /**
       * @instance
       */
      getAvailableFormFields: function alfresco_kickstart_ReviewStep__getAvailableFormFields() {
         var availableFields = [];
         var previewWidget = lang.getObject("rootWidget.layoutWidget.previewWidget", false, this);
         if (previewWidget)
         {
            availableFields = previewWidget.getAvailableFields();
         }
         return availableFields;
      }
   });
});