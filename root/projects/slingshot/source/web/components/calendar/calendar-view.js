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
 * CalendarView base component.
 * Provides common functionality for all Calendar views.
 *
 * @namespace Alfresco
 * @class Alfresco.CalendarView
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Sel = YAHOO.util.Selector,
       fromISO8601 = Alfresco.util.fromISO8601,
       toISO8601 = Alfresco.util.toISO8601,
       dateFormat = Alfresco.thirdparty.dateFormat;
   
   /**
    * CalendarView constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.CalendarView} The new CalendarView instance
    * @constructor
    */
   Alfresco.CalendarView = function CalendarView_constructor(htmlId)
   {
      this.id = htmlId;
      Alfresco.CalendarView.superclass.constructor.call(this, "Alfresco.CalendarView", htmlId, ["calendar", "button", "resize", "datasource", "datatable"]);
      
      /* History navigation event */
      // YAHOO.Bubbling.on("stateChanged", this.onStateChanged, this);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.CalendarView, Alfresco.component.Base, 
   {
      /**
       * Object container for storing YUI widget instances.
       *
       * @property widgets
       * @type object
       */
      widgets: {},
      
      /**
       * Object container for storing module instances.
       *
       * @property modules
       * @type object
       */
      modules: {},
      
      /**
       * Object container for storing YUI pop dialog instances.
       *
       * @property popups
       * @type object
       */
      popups: {},
      
      /**
       * Object container for storing event handlers
       *
       * @property handlers
       * @type object
       */
      handlers: {},
      
      /**
       * Object container for data
       *
       * @property data
       * @type object
       */
      data: {},
      
      /**
       * View type - must be overridden by subclasses
       *
       * @property calendarView
       * @type string
       */
      calendarView: '',
      
      /**
       * Initialises event handling All events are handled through event
       * delegation via the onInteractionEvent handler
       *
       * @method initEvents
       */
      initEvents: function CalendarView_initEvents()
      {
         Event.on(this.id, 'click', this.onInteractionEvent, this, true);
         Event.on(this.id, 'dblclick', this.onInteractionEvent, this, true);
         if (this.calendarView == Alfresco.CalendarView.VIEWTYPE_MONTH) 
         {
            Event.on(this.id, 'mouseover', this.onInteractionEvent, this, true);
            Event.on(this.id, 'mouseout', this.onInteractionEvent, this, true);
         }
         
         YAHOO.Bubbling.on("eventEdited", this.onEventEdited, this);
         YAHOO.Bubbling.on("eventEditedAfter", this.onAfterEventEdited, this);
         YAHOO.Bubbling.on("eventSaved", this.onEventSaved, this);
         YAHOO.Bubbling.on("eventSavedAfter", this.onAfterEventSaved, this);
         YAHOO.Bubbling.on("eventDeleted", this.onEventDeleted, this);
         YAHOO.Bubbling.on("eventDeletedAfter", this.onAfterEventDeleted, this);
         
         YAHOO.Bubbling.on("tagSelected", this.onTagSelected, this);
         YAHOO.Bubbling.on("todayNav", this.onTodayNav, this);
         YAHOO.Bubbling.on("nextNav", this.onNav, this);
         YAHOO.Bubbling.on("prevNav", this.onNav, this);
         YAHOO.Bubbling.on("viewChanged", this.onViewChanged, this);
         YAHOO.Bubbling.on("dateChanged", this.onCalSelect, this);
         YAHOO.Bubbling.on("formValidationError", this.onFormValidationError, this);
         if (this.calendarView == Alfresco.CalendarView.VIEWTYPE_DAY | this.calendarView == Alfresco.CalendarView.VIEWTYPE_WEEK)
         {
            YAHOO.Bubbling.on("eventResized", this.onEventResized, this);
         }
      },
      
      /**
       * Retrieves events from server
       * 
       * @method getEvents
       *  
       */
      getEvents : function CalendarView_getEvents()
      {
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "calendar/events/" + this.options.siteId + "/user",
            dataObj:
            {
               from: toISO8601(this.options.startDate).split('T')[0]
            },
            //filter out non relevant events for current view            
            successCallback: 
            {
               fn: this.onEventsLoaded,
               scope: this
            },
               failureMessage: Alfresco.util.message("load.fail", "Alfresco.CalendarView")
           });
      },
      
      /**
       * Renders view
       *
       * @method render
       *
       */
      render: function CalendarView_render()
      {
      
         // render title
         switch (this.calendarView)
         {
            case Alfresco.CalendarView.VIEWTYPE_MONTH:
               this.titleEl.innerHTML = dateFormat(this.options.titleDate, Alfresco.util.message("calendar.dateFormat.month"));
               // create button on mouseover of day cells
               this.addButton = Alfresco.CalendarHelper.renderTemplate('createEventButton', 
               {
                  addEventUrl: Alfresco.constants.URL_RESCONTEXT + 'components/calendar/images/add-event-16-2.png',
                  addEvent: this.msg('label.add-event')
               });
               // highlight current date
               var now = new Date();
               if (this.options.startDate.getFullYear() === now.getFullYear() && (this.options.startDate.getMonth() == now.getMonth())) 
               {
                  var el = Dom.get('cal-' +
                  (toISO8601(now, 
                  {
                     selector: 'date'
                  })));// .split('T')[0]));
                  Dom.addClass(el, 'current');
               }
               break;
            case Alfresco.CalendarView.VIEWTYPE_WEEK:
               this.titleEl.innerHTML = dateFormat(this.options.titleDate, Alfresco.util.message("calendar.dateFormat.week"));
               break;
            case Alfresco.CalendarView.VIEWTYPE_DAY:
               this.titleEl.innerHTML = dateFormat(this.options.titleDate, Alfresco.util.message("calendar.dateFormat.day"));
               break;
         }
         
         // initialise drag and drop targets
         this.initDD();
         
         // initialise DOM Event registration
         this.initEvents();
         // pre configure config object for calendar object for current view
         this.initCalendarEvents();
         this.getEvents(dateFormat(this.options.startDate, 'yyyy-mm-dd'));
      },
      
      /**
       * Initialises drag and drop targets.
       *
       * @method initDD
       */
      initDD: function()
      {
         this.dragGroup = (this.calendarView === Alfresco.CalendarView.VIEWTYPE_MONTH) ? 'day' : 'hourSegment';
         
         var dragTargets = Dom.getElementsByClassName(this.dragGroup, 'div', YAHOO.util.Dom.get(this.options.id));
         this.hourSegments = dragTargets;
         dragTargets = dragTargets.concat(Dom.getElementsByClassName('target', 'div', YAHOO.util.Dom.get(this.options.id)));
         
         this.dragTargetRegion = YAHOO.util.Dom.getRegion(dragTargets[0]);
         
         for (var i = 0, el; el = dragTargets[i]; i++) 
         {
            new YAHOO.util.DDTarget(el, this.dragGroup);
         };
         
         // holder for fixed hourSegments (ie border bleedthrough fix)
         this._fixedHourSegments = [];
      },
      
      
      /**
       * initialise config object for calendar events
       *
       * @method initCalendarEvents
       *
       */
      initCalendarEvents: function CalendarView_initCalendarEvents()
      {
         var tickSize = (this.dragTargetRegion.bottom - this.dragTargetRegion.top) / 2;
         this.calEventConfig = 
         {
            // work out div.hourSegment half-height so we can get xTick
            // value for resize
            resize: 
            {
               yTicks: tickSize
            },
            yTick: (this.calendarView !== Alfresco.CalendarView.VIEWTYPE_MONTH) ? tickSize : null,
            xTick: (this.calendarView !== Alfresco.CalendarView.VIEWTYPE_MONTH) ? 100 : null,
            view: this.calendarView,
            resizable: ((this.calendarView === Alfresco.CalendarView.VIEWTYPE_WEEK) | (this.calendarView === Alfresco.CalendarView.VIEWTYPE_DAY))
         };
         var vEventEls = YAHOO.util.Dom.getElementsByClassName('vevent', null, YAHOO.util.Dom.get(this.options.id));
         var numVEvents = vEventEls.length;
         
         this.events = [];
         if (this.calendarView === Alfresco.CalendarView.VIEWTYPE_DAY) 
         {
            this.calEventConfig.performRender = false;
         }
         for (var i = 0; i < numVEvents; i++) 
         {
            var vEventEl = vEventEls[i];
            var id = Event.generateId(vEventEl);
            vEventEl.id = id;
            if ((this.calendarView === Alfresco.CalendarView.VIEWTYPE_WEEK) | (this.calendarView === Alfresco.CalendarView.VIEWTYPE_DAY)) 
            {
               this.calEventConfig.resizable = (Dom.hasClass(vEventEl, 'allday') || Dom.hasClass(vEventEl, 'isoutlook')) ? false : true;
            }
            this.calEventConfig.draggable = Dom.hasClass(vEventEl, 'allday') ? false : true;
            this.events[id] = new Alfresco.calendarEvent(vEventEl, this.dragGroup, this.calEventConfig);
            if (this.calendarView != Alfresco.CalendarView.VIEWTYPE_AGENDA) 
            {
               this.events[id].on('eventMoved', this.onEventMoved, this.events[id], this);
            }
            
            
            if ((this.calendarView === Alfresco.CalendarView.VIEWTYPE_WEEK) | (this.calendarView === Alfresco.CalendarView.VIEWTYPE_DAY)) 
            {
               this._adjustHeightByHour(vEventEl);
            }
         }
      },
      
      /**
       * converts data object to template compatible
       *
       * @method convertDataToTemplateData
       * @param data {object} data to convert
       * @return {object} data object with template compatible variables
       *
       */
      convertDataToTemplateData: function(data)
      {
         data.fromDate = data.dtstart;
         data.toDate = data.dtend;
         data.where = data.location;
         data.desc = data.description;
         data.name = data.summary;
         data.duration = Alfresco.CalendarHelper.getDuration(Alfresco.util.fromISO8601(data.dtstart), Alfresco.util.fromISO8601(data.dtend));
         data.start = data.dtstart.split('T')[1].substring(0, 5);
         data.end = data.dtend.split('T')[1].substring(0, 5);
         data.allday = ((data.allday === 'allday') | data.allday === 'true') ? data.allday : '';
         data.tags = (YAHOO.lang.isArray(data.tags)) ? data.tags.join(' ') : data.tags;
         return data;
      },
      
      displayMessage: function CalendarView_displayMessage(message, name)
      {
         Alfresco.util.PopupManager.displayMessage(
         {
            text: Alfresco.util.message(message, name || this.name)
         });
      },
      
      /**
       *Returns the Event Data object associated with the element passed in.
       * 
       * 
       */
      
      getEventObj: function CalendarView_getEventObj(el) 
      {
         var date, elPar, elParId, div, event, rel;
         
         if (el.rel !== "") // New Style (Used by Agenda)
         {
            return this.parseRel(el);
         }
         else //Old Style - DOM driven (Used by Day, Week, Month) 
         {
            elPar = Dom.getAncestorByClassName(elTarget, 'vevent');
            // if the event doesn't have a parent with a vevent class, it'll have one with an allday class.
            if (elPar === null) // add support for timed events that span multiple days: 
            {
               elPar = Dom.getAncestorByClassName(elTarget, 'allday');
            }
            
            elParId = elPar.id.split("-multiple")[0];
            event = this.events[elParId];
         }
         return event
      },
      
      /**
       * builds up the relationship string to store the event reference in the DOM.
       * 
       * @param {Object} data
       */
      getRel: function CalendarView_getRel(data) 
      {
         //Just stores the ISO yyyy-mm-dd string and will use href from link to identify data
         return data.from.split("T")[0];
      },
      
      /**
       * 
       * retrieves the event object based on the data in the rel string.
       * 
       * @param {HTML element} element with a relationship to an event.
       */
      parseRel: function CalendarView_parseRel(element) 
      {
         var relationship,
            data = "",
            date = "",
            result = false;
         
         // If the passed in is a datatable container, it won't have a rel, so get the first a tag in it.
         if (YAHOO.util.Selector.test(element, 'div.yui-dt-liner')) 
         {
            element = Dom.getElementsByClassName("summary", "a", element.parentNode.parentNode)[0]
         }
         
         // check the element has a rel tag supplied.
         if (element.rel !== "" && element.rel !== undefined) 
         {
            date = element.rel;
            data = this.widgets.Data[date].events;
            for (var i = 0; i < data.length; i++) 
            {
               if (data[i].uri === "/calendar/event/" + element.href.split("/calendar/event/")[1]) // element.href needs hostname and port stripping.
               {
                  result = data[i];
               } 
            }
         }
         return result;
      },
      
      /**
       * Shows/hides the early hours of day (midnight till 7am)
       *
       * @method toggleEarlyTableRows
       *
       */
      toggleEarlyTableRows: function CalendarView_toggleEarlyTableRows()
      {
      
         var triggerEl = YAHOO.util.Dom.get('collapseTrigger');
         this.earlyEls = YAHOO.util.Dom.getElementsByClassName('early', 'tr', triggerEl.parentNode);
         var displayStyle = (YAHOO.env.ua.ie) ? 'block' : 'table-row';
         for (var i = 0; i < this.earlyEls.length; i++) 
         {
            var el = this.earlyEls[i];
            YAHOO.util.Dom.setStyle(el, 'display', (this.isShowingEarlyRows) ? 'none' : displayStyle);
         }
         this.isShowingEarlyRows = !this.isShowingEarlyRows;
      },
      
      /**
       * Handler for event retrieval when events are loaded from the server
       * Used by agenda, day and week (i.e. not month view)
       *
       * @method onEventsLoaded
       */
      onEventsLoaded: function CalendarView_onEventsLoaded(o)
      {
         var data = YAHOO.lang.JSON.parse(o.serverResponse.responseText).events;
         var siteEvents = [];
         var events = [];
         var comparisonFn = null;
         var viewStartDate = this.options.startDate;
         var viewEndDate = this.options.endDate;		 
         var site = this.options.siteId;
         
         // Trigger Mini Calendar's rendering before filtering the events
         YAHOO.Bubbling.fire("eventDataLoad",data);
         
         for (var i = 0; i < data.length; i++) 
         {
            var ev = data[i];
            
            // Escape User Input Strings to avoid XSS
            ev.title = Alfresco.util.encodeHTML(ev.title);
            ev.where = Alfresco.util.encodeHTML(ev.where);
            
            if (ev.site == site) 
            {
               siteEvents.push(ev);
            }
         }
         data = siteEvents;
         // TODO: These take no account of timezone. E.g. day view of 6th June. server time = GMT+1000, event time = 06 JUN 2010 05:00GMT+1000, date returned from server is 05 JUN 2010 19:00GMT+0000. 05 JUN != 06 JUN.
         // TODO: This would be better done on the server in the userevents webscript
         comparisonFn = function()
         {
            
            return function(eventDate, endDate)
            {
               // Event can: Start before and finish after display dates
               var eventSurroundsView = (eventDate <= viewStartDate && viewEndDate <= endDate);
               // or: start during
               var startDuring = (eventDate >= viewStartDate && eventDate < viewEndDate);
               // or: finish during
               var endDuring = (endDate >= viewStartDate && endDate < viewEndDate);
               return (eventSurroundsView || startDuring || endDuring);
            };
         }.apply(this);
         
         for (var i = 0; i < data.length; i++) 
         {
            var ev = data[i];
            var date = fromISO8601(ev.when);
            var endDate = fromISO8601(ev.endDate);
            if (comparisonFn(date, endDate)) 
            {
               var datum = {};
               datum.desc = ev.description || '';
               datum.name = ev.title;
               datum.where = ev.where;
               datum.description = ev.description;
               datum.isoutlook = ev.isoutlook == "true" ? "isoutlook" : "";
               datum.contEl = 'div';
               datum.from = dateFormat(date, dateFormat.masks.isoDate) + 'T' + ev.start;
               datum.to = dateFormat(endDate, dateFormat.masks.isoDate) + 'T' + ev.end;
               datum.uri = '/calendar/event/' + this.options.siteId + '/' + ev.name + '?date=' + dateFormat(date, dateFormat.masks.isoDate);
               datum.hidden = '';
               datum.allday = '';
               datum.isMultiDay = (datum.from.split("T")[0] !== datum.to.split("T")[0]);
               datum.isAllDay = (ev.start === "00:00" && ev.end === "00:00");
               datum.el = 'div';
               datum.duration = Alfresco.CalendarHelper.getDuration(fromISO8601(datum.from), fromISO8601(datum.to));
               var days = datum.duration.match(/([0-9]+)D/);
               if (days && days[1]) 
               {
                  datum.duration = datum.duration.replace(/([0-9]+)D/, ++days[1] + 'D');
               }
               
               datum.key = datum.from.split(":")[0] + ':00';
               datum.start = ev.start;
               datum.end = ev.end;
               datum.tags = ev.tags;
               events.push(datum);
            }
         }
         
         this.renderEvents(events);
      },
      
      /**
       * Adds events to view
       *
       * @method add
       * @param {String} id Identifier of event
       * @param {Object} Event Object
       * @return {Boolean} Status of add operation
       */
      add: function CalendarView_add(id, o)
      {
         this.add(id, o);
      },
      
      /**
       * Removes events from view
       *
       * @method remove
       * @param {String} id Identifier of event
       * @return {Boolean} Status of removal operation
       */
      remove: function CalendarView_remove(id)
      {
         this.remove(id);
      },
      
      /**
       * Updates specified event
       *
       * @method update
       *
       * @param {String} id Identifier of event
       * @param {Object} Event Object
       * @return {Boolean} Status of update operation
       */
      update: function CalendarView_update(id, o)
      {
         this.data.update(o);
      },
      
      /**
       * Filters the array of events for multiday events
       * For each Multiday event, it:
       *    - Creates an event for every day in the period.
       *    - If not All day:
       *       - the first day's display end time is set to: 00:00
       *       - the middle days are marked as multiday
       *       - the last day's start time is: 00:00
       *    - Adds cloned tag.
       *       
       * TODO: Currently this is only used by the Agenda view - this needs rolling out across the other views when they get refactored.
       * 
       * @method filterMultiday
       * @param {Array of event objects} events
       */
      filterMultiday: function CalendarView_filterMultiday(events) 
      {
         var DateMath = YAHOO.widget.DateMath;
         
         for (var i=0, numEvents=events.length;i<numEvents;i++) 
         {
            var event = events[i];
            // check if event is multiday
            if (event.isMultiDay) 
            {
               var from = event.from.split("T"),
                  to = event.to.split("T"),
                  startDay = fromISO8601(from[0]),
                  endDay = fromISO8601(to[0]),
                  //Pseudo code
                  iterationDay = new Date(startDay + 86400000)
               
               // if not all day event, end time on first day needs to be midnight.
               if (!event.isAllDay) 
               {
                  event.displayEnd = "00:00";
               }
               
               for (var j=0, iterationDay=DateMath.add(startDay, DateMath.DAY, 1); iterationDay.getTime() <= endDay.getTime(); iterationDay=DateMath.add(iterationDay, DateMath.DAY, 1)) 
               {
                  var clonedEvent = YAHOO.lang.merge(event);
                  
                  // Mark as cloned and provide a marker to locate the original
                  clonedEvent.isCloned = true;
                  clonedEvent.clonedFromDate=event.from;
                  
                  // Sort out the display time.
                  if (!event.isAllDay)   
                  {
                     // If event is not the last day of the repeating sequence, it lasts all day.
                     if (!Alfresco.CalendarHelper.isSameDay(iterationDay, endDay)) 
                     {
                        clonedEvent.isAllDay = true;
                     } else 
                     {
                        // if it is the same day, we need to set the finish time, by removing the displayEnd time.
                        clonedEvent.displayStart="00:00";
                        delete clonedEvent.displayEnd;
                     }
                     
                     // set the DisplayDates for the cloned object to the current day of the loop:
                     clonedEvent.displayFrom = toISO8601(iterationDay);
                  }
                  events.push(clonedEvent);
               } 
            }
         }
         
         return events
      },
      
      /**
       * Remove dom elements that represent multiple day events
       *
       * @method removeMultipleDayEvents
       */
      removeMultipleDayEvents: function CalendarView_removeMultipleDayEvents(srcEl)
      {
         var els = Dom.getElementsByClassName(srcEl.id + '-multiple');
         for (var i = 0, len = els.length; i < len; i++) 
         {
            els[i].parentNode.removeChild(els[i]);
         }
      },
      /**
       * Returns the current date that the user clicked on
       *
       * @method getClickedDate
       * @param el {DOMElement} the element that was clicked on
       * @returns {Date}
       */
      getClickedDate: function CalendarView_getClickedDate(el)
      {
         var date = this.options.titleDate; // set a default incase we can't find the element
         if (el.nodeName.toUpperCase() !== 'TD') 
         {
            el = Dom.getAncestorByTagName(el, 'td');
         }
         if (el) 
         {
            date =  fromISO8601(el.id.replace('cal-', ''));
         }
         return date;
      },
      
      /**
       * Adjusts height of specified event depending on its duration
       *
       * @method _adjustHeightByHour
       * @param el {object} Event element to adjust
       */
      _adjustHeightByHour: function(el)
      {
         //TODO - get this from css class;
         var hourHeight = 4.75; //em
         //adjust height dependent on durations
         if (this.calendarView != Alfresco.CalendarView.VIEWTYPE_MONTH) 
         {
            var start, end = null;
			   // This needs to work for Week view too...
            if (this.events[el.id]) 
				{
					var start = this.events[el.id].getData("dtstart");
					var startTimes = start.split("T")[1].split(":");
					var end = this.events[el.id].getData("dtend");
					var endTimes = end.split("T")[1].split(":");
				}

            if (start && end) // don't do anything if we can't find the event details 
            {
               var startHours = startTimes[0];
               var endHours = endTimes[0];
               
               var startMinutes = startTimes[1];
               var endMinutes = endTimes[1];
               
               var displayDate = this.options.startDate
               
               if (this.calendarView === Alfresco.CalendarView.VIEWTYPE_WEEK) 
               {
                  // get current column date.
                  var parentTd = Dom.getAncestorByTagName(el, "td");
                  displayDate = Alfresco.util.fromISO8601(parentTd.id.replace("cal-", ""));
               }
               
               // if all times are 00:00 then it's an all day event
               var isAllDay = (parseInt(startHours + startMinutes + endHours + endMinutes, 10) == 0) ? true : false;
               
               if (!Alfresco.CalendarHelper.isSameDay(start, displayDate) && !isAllDay) 
               {
                  // Set the duration to assume a midnight start time (00:00)
                  startHours = 0;
                  startMinutes = 0;
                  this._addContinuedFromIcon(el);
               }
               
               if (!Alfresco.CalendarHelper.isSameDay(end, displayDate) && !isAllDay) 
               {
                  // Set the duration to assume a midnight end time (23:59)
                  endHours = 23;
                  endMinutes = 59;
                  this._addContinuedToIcon(el);
               }
               
               var hours = endHours - startHours;
               var minutes = endMinutes - startMinutes;
               
               var height = (hourHeight * hours);
               
               if (minutes !== 0) 
               {
                  height += (hourHeight * (1 / (60 / minutes)));
               }
               
               if (el && height) 
               {
                  Dom.setStyle(el, 'height', height + 'em');
               }
            }
         }
      },
      
      /** Adds the symbol used to show that the event continues from a previous day
       * @method _addContinuedFromIcon
       * @param {Object} el
       */
      _addContinuedFromIcon: function CalendarView_addContinuedFlag(el)
      {
         var startEl = Dom.getElementsByClassName("dtstart", "span", el);
         iconEl = document.createElement("span");
         iconEl.innerHTML = "&lt;"
         iconEl.className = "continuedFrom"
         Dom.insertBefore(iconEl, startEl[0]);
         Dom.setStyle(startEl[0], "display", "none")
      },
      
      /**
       * Adds the symbol used to show that the event continues on the next day
       *
       * @method _addContinuedToIcon
       * @param {Object} el
       */
      _addContinuedToIcon: function CalendarView_addContinuedFlag(el)
      {
         var endEl = Dom.getElementsByClassName("dtend", "span", el);
         iconEl = document.createElement("span");
         iconEl.innerHTML = "&gt;"
         iconEl.className = "continuedTo"
         Dom.insertBefore(iconEl, endEl[0]);
         Dom.setStyle(endEl[0], "display", "none")
      },
      
      /**
       * Displays add dialog
       *
       * @method showAddDialog
       * @param elTarget {object} Element in which the event occured (click)
       *
       */
      showAddDialog: function CalendarView_showAddDialog(elTarget)
      {
         var displayDate;
         // if from toolbar add event
         if (YAHOO.lang.isUndefined(elTarget)) 
         {
            if (this.calendarView === Alfresco.CalendarView.VIEWTYPE_MONTH) 
            {
               elTarget = Dom.get('cal-' + toISO8601(this.options.startDate).split('T')[0]);
            }
            else if (this.calendarView !== Alfresco.CalendarView.VIEWTYPE_AGENDA) 
            {
               elTarget = Dom.get('cal-' + toISO8601(this.options.startDate).split(':')[0] + ':00');
            }
            this.currentDate = displayDate = (Alfresco.util.getQueryStringParameter('date')) ? fromISO8601(Alfresco.util.getQueryStringParameter('date')) : new Date();
         }
         else 
         {
            // from cell
            this.currentDate = displayDate = this.getClickedDate(elTarget);
         }
         
         if (!this.eventDialog) 
         {
            this.eventDialog = Alfresco.util.DialogManager.getDialog('CalendarView.addEvent');
            this.eventDialog.id = this.id + "-editEvent";
            if (this.eventDialog.tagLibrary == undefined) 
            {
               // If there is an existing TagLibrary component on the page, use that, otherwise create a new one.
               var existingTagLibComponent = Alfresco.util.ComponentManager.find({name: "Alfresco.module.TagLibrary"});
               if (existingTagLibComponent.length > 0) 
               {
                  this.eventDialog.tagLibrary = existingTagLibComponent[0];
               }
               else 
               {
                  this.eventDialog.tagLibrary = new Alfresco.module.TagLibrary(this.eventDialog.id);
                  this.eventDialog.tagLibrary.setOptions(
                  {
                     siteId: this.options.siteId
                  });
               }
            }
         }
         var options = 
         {
            site: this.options.siteId,
            displayDate: displayDate,
            actionUrl: Alfresco.constants.PROXY_URI + "calendar/create",
            templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "components/calendar/add-event",
            templateRequestParams: 
            {
               site: this.options.siteId
            },
            doBeforeFormSubmit: 
            {
               fn: function(form, obj)
               {
                  // Update the tags set in the form
                  this.tagLibrary.updateForm(this.id + "-form", "tags");
                  // Avoid submitting the input field used for entering tags
                  var tagInputElem = YAHOO.util.Dom.get(this.id + "-tag-input-field");
                  if (tagInputElem) 
                  {
                     tagInputElem.disabled = true;
                  }
                  var errorEls = YAHOO.util.Dom.getElementsByClassName('error',null,YAHOO.util.Dom.get(this.id + "-form"));

                  for (var i = 0; i <errorEls.length;i++)
                  {
                     YAHOO.util.Dom.removeClass(errorEls[i],'error');
                  }
                  Dom.get(this.id+'-title').disabled = false;
                  Dom.get(this.id+'-location').disabled = false; 
               },
               scope: this.eventDialog
            },
            doBeforeAjaxRequest: 
            {
               fn: function(p_config, p_obj)
               {
                  var isAllDay = document.getElementsByName('allday').checked === true;
                  var startEl = document.getElementsByName('start')[0];
                  var endEl = document.getElementsByName('end')[0];
                  
                  if (p_config.dataObj.tags) 
                  {
                     p_config.dataObj.tags = p_config.dataObj.tags.join(' ');
                  }
                  
                  if (YAHOO.lang.isUndefined(p_config.dataObj.start)) 
                  {
                     p_config.dataObj.start = startEl.value;
                     p_config.dataObj.end = endEl.value;
                  }
                  
                  // if times start and end at 00:00 and not allday then add 1
                  // hour
                  if (!isAllDay && (p_config.dataObj.start == '00:00' && p_config.dataObj.end == '00:00')) 
                  {
                     p_config.dataObj.end = '01:00';
                  }
                  
                  return true ;
               },
               scope: this.eventDialog
            },
            onSuccess: 
            {
               fn: this.onEventSaved,
               scope: this
            },
            onFailure: 
            {
               fn: function()
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: Alfresco.util.message('message.created.failure', this.name)
                  });
               },
               scope: this
            }
         };
         this.eventDialog.setOptions(options);
         this.eventDialog.show();
      },
      
      /**
       * shows edits or add dialog depending on source of event
       *
       * @method showDialog
       * @param e {object} Event object
       * @param elTarget {object} Element in which event occured
       *
       */
      showDialog: function(e, elTarget)
      {
         var event = this.getEventObj(elTarget);
         // Set up the dialog box
         this.setUpDialog(e, elTarget, event);
         
         // if the edit window isn't already showing, show it!
         if (!this.eventInfoPanel.isShowing) 
         {
            this.eventInfoPanel.show(event);            
         }
         
         Event.preventDefault(e);
      },
      
      /**
       * Uses the EventInfo delete method to delete the event after showing a confirmation dialogue.
       * 
       * @method deleteDialog
       * 
       * @param {Object} e
       * @param {Object} elTarget
       */
      deleteDialog: function(e, elTarget)
      {
         var event = this.getEventObj(elTarget);
         // Set up the dialog box
         this.setUpDialog(e, elTarget, event);
         
         //call delete function
         this.eventInfoPanel.onDeleteClick();
         
         Event.preventDefault(e);
      },
      
      /**
       * Uses the EventInfo edit method to jump straight to the event edit screen.
       * 
       * @method editDialog
       * 
       * @param {Object} e
       * @param {Object} elTarget
       */
      editDialog: function(e, elTarget)
      {
         var event = this.getEventObj(elTarget);
         // Set up the dialog box
         this.setUpDialog(e, elTarget, event);
         
         //call edit function
         this.eventInfoPanel.onEditClick();
         
         Event.preventDefault(e);
      },
      
      /**
       * Does the grunt work of setting up the dialogue box for info, edit and delete methods.
       * 
       * @method setUpDialog
       * 
       * @param {Object} e
       * @param {Object} elTarget
       */
      setUpDialog: function(e, elTarget, event) 
      {
         var div = document.createElement('div');
         
         div.id = 'eventInfoPanel';
         document.body.appendChild(div);
         this.eventInfoPanel = new Alfresco.EventInfo(this.id + "");
         this.eventInfoPanel.event = event;
         
         if (!this.eventInfoPanel.isShowing) 
         {
            this.eventInfoPanel.setOptions(
            {
               siteId: this.options.siteId,
               eventUri: event.uri.substring(1,event.uri.length), // strip off leading '/'
               displayDate: this.currentDate,
               event: event,
               permitToEditEvents: this.options.permitToCreateEvents
            });
         }         
      },
      
      /**
       * Updates event to database
       *
       * @method updateEvent
       * @param calEvent {object} The CalendarEvent object to update
       */
      updateEvent: function CalendarView_updateEvent(calEvent)
      {
         var eventUri = Dom.getElementsByClassName('summary', 'a', calEvent.getEl())[0].href;
         var dts = fromISO8601(calEvent.getData('dtstart'));
         var dte = fromISO8601(calEvent.getData('dtend', false));
         // IE's slowness sometimes means that dtend is incorrectly parsed when an
         // event is quickly resized.
         // so we must add a recheck. Interim fix.
         if (YAHOO.lang.isNull(dte)) 
         {
            var dtendData = YAHOO.util.Dom.getElementsByClassName('dtend', 'span', calEvent.getElement())[0];
            var endTime = dtendData.innerHTML.split(':');
            dte = new Date();
            dte.setTime(dts.getTime());
            dte.setHours(endTime[0]);
            dte.setMinutes(endTime[1]);
            calEvent.update(
            {
               dtend: toISO8601(dte)
            });
         }
         var dataObj = 
         {
            "site": this.options.siteId,
            "page": "calendar",
            "from": dateFormat(dts, 'yyyy/mm/dd'),
            "to": dateFormat(dte, 'yyyy/mm/dd'),
            "what": calEvent.getData('summary'),
            "where": calEvent.getData('location'),
            "desc": YAHOO.lang.isNull(calEvent.getData('description')) ? '' : calEvent.getData('description'),
            "fromdate": dateFormat(dts, "dddd, d mmmm yyyy"),
            "start": calEvent.getData('dtstart').split('T')[1].substring(0, 5),
            "todate": dateFormat(dte, "dddd, d mmmm yyyy"),
            "end": calEvent.getData('dtend').split('T')[1].substring(0, 5),
            'tags': calEvent.getData('category'),
            'docfolder': '*NOT_CHANGE*'
         };
         
         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.PUT,
            url: Alfresco.constants.PROXY_URI + 'calendar/' + eventUri.split('/calendar/')[1] + '&page=calendar',
            dataObj: dataObj,
            requestContentType: "application/json",
            responseContentType: "application/json",
            successCallback: 
            {
               fn: function()
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: Alfresco.util.message('message.edited.success', this.name)
                  });
                  
               },
               scope: this
            },
            failureMessage: Alfresco.util.message('message.edited.failure', 'Alfresco.CalendarView')
         });
      },
      
      /**
       * Tests if event is valid for view must be within startdate and (enddate-1 second) of current view
       *
       * @method date {object} Date to validate
       *
       * @return true|false
       *
       */
      isValidDateForView: function(date)
      {
         return (date.getTime() >= this.options.startDate.getTime()) && (date.getTime() < this.options.endDate.getTime());
      },
      
      // HANDLERS
      
      /**
       * Handler for cancelling dialog
       *
       * @method onCancelDialog
       *
       */
      onCancelDialog: function CalendarView_onCancelDialog()
      {
         this.eventDialog.hide();
      },
      
      /**
       * Updates date field in dialog when date in selected in popup calendar
       *
       * @method onDateSelected
       * @param e {object} Event object
       * @param args {object} Event argument object
       */
      onDateSelected: function CalendarView_onDateSelected(e, args, context)
      {
         if (this.currPopUpCalContext) 
         {
            // ugly
            for (var i = 1; i < args[0][0].length; i++) 
            {
               args[0][0][i] = Alfresco.CalendarHelper.padZeros(args[0][0][i]);
            }
            Dom.get(this.currPopUpCalContext).value = args[0][0].join('-');
            // add one hour as default
            if (this.currPopUpCalContext === 'dtend') 
            {
               Dom.get(this.currPopUpCalContext + 'time').value = YAHOO.widget.DateMath.add(fromISO8601(Dom.get('dtstart').value + 'T' + Dom.get('dtstarttime').value), YAHOO.widget.DateMath.HOUR, 1).format(dateFormat.masks.isoTime);
               
            }
         }
      },
      // HANDLERS
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function CalendarView_onReady()
      {
         this.calendarView = this.options.view;
         
         this.calendarView = this.options.view;
         this.startDate = (YAHOO.lang.isString(this.options.startDate)) ? fromISO8601(this.options.startDate) : this.options.startDate;
         this.container = Dom.get(this.id);
         this.containerRegion = Dom.getRegion(this.container);
         this.isShowingEarlyRows = true;
         this.titleEl = Dom.get('calTitle');
         
         // Patch YAHOO.widget.DateMath to support Hours, mins and seconds
         if (!YAHOO.widget.DateMath.HOUR) 
         {
            YAHOO.widget.DateMath.add = function()
            {
               var origAddFunc = YAHOO.widget.DateMath.add;
               YAHOO.widget.DateMath.HOUR = 'H';
               YAHOO.widget.DateMath.SECOND = 'S';
               YAHOO.widget.DateMath.MINUTE = 'Mn';
               return function(date, field, amount)
               {
               
                  switch (field)
                  {
                     case YAHOO.widget.DateMath.MONTH:
                     case YAHOO.widget.DateMath.DAY:
                     case YAHOO.widget.DateMath.YEAR:
                     case YAHOO.widget.DateMath.WEEK:
                        return origAddFunc.apply(YAHOO.widget.DateMath, arguments);
                        break;
                     case YAHOO.widget.DateMath.HOUR:
                        var newHour = date.getHours() + amount;
                        var day = 0;
                        if (newHour < 0) 
                        {
                           while (newHour < 0) 
                           {
                              newHour += 24;
                              day -= 1;
                              
                           }
                        // newHour = 23;
                        }
                        if (newHour > 24) 
                        {
                           while (newHour > 24) 
                           {
                              newHour -= 24;
                              day += 1;
                              
                           }
                        }
                        YAHOO.widget.DateMath._addDays(date, day);
                        date.setHours(newHour);
                        break;
                     case YAHOO.widget.DateMath.MINUTE:
                        date.setMinutes(date.getMinutes() + amount);
                        break;
                     case YAHOO.widget.DateMath.SECOND:
                        date.setMinutes(date.getSeconds() + amount);
                        
                  }
                  return date;
               };
            }();
         }
         
         this.render();
      },
      
      /**
       * Event Delegation handler. Delegates to correct handlers using CSS selectors
       *
       * @method onInteractionEvent
       * @param e {object} DomEvent
       * @param args {array} event arguments
       */
      onInteractionEvent: function CalendarView_onInteractionEvent(o, args)
      {
         // TODO: refactor this if/else list into an event trigger with listeners.
         
         var elTarget, e;
         // if loop added for DataTable event trigger which passes event and target as single object in 1st param
         if (typeof(o.event) === "object" && typeof(o.target) === "object") 
         {
            e = o.event;
            elTarget = o.target;
         }
         else //old style (non DataTable trigger), event as first object, target not included.
         {
            e = o;
            elTarget = Event.getTarget(e);
         }
         
         // Check for event type.
         // repeated if loops are now a series of else if loops to prevent all selectors being attempted. Matching of multiple selectors is not recommended.
         if (e.type === 'mouseover') 
         {
            if (YAHOO.util.Selector.test(elTarget, 'div.' + this.dragGroup)) 
            {
               Dom.addClass(elTarget, 'highlight');
               if (this.options.permitToCreateEvents === 'true') 
               {
                  if (!Dom.hasClass(elTarget, 'disabled')) 
                  {
                     elTarget.appendChild(this.addButton);
                  }
               }
            }
         }
         else if (e.type === 'mouseout') 
         {
            if (YAHOO.util.Selector.test(elTarget, 'div.' + this.dragGroup)) 
            {
               Dom.addClass(elTarget, 'highlight');
            }
         }
         else if (e.type === 'click') 
         {
            // Show or hide wee hours?
            if (YAHOO.util.Selector.test(elTarget, 'a#collapseTriggerLink')) 
            {
               this.toggleEarlyTableRows();
               Event.preventDefault(e);
            }
            // are we adding a new event?
            else if (YAHOO.util.Selector.test(elTarget, 'button#addEventButton') || YAHOO.util.Selector.test(elTarget, 'a.addEvent')) 
            {
               this.showAddDialog(elTarget);
               Event.preventDefault(e);
            }
            // a.summary = a click on the event title. Therefore into Event Info mode.
            else if (YAHOO.util.Selector.test(elTarget, 'a.summary') || YAHOO.util.Selector.test(elTarget, 'div.yui-dt-liner') ) 
            {
               this.showDialog(e, elTarget);
            }
            // Someone clicked the 'show more events in Month View' link.
            else if (YAHOO.util.Selector.test(elTarget, 'li.moreEvents a')) 
            {
               this.onShowMore(e, args, elTarget);
            }
            //Agenda View show more
            else if (YAHOO.util.Selector.test(elTarget, 'a.showMore'))
            {
               this.expandDescription(elTarget);
               Event.preventDefault(e);
            }
            else if (YAHOO.util.Selector.test(elTarget, 'a.showLess'))
            {
               this.collapseDescription(elTarget);
               Event.preventDefault(e);
            }
            // Delete this event link in Agenda DataTable
            else if (YAHOO.util.Selector.test(elTarget, "a.deleteAction"))
            {
               this.deleteDialog(e, elTarget);
            }
            // Edit event link in Agenda DataTable.
            else if (YAHOO.util.Selector.test(elTarget, "a.editAction"))
            {
               this.editDialog(e, elTarget);
            }
         }
      },
      
      /**
       * Handler for when today button is clicked
       *
       * @method onTodayNav
       *
       */
      onTodayNav: function CalendarView_onTodayNav()
      {
         var today = new Date();
         var params = Alfresco.util.getQueryStringParameters();
         params.date = today.getFullYear() + '-' + Alfresco.CalendarHelper.padZeros((~ ~ (1 * (today.getMonth()))) + 1) + '-' + Alfresco.CalendarHelper.padZeros(today.getDate());
         window.location = window.location.href.split('?')[0] + Alfresco.util.toQueryString(params);
      },
      
      /**
       * Handler for when calendar view is changed (day|week|month button is clicked)
       *
       * @method onViewChanged
       *
       */
      onViewChanged: function CalendarView_onViewChanged()
      {
         var views = [Alfresco.CalendarView.VIEWTYPE_DAY, Alfresco.CalendarView.VIEWTYPE_WEEK, Alfresco.CalendarView.VIEWTYPE_MONTH, Alfresco.CalendarView.VIEWTYPE_AGENDA];
         var params = Alfresco.util.getQueryStringParameters();
         params.view = views[arguments[1][1].activeView];
         window.location = window.location.href.split('?')[0] + Alfresco.util.toQueryString(params);
      },
      
      /**
       * Handler for when previous or next button is clicked
       *
       * @method onNav
       * @param e {object}
       *
       */
      onNav: function CalendarView_onNav(e)
      {
         var increment = 1;
         if (e === 'prevNav') 
         {
            increment = -1;
         }
         var date = YAHOO.widget.DateMath.add(this.options.startDate, YAHOO.widget.DateMath[this.calendarView.toUpperCase()], increment);
         var params = Alfresco.util.getQueryStringParameters();
         params.date = dateFormat(date, 'yyyy-mm-dd');
         var newLoc = window.location.href.split('?')[0] + Alfresco.util.toQueryString(params);
         window.location = newLoc;
      },
      /**
       * Handler for when date mini calendar is selected
       *
       * @method onNav
       * @param e {object}
       *
       */
      onCalSelect: function CalendarView_onCalSelect(e, args)
      {
         var date = args[1].date;
         var params = Alfresco.util.getQueryStringParameters();
         params.date = dateFormat(date, 'yyyy-mm-dd');
         var newLoc = window.location.href.split('?')[0] + Alfresco.util.toQueryString(params);
         window.location = newLoc;
      },
      /**
       * Handler for when a tag is selected
       *
       * @method onTagSelected
       *
       */
      onTagSelected: function CalendarView_onTagSelected(e, args)
      {
         var tagName = arguments[1][1].tagname,
            showAllTags = false;
         
         // all tags
         if (tagName == Alfresco.util.message('label.all-tags', 'Alfresco.TagComponent')) 
         {
            this.options.tag = null;
         }
         else 
         {
            this.options.tag = tagName;
         }
         this.updateTitle();
         this.getEvents();
      },
      
      /**
       *
       * @method onFormValidationError
       *
       * @param e {object} Event object
       * @param args {object} Value object referencing elements that are invalid
       */
      onFormValidationError: function CalendarView_onFormValidationError(e, args)
      {
         var args = args[1];
         Alfresco.util.PopupManager.displayMessage(
         {
            text: args.msg
         });
      },
      
      onAfterEventSaved: function CalendarView_onAfterEventSaved(e, args)
      {
         // Refresh the tag component
         this.refreshTags();
         
         // Confirm success to the user
         this.displayMessage('message.created.success', this.name);
      },
      
      onAfterEventDeleted: function CalendarView_onAfterEventDeleted(e, args)
      {
         this.refreshTags();
         this.displayMessage('message.deleted.success', this.name);
      },
      
      onAfterEventEdited: function CalendarView_onAfterEventDeleted(e, args)
      {
         // Refresh the tag component
         this.refreshTags();
      },
      
      refreshTags: function CalendarView_refreshTags()
      {
         YAHOO.lang.later(500, YAHOO.Bubbling, 'fire', 'tagRefresh');
      },
      
      updateTitle: function CalendarView_updateTitle()
      {
         
         var startDate = this.options.startDate,
            endDate = this.options.endDate,
            startDateString = "",
            withYear = this.msg("date-format.longDate"),
            noYear = this.msg("date-format.longDateNoYear"),
            endDateString = dateFormat(endDate, withYear);
         
         // convert date objects to strings
         // only show year in start date if it differs to end date.
         if (startDate.getFullYear() === endDate.getFullYear()) 
         {
            startDateString = dateFormat(startDate, noYear);
         } else 
         {
            startDateString = dateFormat(startDate, withYear)
         }
         
         this.titleEl.innerHTML = this.msg("title.agenda", startDateString, endDateString);
         
         // add tag info to title,
         tagTitleEl = Dom.getElementsByClassName('tagged', "span", this.titleEl);
         if (tagTitleEl.length > 1) 
         {
            this.titleEl.removeChild(tagTitleEl[0]);
         }
         if (this.options.tag) 
         {
            tagTitleEl = Alfresco.CalendarHelper.renderTemplate('taggedTitle', 
            {
               taggedWith: this.msg('label.tagged-with'),
               tag: this.options.tag
            });
            this.titleEl.appendChild(tagTitleEl);
         }
      },
      
      /**
       *
       * takes the event list and removes any items that aren't tagged with the currently selected tag.
       * 
       * @method tagFilter 
       * 
       * @param {Object} events
       */
      tagFilter: function CalendarView_tagFilter(events) 
      {
         var filteredEvents = [],
            tagName = this.options.tag;
         
         // early exit if there is no selected tagName
         if (!tagName) 
         {
            return events;
         } else 
         {
            for (var i = 0, l = events.length; i < l; i++) 
            {
               var eventTags = events[i].tags.split(" "); // TODO: Tags with spaces are not supported in the Calendar Event input field.
               if (Alfresco.util.arrayContains(eventTags, tagName)) 
               {
                  filteredEvents.push(events[i]); 
               }
            }
            return filteredEvents;
         }
      }
   });
   Alfresco.CalendarView.VIEWTYPE_WEEK = 'week';
   Alfresco.CalendarView.VIEWTYPE_MONTH = 'month';
   Alfresco.CalendarView.VIEWTYPE_DAY = 'day';
   Alfresco.CalendarView.VIEWTYPE_AGENDA = 'agenda';
})();

