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
 * 
 * 
 * @module alfresco/documentlibrary/views/layouts/Carousel
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin
 * @mixes module:alfresco/core/Core
 * @mixes module:alfresco/core/CoreWidgetProcessing
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dijit/_OnDijitClickMixin",
        "dojo/text!./templates/Carousel.html",
        "alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin",
        "alfresco/core/Core",
        "alfresco/core/CoreWidgetProcessing",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojo/dom-class",
        "dojo/dom-construct",
        "dojo/dom-style",
        "dojo/dom-geometry",
        "dojo/window"], 
        function(declare, _WidgetBase, _TemplatedMixin, _OnDijitClickMixin, template, _MultiItemRendererMixin, AlfCore, CoreWidgetProcessing, 
                 lang, array, domClass, domConstruct, domStyle, domGeom, win) {

   return declare([_WidgetBase, _TemplatedMixin, _OnDijitClickMixin, _MultiItemRendererMixin, AlfCore, CoreWidgetProcessing], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{cssFile:"./css/Carousel.css"}]
       */
      cssRequirements: [{cssFile:"./css/Carousel.css"}],
      
      /**
       * The HTML template to use for the widget.
       * 
       * @instance
       * @type {String}
       */
      templateString: template,
      
      /**
       * Sets up image source files, etc.
       * 
       * @instance postCreate
       */
      postMixInProperties: function alfresco_documentlibrary_views_layouts_Carousel__postMixInProperties() {
         this.contentNavNextArrowImgSrc = require.toUrl("alfresco/documentlibrary/views/layouts") + "/css/images/filmstrip-content-nav-next.png";
         this.contentNavPrevArrowImgSrc = require.toUrl("alfresco/documentlibrary/views/layouts") + "/css/images/filmstrip-content-nav-prev.png";
      },

      /**
       * Calls [processWidgets]{@link module:alfresco/core/Core#processWidgets}
       * 
       * @instance postCreate
       */
      postCreate: function alfresco_documentlibrary_views_layouts_Carousel__postCreate() {
         if (this.currentItem)
         {
            if (this.widgets)
            {
               this.processWidgets(this.widgets, this.containerNode);
            }
         }

         // Subscibe to the page widgets ready topic to ensure that sizing occurs...
         this.alfSubscribe("ALF_WIDGETS_READY", lang.hitch(this, this.resize), true);
      },

      /**
       * Sets the width of each item (in pixels)
       *
       * @instance
       * @type {number}
       * default 100
       */
      itemWidth: 100,

      /**
       * Sets the width to allow for the next and previous buttons
       *
       * @instance
       * @type {number}
       * @default 40
       */
      navigationMargin: 40,

      /**
       * This function is called once all widgets have been added onto the page. At this point it can be
       * assumed that the widget has been placed into the DOM model and has some dimensions to work with
       *
       * @instance
       */
      resize: function alfresco_documentlibrary_views_layouts_Carousel__resize() {
         this.calculateSizes();
         domStyle.set(this.itemsNode, "width", this.itemsNodeWidth + "px");
         domStyle.set(this.itemsNode, "height", this.height);

         // Set the range of displayed items...
         this.lastDisplayedIndex = this.firstDisplayedIndex + (this.numberOfItemsShown - 1);
         this.renderDisplayedItems();
      },

      /**
       * This can be set to a value (in pixels) to fix the height. If this is left as null then
       * a suitable height will attempt to be calculated
       *
       * @instance
       * @type {string}
       * @default null
       */
      height: null,

      /**
       * Gets the available dimensions of the DOM node in preparation for resizing the widget components.
       * This also works out how many items should be shown within the current viewing frame.
       *
       * @instance
       */
      calculateSizes: function alfresco_documentlibrary_views_layouts_Carousel__calculateSizes() {
         // Get the available width of the items node...
         var computedStyle = domStyle.getComputedStyle(this.domNode);
         var output = domGeom.getMarginBox(this.domNode, computedStyle);

         // The width to use is the node width minus the space reserved for the navigation controls...
         var overallWidth = output.w - (2 * this.navigationMargin);

         // For now assume that the width of each item will be 100px....
         // Divide the itemsNode width by 100 to get the number of items
         this.numberOfItemsShown = Math.floor(overallWidth/this.itemWidth);
         this.itemsNodeWidth = this.numberOfItemsShown * this.itemWidth;

         if (this.fixedHeight != null)
         {
            // Use the configured height...
            this.height = this.fixedHeight;
         }
         else
         {
            // Calculate a suitable height...
            this.height = Math.floor((2 / 3) * this.itemsNodeWidth);
            var viewPort = win.getBox();
            var maxItemHeight = viewPort.h;
            if (this.height > maxItemHeight)
            {
               this.height = maxItemHeight;
            }
            this.height += "px";
         }
      },
      
      /**
       * Overrides the superclass implementation to add an additional li element to get the column effect.
       * 
       * @instance
       * @param {Object} widget The widget definition to create the DOM node for
       * @param {element} rootNode The DOM node to create the new DOM node as a child of
       * @param {String} rootClassName A string containing one or more space separated CSS classes to set on the DOM node
       * @returns {element} A new DOM node for a processed widget to attach to
       */
      createWidgetDomNode: function alfresco_documentlibrary_views_layouts_Carousel__createWidgetDomNode(widget, rootNode, rootClassName) {
         var nodeToAdd = nodeToAdd = domConstruct.create("li", {}, rootNode);
         return domConstruct.create("div", {}, nodeToAdd);
      },

      /**
       * This keeps track of the current left position (e.g. the setting that controls what items you can see 
       * within the clipped frame). This value is updated by the 
       * [onPrevClick]{@link module:alfresco/documentlibrary/views/layouts/Carousel#onPrevClick} and
       * [onNextClick]{@link module:alfresco/documentlibrary/views/layouts/Carousel#onNextClick} functions.
       * 
       * @instance
       * @type {number}
       * @default 0
       */
      currentLeftPosition: 0,

      /**
       * This keeps track of the first displayed item in the currently visible frame.
       *
       * @instance
       * @type {number}
       * @default 0
       */
      firstDisplayedIndex: 0,

      /**
       * This keeps track of the lasts displayed item in the currently visible frame
       *
       * @instance
       * @type {number}
       * @default null
       */
      lastDisplayedIndex: null,

      /**
       * Handles the user clicking on the previous items navigation control.
       *
       * @instance
       * @param {object} evt The click event
       */
      onPrevClick: function alfresco_documentlibrary_views_layouts_Carousel__onPrevClick(evt) {
         this.alfLog("log", "Previous carousel items request", this);
         if (this.currentLeftPosition > 0)
         {
            this.currentLeftPosition -= this.itemsNodeWidth;

            // Update the displayed range...
            this.firstDisplayedIndex -= this.numberOfItemsShown;
            this.lastDisplayedIndex -= this.numberOfItemsShown;
            this.renderDisplayedItems();
         }

         if (this.currentLeftPosition < 0)
         {
            this.currentLeftPosition = 0;
         }
         
         var left = "-" + this.currentLeftPosition + "px";
         domStyle.set(this.containerNode, "left", left);
      },

      /**
       * Handles the user clicking on the previous items navigation control.
       *
       * @instance
       * @param {object} evt The click event
       */
      onNextClick: function alfresco_documentlibrary_views_layouts_Carousel__onNextClick(evt) {
         this.alfLog("log", "Next carousel items request", this);
         this.currentLeftPosition += this.itemsNodeWidth;

         // Update the displayed range...
         this.firstDisplayedIndex += this.numberOfItemsShown;
         this.lastDisplayedIndex += this.numberOfItemsShown;

         this.renderDisplayedItems();

         var left = "-" + this.currentLeftPosition + "px";
         domStyle.set(this.containerNode, "left", left);
      },

      /**
       * Iterates over the [processed widgets]{@link module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin#_renderedItemWidgets}
       * between the [first]{@link module:alfresco/documentlibrary/views/layouts/Carousel#firstDisplayedIndex} and
       * [last]{@link module:alfresco/documentlibrary/views/layouts/Carousel#lastDisplayedIndex} indices calling
       * the render function on each to ensure they display themselves correctly
       *
       * @instance
       */
      renderDisplayedItems: function alfresco_documentlibrary_views_layouts_Carousel__renderDisplayedItems() {
         for (var i=this.firstDisplayedIndex; i<=this.lastDisplayedIndex; i++)
         {
            if (this._renderedItemWidgets)
            {
               var widgets = this._renderedItemWidgets[i];
               array.forEach(widgets, lang.hitch(this, this.renderDisplayedItem));
               
            }
         }
      },

      /**
       * Attempts to render a widget that is currently displayed in the viewing frame.
       *
       * @instance
       * @param {object} widget The widget to render
       * @param {number} index The index of the widget within the array
       */
      renderDisplayedItem: function alfresco_documentlibrary_views_layouts_Carousel__renderDisplayedItem(widget, index) {
         if (widget && typeof widget.render === "function")
         {
            widget.render();
         }
      }
   });
});