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
 * @module alfresco/kickstart/ChoiceStep
 * @extends module:alfresco/kickstart/Step
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/kickstart/Step",
        "alfresco/forms/controls/MultipleEntryFormControl",
        "alfresco/kickstart/ChoiceStepElement"], 
        function(declare, Step, MultipleEntryFormControl, ChoiceStepElement) {
   
   return declare([Step], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/ChoiceStep.css"}],
      
      /**
       * This is intended to be overridden to create the actual initial content for the widget.
       * 
       * @instance
       * @returns {object} The content to be placed in the collapsing section.
       */
      getInitialContent: function alfresco_kickstart_ChoiceStep__getInitialContent() {
         var mefc = this.createWidget({
            name: "alfresco/forms/controls/MultipleEntryFormControl",
            config: {
               elementWidget: "alfresco/kickstart/ChoiceStepElement"
            }
         });
         return mefc;
      }
   });
});