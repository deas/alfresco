/*
 *** Alfresco.CalendarToolbar
*/
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
   
   Alfresco.CalendarToolbar = function(containerId, enabledViews, defaultView)
   {
      this.name = "Alfresco.CalendarToolbar";
      this.id = containerId;
		this.enabledViews = enabledViews;
		this.defaultView = defaultView;

      this.navButtonGroup = null;
      this.nextButton = null;
      this.prevButton = null;
      this.todayButton = null;

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection"], this.onComponentsLoaded, this);

      return this;
   };

   Alfresco.CalendarToolbar.prototype =
   {
      /**
       * Sets the current site for this component.
       * 
       * @property siteId
       * @type string
       */
      setSiteId: function(siteId)
      {
         this.siteId = siteId;
         return this;
      },

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.DocListTree} returns 'this' for method chaining
       */
      setMessages: function(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function()
      {
         Event.onContentReady(this.id, this.init, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initialises components, including YUI widgets.
       *
       * @method init
       */
      init: function()
      {
         /* Add Event Button */
         if (Dom.get(this.id + "-addEvent-button"))
         {
            Alfresco.util.createYUIButton(this, "addEvent-button", this.onButtonClick);
         }
         Alfresco.util.createYUIButton(this, "publishEvents-button", null,
         {
            type: "link"
         });
      
         this.nextButton = Alfresco.util.createYUIButton(this, "next-button", this.onNextNav);
         this.prevButton = Alfresco.util.createYUIButton(this, "prev-button", this.onPrevNav);
         this.todayButton = Alfresco.util.createYUIButton(this, "today-button", this.onTodayNav);

         this.navButtonGroup = new YAHOO.widget.ButtonGroup(this.id + "-navigation");
         if (typeof(this.navButtonGroup) != "undefined" && this.navButtonGroup._buttons != null ) // Will be undefined / null if navigation is hidden serverside (e.g. only one view enabled)
         {
            var view = Alfresco.util.getQueryStringParameter('view') || this.defaultView;
            for (var i = 0; i < this.navButtonGroup._buttons.length; i++) 
            { 
               if (this.navButtonGroup._buttons[i]._button.id.match(view)) 
               {
                  this.navButtonGroup.check(i);
                  this.disableButtons(i);
                  break;
               }
            }
            this.navButtonGroup.on("checkedButtonChange", this.onNavigation, this.navButtonGroup, this);
         }
      },

      onNextNav: function(e)
      {
         this._fireEvent("nextNav");
      },

      onPrevNav: function(e)
      {
         this._fireEvent("prevNav");         
      },

      onTodayNav: function(e)
      {
         this._fireEvent("todayNav");
      },

      onNavigation: function(e)
      {
         this.disableButtons(e.newValue.index);

         YAHOO.Bubbling.fire("viewChanged",
         {
            activeView: e.newValue.index
         });
      },
      disableButtons : function(butIndex) 
      {
         var selectedButton = this.navButtonGroup.getButtons()[butIndex];
         // disable buttons for agenda view only
         if (this.todayButton != null) // Note: Today button will be null if elements are hidden serverside
         {
            if (selectedButton.get('label') === Alfresco.util.message('label.agenda', 'Alfresco.CalendarView')) 
            {
               this.todayButton.set('disabled', true);
               this.nextButton.set('disabled', true);
               this.prevButton.set('disabled', true);
            }
            else 
            {
               this.todayButton.set('disabled', false);
               this.nextButton.set('disabled', false);
               this.prevButton.set('disabled', false);
            }
         }
      },
      _fireEvent: function(type)
      {
         YAHOO.Bubbling.fire(type, 
         {
            source: this
         });
      },

      /**
       * Fired when the "Add Event" button is clicked.
       * Displays the event creation form. Initialises the 
       * form if it hasn't been initialised. 
       *
       * @param e {object} DomEvent
       * @param obj {object} Object passed back from addListener method
       * @method  onButtonClick
       */     
      onButtonClick: function(e)
      {
         var obj = Alfresco.util.ComponentManager.findFirst("Alfresco.CalendarView");
         if (obj)
         {
             obj.showAddDialog();
         }
      }
   };
})();