/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
 * This is meant to be used as an augmentation to Alfresco.component.Base.
 * It allows events to be delegated to the root element of the component
 * and passes events through to the correct event handler using CSS rules.
 * 
 * Usage:
 *    Component must have an initEvents method which registers event handlers
 *    via registerEventHandler(). initEvents() must be called in component's
 *    onReady() method.
 *    Example call of registerEventHandler from with initEvents() :
 *    (2nd parameter can be an array of third parameters)
 *      this.registerEventHandler('click', 'button.editRef',
 *      {
 *         handler: function editReference(e, args)
 *         {
 *            console.log(arguments, '[editReference]');
 *         },
 *         scope : this
 *      });
 *    
 *    or multiple handlers for the same event: 
 * 
 *    this.registerEventHandler('click',
 *    [
 *       {
 *          rule: 'button.editRef',
 *          o:
 *          {
 *             handler: function editReference(e,args)
 *             {
 *                alert('editReference');
 *             },
 *             scope : this
 *          }
 *       },
 *       {
 *          rule: 'button.deleteRef',
 *          o:
 *          {
 *             handler: this.onDeleteReference,
 *             scope: this
 *          }
 *       }
 *    ]);
 * 
 */
(function Base_Component_Event_Delegator()
{
   /**
    * YUI Library aliases
    */
   var Event = YAHOO.util.Event,
       Sel = YAHOO.util.Selector;

   var eventDelegator = function(){};
 
   eventDelegator.prototype =
   {
      /**
       * Container for event handlers
       */
      eventHandlers: {},

      /**
       * Registers an event handler against a specific CSS rule
       * @method registerEventHandler
       */
      registerEventHandler: function registerEventHandler(eventName, rule, o)
      {
         if ((!YAHOO.lang.isUndefined(rule)) && YAHOO.lang.isArray(rule))
         {
            for (var i = 0, len = rule.length; i < len; i++)
            {
               this.registerEventHandler(eventName, rule[i].rule, rule[i].o);
            }
            return this;
         }
         this.eventHandlers[eventName] = this.eventHandlers[eventName] || {};
         this.eventHandlers[eventName][rule] = o;
         return this;
      },

      /**
       * Event delegation handler for any event type
       * @method onInteractionEvent
       */
      onInteractionEvent: function RM_References_onInteractionEvent(e, args)
      {
         // get element that triggered event
         var elTarget = Event.getTarget(e),
            eventName = e.type;

         // Event.preventDefault(e)
         // Iterate through rules and execute handlers
         if (this.eventHandlers[eventName])
         {
            var rules = this.eventHandlers[eventName],
               handlerObj;
            for (var rule in rules)
            {
               if (Sel.test(elTarget, rule))
               {
                  handlerObj = rules[rule];
                  if (handlerObj.handler && YAHOO.lang.isFunction(handlerObj.handler))
                  {
                     return handlerObj.handler.apply(handlerObj.scope || window, arguments);
                  }
               }
            }
         }
         return this;
      }
   };

   YAHOO.augment(Alfresco.component.Base, eventDelegator);
})();