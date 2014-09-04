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
 * @module alfresco/layout/AlfSideBarContainer
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @mixes module:alfresco/core/CoreWidgetProcessing
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "alfresco/core/ResizeMixin",
        "alfresco/services/_PreferenceServiceTopicMixin",
        "dojo/text!./templates/AlfSideBarContainer.html",
        "alfresco/core/Core",
        "alfresco/core/CoreWidgetProcessing",
        "dijit/layout/BorderContainer",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojo/dom-style",
        "dojo/dom-class",
        "dojo/on",
        "dojo/dom-geometry",
        "dojo/window"], 
        function(declare, _WidgetBase, _TemplatedMixin, ResizeMixin, _PreferenceServiceTopicMixin, template, AlfCore, CoreWidgetProcessing, BorderContainer, lang, array, domStyle, domClass, on, domGeom, win) {
   
   return declare([_WidgetBase, _TemplatedMixin, ResizeMixin, _PreferenceServiceTopicMixin, AlfCore, CoreWidgetProcessing], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{cssFile:"./css/AlfSideBarContainer.css"}]
       */
      cssRequirements: [{cssFile:"/themes/lightTheme/yui/assets/skin.css"},
                        {cssFile:"/css/yui-fonts-grids.css"},
                        {cssFile:"./css/AlfSideBarContainer.css"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {string}
       */
      templateString: template,
      
      /**
       * This uses the YUI YAHOO.util.Resize widget.
       * 
       * @instance
       * @type {String[]} 
       */
      nonAmdDependencies: ["/yui/yahoo-dom-event/yahoo-dom-event.js",
                           "/yui/dragdrop/dragdrop.js",
                           "/yui/element/element.js",
                           "/yui/animation/animation.js",
                           "/yui/resize/resize.js"],


      /**
       * Indicates whether or not to show the sidebar when initially rendered.
       * @instance
       * @type {boolean} 
       */
      showSidebar: true,
      
      /**
       * The minimum width (in pixels) for the sidebar
       * @instance
       * @type {number} 
       * @default 150
       */
      minSidebarWidth: 150,
      
      /**
       * The initial width (in pixels) of the sidebar
       * @instance
       * @type {number} 
       * @default 200
       */
      initialSidebarWidth: 150,
      
      /**
       * The last registered width (in pixels) of the sidebar (needed for window resize events)
       * @instance
       * @type {number}
       * @default null 
       */
      lastSidebarWidth: null,
      
      /**
       * The YAHOO.util.Resize control
       * @instance
       * @type {object}
       * @default null 
       */
      resizer: null,
      
      /**
       * This will be set to the resize drag handle created by the YUI Resize widget
       * @instance
       * @type {element}
       * @default null 
       */
      resizeHandlerNode: null,
      
      /**
       * This property allows the height of the sidebar to accommodate a "sticky" footer. The height is otherwise calculated as
       * the height of the view port minus the top position of the side bar (unless either side bar or main content are larger).
       * By setting this property it is possible to also deduct the height of a sticky footer. 
       * 
       * @instance
       * @type {integer} 
       * @default 0
       */
      footerHeight: 0,
      
      /**
       * It is possible to optionally provide an array of events that the widget should
       * subscribe to that trigger resize events. This has initially been added to address the problem that occurred when the
       * alfresco/wrapped/DocumentList widget would resize itself after the initial sizing causing the sidebar to render incorrectly.
       * By allowing custom events to be subscribed to it is possible to work around issues such as these.
       * 
       * @instance
       * @type {array} 
       * @default null
       */
      customResizeTopics: null,
      
      /**
       * @instance
       * @type {string}
       * @default "org.alfresco.share.sidebarWidth"
       */
      sidebarWidthPreferenceId: "org.alfresco.share.sideBarWidth",
      
      /**
       * Makes a request to get the users sidebar width preference.
       * 
       * @instance
       */
      postMixInProperties: function alfresco_layout_AlfSideBarContainer__postMixInProperties() {
         this.alfPublish(this.getPreferenceTopic, {
            preference: this.sidebarWidthPreferenceId,
            callback: this.setSideBarWidth,
            callbackScope: this
         });
      },
      
      /**
       * Sets the initial sidebar width from the users saved preferences.
       * 
       * @instance
       * @param {number} value The saved width preference
       */
      setSideBarWidth: function alfresco_layout_AlfSideBarContainer__setSideBarWidth(value) {
         if (value != null)
         {
            this.initialSidebarWidth = value;
         }
      },
      
      /**
       * Adds widgets to the sidebar and main container node and sets up the event handlers for
       * resize events.
       * 
       * @instance
       */
      postCreate: function alfresco_layout_AlfSideBarContainer__postCreate() {
         if (this.widgets)
         {
            array.forEach(this.widgets, lang.hitch(this, "addWidget"));
         }
         
         // Set up the resizer that allows the sidebar to be dynamically made larger or smaller...
         var size = parseInt(domStyle.get(this.domNode, "width"), 10);
         var max = (size - this.minSidebarWidth);
         this.resizer = new YAHOO.util.Resize(this.sidebarNode, {
             handles: ['r'],
             minWidth: this.minSidebarWidth,
             maxWidth: max
         });
         
         // Get a reference to the resize handle node created by the YUI Resize widget...
         // This reference is required for adding/removing classes to change the pop-in/pop-out
         // image on the drag handle...
         var resizeHandles = dojo.query(".yui-resize-handle-inner-r", this.domNode);
         if (resizeHandles != null & resizeHandles.length == 1)
         {
            this.resizeHandlerNode = resizeHandles[0];
            on(this.resizeHandlerNode, "click", lang.hitch(this, "onResizeHandlerClick"));
         }
         else
         {
            this.alfLog("warn", "An expected number of resize handles were found", resizeHandles);
         }
         
         // We need to subscribe after the resize widget has been created...
         this.alfSubscribe("ALF_DOCLIST_SHOW_SIDEBAR", lang.hitch(this, "showEventListener"));

         // Subscribe to all the configured custom resize topics...
         if (this.customResizeTopics != null && this.customResizeTopics.length)
         {
            var _this = this;
            array.forEach(this.customResizeTopics, function(topic, i) {
               _this.alfSubscribe(topic, lang.hitch(_this, "resizeHandler"));
            });
         }

         // Handle resize events...
         this.resizer.on('resize', lang.hitch(this, "resizeHandler"));
         this.resizer.on('endResize', lang.hitch(this, "endResizing"));
         
         // Keep track of the overall browser window changing in size...
         on(window, "resize", lang.hitch(this, "resizeHandler"));
         
         // Perform the initial rendering...
         this.lastSidebarWidth = this.initialSidebarWidth;
         this.resizeHandler({width: this.lastSidebarWidth});
         this.render(this.showSidebar);
      },
      
      /**
       * 
       * @instance
       * @param {object} widget The widget to add
       * @param {integer} index The index of the widget
       */
      addWidget: function alfresco_layout_AlfSideBarContainer__addWidget(widget, index) {
         var domNode = null;
         if (widget.align == "sidebar")
         {
            domNode = this.createWidgetDomNode(widget, this.sidebarNode);
         }
         else
         {
            domNode = this.createWidgetDomNode(widget, this.mainNode);
         }
         this.createWidget(widget, domNode);
      },
      
      /**
       * 
       * @instance
       * @param {object} evt The resize event object
       */
      resizeHandler: function alfresco_layout_AlfSideBarContainer__resizeHandler(evt) {
         var size = parseInt(domStyle.get(this.domNode, "width"), 10);
         var w = this.lastSidebarWidth; // Initialise to last known width of the sidebar (needed for window resize events)
         if (evt && evt.width != null)
         {
            w = evt.width;
            this.lastSidebarWidth = w;
         }
         
         // Get the position of the DOM node and the available view port height...
         var box = domGeom.getMarginBox(this.containerNode);
         var winBox = win.getBox();
         var availableHeight = winBox.h - box.t - this.footerHeight;
         
         // Get the height of the content...
         var sidebarContentHeight = this.calculateHeight(this.sidebarNode, 0, this.sidebarNode.children.length - 1);
         var mainContentHeight = this.calculateHeight(this.mainNode, 0, this.mainNode.children.length);
         this.alfLog("log", "Sidebar height: " + sidebarContentHeight + "px");
         this.alfLog("log", "Main content height: " + mainContentHeight + "px");
         this.alfLog("log", "Viewport height: " + availableHeight + "px");
         
         // Work out the max height for the side bar...
         var c = (sidebarContentHeight > mainContentHeight) ? sidebarContentHeight : mainContentHeight;
         var h = (c > availableHeight) ? c : availableHeight;
         
         this.alfLog("log", "Setting height as (px): ", h);
         domStyle.set(this.sidebarNode, "height", h + "px");
         domStyle.set(this.mainNode, "width", (size - w) + "px");
         
         // Fire a custom event to let contained objects know that the node has been resized.
         this.alfPublishResizeEvent(this.mainNode);
      },
      
      /**
       * Calls [resizeHandler]{@link module:alfresco/layout/AlfSideBarContainer#resizeHandler} and then
       * saves the new width as a user preference.
       * 
       * @instance
       * @param {object} evt The resize event
       */
      endResizing: function alfresco_layout_AlfSideBarContainer__endResizing(evt) {
         this.resizeHandler(evt);
         this.alfPublish(this.setPreferenceTopic, {
            preference: this.sidebarWidthPreferenceId,
            value: evt.width
         });
         this.hiddenSidebarWidth = evt.width;
      },
      
      /**
       * @instance
       * @param {element} node The element to calculate the height of
       */
      calculateHeight: function alfresco_layout_AlfSidebarContainer__calculateHeight(node, start, end) {
         var h = 0;
         for (var i=start; i<end; i++)
         {
            h = h + parseInt(domStyle.get(node.children[i], "height"), 10);
         }
         return h;
      },
      
      /**
       * 
       * @instance
       * @param {object} payload The payload published on the subscribed topic
       */
      showEventListener: function alfresco_layout_AlfSidebarContainer__showEventListener(payload) {
         this.alfLog("log", "Handle show request", payload);
         if (payload && payload.selected != null)
         {
            this.render(payload.selected);
         }
      },
      
      /**
       * Handles a user explicitly clicking on the resize handle node to toggle the sidebar being shown
       * 
       * @instance
       * @param {object} evt The click event
       */
      onResizeHandlerClick: function alfresco_layout_AlfSidebarContainer__onResizeHandlerClick(evt) {
         if (this.resizeHandlerNode)
         {
            // Render the sidebar depending upon whether or not the sidebar is currently being shown or
            // not. We're using the classes applied to the drag handle node to determine whether or not
            // to show or hide the sidebar.
            this.alfPublish("ALF_DOCLIST_SHOW_SIDEBAR", {
               selected: domClass.contains(this.resizeHandlerNode, "pop-out")
            });
         }
      },
      
      /**
       * @instance
       * @type {integer}
       */
      hiddenSidebarWidth: null,
      
      /**
       * Renders the sidebar container (basically controls whether or not the side bar is displayed or not).
       * 
       * @instance
       * @param {boolean} show Indicates whether or not to show the sidebar
       */
      render: function alfresco_layout_AlfSidebarContainer__showSidebar(show) {
         if (show)
         {
            // Hide all the child nodes of the side bar (except for the resize handle)...
            for (var i=0; i<this.sidebarNode.children.length - 1; i++)
            {
               domClass.remove(this.sidebarNode.children[i], "share-hidden");
            }
            
            // Show the sidebar...
            this.resizer.unlock(true); // Unlock the resizer when the sidebar is not shown...
            var width = (this.hiddenSidebarWidth) ? this.hiddenSidebarWidth : this.initialSidebarWidth;
            this.resizer.resize(null, 200, width, 0, 0, true);
            if (this.resizeHandlerNode)
            {
               domClass.remove(this.resizeHandlerNode, "pop-out");
               domClass.add(this.resizeHandlerNode, "pop-in");
            }
         }
         else
         {
            // Hide the sidebar...
            this.resizer.resize(null, 200, 9, 0, 0, true); // Note - we cannot make the width 0 otherwise the container disappears!
            this.resizer.lock(true); // Lock the resizer when the sidebar is not shown...
            if (this.resizeHandlerNode)
            {
               domClass.add(this.resizeHandlerNode, "pop-out");
               domClass.remove(this.resizeHandlerNode, "pop-in");
            }
            
            // Hide all the child nodes of the side bar (except for the resize handle)...
            for (var i=0; i<this.sidebarNode.children.length - 1; i++)
            {
               domClass.add(this.sidebarNode.children[i], "share-hidden");
            }
         }
      }
   });
});