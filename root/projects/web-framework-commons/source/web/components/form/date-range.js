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
 * DateRange component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DateRange
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
   var $html = Alfresco.util.encodeHTML;

   /**
    * DateRange constructor.
    * 
    * @param {String} htmlId The HTML id of the control element
    * @param {String} valueHtmlId The HTML id prefix of the value elements
    * @return {Alfresco.DateRange} The new DateRange instance
    * @constructor
    */
   Alfresco.DateRange = function(htmlId, valueHtmlId)
   {
      Alfresco.DateRange.superclass.constructor.call(this, "Alfresco.DateRange", htmlId, ["button", "calendar"]);
      
      this.valueHtmlId = valueHtmlId;
      this.currentFromDate = "";
      this.currentToDate = "";
      
      return this;
   };
   
   YAHOO.extend(Alfresco.DateRange, Alfresco.component.Base,
   {
      /**
       * Current From date value
       * 
       * @property currentFromDate
       * @type string
       */
      currentFromDate: null,
      
      /**
       * Current To date value
       * 
       * @property currentToDate
       * @type string
       */
      currentToDate: null,
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DateRange_onReady()
      {
         var toDate = new Date();
         var fromDate = new Date();
         fromDate.setMonth(toDate.getMonth() - 1);
         
         // construct the pickers
         var page = (fromDate.getMonth() + 1) + "/" + fromDate.getFullYear();
         var selected = (fromDate.getMonth() + 1) + "/" + fromDate.getDate() + "/" + fromDate.getFullYear();   
         this.widgets.calendarFrom = new YAHOO.widget.Calendar(this.id + "-from", this.id + "-from", { title:this.msg("form.control.date-picker.choose"), close:true });
         this.widgets.calendarFrom.cfg.setProperty("pagedate", page);
         this.widgets.calendarFrom.cfg.setProperty("selected", selected);
         Alfresco.util.calI18nParams(this.widgets.calendarFrom);
         page = (toDate.getMonth() + 1) + "/" + toDate.getFullYear();
         selected = (toDate.getMonth() + 1) + "/" + toDate.getDate() + "/" + toDate.getFullYear();   
         this.widgets.calendarTo = new YAHOO.widget.Calendar(this.id + "-to", this.id + "-to", { title:this.msg("form.control.date-picker.choose"), close:true });
         this.widgets.calendarTo.cfg.setProperty("pagedate", page);
         this.widgets.calendarTo.cfg.setProperty("selected", selected);
         Alfresco.util.calI18nParams(this.widgets.calendarTo);
         
         // setup events
         this.widgets.calendarFrom.selectEvent.subscribe(this._handlePickerChangeFrom, this, true);
         Event.addListener(this.id + "-date-from", "keyup", this._handleFieldChangeFrom, this, true);
         Event.addListener(this.id + "-icon-from", "click", this._showPickerFrom, this, true);
         this.widgets.calendarTo.selectEvent.subscribe(this._handlePickerChangeTo, this, true);
         Event.addListener(this.id + "-date-to", "keyup", this._handleFieldChangeTo, this, true);
         Event.addListener(this.id + "-icon-to", "click", this._showPickerTo, this, true);
         
         // render the calendar controls
         this.widgets.calendarFrom.render();
         this.widgets.calendarTo.render();
      },
      
      /**
       * Handles the date picker icon being clicked.
       * 
       * @method _showPickerFrom
       * @param event The event that occurred
       * @private
       */
      _showPickerFrom: function DateRange__showPickerFrom(event)
      {
         this.widgets.calendarFrom.show();
      },
      
      /**
       * Handles the date picker icon being clicked.
       * 
       * @method _showPickerTo
       * @param event The event that occurred
       * @private
       */
      _showPickerTo: function DateRange__showPickerTo(event)
      {
         this.widgets.calendarTo.show();
      },
      
      /**
       * Handles the from date being changed in the date picker YUI control.
       * 
       * @method _handlePickerChangeFrom
       * @param type
       * @param args
       * @param obj
       * @private
       */
      _handlePickerChangeFrom: function DateRange__handlePickerChangeFrom(type, args, obj)
      {
         // update the date field
         var selected = args[0];
         var selDate = this.widgets.calendarFrom.toDate(selected[0]);
         var dateEntry = selDate.toString(this.msg("form.control.date-picker.entry.date.format"));
         Dom.get(this.id + "-date-from").value = dateEntry;
         
         // if we have a valid date, convert to ISO format and set value on hidden field
         if (selDate != null)
         {
            var isoValue = Alfresco.util.toISO8601(selDate, {"milliseconds":false});
            this.currentFromDate = isoValue;
            this._updateCurrentValue();
            
            Dom.removeClass(this.id + "-date-from", "invalid");
         }
         
         // hide the popup calendar
         this.widgets.calendarFrom.hide();
      },
      
      /**
       * Handles the from date being changed in the date picker YUI control.
       * 
       * @method _handlePickerChangeTo
       * @param type
       * @param args
       * @param obj
       * @private
       */
      _handlePickerChangeTo: function DateRange__handlePickerChangeTo(type, args, obj)
      {
         // update the date field
         var selected = args[0];
         var selDate = this.widgets.calendarTo.toDate(selected[0]);
         var dateEntry = selDate.toString(this.msg("form.control.date-picker.entry.date.format"));
         Dom.get(this.id + "-date-to").value = dateEntry;
         
         // if we have a valid date, convert to ISO format and set value on hidden field
         if (selDate != null)
         {
            var isoValue = Alfresco.util.toISO8601(selDate, {"milliseconds":false});
            this.currentToDate = isoValue;
            this._updateCurrentValue();
            
            Dom.removeClass(this.id + "-date-to", "invalid");
         }
         
         // hide the popup calendar
         this.widgets.calendarTo.hide();
      },
      
      /**
       * Updates the currently stored date range value in the hidden form field.
       * 
       * @method _updateCurrentValue
       * @private
       */
      _updateCurrentValue: function DateRange__updateCurrentValue()
      {
         Dom.get(this.valueHtmlId).value = this.currentFromDate + "|" + this.currentToDate;
      },
      
      /**
       * Handles the date or time being changed in either input field.
       * 
       * @method _handleFieldChangeFrom
       * @param event The event that occurred
       * @private
       */
      _handleFieldChangeFrom: function DateRange__handleFieldChangeFrom(event)
      {
         var changedDate = Dom.get(this.id + "-date-from").value;
         if (changedDate.length > 0)
         {
            // convert to format expected by YUI
            var parsedDate = Date.parseExact(changedDate, this.msg("form.control.date-picker.entry.date.format"));
            if (parsedDate != null)
            {
               this.widgets.calendarFrom.select((parsedDate.getMonth() + 1) + "/" + parsedDate.getDate() + "/" + parsedDate.getFullYear());
               var selectedDates = this.widgets.calendarFrom.getSelectedDates();
               if (selectedDates.length > 0)
               {
                  Dom.removeClass(this.id + "-date-from", "invalid");
                  var firstDate = selectedDates[0];
                  this.widgets.calendarFrom.cfg.setProperty("pagedate", (firstDate.getMonth()+1) + "/" + firstDate.getFullYear());
                  this.widgets.calendarFrom.render();
               }
            }
            else
            {
               Dom.addClass(this.id + "-date-from", "invalid");
            }
         }
         else
         {
            // when the date is completely cleared remove the hidden field and remove the invalid class
            Dom.removeClass(this.id + "-date-from", "invalid");
            Dom.get(this.valueHtmlId + "-from").value = "";
         }
      },
      
      /**
       * Handles the date or time being changed in either input field.
       * 
       * @method _handleFieldChangeFrom
       * @param event The event that occurred
       * @private
       */
      _handleFieldChangeTo: function DateRange__handleFieldChangeTo(event)
      {
         var changedDate = Dom.get(this.id + "-date-to").value;
         if (changedDate.length > 0)
         {
            // convert to format expected by YUI
            var parsedDate = Date.parseExact(changedDate, this.msg("form.control.date-picker.entry.date.format"));
            if (parsedDate != null)
            {
               this.widgets.calendarTo.select((parsedDate.getMonth() + 1) + "/" + parsedDate.getDate() + "/" + parsedDate.getFullYear());
               var selectedDates = this.widgets.calendarTo.getSelectedDates();
               if (selectedDates.length > 0)
               {
                  Dom.removeClass(this.id + "-date-to", "invalid");
                  var firstDate = selectedDates[0];
                  this.widgets.calendarTo.cfg.setProperty("pagedate", (firstDate.getMonth()+1) + "/" + firstDate.getFullYear());
                  this.widgets.calendarTo.render();
               }
            }
            else
            {
               Dom.addClass(this.id + "-date-to", "invalid");
            }
         }
         else
         {
            // when the date is completely cleared remove the hidden field and remove the invalid class
            Dom.removeClass(this.id + "-date-to", "invalid");
            Dom.get(this.valueHtmlId + "-to").value = "";
         }
      }
   });
})();