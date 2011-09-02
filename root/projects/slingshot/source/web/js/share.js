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
 *    Alfresco.widget.DashletTitleBarActions
 *    Alfresco.widget.DashletResizer
 *    Alfresco.component.ShareFormManager
 *    Alfresco.component.SimpleDocList
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
         Alfresco.util.generateDomId(el);
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
 * Like widget helper class.
 *
 * @namespace Alfresco
 * @class Alfresco.Like
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
    * Like constructor.
    *
    * @param {String} el The HTML id of the parent element
    * @return {Alfresco.Like} The new Like instance
    * @constructor
    */
   Alfresco.Like = function Like_constructor(el)
   {
      if (YAHOO.lang.isString(el))
      {
         el = Dom.get(el);
      }
      else if (!el.getAttribute("id"))
      {
         Alfresco.util.generateDomId(el);
      }
      YAHOO.util.Dom.addClass(el, "item-social");

      Alfresco.Like.superclass.constructor.call(this, "Alfresco.Like", el.getAttribute("id"), ["json"]);

      // Save references to dom object
      this.widgets.spanEl = el;
      this.services.likes = new Alfresco.service.Ratings(Alfresco.service.Ratings.LIKES);

      return this;
   };

   YAHOO.extend(Alfresco.Like, Alfresco.component.Base,
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
          * Reference to the current document
          *
          * @property nodeRef
          * @type String
          */
         nodeRef: null,

         /**
          * Current siteId, if any.
          *
          * @property siteId
          * @type String
          */
         siteId: null,

         /**
          * The name of the object to like, will be used in activity.
          *
          * @property type
          * @type String
          */
         displayName: null,
         
         /**
          * The type of object to like.
          * Supported types are: "document", "folder".
          *
          * @property type
          * @type String
          * @default "document"
          */
         type: "document",

         /**
          * The default activity data that will be posted based on the type option
          *
          * @property activity
          * @type {Object}
          */
         activity:
         {
            "folder":
            {
               type: "org.alfresco.documentlibrary.folder-liked",
               page: "folder-details?nodeRef={nodeRef}"
            },
            "document":
            {
               type: "org.alfresco.documentlibrary.file-liked",
               page: "document-details?nodeRef={nodeRef}"
            }
         }            
      },

      /**
       * If the current user likes the nodeRef
       *
       * @type {Boolean}
       * @property isLiked
       */
      isLiked: false,

      /**
       * The total amount of users that like nodeRef
       *
       * @type {Number}
       * @property isLiked
       */
      totalLikes: 0,

      /**
       * NOTE! Implement when needed.
       *
       * Set id, load Like data and render.
       *
       * @method loadAndDisplay
       */
      loadAndDisplay: function Like_loadAndDisplay()
      {
         throw new Error("Not implemented yet, load data manually and use display(isLiked, totalLikes) instead.");
      },

      /**
       * 
       *
       * @method display
       * @param isLiked {string}
       * @param totalLikes {string}
       */
      display: function Like_display(isLiked, totalLikes)
      {
         this.isLiked = isLiked || false;
         this.totalLikes = totalLikes || 0;
         this.render();
      },

      /**
       * Create html that represent a like button
       *
       * @method render
       */
      render: function Like_render()
      {
         var html = "";
         if (this.isLiked)
         {
            html = '<a name=".like" rel="false" class="like-action theme-color-1 enabled ' + this.id + '" title="' + this.msg("like." + this.options.type + ".remove.tip") + '" tabindex="0"></a>';
         }
         else
         {
            html = '<a name=".like" rel="true" class="like-action theme-color-1 ' + this.id + '" title="' + this.msg("like." + this.options.type + ".add.tip") + '" tabindex="0">' + this.msg("like." + this.options.type + ".add.label") + '</a>';
         }
         html += '<span class="likes-count">' + $html(this.totalLikes) + '</span>';
         this.widgets.spanEl.innerHTML = html;
      },

      like: function(isLiked)
      {
         var orgValues =
         {
            isLiked: this.isLiked,
            totalLikes: this.totalLikes
         };

         this.isLiked = isLiked == 'true';
         this.totalLikes = this.totalLikes + (this.isLiked ? 1 : -1);

         var responseConfig =
         {
            successCallback:
            {
               fn: function Like_like_success(event)
               {
                  var data = event.json.data;
                  if (data)
                  {
                     this.totalLikes = data.ratingsCount;

                     // Post to the Activities Service on the "Like" action
                     if (this.isLiked)
                     {
                        var activity = this.options.activity[this.options.type];
                        if (activity)
                        {
                           var page = YAHOO.lang.substitute(activity.page, { nodeRef: this.options.nodeRef });
                           Alfresco.Share.postActivity(this.options.siteId, activity.type, this.options.displayName, page,
                           {
                              nodeRef: this.options.nodeRef,
                              fileName: this.options.displayName
                           })
                        }
                     }
                  }
               },
               scope: this
            },
            failureCallback:
            {
               fn: function Like_like_failure(event)
               {
                  this.isLiked = orgValues.isLiked;
                  this.totalLikes = orgValues.totalLikes;
                  this.render();
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: this.msg("like.message.failure")
                  });
               },
               scope: this
            }
         };

         if (this.isLiked)
         {
            this.services.likes.set(new Alfresco.util.NodeRef(this.options.nodeRef), 1, responseConfig);
         }
         else
         {
            this.services.likes.remove(new Alfresco.util.NodeRef(this.options.nodeRef), responseConfig);
         }

         // Render new (unsaved) values
         this.render();
      }

   });
})();

