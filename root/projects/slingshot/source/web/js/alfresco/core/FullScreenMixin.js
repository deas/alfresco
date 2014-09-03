/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
 * This module can be mixed into any other widget that has a requirement to make it's DOM element
 * either full-screen or full-window.
 * 
 * @module alfresco/core/FullScreenMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dojo/on",
        "dojo/dom-class",
        "dojo/has"], 
        function(declare, on, domClass, has) {
   
   return declare(null, {
      
      /**
       * The current full screen mode
       *
       * @instance
       * @type {boolean}
       * @default true
       */
      isWindowOnly: true,
      
      /**
       * Toggles full-screen mode for the current context element
       *
       * @instance
       */
      toggleFullScreen: function alfresco_core_FullScreenMixin__toggleFullScreen(isWindowOnly)
      {
         if (this.domNode != null)
         {
            if (!dojo.doc.fullscreen && !dojo.doc.mozFullScreen && !dojo.doc.webkitFullScreen)
            {
               this.requestFullScreen(isWindowOnly);
            }
            else
            {
               this.cancelFullScreen();
            }
         }
      },
      
      /**
       * Enters full-screen mode for the current context element
       *
       * @instance
       * @param {boolean} isWindowOnly Indicates whether to make the element the size of the window
       */
      requestFullScreen: function alfresco_core_FullScreenMixin__requestFullScreen(isWindowOnly)
      {
         if (isWindowOnly != null)
         {
            this.isWindowOnly = isWindowOnly;
         }
         if (this.isWindowOnly)
         {
            this.toggleFullWindow();
            return;
         }
         if (this.domNode.requestFullscreen || this.domNode.mozRequestFullScreen || this.domNode.webkitRequestFullScreen)
         {
            domClass.add(this.domNode, 'alf-fullscreen');
            domClass.add(this.domNode, 'alf-entering-true-fullscreen');
         }
         if (this.domNode.requestFullscreen)
         {
            this.domNode.requestFullscreen();
         }
         else if (this.domNode.mozRequestFullScreen)
         {
            this.domNode.mozRequestFullScreen();
         }
         else if (this.domNode.webkitRequestFullScreen)
         {
            // TODO Safari bug doesn't support keyboard input
            if (has("safari") && !has("chrome"))
            {
               this.domNode.webkitRequestFullScreen();
            }
            else
            {
               this.domNode.webkitRequestFullScreen(Element.ALLOW_KEYBOARD_INPUT);
            }
         }
         else
         {
            this.toggleFullWindow();
         }
      },
      
      /**
       * Exits full-screen mode for the current context element
       *
       * @instance
       */
      cancelFullScreen: function alfresco_core_FullScreenMixin__cancelFullScreen()
      {
         if (this.isWindowOnly)
         {
            this.toggleFullWindow();
            return;
         }
         if (dojo.doc.exitFullscreen)
         {
            dojo.doc.exitFullscreen();
         }
         else if (dojo.doc.mozCancelFullScreen)
         {
            dojo.doc.mozCancelFullScreen();
         }
         else if (dojo.doc.webkitCancelFullScreen)
         {
            dojo.doc.webkitCancelFullScreen();
         }
         else
         {
            this.toggleFullWindow();
         }
      },
      
      /**
       * Handles changes to the full screen mode
       *
       * @instance
       */
      onFullScreenChange: function alfresco_core_FullScreenMixin__onFullScreenChange()
      {
         if (this.domNode != null)
         {
            if (domClass.contains(this.domNode, 'alf-entering-true-fullscreen'))
            {
               domClass.remove(this.domNode, 'alf-entering-true-fullscreen');
               // Let resizing take place then add the true-fullscreen class
               var _this = this;
               setTimeout(function()
               {
                  domClass.add(_this.domNode, 'alf-true-fullscreen');
               }, 1000);
            }
            else
            {
               if (domClass.contains(this.domNode, 'alf-true-fullscreen'))
               {
                  if (domClass.contains(this.domNode, 'alf-fullscreen'))
                  {
                     // Exiting true fullscreen complete
                     domClass.remove(this.domNode, 'alf-fullscreen');
                     domClass.remove(this.domNode, 'alf-true-fullscreen');
                     this.onFullScreenExitComplete();
                  }
               }
               else
               {
                  // We've probably been programatically called in fullwindow mode
                  if (!domClass.contains(this.domNode, 'alf-fullscreen'))
                  {
                     domClass.add(this.domNode, 'alf-fullscreen');
                     this.onFullScreenEnterComplete();
                  }
                  else
                  {
                     domClass.remove(this.domNode, 'alf-fullscreen');
                     this.onFullScreenExitComplete();
                  }
               }
            }
         }
      },
      
      /** 
       * This function is called when entering fullscreen mode. It should be overridden by mixing modules
       * if they need to perform some post processing.
       *
       * @instance
       */
      onFullScreenEnterComplete: function alfresco_core_FullScreenMixin__onFullScreenEnterComplete() {
         // No action be default - extension point only.
      },

      /** 
       * This function is called when exiting fullscreen mode. It should be overridden by mixing modules
       * if they need to perform some post processing.
       *
       * @instance
       */
      onFullScreenExitComplete: function alfresco_core_FullScreenMixin__onFullScreenExitComplete() {
         // No action be default - extension point only.
      },

      /**
       * Toggles full-window mode for the current context element for browsers that don't support full-screen or
       * explicit setting of params.isWindowOnly=true.
       *
       * @instance
       */
      toggleFullWindow: function alfresco_core_FullScreenMixin__toggleFullWindow() {
         if (this.domNode != null)
         {
            if (!domClass.contains(this.domNode, 'alf-fullwindow'))
            {
               domClass.add(this.domNode, 'alf-fullwindow');
               var _this = this;

               // By using on.once the keyup capture will only occur once (i.e. the listener
               // is removed once the esc key has been used)...
               on.once(this.domNode, "keyup", function(evt) {
                  if (evt.keyCode == 27)
                  {
                     _this.toggleFullWindow();
                  }
               });
            }
            else
            {
               domClass.remove(this.domNode, 'alf-fullwindow');
            }
            this.onFullScreenChange();
         }
      }
   });
});