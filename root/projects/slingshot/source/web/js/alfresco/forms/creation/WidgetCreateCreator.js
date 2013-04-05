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
define(["alfresco/forms/controls/MultipleEntryCreator",
        "alfresco/forms/creation/WidgetCreateCreatorElement",
        "dojo/_base/declare",
        "dojo/_base/array",
        "dijit/registry",
        "dojo/dom-construct"], 
        function(MultipleEntryCreator, PageCreateCreatorElement, declare, array, registry, domConstruct) {
   
   return declare([MultipleEntryCreator], {
      
      /**
       * Overrides the default Drag-And-Drop type to prevent other objects being dropped into the 
       * creator (for example, we don't want the options or the rules or anything else to be dropped
       * in).
       */
      getDNDType: function() {
         return "FormCreationField";
      },
      
      /**
       * Override the default avatar node construction so that we use the field attribute from the value
       * to indicate what is being dragged.
       */
      createDNDAvatarNode: function(widget) {
         return domConstruct.create("div", { innerHTML: this.encodeHTML((widget && widget.value && widget.value.field) ? widget.value.field : ""}));
      },
      
      createElementWidget: function(elementConfig) {
         var widget = new PageCreateCreatorElement({elementConfig: elementConfig});
         return widget;
      }
   });
});