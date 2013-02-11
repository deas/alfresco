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
define(["alfresco/forms/controls/MultipleEntryFormControl",
        "dojo/_base/declare",
        "dojo/_base/array"], 
        function(MultipleEntryFormControl, declare, array) {
   
   return declare([MultipleEntryFormControl], {
      
//      getValue: function() {
//         
//         var values = this.inherited(arguments);
//         
//         var options = [];
//         array.forEach(values, function(value, index) {
//            options.push({label: value, value: value});
//         }); 
//         
//         var optionsConfig = { fixed: options};
//         return optionsConfig;
//      },
//      
//      setValue: function(value) {
//         // In this particular instance we are currently only supporting fixed options
//         // configuration for the form control, so even though the data that is returned
//         // from getValue() is the correct definition for rendering the form it is not
//         // quite correct for rendering the form definition.
//         var value = ((value) ? value.fixed : null);
//         this.alfLog("log", "Setting OPTIONS: ", value);
//         if (this.wrappedWidget)
//         {
//            this.wrappedWidget.setValue(value);
//         }
//      }
   });
});