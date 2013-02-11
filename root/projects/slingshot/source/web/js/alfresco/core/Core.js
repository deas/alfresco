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
        "dijit/registry",
        "dojo/topic",
        "dojo/_base/array",
        "dojo/_base/lang",
        "dojo/dom-construct",
        "dojox/uuid/generateRandomUuid",
        "dojo/request/xhr",
        "dojo/json",
        "dojo/date/stamp"], 
        function(declare, registry, pubSub, array, lang, domConstruct, uuid, xhr, JSON, stamp) {
   
   return declare(null, {
      
      /**
       * Topics known to this object.
       * PLEASE NOTE: Use of this property is still at the conceptual phase. The idea being that topics
       *              could be discovered from available services. This area needs further investigation/work
       * 
       * @property {object} topics
       */
      topics: {
         dialog: {
            open: "OPEN_DIALOG",
            close: "CLOSE_DIALOG"
         }
      },
      
      /**
       * Creates and returns a new UUID (universally unique identifier). The UUID is generated using the
       * dojox/uuid/generateRandomUuid module
       * 
       * @method generateUuid
       * @returns {string} A new UUID
       */
      generateUuid: function alfresco_core_Core__generateUuid() {
         return uuid();
      },
      
      /**
       * This function is based on the version that can be found in alfresco.js. It searches through all of
       * the available scopes for the widget and for all of the widgets inherited from.
       * 
       * @method message
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
       * This function wraps the standard Dojo publish function. It should always be used rather than
       * calling the Dojo implementation directly to allow us to make changes to the implementation or
       * to introduce additional features (such as scoping) or updates to the payload.
       * 
       * @method alfPublish
       * @param {string} topic The topic on which to publish
       * @param {object} payload The payload to publish on the supplied topic
       */
      alfPublish: function alfresco_core_Core__alfPublish(topic, payload) {
         payload.alfTopic = topic;
         pubSub.publish(topic, payload);
      },
      
      /**
       * This function wraps the standard Dojo subscribe function. It should always be used rather than
       * calling the Dojo implementation directly to allow us to make changes to the implementation or
       * to introduce additional features (such as scoping) or updates to the callback.
       * 
       * @method alfSubscribe
       * @param {string} topic The topic on which to subscribe
       * @param {function} callback The callback function to call when the topic is published on.
       */
      alfSubscribe: function alfresco_core_Core__alfSubscribe(topic, callback) {
         var handle = pubSub.subscribe(topic, callback);
         return handle;
      },
      
      /**
       * This function wraps the standard unsubscribe function. It should always be used rather than call
       * the Dojo implementation directly.
       */
      alfUnsubscribe: function alfresco_core_Core__alfUnsubscribe(handle) {
         if (handle) 
         {
            handle.remove();
         }
      },
      
      /**
       * This function can be used to instantiate an array of widgets. The create of each widgets DOM node
       * is delegated to the "createWidgetDomNode" function and the actual instantiation of the widget is
       * handled by the "createWidget" function.
       * 
       * @method processWidgets 
       * @param {array} widgets An array of the widget definitions to instantiate
       * @param {element} rootNode The DOM node which should be used to add instantiated widgets to
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
            array.forEach(widgets, function(entry, i) {
               var domNode = _this.createWidgetDomNode(entry, rootNode, entry.className);
               _this.createWidget(entry.name, entry.config, domNode, _this._registerProcessedWidget, _this);
            });
         }
      },
      
      /**
       * This will keep track of all the widgets created as a result of a call to the processWidgets function
       * 
       * @property {array} _processedWidgets An array of the widgets being processed or that have been processed.
       * @default null
       */
      _processedWidgets: null,
      
      /**
       * This is used to countdown the widgets that are still waiting to be created. It is initialised to the size
       * of the widgets array supplied to the "processWidgets" function.
       * 
       * @property {integer} _processedWidgetCountdown
       * @default null
       */
      _processedWidgetCountdown: null,
      
      /**
       * This function registers the creation of a widget. It decrements the "_processedWidgetCountdown" attribute
       * and calls the "allWidgetsProcessed" function when it reaches zero.
       * 
       * @method _registerProcessedWidget
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
       * This is set from false to true after the allWidgetsProcessed extension point function is called. It can be used
       * to check whether or not widget processing is complete. This is to allow for checks that widget processing has been
       * completed BEFORE attaching a listener to the allWidgetsProcessed function.
       * 
       * @property {boolean} widgetProcessingComplete
       * @default false
       */
      widgetProcessingComplete: false,
      
      /**
       * This is an extension point for handling the completion of calls to "processWidgets"
       * 
       * @method allWidgetsProcessed
       * @param {array} widgets An array of all the widgets that have been processed
       */
      allWidgetsProcessed: function alfresco_core_Core__allWidgetsProcessed(widgets) {
         this.alfLog("log", "All widgets processed");
      },
      
      /**
       * Handles the dependency management and instantiation of services required.
       * 
       * @method processServices
       * @param {array} services An array of the services to be instantiated.
       */
      processServices: function alfresco_core_Core__processServices(services) {
         var _this = this;
         if (services)
         {
            // Iterate over all the widgets in the configuration object and add them...
            array.forEach(services, function(entry, i) {
               var requires = [entry];
               require(requires, function(ServiceType) {
                  new ServiceType();
               });
            });
         }
      },
      
      /**
       * Creates a new DOM node for a widget to use. The DOM node contains a child <div> element
       * that the widget will be attached to and an outer <div> element that additional CSS classes
       * can be applied to.
       * 
       * @method createWidgetDomNode
       * @param {object} widget The widget definition to create the DOM node for
       * @param {element} rootNode The DOM node to create the new DOM node as a child of
       * @param {string} rootClassName A string containing one or more space separated CSS classes to set on the DOM node
       */
      createWidgetDomNode: function alfresco_core_Core__createWidgetDomNode(widget, rootNode, rootClassName) {
         // Add a new <div> element to the "main" domNode (defined by the "data-dojo-attach-point"
         // in the HTML template)...
         var className = (rootClassName) ? rootClassName : "";
         var outerDiv = domConstruct.create("div", { className: className}, rootNode);
         var innerDiv = domConstruct.create("div", {}, outerDiv);
         return innerDiv;
      },
      
      /**
       * This method will instantiate a new widget having requested that its JavaScript resource and
       * dependent resources be downloaded. In principle all of the required resources should be available
       * if the widget is being processed in the context of the Surf framework and dependency analysis of 
       * the page has been completed. However, if this is being performed as an asynchronous event it may 
       * be necessary for Dojo to request additional modules. This is why the callback function is required
       * to ensure that successfully instantiated modules can be kept track of.
       * 
       * @method createWidget
       * @param {string} type The module to require/instantiate
       * @param {object} config The configuration to pass as an instantiation argument to the widget
       * @param {element} domNode The DOM node to attach the widget to
       * @param {function} callback A function to call once the widget has been instantiated
       * @param {array} callbackArgs An array of arguments to pass to the callback function
       */
      createWidget: function alfresco_core_Core__createWidget(type, config, domNode, callback, callbackArgs) {
         
         var _this = this;
         this.alfLog("log", "Creating widget: " + type, config);
         
         // Make sure we have an instantiation args object...
         var initArgs = (config && (typeof config === 'object')) ? config : {};
         
         // Ensure that each widget has a unique id. Without this Dojo seems to occasionally
         // run into trouble trying to re-use an existing id...
         if (typeof initArgs.id == "undefined")
         {
            initArgs.id = type + "___" + this.generateUuid();
         }
         
         // Just to be sure, check that no widget doesn't already exist with that id and
         // if it does, generate a new one...
         if (registry.byId(initArgs.id) != null)
         {
            initArgs.id = type + "___" + this.generateUuid();
         }
         
         // Dynamically require the specified widget
         // The use of indirection is done so modules will not rolled into a build (should we do one)
         var requires = [type];
         require(requires, function(WidgetType) {
            // Instantiate the new widget
            
            // This is an asynchronous response so we need a callback method...
            var widget = new WidgetType(initArgs, domNode);
            _this.alfLog("log", "Created widget", widget);
            widget.startup();
            if (callback)
            {
               callback(widget, callbackArgs);
            }
         });
      },
      
      /**
       * Mixes in additional functions defined by a "functionMixins" instance property.
       * 
       * @method processFunctionMixins
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
       * This function handles displaying popup messages. It currently uses the legacy YUI functions that are defined
       * in alfresco.js and are expected to be available in the JavaScript global namespace until Share is merged
       * completely to the new UI framework. At some point in time this function should be updated to use alternative
       * means of displaying a message. 
       * 
       * @method displayMessage
       * @param msg {String} The message to be displayed.
       */
      displayMessage: function alfresco_core_Core__displayMessage(msg) {
         if (Alfresco && Alfresco.util && Alfresco.util.PopupManager)
         {
            Alfresco.util.PopupManager.displayMessage({
               text: msg
            });
         }
         else
         {
            this.alfLog("error", "Alfresco.util.PopupManager not available for handling displayMessage requests.");
         }
      },
      
      /**
       * This function handles displaying popup messages that require some acknowledgement. 
       * It currently uses the legacy YUI functions that are defined
       * in alfresco.js and are expected to be available in the JavaScript global namespace until Share is merged
       * completely to the new UI framework. At some point in time this function should be updated to use alternative
       * means of displaying a message. 
       * 
       * @method displayMessage
       * @param msg {String} The message to be displayed.
       */
      displayPrompt: function alfresco_core_Core__displayPrompt(msg) {
         if (Alfresco && Alfresco.util && Alfresco.util.PopupManager)
         {
            Alfresco.util.PopupManager.displayPrompt({
               text: msg
            });
         }
         else
         {
            this.alfLog("error", "Alfresco.util.PopupManager not available for handling displayMessage requests.");
         }
      },
      
      /**
       * This gets the relative time based on the supplied ISO dates. Currently this uses the original Alfresco
       * capability when available. 
       * 
       * TODO: Replace with own implementation
       * TODO: Move out of Core - this should be in a separate mixin
       * 
       * @method getRelativeTime
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
       * This function is intended to provide the entry point to all client-side logging from the application. By 
       * default it simply delegates to the standard browser console object but could optionally be overridden or
       * extended to provide advanced capabilities like posting client-side logs back to the server, etc.
       * 
       * @method alfLog
       * @param {string} severity The severity of the message to be logged
       * @param {string} message The message to be logged
       */
      alfLog: function alfresco_core_Core__alfLog(severity, message) {
         
         if (Alfresco && Alfresco.logging)
         {
            if (arguments.length < 2)
            {
               // Catch developer errors !!
               console.error("AlfLog was not supplied enough arguments. A severity and message are the minimum required arguments.", arguments);
            }
            else if (typeof console[severity] != "function")
            {
               // Catch developer errors !!
               console.error("The supplied severity is not a function of console", severity);
            }
            else
            {
               // Call the console method passing all the additional arguments)...
               var callerName = arguments.callee.caller.name;
               if (callerName && callerName != "")
               {
                  var re1 = /([^_])(_){1}/g,
                      re2 = /(\/_)/g;
                  callerName = callerName.replace(re1, "$1/").replace(re2, "[") + "] >> ";
               }
               else
               {
                  callerName = "";
               }
               message = callerName + message;
               var messageArgs = [message];
               for (var i=2; i<arguments.length; i++)
               {
                  messageArgs.push(arguments[i]);
               }
               console[severity].apply(console, messageArgs);
            }
         }
      }
   });
});