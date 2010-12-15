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
 * Share Common JavaScript module
 *
 * Contains:
 *    Alfresco.Share
 *    Alfresco.Location
 *    Alfresco.widget.Resizer
 *    Alfresco.widget.DashletResizer
 *    Alfresco.component.ShareFormManager
 */

/**
 * Share common helper classes and static methods
 *
 * @namespace Alfresco.Share
 */
Alfresco.Share = Alfresco.Share || {};

/**
 * Post an Activity to the Activity Service
 *
 * @method Alfresco.Share.postActivity
 * @param siteId {string} Site
 * @param activityType {string} e.g. org.alfresco.documentlibrary.file-added
 * @param title {string} title string for activity entry
 * @param page {string} page to link to from activity (includes ant page request parameters, i.e. queryString)
 * @param data {object} data attached to activity, e.g.
 * <pre>
 *    nodeRef {string} Must have either nodeRef or parentNodeRef
 *    parentNodeRef {string} Must have either parentNodeRef or nodeRef
 *    appTool {string} Share application used for filtering, e.g. "documentlibrary"|"blog"|"links"
 * </pre>
 */
Alfresco.Share.postActivity = function(siteId, activityType, title, page, data)
{
   // Mandatory parameter check
   if (!YAHOO.lang.isString(siteId) || siteId.length === 0 ||
      !YAHOO.lang.isString(activityType) || activityType.length === 0 ||
      !YAHOO.lang.isString(title) || title.length === 0 ||
      !YAHOO.lang.isObject(data) === null ||
      !(YAHOO.lang.isString(data.nodeRef) || YAHOO.lang.isString(data.parentNodeRef)))
   {
      return;
   }
   
   // This is a "fire-and-forget" webscript; we're not concerned with success/failure status
   var config =
   {
      method: "POST",
      url: Alfresco.constants.PROXY_URI + "slingshot/activity/create",
      dataObj: YAHOO.lang.merge(
      {
         site: siteId,
         type: activityType,
         title: title,
         page: page
      }, data)
   };
   
   Alfresco.logger.debug("Alfresco.Share.postActivity: ", config.dataObj);
   
   try
   {
      Alfresco.util.Ajax.jsonRequest(config);
   }
   catch (e)
   {
   }
};

