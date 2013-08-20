/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
 * A mixin class that provides utility functions for manipulating objects in the DOM.
 * 
 * @module alfresco/core/DomElementUtils
 * @author Dave Draper
 */
define(["dojo/_base/declare"], 
        function(declare) {
   
   /**
    * This class can be mixed into other classes to provide additional DOM element utility functions.
    */
   return declare(null, {
      
      /**
       * This function was taken from the following answer found on StackOverflow: 
       * http://stackoverflow.com/questions/3053542/how-to-get-the-start-and-end-points-of-selection-in-text-area/3053640#3053640
       * 
       * The purpose of this function is to return information on selection of text in an element.
       * 
       * @instance
       * @param {DOM Element} el The element to find the text selection for.
       * @returns {Object} An object with the attributes "start" and "end" that indicate the text selection.
       */
      getInputSelection: function(el) {
         var start = 0, end = 0, normalizedValue, range,
         textInputRange, len, endRange;

         if (typeof el.selectionStart == "number" && typeof el.selectionEnd == "number") 
         {
            start = el.selectionStart;
            end = el.selectionEnd;
         }
         else 
         {
            range = document.selection.createRange();
   
            if (range && range.parentElement() == el) 
            {
               len = el.value.length;
               normalizedValue = el.value.replace(/\r\n/g, "\n");
   
               // Create a working TextRange that lives only in the input
               textInputRange = el.createTextRange();
               textInputRange.moveToBookmark(range.getBookmark());
   
               // Check if the start and end of the selection are at the very end
               // of the input, since moveStart/moveEnd doesn't return what we want
               // in those cases
               endRange = el.createTextRange();
               endRange.collapse(false);
   
               if (textInputRange.compareEndPoints("StartToEnd", endRange) > -1) 
               {
                  start = end = len;
               } 
               else 
               {
                  start = -textInputRange.moveStart("character", -len);
                  start += normalizedValue.slice(0, start).split("\n").length - 1;
   
                  if (textInputRange.compareEndPoints("EndToEnd", endRange) > -1) 
                  {
                     end = len;
                  } 
                  else 
                  {
                     end = -textInputRange.moveEnd("character", -len);
                     end += normalizedValue.slice(0, end).split("\n").length - 1;
                  }
               }
            }
        }
   
        return {
            start: start,
            end: end
        };
      },
      
      /**
       * This function is based on the answer given to the following StackOverflow question:
       * http://stackoverflow.com/questions/1336585/howto-place-cursor-at-beginning-of-textarea
       * 
       * The purpose is to set the carat in the supplied element.
       * 
       * @instance
       * @param {DOM Element} el The element to set the carat position on
       * @param {number} position The index at which to set the carat
       */
      setCaretPosition: function(el, position) {
         if (el.setSelectionRange) 
         { 
             el.focus(); 
             el.setSelectionRange(position, position); 
         }
         else if (el.createTextRange) 
         { 
             var range = el.createTextRange();
             range.moveStart('character', position); 
             range.select(); 
         }
      }
   });
});
