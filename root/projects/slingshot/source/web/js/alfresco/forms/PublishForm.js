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
define(["dojo/_base/declare",
        "alfresco/forms/Form"], 
        function(declare, Form) {
   
   return declare([Form], {
      
      /**
       * This function overrides the default implementation so that instead of performing an XHR
       * POST operation it simply publishes a topic indicating that the "OK" button has been clicked.
       */
      _onOK: function() {
         
         
         
      },
   
      /**
       * Overridden to hide the buttons.
       */
      createButtons: function() {
         // TODO: This isn't really accurate for a PublishForm. This should arguably done in a different class
      }
   });
});