/**
 * Asset location helper class.
 *
 * @namespace Alfresco
 * @class Alfresco.Location
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths;
   
   /**
    * Location constructor.
    *
    * @param {String} el The HTML id of the parent element
    * @return {Alfresco.Location} The new Location instance
    * @constructor
    */
   Alfresco.Location = function Location_constructor(el)
   {
      if (YAHOO.lang.isString(el))
      {
         el = Dom.get(el);
      }
      else if (!el.getAttribute("id"))
      {
         Alfresco.uti.generateDomId(el);
      }

      Alfresco.Location.superclass.constructor.call(this, "Alfresco.Location", el.getAttribute("id"), ["json"]);

      // Save references to dom object
      this.widgets.spanEl = el;

      return this;
   };

   YAHOO.extend(Alfresco.Location, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Repository's rootNode
          *
          * @property rootNode
          * @type Alfresco.util.NodeRef
          */
         rootNode: null,

         /**
          * Current siteId (if any).
          *
          * @property siteId
          * @type string
          */
         siteId: ""
      },

      /**
       * The locations object representing the current location
       *
       * @property _locations
       * @type object
       */
      _locations: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function Location_onReady()
      {
      },

      /**
       * Set nodeRef, will lookup the cntext of the nodeRef and display the result depending
       * on the scope of the options (site and rootNode).
       *
       * @method displayByNodeRef
       * @param nodeRef {Alfresco.util.NodeRef|string}
       */
      displayByNodeRef: function Location_displayByNodeRef(nodeRef)
      {
         // Find the path for the nodeRef
         if (YAHOO.lang.isString(nodeRef))
         {
            nodeRef = Alfresco.util.NodeRef(nodeRef);
         }
         if (nodeRef)
         {
            var url = Alfresco.constants.PROXY_URI + "slingshot/doclib/node/" + nodeRef.uri + "/location";
            if (this.options.siteId === "" && this.options.rootNode)
            {
               // Repository mode
               url += "?libraryRoot=" + encodeURIComponent(this.options.rootNode.toString());
            }
            Alfresco.util.Ajax.jsonGet(
            {
               url: url,
               successCallback:
               {
                  fn: function(response)
                  {
                     if (response.json !== undefined)
                     {
                        var locations = response.json;
                        this._locations = locations;
                        if (locations.site)
                        {
                           this.displayByPath($combine(locations.site.path, locations.site.file), locations.site.site, locations.site.siteTitle);
                        }
                        else
                        {
                           this.displayByPath($combine(locations.repo.path, locations.repo.file));
                        }

                        YAHOO.Bubbling.fire("itemLocationLoaded",
                        {
                           eventGroup: this,
                           locations: locations
                        });

                     }
                  },
                  scope: this
               },
               failureCallback:
               {
                  fn: function(response)
                  {
                     if (this.widgets.spanEl)
                     {
                        this.widgets.spanEl.innerHTML = '<span class="location error">' + this.msg("message.failure") + '</span>';
                     }
                  },
                  scope: this
               }
            });
         }
         else
         {
            this.widgets.spanEl.innerHTML = '<span class="location-none">' + this.msg("location.label.none") + '</span>';
         }
      },

      /**
       * Renders the location path as HTML
       *
       * @method displayByPath
       * @param fullPath {string}
       * @param siteId {string}
       * @param siteTitle {string}
       */
      displayByPath: function Location_displayByPath(fullPath, siteId, siteTitle)
      {
         this._locations = null;
         if (this.widgets.spanEl)
         {
            this.widgets.spanEl.innerHTML = this.generateHTML(fullPath, siteId, siteTitle);
         }
      },
      
      /**
       * Create html that represent a path and site
       *
       * @method generateHTML
       * @param fullPath
       * @param siteId
       * @param siteTitle
       * @return {string} html respresenting path and site as span elements
       */
      generateHTML: function Location_generateHTML(fullPath, siteId, siteTitle)
      {
         var i = fullPath.lastIndexOf("/"),
            path = i >= 0 ? fullPath.substring(0, i + 1) : "",
            name = i >= 0 ? fullPath.substring(i + 1) : fullPath;

         if (siteId)
         {
            if (Alfresco.util.arrayContains(["/", ""], name + path))
            {
               fullPath = this.msg("location.path.documents");
               name = this.msg("location.path.documents");
            }
            else
            {
               fullPath = this.msg("location.path.documents") + fullPath;
               name = ".../" + name;
            }
         }
         else
         {
            if (Alfresco.util.arrayContains(["/", ""], name + path))
            {
               fullPath = this.msg("location.path.repository");
               name = this.msg("location.path.repository");
            }
            else
            {
               fullPath = this.msg("location.path.repository") + fullPath;
               name = ".../" + name;
            }
         }
         var pathHtml = '<span class="location-path" title="' + this.msg("location.tooltip.path", fullPath) + '">' + $html(name) + '</span>';
         if (siteId)
         {
            if (siteId && siteId != this.options.siteId)
            {
               var siteHtml = '<span class="location-site" title="' + this.msg("location.tooltip.site", siteTitle ? siteTitle : siteId) + '">' + $html(siteTitle ? siteTitle : siteId) + '</span>';
               return this.msg("location.label.site", pathHtml, siteHtml);
            }
            else
            {
               return this.msg("location.label.local", pathHtml);
            }
         }
         else
         {
            return this.msg("location.label.repository", pathHtml);
         }
      }
   });
})();

