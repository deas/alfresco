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
define(["dojo/_base/declare"], 
        function(declare) {
   return declare(null, {
      
      callbackMonkey: function(nameOfChangedProperty, oldValue, newValue, callingObject, attribute) {
         return newValue == "required";
      },
      
      callbackOptions: function(nameOfChangedProperty, oldValue, newValue, callingObject) {
         callingObject.setOptions([
            { label: "Dynamic1_" + newValue, value: "Dynamic1"},
            { label: "Dynamic2_" + newValue, value: "Dynamic2"}
         ]);
      },
      
      overSixty: function(nameOfChangedProperty, oldValue, newValue, callingObject) {
         return (newValue >= 60);
      },
      
      overEighteen: function(nameOfChangedProperty, oldValue, newValue, callingObject) {
         return (newValue >= 18);
      },
      
      ambitions: function(nameOfChangedProperty, oldValue, newValue, callingObject) {
         var options = [];
         if (newValue==="u")
         {
            options = [
               { label: "I'd like to get a job", value: "js"},
               { label: "I'm happy unemployed", value: "hu"}
            ];
         }
         else if (newValue==="pt")
         {
            options = [
              { label: "I'd like to quit", value: "q"},
              { label: "I'm happy as I am", value: "h"},
              { label: "I'd like to go full time", value: "ft"}
           ];
         }
         else if (newValue==="ft")
         {
            options = [
              { label: "I'd like to quit", value: "q"},
              { label: "I'd like to go part time", value: "pt"},
              { label: "I'm happy as I am", value: "h"}
           ];
         }
         callingObject.setOptions(options);
      }
      
   });
});