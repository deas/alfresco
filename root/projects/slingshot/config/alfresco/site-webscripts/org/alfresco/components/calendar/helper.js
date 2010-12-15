var CalendarScriptHelper = (function()
{
   var now = new Date();
   /* Number of ms in a day */
   var DAY = 24 * 60 * 60 * 1000;
   /* Days in a week */
   var DAYS_IN_WEEK = 7;
   
   //add n days to given date
   var addDays = function(d, nDays)
   {
      d.setDate(d.getDate() + nDays);
      return d;
   };
   //returns number of days in month
   var daysInMonth = function(iMonth, iYear)
   {
      return 32 - new Date(iYear, iMonth, 32).getDate();
   };
   
   var zeroPad = function(value)
   {
      return (value < 10) ? '0' + value : value;
   };
   // {
   // "name" : "1225808148178-998.ics",
   // "title" : "lunch",
   // "where" : "somewhere",
   // "when" : "04 Nov 2008",
   //   "url" : "page/site/testSite/calendar?date=2008-11-04",
   // "start" : "12:00",
   // "end" : "13:00",
   // "site" : "testSite"
   // }
   var convertToIcalFormat = function(event, eventDate, endDate)
   {
      var convertedEvent = {};
      convertedEvent.location = event.where;
      eventDate.setHours(new String(event.start).split(':')[0]);
      convertedEvent.dtstart = toISOString(eventDate).split('+')[0];
      convertedEvent.dtstartText = zeroPad(eventDate.getHours()) + ':' + zeroPad(eventDate.getMinutes());
      eventDate.setHours(new String(event.end).split(':')[0]);
      convertedEvent.dtend = toISOString(endDate).split('+')[0];
      convertedEvent.dtendText = zeroPad(endDate.getHours()) + ':' + zeroPad(endDate.getMinutes());
      convertedEvent.summary = event.title;
      // convertedEvent.url = event.url;
      convertedEvent.description = event.description || event.title;
      convertedEvent.location = event.where;
      convertedEvent.name = event.name;
      convertedEvent.tags = event.tags;
      convertedEvent.duration = event.duration;
      
      if (event.start === event.end) 
      {
         convertedEvent.allday = true;
      }
      else 
      {
         convertedEvent.allday = false;
      }
      
      
      return convertedEvent;
   };
   
   /**
    * Converts a JavaScript native Date object into a ISO8601-formatted string
    *
    * Original code:
    *    dojo.date.stamp.toISOString
    *    Copyright (c) 2005-2008, The Dojo Foundation
    *    All rights reserved.
    *    BSD license (http://trac.dojotoolkit.org/browser/dojo/trunk/LICENSE)
    *
    * @method toISO8601
    * @param dateObject {Date} JavaScript Date object
    * @param options {object} Optional conversion options
    *    zulu = true|false
    *    selector = "time|date"
    *    milliseconds = true|false
    * @return {string}
    * @static
    */
   var toISOString = function()
   {
      //	summary:
      //		Format a Date object as a string according a subset of the ISO-8601 standard
      //
      //	description:
      //		When options.selector is omitted, output follows [RFC3339](http://www.ietf.org/rfc/rfc3339.txt)
      //		The local time zone is included as an offset from GMT, except when selector=='time' (time without a date)
      //		Does not check bounds.  Only years between 100 and 9999 are supported.
      //
      //	dateObject:
      //		A Date object
      var _ = function(n)
      {
         return (n < 10) ? "0" + n : n;
      };
      
      return function(dateObject, options)
      {
         options = options || {};
         var formattedDate = [];
         var getter = options.zulu ? "getUTC" : "get";
         var date = "";
         if (options.selector != "time") 
         {
            var year = dateObject[getter + "FullYear"]();
            date = ["0000".substr((year + "").length) + year, _(dateObject[getter + "Month"]() + 1), _(dateObject[getter + "Date"]())].join('-');
         }
         formattedDate.push(date);
         if (options.selector != "date") 
         {
            var time = [_(dateObject[getter + "Hours"]()), _(dateObject[getter + "Minutes"]()), _(dateObject[getter + "Seconds"]())].join(':');
            var millis = dateObject[getter + "Milliseconds"]();
            if (options.milliseconds) 
            {
               time += "." + (millis < 100 ? "0" : "") + _(millis);
            }
            if (options.zulu) 
            {
               time += "Z";
            }
            else if (options.selector != "time") 
            {
               var timezoneOffset = dateObject.getTimezoneOffset();
               var absOffset = Math.abs(timezoneOffset);
               time += (timezoneOffset > 0 ? "-" : "+") +
               _(Math.floor(absOffset / 60)) +
               ":" +
               _(absOffset % 60);
            }
            formattedDate.push(time);
         }
         return formattedDate.join('T'); // String
      };
   }();
   /**
    * Converts an ISO8601-formatted date into a JavaScript native Date object
    *
    * Original code:
    *    dojo.date.stamp.fromISOString
    *    Copyright (c) 2005-2008, The Dojo Foundation
    *    All rights reserved.
    *    BSD license (http://trac.dojotoolkit.org/browser/dojo/trunk/LICENSE)
    *
    * @method Alfresco.thirdparty.fromISO8601
    * @param formattedString {string} ISO8601-formatted date string
    * @return {Date|null}
    * @static
    */
   var fromISOString = function()
   {
      //	summary:
      //		Returns a Date object given a string formatted according to a subset of the ISO-8601 standard.
      //
      //	description:
      //		Accepts a string formatted according to a profile of ISO8601 as defined by
      //		[RFC3339](http://www.ietf.org/rfc/rfc3339.txt), except that partial input is allowed.
      //		Can also process dates as specified [by the W3C](http://www.w3.org/TR/NOTE-datetime)
      //		The following combinations are valid:
      //
      //			* dates only
      //			|	* yyyy
      //			|	* yyyy-MM
      //			|	* yyyy-MM-dd
      // 			* times only, with an optional time zone appended
      //			|	* THH:mm
      //			|	* THH:mm:ss
      //			|	* THH:mm:ss.SSS
      // 			* and "datetimes" which could be any combination of the above
      //
      //		timezones may be specified as Z (for UTC) or +/- followed by a time expression HH:mm
      //		Assumes the local time zone if not specified.  Does not validate.  Improperly formatted
      //		input may return null.  Arguments which are out of bounds will be handled
      //		by the Date constructor (e.g. January 32nd typically gets resolved to February 1st)
      //		Only years between 100 and 9999 are supported.
      //
      //	formattedString:
      //		A string such as 2005-06-30T08:05:00-07:00 or 2005-06-30 or T08:05:00
      
      var isoRegExp = /^(?:(\d{4})(?:-(\d{2})(?:-(\d{2}))?)?)?(?:T(\d{2}):(\d{2})(?::(\d{2})(.\d+)?)?((?:[+-](\d{2}):(\d{2}))|Z)?)?$/;
      
      return function(formattedString)
      {
         var match = isoRegExp.exec(formattedString);
         var result = null;
         
         if (match) 
         {
            match.shift();
            if (match[1]) 
            {
               match[1]--;
            } // Javascript Date months are 0-based
            if (match[6]) 
            {
               match[6] *= 1000;
            } // Javascript Date expects fractional seconds as milliseconds
            result = new Date(match[0] || 1970, match[1] || 0, match[2] || 1, match[3] || 0, match[4] || 0, match[5] || 0, match[6] || 0);
            
            var offset = 0;
            var zoneSign = match[7] && match[7].charAt(0);
            if (zoneSign != 'Z') 
            {
               offset = ((match[8] || 0) * 60) + (Number(match[9]) || 0);
               if (zoneSign != '-') 
               {
                  offset *= -1;
               }
            }
            if (zoneSign) 
            {
               offset -= result.getTimezoneOffset();
            }
            if (offset) 
            {
               result.setTime(result.getTime() + offset * 60000);
            }
         }
         
         return result; // Date or null
      };
   }();
   
   return {
      /**
       * Retrieves user events for specified date
       *
       * @param {Date} d  Optional. Date from which to retrieve user events. If not specified,
       * the date used is one specified in request or the current date.
       *
       * @returns  {Array} eventList Array of events
       */
      getUserEvents: function(d)
      {
         var d = d || this.getContextDate(this.getDefaultDate());
         var uri = "/calendar/events/" + encodeURIComponent(page.url.templateArgs.site) +
         "/user?from=" +
         encodeURIComponent(toISOString(d, {
            selector: 'date'
         }));
         var connector = remote.connect("alfresco");
         var result = connector.get(uri);
         if (result.status == status.STATUS_OK) 
         {
            var eventList = eval('(' + result.response + ')').events;
            return eventList;
         }
      },
      
      getDefaultDate: function()
      {
         var d = new Date();
         switch (this.getView())
         {
            case 'agenda':
            case 'day': // today
               return d;
            case 'month': //first day of month
               return new Date(d.getTime() - ((d.getDate() - 1) * DAY));
            case 'week': //first day of week
               return new Date(d.getTime() - ((d.getDay() - 1) * DAY));
         }
      },
      
      /**
       *  Gets the current date
       *
       * @returns {Date} The current date
       */
      getCurrentDate: function()
      {
         return now;
      },
      
      getView: function()
      {
         return escape(getPageUrlParam('view', 'month').toLowerCase());
      },
      
      /**
       * Gets the requested date for the request or the specified default date (for the current view) if not specified
       *
       * @param   {Date} Date to use as default if not date param specified
       * @returns {String} The contextual date for the request.
       */
      getContextDate: function(defaultDate)
      {
         return fromISOString(getPageUrlParam('date', defaultDate.getFullYear() + "-" + zeroPad(defaultDate.getMonth() + 1) + "-" + zeroPad(defaultDate.getDate())));
      },
      
      /**
       * Initialises data used to render the week view
       *
       * @returns {Object} week view relevant data
       *
       */
      initialiseDayView: function(d)
      {
         var viewArgs = {};
         
         viewArgs.startDate = toISOString(d, {
            selector: 'date'
         });
         viewArgs.endDate = toISOString(new Date(d.getTime() + (DAY * 1)), {
            selector: 'date'
         });
         viewArgs.titleDate = viewArgs.startDate;
         
         return viewArgs;
      },
      
      /**
       * Initialises data used to render the week view
       *
       * @returns {Object} week view relevant data
       *
       */
      initialiseWeekView: function(d)
      {
         var viewArgs = {};
         var currentDate = new Date();
         var currentDateMs = currentDate.getTime();
         /* Start at the beginning of the week for the week view */
         var startTime = d.getTime() - (DAY * d.getDay());
         
         var startDate = new Date(startTime);
         var endOfWeekDate = new Date(((startDate.getTime() + (DAY * 6))));
         
         //get day of week if current day is in the week that is shown
         viewArgs.startDate = toISOString(startDate).split('+')[0];
         viewArgs.titleDate = toISOString(endOfWeekDate).split('+')[0];
         viewArgs.endDate = toISOString(new Date(((startDate.getTime() + (DAY * 7))))).split('+')[0];
         if (currentDateMs > startTime && currentDateMs < endOfWeekDate.getTime()) 
         {
            viewArgs.dayOfWeek = currentDate.getDay();
         }
         else 
         {
            viewArgs.dayOfWeek = -1;
         }
         
         viewArgs.columnHeaders = [];
         viewArgs.dates = [];
         for (var i = 0; i < DAYS_IN_WEEK; i++) 
         {
            viewArgs.columnHeaders.push(new Date(startDate.getTime()));
            viewArgs.dates.push({
               id: toISOString(startDate, {
                  selector: 'date'
               }).split('T')[0]
            });
            startDate.setTime(startDate.getTime() + DAY);
         }
         
         return viewArgs;
      },
      
      /**
       * Initialises data used to render the month view
       *
       * @returns {Object} month view relevant data
       */
      initialiseMonthView: function(d)
      {
         var viewArgs = {};
         
         //the first day in month as a Date object - actually first day to render in view (so could be a day in the previous month)
         var firstDayOfMonth = new Date(d.getTime() - ((d.getDate() - 1) * DAY));
         var actualFirstDayOfMonth = new Date(d.getFullYear(), d.getMonth(), 1);
         //number of days in month
         var num_daysInMonth = daysInMonth(d.getMonth(), d.getFullYear());
         // lastDayOfMonth is midnight on the first day of next month (i.e. immediately after the current month ends) 
         var lastDayOfMonth = new Date(((firstDayOfMonth.getTime() + (DAY * num_daysInMonth))));
         //gets the first day of month index eg 6 = 6 cells in
         var startDay_month = firstDayOfMonth.getDay();
         
         //the first day in the last month that can be shown in month view eg 26th
         var lastMonth_days = addDays(firstDayOfMonth, (1 - (startDay_month)) - 1);//addMonth(now,-1).toString();
         //number of last month days showing in view
         var numLastMonthDays = startDay_month;
         //date of 1st day in next month
         var nextMonthDays = lastDayOfMonth;
         //array of dates to show in view
         var monthview_dates = [];
         //store  user events in array and key array by date
         viewArgs.viewEvents = [];
			// store user events for other months in an array
			viewArgs.spanningEvents = [];
			
         viewArgs.startDate = toISOString(new Date(d.getTime() - ((d.getDate() - 1) * DAY)), {
            selector: 'date'
         });
         viewArgs.titleDate = viewArgs.startDate;
         viewArgs.endDate = toISOString(lastDayOfMonth, {
            selector: 'date'
         });
         
         var events = this.getUserEvents(firstDayOfMonth, {
            selector: 'date'
         });
         if (events !== undefined) 
         {
            for (var i = 0; i < events.length; i++) 
            {
               var eventDate = new Date();
               var endDate = new Date();
               var ev = events[i];
               eventDate.setTime(fromISOString(ev.when));
               endDate.setTime(fromISOString(ev.endDate));
               // Check event should be shown within the month displayed:
               //  - event starts BEFORE or during the month: eventStart < monthEnd, AND
               //  - event ends during OR after the month eventEnd > monthStart
               if (eventDate.getTime() <= lastDayOfMonth.getTime() && endDate >= actualFirstDayOfMonth.getTime())
               {
					   var icalEvent = convertToIcalFormat(ev, eventDate, endDate);
						var eventMonth = eventDate.getMonth(); 
					   var key = 'ev_';
						if (eventMonth === d.getMonth()) 
					   {
						  	 key += eventDate.getDate();						
						} else 
						{
                     icalEvent.isSpannedEvent = true;
							key = "spannedEvents"; // write events to new array, specifically for events that span multiple months
						}
						
						if (viewArgs.viewEvents[key] === undefined) 
                  {
                     viewArgs.viewEvents[key] = [];
                  }
                  // add event to array
                  viewArgs.viewEvents[key].push(icalEvent); 
               }
            }
         }
         //calculate the dates to show in month view for current month
         var numPreviousAndCurrentMonthDays = num_daysInMonth + numLastMonthDays;
         
         // the number of additional days required from next month to make up a full week
         var additionalDaysNextMonth = 0;
         // if the number of days fits the display exactly, we don't need to add an extra week,
         if ((numPreviousAndCurrentMonthDays % 7) !== 0) 
         {
            additionalDaysNextMonth = 7 - (numPreviousAndCurrentMonthDays % 7);
         }
         
         // how many cells should we show? Days in this month plus additional days required from last month and next month
         var numCellsToRender = numPreviousAndCurrentMonthDays + additionalDaysNextMonth;
         
         for (var i = 0; i <= numCellsToRender; i++) 
         {
            //last month days
            if (i < startDay_month) 
            {
               monthview_dates[i] = {
                  id: toISOString(lastMonth_days, {
                     selector: 'date'
                  }),
                  day: lastMonth_days.getDate()
               };
               lastMonth_days = addDays(lastMonth_days, 1);
            }
            //next month days
            else if (i >= numPreviousAndCurrentMonthDays) 
            {
               monthview_dates[i] = {
                  id: toISOString(nextMonthDays, {
                     selector: 'date'
                  }),
                  day: nextMonthDays.getDate()
               };
               nextMonthDays = addDays(nextMonthDays, 1);
            }
            //month days
            else 
            {
               var d = fromISOString(viewArgs.startDate);
               var day = ((i - startDay_month) + 1);
               d.setDate(day);
               
               monthview_dates[i] = {
                  id: toISOString(d, {
                     selector: 'date'
                  }),
                  day: day
               };
 
               if (viewArgs.viewEvents['ev_' + day]) 
               {
                  monthview_dates[i].events = viewArgs.viewEvents['ev_' + day];
               }
            }
            
         }
         
         viewArgs.dates = monthview_dates;
         //used to determine whether to disable cell or not
         viewArgs.startDay_month = startDay_month;
         //used to determine whether to disable cell or not
         viewArgs.num_daysInMonth = num_daysInMonth;
         viewArgs.num_month_rows = (numCellsToRender / 7) - 1;//zero index
         return viewArgs;
      },
      initialiseAgendaView: function(d)
      {
         var viewArgs = {};
         var firstDayOfMonth = new Date(d.getTime() - ((d.getDate() - 1) * DAY));
         //number of days in month
         var num_daysInMonth = daysInMonth(d.getMonth(), d.getFullYear());
         
         var lastDayOfMonth = new Date(((firstDayOfMonth.getTime() + (DAY * num_daysInMonth))));
         
         
         viewArgs.viewEvents = [];
         viewArgs.startDate = toISOString(d, {
            selector: 'date'
         });
         viewArgs.titleDate = viewArgs.startDate;
         viewArgs.endDate = toISOString(lastDayOfMonth, {
            selector: 'date'
         });
         return viewArgs;
      },
      isUserPermittedToCreateEvents: function()
      {
         var json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + encodeURIComponent(user.name));
         var membership = eval('(' + json + ')');
         return (membership.role !== "SiteConsumer") ? 'true' : 'false';
      },
      initView: function()
      {
         var viewArgs = {};
         viewArgs.viewType = CalendarScriptHelper.getView();
         viewArgs.permitToCreateEvents = this.isUserPermittedToCreateEvents();
         if (viewArgs.viewType == 'month') 
         {
            viewArgs.view = this.initialiseMonthView(this.getContextDate(this.getDefaultDate()));
         }
         else if (viewArgs.viewType == 'week') 
         {
            viewArgs.view = this.initialiseWeekView(this.getContextDate(this.getDefaultDate()));
         }
         else if (viewArgs.viewType == 'day') 
         {
            viewArgs.view = this.initialiseDayView(this.getContextDate(this.getDefaultDate()));
         }
         else if (viewArgs.viewType == 'agenda') 
         {
            viewArgs.view = this.initialiseAgendaView(this.getContextDate(this.getDefaultDate()));
         }
         return viewArgs;
      },
      convertFromISOString: function(formattedDate)
      {
         return fromISOString(formattedDate);
      },
      convertToISOString: function(dateObject, options)
      {
         return toISOString(dateObject, options);
      }
   };
   
}());
