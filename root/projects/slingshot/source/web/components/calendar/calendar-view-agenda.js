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
 * CalendarAgendaView base component.
 * 
 * @namespace Alfresco
 * @class Alfresco.CalendarAgendaView
 */
( function() 
{
    
 var Dom = YAHOO.util.Dom,
    Event = YAHOO.util.Event,
    Selector = YAHOO.util.Selector,
    fromISO8601 = Alfresco.util.fromISO8601,
    toISO8601 = Alfresco.util.toISO8601,
    dateFormat = Alfresco.thirdparty.dateFormat;
    
YAHOO.lang.augmentObject(Alfresco.CalendarView.prototype, {            


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
    * Render events to DOM
    *  
    * @method addEvents
    * 
    */
   renderEvents : function CalendarAgendaView__renderEvents(events)
   {
      //sort events by day
      var sortedEvents = [];
      var numEvents = events.length;

      for (var i=0;i<numEvents;i++)
      {
      var event = events[i];
      var date = event.from.split('T')[0];
      if (!sortedEvents[date])
      {
        sortedEvents[date]=[];
      }
      sortedEvents[date].push(event);
      }
      //render
      this.renderAgendaDayEvents(sortedEvents, Dom.get(this.options.id));
      this.initCalendarEvents();          
      YAHOO.Bubbling.fire("eventDataLoad",events);       
   },          

   /**
    * 
    * if old parent has no li els anymore (event has been moved to new day)
    * delete parent and h2 
    * 
    * @param el {element}
    */
   _cleanUpAgendaView : function CalendarAgendaView__cleanUpAgendaView(el)
   {

     if (el.getElementsByTagName('li').length===0)
     {
        var h2El = Dom.getPreviousSibling(el);
        el.parentNode.removeChild(el);
        h2El.parentNode.removeChild(h2El);
     }
      
   },
   
   /**
    * Gets a correct DOM element to insert new events before in agenda view.
    * 
    * @param   date {Date} Date in ISO format
    */
   _getAgendaInsertNode : function CalendarAgendaView__getAgendaInsertNode(date)
   {
      var day = date.split('-');
      var partialId = day[0]+'-'+day[1];
      day = day[2];
      var adjEl;
      while(day<=31)
      {
         adjEl=Dom.get('cal-'+partialId+'-'+Alfresco.CalendarHelper.padZeros(day));
         if (adjEl)
         {
            break;
         }
         day++;         
      }
      return Dom.getPreviousSibling(adjEl); 
   },
   
   /** Render events in agenda view
    *  
    * @param events {array} events keyed by date
    * @param targetEl {element} element to add events to.
    * @param insertBeforeNode {element} (optional) if specified, events are added before this element
    */
   renderAgendaDayEvents : function CalendarAgendaView_renderAgendaDayEvents(events,targetEl,insertBeforeNode)
   {
       for (var event in events)
       {
         var date = Alfresco.util.formatDate(Alfresco.util.fromISO8601(events[event][0].from), Alfresco.util.message("calendar.dateFormat.agenda"));
         var contDiv = document.createElement('div');
         contDiv.id = 'cal-' + events[event][0].from.split('T')[0];
         var header = Alfresco.CalendarHelper.renderTemplate('agendaDay',{date:date});
         var eventsHTML = '';
         var ul = document.createElement('ul');
         var agendaEvts = events[event];
         for (var i = 0; i<agendaEvts.length;i++)
         {
           var event = agendaEvts[i];
           event.hidden='';
           event.contel = 'div';
           event.el = 'li';
           if (event.start===event.end) {
             event.allday = 'allday';
           }
           var evEl = Alfresco.CalendarHelper.renderTemplate('vevent',event);
           Dom.generateId(evEl);
           if (event.allday=='allday')
           {
              var pEl = Dom.getElementsByClassName('dates','p',evEl)[0];
              Dom.addClass(pEl,'theme-bg-color-1');
              Dom.addClass(pEl,'theme-color-1');                 
           }
           ul.appendChild(evEl);
         }
      
         contDiv.appendChild(ul);
      
         if (!insertBeforeNode)
         {
            targetEl.appendChild(header);
            targetEl.appendChild(contDiv);               
         }
         else
         {
            targetEl.insertBefore(header,insertBeforeNode);
            // Dom.insertAfter(header,insertAfterNode);
            Dom.insertAfter(contDiv,header);
         }
      }
      return evEl;
   },   
         
   /**
    * Handler for eventEdited event. Updates event in DOM in response to updated event data.
    * 
    * @method  onEventEdited
    * 
    * @param e {object} event object
    * @param o {object} new event data
    *  
    */
   onEventEdited : function CalendarAgendaView_onEventEdited(e,o) 
   {
      var data = o[1].data;
      var id = o[1].id;

      var event = null;
      if (this.events[id]) {
         event = this.events[id];
      }

      var eventEl = event.el;

      var targetEl = null;
      var dateParts = data.dtstart.split('T');
      var hour = dateParts[1].split(':')[0];
      var min =  dateParts[1].split(':')[1];
      var id = 'cal-'+dateParts[0];

      var evDate = fromISO8601(data.dtstart);
      // if event is valid for view must be within startdate and (enddate-1 second) of current view
      if (!this.isValidDateForView(evDate))
      {
       if (this.events[eventEl.id])
       {
         delete this.events[eventEl.id];              
       }
       var currPar = Dom.getAncestorByTagName(eventEl,'div');//div
       eventEl.parentNode.removeChild(eventEl);
      }
      //valid for current view
      else {
       //allday
       if(data.allday && data.allday!='false')
       {
         data.contEl='div';
         data.hidden='';
         data.el='div';

         if (data.dtstart) { // have to convert
          data = this.convertDataToTemplateData(data);
         }
         // data.contEl='div';
         // data.hidden='';
         // data.el='div';
   

         var days = data.duration.match(/([0-9]+)D/);
         if (days && days[1])
         {
            data.duration = data.duration.replace(/([0-9]+)D/,++days[1]+'D');
         }
         var currPar = Dom.getAncestorByTagName(eventEl,'div');//div            
         if (Dom.hasClass(eventEl,'allday'))
         {
            this.removeMultipleDayEvents(eventEl);
         }
         eventEl.parentNode.removeChild(eventEl);
   
         var eventEl = Alfresco.CalendarHelper.renderTemplate('vevent',data);         
         eventEl = this.renderAllDayEvents(eventEl,data);
         this.calEventConfig.draggable = false;
         this.calEventConfig.resizable = false;
         var newCalEvent = new Alfresco.calendarEvent(eventEl, this.dragGroup,YAHOO.lang.merge(this.calEventConfig,{performRender:false}));
         this.events[eventEl.id]=newCalEvent;
       }
       // not allday event
       else 
       { 
         //move to correct cell
         Dom.removeClass(eventEl,'allday');
         this.removeMultipleDayEvents(eventEl);
   
         data.el = 'li';  
         //tag with enclosing brackets
         data.contEl = 'div';
         data.hidden = '';
         data.allday='';
         data = this.convertDataToTemplateData(data);
         var elNextEvent = eventEl.nextSibling;
         var currPar = Dom.getAncestorByTagName(eventEl,'div');//div
         eventEl.parentNode.removeChild(eventEl);


         eventEl = Alfresco.CalendarHelper.renderTemplate('vevent',data);
         Dom.generateId(eventEl);
         targetEl = Dom.get('cal-'+data.fromDate.split('T')[0]);
         targetEl = targetEl.getElementsByTagName('ul')[0];
         Dom.removeClass(Dom.getElementsByClassName('dates','p',eventEl)[0],'theme-bg-color-1');
         if (!elNextEvent)
         {
           targetEl.appendChild(eventEl);                 
         }
         else
         {
           targetEl.insertBefore(eventEl,elNextEvent);
         }
         this.calEventConfig.draggable = false;
         this.calEventConfig.resizable = false;
         this.events[eventEl.id] = new Alfresco.calendarEvent(eventEl, this.dragGroup,this.calEventConfig);
       }       
      }
      if (data.tags)
      {
       data.category=data.tags;          
      }

      this._cleanUpAgendaView(currPar);
      this.events[eventEl.id].update(data);
      YAHOO.Bubbling.fire("eventEditedAfter");
     
   },

   /**
    * Handler for when event is saved
    * 
    * @method onEventSaved
    * 
    * @param e {object} event object 
    */
   onEventSaved : function CalendarAgendaView_onEventSaved(e)
   {
      var data = YAHOO.lang.JSON.parse(e.serverResponse.responseText).event;

      var dtStartDate = Alfresco.util.fromISO8601(data.from+'T'+data.start);
      if (this.isValidDateForView(dtStartDate))
      {
         var dtEndDate = Alfresco.util.fromISO8601(data.to+'T'+data.end);
         data.duration = Alfresco.CalendarHelper.getDuration(dtStartDate,dtEndDate);
         //tagname
         data.el = 'li';  
         //tag with enclosing brackets
         data.contEl = 'div';
         data.hidden ='';
         data.tags = data.tags.join(' ');
         data.allday = (YAHOO.lang.isUndefined(data.allday)) ? '' : data.allday;
         data.from = data.from +'T'+data.start;
         data.to = data.to +'T'+data.end;

         //render into allday section
         if(data.allday)
         {
            data.allday = 'allday';
         }
         else 
         { //not allday
            data.el = 'li';  
            //tag with enclosing brackets
            data.contEl = 'div';
            data.hidden = '';
            
         }
         this.calEventConfig.resizable = false;
         this.calEventConfig.draggable = false;
         var vEventEl;
         //get containing date TD cell for event
         var targetEl = Dom.get('cal-'+data.from.split('T')[0]);
         // date div doesn't exist in agenda view so let's create it.
         if (!targetEl)
         {
            var event = [];
            var identifier = data.from.split('T')[0];
            event[identifier] = [];
            event[identifier].push(data);
            var adjEl = this._getAgendaInsertNode(identifier);
            vEventEl = this.renderAgendaDayEvents(event,YAHOO.util.Selector.query('div.agendaview div', null, true),adjEl);
         } // already exists 
         else 
         {
            targetEl = targetEl.getElementsByTagName('ul')[0];
            vEventEl = Alfresco.CalendarHelper.renderTemplate('vevent',data);
            targetEl.appendChild(vEventEl);
            if (data.allday=='allday')
            {
               var pEl = Dom.getElementsByClassName('dates','p',vEventEl)[0];
               Dom.addClass(pEl,'theme-bg-color-1');
               Dom.addClass(pEl,'theme-color-1');                                
            }
         }
         var id = Event.generateId(vEventEl);
         var newCalEvent = new Alfresco.calendarEvent(vEventEl, this.dragGroup,YAHOO.lang.merge(this.calEventConfig,{performRender:false}));
         this.events[id]=newCalEvent;
      }
      YAHOO.Bubbling.fire("eventSavedAfter");
      this.displayMessage('message.created.success',this.name);             
   },
   
   /**
    * Handler for when an event is deleted
    * 
    * @method  onEventDeleted
    *  
    */
   onEventDeleted : function CalendarAgendaView_onEventDeleted()
   {
      this.displayMessage('message.deleted.success',this.name);     
      var id = arguments[1][1].id;
      var currPar = Dom.getAncestorByTagName(this.events[id].getElement(),'div');//div
      var evt = this.events[id].getElement();
      this.events[id].deleteEvent();
      this._cleanUpAgendaView(currPar);
      Event.purgeElement(this.events[id].getEl(),true);          
      delete this.events[id];
   }
});
})();