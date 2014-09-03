/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
 * Extends the standard [inline property editor]{@link module:alfresco/renderers/InlineEditProperty} to 
 * change the edit text box to be a select menu. The select menu is rendered by a
 * [DojoSelect widget]{@link module:alfresco/forms/controls/DojoSelect} and this module accepts the same 
 * [optionsConfig]{@link module:alfresco/forms/controls/BaseFormControl#optionsConfig} as it does.
 *
 * @module alfresco/renderers/InlineEditSelect
 * @extends module:alfresco/renderers/InlineEditProperty
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/renderers/InlineEditProperty",
        "alfresco/forms/controls/DojoSelect"], 
        function(declare, InlineEditProperty, DojoSelect) {

   return declare([InlineEditProperty], {

      /**
       * Gets the edit widget (creating it the first time it is requested).
       *
       * @instance
       * @returns {object} The widget for editing.
       */
      getEditWidget: function alfresco_renderers_InlineEditSelect__getEditWidget() {
         if (this.editWidget === null)
         {
            this.editWidget = this.createWidget({
               name: "alfresco/forms/controls/DojoSelect",
               config: {
                  optionsConfig: this.optionsConfig
               }
            }, this.editWidgetNode);
         }
         return this.editWidget;
      }
   });
});