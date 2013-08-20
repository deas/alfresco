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
 * @module alfresco/header/UserStatus
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes dijit/_FocusMixin
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase",
        "dijit/_TemplatedMixin",
        "dijit/_FocusMixin",
        "dojo/text!./templates/UserStatus.html",
        "alfresco/core/Core",
        "dijit/form/Textarea",
        "dojo/on",
        "dojo/_base/event",
        "dojo/request/xhr",
        "dojo/keys",
        "dijit/focus",
        "dojo/_base/array",
        "dijit/registry",
        "dojo/dom-style"], 
        function(declare, _WidgetBase, _TemplatedMixin, _FocusMixin, template,  AlfCore, Textarea, on, event, xhr, keys, focusUtil, array, registry, domStyle) {
   
   return declare([_WidgetBase, _TemplatedMixin, _FocusMixin, AlfCore], {
      
      /**
       * The scope to use for i18n messages.
       * 
       * @instance
       * @type {string}
       */
      i18nScope: "org.alfresco.UserStatus",
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {{cssFile: string, media: string}[]}
       * @default [{cssFile:"./css/UserStatus.css"}]
       */
      cssRequirements: [{cssFile:"./css/UserStatus.css"}],
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {{i18nFile: string}[]}
       * @default [{i18nFile: "./i18n/UserStatus.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/UserStatus.properties"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {string}
       */
      templateString: template,

      /**
       * This will be intialised to reference a TextArea widget by the postCreate function.
       * 
       * @instance
       * @type {object}
       */
      _userStatusWidget: null,
      
      /**
       * This indicates whether or not the current user status is known. It is initialised to null but will be set
       * with a boolean value during the postCreate function to indicate whether or not a status was passed as an
       * argument. This could be useful when making a decision on how the status is displayed.
       * 
       * @instance
       * @type {boolean}
       */
      unknownStatus: null,
      /**
       * This represents the current user status. It should be provided with a value when the widget is instantiated.
       * 
       * @instance
       * @type {string}
       */
      userStatus: "",
      
      /**
       * This represents the time of the last user status update. It should be provided with a value when the widget
       * is instantiated.
       * 
       * @instance
       * @type {string}
       */
      userStatusTime: null,
      
      /**
       * When the _onFocus function is called this will be set as an array of all the widgets with popups that have been
       * locked. This array is maintained so that the _onBlur function can iterate through it to unlock all the widgets
       * when the user moves focus away from the UserStatus widget. 
       * 
       * @instance
       * @type {array}
       */
      _popupLocks: null,
      
      /**
       * The focus function is implemented here to support accessibility keyboard handling. When the UserStatus widget
       * gains focus it needs to ensure that the wrapped dijit/form/Textarea widget is given focus (i.e. that the cursor
       * is placed inside the text area read for typing).
       * 
       * @instance
       */
      focus: function alf_header_UserStatus__focus() {
         this.alfLog("log", "User Status focus");
         if (this._userStatusWidget)
         {
            this._userStatusWidget.focus();
         }
      },
      
      /**
       * Overrides the default versions provided by the dijit/_FocusMixin module to prevent all the widgets in the current
       * active stack from closing any popups that they have open. This implementation has been implemented as a compromise
       * to address the problem of the UserStatus widget being used within a menu item in a popup drop down list (which
       * was the use case for its implementation). 
       * 
       * Without the locks being imposed (specifically on the root dijit/MenuBar) the popup would be closed after the user
       * entered text in the status dijit/form/Textarea but BEFORE they were able to click the "post" button or even when
       * the MenuBar popup label was hovered over. It was not possible to manipulate the events to prevent this from 
       * happening so this was the best available option. It does rely on at least one widget in the active stack from 
       * implementing a "lockPopupsOpen" function to achieve the desired effect (the corresponding function can be found
       * in alfresco/menus/AlfMenuBar).
       * 
       * @instance
       */
      _onFocus: function alf_header_UserStatus___onFocus(){
         var _this = this;
         this.alfLog("log", "UserStatus Focus - LOCK");
         this._popupLocks = [];
         array.forEach(focusUtil.activeStack, function(entry, index) {
            var widget = registry.byId(entry);
            if (typeof widget.lockPopupsOpen == "function")
            {
               _this.alfLog("log", "Locking popups of ", widget);
               widget.lockPopupsOpen(true);
               _this._popupLocks.push(widget);
            }
         });
         this.inherited(arguments);
      },
      
      /**
       * Overrides the default versions provided by the dijit/_FocusMixin module to allow all the widgets in the previous
       * active stack to close any popups that they have open. See the documentation for the _onFocus function for further
       * details.
       * 
       * @instance
       */
      _onBlur: function alf_header_UserStatus___onBlur(){
         var _this = this;
         this.alfLog("log", "UserStatus Blur - UNLOCK");
         array.forEach(this._popupLocks, function(widget, index) {
            if (typeof widget.lockPopupsOpen == "function")
            {
               _this.alfLog("log", "Unlocking popups of ", widget);
               widget.lockPopupsOpen(false);
            }
         });
         this.inherited(arguments);
      },
      
      /**
       * 
       * @instance
       */
      postCreate: function alf_header_UserStatus__postCreate() {
         var _this = this;

         // Subscribe to user status updates...
         // This ensures that user status updates can be updated from events other than the user just entering
         // data into the status box...
         this.alfSubscribe("ALF_USER_STATUS_UPDATED", function(payload) {
            _this.statusUpdated(payload);
         });
         
         // Check that there is a valid user status and set it appropriately if not...
         this.unknownStatus = (this.userStatus == "");
         if (this.unknownStatus)
         {
            this.userStatus = this.message("unknown.status.label");
         }
         
         // Set the current status...
         this._userStatusWidget = new Textarea({
            value: this.userStatus
         });
         this._userStatusWidget.placeAt(this.focusNode);

         // Make a guess at a decent size of the text area... although the widget does offer the ability
         // to resize itself, it relies on it being visible (which when the menu is created it won't be).
         // Therefore we'll make a decent guess as to a sensible height. There is almost certainly a better
         // way of doing this - the best way would be to trigger a resize when the status widget is visible
         var height = 3;
         if (this.userStatus != null)
         {
            height = Math.ceil(this.userStatus.length/30);
         }
         domStyle.set(this._userStatusWidget.domNode, "height", (height * 25) +  "px");
         
         // Set the relative time (the time supplied should be in ISO8061 standard)...
         this.setStatusRelativeTime();
         
         // Set the labels for the status bar and the post button...
         //this._titleNode.innerHTML = this.message("status.label");
         this._postButtonNode.innerHTML = this.message("post.button.label");

         /* NOTE: We have intentionally not used a Dojo button here because we need to control the
          * events that bubble. Although possible it is challenging to prevent the internal operation
          * on the button node that occurs when clicking the Dojo button.
          */
         on(this._postButtonNode, "click", function(evt) {
            _this.postStatus(evt);
         });
         
         // It's important to handle clicks in the text area correctly.
         this._userStatusWidget.on("click", function(evt) {
            _this.onUserStatusClick(evt);
         });
         
         // It's important to handle clicks in the text area correctly.
         this._userStatusWidget.on("blur", function(evt) {
            _this.onUserStatusBlur(evt);
         });

         
         /* The following extract of code probably warrants some explanation !!! :)
          * The UserStatus widget was created to be included in a drop down menu from the header bar. However,
          * it's requires that the user is able to enter spaces and carriage returns. Unfortunately, these keyboard
          * events are captured for accessibility purposes to allow the UI to be driven entirely by the keyboard. Both
          * "space" and "carriage return" are considered control characters for selecting menu items. This particular
          * piece of code can be found in the dijit/a11yClick module. The keydown handler was calling preventDefault
          * on the event stopping the character code from being applied to the underlying textarea node. Fortunately
          * we are able to process the keydown event BEFORE a11yClick and swap out its preventDefault function for
          * one that does nothing. This allows the event to pass through to the node and the character is entered.
          */ 
         on(this._userStatusWidget.textbox, "keydown", function(evt) {
            _this.alfLog("log", "User Status KeyDown");
            var swallowPreventDefault = function() {
               // No action required.
            };
            if (evt.keyCode == keys.SPACE)
            {
               // Stop the event from being stopped...
               evt.preventDefault = swallowPreventDefault; 
            }
         });
         
         /* ...similar to the previous connection we want to capture enter key presses and post them. The keyup
          * event is captured by a11yClick and used to generate a click event. So we need to stop the event
          * to prevent the click occurring. Not stopping the event results in the textarea being refocused 
          * which appears as though the user has clicked in the status box and the status would be cleared (i.e.
          * you wouldn't be able to see the status you just posted!). Similarly we need to prevent space keyup
          * events doing the same thing!
          */
         on(this._userStatusWidget.textbox, "keyup", function(evt) {
            // Post the status on enter key presses...
            _this.alfLog("log", "User Status Keyup");
            if (evt.keyCode == keys.ENTER)
            {
               _this.postStatus(evt);
               event.stop(evt);
            }
            else if (evt.keyCode == keys.SPACE)
            {
               event.stop(evt);
            }
         });
      },
      
      /**
       * This function was originally copied from header.js. PLEASE NOTE: That it still uses the Alfresco.util.relativeTime function which 
       * will be available in Share for the considerable future but at some point this function will need to be ported to the Dojo framework.
       * 
       * @instance
       */
      setStatusRelativeTime: function alf_header_UserStatus__setStatusRelativeTime()
      {
         var relativeTime = (this.unknownStatus) ? this.message("status.never-updated") : this.getRelativeTime(this.userStatusTime);
         this._lastUpdateNode.innerHTML = this.message("status.updated", [relativeTime]);
      },
      
      /**
       * Called when the user clicks on the post button.
       * 
       * @instance
       * @param {object} evt The click event
       */
      postStatus: function alf_header_UserStatus__postStatus(evt) {
         
         // Reset the click count (that prevents the user from inadvertently deleting half entered status messages)
         // and post and event to the user service to update the status...
         this.alfPublish("ALF_UPDATE_USER_STATUS", {
            status: this._userStatusWidget.get("value")
         });
      },

      /**
       * This is the handler called when "ALF_USER_STATUS_UPDATED" topics are published. It retrieves
       * the new status (if available) and status update time (if available) and displays them in 
       * the widget.
       *
       * @instance
       * @param {object} payload
       */
      statusUpdated: function alf_header_UserStatus__statusUpdated(payload)
      {
         // Reset the click status...
         this._userStatusClickedOnce = false;
         
         // Update the user status if provided in the publication payload...
         if (payload.userStatus)
         {
            this._userStatusWidget.set("value", payload.userStatus);
            this.userStatus = payload.userStatus;
            this.unknownStatus = false;
         }
         else
         {
            // Update the local status based on the value of the widget. Even if it hasn't been
            // set (because there was no value in the payload). Then we need to assume that
            // the status was updated by this widget. 
            this.userStatus = this._userStatusWidget.get("value");
         }
         
         // Update the user status update time if provided in the publication payload...
         if (payload.userStatusTime)
         {
            this.userStatusTime = payload.userStatusTime;
            this.setStatusRelativeTime();
         }
         // Display a success message...
         this.displayMessage(this.message("message.status.success"));
      },
      
      /**
       * This boolean value determines whether or not the user has clicked in the status box. It is set to true
       * the first time the user clicks inside the status box and is reset when they move away from it without
       * entering any data or after they post a new status message.
       * 
       * @instance
       * @type _userStatusClickedOnce {boolean}
       */
      _userStatusClickedOnce: false,
      
      /**
       * Called when the user clicks inside the text area to set some status. It's important that we ensure that
       * the click event is propagated no further otherwise it will result in a surrounding menu closing.
       * 
       * @instance
       * @param {object} evt The click event.
       */
      onUserStatusClick: function alf_header_UserStatus__onUserStatusClick(evt) {
         if (this._userStatusClickedOnce)
         {
            // Don't clear the previous status text if the user has already clicked in the text area once without
            // posting the entered status.
            // No action required.
         }
         else
         {
            // Clear the previous status the first time that the user clicks in the text area...
            this._userStatusWidget.set("value", "");
            this._userStatusClickedOnce = true;
         }
      },
      
      /**
       * When the user moves away from the user status text box we need to make a decision on whether or not
       * to reset their status message. The deciding factor is whether or not they have made any contributions
       * towards entering a new status. If they haven't then the status is reset as is the click counter.
       * 
       * @instance
       * @param {object} evt The blur event.
       */
      onUserStatusBlur: function alf_header_UserStatus__onUserStatusBlur(evt) {
         this.alfLog("log", "User Status Blur");
         if (this._userStatusWidget.get("value").length == 0)
         {
            // If the user has not entered any data then reset the status...
            this._userStatusWidget.set("value", this.userStatus);
            this._userStatusClickedOnce = false;
         }
         else
         {
            // No action required.
         }
      }
   });
});