/**
 * Alfresco.CalendarHelper. Helper object consisting of useful helper methods
 *
 * @constructor
 */
Alfresco.CalendarHelper = (function Alfresco_CalendarHelper()
{
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Sel = YAHOO.util.Selector,
       fromISO8601 = Alfresco.util.fromISO8601,
       toISO8601 = Alfresco.util.toISO8601,
       dateFormat = Alfresco.thirdparty.dateFormat;
   var templates = [];
   
   return {
      /**
       * Calculates end date depending on specified duration, in ISO8601 format
       *
       * @method getEndDate
       * @param dateISO {String} startDate in ISO8601 format
       * @param duration {object} Duration object
       */
      getEndDate: function Alfresco_CalendarHelper_getEndDate(dateISO, duration)
      {
         var newDate = Alfresco.util.fromISO8601(dateISO);
         for (var item in duration) 
         {
            newDate = YAHOO.widget.DateMath.add(newDate, (item === 'M') ? YAHOO.widget.DateMath.MINUTE : item, (~ ~ (1 * duration[item])));
         }
         return Alfresco.util.toISO8601(newDate).split('+')[0];// newDate.toISO8601String(5);
      },
      
      /**
       * Correctly determines which hour segment the event element is in. Returns the hour
       *
       * @method determineHourSegment
       * @param ePos {object} Object containing XY position of element to test
       * @param el {object} Event element
       * @return {string} Hour
       */
      determineHourSegment: function Alfresco_CalendarHelper_determineHourSegment(ePos, el)
      {
         var r = Dom.getRegion(el);
         var y = ePos[1];
         var threshold = Math.round((r.bottom - r.top) / 2);
         var inFirstHalfHour = (!Dom.getPreviousSibling(el)); // first half of
         // hour
         
         var hour = Dom.getAncestorByTagName(el, 'tr').getElementsByTagName('h2')[0].innerHTML;
         if (inFirstHalfHour === true) 
         {
            hour = (y - r.top < threshold) ? hour : hour.replace(':00', ':15');
            
         }
         else 
         {
            hour = (y - r.top < threshold) ? hour.replace(':00', ':30') : hour.replace(':00', ':45');
            
         }
         return hour;
      },
      
      /**
       * calculates duration based on specified start and end dates
       *
       *
       * @method getDuration
       * @param dtStartDate {Date} start date
       * @param dtEndDate {Date} end date
       * @return {String} Duration in ical format eg PT2H15M
       */
      getDuration: function Alfresco_CalendarHelper_getDuration(dtStartDate, dtEndDate)
      {
         var diff = dtEndDate.getTime() - dtStartDate.getTime();
         var dateDiff = {};
         var duration = 'P';
         var diff = new Date();
         diff.setTime(Math.abs(dtEndDate.getTime() - dtStartDate.getTime()));
         var timediff = diff.getTime();
         
         dateDiff[YAHOO.widget.DateMath.WEEK] = Math.floor(timediff / (1000 * 60 * 60 * 24 * 7));
         timediff -= dateDiff[YAHOO.widget.DateMath.WEEK] * (1000 * 60 * 60 * 24 * 7);
         
         dateDiff[YAHOO.widget.DateMath.DAY] = (Math.floor(timediff / (1000 * 60 * 60 * 24)));
         timediff -= dateDiff[YAHOO.widget.DateMath.DAY] * (1000 * 60 * 60 * 24);
         
         dateDiff[YAHOO.widget.DateMath.HOUR] = Math.floor(timediff / (1000 * 60 * 60));
         timediff -= dateDiff[YAHOO.widget.DateMath.HOUR] * (1000 * 60 * 60);
         
         dateDiff[YAHOO.widget.DateMath.MINUTE] = Math.floor(timediff / (1000 * 60));
         timediff -= dateDiff[YAHOO.widget.DateMath.MINUTE] * (1000 * 60);
         
         dateDiff[YAHOO.widget.DateMath.SECOND] = Math.floor(timediff / 1000);
         timediff -= dateDiff[YAHOO.widget.DateMath.SECOND] * 1000;
         
         if (dateDiff[YAHOO.widget.DateMath.WEEK] > 0) 
         {
            duration += dateDiff[YAHOO.widget.DateMath.WEEK] + YAHOO.widget.DateMath.WEEK;
         }
         if (dateDiff[YAHOO.widget.DateMath.DAY] > 0) 
         {
            duration += dateDiff[YAHOO.widget.DateMath.DAY] + YAHOO.widget.DateMath.DAY;
         }
         duration += 'T';
         if (dateDiff[YAHOO.widget.DateMath.HOUR] > 0) 
         {
            duration += dateDiff[YAHOO.widget.DateMath.HOUR] + YAHOO.widget.DateMath.HOUR;
         }
         if (dateDiff[YAHOO.widget.DateMath.MINUTE] > 0) 
         {
            duration += dateDiff[YAHOO.widget.DateMath.MINUTE] + 'M';
         }
         if (dateDiff[YAHOO.widget.DateMath.SECOND] > 0) 
         {
            duration += dateDiff[YAHOO.widget.DateMath.SECOND] + YAHOO.widget.DateMath.SECOND;
         }
         return duration;
      },
      
      /**
       * Pads specified value with zeros if value is less than 10
       *
       * @method padZeros
       *
       * @param value {Object} value to pad
       * @return {String} padded value
       */
      padZeros: function Alfresco_CalendarHelper_padZeros(value)
      {
         return (value < 10) ? '0' + value : value;
      },
      
      /**
       * Converts a date string in the input field to a date object.
       *
       * @method getDateFromField
       *
       * @param field {DOM Object} input element
       * @return d {Date}
       */
      getDateFromField: function Alfresco_CalendarHelper_getDateFromField(field)
      {  
         var dateString = field.title;
         var d = (dateString !== "") ? fromISO8601(dateString) : new Date();
         return d;
      },
      
      /**
       * Formats the date
       * 
       * @param field {DOM Object} DOM object of element
       */
      writeDateToField: function Alfresco_CalendarHelper_writeDateToField(date, field)
      {
         var formattedDate = dateFormat(date, Alfresco.util.message("calendar.dateFormat.full"));
         field.value = formattedDate;
         field.title = toISO8601(date);
      },
      
      /**
       * Add an template using specified name as a reference
       */
      addTemplate: function Alfresco_CalendarHelper_addTemplate(name, template)
      {
         templates[name] = template;
      },
      
      /**
       * Retreives specified template
       *
       * @method getTemplate
       * @param name {string} Name of template to retrieve
       * @return {string} template
       */
      getTemplate: function Alfresco_CalendarHelper_getTemplate(name)
      {
         return templates[name];
      },
      
      /**
       * renders template as a DOM HTML element. Element is *not* added to document
       *
       * @param name Name of template to render
       * @param data Data to render template against
       * @return HTMLElement Newly created div
       */
      renderTemplate: function Alfresco_CalendarHelper_renderTemplate(name, data)
      {
         var el = document.createElement('div');
         if (templates[name] && el) 
         {
            var el = YAHOO.lang.isString(el) ? Dom.get(el) : el;
            var template = templates[name];
            var div = document.createElement('div');
            if (data) 
            {
               template = YAHOO.lang.substitute(template, data);
            }
            
            div.innerHTML = template;
            el.appendChild(div.firstChild);
            
            return el.lastChild;
         }
      },
      
      /**
       * Checks whether start date is earlier than end date.
       *
       * @method isValidDate
       * @param {Date} dtStartDate Start date
       * @param {Date} dtEndDate End date
       *
       * @return {Boolean} flag denoting whether date is valid or not.
       */
      isValidDate: function Alfresco_CalendarHelper_isValidDate(dtStartDate, dtEndDate)
      {
         return dtStartDate.getTime() < dtEndDate.getTime();
      },
      
      /**
       * Checks to see if the two dates are the same
       *
       * @method isSameDay
       * @param {Date|string} dateOne (either JS Date Object or ISO8601 date string)
       * @param {Date|string} dateTwo
       *
       * @return {Boolean} flag indicating if the dates are the same or not
       */
      isSameDay: function Alfresco_CalendarHelper_isSameDay(dateOne, dateTwo)
      {
         if (typeof(dateOne) === "string") 
         {
            dateOne = fromISO8601(dateOne);
         }
         if (typeof(dateTwo) === "string") 
         {
            dateTwo = fromISO8601(dateTwo);
         }
         return (dateOne.getDate() === dateTwo.getDate() && dateOne.getMonth() === dateTwo.getMonth() && dateOne.getFullYear() === dateTwo.getFullYear());
      },

      
      /**
       * Checks to see if dateOne is earlier than dateTwo or not
       *
       * @method isBefore
       * @param {Date|string} dateOne (either JS Date Object or ISO8601 date string)
       * @param {Date|string} dateTwo
       *
       * @return {Boolean}
       */      
		isBefore: function Alfresco_CalendarHelper_isBefore(dateOne, dateTwo) 
		{
			
         if (typeof(dateOne) === "string") 
         {
            dateOne = fromISO8601(dateOne);
         }
         if (typeof(dateTwo) === "string") 
         {
            dateTwo = fromISO8601(dateTwo);
         }
			
			return (dateOne < dateTwo);
			
		},
		
      /**
       * @method isAllDay
       * @param {Object} event data object
       *
       * @return {Boolean} flag indicating whether event is a timed event or not
       */
      isAllDay: function Alfresco_CalendarHelper_isTimedEvent(eventData)
      {
         var isSameDay = this.isSameDay(eventData.from, eventData.to);
         var isMidnight = (eventData.end == eventData.start && "00:00") ? true : false;
         return (!isSameDay && isMidnight);
      }
   };
})();