/**
 * Favourite widget helper class.
 *
 * @namespace Alfresco
 * @class Alfresco.Favourite
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
    * Favourite constructor.
    *
    * @param {String} el The HTML id of the parent element
    * @return {Alfresco.Favourite} The new Favourite instance
    * @constructor
    */
   Alfresco.Favourite = function Favourite_constructor(el)
   {
      if (YAHOO.lang.isString(el))
      {
         el = Dom.get(el);
      }
      else if (!el.getAttribute("id"))
      {
         Alfresco.util.generateDomId(el);
      }
      YAHOO.util.Dom.addClass(el, "item-social");

      Alfresco.Favourite.superclass.constructor.call(this, "Alfresco.Favourite", el.getAttribute("id"), ["json"]);
      
      // Save references to dom object
      this.widgets.spanEl = el;
      this.services.preferences = new Alfresco.service.Preferences();

      return this;
   };

   YAHOO.extend(Alfresco.Favourite, Alfresco.component.Base,
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
          * Reference to the current document
          *
          * @property nodeRef
          * @type String
          */
         nodeRef: null,

         /**
          * The type of object to Favourite.
          * Supported types are: "document", "folder".
          *
          * @property type
          * @type String
          * @default "document"
          */
         type: "document",

         /**
          * The preference that will be favourised based on the type option
          *
          * @property preference
          * @type {Object}
          */
         preference:
         {
            "folder":
            {
               key: Alfresco.service.Preferences.FAVOURITE_FOLDERS
            },
            "document":
            {
               key: Alfresco.service.Preferences.FAVOURITE_DOCUMENTS
            }
         }
      },

      /**
       * If the current user favourites the nodeRef
       *
       * @type {Boolean}
       * @property isFavourite
       */
      isFavourite: false,

      /**
       * NOTE! Implement when needed.
       *
       * Set id, load Like data and render.
       *
       * @method loadAndDisplay
       */
      loadAndDisplay: function Favourite_loadAndDisplay()
      {
         throw new Error("Not implemented yet, load data manually and use display(isFavourite) instead.");
      },

      /**
       * Display the favourite widget as a favourite or not depending on the isFavourite parameter
       *
       * @method display
       * @param isFavourite {string}
       */
      display: function Favourite_display(isFavourite)
      {
         this.isFavourite = isFavourite || false;
         this.render();
      },

      /**
       * Create html that represent a favourite button
       *
       * @method render
       */
      render: function Favourite_render()
      {
         var html = "";
         if (this.isFavourite)
         {
            html = '<a name=".favourite" rel="false" class="favourite-action theme-color-1 favourite-' + this.options.type + ' enabled ' + this.id + '" title="' + this.msg("favourite." + this.options.type + ".remove.tip") + '" tabindex="0"></a>';
         }
         else
         {
            html = '<a name=".favourite" rel="true" class="favourite-action theme-color-1 favourite-' + this.options.type + ' ' + this.id + '" title="' + this.msg("favourite." + this.options.type + ".add.tip") + '" tabindex="0">' + this.msg("favourite." + this.options.type + ".add.label") + '</a>';
         }
         this.widgets.spanEl.innerHTML = html;
      },

      favourite: function(isFavourite)
      {
         var orgValues =
         {
            isFavourite: this.isFavourite
         };

         this.isFavourite = isFavourite == 'true';

         var responseConfig =
         {
            failureCallback:
            {
               fn: function Favourite_favourite_failure(event)
               {
                  this.isFavourite = orgValues.isFavourite;
                  this.render();
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: this.msg("favourite.message.failure")
                  });
               },
               scope: this
            }
         };

         // Save
         var preference = this.options.preference[this.options.type];
         if (preference)
         {
            var action = this.isFavourite ? "add" : "remove";
            this.services.preferences[action].call(this.services.preferences, preference.key, this.options.nodeRef, responseConfig);
         }
         else
         {
            throw new Error("No prefence has been given for type '" + this-options.type + "'.");
         }

         // Render new (unsaved) values
         this.render();
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
       * Object container for initialisation options
       *
       * @property options
       * @type object
       */
      options:
      {
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
          * Optional initial width of the resizer
          * 
          * @property initialWidth
          * @type int
          */
         initialWidth: null
      },
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
         this.widgets.horizResize = new YAHOO.util.Resize(this.options.divLeft,
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
         this.options.documentHeight = Dom.getXY("alf-ft")[1];

         YAHOO.lang.later(1000, this, function()
         {
            var h = Dom.getXY("alf-ft")[1];
            if (Math.abs(this.options.documentHeight - h) > 4)
            {
               this.options.documentHeight = h;
               this.onResize();
            }
         }, null, true);

         // Initial size
         var width = (this.options.initialWidth ? this.options.initialWidth : this.DEFAULT_FILTER_PANEL_WIDTH);
         if (YAHOO.env.ua.ie > 0)
         {
            this.widgets.horizResize.resize(null, this.widgets.horizResize.get("element").offsetHeight, width, 0, 0, true);
         }
         else
         {
            this.widgets.horizResize.resize(null, this.widgets.horizResize.get("height"), width, 0, 0, true);
         }

         this.onResize(width);
      },

      /**
       * Fired by via resize event listener.
       *
       * @method onResize
       */
      onResize: function Resizer_onResize(width)
      {
         var cn = Dom.get(this.options.divLeft).childNodes,
            handle = cn[cn.length - 1];

         Dom.setStyle(this.options.divLeft, "height", "auto");
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
            Dom.setStyle(this.options.divRight, "margin-left", 8 + width + "px");
         }
      },
            
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.widget.Resizer} returns 'this' for method chaining
       */
      setOptions: function Resizer_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      }
   };
})();