/**
 * Alfresco Resizer.
 * 
 * @namespace Alfresco.widget
 * @class Alfresco.widget.Resizer
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * Resizer constructor.
    * 
    * @return {Alfresco.widget.Resizer} The new Alfresco.widget.Resizer instance
    * @constructor
    */
   Alfresco.widget.Resizer = function Resizer_constructor(p_name)
   {
      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["resize"], this.onComponentsLoaded, this);

      this.name = p_name;

      // Initialise prototype properties
      this.widgets = {};

      return this;
   };

   Alfresco.widget.Resizer.prototype =
   {
      /**
       * Minimum Filter Panel height.
       * 
       * @property MIN_FILTER_PANEL_HEIGHT
       * @type int
       */
      MIN_FILTER_PANEL_HEIGHT: 200,

      /**
       * Minimum Filter Panel width.
       * 
       * @property MIN_FILTER_PANEL_WIDTH
       * @type int
       */
      MIN_FILTER_PANEL_WIDTH: 140,

      /**
       * Default Filter Panel width.
       * 
       * @property DEFAULT_FILTER_PANEL_WIDTH
       * @type int
       */
      DEFAULT_FILTER_PANEL_WIDTH: 160,

      /**
       * Maximum Filter Panel width.
       * 
       * @property MAX_FILTER_PANEL_WIDTH
       * @type int
       */
      MAX_FILTER_PANEL_WIDTH: 500,

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: null,

      /**
       * DOM ID of left-hand container DIV
       *
       * @property divLeft
       * @type string
       * @default "alf-filters"
       */
      divLeft: "alf-filters",

      /**
       * DOM ID of right-hand container DIV
       *
       * @property divRight
       * @type string
       * @default "alf-content"
       */
      divRight: "alf-content",

      /**
       * Used to monitor document length
       *
       * @property documentHeight
       * @type int
       */
      documentHeight: -1,

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function Resizer_onComponentsLoaded()
      {
         YAHOO.util.Event.onDOMReady(this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function Resizer_onReady()
      {
         // Horizontal Resizer
         this.widgets.horizResize = new YAHOO.util.Resize(this.divLeft,
         {
            handles: ["r"],
            minWidth: this.MIN_FILTER_PANEL_WIDTH,
            maxWidth: this.MAX_FILTER_PANEL_WIDTH
         });

         // Before and End resize event handlers
         this.widgets.horizResize.on("beforeResize", function(eventTarget)
         {
            this.onResize(eventTarget.width);
         }, this, true);
         this.widgets.horizResize.on("endResize", function(eventTarget)
         {
            this.onResize(eventTarget.width);
         }, this, true);

         // Recalculate the vertical size on a browser window resize event
         YAHOO.util.Event.on(window, "resize", function(e)
         {
            this.onResize();
         }, this, true);

         // Monitor the document height for ajax updates
         this.documentHeight = Dom.getXY("alf-ft")[1];

         YAHOO.lang.later(1000, this, function()
         {
            var h = Dom.getXY("alf-ft")[1];
            if (Math.abs(this.documentHeight - h) > 4)
            {
               this.documentHeight = h;
               this.onResize();
            }
         }, null, true);
               
         // Initial size
         if (YAHOO.env.ua.ie > 0)
         {
            this.widgets.horizResize.resize(null, this.widgets.horizResize.get("element").offsetHeight, this.DEFAULT_FILTER_PANEL_WIDTH, 0, 0, true);
         }
         else
         {
            this.widgets.horizResize.resize(null, this.widgets.horizResize.get("height"), this.DEFAULT_FILTER_PANEL_WIDTH, 0, 0, true);
         }

         this.onResize(this.DEFAULT_FILTER_PANEL_WIDTH);
      },

      /**
       * Fired by via resize event listener.
       *
       * @method onResize
       */
      onResize: function Resizer_onResize(width)
      {
         var cn = Dom.get(this.divLeft).childNodes,
            handle = cn[cn.length - 1];

         Dom.setStyle(this.divLeft, "height", "auto");
         Dom.setStyle(handle, "height", "");

         var h = Dom.getXY("alf-ft")[1] - Dom.getXY("alf-hd")[1] - Dom.get("alf-hd").offsetHeight;

         if (YAHOO.env.ua.ie === 6)
         {
            var hd = Dom.get("alf-hd"), tmpHeight = 0;
            for (var i = 0, il = hd.childNodes.length; i < il; i++)
            {
               tmpHeight += hd.childNodes[i].offsetHeight;
            }
            h = Dom.get("alf-ft").parentNode.offsetTop - tmpHeight; 
         }
         if (h < this.MIN_FILTER_PANEL_HEIGHT)
         {
            h = this.MIN_FILTER_PANEL_HEIGHT;
         }

         Dom.setStyle(handle, "height", h + "px");

         if (width !== undefined)
         {
            // 8px breathing space for resize gripper
            Dom.setStyle(this.divRight, "margin-left", 8 + width + "px");
         }
      }
   };
})();