Alfresco.CalendarHelper.addTemplate('vevent', '<{el} class="vevent {allday} {hidden} {isoutlook} theme-bg-color-1 theme-border-2"> ' +
'<{contEl}>' +
'<p class="dates">' +
'<span class="dtstart" title="{from}">{start}</span> - ' +
'<span class="dtend" title="{to}">{end}</span>' +
'</p>' +
'<p class="description">{desc}</p>' +
'<a class="summary theme-color-1" href="{uri}">{name}</a>' +
'<span class="location">{where}</span>' +
'<span class="duration" title="{duration}">{duration}</span>' +
'<span class="category">{tags}</span>' +
'</{contEl}>' +
'</{el}>');
Alfresco.CalendarHelper.addTemplate('agendaDay', '<h2>{date}</h2>');

Alfresco.CalendarHelper.addTemplate('agendaDayItem', '<li class="vevent"><span>{start} - {end}</span>' +
'<a href="{uri}" class="summary">{name}</a></li>');

Alfresco.CalendarHelper.addTemplate('createEventButton', '<button id="addEventButton"><img src="{addEventUrl}" alt="{addEvent}" /></button>');
Alfresco.CalendarHelper.addTemplate('taggedTitle', "<span class=\"tagged\">{taggedWith} <span class=\"theme-color-2\">'{tag}'</span></span>");

