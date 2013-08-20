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
 * @module alfresco/kickstart/StepPalette
 * @extends module:alfresco/creation/DragWidgetPalette
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/creation/DragWidgetPalette",
        "dojo/text!./templates/StepTemplate.html",
        "dojo/dom-construct",
        "dojo/string",
        "alfresco/kickstart/ReviewStep",
        "alfresco/kickstart/ChoiceStep"], 
        function(declare, DragWidgetPalette, stepTemplate, domConstruct, stringUtil) {
   
   return declare([DragWidgetPalette], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/StepPalette.css"}],
      
      dragWithHandles: true,
      
      /**
       * Handles the creation of drag'n'drop avatars. This could check the supplied hint parameter
       * to see if an avatar is required, but since the source doesn't allow self-copying and is not
       * a target in itself then this is not necessary.
       * 
       * @instance
       */
      creator: function alfresco_kickstart_StepPalette__creator(item, hint) {
         this.alfLog("log", "Creating", item, hint);
         
         var node = domConstruct.toDom(stringUtil.substitute(stepTemplate, {
            title: item.data.name,
            iconClass: item.data.iconClass
         }));
         return {node: node, data: item, type: ["widget"]};
      },
      
      /**
       * @instance
       * @returns {object[]}
       */
      getPaletteItems: function alfresco_creationDragWidgetPalette__getPaletteItems() {
         return [
            {
               data: {
                  name: "User",
                  module: "alfresco/kickstart/Step",
                  iconClass: "users",
                  defaultConfig: {
                     name: "Users",
                     stepTitle: "Users",
                     iconClass: "users"
                  }
               },
               type: [ "widget" ]
            },
            {
               data: {
                  name: "Review",
                  module: "alfresco/kickstart/ReviewStep",
                  iconClass: "review",
                  defaultConfig: {
                     name: "Review",
                     stepTitle: "Review",
                     iconClass: "review"
                  }
               },
               type: [ "widget" ]
            },
            {
               data: {
                  name: "Feedback",
                  module: "alfresco/kickstart/Step",
                  iconClass: "feedback",
                  defaultConfig: {
                     name: "Feedback",
                     stepTitle: "Feedback",
                     iconClass: "feedback"
                  }
               },
               type: [ "widget" ]
            },
            {
               data: {
                  name: "Choice",
                  module: "alfresco/kickstart/ChoiceStep",
                  iconClass: "choice",
                  defaultConfig: {
                     name: "Choice",
                     stepTitle: "Choice",
                     iconClass: "choice"
                  }
               },
               type: [ "widget" ]
            },
            {
               data: {
                  name: "Parallel",
                  module: "alfresco/kickstart/Step",
                  iconClass: "parallel",
                  defaultConfig: {
                     name: "Parallel",
                     stepTitle: "Parallel",
                     iconClass: "parallel"
                  }
               },
               type: [ "widget" ]
            },
            {
               data: {
                  name: "System",
                  module: "alfresco/kickstart/Step",
                  iconClass: "system",
                  defaultConfig: {
                     name: "System",
                     stepTitle: "System",
                     iconClass: "system"
                  }
               },
               type: [ "widget" ]
            },
            {
               data: {
                  name: "Email",
                  module: "alfresco/kickstart/Step",
                  iconClass: "email",
                  defaultConfig: {
                     name: "Email",
                     stepTitle: "Email",
                     iconClass: "email"
                  }
               },
               type: [ "widget" ]
            }
         ];
      }
   });
});