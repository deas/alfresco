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
 * CalendarDayView base component.
 * 
 * @namespace Alfresco
 * @class Alfresco.CalendarDayView
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
    * Event initialization occurs here
    *  
    * @method initEvents
    */
   initEvents : function CalendarDayView_initEvents()
   {
      // superCall???
      Alfresco.CalendarDayView.superclass.initEvents.call(this);
   },
   
   /**
    * Renders view
    * 
    * @method render
    *  
    */
   render: function CalendarDayView_render()
   {
      Alfresco.CalendarDayView.superclass.render.call(this);
   },
   
   /**
    * Render events to DOM
    *  
    * @method addEvents
    * 
    */
   renderEvents : function CalendarView_renderEvents(events) 
   {
      var offsetTop = 0;
      var min;
      var getTargetEl = function(ev)
      {
         return function(ev) 
         {
             var segments  = Dom.getElementsByClassName('hourSegment','div',Dom.get('cal-'+ev.key));
             var min = (~~(1* (ev.from.split('T')[1].split(':')[1]) ) );
             var targetEl = (min>=30) ? segments[1] : segments[0];
             var reg = Dom.getRegion(targetEl);
             //on the hour or half hour
             if (min === 0 | min === 30)
             {

                 offsetTop = Math.round(reg.bottom/reg.top);
             }
             else 
             {
                 offsetTop = Math.round(reg.height/2);
             }
             return targetEl;
         };                
      }(this.calendarView);

      var len = events.length;
      var tdsWithEvents = [];
      for (var i = 0;i<len;i++)
      {
        var ev = events[i];
        var vEventEl = Alfresco.CalendarHelper.renderTemplate('vevent',ev);
        var id = Event.generateId(vEventEl);
        vEventEl.id = id;
        //all day
        if (ev.isAllDay) 
        {
          this.renderAllDayEvents(vEventEl,ev);
        }
        else {
          var targetEl = getTargetEl(ev);

          if (targetEl)
          {

              YAHOO.util.Dom.setStyle(targetEl,'position','relative');
              targetEl.appendChild(vEventEl);                
          }
          Dom.setStyle(vEventEl,'top',offsetTop+'px');              

          var td = Dom.getAncestorByTagName(vEventEl,'td');

          //maintain list of tds in with events have been added
          if (!tdsWithEvents[td.id])
          {
            tdsWithEvents[td.id]=td;
          }    
        }
      }

      this.initCalendarEvents();
      //render multiple events correctly
      this.renderMultipleEvents();
      if (YAHOO.env.ua.ie)
      {
         //set any div.hourSegment to position:static if it does not have any events within
         for (var i=0,len = this.hourSegments.length;i<len;i++)
         {
           var hourSegment = this.hourSegments[i];
           var hasEvent = (YAHOO.util.Dom.getElementsByClassName('vevent','div',hourSegment).length>0);
           if (YAHOO.util.Dom.getStyle(hourSegment,'position')==='relative')
           {
             if (!hasEvent)
             {
               YAHOO.util.Dom.setStyle(hourSegment,'position','static');
             }
             else
             {
               if (YAHOO.env.ua.ie>6)
               {
                 YAHOO.util.Dom.setStyle(hourSegment,'border-bottom-color','transparent');                  
               }
               else
               {
                  YAHOO.util.Dom.setStyle(hourSegment,'border-bottom-width',0);
               }

               this._fixedHourSegments.push(hourSegment);
             }
           }            
         }            
      }
      YAHOO.Bubbling.fire("eventDataLoad",events); 
   },     

   /**
    * Render (overlapping) multiple events correctly by calulating
    * events that overlap and resizing their widths accordingly.
    * It is called every time an event is created, edited, moved or deleted.
    * 
    * @method renderMultipleEvents
    *  
    */
   renderMultipleEvents : function CalendarDayView_renderMultipleEvents()
   {
     var existingEvents = Dom.getElementsByClassName('vevent','div');

     var numExistingEvents = existingEvents.length;
     var intersectEvents = [];
     
     for (var i=0;i<numExistingEvents;i++)
     {
        var elEv = existingEvents[i];
        if (!Dom.hasClass(elEv,'allday'))
        {
           Dom.setStyle(elEv,'width','99.3%');
           Dom.setStyle(elEv,'left',0+'px');           
           intersectEvents[elEv.id] = [];

           var reg = Dom.getRegion(elEv);
           if (reg!==false)
           {
               for (var j=0;j<numExistingEvents;j++)
               {
                  var el = existingEvents[j];
                  if (el.id!==elEv.id)
                  {
                    var intersectRegion = reg.intersect(Dom.getRegion(el));

                    if (!YAHOO.lang.isNull(intersectRegion))
                    {
                       intersectEvents[elEv.id].push(el);
                    }
                  }
               }
           }  
        }
     }
     var processedElems = [];
     for (var p in intersectEvents){
      var numOfIntersectedEvents = intersectEvents[p].length;
      if (numOfIntersectedEvents>0)
      {
         var parRegion = Dom.getRegion(document.getElementById(p).parentNode);

         var newWidth= (parRegion.right-parRegion.left)/(numOfIntersectedEvents+1);
         Dom.setStyle(p,'width',newWidth+'px');
         Dom.setStyle(p,'left',0+'px');         
         processedElems.push(p);
         for (var i=0;i<numOfIntersectedEvents;i++)
         {
            var el = intersectEvents[p][i];
            Dom.setStyle(el,'width',newWidth+'px');
            Dom.setStyle(el,'left',newWidth*(i+1)+'px');
            processedElems.push(el.id);
         }
      }
     }
   },

   /** 
    *  fix table cell border bleedthrough in IE
    * 
    *  Find overlapping elements and make border transparent
    *  for div.hourSegment that contain events. This is only done for
    *  the column that the event has been moved to.
    *  
    *  @param calEventEl {Element} Calendar Event object
    */ 
   _fixIEBorderBleedThrough : function CalendarWeekView__fixIEBorderBleedThrough(calEventEl)
   {
      if (!YAHOO.env.ua.ie)
      {
       return;
      }

      var calTable = Dom.getAncestorByTagName(calEventEl,'table');

      var parentCell = Dom.getAncestorByTagName(calEventEl,'td');
      var cellIndex = parentCell.cellIndex;
      var evRegion = Dom.getRegion(calEventEl);
      var rows = calTable.rows;

      for (var i = 2,len=rows.length;i<len;i++)
      {
         var cell = rows[i].cells[cellIndex];
         if (cell)
         {
           var cellEvents = Dom.getElementsByClassName('vevent','div',cell);
           if (cellEvents.length>0 && cell!=parentCell)
           {
             for (var j=0,evLen=cellEvents.length;j<evLen;j++)
             {
               if (evRegion.intersect(Dom.getRegion(cellEvents[j])))
               {
                 var hourSegment = Dom.getAncestorByClassName(cellEvents[j],'hourSegment','div');
                 //ie6 doesn't do transparent borders so we remove the bottom border for that browser
                 if (YAHOO.env.ua.ie && YAHOO.env.ua.ie>6)
                 {
                   Dom.setStyle(hourSegment,'border-bottom-color','transparent');
                 }
                 else
                 {
                   Dom.setStyle(hourSegment,'border-bottom-width',0);
                 }
                 this._fixedHourSegments.push(hourSegment);
               }
             }
           }
         }
      }
      //set any div.hourSegment to position:static if it does not have any events within
      for (var i=0,len = this.hourSegments.length;i<len;i++)
      {
         var hourSegment = this.hourSegments[i];
         var hasEvent = (Dom.getElementsByClassName('vevent','div',hourSegment).length>0);
         if (!hasEvent && Dom.getStyle(hourSegment,'position')==='relative')
         {
            Dom.setStyle(hourSegment,'position','static');
         }
      }
      // tmp container for clean array of fixed elements
      var newFixedHourSegments = [];
      for (var i=0,len = this._fixedHourSegments.length;i<len;i++)
      {
         var hourSegment = this._fixedHourSegments[i];
         var hasEvent = (Dom.getElementsByClassName('vevent','div',hourSegment).length>0);
         if (!hasEvent)
         {
            Dom.setStyle(hourSegment,'border-bottom-color','#eaeaea');
            Dom.setStyle(hourSegment,'border-bottom-width','1px');                  
         }
         else
         {
            newFixedHourSegments.push(hourSegment);
         }
      }
      this._fixedHourSegments = newFixedHourSegments;
   },

  /**
    * Render all day events
    * 
    * @method renderAllDayEvents
    * @param eventEl {object} reference to event element
    * @param data {object} Value object of event data
    * 
    */
   renderAllDayEvents : function(eventEl,data) 
   {
      YAHOO.util.Dom.generateId(eventEl);
      // put into all day section
      var targetEl = Dom.get('alldayCell');
      targetEl.appendChild(eventEl);

      Dom.addClass(eventEl,'allday');
      Dom.setStyle(eventEl,'width','100%');
      Dom.setStyle(eventEl,'height','auto');
      Dom.setStyle(eventEl,'top','auto');
      Dom.setStyle(eventEl,'left','auto');
     return eventEl;
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
   onEventEdited : function CalendarDayView_onEventEdited(e,o) 
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
      else {
       if(data.allday && data.allday!='false')
       {
         if (data.dtstart) { // have to convert
          data = this.convertDataToTemplateData(data);
         }
         data.contEl='div';
         data.hidden='';
         if (this.calendarView!==Alfresco.CalendarView.VIEWTYPE_AGENDA)
         {
            data.el='div';
         }
         else 
         {
            data.el = 'li';
            data.allday='allday';               
         }
         var days = data.duration.match(/([0-9]+)D/);
         if (days && days[1])
         {
            data.duration = data.duration.replace(/([0-9]+)D/,++days[1]+'D');
         }
         var currPar = Dom.getAncestorByTagName(eventEl,'div');//div            
         if (Dom.hasClass(eventEl,'allday'))
         {
            this.removeMultipleAllDayEvents(eventEl);
         }
         eventEl.parentNode.removeChild(eventEl);
   
         var eventEl = Alfresco.CalendarHelper.renderTemplate('vevent',data);         
         eventEl = this.renderAllDayEvents(eventEl,data);
         this.calEventConfig.draggable = false;
         this.calEventConfig.resizable = false;
         var newCalEvent = new Alfresco.calendarEvent(eventEl, this.dragGroup,YAHOO.lang.merge(this.calEventConfig,{performRender:false}));
         this.events[eventEl.id]=newCalEvent;
       }
       else 
       { 
         //move to correct cell
         Dom.removeClass(eventEl,'allday');
         id += 'T'+hour+':00';
         var index = ( (~~ (1 * min))>=30) ? 1 : 0;
         targetEl = Dom.get(id);
         targetEl = Dom.getElementsByClassName('hourSegment','div',targetEl)[index];

         Dom.setStyle(targetEl,'position','relative');
         targetEl.appendChild(eventEl);

         data.duration = Alfresco.CalendarHelper.getDuration(evDate, fromISO8601(data.dtend));
         this.calEventConfig.draggable = Dom.hasClass(eventEl,'allday') ? false : true;
         this.calEventConfig.resizable = (Dom.hasClass(eventEl,'allday')) ? false : true;
         this.events[eventEl.id] = new Alfresco.calendarEvent(eventEl, this.dragGroup,this.calEventConfig);
       }       
      }
      if (data.tags)
      {
       data.category=data.tags;          
      }

      this.events[eventEl.id].update(data);

      if (!Dom.hasClass(eventEl,'allday'))
      {
       this._adjustHeightByHour(eventEl);
      }
      this.renderMultipleEvents();
      YAHOO.Bubbling.fire("eventEditedAfter");
     
   },
   
   /**
    * Handler for when event is saved
    * 
    * @method onEventSaved
    * 
    * @param e {object} event object 
    */
   onEventSaved : function CalendarDayView_onEventSaved(e)
   {
      var data = YAHOO.lang.JSON.parse(e.serverResponse.responseText).event;

      var dtStartDate = Alfresco.util.fromISO8601(data.from+'T'+data.start);
      if (this.isValidDateForView(dtStartDate))
      {
       var dtEndDate = Alfresco.util.fromISO8601(data.to+'T'+data.end);
       data.duration = Alfresco.CalendarHelper.getDuration(dtStartDate,dtEndDate);       
       //tagname
       data.el = 'div';  
       //tag with enclosing brackets
       data.contEl = 'div';
       data.hidden ='';
       data.tags = data.tags.join(' ');
       data.allday = (YAHOO.lang.isUndefined(data.allday)) ? '' : data.allday;
       data.from = data.from +'T'+data.start;
       data.to = data.to +'T'+data.end;
       //get containing date TD cell for event
       var targetEl = Dom.get('cal-'+data.from.split(':')[0]+':00');
       //render into allday section
       if(data.allday)
       {
         data.allday = 'allday';
         var vEventEl = Alfresco.CalendarHelper.renderTemplate('vevent',data);
         vEventEl = this.renderAllDayEvents(vEventEl,data);
         this.calEventConfig.draggable = YAHOO.util.Dom.hasClass(vEventEl,'allday') ? false : true;
         this.calEventConfig.resizable = (Dom.hasClass(vEventEl,'allday')) ? false : true;
         var newCalEvent = new Alfresco.calendarEvent(vEventEl, this.dragGroup,YAHOO.lang.merge(this.calEventConfig,{performRender:false}));
         this.events[vEventEl.id]=newCalEvent;
       }
       else 
       {
         var vEventEl = Alfresco.CalendarHelper.renderTemplate('vevent',data);
         var min = data.from.split('T')[1].split(':')[1];
         var segments  = Dom.getElementsByClassName('hourSegment','div',targetEl);
         targetEl = ( (~~ (1* min))>=30) ? segments[1] : segments[0];
         YAHOO.util.Dom.setStyle(targetEl,'position','relative');
         if (min=='15' | min=='45')
         {
            Dom.setStyle(targetEl,'top',Dom.getRegion(targetEl).height/2+'px');
         }         
         targetEl.appendChild(vEventEl);
         this.calEventConfig.resizable = true;
         this.calEventConfig.draggable = true;
         var id = Event.generateId(vEventEl);
         var newCalEvent = new Alfresco.calendarEvent(vEventEl, this.dragGroup,YAHOO.lang.merge(this.calEventConfig,{performRender:false}));
         this.events[id]=newCalEvent;

         newCalEvent.on('eventMoved', this.onEventMoved, newCalEvent, this);
         if (!Dom.hasClass(vEventEl,'allday'))
         {
            this._adjustHeightByHour(vEventEl);
         }
         this.renderMultipleEvents();
         this._fixIEBorderBleedThrough(newCalEvent.getEl());
       }
      }
        
      YAHOO.Bubbling.fire("eventSavedAfter");
   },

   /**
    * Handler for when an event is deleted
    * 
    * @method  onEventDeleted
    *  
    */
   onEventDeleted : function CalendarDayView_onEventDeleted()
   {     
      var id = arguments[1][1].id;
      var currPar = Dom.getAncestorByTagName(this.events[id].getElement(),'div');//div
      var evt = this.events[id].getElement();
 
      this.events[id].deleteEvent();
      Event.purgeElement(this.events[id].getEl(),true);          
      delete this.events[id];
      this.renderMultipleEvents();
      YAHOO.Bubbling.fire("eventDeletedAfter");
   },
   
   /**
    * Handler for when an event is moved(dragged). Updates DOM with new event data
    *
    * @method onEventMoved
    * @param args {object} Event arguments
    * @param calEvent {object} CalendarEvent object - the moved event
    * 
    */
   onEventMoved : function CalendarDayView_onEventMoved(args,calEvent) 
   {

      var calEventEl = calEvent.getEl();
      var targetEl = arguments[0].targetEl || calEventEl;
      var timeReplace = /T([0-9]{2}):([0-9]{2})/;
      var dateReplace = /^([0-9]{4})-([0-9]{2})-([0-9]{2})/;

      this.currentDate = this.getClickedDate(targetEl);
      var date = toISO8601(this.currentDate);
      var newDtStart = calEvent.getData('dtstart');
      if (date !== null)
      {
         newDtStart = newDtStart.replace(dateReplace,date.split('T')[0]);
      }
      var hour = Alfresco.CalendarHelper.determineHourSegment(Dom.getRegion(calEventEl),targetEl);
      newDtStart = newDtStart.replace(timeReplace,'T'+hour);

      var newEndDate = Alfresco.CalendarHelper.getEndDate(newDtStart,calEvent.getData('duration',true));
      var duration = calEvent.getData('duration',true);
      calEvent.update({
         dtstart : newDtStart,
         dtend : newEndDate
      });
      
      if (args.dropped)
      {
         YAHOO.lang.later(0, this,this.updateEvent,calEvent);
         this.renderMultipleEvents();
         if (YAHOO.env.ua.ie)
         {
           this._fixIEBorderBleedThrough(calEventEl);
         }
      }
   },

   /**
    * Handler for when an event is resized
    * 
    * @method onEventResized
    * 
    * @param e {object} Event object
    * @param o {object} Event argument 
    */
   onEventResized : function CalendarDayView_onEventResized(e,o){
       this.updateEvent(o[1]);
   }                   
});
})();