/**
 * Dashlet title bar action controller
 *
 * When creating a new title bar action controller it is necessary to call setOptions with the following
 * attributes in a hash:
 * - actions: an array of the actions to display (see below)
 *
 * Actions:
 * Each action can have the following attributes:
 * - cssClass (required)      : this should be a CSS class that defines a 16x16 image to render as the action icon
 * - tooltip (options)        : this should be a message to use for the hover help tooltip
 * - eventOnClick (optional)  : this is the custom event event that will be fired when the action is clicked
 * - linkOnClick (optional)   : this is URL that the browser will redirect to when the action is clicked
 * - targetOnClick (optional) : this is the URL that the browser display in a new window/tab
 * - bubbleOnClick (optional) : this should be an object containing "message" (String) and "messageArgs" (String array) attributes
 *
 * @namespace Alfresco.widget
 * @class Alfresco.widget.DashletTitleBarActions
 */

var DASHLET_TITLE_BAR_ACTIONS_OPACITY = 0,
   OPACITY_FADE_SPEED = 0.2;

(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector;

   /**
    * Dashlet Title Bar Action controller constructor.
    *
    * @return {Alfresco.widget.DashletTitleBarActions} The new Alfresco.widget.DashletTitleBarActions instance
    * @constructor
    */
   Alfresco.widget.DashletTitleBarActions = function DashletTitleBarActions_constructor(htmlId)
   {
      return Alfresco.widget.DashletTitleBarActions.superclass.constructor.call(this, "Alfresco.widget.DashletTitleBarActions", htmlId, ["selector"]);
   };

   YAHOO.extend(Alfresco.widget.DashletTitleBarActions, Alfresco.component.Base,
   {
      /**
       * DOM node of dashlet
       * Looks for first child DIV of dashlet with class="dashlet" and attach to this
       *
       * @property dashlet
       * @type object
       * @default null
       */
      dashlet: null,

      /**
       * DOM node of dashlet title
       * The first child DIV of dashlet with class="title"
       *
       * @property dashletTitle
       * @type object
       * @default null
       */
      dashletTitle: null,

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
       * The that node containing all the actions nodes. The actions are
       * grouped under a single parent so that only one animation effect needs
       * to be applied.
       *
       * @property actionsNode
       * @type object
       * @default null
       */
      actionsNode: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DashletTitleBarActions_onReady()
      {
         this.dashlet = Selector.query("div.dashlet", Dom.get(this.id), true);
         this.dashletTitle = Selector.query("div.title", this.dashlet, true);
         this.dashletBody = Selector.query("div.body", this.dashlet, true);
         if (this.dashlet && this.dashletTitle && this.dashletBody)
         {
            this.actionsNode = document.createElement("div");
            Dom.addClass(this.actionsNode, "titleBarActions");  // This class sets the position of the actions.
            if (YAHOO.env.ua.ie > 0)
            {
               // IE doesn't handle the fading in/out very well so we won't do it. 
            }
            else
            {
               Dom.setStyle(this.actionsNode, "opacity", DASHLET_TITLE_BAR_ACTIONS_OPACITY);
            }
          

            // Add the actions node before the dashlet body...
            this.dashlet.insertBefore(this.actionsNode, this.dashletBody);

            // Reverse the order of the arrays so that the first entry is furthest to the left...
            this.options.actions.reverse();
            // Iterate through the array of actions creating a node for each one...
            for (var i = 0; i < this.options.actions.length; i++)
            {
               var currAction = this.options.actions[i];
               if (currAction.cssClass && (currAction.eventOnClick ||
                                           currAction.linkOnClick ||
                                           currAction.targetOnClick ||
                                           currAction.bubbleOnClick))
               {
                  var currActionNode = document.createElement("div");  // Create the node
                  if (currAction.tooltip)
                  {
                     Dom.setAttribute(currActionNode, "title", currAction.tooltip);
                  }
                  Dom.addClass(currActionNode, "titleBarActionIcon");
                  Dom.addClass(currActionNode, currAction.cssClass);   // Set the class (this should add the icon image
                  this.actionsNode.appendChild(currActionNode);        // Add the node to the parent

                  if (currAction.id)
                  {
                     currActionNode.id = this.id + currAction.id;
                  }

                  var _this = this;
                  if (currAction.eventOnClick)
                  {
                     var customEvent = currAction.eventOnClick; // Copy this value as the currAction handle will be reassigned...

                     // If the action is an event then the value passed should be a custom event that
                     // we will simply fire when the action node is clicked...
                     Event.addListener(currActionNode, "click", function(e)
                     {
                        _this._fadeOut(e, _this);
                        customEvent.fire({});
                     });
                  }
                  else if (currAction.linkOnClick)
                  {
                     var link = currAction.linkOnClick; // Copy this value as the currAction handle will be reassigned...

                     // If the action is a navigation link, then add a listener function that updates
                     // the browsers current location to be the supplied value...
                     Event.addListener(currActionNode, "click", function()
                     {
                        window.location = link;
                     });
                  }
                  else if (currAction.targetOnClick)
                  {
                     // If the action is a target link, then open a new window/tab and set its location
                     // to the supplied value...
                     var target = currAction.targetOnClick; // Copy this value as the currAction handle will be reassigned...

                     Event.addListener(currActionNode, "click", function()
                     {
                        window.open(target);
                     });
                  }
                  else if (currAction.bubbleOnClick)
                  {
                     var balloon = Alfresco.util.createBalloon(this.id,
                     {
                        html: currAction.bubbleOnClick.message,
                        width: "30em"
                     });

                     Event.addListener(currActionNode, "click", balloon.show, balloon, true);
                  }
               }
               else
               {
                  Alfresco.logger.warn("DashletTitleBarActions_onReady: Action is not valid.");
               }
            }

            // Add a listener to animate the actions...
            Event.addListener(this.dashlet, "mouseover", this._fadeIn, this);
            Event.addListener(this.dashlet, "mouseout", this._fadeOut, this);
         }
         else
         {
            // It's not possible to set up the actions without the dashlet, its title and the body
         }
      },

      /**
       * Fade the node actions out
       *
       * @method _fadeOut
       * @param e {event} The current event
       * @param me {scope} the context to run in
       * @protected
       */
      _fadeOut: function DashletTitleBarActions__fadeOut(e, me)
      {
         if (YAHOO.env.ua.ie > 0 && YAHOO.env.ua.ie < 9)
         {
            me.actionsNode.style.display = "none";
         }
         else
         {
            // Only fade out if the mouse has left the dashlet entirely
            if (!Dom.isAncestor(me.dashlet, Event.getRelatedTarget(e)))
            {
               var fade = new YAHOO.util.Anim(me.actionsNode,
               {
                  opacity:
                  {
                     to: DASHLET_TITLE_BAR_ACTIONS_OPACITY
                  }
               }, OPACITY_FADE_SPEED);
               fade.animate();
            }
         }
      },

      /**
       * Fade the actions node in
       *
       * @method _fadeIn
       * @param e {event} The current event
       * @param me {scope} the context to run in
       * @protected
       */
      _fadeIn: function DashletTitleBarActions__fadeIn(e, me)
      {
         if (YAHOO.env.ua.ie > 0 && YAHOO.env.ua.ie < 9)
         {
            me.actionsNode.style.display = "block";
         }
         else
         {
            var fade = new YAHOO.util.Anim(me.actionsNode,
            {
               opacity:
               {
                  to: 1
               }
            }, OPACITY_FADE_SPEED);
            fade.animate();
         }
      }
   });
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
    * @return {Alfresco.component.ShareFormManager} The new ShareFormManager instance
    * @constructor
    */
   Alfresco.component.ShareFormManager = function ShareFormManager_constructor(el)
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

/**
 * Creates img markup representing a users avatar.
 *
 * @param userName {string} Username to display the avatar for
 * @param size {number} Optional: 64|32 are the currently supported avatar sizes. Default is 64px
 */
Alfresco.Share.userAvatar = function(userName, size)
{
   var imgUrl = Alfresco.constants.URL_CONTEXT + "components/images/no-user-photo-64.png";
   if (userName)
   {
      imgUrl = Alfresco.constants.PROXY_URI + "slingshot/profile/avatar/" + encodeURIComponent(userName);
      if (size === 32)
      {
         imgUrl += "/thumbnail/avatar32";
      }
   }
   return '<img src="' + imgUrl + '" alt="' + Alfresco.util.message("label.avatar") + '"/>';
};

/**
 * SimpleDocList component.
 *
 * Generates a simple DataTable-based document list view
 *
 * @namespace Alfresco.component
 * @class Alfresco.component.SimpleDocList
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $links = Alfresco.util.activateLinks,
      $userProfile = Alfresco.util.userProfileLink,
      $siteDashboard = Alfresco.util.siteDashboardLink,
      $relTime = Alfresco.util.relativeTime;

   /**
    * Use the getDomId function to get unique names for global event handling
    */
   var FAVOURITE_EVENTCLASS = Alfresco.util.generateDomId(null, "favourite"),
      LIKE_EVENTCLASS = Alfresco.util.generateDomId(null, "like");

   /**
    * SimpleDocList constructor.
    *
    * @param {String} htmlid The HTML id of the parent element
    * @return {Alfresco.component.SimpleDocList} The new SimpleDocList instance
    * @constructor
    */
   Alfresco.component.SimpleDocList = function SimpleDocList_constructor(htmlId)
   {
      Alfresco.component.SimpleDocList.superclass.constructor.call(this, "Alfresco.component.SimpleDocList", htmlId, ["button", "container", "datasource", "datatable", "animation"]);

      this.previewTooltips = [];
      this.metadataTooltips = [];

      // Preferences service
      this.services.preferences = new Alfresco.service.Preferences();
      this.services.likes = new Alfresco.service.Ratings(Alfresco.service.Ratings.LIKES);
      
      return this;
   };
   
   /**
    * Generate "Favourite" UI
    *
    * @method generateFavourite
    * @param scope {object} DocumentLibrary instance
    * @param record {object} DataTable record
    * @return {string} HTML mark-up for Favourite UI
    */
   Alfresco.component.SimpleDocList.generateFavourite = function SimpleDocList_generateFavourite(scope, record)
   {
      var i18n = "favourite." + (record.getData("isFolder") ? "folder." : "document."),
         html = "";

      if (record.getData("isFavourite"))
      {
         html = '<a class="favourite-action ' + FAVOURITE_EVENTCLASS + ' enabled" title="' + scope.msg(i18n + "remove.tip") + '" tabindex="0"></a>';
      }
      else
      {
         html = '<a class="favourite-action ' + FAVOURITE_EVENTCLASS + '" title="' + scope.msg(i18n + "add.tip") + '" tabindex="0">' + scope.msg(i18n + "add.label") + '</a>';
      }

      return html;
   };

   /**
    * Generate "Likes" UI
    *
    * @method generateLikes
    * @param scope {object} DocumentLibrary instance
    * @param record {object} DataTable record
    * @return {string} HTML mark-up for Likes UI
    */
   Alfresco.component.SimpleDocList.generateLikes = function SimpleDocList_generateLikes(scope, record)
   {
      var likes = record.getData("likes"),
         i18n = "like." + (record.getData("isFolder") ? "folder." : "document."),
         html = "";

      if (likes.isLiked)
      {
         html = '<a class="like-action ' + LIKE_EVENTCLASS + ' enabled" title="' + scope.msg(i18n + "remove.tip") + '" tabindex="0"></a>';
      }
      else
      {
         html = '<a class="like-action ' + LIKE_EVENTCLASS + '" title="' + scope.msg(i18n + "add.tip") + '" tabindex="0">' + scope.msg(i18n + "add.label") + '</a>';
      }

      html += '<span class="likes-count">' + $html(likes.totalLikes) + '</span>';

      return html;
   };

   /**
    * Generate "Comments" UI
    *
    * @method generateComments
    * @param scope {object} DocumentLibrary instance
    * @param record {object} DataTable record
    * @return {string} HTML mark-up for Comments UI
    */
   Alfresco.component.SimpleDocList.generateComments = function SimpleDocList_generateComments(scope, record)
   {
      var file = record.getData(),
         url = Alfresco.constants.URL_PAGECONTEXT + "site/" + file.location.site + "/" + (file.isFolder ? "folder" : "document") + "-details?nodeRef=" + file.nodeRef + "#comment",
         i18n = "comment." + (file.isFolder ? "folder." : "document.");

      return '<a href="' + url + '" class="comment" title="' + scope.msg(i18n + "tip") + '" tabindex="0">' + scope.msg(i18n + "label") + '</a>';
   };

   YAHOO.extend(Alfresco.component.SimpleDocList, Alfresco.component.Base,
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
          * Show File size metadata
          *
          * @property showFileSize
          * @type boolean
          * @default true
          */
         showFileSize: true
      },

      /**
       * Holds IDs to register preview tooltips with.
       *
       * @property previewTooltips
       * @type array
       */
      previewTooltips: null,

      /**
       * Holds IDs to register metadata tooltips with.
       *
       * @property metadataTooltips
       * @type array
       */
      metadataTooltips: null,

      /**
       * Fired by YUI when parent element is available for scripting
       *
       * @method onReady
       */
      onReady: function SimpleDocList_onReady()
      {
         var me = this;

         // Tooltip for thumbnail on mouse hover
         this.widgets.previewTooltip = new YAHOO.widget.Tooltip(this.id + "-previewTooltip",
         {
            width: "108px"
         });
         this.widgets.previewTooltip.contextTriggerEvent.subscribe(function(type, args)
         {
            var context = args[0],
               record = me.widgets.alfrescoDataTable.getData(context.id),
               thumbnailUrl = Alfresco.constants.PROXY_URI + "api/node/" + record.nodeRef.replace(":/", "") + "/content/thumbnails/doclib?c=queue&ph=true";

            this.cfg.setProperty("text", '<img src="' + thumbnailUrl + '" />');
         });

         // Tooltip for metadata on mouse hover
         this.widgets.metadataTooltip = new YAHOO.widget.Tooltip(this.id + "-metadataTooltip");
         this.widgets.metadataTooltip.contextTriggerEvent.subscribe(function(type, args)
         {
            var context = args[0],
               record = me.widgets.alfrescoDataTable.getData(context.id),
               locn = record.location;

            var text = '<em>' + me.msg("label.site") + ':</em> ' + $html(locn.siteTitle) + '<br />';
            text += '<em>' + me.msg("label.path") + ':</em> ' + $html(locn.path);

            this.cfg.setProperty("text", text);
         });

         /**
          * Create datatable
          */
         this.widgets.alfrescoDataTable = new Alfresco.util.DataTable(
         {
            dataSource:
            {
               url: this.getWebscriptUrl(),
               initialParameters: this.getParameters(),
               config:
               {
                  responseSchema:
                  {
                     resultsList: "items"
                  }
               }
            },
            dataTable:
            {
               container: this.id + "-documents",
               columnDefinitions:
               [
                  { key: "thumbnail", sortable: false, formatter: this.bind(this.renderCellThumbnail), width: 16 },
                  { key: "detail", sortable: false, formatter: this.bind(this.renderCellDetail) }
               ],
               config:
               {
                  className: "alfresco-datatable simple-doclist",
                  renderLoopSize: 4
               }
            }
         });

         // Override DataTable function to set custom empty message
         var me = this,
            dataTable = this.widgets.alfrescoDataTable.getDataTable(),
            original_doBeforeLoadData = dataTable.doBeforeLoadData;

         dataTable.doBeforeLoadData = function SimpleDocList_doBeforeLoadData(sRequest, oResponse, oPayload)
         {
            if (oResponse.results.length === 0)
            {
               oResponse.results.unshift(
               {
                  isInfo: true,
                  title: me.msg("empty.title"),
                  description: me.msg("empty.description")
               });
            }

            return original_doBeforeLoadData.apply(this, arguments);
         };

         // Rendering complete event handler
         dataTable.subscribe("renderEvent", function()
         {
            // Register tooltip contexts
            this.widgets.previewTooltip.cfg.setProperty("context", this.previewTooltips);
            this.widgets.metadataTooltip.cfg.setProperty("context", this.metadataTooltips);
         }, this, true);

         // Hook favourite document events
         var fnFavouriteHandler = function SimpleDocList_fnFavouriteHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               me.onFavourite.call(me, args[1].target.offsetParent, owner);
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction(FAVOURITE_EVENTCLASS, fnFavouriteHandler);

         // Hook like/unlike events
         var fnLikesHandler = function SimpleDocList_fnLikesHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               me.onLikes.call(me, args[1].target.offsetParent, owner);
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction(LIKE_EVENTCLASS, fnLikesHandler);
      },

      /**
       * Generate base webscript url.
       * Can be overridden.
       *
       * @method getWebscriptUrl
       */
      getWebscriptUrl: function SimpleDocList_getWebscriptUrl()
      {
         return Alfresco.constants.PROXY_URI + "slingshot/doclib/doclist/documents/node/alfresco/sites/home?max=50";
      },

      /**
       * Generates webscript parameters based on current filters, etc.
       * Meant to be overridden depending on use case.
       *
       * @method getParameters
       */
      getParameters: function SimpleDocList_getParameters()
      {
         return "";
      },

      /**
       * Reloads the DataTable
       *
       * @method reloadDataTable
       */
      reloadDataTable: function SimpleDocList_reloadDataTable()
      {
         // Reset tooltips arrays
         this.previewTooltips = [];
         this.metadataTooltips = [];

         this.widgets.alfrescoDataTable.loadDataTable(this.getParameters());
      },

      /**
       * Thumbnail custom datacell formatter
       *
       * @method renderCellThumbnail
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellThumbnail: function SimpleDocList_renderCellThumbnail(elCell, oRecord, oColumn, oData)
      {
         var columnWidth = 40,
            record = oRecord.getData(),
            desc = "";

         if (record.isInfo)
         {
            columnWidth = 52;
            desc = '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/help-docs-bw-32.png" />';
         }
         else
         {
            var name = record.fileName,
               extn = name.substring(name.lastIndexOf(".")),
               locn = record.location,
               nodeRef = new Alfresco.util.NodeRef(record.nodeRef),
               docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + locn.site + "/document-details?nodeRef=" + nodeRef.toString();

            if (this.options.simpleView)
            {
               /**
                * Simple View
                */
               var id = this.id + '-preview-' + oRecord.getId();
               desc = '<span id="' + id + '" class="icon32"><a href="' + docDetailsUrl + '"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/filetypes/' + Alfresco.util.getFileIcon(name) + '" alt="' + extn + '" title="' + $html(name) + '" /></a></span>';

               // Preview tooltip
               this.previewTooltips.push(id);
            }
            else
            {
               /**
                * Detailed View
                */
               columnWidth = 100;
               desc = '<span class="thumbnail"><a href="' + docDetailsUrl + '"><img src="' + Alfresco.constants.PROXY_URI + 'api/node/' + nodeRef.uri + '/content/thumbnails/doclib?c=queue&ph=true" alt="' + extn + '" title="' + $html(name) + '" /></a></span>';
            }
         }

         oColumn.width = columnWidth;

         Dom.setStyle(elCell, "width", oColumn.width + "px");
         Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

         elCell.innerHTML = desc;
      },

      /**
       * Detail custom datacell formatter
       *
       * @method renderCellDetail
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellDetail: function SimpleDocList_renderCellDetail(elCell, oRecord, oColumn, oData)
      {
         var record = oRecord.getData(),
            desc = "";

         if (record.isInfo)
         {
            desc += '<div class="empty"><h3>' + record.title + '</h3>';
            desc += '<span>' + record.description + '</span></div>';
         }
         else
         {
            var id = this.id + '-metadata-' + oRecord.getId(),
               version = "",
               description = '<span class="faded">' + this.msg("details.description.none") + '</span>',
               dateLine = "",
               canComment = record.permissions.userAccess.create,
               locn = record.location,
               nodeRef = new Alfresco.util.NodeRef(record.nodeRef),
               docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + locn.site + "/document-details?nodeRef=" + nodeRef.toString();

            // Description non-blank?
            if (record.description && record.description !== "")
            {
               description = $links($html(record.description));
            }

            // Version display
            if (record.version && record.version !== "")
            {
               version = '<span class="document-version">' + $html(record.version) + '</span>';
            }
            
            // Date line
            var dateI18N = "modified", dateProperty = record.modifiedOn;
            if (record.custom && record.custom.isWorkingCopy)
            {
               dateI18N = "editing-started";
            }
            else if (record.modifiedOn === record.createdOn)
            {
               dateI18N = "created";
               dateProperty = record.createdOn;
            }
            if (Alfresco.constants.SITE === "")
            {
               dateLine = this.msg("details." + dateI18N + "-in-site", $relTime(dateProperty), $siteDashboard(locn.site, locn.siteTitle, 'class="site-link theme-color-1" id="' + id + '"'));
            }
            else
            {
               dateLine = this.msg("details." + dateI18N + "-by", $relTime(dateProperty), $userProfile(record.modifiedByUser, record.modifiedBy, 'class="theme-color-1"'));
            }

            if (this.options.simpleView)
            {
               /**
                * Simple View
                */
               desc += '<h3 class="filename simple-view"><a class="theme-color-1" href="' + docDetailsUrl + '">' + $html(record.displayName) + '</a></h3>';
               desc += '<div class="detail"><span class="item-simple">' + dateLine + '</span></div>';
            }
            else
            {
               /**
                * Detailed View
                */
               desc += '<h3 class="filename"><a class="theme-color-1" href="' + docDetailsUrl + '">' + $html(record.displayName) + '</a>' + version + '</h3>';

               desc += '<div class="detail">';
               desc +=    '<span class="item">' + dateLine + '</span>';
               if (this.options.showFileSize)
               {
                  desc +=    '<span class="item">' + Alfresco.util.formatFileSize(record.size) + '</span>';
               }
               desc += '</div>';
               desc += '<div class="detail"><span class="item">' + description + '</span></div>';

               /* Favourite / Likes / Comments */
               desc += '<div class="detail detail-social">';
               desc +=    '<span class="item item-social">' + Alfresco.component.SimpleDocList.generateFavourite(this, oRecord) + '</span>';
               desc +=    '<span class="item item-social item-separator">' + Alfresco.component.SimpleDocList.generateLikes(this, oRecord) + '</span>';
               if (canComment)
               {
                  desc +=    '<span class="item item-social item-separator">' + Alfresco.component.SimpleDocList.generateComments(this, oRecord) + '</span>';
               }
               desc += '</div>';
            }
            
            // Metadata tooltip
            this.metadataTooltips.push(id);
         }

         elCell.innerHTML = desc;
      },

      /**
       * Like/Unlike event handler
       *
       * @method onLikes
       * @param row {HTMLElement} DOM reference to a TR element (or child thereof)
       */
      onLikes: function SimpleDocList_onLikes(row)
      {
         var file = this.widgets.alfrescoDataTable.getData(row),
            nodeRef = new Alfresco.util.NodeRef(file.nodeRef),
            likes = file.likes;

         likes.isLiked = !likes.isLiked;
         likes.totalLikes += (likes.isLiked ? 1 : -1);

         var responseConfig =
         {
            successCallback:
            {
               fn: function SimpleDocList_onLikes_success(event, p_nodeRef)
               {
                  var data = event.json.data;
                  if (data)
                  {
                     // Update the record with the server's value
                     var record = this.widgets.alfrescoDataTable.findRecordByParameter("nodeRef", p_nodeRef),
                        file = record.getData(),
                        likes = file.likes;

                     likes.totalLikes = data.ratingsCount;
                     this.widgets.alfrescoDataTable.getDataTable().updateRow(record, file);

                     // Post to the Activities Service on the "Like" action
                     if (likes.isLiked)
                     {
                        var activityData =
                        {
                           nodeRef: file.nodeRef
                        };
                        Alfresco.Share.postActivity(this.options.siteId, "file-liked", file.fileName, "document-details", activityData);
                     }
                  }
               },
               scope: this,
               obj: nodeRef.toString()
            },
            failureCallback:
            {
               fn: function SimpleDocList_onLikes_failure(event, p_nodeRef)
               {
                  // Reset the flag to it's previous state
                  var record = this.widgets.alfrescoDataTable.findRecordByParameter("nodeRef", p_nodeRef),
                     file = record.getData(),
                     likes = file.likes;

                  likes.isLiked = !likes.isLiked;
                  likes.totalLikes += (likes.isLiked ? 1 : -1);
                  this.widgets.alfrescoDataTable.getDataTable().updateRow(record, file);
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: this.msg("message.save.failure", file.displayName)
                  });
               },
               scope: this,
               obj: nodeRef.toString()
            }
         };

         if (likes.isLiked)
         {
            this.services.likes.set(nodeRef, 1, responseConfig);
         }
         else
         {
            this.services.likes.remove(nodeRef, responseConfig);
         }
         this.widgets.alfrescoDataTable.getDataTable().updateRow(record, file);
      },

      /**
       * Handler to set/reset favourite for document or folder
       *
       * @method onFavourite
       * @private
       * @param row {HTMLElement} DOM reference to a TR element (or child thereof)
       * @param prefKey {String} The preferences key
       */
      onFavourite: function SimpleDocList_onFavourite(row)
      {
         var record = this.widgets.alfrescoDataTable.getRecord(row),
            file = record.getData(),
            nodeRef = file.nodeRef;

         file.isFavourite = !file.isFavourite;
         this.widgets.alfrescoDataTable.getDataTable().updateRow(record, file);

         var responseConfig =
         {
            failureCallback:
            {
               fn: function SimpleDocList_onFavourite_failure(event, p_oRow)
               {
                  // Reset the flag to it's previous state
                  var record = this.widgets.alfrescoDataTable.getRecord(p_oRow),
                     file = record.getData();

                  file.isFavourite = !file.isFavourite;
                  this.widgets.alfrescoDataTable.getDataTable().updateRow(record, file);
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: this.msg("message.save.failure", file.displayName)
                  });
               },
               scope: this,
               obj: row
            }
         };

         this.services.preferences[file.isFavourite ? "add" : "remove"].call(this.services.preferences, Alfresco.service.Preferences.FAVOURITE_DOCUMENTS, nodeRef, responseConfig);
      }
   });
})();