/**
 * Dashlet Resizer.
 * 
 * @namespace Alfresco.widget
 * @class Alfresco.widget.DashletResizer
 */
(function()
{
   /**
   * YUI Library aliases
   */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector;

   /**
    * Dashlet Resizer constructor.
    * 
    * @return {Alfresco.widget.DashletResizer} The new Alfresco.widget.DashletResizer instance
    * @constructor
    */
   Alfresco.widget.DashletResizer = function DashletResizer_constructor(htmlId, dashletId)
   {
      this.name = "Alfresco.widget.DashletResizer";
      this.id = htmlId;
      this.dashletId = dashletId;

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["resize", "selector"], this.onComponentsLoaded, this);
   
      // Initialise prototype properties
      this.widgets = {};
         
      return this;
   };

   Alfresco.widget.DashletResizer.prototype =
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * The initial dashlet height.
          * 
          * @property dashletHeight
          * @type int
          */
         dashletHeight: -1,

         /**
          * Minimum Dashlet height.
          * 
          * @property minDashletHeight
          * @type int
          * @default 100
          */
         minDashletHeight: 80,

         /**
          * Maximum Dashlet height.
          * 
          * @property maxDashletHeight
          * @type int
          * @default 1200
          */
         maxDashletHeight: 1200
      },
   
      /**
       * The dashletId.
       * 
       * @property dashletId
       * @type string
       */
      dashletId: "",

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: null,
   
      /**
       * DOM node of dashlet
       * Resizer will look for first child DIV of dashlet with class="dashlet" and attach to this
       *
       * @property dashlet
       * @type object
       * @default null
       */
      dashlet: null,

      /**
       * DOM node of dashlet body
       * Resizer will look for first child DIV of dashlet with class="body" and resize this element
       *
       * @property dashletBody
       * @type object
       * @default null
       */
      dashletBody: null,

      /**
       * Difference in height between dashlet offsetHeight and dashletBody CSS height
       *
       * @property heightDelta
       * @type int
       * @default 0
       */
      heightDelta: 0,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.widget.DashletResizer} returns 'this' for method chaining
       */
      setOptions: function DashletResizer_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
   
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function DashletResizer_onComponentsLoaded()
      {
         Event.onDOMReady(this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DashletResizer_onReady()
      {
         // Have permission to resize?
         if (!Alfresco.constants.DASHLET_RESIZE)
         {
            return;
         }
      
         // Find dashlet div
         this.dashlet = Selector.query("div.dashlet", Dom.get(this.id), true);
         if (!this.dashlet)
         {
            return;
         }
         Dom.addClass(this.dashlet, "resizable");

         // Find dashlet body div?
         this.dashletBody = Selector.query("div.body", this.dashlet, true);
         if (!this.dashletBody)
         {
            return;
         }

         // Difference in height between dashlet and dashletBody for resize events
         var origHeight = Dom.getStyle(this.dashlet, "height");
         if (origHeight == "auto")
         {
            origHeight = this.dashlet.offsetHeight - parseInt(Dom.getStyle(this.dashlet, "padding-bottom"), 10);
         }
         else
         {
            origHeight = parseInt(origHeight, 10);
         }
         this.heightDelta = origHeight - parseInt(Dom.getStyle(this.dashletBody, "height"), 10);

         // Create and attach Vertical Resizer
         this.widgets.resizer = new YAHOO.util.Resize(this.dashlet,
         {
            handles: ["b"],
            minHeight: this.options.minDashletHeight,
            maxHeight: this.options.maxDashletHeight
         });
      
         // During resize event handler
         this.widgets.resizer.on("resize", function()
         {
            this.onResize();
         }, this, true);
      
         // End resize event handler
         this.widgets.resizer.on("endResize", function(eventTarget)
         {
            this.onEndResize(eventTarget.height);
         }, this, true);

         // Clear the fixed-pixel width the dashlet has been given
         Dom.setStyle(this.dashlet, "width", "");
      },

      /**
       * Fired by resize event listener.
       *
       * @method onResize
       */
      onResize: function DashletResizer_onResize()
      {
         var height = parseInt(Dom.getStyle(this.dashlet, "height"), 10) - this.heightDelta;
         Dom.setStyle(this.dashletBody, "height", height + "px");
         Dom.setStyle(this.dashletBody.getElementsByTagName("iframe"), "height", height + "px");
      },

      /**
       * Fired by end resize event listener.
       *
       * @method onResize
       * @param h Height - not used
       */
      onEndResize: function DashletResizer_onEndResize(h)
      {
         // Clear the fixed-pixel width the dashlet has been given
         Dom.setStyle(this.dashlet, "width", "");
      
         Alfresco.util.Ajax.jsonRequest(
         {
            method: "POST",
            url: Alfresco.constants.URL_SERVICECONTEXT + "modules/dashlet/config/" + this.dashletId,
            dataObj:
            {
               height: parseInt(Dom.getStyle(this.dashlet, "height"), 10) - this.heightDelta
            },
            successCallback: function(){},
            successMessage: null,
            failureCallback: function(){},
            failureMessage: null
         });
      }
   };
})();

