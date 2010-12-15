/**
 * Alfresco.CalendarEvent
 * Represents an calendar event
 * 
 * @constructor
 * @subclass YAHOO.util.DD
 * 
 * @param id {String} Id of event element 
 * @param sGroup {String} Name of draggable group
 * @param config {object} Configuration object
 * 
 */
( function() 
{
   
var Dom = YAHOO.util.Dom,
    Event = YAHOO.util.Event,
    Selector = YAHOO.util.Selector,
    fromISO8601 = Alfresco.util.fromISO8601,
    toISO8601 = Alfresco.util.toISO8601,
    dateFormat = Alfresco.thirdparty.dateFormat;
        
Alfresco.calendarEvent = function Alfresco_CalendarEvent(id, sGroup, config) {
    this.el = YAHOO.util.Dom.get(id);
    if (config.draggable)
    {
      Alfresco.calendarEvent.superclass.constructor.apply(this, arguments);
      this.initDDBehaviour(id, sGroup, config);      
    }
    this.initEventData(id, (YAHOO.lang.isUndefined(config.performRender)) ? true : config.performRender ) ;
    this.initEvents();
    YAHOO.util.DDM.mode = YAHOO.util.DDM.INTERSECT; 

    if (config.resizable===true)
    {
      this.resize = new YAHOO.util.Resize(this.getEl(),{
          handles:['b'],
          hover:true,
          yTicks:config.resize.yTicks // half height of div.hourSegment
      });
      /**
       * Over large resize actions (or after multiple resizes), the bottom edge does quite line up correctly with the hour segments.
       * The handler divides the height of the resized element by the xTick value. This gives the number of
       * 'ticked' positions there are in the current height. This value is then divided by 2 (2 div.hourSegment per hour)
       * and finally by 5. This last number is added to the height which lines up correctly.
       * 
       * numOfTickPos = height/xTick
       * numOfHourSegments = numOfTickPos/2
       * delta = numOfHourSegments/5
       * (dividing by 5 seems to work best here)
       * (delta = numOfTickPos/10 uses one less division)
       */
      this.resize.on('resize',this.onResize,this,true);
      this.resize.on('endResize',function endResize(args){
      this.onResize(args);
      YAHOO.Bubbling.fire('eventResized',this);
     },this,true);
   }
};

YAHOO.extend(Alfresco.calendarEvent, YAHOO.util.DD, {
    /**
     *  Get event dom element
     */ 
     getElement : function calendarEvent_() {
       return this.el;
     },
    /**
     * Initialises custom events
     * 
     * @method initEvents 
     */
    initEvents : function calendarEvent_() {
       this.createEvent('eventMoved');
    },
    /**
     * Initialises event data by parsing the element's microformat information
     * 
     * @method initEventData
     * @param id {string} Id of element to use as source of event data
     * @param performRender {Boolean} Flag denoting whether to render data after parsing 
     */
    initEventData: function calendarEvent_initEventData(id,performRender) {
        this.eventData = new microformatParser(
                      {
                          ufSpec : hcalendar,
                          srcNode : id
                      });
        this.eventData.parse();

        if (performRender===true)
        {
            this.eventData.render();
        }
    },
 
    /**
     * Initialise drag and drop behaviour
     * 
     * @method initDDBehaviour
     *  
     * @param id {String} Id of event element 
     * @param sGroup {String} Name of draggable group
     * @param config {object} Configuration object 
     */
    initDDBehaviour: function calendarEvent_initDDBehaviour(id, sGroup, config) {
      if (!id) { 
          return; 
      }

      if (!(YAHOO.util.Dom.hasClass(this.getEl(),'allday')))
      {
          YAHOO.util.DDM.mode = YAHOO.util.DDM.INTERSECT; 
          var el = this.getDragEl();

          // specify that this is not currently a drop target
          this.isTarget = false;
   
      }
      this.initConstraints(YAHOO.util.Dom.getAncestorByTagName(el,'tbody'));
      if (this.config.yTick!==null)
      {
          this.setYTicks(0,this.config.yTick);            
      }
      if (this.config.xTick!==null)
      {
          this.setXTicks(0,this.config.xTick);
      }
    },
 
    /**
     * handler for startDrag event
     * 
     * @method startDrag
     * 
     */
    startDrag: function calendarEvent_startDrag(x, y) 
    {
      YAHOO.util.Dom.setStyle(this.getEl(),'z-index','99');
    },

    /**
     * Handler for endDrag event
     * 
     * @method endDrag 
     */
    endDrag: function calendarEvent_endDrag(e) {
      YAHOO.util.Dom.setStyle(this.getEl(),'z-index','1');
    },

    /**
     * Handler for dragDrop event
     * 
     * @method onDragDrop
     */
    onDragDrop: function calendarEvent_onDragDrop(e, id) 
    {
      // get the drag and drop object that was targeted
      var oDD;
      if ("string" == typeof id) 
      {
          oDD = YAHOO.util.DDM.getDDById(id);
      }
      else 
      {
          oDD = YAHOO.util.DDM.getBestMatch(id);
      }
      //elem that dragged el was dropped on
      var targetEl = oDD.getEl(); 
      var el = this.getEl();
      var draggedOutCellUl = el.parentNode;
      var currTd;
      if ( (YAHOO.util.Dom.hasClass(targetEl,'day')) )
      {
          currTd = targetEl;
          if (YAHOO.util.Dom.hasClass(el,'allday'))
          {
              targetEl.appendChild(el);
              //force a reparse as dom refs get out of sync
              this.eventData.parse(el.parentNode);
          }
          else {
            var ul = targetEl.getElementsByTagName('ul');
            var elUl = null;
            var dayHasExistingEvents = false;
            //day has no event so add ul
            if (ul.length === 0) {

                elUl = document.createElement('ul');
                elUl.className=el.parentNode.className;
                elUl = targetEl.appendChild(elUl);
            }
            // just add to existing ul
            else {
                dayHasExistingEvents = true;
                elUl = ul[0];
            }
            //if dragged onto different day
            if (elUl!==el.parentNode)
            {
              //make sure source UL shows all available events eg unhide (hidden)
              var dayEventsHidden = YAHOO.util.Dom.getElementsByClassName('hidden','li',el.parentNode);
              if (dayEventsHidden.length>0) 
              {
                  YAHOO.util.Dom.removeClass(dayEventsHidden[0],'hidden');
              }
              var checkIfOverThreshold = function (listEl)
              {
                var dayEvents = listEl.getElementsByTagName('li');
                var allDayEvents = YAHOO.util.Selector.query('div.allday',listEl.parentNode);                
                // var threshold = (numAllDayEvents) ? 4 - numAllDayEvents : 4;
                var targetHeight = YAHOO.util.Dom.getRegion(targetEl).height - (YAHOO.util.Dom.getRegion(dayEvents[0]).height*4);
                var numEventsHeight = ((dayEvents.length>0) ? YAHOO.util.Dom.getRegion(dayEvents[0]).height*dayEvents.length:0);
                //add alldayEvents height
                if (allDayEvents.length>0)
                {
                   numEventsHeight+=YAHOO.util.Dom.getRegion(allDayEvents[0]).height*allDayEvents.length;
                }
                return (numEventsHeight>=targetHeight);
              };
              //must sort and not insert after showmore
              if (dayHasExistingEvents)
              {
                var moreEventsTrigger = YAHOO.util.Dom.getElementsByClassName('moreEvents','li',elUl);                
                //add event as hidden
                if (checkIfOverThreshold(elUl))
                {
                    if (!YAHOO.util.Dom.hasClass(elUl,'showing'))
                    {
                        YAHOO.util.Dom.addClass(el,'hidden');
                    }
                    //check if more events link is in this cell and add before
                    if (moreEventsTrigger.length>0)
                    {
                      elUl.insertBefore(el,moreEventsTrigger[0]);
                    }
                    //otherwise add event and then create more events link
                    else
                    {
                      elUl.appendChild(el);
                      YAHOO.util.Dom.setStyle(el,'position','static');                      
                      elUl.innerHTML +='<li class="moreEvents"><a href="#" class="theme-color-1">'+Alfresco.util.message('label.show-more','Alfresco.CalendarView')+'</a></li>';
                    }
                }
                //just add as normal
                else {
                    elUl.appendChild(el);
                }
              }
              else {
                  elUl.appendChild(el);
              }
              //remove show more trigger link from dragged *out* cell
              var moreEventsTrigger = YAHOO.util.Dom.getElementsByClassName('moreEvents','li',draggedOutCellUl);                              
              if (moreEventsTrigger.length>0 && checkIfOverThreshold(draggedOutCellUl))
              {
                 YAHOO.util.Dom.removeClass(moreEventsTrigger[0].previousSibling, 'hidden');
                 draggedOutCellUl.removeChild(moreEventsTrigger[0]);
              }

              //force a reparse as dom refs get out of sync
              this.eventData.parse(el.parentNode);
            }
            YAHOO.util.Dom.setStyle(el,'position','static');
          }
       
      }
      if ( (YAHOO.util.Dom.hasClass(targetEl,'hourSegment')) )
      {
        currTd = YAHOO.util.Dom.getAncestorByTagName(el.parentNode,'td');

        targetEl = this.targetEl;
        if (targetEl)
        {
            var delta  =  YAHOO.util.Dom.getY(el)-YAHOO.util.Dom.getY(targetEl);
            //move el
            YAHOO.util.Dom.setStyle(targetEl,'position','relative');
            targetEl.appendChild(el);
            //reset to 0,0 origin
            YAHOO.util.DDM.moveToEl(el,targetEl);
            // if not dragged to top left pos move to delta
            if ((~~(1* delta))>1)
            {
                YAHOO.util.Dom.setStyle(el,'top',delta+'px');
            }
        }
        this.targetEl = targetEl;
      }
      this.fireEvent('eventMoved',{targetEl:this.targetEl,dropped:true,previousTargetEl:currTd});
    },
    swap: function calendarEvent_swap(el1, el2) 
    {
        var Dom = YAHOO.util.Dom;
        var pos1 = Dom.getXY(el1);
        var pos2 = Dom.getXY(el2);
        Dom.setXY(el1, pos2);
        Dom.setXY(el2, pos1);
    },

    /**
     * Handler for dragOver method
     * 
     * @method onDragOver 
     */
    onDragOver: function calendarEvent_onDragOver(e, id) 
    {
       if (this.config.view!==Alfresco.CalendarView.VIEWTYPE_MONTH)
       {
          //elem that dragged el was dropped on
          var targetEl = this.getBestMatch(id);

          if (targetEl)
          {
            //week and day view
            if ( (YAHOO.util.Dom.hasClass(targetEl,'hourSegment')) )
            {
                var el = this.getEl();
                //resize according to target's width and x coord
                YAHOO.util.Dom.setX(el,Math.max(0,(~~(1* YAHOO.util.Dom.getX(targetEl)) )));
            }
            this.targetEl = targetEl;
            this.fireEvent('eventMoved',{targetEl:this.targetEl,dropped:false});
          }          
       }
     },
 
    /**
     * Setup co-ordinates to constrain dragging behaviour. Contrains dragging 
     * to tbody element except for first two rows if in Day or Week view
     * 
     * @method initConstraints
     * @param constraintEl {object} Element to which to constrain dragging behaviour 
     */
    initConstraints : function calendarEvent_initConstraints(constraintEl) {
      var Dom = YAHOO.util.Dom;
      if (constraintEl)
      {
        //Get the top, right, bottom and left positions
        var region = Dom.getRegion(constraintEl);
        //Get the element we are working on
        var el = this.getEl();

        //Get the xy position of it
        var xy = Dom.getXY(el);

        //Get the width and height
        var elRegion = Dom.getRegion(el);
        var width = (~~(1* (elRegion.right-elRegion.left) ));
        var height = (~~(1* (elRegion.bottom-elRegion.top) ));
        //must not include allday and toggle rows
        if (this.config.view===Alfresco.CalendarView.VIEWTYPE_DAY | this.config.view===Alfresco.CalendarView.VIEWTYPE_WEEK)
        {
          var trRows= constraintEl.getElementsByTagName('tr');
          var alldayRegion = Dom.getRegion(trRows[0]);
          var toggleRegion = Dom.getRegion(trRows[1]);
          region.top+= (alldayRegion.bottom-alldayRegion.top) + (toggleRegion.bottom-toggleRegion.top);
        }
        //Set the constraints based on the above calculations
        this.setXConstraint(xy[0] - region.left,region.right - xy[0] - width);
        this.setYConstraint(xy[1] - region.top, region.bottom - xy[1] - height);
      }
    },
 
    /**
     * updates event data (and DOM) using microformat structure
     * 
     * @method update
     * @param vevent {object} Value object containing event data
     */ 
    update : function calendarEvent_update(vevent) {
      this.eventData.update(vevent,true);
    },
 
    /**
     * Gets the correct target based on top left position and area
     * 
     * @method getBestMatch
     * @param els {Array} Array of ids of elements to test
     * @return targetEl {object} Best matching HTML element
     */
    getBestMatch : function calendarEvent_getBestMatch(els) 
    {
        var range = 2;
        var area = 0;
        var targetEl = null;
        var top = YAHOO.util.Dom.getRegion(this.getEl()).top;
        for (var item in els)
        {
          var el = els[item];
          var overlap = el.overlap;

          if (overlap) 
          { 
            if ((overlap.top - top)<range)
            {
              if (overlap.getArea() > area) {
                   targetEl = el._domRef;
                   area = overlap.getArea();
              }
            }
          }
        }
        return targetEl;
    },
 
    /**
     * Returns specified event data. If no fieldName is passed then returns a dump
     * of all event data
     * 
     * @method getData
     * @param fieldName {String} Name of event to retrieve
     * @param parsedValue {Boolean} Flag to denote whether to return data as parsed or not 
     * 
     * @return {object} field value 
     */
    getData : function calendarEvent_getData(fieldName,parsedValue)
    {
        if (fieldName)
        {
            return this.eventData.get(fieldName,parsedValue);            
        }
        else {
            return this.eventData.getAll();
        }

    },
 
    /**
     * Change specified field of event data
     * 
     * @method setData
     * @param fieldName {String} Name of field to change 
     * @param value {object} value of field to change to
     */
    setData : function calendarEvent_setData(fieldName,value)
    {
        if (!YAHOO.lang.isUndefined(this.eventData[fieldName]))
        {
            this.eventData[fieldName] = value;
        }
    },
 
    /**
     * Deletes event from DOM
     * 
     * @method  deleteEvent
     *  
     */
    deleteEvent : function calendarEvent_deleteEvent() {
       this.el.parentNode.removeChild(this.el);
    },
 
    /**
     * Handler for onResize method
     * 
     * @method onResize
     * @param args {object} event argument object
     */    
    onResize : function calendarEvent_onResize(args)
    {
       var yTick = args.target.get('yTicks');
       this.delta = Math.ceil((args.height/yTick)/10);
       YAHOO.util.Dom.setStyle(args.target.getWrapEl(),'height',args.height+(this.delta*2)+'px');
       //get time
       var hours = args.height/args.target.get('yTicks')/4;
       var mins = hours*60;
       var duration = "PT"+ (~~(1* hours)) +'H'+mins%60+'M';
       var endDate = Alfresco.CalendarHelper.getEndDate(this.getData('dtstart'),hcalendar.parsers['duration'](duration));
       this.update({
          dtend : endDate,
          duration:duration
       });
    },
    /**
     * Shows event
     *
     */
    show : function calendarEvent_show()
    {
      var el = this.getElement();
      if (Dom.hasClass(this.el, 'allday'))
      {
         var multiples = Dom.getElementsByClassName(el.id+'-multiple', 'div');
         for (var i=0,len = multiples.length;i<len;i++)
         {
            Dom.setStyle(multiples[i],'display','');
         }
      }
      YAHOO.util.Dom.setStyle(el,'display','');
    },
    /**
     * Hides event
     *
     */
    hide : function calendarEvent_hide()
    {
      var el = this.getElement()
      if (Dom.hasClass(this.el, 'allday'))
      {
         var multiples = Dom.getElementsByClassName(el.id+'-multiple', 'div');
         for (var i=0,len = multiples.length;i<len;i++)
         {
            Dom.setStyle(multiples[i],'display','none');
         }
      }
      YAHOO.util.Dom.setStyle(el,'display','none');
    }
});

})();