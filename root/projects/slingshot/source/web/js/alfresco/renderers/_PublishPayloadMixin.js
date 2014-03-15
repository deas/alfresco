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
 * The _PublishPayloadMixin provides functionality that makes it possible to define the payload that will be 
 * constructed on a topic publish from the currentItem properties or payload. The model configuration defines 
 * the name of variables in the new payload and the name and location of where those variables can be found 
 * in the inbound request.
 * 
 * In the following example one variable called shortName is defined as being sourced from the shortName 
 * property of the currentItem. A second variable called visibility is defined as being the value property 
 * found within the payload.
 * 
 * Example model configuration:
 * 
 * publishTopic: "ALF_DO_SOMETHING",
 * publishPayload: {
 *    shortName: {
 *       alfType: "item",
 *       alfProperty: "shortName"
 *    },
 *    visibility: {
 *       alfType: "payload",
 *       alfProperty: "value"
 *    }
 * }
 * 
 * Widgets mixing in the _PublishPayloadMixin will support the model configuration shown.
 * 
 * @module alfresco/renderers/_PublishPayloadMixin
 * @author Richard Smith
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "dojo/_base/lang",
        "alfresco/core/ObjectTypeUtils"], 
        function(declare, AlfCore, lang, ObjectTypeUtils) {
   
   return declare(null, {

      /** 
       * <p>This function is used to process configurable payloads. If a publishPayload property is available on the configuration 
       * object it iterates over the first-level of attributes of the defined payload and checks to see if the 
       * attribute is an object featuring both 'alfType' and 'alfProperty' properties. If the attribute does match this 
       * criteria then the payload will be processed to attempt to retrieve the defined 'alfProperty' from a specific 
       * type. Currently two types are supported:
       * <ul><li>'item' which indicates the property is of the item object</li>
       * <li>'payload' which indicates the property is of the payload object</li></ul><p>
       * <p>A defReturn attribute provides the option for the default return to be defined should there be no 
       * publishPayload provided on the configuration object.</p>
       * 
       * @instance
       * @param {object} configuration
       * @param {object} item
       * @param {object} payload
       * @param {object} defReturn
       * @returns {object} The payload to be published
       */
      generatePayload: function alfresco_renderers__PublishPayloadMixin__generatePayload(configuration, item, payload, defReturn) {

         var publishPayload = (defReturn) ? defReturn : null;

         if(configuration.publishPayload)
         {
            publishPayload = lang.clone(configuration.publishPayload);
            for (var key in publishPayload)
            {
               var value = publishPayload[key];
               if (ObjectTypeUtils.isObject(value) && value.alfType !== undefined && value.alfProperty !== undefined)
               {
                  var type = value.alfType;
                  var property = value.alfProperty;
                  
                  if (type == "item" && item)
                  {
                     value = lang.getObject(property, null, item);
                  }
                  else if (type == "payload" && payload)
                  {
                     value = lang.getObject(property, null, payload);
                  }
                  else
                  {
                     this.alfLog("warn", "A payload was defined with 'alfType' and 'alfProperty' attributes but the 'alfType' attribute was neither 'item' nor 'payload' (which are the only supported types), or the target object was null", this);
                  }
                  publishPayload[key] = value;
               }
            }
         }
         return publishPayload;
      }
   });
});