/**
 * ShareFormManager component.
 *
 * Determines those pages defined as "AJAX state pages" and thus able to restore previous
 * state from URL arguments.
 *
 * @namespace Alfresco.component
 * @class Alfresco.component.ShareFormManager
 */
(function()
{
   /**
    * ShareFormManager constructor.
    *
    * @param {String} el The HTML id of the parent element
    * @return {Alfresco.RulesHeader} The new RulesHeader instance
    * @constructor
    */
   Alfresco.component.ShareFormManager = function Location_constructor(el)
   {
      Alfresco.component.ShareFormManager.superclass.constructor.call(this, el);

      // Re-register with our own name
      this.name = "Alfresco.component.ShareFormManager";
      Alfresco.util.ComponentManager.reregister(this);

      // Instance variables
      this.options = YAHOO.lang.merge(this.options, Alfresco.component.ShareFormManager.superclass.options);

      return this;
   };

   YAHOO.extend(Alfresco.component.ShareFormManager, Alfresco.component.FormManager,
   {
      /**
       * Share pages that use ajax state ("#").
       *
       * @override
       * @method pageUsesAjaxState
       * @param url
       * @return {boolean} True if the url is recognised as a page that uses ajax states (adds values after "#" on the url)
       */
      pageUsesAjaxState: function FormManager_pageUsesAjaxState(url)
      {
         return (url.match(/documentlibrary([?]|$)/) ||
               url.match(/repository([?]|$)/) ||
               url.match(/my-workflows([?]|$)/) ||
               url.match(/my-tasks([?]|$)/));
      },

      /**
       * Override this method to make the user visit this url if no preferred url was given for a form and
       * there was no page visited before the user came to the form page.
       *
       * @method getSiteDefaultUrl
       * @return {string} The url to make the user visit if no other alternatives have been found
       */
      getSiteDefaultUrl: function FormManager_getSiteDefaultUrl()
      {
         return Alfresco.util.uriTemplate("userdashboardpage", 
         {
            userid: encodeURIComponent(Alfresco.constants.USERNAME)
         });
      }
   });
})();
