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
 * This should be mixed into all Alfresco widgets as it provides the essential functions that they will 
 * undoubtedly required, e.g. logging, publication/subscription handling, i18n message handling, etc. 
 * 
 * @module alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/CoreData",
        "dijit/registry",
        "dojo/topic",
        "dojo/_base/array",
        "dojo/_base/lang",
        "dojo/dom-construct",
        "dojox/uuid/generateRandomUuid",
        "dojo/request/xhr",
        "dojo/json",
        "dojo/date/stamp",
        "dojox/html/entities",
        "dojo/sniff"], 
        function(declare, CoreData, registry, pubSub, array, lang, domConstruct, uuid, xhr, JSON, stamp, htmlEntities, has) {
   
   return declare(null, {
      
      /**
       * Creates and returns a new UUID (universally unique identifier). The UUID is generated using the
       * dojox/uuid/generateRandomUuid module
       * 
       * @instance
       * @returns {string} A new UUID
       */
      generateUuid: function alfresco_core_Core__generateUuid() {
         return uuid();
      },
      
      /**
       * This function is based on the version that can be found in alfresco.js. It searches through all of
       * the available scopes for the widget and for all of the widgets inherited from.
       * 
       * @instance
       * @param {string} p_messageId The id of the message to be displayed.
       * @returns {string} A localized form of the supplied message
       */
      message: function alfresco_core_Core__message(p_messageId) {

         if (typeof p_messageId != "string")
         {
            throw new Error("Missing or invalid argument: messageId");
         }

         var msg = p_messageId;
         
         // Check the global message bundle for the message id (this will get overridden if a more specific
         // property is available)...
         if (typeof Alfresco.messages.global === "object")
         {
            var globalMsg = Alfresco.messages.global[p_messageId];
            if (typeof globalMsg == "string")
            {
               msg = globalMsg;
            }
         }
         
         // Overwrite with page scope...
         if (typeof Alfresco.messages.pageScope === "object")
         {
            var pageScopeMsg = Alfresco.messages.pageScope[p_messageId];
            if (typeof pageScopeMsg == "string")
            {
               msg = pageScopeMsg;
            }
         }
         
         // Overwrite page scope with default scope...
         if (typeof Alfresco.messages.scope[Alfresco.messages.defaultScope] === "object")
         {
            var scopeMsg = Alfresco.messages.scope[Alfresco.messages.defaultScope][p_messageId];
            if (typeof scopeMsg == "string")
            {
               msg = scopeMsg;
            }
         }
         
         // Work through the base classes and use their i18nScope property (if available) as a scope to 
         // check. This allows a widget to check its class hierarchy for message scopes.
         array.forEach(this.constructor._meta.parents, function(entry, i) {
            
            // PLEASE NOTE: Use of the constructor _meta property is used at risk. It is the recognised
            //              way of accessing parent classes (for example it is used in the .isInstanceOf()
            //              function but there is a warning that it is not part of an API that can be relied
            //              upon to never change. Should message handling fail, then this might be an area
            //              to investigate.
            if (entry._meta && entry._meta.hidden && entry._meta.hidden.i18nScope && Alfresco.messages.scope[entry._meta.hidden.i18nScope])
            {
               var scopeMsg = Alfresco.messages.scope[entry._meta.hidden.i18nScope][p_messageId];
               if (typeof scopeMsg == "string")
               {
                  msg = scopeMsg;
               }
            }
         });
         
         // Set the main scope for the calling class...
         // This will either be the i18nScope or the default message scope if i18nScope is not defined
         var messageScope;
         if (typeof this.i18nScope != "undefined" && typeof Alfresco.messages.scope[this.i18nScope] === "object")
         {
            var scopeMsg = Alfresco.messages.scope[this.i18nScope][p_messageId];
            if (typeof scopeMsg == "string")
            {
               msg = scopeMsg;
            }
         }
         
         // Search/replace tokens
         var tokens = [];
         if ((arguments.length == 2) && (typeof arguments[1] == "object"))
         {
            tokens = arguments[1];
         }
         else
         {
            tokens = Array.prototype.slice.call(arguments).slice(2);
         }
         
         // Emulate server-side I18NUtils implementation
         if (tokens instanceof Array && tokens.length > 0)
         {
            msg = msg.replace(/''/g, "'");
         }
         
         // TODO: Need to check this works with old Share strings...
         msg = lang.replace(msg, tokens);
         return msg;
      },
      
      /**
       * Use this function to ensure that all text added to the HTML page is encoded to prevent XSS style
       * attacks. This wraps the dojox/html/entities encode function. It is intentionally wrapped so that
       * if we need to make a change (e.g. change the encoding handling) we can make it in one place
       * 
       * @instance
       * @returns The encoded input string
       */
      encodeHTML: function alfresco_core_Core__encodeHTML(textIn) {
         return htmlEntities.encode(textIn);
      },

      /**
       * This is the scope to use within the data model. If this is not initiated during instantiation then
       * it will be assigned to the root scope of the data model the first time any of the data API functions
       * are used. 
       * 
       * @instance
       * @type {object}
       * @default null
       */
      dataScope: null,
      
      /**
       * This will be used to keep track of all the data event callbacks that are registered for the instance.
       * These will be iterated over and removed when the instance is destroyed.
       * 
       * @instance
       * @type {function[]}
       * @default null
       */
      dataBindingCallbacks: null,
      
      alfProcessDataDotNotation: function alfresco_core_Core__alfProcessDataDotNotation(dotNotation) {
         var re = /(\.|\[)/g;
         return dotNotation.replace(re, "._alfValue$1")
      },
      
      /**
       * This both sets data and registers the widget of as the owner of the data. This is done so that 
       * when the widget is destroyed the data it owned will be removed from the data model
       * 
       * @instance
       * @param {string} dotNotation A dot notation representation of the location within the data model to set.
       * @param {object} value The value to set
       * @param {object} scope The scope to set the data at. If null the instance scope will be used.
       * @returns {object} An object that the widget can use to remove the data when it is destroyed.
       */
      alfSetData: function alfresco_core_Core__alfSetData(dotNotation, value, scope) {
         this.alfLog("log", "Setting data", dotNotation, value, scope, this);
         var dataOwnerBinding = {};
         if (this.dataScope == null)
         {
            this.dataScope = CoreData.getSingleton().root;
         }
         if (scope == null)
         {
            scope = this.dataScope;
         }
         
         // Process the dotNotation...
         // Adds in the additional "_alfValue" objects...
         dotNotation = this.alfProcessDataDotNotation(dotNotation);
         
         var data = lang.getObject(dotNotation, false, scope);
         if (data == null)
         {
            // The data item doesn't exist yet, create it now and register the caller
            // as the owner. Not sure if this is necessary, we can't tell if the widget is destroyed
         }
         // Set the new data...
         data = lang.getObject(dotNotation, true, scope);
         var oldValue = data._alfValue;
         lang.setObject(dotNotation + "._alfValue", value, scope);
         
         if (data._alfCallbacks != null)
         {
            // Move all the pending callbacks into the callback property
            for (var callbackId in data._alfCallbacks)
            {
               if (typeof data._alfCallbacks[callbackId] === "function")
               {
                  data._alfCallbacks[callbackId](dotNotation, oldValue, value);
               }
            }
         }
         return dataOwnerBinding;
      },
      
      /**
       * This gets the data from the location in the model defined by the scope. If no explicit scope
       * is provided then the instance scope will be used.
       * 
       * @instance
       * @param {string} dotNotation A dot notation representation of the location within the data model to get
       * @param {object} scope The scope to get the data from. If null then then instance scope will be used.
       * @returns {object} The data at the supplied location
       */
      alfGetData: function alfresco_core_Core__alfGetData(dotNotation, scope) {
         // If a data scope has not been set then get the root data model
         if (this.dataScope == null)
         {
            this.dataScope = CoreData.getSingleton().root;
         }
         if (scope == null)
         {
            scope = this.dataScope;
         }
         dotNotation = this.alfProcessDataDotNotation(dotNotation);
         var data = lang.getObject(dotNotation + "._alfValue", false, scope);
         this.alfLog("log", "Getting data", dotNotation, scope, data, this);
         return data;
      },
      
      /**
       * Binds a callback function to an entry in the data model so that when the data is changed the callback
       * will be executed. This allows widgets to respond to data changes dynamically. A reference to the 
       * call back will be returned and it is important that these callbacks are deleted when the widget
       * is destroyed to prevent memory leaks.
       * 
       * @instance
       * @param {string} dotNotation A dot notation representation of the location with the data model to bind to
       * @param {object} scope The scope to look for the dot notated data at
       * @param {function} callback The function to call when the data is changed
       * @returns {object} A reference to the callback so that it can be removed when the caller is destroyed 
       */
      alfBindDataListener: function alfresco_core_Core__alfBindDataListener(dotNotation, scope, callback) {
         if (dotNotation)
         {
            this.alfLog("log", "Binding data listener", dotNotation, scope, callback, this);
            if (this.dataScope == null)
            {
               this.dataScope = CoreData.getSingleton().root;
            }
            if (scope == null)
            {
               scope = this.dataScope;
            }
            // TODO: Validate the dotNotation??
            dotNotation = this.alfProcessDataDotNotation(dotNotation);
            
            var callbacks = lang.getObject(dotNotation + "._alfCallbacks", true, scope);
            var callbackId = this.generateUuid(); // Create a uuid for the callback
            callbacks[callbackId] = callback;     // Set the callback
            
            // Create and return the binding (this should provide enough information to delete the callback
            // from the data model when the owning widget is destroyed)
            var binding = {
               scope: this.dataScope,
               dotNotation: dotNotation,
               callbackId: callbackId
            };
            if (this.dataBindingCallbacks == null)
            {
               this.dataBindingCallbacks = [];
            }
            this.dataBindingCallbacks.push(binding);
            return binding;
         }
      },
      
      /**
       * @instance
       * @param {object} The binding object
       */
      alfRemoveDataListener: function alfresco_core_Core__alfRemoveDataListener(binding) {
         // Need to check my logic here (!?)
         this.alfLog("log", "Removing data binding", binding);
         try
         {
            var data = lang.getObject(binding.dotNotation, false, binding.scope);
            if (data != null)
            {
               delete data._alfCallbacks[binding.callbackId];
            }
         }
         catch(e)
         {
            this.alfLog("error", "Could not delete data listener binding", binding);
         }
      },
      
      /**
       * A String that is used to prefix all pub/sub communications to ensure that only relevant
       * publications are handled and issued.
       * 
       * @instance
       * @type {string}
       * @default ""
       */
      pubSubScope: "",
      
      /**
       * Used to track of any subscriptions that are made. They will be all be unsubscribed when the 
       * [destroy]{@link module:alfresco/core/Core#destroy} function is called.
       * 
       * @instance
       * @type {array}
       * @default null 
       */
      alfSubscriptions: null,
      
      /**
       * This function wraps the standard Dojo publish function. It should always be used rather than
       * calling the Dojo implementation directly to allow us to make changes to the implementation or
       * to introduce additional features (such as scoping) or updates to the payload.
       * 
       * @instance
       * @param {string} topic The topic on which to publish
       * @param {object} payload The payload to publish on the supplied topic
       * @param {boolean} global Indicates that the pub/sub scope should not be applied
       */
      alfPublish: function alfresco_core_Core__alfPublish(topic, payload, global) {
         var scopedTopic = (global ? "" : this.pubSubScope) + topic;
         payload.alfTopic = scopedTopic;
         pubSub.publish(scopedTopic, payload);
      },
      
      /**
       * This function wraps the standard Dojo subscribe function. It should always be used rather than
       * calling the Dojo implementation directly to allow us to make changes to the implementation or
       * to introduce additional features (such as scoping) or updates to the callback. The subscription
       * handle that gets created is add to [alfSubscriptions]{@link module:alfresco/core/Core#alfSubscriptions}
       * 
       * @instance
       * @param {string} topic The topic on which to subscribe
       * @param {function} callback The callback function to call when the topic is published on.
       * @param {boolean} global Indicates that the pub/sub scope should not be applied
       * @returns {object} A handle to the subscription
       */
      alfSubscribe: function alfresco_core_Core__alfSubscribe(topic, callback, global) {
         var scopedTopic = (global ? "" : this.pubSubScope) + topic;
         var handle = pubSub.subscribe(scopedTopic, callback);
         if (this.alfSubscriptions == null)
         {
            this.alfSubscriptions = [];
         }
         this.alfSubscriptions.push(handle);
         return handle;
      },
      
      /**
       * This function wraps the standard unsubscribe function. It should always be used rather than call
       * the Dojo implementation directly.
       * 
       * @instance
       * @param {object} The subscription handle to unsubscribe
       */
      alfUnsubscribe: function alfresco_core_Core__alfUnsubscribe(handle) {
         if (handle) 
         {
            handle.remove();
         }
      },

      /**
       * This function will override a destroy method if available (e.g. if this has been mixed into a
       * widget instance) so that any subscriptions that have been made can be removed. This is necessary
       * because subscriptions are not automatically cleaned up when the widget is destroyed.
       * 
       * This also removes any data binding listeners that have been registered.
       * 
       * @instance
       * @param {boolean} preserveDom
       */
      destroy: function alfresco_core_Core__destroy(preserveDom) {
         if (typeof this.inherited === "function")
         {
            this.inherited(arguments);
         }
         if (this.alfSubscriptions != null)
         {
            array.forEach(this.alfSubscriptions, function(handle, i) {
               if (typeof handle.remove === "function")
               {
                  handle.remove();
               }
            });
         }
         if (this.dataBindingCallbacks != null)
         {
            array.forEach(this.dataBindingCallbacks, function(binding, i) {
               this.alfRemoveDataListener(binding);
            }, this);
         }
      },

      /**
       * This function can be used to instantiate an array of widgets. Each widget configuration in supplied
       * widgets array is passed to the [processWidget]{@link module:alfresco/core/Core#processWidget} function
       * to handle it's creation.
       * 
       * @instance 
       * @param {Object[]} widgets An array of the widget definitions to instantiate
       * @param {DOM Element} rootNode The DOM node which should be used to add instantiated widgets to
       */
      processWidgets: function alfresco_core_Core__processWidgets(widgets, rootNode) {
         // There are two options for providing configuration, either via a JSON object or
         // via a URL to asynchronously retrieve the configuration. The configuration object
         // takes precedence as it will be faster by virtue of not requiring a remote call.
         
         // For the moment we'll just ignore handling the configUrl...
         var _this = this;
         if (widgets && widgets instanceof Array)
         {
            // Reset the processing complete flag (this is to support multiple invocations of widget processing)...
            this.widgetProcessingComplete = false;
            
            // TODO: Using these attributes will not support multiple calls to processWidgets from within the same object instance
            this._processedWidgetCountdown = widgets.length;
            this._processedWidgets = [];
            
            // Iterate over all the widgets in the configuration object and add them...
            array.forEach(widgets, lang.hitch(this, "processWidget", rootNode));
         }
      },
      
      /**
       * Creates a widget from the supplied configuration. The creation of each widgets DOM node
       * is delegated to the [createWidgetDomNode]{@link module:alfresco/core/Core#createWidgetDomNode} function 
       * and the actual instantiation of the widget is handled by the [createWidget]{@link module:alfresco/core/Core#createWidget} function.
       * Before creation of the widget begins the [filterWidget]{@link module:alfresco/core/Core#filterWidget} function is
       * called to confirm that the widget should be created. This allows extending classes the opportunity filter
       * out widget creation in specific circumstances.
       *
       * @instance
       * @param {DOM element} rootNode The DOM node where the widget should be created.
       * @param {object} widgetConfig The configuration for the widget to be created
       * @param {number} index The index of the widget configuration in the array that it was taken from
       */
      processWidget: function alfresco_core_Core__processWidget(rootNode, widgetConfig, index) {
         if (this.filterWidget(widgetConfig))
         {
            var domNode = this.createWidgetDomNode(widgetConfig, rootNode, widgetConfig.className);
            this.createWidget(widgetConfig, domNode, this._registerProcessedWidget, this);
         }
      },
      
      /**
       * This function is called from the [processWidget]{@link module:alfresco/core/Core#processWidget} function
       * in order to give a final opportunity for extending classes to prevent the creation of a widget in certain
       * circumstances. This was added to the core initially to allow the 
       * [_MultiItemRendererMixin]{@link module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin} 
       * to filter widget creation based on configurable properties of the items being rendered. However it is
       * expected that this can be used as an extension point in other circumstances. 
       *
       * @instance
       * @param {object} widgetConfig The configuration for the widget to be created
       * @returns {boolean} This always returns true by default.
       */
      filterWidget: function alfresco_core_Core__filterWidget(widgetConfig) {
         return true;
      },
      
      /**
       * Used to keep track of all the widgets created as a result of a call to the [processWidgets]{@link module:alfresco/core/Core#processWidgets} function
       * 
       * @instance
       * @type {Array}
       * @default null
       */
      _processedWidgets: null,
      
      /**
       * This is used to countdown the widgets that are still waiting to be created. It is initialised to the size
       * of the widgets array supplied to the [processWidgets]{@link module:alfresco/core/Core#processWidgets} function.
       * 
       * @instance 
       * @type {number}
       * @default null
       */
      _processedWidgetCountdown: null,
      
      /**
       * This function registers the creation of a widget. It decrements the 
       * [_processedWidgetCountdown]{@link module:alfresco/core/Core#_processedWidgetCountdown} attribute
       * and calls the [allWidgetsProcessed]{@link module:alfresco/core/Core#allWidgetsProcessed} function when it reaches zero.
       * 
       * @instance
       * @param {object} widget The widget that has just been processed.
       * @param {object} _this The current scope of the instantiating widget
       */
      _registerProcessedWidget: function alfresco_core_Core___registerProcessedWidget(widget, _this) {
         _this._processedWidgetCountdown--;
         _this._processedWidgets.push(widget);
         if (_this._processedWidgetCountdown == 0)
         {
            _this.allWidgetsProcessed(_this._processedWidgets);
            _this.widgetProcessingComplete = true;
         }
      },
      
      /**
       * This is set from false to true after the [allWidgetsProcessed]{@link module:alfresco/core/Core#allWidgetsProcessed}
       * extension point function is called. It can be used to check whether or not widget processing is complete. 
       * This is to allow for checks that widget processing has been completed BEFORE attaching a listener to the 
       * [allWidgetsProcessed]{@link module:alfresco/core/Core#allWidgetsProcessed} function.
       * 
       * @instance
       * @type {boolean}
       * @default false
       */
      widgetProcessingComplete: false,
      
      /**
       * This is an extension point for handling the completion of calls to [processWidgets]{@link module:alfresco/core/Core#processWidgets}
       * 
       * @instance
       * @param {Array} widgets An array of all the widgets that have been processed
       */
      allWidgetsProcessed: function alfresco_core_Core__allWidgetsProcessed(widgets) {
         this.alfLog("log", "All widgets processed");
      },
      
      /**
       * Handles the dependency management and instantiation of services required.
       * 
       * @instance
       * @param {Array} services An array of the services to be instantiated.
       */
      processServices: function alfresco_core_Core__processServices(services) {
         var _this = this;
         if (services)
         {
            // Iterate over all the widgets in the configuration object and add them...
            array.forEach(services, function(entry, i) {
               var dep = null,
                   config = {};
               if (typeof entry === "string")
               {
                  dep = entry;
               }
               else if (typeof entry === "object" && entry.name != null)
               {
                  dep = entry.name;
                  if (typeof entry.config === "object")
                  {
                     config = entry.config;
                  }
               }
               var requires = [dep];
               require(requires, function(ServiceType) {
                  new ServiceType(config);
               });
            });
         }
      },
      
      /**
       * Creates a new DOM node for a widget to use. The DOM node contains a child <div> element
       * that the widget will be attached to and an outer <div> element that additional CSS classes
       * can be applied to.
       * 
       * @instance
       * @param {object} widget The widget definition to create the DOM node for
       * @param {DOM Element} rootNode The DOM node to create the new DOM node as a child of
       * @param {string} rootClassName A string containing one or more space separated CSS classes to set on the DOM node
       */
      createWidgetDomNode: function alfresco_core_Core__createWidgetDomNode(widget, rootNode, rootClassName) {
         // Add a new <div> element to the "main" domNode (defined by the "data-dojo-attach-point"
         // in the HTML template)...
         var tmp = rootNode;
         if (rootClassName != null && rootClassName != "")
         {
            tmp = domConstruct.create("div", { className: rootClassName}, rootNode);
         }
         return domConstruct.create("div", {}, tmp);
      },
      
      /**
       * This method will instantiate a new widget having requested that its JavaScript resource and
       * dependent resources be downloaded. In principle all of the required resources should be available
       * if the widget is being processed in the context of the Surf framework and dependency analysis of 
       * the page has been completed. However, if this is being performed as an asynchronous event it may 
       * be necessary for Dojo to request additional modules. This is why the callback function is required
       * to ensure that successfully instantiated modules can be kept track of.
       * 
       * @instance
       * @param {object} config The configuration for the widget
       * @param {DOM Element} domNode The DOM node to attach the widget to
       * @param {function} callback A function to call once the widget has been instantiated
       * @param {Array} callbackArgs An array of arguments to pass to the callback function
       */
      createWidget: function alfresco_core_Core__createWidget(config, domNode, callback, callbackArgs) {
         
         var _this = this;
         this.alfLog("log", "Creating widget: ",config);
         
         // Make sure we have an instantiation args object...
         var initArgs = (config && config.config && (typeof config.config === 'object')) ? config.config : {};
         
         // Ensure that each widget has a unique id. Without this Dojo seems to occasionally
         // run into trouble trying to re-use an existing id...
         if (typeof initArgs.id == "undefined")
         {
            initArgs.id = config.name + "___" + this.generateUuid();
         }
         
         if (initArgs.generatePubSubScope === true)
         {
            // Generate a new pubSubScope if requested to...
            initArgs.pubSubScope = this.generateUuid();
         }
         else if (initArgs.pubSubScope === undefined)
         {
            // ...otherwise inherit the callers pubSubScope if one hasn't been explicitly configured...
            initArgs.pubSubScope = this.pubSubScope;
         }
         if (initArgs.dataScope === undefined)
         {
            initArgs.dataScope = this.dataScope;
         }
         
         // Create a reference for the widget to be added to. Technically the require statement
         // will need to asynchronously request the widget module - however, assuming the widget
         // has been included in such a way that it will have been included in the generated 
         // module cache then the require call will actually process synchronously and the widget
         // variable will be returned with an assigned value...
         var widget = null;
         
         // Dynamically require the specified widget
         // The use of indirection is done so modules will not rolled into a build (should we do one)
         var requires = [config.name];
         require(requires, function(WidgetType) {
            // Just to be sure, check that no widget doesn't already exist with that id and
            // if it does, generate a new one...
            if (registry.byId(initArgs.id) != null)
            {
               initArgs.id = config.name + "___" + _this.generateUuid();
            }
            
            // Instantiate the new widget
            // This is an asynchronous response so we need a callback method...
            widget = new WidgetType(initArgs, domNode);
            _this.alfLog("log", "Created widget", widget);
            widget.startup();
            if (config.assignTo != null)
            {
               _this[config.assignTo] = widget;
            }
            if (callback)
            {
               callback(widget, callbackArgs);
            }
         });
         
         if (widget == null)
         {
            this.alfLog("warn", "A widget was not declared so that it's modules were included in the loader cache", config, this);
         }
         return widget;
      },
      
      /**
       * Mixes in additional functions defined by a "functionMixins" instance property. This function 
       * is no longer commonly used and may not actually be required. It was initially provided
       * for form processing. It's being marked as deprecated as it may get deleted
       * 
       * @instance
       * @deprecated
       */
      processFunctionMixins: function alfresco_core_Core__processFunctionMixins() {
         var _this = this;
         if (this.functionMixins != null)
         {
           
            array.forEach(this.functionMixins, function(mixin, index) {
               var requires = [mixin];
               require(requires, function(MixinType) {
                  // This is an asynchronous response so we need a callback method...
                  var mixinInstance = new MixinType();
                  _this.alfLog("log", "Created mixin", mixinInstance);
                  declare.safeMixin(_this, mixinInstance);
               });
            });
         }
      },
      
      /**
       * This gets the relative time based on the supplied ISO dates. Currently this uses the original Alfresco
       * capability when available. 
       * 
       * @instance
       * @deprecated Use the version provided by the [TemporalUtils]{@link module:alfresco/core/TemporalUtils} mixin
       */
      getRelativeTime: function alfresco_core_Core__getRelativeTime(from, to) {
         
         var relativeTime = "";
         if (Alfresco && Alfresco.util && typeof Alfresco.util.relativeTime === "function")
         {
            relativeTime = Alfresco.util.relativeTime(from, to);
         }
         else
         {
            // Use the Dojo time to output a more friendly date...
            // TODO: This should be made relative!!
            relativeTime = stamp.fromISOString(from);
            if (relativeTime)
            {
               relativeTime = relativeTime.toGMTString()
            }
            else
            {
               relativeTime = "";
            }
         }
         return relativeTime;
      },
      
      /**
       * @instance
       * @type {string}
       * @default "ALF_LOG_REQUEST"
       */
      alfLoggingTopic: "ALF_LOG_REQUEST",
      
      /**
       * This function is intended to provide the entry point to all client-side logging from the application. By 
       * default it simply delegates to the standard browser console object but could optionally be overridden or
       * extended to provide advanced capabilities like posting client-side logs back to the server, etc.
       * 
       * @instance
       * @param {string} severity The severity of the message to be logged
       * @param {string} message The message to be logged
       */
      alfLog: function alfresco_core_Core__alfLog(severity, message) {
         this.alfPublish(this.alfLoggingTopic, {
            callerName: arguments.callee.caller.name,
            severity: severity,
            messageArgs: Array.prototype.slice.call(arguments, 1)
         }, true);
      }
   });
});