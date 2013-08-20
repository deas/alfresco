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
 * @module alfresco/renderers/Tags
 * @extends module:alfresco/renderers/InlineEditProperty
 * @mixes dijit/_OnDijitClickMixin
 * @mixes module:alfresco/core/CoreXhr
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/renderers/InlineEditProperty", 
        "dijit/_OnDijitClickMixin",
        "alfresco/core/ObjectTypeUtils",
        "alfresco/core/CoreXhr",
        "dojo/text!./templates/Tags.html",
        "dojo/_base/array",
        "dojo/_base/lang",
        "alfresco/renderers/ReadOnlyTag",
        "alfresco/renderers/EditTag",
        "dojo/dom-construct",
        "dijit/registry",
        "dojo/on",
        "dojo/dom-attr",
        "dojo/dom-class",
        "dojo/keys",
        "dojo/_base/event",
        "dojo/store/JsonRest",
        "dijit/form/ComboBox",
        "dijit/form/nls/ComboBox",
        "dijit/form/nls/validate",
        "dojo/store/util/QueryResults",
        "dojo/store/util/SimpleQueryEngine",
        "dojo/data/util/filter",
        "dojo/aspect",
        "dojo/html"], 
        function(declare, Property, _OnDijitClickMixin, ObjectTypeUtils, CoreXhr, template, array, lang, ReadOnlyTag, EditTag, 
                 domConstruct, registry, on, domAttr, domClass, keys, event, JsonRest, ComboBox, NlsComboBox, validate, QueryResults, 
                 SimpleQueryEngine, filter, aspect, html) {

   return declare([Property, _OnDijitClickMixin, CoreXhr], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {{cssFile: string, media: string}[]}
       * @default [{cssFile:"./css/Tags.css"}]
       */
      cssRequirements: [{cssFile:"./css/Tags.css"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {string}
       */
      templateString: template,
      
      /**
       * 
       * @instance
       */
      postMixInProperties: function alfresco_renderers_Tags__postMixInProperties() {
         this.inherited(arguments);
      },
      
      /**
       * @instance
       * @type {object[]}
       * @default null
       */
      currentReadOnlyTags: null,
      
      /**
       * Overrides the [inherited function]{@link module:alfresco/renderers/Property#getRenderedProperty} to convert the tags
       * value into visual tokens.
       * 
       * @instance
       */
      getRenderedProperty: function alfresco_renderers_Tags__getRenderedProperty(value) {
         // Reset the tags...
         var renderedValue = null;
         array.forEach(this.currentTags, lang.hitch(this, "destroyTag"));
         this.currentTags = [];
         if (!ObjectTypeUtils.isArray(value))
         {
            this.alfLog("warn", "Expected an array value for tags", this, value);
         }
         else
         {
            array.forEach(value, lang.hitch(this, "createReadOnlyTag", "name", "nodeRef"));
            renderedValue = ""; // By setting an empty string as a rendered value the inherited postMixInProperties function knows that a value is set.
         }
         return renderedValue; // Always return the empty string
      },
  
      /**
       * By default this simply calls the destroy function of the tag widget (if it has one)
       * 
       * @instance
       * @param {object} tagWidget The tag to be destroyed.
       */
      destroyTag: function alfresco_renderers_Tags__destroyTag(tagWidget) {
         if (typeof tagWidget.destroy === "function")
         {
            tagWidget.destroy();
         }
      },
      
      /**
       * Called from [getRenderedProperty]{@link module:alfresco/renderers/Tags#getRenderedProperty} to render an individual
       * read only tag.
       * 
       * @instance
       * @param {string} nameAttribute The attribute to use from the tagData object as the tag name
       * @param {string} valueAttribute The attribute to use from the tagData object as the tag value
       * @param {object} tagData The tag data to render
       * @param {number} index The index of the tag in the overall array
       */
      createReadOnlyTag: function alfresco_renderers_Tags__createReadOnlyTag(nameAttribute, valueAttribute, tagData, index) {
         if (tagData == null || tagData[nameAttribute] == null)
         {
            this.alfLog("warn", "No '" + nameAttribute + "' attribute for tag", this, tag);
         }
         else
         {
            var tagWidget = new ReadOnlyTag({
               tagName: tagData[nameAttribute],
               tagValue: tagData[valueAttribute]
            });
            this.currentTags.push(tagWidget);
         }
      },
      
      /**
       * Extends the [inherited function]{@link module:alfresco/renderers/InlineEditProperty#postCreate} to add a custom
       * event handler for "ALF_REMOVE_TAG" events that are fired from descendant DOM nodes and to iterate over the
       * [read only tags]{@link module:alfresco/renderers/ReadOnlyTag} created by calls to the [createReadOnlyTag]{@link module:alfresco/renderers/Tags#createReadOnlyTag}
       * function and calls [placeTag]{@link module:alfresco/renderers/Tags#placeTag} on each of them to add them
       * to the widget.
       * 
       * @instance
       */
      postCreate: function alfresco_renderers_Tags__postCreate() {
         this.inherited(arguments);
         on(this.editTagsNode, "ALF_REMOVE_TAG", lang.hitch(this, "onRemoveTag"));
         array.forEach(this.currentTags, lang.hitch(this, "placeReadOnlyTag"));
      },
      
      /**
       * @instance
       * @param {object} tagWidget The tag widget to place.
       * @param {number} index The index of the tag
       */
      placeReadOnlyTag: function alfresco_renderers_Tags__placeReadOnlyTag(tagWidget, index) {
         tagWidget.placeAt(this.renderedValueNode);
      },
      
      /**
       * Extends the [inherited function]{@link module:alfresco/renderers/InlineEditProperty#onEditClick} to 
       * create the edit tag instances.
       * 
       * @instance
       */
      onEditClick: function alfresco_renderers_Tags__onEditClick() {
         this.inherited(arguments);
         array.forEach(this.currentTags, lang.hitch(this, "createEditTag", "tagName", "tagValue"));
         if (this.tagStore == null)
         {
            this.tagStore = new JsonRest({
               target: Alfresco.constants.PROXY_URI + "api/forms/picker/category/workspace/SpacesStore/tag:tag-root/children"
            });
            aspect.before(this.tagStore, "query", lang.hitch(this, "interceptQuery"));
         }
         if (this.comboBox == null)
         {
            this.comboBox = new ComboBox({
               store: this.tagStore,
               searchAttr: "name",
               queryExpr: "${0}"
            }, this.editInputNode);
            aspect.before(this.comboBox, "_openResultList", lang.hitch(this, "interceptResults"))
         }
      },
      
      /**
       * Added as an aspect before the query function of the tag store to change the query string. This 
       * is done so that query passed in the XHR request will get the correct results. The reason this
       * is necessary is because the XHR request may not return the data in the standard Dojo pattern
       * so this function allows the query argument (which is configured to work with the ComboBox) to 
       * be updated to work with the back-end WebScript.
       * 
       * @instance
       * @param {object} query The query that will be passed to the query function
       * @param {object} options The query options that will be passed to the query function
       * @returns {object[]} The updated arguments to pass to the query function.
       */
      interceptQuery: function alfresco_renderers_Tags__interceptQuery(query, options) {
         var updatedQuery = {
            selectableType: "cm:category",
            size: "100",
            aspect: "cm:taggable",
            searchTerm: query.name
         }
         return [query, options];
      },
      
      /**
       * Added as an aspect before the _openResultList function to update the results arguments that is
       * passed. This is required because the XHR response is not in the schema required by the ComboBox
       * so it is necessary to modify it to contain the correct data.
       * 
       * @instance
       * @param {object} res The results that were returned from the XHR request
       * @param {object} query The query configuration used to request the results
       * @param {object} options The options used to query the results
       * @returns {object[]} The updated arguments to pass to the query function.
       */
      interceptResults: function alfresco_renderers_Tags__interceptResults(res, query, options) {
         var updatedResults = res.data.items;
         var updatedQuery = {
            name: new RegExp("^" + query.name.toString() + ".*$")
         }; 
         var results = QueryResults(SimpleQueryEngine(updatedQuery)(updatedResults));
         return [results, updatedQuery, options];
      },
      
      /**
       * Called from [onEditClick]{@link module:alfresco/renderers/Tags#onEditClick} to render an individual
       * edit tag.
       * 
       * @instance
       * @param {string} nameAttribute The attribute to use from the tagData object as the tag name
       * @param {string} valueAttribute The attribute to use from the tagData object as the tag value
       * @param {object} tagData The read-only tag widget to create a corresponding edit tag for.
       * @param {number} index The index of the tag in the overall array
       */
      createEditTag: function alfresco_renderers_Tags__createEditTag(nameAttribute, valueAttribute, tagData, index) {
         var tagWidget = new EditTag({
            tagName: tagData[nameAttribute],
            tagValue: tagData[valueAttribute]
         });
         tagWidget.placeAt(this.editTagsNode);
      },
      
      /**
       * Handles "ALF_REMOVE_TAG" events that are emitted from a DOM node descendant. The event target
       * should map to a previously created edit tag that can then be destroyed.
       * 
       * @instance
       */
      onRemoveTag: function alfresco_renderers_Tags__onRemoveTag(evt) {
         var tagWidget = registry.byNode(evt.target);
         if (tagWidget != null)
         {
            tagWidget.destroy();
         }
      },
      
      /**
       * @instance
       */
      onPropertyUpdate: function alfresco_renderers_Tags__onSaveSuccess(response, originalConfig) {
         // This needs to be updated to render the tags...
         html.set(this.renderedValueNode, "");
         array.forEach(this.currentTags, lang.hitch(this, "destroyTag"));
         this.currentTags = [];
         var editTags = registry.findWidgets(this.editTagsNode);
         array.forEach(editTags, lang.hitch(this, "createReadOnlyTag", "tagName", "tagValue"));
         array.forEach(this.currentTags, lang.hitch(this, "placeReadOnlyTag"));
         array.forEach(editTags, lang.hitch(this, "destroyTag"));
         domClass.remove(this.renderedValueNode, "hidden faded");
         domClass.add(this.editNode, "hidden");
         this.renderedValueNode.focus();
      },
      
      /**
       * This function is connected via the widget template. It occurs whenever a key is pressed whilst
       * focus is on the input field for updating the property value. All keypress events other than the
       * enter and escape key are ignored. Enter will save the data, escape will cancel editing
       * 
       * @instance
       * @param {object} e The key press event
       */
      onValueEntryKeyPress: function alfresco_renderers_Tags__onValueEntryKeyPress(e) {
         if(e.charOrCode == keys.ESCAPE)
         {
            event.stop(e);
            this.onCancel();
         }
         else if(e.charOrCode == keys.ENTER)
         {
            event.stop(e);
            var inputValue = this.comboBox.get("value");
            if (inputValue != "")
            {
               this.comboBox.set("value", "");
               this.createRemoteTag(inputValue, false);
            }
            else
            {
               this.onSave();
            }
         }
         else if (e.charOrCode == keys.PAGE_DOWN ||
                  e.charOrCode ==  keys.DOWN_ARROW ||
                  e.charOrCode == keys.PAGE_UP ||
                  e.charOrCode ==  keys.UP_ARROW)
         {
            // Prevent up/down keys from bubbling. This is done to ensure that the 
            // focus doesn't shift to the previous or next item in the view.
            event.stop(e);
         }
      },
      
      /**
       * Gets the URL to use when creating remote tags. 
       * 
       * @instance
       * @returns {string} The URL to use when remotely creating tags
       */
      getCreateRemoteTagURL: function alfresco_renderers_Tags__createRemoteTag() {
         return Alfresco.constants.PROXY_URI + "api/tag/workspace/SpacesStore"; 
      },
      
      /**
       * Creates and returns an object to post to the URL returned by [getCreateRemoteTagURL]{@link module:alfresco/renderers/Tags#getCreateRemoteTagURL}
       * in order to create a new tag.
       * 
       * @instance
       * @param {string} tagName The name of the tag to create
       * @returns {object} The data to post in order to create a new tag.
       */
      getCreateRemoteTagData: function alfresco_renderers_Tags__getCreateRemoteTagData(tagName) {
         return {
            name: tagName
         };
      },
      
      /**
       * Creates a tag at the remote store (the same location from which available tags are retrieved). This
       * function is called regardless of whether or not an existing tag was created. It is expected that
       * the REST API will be able to handle duplicated (e.g. not recreate a duplicate but just return the
       * details of the existing tag).
       * 
       * @instance
       * @param {string} tagName The name of the tag to create.
       * @param {boolean} saveTagsAfterCreate Indicates whether or not to save all tags on successful creation.
       * @return {object} The created tag details
       */
      createRemoteTag: function alfresco_renderers_Tags__createRemoteTag(tagName, saveTagsAfterCreate) {
         var config = {
            url: this.getCreateRemoteTagURL(),
            method: "POST",
            saveTagsAfterCreate: saveTagsAfterCreate,
            data: this.getCreateRemoteTagData(tagName),
            successCallback: this.onCreateRemoteTagSuccess,
            failureCallback: this.onCreateRemoteTagFailure,
            callbackScope: this
         }
         this.serviceXhr(config);
      },
      
      /**
       * @instance
       * @param {object} response The response from the request to create a tag
       * @param {object} originalRequestConfig The object passed when making the request
       */
      onCreateRemoteTagSuccess: function alfresco_renderers_Tags__onCreateRemoteTagSuccess(response, originalRequestConfig) {
         this.createEditTag("name", "nodeRef", response);
         if (originalRequestConfig.saveTagsAfterCreate == true)
         {
            this.onSave();
         }
      },
      
      /**
       * @instance
       * @param {object} response The response from the request to create a tag
       * @param {object} originalRequestConfig The object passed when making the request
       */
      onCreateRemoteTagFailure: function alfresco_renderers_Tags__onCreateRemoteTagFailure(response, originalRequestConfig) {
         this.alfLog("error", "Could not create a tag", response, originalRequestConfig);
      },
      
      /**
       * This extends the [inherited function]{@link module:alfresco/renderers/InlineEditProperty#onSave} to check whether
       * or not there is anything selected in the ComboBox. If so it uses the data to create the edit tag rather than
       * saving. This ensures that all of the tags are captured and saved.
       * 
       * @instance
       */
      onSave: function alfresco_renderers_Tags__onSave() {
         var inputValue = this.comboBox.get("value");
         if (inputValue != "")
         {
            this.comboBox.set("value", "");
            this.createRemoteTag(inputValue, true);
         }
         else
         {
            this.inherited(arguments);
         }
      },
      
      /**
       * This extends the [inherited function]{@link module:alfresco/renderers/InlineEditProperty#onCancel} to ensure
       * that the previous ComboBox data is cleared.
       * 
       * @instance
       */
      onCancel: function alfresco_renderers_InlineEditProperty__onCancel() {
         this.inherited(arguments);
         this.comboBox.set("value", "");
         var editTags = registry.findWidgets(this.editTagsNode);
         array.forEach(editTags, lang.hitch(this, "destroyTag"));
      },
      /**
       * This overrides the [inherited function]{@link module:alfresco/renderers/InlineEditProperty#getSaveData} to
       * return an object with the [postParam]{@link module:alfresco/renderers/InlineEditProperty#postParam} set to
       * a comma delimited string of the values associated with each tag.
       * 
       * @instance
       * @returns {object} The data object to post when performing a save
       */
      getSaveData: function alfresco_renderers_Tags__getSaveData() {
         var editTags = registry.findWidgets(this.editTagsNode);
         var saveValue = "";
         for (var i=0; i<editTags.length; i++) 
         {
            saveValue = saveValue + "," + editTags[i].tagValue;
         }
         // Trim the first comma...
         if (saveValue.length > 0)
         {
            saveValue = saveValue.substring(1);
         }
         var data = {};
         data[this.postParam] = saveValue;
         return data;
      }
   });
});