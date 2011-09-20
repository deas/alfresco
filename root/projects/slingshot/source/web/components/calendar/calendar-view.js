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
      Alfresco.CalendarView.superclass.constructor.call(this, "Alfresco.CalendarView", htmlId, ["calendar", "button", "resize", "datasource", "datatable", "history"]);

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

         YAHOO.Bubbling.on("eventEdited", this.onEventEdited, this);
         YAHOO.Bubbling.on("eventEditedAfter", this.onAfterEventEdited, this);
         YAHOO.Bubbling.on("eventSaved", this.onEventSaved, this);
         YAHOO.Bubbling.on("eventSavedAfter", this.onAfterEventSaved, this);
         YAHOO.Bubbling.on("eventDeleted", this.onEventDeleted, this);
         YAHOO.Bubbling.on("eventDeletedAfter", this.onAfterEventDeleted, this);

         YAHOO.Bubbling.on("tagSelected", this.onTagSelected, this);
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
         if (this.calendarView === Alfresco.CalendarView.VIEWTYPE_AGENDA ) {

            // initialise DOM Event registration
            this.initEvents();
            // Load events. Rest of init is handled by a call back from the event loading.
            this.getEvents(dateFormat(this.options.startDate, 'yyyy-mm-dd'));
         } else {
            // FullCalendar handles event loading and callbacks, so call the function that triggers that.
            this.renderEvents();
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
       * @param data {object} either the HTML node of the event or the event data
       */
      
      getEventObj: function CalendarView_getEventObj(data)
      {
         // If we've got the HTML node, it'll have a rel attr we can use to get the event data
         if (typeof(data.rel) !== "undefined" && data.rel !== "")
         {
            return this.parseRel(data);
         }
         // Otherwise, assume it's the event object, so just send that back.
         else
         {
            return data;
         }
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
         if (Sel.test(element, 'div.yui-dt-liner')) 
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
            // TODO: Make this format consistent across calendar views and API.
            var ev = data[i];
            var date = fromISO8601(ev.startAt.iso8601);
            var endDate = fromISO8601(ev.endAt.iso8601);
            if (comparisonFn(date, endDate))
            {
               var datum = {};
               datum.desc = ev.description || '';
               datum.name = ev.title;
               datum.title = ev.title;
               datum.where = ev.where;
               datum.description = ev.description;
               datum.isoutlook = ev.isoutlook == "true" ? "isoutlook" : "";
               datum.contEl = 'div';
               datum.from = ev.startAt.iso8601;
               datum.to = ev.endAt.iso8601;
               datum.uri = '/calendar/event/' + this.options.siteId + '/' + ev.name + '?date=' + ev.startAt.iso8601;
               datum.hidden = '';
               datum.allday = '';
               datum.isMultiDay = (!Alfresco.CalendarHelper.isSameDay(date, endDate));
               datum.isAllDay = (ev.allday == "true") ? true : false;
               datum.el = 'div';
               datum.duration = Alfresco.CalendarHelper.getDuration(date, endDate);
               var days = datum.duration.match(/([0-9]+)D/);
               if (days && days[1])
               {
                  datum.duration = datum.duration.replace(/([0-9]+)D/, ++days[1] + 'D');
               }

               datum.key = datum.from.split(":")[0] + ':00';
               datum.start = dateFormat(date, "HH:MM");
               datum.end = dateFormat(endDate, "HH:MM");
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
       * @param events {Array} Array of event objects
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
       * @param el {DOM Element} the element that was clicked on
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
       * @param date {Date object} Javascript date object containing the start date for the new event.
       *
       */
      showAddDialog: function CalendarView_showAddDialog(date)
      {
         var displayDate;
         // if from toolbar add event
         if (YAHOO.lang.isUndefined(date))
         {
            this.currentDate = displayDate = (Alfresco.util.getQueryStringParameter('date')) ? fromISO8601(Alfresco.util.getQueryStringParameter('date')) : new Date();
         }
         else
         {
            // from cell
            this.currentDate = displayDate = date;
         }

         if (this.eventDialog)
         {
            this.eventDialog.dialog.destroy();
            delete this.eventDialog;
         }

         var editInfo = new Alfresco.EventInfo(this.id);

         this.eventDialog = editInfo.initEditDialog(
         {
            actionUrl: Alfresco.constants.PROXY_URI + "calendar/create",
            ajaxSubmitMethod: Alfresco.util.Ajax.POST,
            templateRequestParams:
            {
               site: this.options.siteId
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
         });
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
         this.eventInfoPanel = new Alfresco.EventInfo(this.id);
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
       * @param o{object} DomEvent
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
            if (Sel.test(elTarget, 'div.' + this.dragGroup))
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
            if (Sel.test(elTarget, 'div.' + this.dragGroup))
            {
               Dom.addClass(elTarget, 'highlight');
            }
         }
         else if (e.type === 'click') 
         {
            // Show or hide wee hours?
            if (Sel.test(elTarget, 'a#collapseTriggerLink')) 
            {
               this.toggleEarlyTableRows();
               Event.preventDefault(e);
            }
            // are we adding a new event?
            else if (Sel.test(elTarget, 'button#addEventButton') || Sel.test(elTarget.offsetParent, 'button#addEventButton') || Sel.test(elTarget, 'a.addEvent')) 
            {
               this.showAddDialog(elTarget);
               Event.preventDefault(e);
            }
            // a.summary = a click on the event title. Therefore into Event Info mode.
            else if (Sel.test(elTarget, 'a.summary') || Sel.test(elTarget, 'div.yui-dt-liner') ) 
            {
               this.showDialog(e, elTarget);
            }
            // Someone clicked the 'show more events in Month View' link.
            else if (Sel.test(elTarget, 'li.moreEvents a')) 
            {
               this.onShowMore(e, args, elTarget);
            }
            //Agenda View show more
            else if (Sel.test(elTarget, 'a.showMore'))
            {
               this.expandDescription(elTarget);
               Event.preventDefault(e);
            }
            else if (Sel.test(elTarget, 'a.showLess'))
            {
               this.collapseDescription(elTarget);
               Event.preventDefault(e);
            }
            // Delete this event link in Agenda DataTable
            else if (Sel.test(elTarget, "a.deleteAction"))
            {
               this.deleteDialog(e, elTarget);
            }
            // Edit event link in Agenda DataTable.
            else if (Sel.test(elTarget, "a.editAction"))
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
       * Handler for when calendar view is changed (agenda button is clicked)
       *
       * @method onViewChanged
       *
       */
      onViewChanged: function CalendarView_onViewChanged()
      {

         var params = Alfresco.util.getQueryStringParameters(),
            hash = window.location.hash;
            dateBookmark = hash.substring(hash.indexOf("date=") + 5).split("&")[0];
         params.view = Alfresco.util.ComponentManager.findFirst("Alfresco.CalendarToolbar").enabledViews[arguments[1][1].activeView];
         if (dateBookmark !== "")
         {
            params.date = dateBookmark;
         }
         // Remove both current parameters and current bookmarks.
         window.location = window.location.href.split('?')[0].split('#')[0] + Alfresco.util.toQueryString(params);

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
         args = args[1];
         Alfresco.util.PopupManager.displayMessage(
         {
            text: args.msg
         });
      },

      /**
       * Handler for eventEdited event. Updates event in DOM in response to updated event data.
       *
       * @method  onEventEdited
       *
       * @param e {object} event object
       * @param o {object} new event data
       */
      onEventEdited : function CalendarView_onEventEdited(e,o)
      {
         this.getEvents()
         YAHOO.Bubbling.fire("eventEditedAfter");
      },

      /**
       * Handler for when event is saved
       *
       * @method onEventSaved
       *
       * @param e {object} event object
       */
      onEventSaved : function CalendarView_onEventSaved(e)
      {
         this.getEvents();
         YAHOO.Bubbling.fire("eventSavedAfter");
         this.displayMessage('message.created.success',this.name);
      },

      /**
       * Handler for when an event is deleted
       *
       * @method  onEventDeleted
       */
      onEventDeleted : function CalendarView_onEventDeleted()
      {
         this.getEvents();
         YAHOO.Bubbling.fire("eventDeletedAfter");
         this.msg('message.deleted.success',this.name);
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

      /**
       * Stub function - to be overridden on the view level (e.g. by CalendarAgendaView_updateTitle)
       */
      updateTitle: function CalendarView_updateTitle()
      {
         return;
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
               var eventTags = events[i].tags
               // TODO: Remove this check once we have a consistent event object
               if (typeof(eventTags) === "string")
               {
                  eventTags = eventTags.split(" "); // TODO: Tags with spaces are not supported in the Calendar Event input field.
               }
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