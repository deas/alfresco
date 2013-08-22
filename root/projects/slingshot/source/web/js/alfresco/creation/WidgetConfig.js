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
 * @module alfresco/creation/WidgetConfig
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/WidgetConfig.html",
        "alfresco/core/Core",
        "dojo/_base/lang",
        "dijit/registry",
        "dojo/_base/array",
        "dijit/form/Button",
        "dojo/dom-class"], 
        function(declare, _Widget, _Templated, template, AlfCore, lang, registry, array, Button, domClass) {
   
   return declare([_Widget, _Templated, AlfCore], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/WidgetConfig.css"}],
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type i18nRequirements {Array}
       */
      i18nRequirements: [{i18nFile: "./i18n/WidgetConfig.properties"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type template {String}
       */
      templateString: template,
      
      /**
       * This will need to be prefixed with a scope.
       * 
       * @instance
       * @type {string}
       * @default ""
       */
      configTopic: "ALF_CONFIGURE_WIDGET",
      
      /**
       * Can be used to clear the currently displayed widget.
       */
      clearTopic: "ALF_CLEAR_CONFIGURE_WIDGET",
      
      /**
       * @instance
       */
      postCreate: function alfresco_creation_WidgetConfig__postCreate() {
         this.alfSubscribe(this.configTopic, lang.hitch(this, "displayWidgetConfig"));
         this.alfSubscribe(this.clearTopic, lang.hitch(this, "clearCurrentDisplay"));
         
         this.saveButton = new Button({
            label: "Save",
            onClick: lang.hitch(this, "saveWidgetConfig")
         }, this.controlsNode);
         
         // Hide the button...
         domClass.add(this.saveButton.domNode, "share-hidden");
      },
      
      /**
       * The configuration for the currently displayed widget.
       * 
       * @instance
       * @default
       */
      currentWidgetConfig: null,
      
      /**
       * Handler for the save button that published the updated configuration for a widget so
       * that it can be re-rendered.
       * 
       * @instance
       */
      saveWidgetConfig: function alfresco_creation_WidgetConfig__saveWidgetConfig() {
         if (this.currentWidgetConfig != null)
         {
            // Get the values for the current widgets...
            var updatedConfig = {};
            var currentWidgets = registry.findWidgets(this.configNode);
            for (var i=0; i<currentWidgets.length; i++)
            {
               var c = currentWidgets[i];
               updatedConfig[c.get("name")] = c.getValue();
            }
            
            this.alfPublish("ALF_UPDATE_RENDERED_WIDGET", {
               node: this.currentWidgetConfig.selectedNode,
               updatedConfig: updatedConfig,
               originalConfig: this.currentWidgetConfig.selectedItem
            });
            
            // Clear the display...
            this.clearCurrentDisplay();
         }
      },
      
      /**
       * Clears the currently displayed widget configuration.
       * 
       * @instance
       */
      clearCurrentDisplay: function alfresco_creation_WidgetConfig__clearCurrentDisplay() {
         var currentWidgets = registry.findWidgets(this.configNode);
         array.forEach(currentWidgets,  lang.hitch(this, "destroyOldConfigWidget"));
         domClass.add(this.saveButton.domNode, "share-hidden");
      },
      
      /**
       * This function is called whenever a new widget is selected that requires configuration.
       * 
       * @instance
       */
      displayWidgetConfig: function alfresco_creation_WidgetConfig__displayWidgetConfig(payload) {
         
         // Clear the previous widgets...
         this.clearCurrentDisplay();
         
         // Create the new widgets...
         if (lang.exists("selectedItem.data.configWidgets", payload))
         {
            // Save the current widget configuration...
            this.currentWidgetConfig = payload;
            
            // Create the controls for configuring the widget...
            this.processWidgets(payload.selectedItem.data.configWidgets, this.configNode);
            
            domClass.remove(this.saveButton.domNode, "share-hidden");
         }
         // TODO: THE BLOCK ABOVE NEEDS DELETING ONCE THE OTHER WIDGETS HAVE BEEN UPDATED TO NOT RELY ON THE "DATA" OBJECT
         if (lang.exists("selectedItem.widgetsForConfig", payload))
         {
            // Save the current widget configuration...
            this.currentWidgetConfig = payload;
            
            // Create the controls for configuring the widget...
            this.processWidgets(payload.selectedItem.widgetsForConfig, this.configNode);
            
            domClass.remove(this.saveButton.domNode, "share-hidden");
         }
      },
      
      allWidgetsProcessed: function alfresco_creation_WidgetConfig__allWidgetsProcessed(widgets) {
         this.alfLog("log", "Widget config processed");
      },
      
      /**
       * Simply calls destroyRecursive on the supplied widget, but abstracted to it's own function for
       * the purposes of extensibility.
       * 
       * @instance
       * @param {object} The widget to destroy
       * @param {number} The index of the widget in the config panel
       */
      destroyOldConfigWidget: function alfresco_creation_WidgetConfig__destroyOldConfigWidget(widget, index) {
         widget.destroyRecursive(false);
      }
      
   });
});