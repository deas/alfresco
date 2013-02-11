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
 * This defines an individual control that can be used as part of a form for constructing Workflow steps
 */
define(["alfresco/forms/controls/MultipleEntryFormControl",
        "dojo/_base/declare",
        "alfresco/dynamic-workflows/WorkflowStepCreator"], 
        function(MultipleEntryFormControl, declare, WorkflowStepCreator) {
   
   return declare([MultipleEntryFormControl], {
      
      /**
       * This should be passed as a constructor argument. It will be used to post topics that request
       * new details to be displayed.
       */
      pubSubScope: null,
      
      getWidgetConfig: function() {
         return {
            id : this.generateUuid(),
            name: this.name,
            value: this.value,
            pubSubScope: this.pubSubScope
         };
      },
      
      createFormControl: function(config, domNode) {
         return new WorkflowStepCreator(config);
      }
   });
});