/**
 * Alfresco.util.DialogManager. Helper object to manage dialogs.
 *
 * @constructor
 */
Alfresco.util.DialogManager = (function()
{
  var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Sel = YAHOO.util.Selector,
      fromISO8601 = Alfresco.util.fromISO8601,
      toISO8601 = Alfresco.util.toISO8601,
      dateFormat = Alfresco.thirdparty.dateFormat;
   
   var dialogs = [];
   
   var dialogConfig = 
   {
      width: "42em",
      displayDate: null,
      doBeforeDialogShow: 
      {
         fn: function doBeforeDialogShow(form)
         {
            
            // Ensure form fields are blank / default.
            // The eventDialog should get recreated everytime, but
            // it doesn't always get reset if the user clicks cancel.
            Dom.get(this.id + "-title").value = "";
            Dom.get(this.id + "-location").value = "";
            Dom.get(this.id + "-description").value = "";
            
            // Default Dates:
            var date = new Date();
            // Pretty formatting
            Alfresco.CalendarHelper.writeDateToField(this.options.displayDate, Dom.get("fd"));
            Alfresco.CalendarHelper.writeDateToField(this.options.displayDate, Dom.get("td"));
            Dom.get(this.id + "-from").value = Dom.get(this.id + "-to").value = dateFormat(this.options.displayDate, 'yyyy/mm/dd');
            Dom.get(this.id + "-tag-input-field").disabled = false;
            form.errorContainer = null;
            // disable time fields if all day event
            document.getElementsByName('start')[0].disabled = document.getElementsByName('end')[0].disabled = document.getElementsByName('allday')[0].checked;
            // hide mini-cal
            this.dialog.hideEvent.subscribe(function()
            {
               var oCal = Alfresco.util.ComponentManager.findFirst('Alfresco.CalendarView');
               if (oCal && oCal.oCalendar) 
               {
                  oCal.oCalendar.hide();
               }
            }, this, true);
         },
         scope: Alfresco.util.ComponentManager.findFirst('Alfresco.CalendarView')
      },
      doSetupFormsValidation: 
      {
         fn: function doSetupFormsValidation(form)
         {
            var cal = Alfresco.util.ComponentManager.findFirst('Alfresco.CalendarView');
            
            // validate text fields
            var validateTextRegExp = 
            {
               pattern: /({|})/,
               match: false
            };
            var textElements = [this.id + "-title", this.id + "-location", this.id + "-description"];
            form.addValidation(textElements[0], Alfresco.forms.validation.mandatory, null, "blur");
            form.addValidation(textElements[0], Alfresco.forms.validation.mandatory, null, "keyup");
            
            for (var i = 0; i < textElements.length; i++) 
            {
               form.addValidation(textElements[i], Alfresco.forms.validation.regexMatch, validateTextRegExp, "blur");
               form.addValidation(textElements[i], Alfresco.forms.validation.regexMatch, validateTextRegExp, "keyup");
            }
            // validate time fields
            var validateTimeRegExp = 
            {
               pattern: /^\d{1,2}:\d{2}/,
               match: true
            };
            var timeElements = [this.id + "-start", this.id + "-end"];
            for (var i = 0; i < timeElements.length; i++) 
            {
               form.addValidation(timeElements[i], Alfresco.forms.validation.regexMatch, validateTimeRegExp, "blur", cal.msg('message.invalid-time'));
            }
            
            form.addValidation(this.id + "-tag-input-field", Alfresco.module.event.validation.tags, null, "keyup");
            
            this.tagLibrary.initialize(form);
            
            var dateElements = ["td", "fd", this.id + "-start", this.id + "-end"];
            for (var i = 0; i < dateElements.length; i++) 
            {
               form.addValidation(dateElements[i], this.options._onDateValidation, 
               {
                  "obj": this
               }, "blur");
            }
            
            // Setup date validation
            form.addValidation("td", this.options._onDateValidation, 
            {
               "obj": this
            }, "focus");
            form.addValidation("fd", this.options._onDateValidation, 
            {
               "obj": this
            }, "focus");
            
            form.setShowSubmitStateDynamically(true, false);
            form.setSubmitElements(this.widgets.okButton);
            
            /**
             * keyboard handler for popup calendar button. Requried as YUI
             * button's click event doesn't fire in firefox
             */
            var buttonKeypressHandler = function()
            {
               var dialogObject = Alfresco.util.DialogManager.getDialog('CalendarView.addEvent');
               return function(e)
               {
                  if (e.keyCode === YAHOO.util.KeyListener.KEY['ENTER']) 
                  {
                     dialogObject.options.onDateSelectButton.apply(this, arguments);
                     return false;
                  }
               };
            }();
            
            var browseButton = Alfresco.util.createYUIButton(this, "browse-button", function()
            {
               if (!this.browsePanel) 
               {
                  this.hide();
                  Alfresco.util.Ajax.request(
                  {
                     url: Alfresco.constants.URL_SERVICECONTEXT + "components/calendar/browse-docfolder",
                     dataObj: 
                     {
                        site: this.options.site
                     },
                     successCallback: 
                     {
                        fn: function(response)
                        {
                           var containerDiv = document.createElement("div");
                           containerDiv.innerHTML = response.serverResponse.responseText;
                           var panelDiv = Dom.getFirstChild(containerDiv);
                           this.browsePanel = Alfresco.util.createYUIPanel(panelDiv);
                           var parentDialog = this;
                           var selectedDocfolder = Dom.get(parentDialog.id + "-docfolder").value;
                           
                           Alfresco.util.createYUIButton(this.browsePanel, "ok", function()
                           {
                              if (selectedDocfolder.charAt(selectedDocfolder.length - 1) == '/') 
                              {
                                 selectedDocfolder = selectedDocfolder.substring(0, selectedDocfolder.length - 1);
                              }
                              Dom.get(parentDialog.id + "-docfolder").value = selectedDocfolder;
                              parentDialog.browsePanel.hide();
                              parentDialog.show();
                           });
                           
                           Alfresco.util.createYUIButton(this.browsePanel, "cancel", function()
                           {
                              parentDialog.browsePanel.hide();
                              parentDialog.show();
                           });
                           
                           Alfresco.util.createTwister("twister");
                           var tree = new YAHOO.widget.TreeView("treeview");
                           tree.setDynamicLoad(function(node, fnLoadComplete)
                           {
                              var nodePath = node.data.path;
                              var uri = Alfresco.constants.PROXY_URI + "slingshot/doclib/treenode/site/" + $combine(encodeURIComponent(parentDialog.options.site), encodeURIComponent("documentLibrary"), Alfresco.util.encodeURIPath(nodePath));
                              var callback = 
                              {
                                 success: function(oResponse)
                                 {
                                    var results = YAHOO.lang.JSON.parse(oResponse.responseText), item, treeNode;
                                    if (results.items) 
                                    {
                                       for (var i = 0, j = results.items.length; i < j; i++) 
                                       {
                                          item = results.items[i];
                                          item.path = $combine(nodePath, item.name);
                                          treeNode = _buildTreeNode(item, node, false);
                                          if (!item.hasChildren) 
                                          {
                                             treeNode.isLeaf = true;
                                          }
                                       }
                                    }
                                    oResponse.argument.fnLoadComplete();
                                 },
                                 
                                 failure: function(oResponse)
                                 {
                                    Alfresco.logger.error("", oResponse);
                                 },
                                 
                                 argument: 
                                 {
                                    "node": node,
                                    "fnLoadComplete": fnLoadComplete
                                 },
                                 
                                 scope: this
                              };
                              
                              YAHOO.util.Connect.asyncRequest('GET', uri, callback);
                           });
                           
                           tree.subscribe("clickEvent", function(args)
                           {
                              selectedDocfolder = "documentLibrary" + args.node.data.path;
                           });
                           
                           tree.subscribe("collapseComplete", function(node)
                           {
                              selectedDocfolder = "documentLibrary" + node.data.path;
                           });
                           
                           var tempNode = _buildTreeNode(
                           {
                              name: "documentLibrary",
                              path: "/",
                              nodeRef: ""
                           }, tree.getRoot(), false);
                           
                           tree.render();
                           this.browsePanel.setFirstLastFocusable();
                           this.browsePanel.show();
                        },
                        scope: this
                     },
                     failureMessage: "Could not load dialog template from '" + Alfresco.constants.URL_SERVICECONTEXT + "components/calendar/browse-docfolder" + "'.",
                     scope: this,
                     execScripts: true
                  });
               }
               else 
               {
                  this.hide();
                  this.browsePanel.show();
               }
            });
            
            /**
             * Button declarations that, when clicked, display the calendar date
             * picker widget.
             */
            if (!this.startButton) 
            {
               this.startButton = new YAHOO.widget.Button(
               {
                  type: "link",
                  id: "calendarpicker",
                  label: '',
                  href: '',
                  tabindex: 6,
                  container: this.id + "-startdate"
               });
               
               this.startButton.on("click", this.options.onDateSelectButton);
               Event.on(Dom.get("fd") , "click" , this.options.onDateSelectButton , {} , this)
               this.startButton.on("keypress", buttonKeypressHandler);
            }
            if (!this.endButton) 
            {
               this.endButton = new YAHOO.widget.Button(
               {
                  type: "link",
                  id: "calendarendpicker",
                  label: '',
                  href: 'test',
                  tabindex: 8,
                  container: this.id + "-enddate"
               });
               
               this.endButton.on("click", this.options.onDateSelectButton);
               Event.on(Dom.get("td") , "click" , this.options.onDateSelectButton , {} , this)
               this.endButton.on("keypress", buttonKeypressHandler);
            }
            
            /* disable time fields if all day is selected */
            Event.addListener(document.getElementsByName('allday')[0], 'click', function(e)
            {
               if (YAHOO.util.Event.getTarget(e).checked===true) 
               {
                  // hide time boxes if they're not relevent.
                  YAHOO.util.Dom.addClass(document.getElementsByName('start')[0].parentNode, "hidden")
                  YAHOO.util.Dom.addClass(document.getElementsByName('end')[0].parentNode, "hidden")
               } else 
               {
                  // show them if they are.
                  YAHOO.util.Dom.removeClass(document.getElementsByName('start')[0].parentNode, "hidden")
                  YAHOO.util.Dom.removeClass(document.getElementsByName('end')[0].parentNode, "hidden")
               }
            });
            YAHOO.Bubbling.on('formValidationError', Alfresco.util.ComponentManager.findFirst('Alfresco.CalendarView').onFormValidationError, this);
         },
         scope: Alfresco.util.ComponentManager.findFirst('Alfresco.CalendarView')
      },
      /**
       * Event handler that gets fired when a user clicks on the date selection
       * button in the event creation form. Displays a mini YUI calendar. Gets
       * called for both the start and end date buttons.
       *
       * @method onDateSelectButton
       * @param e {object} DomEvent
       */
      onDateSelectButton: function onDateSelectButton(e)
      {
         Event.stopEvent(e);
         var o = Alfresco.util.ComponentManager.findFirst('Alfresco.CalendarView'),
            targetEl = Event.getTarget(e);
         
         o.oCalendarMenu = new YAHOO.widget.Overlay("calendarmenu", 
         {
            context: [targetEl, 'tr', 'br']
         });
         o.oCalendarMenu.setBody("&#32;");
         o.oCalendarMenu.body.id = "calendarcontainer";
         var container = (targetEl.id === "calendarendpicker-button" || targetEl.id === "calendarpicker-button")? this.get('container') : targetEl.parentNode.id ;
         
         if (YAHOO.env.ua.ie) 
         {
            o.oCalendarMenu.render(YAHOO.util.Dom.get(container).parentNode);
         }
         else 
         {
            o.oCalendarMenu.render(YAHOO.util.Dom.getAncestorByClassName(Event.getTarget(e), 'yui-panel', 'div'));
         }
         var fromDate = Alfresco.CalendarHelper.getDateFromField(YAHOO.util.Dom.get('fd'));
         var d = (container.indexOf("enddate") > -1) ? Alfresco.CalendarHelper.getDateFromField(YAHOO.util.Dom.get('td')) : fromDate ;
         
         var pagedate = Alfresco.CalendarHelper.padZeros(d.getMonth() + 1) + '/' + d.getFullYear();
         
         var startOptions = 
         {
            pagedate: pagedate,
            close: true
            
         },
         endOptions = 
         {
            pagedate: pagedate,
            close: true,
            mindate: fromISO8601(YAHOO.util.Dom.get('fd').title)
         };
         
         var options = (container.indexOf("enddate") > -1)? endOptions : startOptions;
         
         o.oCalendar = new YAHOO.widget.Calendar("buttoncalendar", o.oCalendarMenu.body.id, options);
         Alfresco.util.calI18nParams(o.oCalendar);
         o.oCalendar.render();
         o.oCalendar.selectEvent.subscribe(function(type, args)
         {
            var date;
            var Dom = YAHOO.util.Dom;
            if (args) 
            {
               var prettyId, hiddenId;
               if (container.indexOf("enddate") > -1) 
               {
                  prettyId = "td";
               }
               else 
               {
                  prettyId = "fd";
               }
               
               date = args[0][0];
               var selectedDate = new Date(date[0], (date[1] - 1), date[2]);
               
               var elem = Dom.get(prettyId);
               Alfresco.CalendarHelper.writeDateToField(selectedDate, elem);
               
               if (prettyId == "fd") 
               {
                  // If a new fromDate was selected
                  var toDate = Alfresco.CalendarHelper.getDateFromField(Dom.get("td"));
                  if (YAHOO.widget.DateMath.before(toDate, selectedDate)) 
                  {
                     //...adjust the toDate if toDate is earlier than the new fromDate
                     var tdEl = Dom.get("td");
                     Alfresco.CalendarHelper.writeDateToField(selectedDate, tdEl)
                     document.getElementsByName('to')[0].value = dateFormat(selectedDate, 'yyyy/mm/dd');
                  }
                  document.getElementsByName('from')[0].value = dateFormat(selectedDate, 'yyyy/mm/dd');
               }
               else 
               {
                  var toDate = Alfresco.CalendarHelper.getDateFromField(elem);
                  document.getElementsByName('to')[0].value = dateFormat(toDate, 'yyyy/mm/dd');
               }
            }
            o.oCalendarMenu.hide();
            (container.indexOf("enddate") > -1) ? YAHOO.util.Dom.get('calendarendpicker-button').focus() : YAHOO.util.Dom.get('calendarpicker-button').focus();
         }, o, true);
         o.oCalendarMenu.body.tabIndex = -1;
         o.oCalendar.oDomContainer.tabIndex = -1;
         o.oCalendarMenu.show();
         o.oCalendar.show();
         o.oCalendarMenu.body.focus();
         return false;
      },
      
      _onDateValidation: function _onDateValidation(field, args, event, form, silent)
      {
         var fromHours = Dom.get(args.obj.id + "-start").value.split(':');
         var toHours = Dom.get(args.obj.id + "-end").value.split(':');
         
         // Check that the end date is after the start date
         var startDate = Alfresco.CalendarHelper.getDateFromField(Dom.get("fd"));
         startDate.setHours(fromHours[0]);
         startDate.setMinutes(fromHours[1]);
         
         var toDate = Alfresco.CalendarHelper.getDateFromField(Dom.get("td"));
         toDate.setHours(toHours[0]);
         toDate.setMinutes(toHours[1]);
         
         //allday events; the date and time can be exactly the same so test for this too
         if (startDate.getTime() === toDate.getTime()) 
         {
            return true;
         }
         var after = YAHOO.widget.DateMath.after(toDate, startDate);
         
         if (Alfresco.logger.isDebugEnabled()) 
         {
            Alfresco.logger.debug("Current start date: " + startDate + " " + Dom.get(args.obj.id + "-start").value);
            Alfresco.logger.debug("Current end date: " + toDate + " " + Dom.get(args.obj.id + "-end").value);
            Alfresco.logger.debug("End date is after start date: " + after);
         }
         
         if (!after && !silent) 
         {
            form.addError(Alfresco.util.message('message.invalid-date', 'Alfresco.CalendarView'), field);
         }
         return after;
      }
   };
   
   return {
      registerDialog: function registerDialog(dialogName)
      {
      
         var dialog = new Alfresco.module.SimpleDialog();
         dialog.setOptions(dialogConfig);
         dialogs[dialogName] = dialog;
         return dialogs[dialogName];
      },
      getDialog: function getDialog(dialogName)
      {
         return dialogs[dialogName] || null;
      }
   };
})();

Alfresco.util.DialogManager.registerDialog('CalendarView.addEvent');
