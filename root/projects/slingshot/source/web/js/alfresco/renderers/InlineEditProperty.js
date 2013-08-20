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
 * Extends the standard (read-only) [property renderer]{@link module:alfresco/renderers/Property} to provide
 * the ability to edit and save changes to the property. Currently the implementation performs its own 
 * XHR post to save the date but it may be updated to use services to handle this action at some point in the
 * future.
 * 
 * @module alfresco/renderers/InlineEditProperty
 * @extends module:alfresco/renderers/Property
 * @mixes dijit/_OnDijitClickMixin
 * @mixes module:alfresco/core/CoreXhr
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/renderers/Property", 
        "dijit/_OnDijitClickMixin",
        "alfresco/core/CoreXhr",
        "dojo/text!./templates/InlineEditProperty.html",
        "dojo/dom-class",
        "dojo/html",
        "dojo/dom-attr",
        "dojo/_base/fx",
        "dojo/keys",
        "dojo/_base/event"], 
        function(declare, Property, _OnDijitClickMixin, CoreXhr, template, domClass, html, domAttr, fx, keys, event) {

   return declare([Property, _OnDijitClickMixin, CoreXhr], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {{cssFile: string, media: string}[]}
       * @default [{cssFile:"./css/InlineEditProperty.css"}]
       */
      cssRequirements: [{cssFile:"./css/InlineEditProperty.css"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {string}
       */
      templateString: template,
      
      /**
       * The is the name of the parameter that will be used to persist changes to the property
       * @instance
       * @type {string}
       * @default null
       */
      postParam: null,
      
      /**
       * This extends the inherited function to set the [postParam]{@link module:alfresco/renderers/InlineEditProperty#postParam]
       * attribute based on the [propertyToRender]{@link module:alfresco/renderers/InlineEditProperty#propertyToRender] if 
       * provided. It is expected that these will be different because the properties WebScript that this widget will use
       * by default to persist changes takes just the name of the property but this is expected to be nested within the
       * [currentItem]{@link module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin#currentItem}.
       * 
       * @instance
       */
      postMixInProperties: function alfresco_renderers_InlineEditProperty__postMixInProperties() {
         this.inherited(arguments);
         
         if (this.postParam == null && this.propertyToRender != null)
         {
            this.postParam = "prop_" + this.propertyToRender.replace(/:/g, "_");
         }
      },
      
      /**
       * This function is called whenever the user clicks on the edit icon. It hides the display DOM node
       * and shows the edit DOM nodes.
       * 
       * @instance
       */
      onEditClick: function alfresco_renderers_InlineEditProperty__onEditClick() {
         if (this.renderPropertyNotFound)
         {
            domAttr.set(this.editInputNode, "value", "");
         }
         domClass.toggle(this.renderedValueNode, "hidden");
         domClass.toggle(this.editNode, "hidden");
         this.editInputNode.focus(); // Focus on the input node so typing can occur straight away
      },
      
      /**
       * Checks for the CTRL-e combination and when detected moves into edit mode.
       * 
       * @instance
       * @param {object} evt The keypress event
       */
      onKeyPress: function alfresco_renderers_InlineEditProperty__onKeyPress(evt) {
         if (evt.ctrlKey == true && evt.charCode == "101")
         {
            // On ctrl-e simulate an edit click
            event.stop(evt);
            this.onEditClick();
         }
      },
      
      /**
       * This function is connected via the widget template. It occurs whenever a key is pressed whilst
       * focus is on the input field for updating the property value. All keypress events other than the
       * enter and escape key are ignored. Enter will save the data, escape will cancel editing
       * 
       * @instance
       * @param {object} e The key press event
       */
      onValueEntryKeyPress: function alfresco_renderers_InlineEditProperty__onValueEntryKeyPress(e) {
         if(e.charOrCode == keys.ESCAPE)
         {
            event.stop(e);
            this.onCancel();
         }
         else if(e.charOrCode == keys.ENTER)
         {
            event.stop(e);
            this.onSave();
         }
      },
      
      /**
       * @instance
       */
      onSave: function alfresco_renderers_InlineEditProperty__onSave() {
         // TODO: There's an argument to suggest that this should be handled via a pub/sub event ?
         if (this.postParam != null)
         {
            var config = {
               url: this.getSaveUrl(),
               method: "POST",
               data: this.getSaveData(),
               successCallback: this.onPropertyUpdate,
               failureCallback: this.onPropertyUpdateFailure,
               callbackScope: this
            }
            this.serviceXhr(config);
         }
         
         // TODO: Set some sort of indicator to show that a save operation is in flight?
      },
      
      /**
       * Retrieves a URL to use to perform a save by posting to.
       * 
       * @instance
       * @returns {string} The URL to perform an XHR post to save the data.
       */
      getSaveUrl: function alfresco_renderers_InlineEditProperty__getSaveUrl() {
         var nodeRef = new Alfresco.util.NodeRef(this.currentItem.nodeRef);
         var url = Alfresco.constants.PROXY_URI + "api/node/" + nodeRef.uri + "/formprocessor";
         return url;
      },
      
      /**
       * Gets the data object to post.
       * 
       * @instance
       * @returns {object} The data object to post.
       */
      getSaveData: function alfresco_renderers_InlineEditProperty__getSaveData() {
         var data = {};
         data[this.postParam] = domAttr.get(this.editInputNode, "value");
         return data;
      },
      
      /**
       * This function is called following a successful request to update the value of the rendered property.
       * 
       * @instance
       */
      onPropertyUpdate: function alfresco_renderers_InlineEditProperty__onSaveSuccess(response, originalConfig) {
         this.alfLog("log", "Property '" + this.propertyToRender + "' successfully updated for node: ", this.currentItem);
         this.renderedValue = originalConfig.data[this.postParam];
         
         // This is a bit ugly... there will be better ways to handle this...
         // Basically it's handling the situation where the prefix/suffix for cleared when data wasn't originally available...
         var prefix = (this.requestedValuePrefix) ? this.requestedValuePrefix : this.renderedValuePrefix;
         var suffix = (this.requestedValueSuffix) ? this.requestedValueSuffix : this.renderedValueSuffix;
         
         html.set(this.renderedValueNode, prefix + this.renderedValue + suffix);
         domClass.remove(this.renderedValueNode, "hidden faded");
         domClass.add(this.editNode, "hidden");
         this.renderedValueNode.focus();
      },
      
      /**
       * This function is called following a failed request to update the value of the rendered property. It delegates
       * handling the reset to the "onCancel" function.
       * 
       * @instance
       */
      onPropertyUpdateFailure: function alfresco_renderers_InlineEditProperty__onPropertyUpdateFailure(response, originalConfig) {
         this.alfLog("warn", "Property '" + this.propertyToRender + "' was not updated for node: ", this.currentItem);
         this.onCancel();
      },
      
      /**
       * @instance
       */
      onCancel: function alfresco_renderers_InlineEditProperty__onCancel() {
         domClass.remove(this.renderedValueNode, "hidden");
         domClass.add(this.editNode, "hidden");
         
         // Reset the input field...
         domAttr.set(this.editInputNode, "value", this.renderedValue);
         this.renderedValueNode.focus();
      },
      
      /**
       * @instance
       */
      showEditIcon: function alfresco_renderers_InlineEditProperty__showEditIcon() {
         fx.fadeIn({ node: this.editIconNode }).play();
      },
      
      /**
       * @instance
       */
      hideEditIcon: function alfresco_renderers_InlineEditProperty__hideEditIcon() {
         fx.fadeOut({ node: this.editIconNode }).play();
      }
   });
});