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
 * Alfresco.CalendarView
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
   
   Alfresco.CalendarView = function(htmlId)
   {
      this.name = "Alfresco.CalendarView";
      this.id = htmlId;

      this.currentDate = new Date();
      this.months = Alfresco.util.message("months.long").split(",");
      this.eventData = {};
      this.VIEW_CONTAINER =
      [
         this.id + "-day",
         this.id + "-week",
         this.id + "-month",
         this.id + "-agenda"
      ];

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["calendar", "button"], this.onComponentsLoaded, this);

      return this;
   }

   Alfresco.CalendarView.prototype =
   {
      /**
       * Array of month names.
       *
       * @property months
       * @type array
       */
      months: null,

      /**
       * Event data (cached).
       *
       * @property eventData
       * @type object
       */
      eventData: null,

      /**
       * The index of the view that is currently selected:
       * 0 -> Day; 1 -> Week; 2 -> Month; 3 -> Agenda
       * 
       * All view indices are 0-based.
       *
       * @property activeView
       * @type integer
       * @default 2
       */
      activeView: 2,
      
      /**
       * Array of DIVs containing content for each view
       *
       * @property VIEW_CONTAINER
       * @type array
       */
      VIEW_CONTAINER: null,
         
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
       * Set messages for this component
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
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
       * Initialises components, including YUI widgets and loads event data.
       *
       * @method init
       */
      init: function()
      {
         // Listen for any navigation events
         YAHOO.Bubbling.on("nextNav", this.onNextNav, this);
         YAHOO.Bubbling.on("prevNav", this.onPrevNav, this);
         YAHOO.Bubbling.on("todayNav", this.onTodayNav, this);
         YAHOO.Bubbling.on("viewChanged", this.onViewChanged, this);
         YAHOO.Bubbling.on("dateChanged", this.onDateChanged, this);

         // Load the data
         this._loadData();

         // Decoupled event listeners
         YAHOO.Bubbling.on("eventSaved", this.onEventSaved, this);
         // Listen for when an event has been deleted as view will need refreshing.
         YAHOO.Bubbling.on("eventDeleted", this.onEventDelete, this);
         // Listen for when an event has been updated
         YAHOO.Bubbling.on("eventUpdated", this.onEventUpdated, this);

         YAHOO.Bubbling.on("tagSelected", this.onTagSelected, this);
         
         // Set-up initial view
         YAHOO.Bubbling.fire("viewChanged",
         {
            activeView: this.activeView
         });
      },
        
      onTagSelected: function(e, args)
      {
         var tagname = args[1].tagname;

         var id = this.VIEW_CONTAINER[this.activeView];
         var context = Dom.get(id);

         // TODO: do something with the tag name for all tags
         if (tagname === Alfresco.util.message("label.all-tags", this.name))
         {
            var divs = Dom.getElementsByClassName('eventDeselect', 'div', context);
            for (var i=0; i < divs.length; i++)
            {
               Dom.removeClass(divs[i], 'eventDeselect');
            }

            this._tagSelected = "";
         }
         else if (this._tagSelected !== tagname) 
         {
            var div, divs = Dom.getElementsByClassName('cal-event-entry', 'div', context);
            for (var x=0; x < divs.length; x++) 
            {
               div = divs[x];

               if (Dom.hasClass(div, 'eventDeselect'))
               {
                  Dom.removeClass(div, 'eventDeselect');
               }

               if (!Dom.hasClass(div, 'cal-' + tagname)) 
               {
                  Dom.addClass(divs[x], 'eventDeselect');
               }
            }

            this._tagSelected = tagname;
         }           
      },


      /**
       * Event handlers for the navigation buttons - Next, Prev and Today
       */
      
      /**
       * Fired when the user selects the "Next" button.
       * For example, if the user is looking at the month view, when the user
       * selects "Next", the next month will be displayed.
       *
       * @method onNextNav
       * @param e {object} DomEvent
       */
      onNextNav: function(e, args)
      {
         var DateMath = YAHOO.widget.DateMath;
         var fields = [DateMath.DAY, DateMath.WEEK, DateMath.MONTH, null];
         
         var field = fields[this.activeView];
         if (field)
         {
            this.currentDate = DateMath.add(this.currentDate, field, 1);
            this._refreshCurrentView();
         }
      },
      
      /**
       * Fired when the user selects the "Previous" button.
       * For example, if the user is looking at the month view, when the user
       * selects "Previous", the previous month will be displayed.
       *
       * @method onPrevNav
       * @param e {object} DomEvent
       */
      onPrevNav: function(e, args)
      {
         var DateMath = YAHOO.widget.DateMath;
         var fields = [DateMath.DAY, DateMath.WEEK, DateMath.MONTH, null];
         
         var field = fields[this.activeView];
         if (field)
         {
            this.currentDate = DateMath.subtract(this.currentDate, field, 1);
            this._refreshCurrentView();
         }
      },
      
      /**
       * Sets the current date (and view) to today.
       *
       * @method onTodayNav
       * @param e {object} DomEvent
       */
      onTodayNav: function(e, args)
      {
         this.currentDate = new Date();
         this._refreshCurrentView();
      },

      /**
       * Sets the current view
       *
       * @method onViewChanged
       * @param e {object} DomEvent
       * @param args {array} Event arguments
       */
      onViewChanged: function(e, args)
      {
         var obj = args[1];
         if (obj)
         {
            this.activeView = obj.activeView;
            for (var i = 0, ii = this.VIEW_CONTAINER.length; i < ii; i++)
            {
               Dom.setStyle(this.VIEW_CONTAINER[i], "display", i == this.activeView ? "block" : "none");
            }
            this._refreshCurrentView();
         }
      },
      
      /**
       * Sets the current date
       *
       * @method onDateChanged
       * @param e {object} DomEvent
       * @param args {array} Event arguments
       */
      onDateChanged: function(e, args)
      {
         var obj = args[1];
         if (obj && obj.date)
         {
            this.currentDate = new Date(obj.date);
            this._refreshCurrentView();
         }
      },

      /**
       * Loads the most recent event data. Resets the (cached) data.
       *
       * @method _loadData
       */
      _loadData: function()
      {
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "calendar/eventList",
            dataObj:
            {
               "site": this.siteId
            },
            successCallback:
            {
               fn: this.onDataLoad,
               scope: this
            },
            failureMessage: Alfresco.util.message("load.fail", "Alfresco.CalendarView")
         });
      },

      /**
       * Refreshes currently active view
       *
       * @method _refreshCurrentView
       */
      _refreshCurrentView: function()
      {
         var funcs = [this.refreshDay, this.refreshWeek, this.refreshMonth, this.refreshAgenda];
         
         var fn = funcs[this.activeView];
         if (fn)
         {
            var args = [this.currentDate];
            fn.apply(this, args);
         }
      },
      
      /**
       * Gets called when an event is (successfully) updated.
       *
       * @method onEventUpdated
       * @param e {object} Event fired
       * @param args {array} Event parameters
       */
      onEventUpdated: function(e, args)
      {
         this._loadData(); // refresh the data
      },
      
      /**
       * Gets fired when an event is deleted. Removes the event from the cached data
       * and refreshes the view, if necessary.
       * 
       * @method onEventDelete
       * @param e {object} Event fired
       * @param args {array} Event parameters
       */
      onEventDelete: function(e, args)
      {
         var obj = args[1];
         if (obj)
         {
            var events = this.eventData[obj.from];
            if (events)
            {
               // Try and find the event that was just deleted and remove it
               for (var i=0; i < events.length; i++)
               {
                  var e = events[i];
                  if (e.name === obj.name)
                  {
                     // Remove it
                     events.splice(i, 1);
                     this._refreshCurrentView();
                     break;
                  }
               }
            }
         }
      },

      /**
       * View Refresh Required event handler.
       * Called when a new event has been created.
       * Updates the current view with details of the newly created event.
       *
       * @method onEventSaved
       * @param e {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onEventSaved: function(e, args)
      {
         var DateMath = YAHOO.widget.DateMath;
         var obj = args[1];
         if (obj)
         {
            var events = this.eventData[obj.from];
            if (events === undefined)
            {
               events = [];
               this.eventData[obj.from] = events;
            }
   
            events.push(
            {
               "name": obj.name,
               "start": obj.start,
               "end": obj.end,
               "uri": obj.uri,
               "from": obj.from,
               "to": obj.to,
               "tags": obj.tags
            });
   
            // Need to re-order on start time
            events.sort(function(a,b)
            {
               var startTimeA = a.start.split(":");
               var startTimeB = b.start.split(":");
      
               if (startTimeA[0] < startTimeB[0] || startTimeA[1] < startTimeB[1])
               {
                  return 0;
               }
               else
               {
                  return 1;
               }
            });
   
            var dateStr = obj.from.split("/");

            var eventDate = new Date();
            eventDate.setYear(dateStr[2]);
            eventDate.setMonth(dateStr[0] - 1);
            eventDate.setDate(dateStr[1]);

            var dateBegin, dateEnd, f;
            
            // For the current view, figure out if it needs updating based on the event that was just created
            switch (this.activeView)
            {
               case 0: // day
                  dateBegin = DateMath.getDate(this.currentDate.getFullYear(), this.currentDate.getMonth(), this.currentDate.getDate());
                  dateEnd = DateMath.add(dateBegin, DateMath.DAY, 1);
                  f = this.refreshDay;
                  break;

               case 1: // week
                  dateBegin = DateMath.subtract(this.currentDate, DateMath.DAY, this.currentDate.getDay());
                  dateBegin.setHours(0, 0, 0);
                  dateEnd = DateMath.add(dateBegin, DateMath.DAY, 7);
                  f = this.refreshWeek;
                  break;

               case 2: // month
                  dateBegin = DateMath.findMonthStart(this.currentDate);
                  var monthEnd = DateMath.findMonthEnd(this.currentDate);
                  // This is to catch events that occur on the last day of the current mont
                  dateEnd = DateMath.add(monthEnd, DateMath.DAY, 1);
                  f = this.refreshMonth;
                  break;
               
               case 3: // agenda
                  dateBegin = new Date();
                  dateEnd = DateMath.add(dateBegin, DateMath.YEAR, 999)
                  f = this.refreshAgenda;
            }

            if (DateMath.between(eventDate, dateBegin, dateEnd))
            {
               var args = [this.currentDate];
               f.apply(this, args);
            }
         }
      },

      /**
       * Fired when the event data has loaded successfully.
       * Caches the data locally and updates the view with the current event data.
       *
       * @method onDataLoad
       * @param o {object} DomEvent
       */
      onDataLoad: function(o)
      {
         this.eventData = YAHOO.lang.JSON.parse(o.serverResponse.responseText);
         // Initialise the current view 
         this._refreshCurrentView();

         // Now that the data has been loaded we can display the calendar
         Dom.get('calendar-view').style.visibility = "visible";

         // Fire "onEventDataLoad" event to inform other components to refresh their view
         YAHOO.Bubbling.fire('onEventDataLoad',
         {
            source: this
         });
      },

      /**
       * Updates the view to display events that occur during the specified period
       * as indicated by the "year" and "month" parameters.
       *
       * @method refresh
       * @param year {integer}
       * @param month {integer}
       */
      refreshMonth: function(date)
      {
         var DateMath = YAHOO.widget.DateMath;
         /* Set to the first day of the month */
         var startDate = DateMath.findMonthStart(date);
         var startDay = startDate.getDay();
         var endDate = DateMath.findMonthEnd(date);

         /* Change the month label */
         var month = date.getMonth();
         var label = Dom.get(this.id + "-monthLabel");
         label.innerHTML = this.months[month] + " " + date.getFullYear();

         var days_in_month = endDate.getDate();
         var daynum = 1;

         for (var i = 0; i < 42; i++)
         {
            var elem = Dom.get("cal_month_t_" + i);
            if (elem !== null)
            {
               elem.innerHTML = ""; /* reset */
               if (startDay <= i && i < (startDay + days_in_month))
               {
                  var h = document.createElement('div');
                  Dom.addClass(h, 'dayLabel');
                  h.innerHTML = "<a href=\"#\">" + daynum + "</a>"; /* JavaScript days are 1 less */
                  elem.appendChild(h);

                  var key = (month+1) + "/" + daynum + "/" + date.getFullYear();
                  var events = this.eventData[key];
                  if (events)
                  {
                     for (var j=0; j < events.length; j++)
                     {
                        var d = this._createEventContainer(events[j]);
                        elem.appendChild(d);
                     }
                  }
                  ++daynum;
               }
            }
         }
      },

      onEventClick: function(e, obj)
      {
         Event.stopEvent(e);
         // Stop listening after first click so we don't launch the event info panel several times...
         Event.removeListener(obj.div, 'click', this.onEventClick);
         var panel = new Alfresco.EventInfo(this.id + "-eventInfo");
         panel.setOptions(
         {
            siteId: this.siteId,
            onClose:
            {
               fn: function()
               {
                  // ... and start listening again when the event info panel is closed.
                  Event.addListener(obj.div, 'click', this.onEventClick,
                  {
                     event: obj.event,
                     div: obj.div
                  }, this);
               },
               scope: this,
               obj: obj.div
            }
         });
         panel.show(obj.event); // event object
      },

      /**
       * Functions specific to the week view
       *
       */

     /**
      * Given a date displays the event(s) for the week the date falls in.
      * Weeks start from Sunday. For example, if given a date that falls on a Wednesday,
      * events that start from the previous Sunday will be displayed.
      *
      * @param date {Date} JavaScript date object
      * @method  displayPrevMonth
      */
      refreshWeek: function(date)
      {
         var DateMath = YAHOO.widget.DateMath;

         var startDate = DateMath.subtract(date, DateMath.DAY, date.getDay());
         var endDate = DateMath.add(startDate, DateMath.DAY, 6);
      
         /* Update the week label */
         var weekLabel = "";
         if (startDate.getMonth() === endDate.getMonth())
         {
            weekLabel = startDate.getDate() + " - " + Alfresco.util.formatDate(endDate, "d mmm yyyy");
         }
         else
         {
            weekLabel = Alfresco.util.formatDate(startDate, "d mmm yyyy") + " - " + Alfresco.util.formatDate(endDate, "d mmm yyyy");
         }
      
         var label = Dom.get(this.id + "-weekLabel");
         label.innerHTML = weekLabel;

         var colElem;
         var colDate = startDate;
         /* Update the column headers */
         for (var col=0; col < 7; col++)
         {
            colElem = Dom.get(this.id + "-weekheader-" + col);
            if (colElem)
            {
               colElem.innerHTML = Alfresco.util.formatDate(colDate, "ddd d/m");
            }
            colDate = DateMath.add(colDate, DateMath.DAY, 1);
         }

         // Clear any previous events
         var container = Dom.get("week-view");
         if (container)
         {
            var elems = this._getWeekViewEvents(container, "cal-event-entry");
            if (elems)
            {   
               for (var i=0; i < elems.length; i++)
               {
                  // FIXME: the node should really be deleted
                  elems[i].innerHTML = "";  
               }
            }
         }

         for (var day=0; day < 7; day++)
         {
            /* Event data is keyed on m/d/yyyy */
            var events = this.eventData[ Alfresco.util.formatDate(startDate, "m/d/yyyy") ];
            if (events)
            {
               for (var i=0; i < events.length; i++)
               {
                  var event = events[i];
                  var startTime = event.start;
                  if (startTime)
                  {
                     var parts = startTime.split(":");
                     var hours = parseInt(parts[0]);
                     var minutes = parseInt(parts[1]);

                     // Figure out where the event should be placed
                     var row = hours * 2 + (minutes > 0 ? 1 : 0);
                     var col = startDate.getDay();
                     var id = this.id + "_calendar_cell" + (row*7 + col);

                     var elem = Dom.get(id);
                     elem.innerHTML = ""; // reset
                     if (elem)
                     {
                        var d = this._createEventContainer(event);
                        elem.appendChild(d);
                     }
                  }
                }
             }
             startDate = DateMath.add(startDate, DateMath.DAY, 1);
         }
     },

     _getWeekViewEvents: function(container, className)
     {
        var arrElems = container.getElementsByTagName("div");
        className = className.replace(/\-/g, "\\-");
        var regExp = new RegExp("(^|\\s)" + className + "(\\s|$)");
        var returnElems = [];
        var elem;
        for (var i=0; i < arrElems.length; i++)
        {
           elem = arrElems[i];
           if (regExp.test(elem.className))
           {
              returnElems.push(elem);
           }
        }   
        return returnElems;
     },

     /**
      * Functions specific to the day view
      *
      *
      */

     /**
      * Given a date displays the event(s) for that date.
      * Figures out if events overlap and, if so, alters how the events
      * are displayed appropriately.
      *
      * @param date {Date} JavaScript date object
      * @method  refreshDay
      */
      refreshDay: function(date)
      {
         var DateMath = YAHOO.widget.DateMath;

         /* Change the day label */
         var label = Dom.get(this.id + "-dayLabel");
         label.innerHTML = Alfresco.util.formatDate(date, "dd mmm yyyy");

         var WIDTH = 88;
         var HEIGHT = 18;
         var DENOM = 1000 * 60 * 30; // 30 minute slots

         var container = Dom.get(this.id + "-dayEventsView");
         container.innerHTML = ""; // reset
         var events = this.eventData[ Alfresco.util.formatDate(date, "m/d/yyyy") ];
         var total = events.length;
         if (total > 0)
         {
            var indents = [];
            // Assumes that events are sorted by start time
            for (var i=0; i < total; i++)
            {
               var event = events[i];
               // TODO: sort this out
               var startDate = new Date(date.getTime());
               var startTime = event.start.split(":");
               startDate.setHours(startTime[0], startTime[1]);

               var endDate = new Date(date.getTime());
               if (!event.end)
               {
                  event.end = "23:00"; // TODO: choose a sensible default
               }
               var endTime = event.end.split(":");
               endDate.setHours(endTime[0], endTime[1]);

               indents[i] = 0; // initialise
               var indent = 0;
               // Check the previous events for overlap
               for (var j = i-1; j >= 0; j--)
               {
                  /**
                   * Events are already sorted by start time
                   */
                  var e = events[j];
                  var sDate = new Date(date.getTime());
                  var sTime = e.start.split(":");
                  sDate.setHours(sTime[0], sTime[1]);

                  var eDate = new Date(date.getTime());
                  var eTime = e.end.split(":");
                  eDate.setHours(eTime[0], eTime[1]);
                     
                  // Check to see if the events overlap
                  if (DateMath.after(eDate, startDate)) 
                  {
                     if (indent === indents[j]) 
                     {
                        indent += 1;
                     }
                  }
                }
                // Store the offset for each event
                indents[i] = indent;

                // Now display the event
                var div = this._createEventContainer(event);
                Dom.addClass(div, "dayEvent");
                
                // Figure out the height of the div based upon
                // the number of half hour slots it occupies
                var span = Math.round((endDate.getTime() - startDate.getTime()) / DENOM);
                div.style.height = (HEIGHT * span) + "px";
                // Set the position
                var top = startDate.getHours() * 2 + (startDate.getMinutes() > 0 ? 1 : 0);
                div.style.top = (HEIGHT * top - 2) + "px";
                div.style.left = (1 + WIDTH * indent) + "px";

                container.appendChild(div);
             }
         }
     },
     
      _createEventContainer: function(event, label)
      {
         label = label || event.name; // set default value if none specified

         var div = document.createElement("div");
         var classes = ["cal-event-entry"];
         for (var i=0; i < event.tags.length; i++)
         {
            classes.push("cal-" + event.tags[i]);
         }
         div.setAttribute("class", classes.join(" "));
         div.innerHTML = '<a href="#">' + $html(label) + '</a>';

         // Listen for clicks on the event div so we later can launch the event info panel
         Event.addListener(div, 'click', this.onEventClick,
         {
            event: event,
            div: div
         }, this);
         return div;
      },

      /**
       * Methods specific to the agenda view 
       * of events.
       */

      /**
       * Updates the agenda view. Currently displays ALL the events for a site.
       *
       * @method refreshAgenda
       */
      refreshAgenda: function()
      {
         var DateMath = YAHOO.widget.DateMath;

         var elem = Dom.get(this.id + "-agendaContainer");

         if (elem.hasChildNodes())
         {
            while (elem.childNodes.length >= 1)
            {
               elem.removeChild(elem.firstChild);       
            } 
         }
         
         var now = new Date(), dateParts, eventDate, div, atLeastOneEvent = false;
         now.setHours(0, 0, 0, 0);
      
         for (var key in this.eventData)
         {
            if (this.eventData.hasOwnProperty(key))
            {
               dateParts = key.split("/");
               eventDate = DateMath.getDate(dateParts[2], (dateParts[0]-1), dateParts[1]);
               if (eventDate >= now)
               {
                  atLeastOneEvent = true;
                  div = document.createElement("div");
                  div.setAttribute("class", "agenda-item");
                  this.renderAgendaItems(eventDate, div);
                  elem.appendChild(div);
               }
            }
         }
         
         if (!atLeastOneEvent)
         {
            div = document.createElement("div");
            div.setAttribute("class", "no-agenda-items");
            div.innerHTML = Alfresco.util.message("message.no-agenda-items", this.name);
            elem.appendChild(div);
         }
      },
   
      /**
       * This method generates the HTML to display the events 
       * for a given day, specified by the "date" parameter.
       * 
       * @method agendaItemCellRenderer
       * @param date {Date} JavaScript date object
       * @param parent {Dom} DOM element to append to
       */
      renderAgendaItems: function(date, parent)
      {
         var title = Alfresco.util.formatDate(date, "mediumDate");
         var div = document.createElement("div");
         div.setAttribute("class", "dayheader");
         div.innerHTML = title;
         parent.appendChild(div);
      
         var events = this.eventData[ Alfresco.util.formatDate(date, "m/d/yyyy") ];
         if (events && events.length > 0)
         {
            var event;
            for (var i=0; i < events.length; i++)
            {
               event = events[i];
               parent.appendChild(this._createEventContainer(event, event.start + " " + event.name));   
            }
         }
      }
   };
})();