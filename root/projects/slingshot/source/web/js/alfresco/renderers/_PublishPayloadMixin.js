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
 * <p>The _PublishPayloadMixin should be mixed into all modules that perform publications. It provides a consistent
 * way of generating the payload body. There are several different ways of generating a payload which include the 
 * following:</p>
 * <ul><li>configured payload</li>
 * <li>the current item</li>
 * <li>the configured payload processed through one or more modifier functions</li>
 * <li>a new payload built from properties in the current item or a triggering publication payload</li>
 * <li>any of the above with the current item "mixed in"</li></ul>
 * 
 * <p>In the following example one variable called shortName is defined as being sourced from the shortName 
 * property of the currentItem. A second variable called visibility is defined as being the value property 
 * found within the payload.</p>
 * 
 * <p>Example model configuration:</p>
 * 
 * <p><pre>publishTopic: "ALF_DO_SOMETHING",
 * publishPayload: {
 *    shortName: {
 *       alfType: "item",
 *       alfProperty: "shortName"
 *    },
 *    visibility: {
 *       alfType: "payload",
 *       alfProperty: "value"
 *    }
 * }</pre></p>
 * 
 * <p>Widgets mixing in the _PublishPayloadMixin will support the model configuration shown.</p>
 * 
 * @module alfresco/renderers/_PublishPayloadMixin
 * @auther Dave Draper
 * @author Richard Smith
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "alfresco/core/ObjectProcessingMixin",
        "dojo/_base/lang",
        "alfresco/core/ObjectTypeUtils"], 
        function(declare, AlfCore, ObjectProcessingMixin, lang, ObjectTypeUtils) {
   
   return declare([ObjectProcessingMixin], {

      /** 
       * Generates the payload based on the supplied attributes.
       *
       * @instance
       * @param {object} configuredPayload The configured payload
       * @param {object} currentItem The current item
       * @param {object} receivedPayload A payload that may have been received to trigger the request to generate a new payload (set as null if not applicable)
       * @param {string} payloadType The type of payload to generate
       * @param {boolean} mixinCurrentItem Whether to mixin the current item into the generated payload
       * @param {array} publishPayloadModifiers An array of modifier functions to apply when the type is "PROCESS"
       * @returns {object} The generated payload
       */
      generatePayload: function alfresco_renderers__PublishPayloadMixin__generatePayload(configuredPayload, currentItem, receivedPayload, payloadType, mixinCurrentItem, publishPayloadModifiers) {
         var generatedPayload = null;
         if (payloadType == null || payloadType == "CONFIGURED")
         {
            // No payload type has been configured, or has been set to the default of "CONFIGURED" - just use the payload as is
            generatedPayload = configuredPayload;
         }
         else if (payloadType == "CURRENT_ITEM")
         {
            // Use the current item as the payload...
            generatedPayload = currentItem;
         }
         else if(payloadType == "PROCESS")
         {
            // Clone the configured payload so as not to "pollute" the statically defined value...
            generatedPayload = lang.clone(configuredPayload);

            // The configured payload should be process the payload using the modifier functions
            this.processObject(publishPayloadModifiers, generatedPayload);
         }
         else if (payloadType == "BUILD")
         {
            // Clone the configured payload so as not to "pollute" the statically defined value...
            generatedPayload = lang.clone(configuredPayload);

            // Build the payload using the "alfType" and "alfProperty" keywords...
            generatedPayload = this.buildPayload(generatedPayload, currentItem, receivedPayload);
         }

         // Mixin the current item into the payload if required...
         if (mixinCurrentItem == true)
         {
            if (this.currentItem != null)
            {
               lang.mixin(generatedPayload, currentItem);
            }
            else
            {
               this.alfLog("warn", "A request was made to mix the 'currentItem' into the publish payload, but no 'currentItem' has been defined", this);
            }
         }
         return generatedPayload;
      },

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
       * @param {object} configuredPayload The configured payload - this is used to generate a new payload
       * @param {object} currentItem The current item
       * @param {object} receivedPayload The payload that triggered the request to generate a new payload
       * @returns {object} The payload to be published
       */
      buildPayload: function alfresco_renderers__PublishPayloadMixin__buildPayload(configuredPayload, currentItem, receivedPayload) {
         // TODO: Needs to process deep-levels of nesting...
         if(configuredPayload != null)
         {
            // Copy the original to grab data from...
            for (var key in configuredPayload)
            {
               var value = configuredPayload[key];
               if (ObjectTypeUtils.isObject(value) && value.alfType !== undefined && value.alfProperty !== undefined)
               {
                  var type = value.alfType;
                  var property = value.alfProperty;
                  
                  if (type == "item" && currentItem)
                  {
                     value = lang.getObject(property, null, currentItem);
                  }
                  else if (type == "payload" && receivedPayload)
                  {
                     value = lang.getObject(property, null, receivedPayload);
                  }
                  else
                  {
                     this.alfLog("warn", "A payload was defined with 'alfType' and 'alfProperty' attributes but the 'alfType' attribute was neither 'item' nor 'payload' (which are the only supported types), or the target object was null", this);
                  }
                  configuredPayload[key] = value;

                  // Clean up the payload...
                  delete value.alfType;
                  delete value.alfProperty;
               }
            }
         }
         return configuredPayload;
      }
   });
});