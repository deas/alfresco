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
 * <p>This renderer is specifically designed report progress to the user.
 *
 * Interfaces:
 * External:
 * requestProgressTopic -> Passed in by config, used to request an update. (e.g. from Document Service)
 * progressRenderErrorTopic -> Passed in by config, published to let listeners know an error has occured.
 * progressRenderCompleteTopic -> passed in by config, indicates renderer has completed.
 * progressRenderCancelledTopic -> passed in by config, indicates renderer has been cancelled.
 *
 * Internal
 * progressUpdateTopic ->  Sent in payload of requestProgressTopic. Called when progress returns & updates progress dialog.
 * progressCompleteTopic -> Sent in payload of requestProgressTopic. Called when progress is finished.
 * progressCancelledTopic -> Sent in payload of requestProgressTopic. Called when action has been cancelled.
 * progressErrorTopic -> Sent in payload of requestProgressTopic. Called when there has been a fatal error. Progress has stopped and will not resume. Error status contained in payload.errorMessage.
 *
 * @module alfresco/renderers/Progress
 * @extends dijit/_WidgetBase
 * @author David Webster
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase",
        "dijit/_TemplatedMixin",
        "alfresco/core/Core",
        "dojo/text!./templates/Progress.html",
        "dojo/_base/lang",
        "dojo/dom-style"],
        function(declare, _WidgetBase, _TemplatedMixin, AlfCore, template, lang, domStyle) {

   return declare([_WidgetBase, AlfCore, _TemplatedMixin], {

      /**
       * An array of the i18n files to use with this widget.
       *
       * @instance
       * @type {object[]}
       * @default [{i18nFile: "./i18n/Progress.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/Progress.properties"}],

      /**
       * An array of the CSS files to use with this widget.
       *
       * @instance
       * @type {object[]}
       * @default [{cssFile:"./css/Progress.css"}]
       */
      cssRequirements: [{cssFile:"./css/Progress.css"}],

      /**
       * The HTML template to use for the widget.
       *
       * @instance
       * @type {String}
       */
      templateString: template,

      /**
       * renderProgressUI
       *
       * @instance
       * @type {String}
       */
      renderProgressUITopic: "ALF_PROGRESS_RENDER",

      /**
       * Sets up the form specific configuration for the dialog.
       *
       * @instance
       * @returns {object} The dialog configuration.
       */
      postCreate: function alfresco_renderers_Progress__postCreate() {

         // Subscribe to Update Progress Action.
         this.alfSubscribe("ALF_CLOSE_DIALOG", lang.hitch(this, this.cleanUp));
         this.alfSubscribe(this.renderProgressUITopic, lang.hitch(this, this.onRenderUI));

         // Kick off the initial progress request
         this.onRequestProgress();
      },

      /**
       * Called when progress returns & updates progress dialog.
       *
       * @instance
       */
      onRequestProgress: function alfresco_renderers_Progress__onRequestProgress() {
         this.alfLog("info", "Requesting progress");

         if (!this.requestProgressTopic) {
            this.alfLog("error", "I don't know where to request progress from: requestProgressTopic not set!" + this);
            return;
         }

         if (!this.nodes) {
            this.alfLog("error", "No nodes to request progress for. Please check you're setting 'nodes' in widget Config." + this.config);
            return;
         }

         var responseTopic = this.generateUuid(),
            progressUpdateTopic = responseTopic + "_update",
            progressCompleteTopic = responseTopic + "_complete",
            progressCancelledTopic = responseTopic + "_cancelled",
            progressErrorTopic = responseTopic + "_error",
            subscriptionListeners = [
               this.alfSubscribe(progressUpdateTopic, lang.hitch(this, this.onProgressUpdate)),
               this.alfSubscribe(progressCompleteTopic, lang.hitch(this, this.onProgressComplete)),
               this.alfSubscribe(progressCancelledTopic, lang.hitch(this, this.onProgressCancelled)),
               this.alfSubscribe(progressErrorTopic, lang.hitch(this, this.onProgressError))
            ],
            payload = {
               progressUpdateTopic: progressUpdateTopic,
               progressCompleteTopic: progressCompleteTopic,
               progressCancelledTopic: progressCancelledTopic,
               progressErrorTopic: progressErrorTopic,
               subscriptionListeners: subscriptionListeners,
               nodes: this.nodes
            };

         this.alfPublish(this.requestProgressTopic, payload);
      },

      /**
       * Called when progress returns & updates progress dialog.
       *
       * @instance
       * @param {object} payload
       */
      onProgressUpdate: function alfresco_renderers_Progress__onProgressUpdate(payload) {
         this.alfLog("debug", "Progress Dialog Update received: " + payload);

         this.alfPublish(this.renderProgressUITopic, payload);
      },

      /**
       * Called when progress is finished.
       *
       * @instance
       * @param {object} payload
       */
      onProgressComplete: function alfresco_renderers_Progress__onProgressComplete(payload) {
         this.alfLog("debug", "Progress Dialog Complete: " + payload);

         // Update the UI:
         this.displayUIMessage(this.message("renderer.progress.complete"));
         this.updateProgressBar(0);

         // Trigger the progressFinishedTopic
         if (this.progressFinishedTopic) {
            this.alfPublish(this.progressFinishedTopic, payload);
         }

         this.alfPublish("ALF_CLOSE_DIALOG", payload, true);
      },

      /**
       * Called when action has been cancelled.
       *
       * @instance
       * @param {object} payload
       */
      onProgressCancelled: function alfresco_renderers_Progress__onProgressCancelled(payload) {
         this.alfLog("debug", "Progress Dialog Cancelled: " + payload);

         this.alfPublish("ALF_CLOSE_DIALOG", payload, true);
      },

      /**
       * Called when there has been a fatal error. Progress has stopped and will not resume.
       * Error status contained in payload.errorMessage.
       *
       * @instance
       * @param {object} payload
       */
      onProgressError: function alfresco_renderers_Progress__onProgressError(payload) {
         this.alfLog("debug", "Progress Dialog Error: " + payload);

         this.displayUIMessage(this.message("renderer.progress.error"));
      },

      /**
       * Called to update the UI with the data.
       * @param payload
       */
      onRenderUI: function alfresco_renderers_Progress_onRenderUI(payload) {
         var done = parseInt(payload.response.done, 10),
            total = parseInt(payload.response.total, 10),
            filesAdded = parseInt(payload.response.filesAdded, 10),
            totalFiles = parseInt(payload.response.totalFiles, 10);

         if (!total || !done || !filesAdded || !totalFiles)
         {
            this.alfLog("error", "Missing required data")
         }

         var progressPercentage = (total > 0) ? Math.round(done / total * 100) : 0,
            percentageRemaining = 100 - progressPercentage;

         this.alfLog("info", "progress: "+ progressPercentage);

         // check DOM nodes:
         if (!this.labelNode || !this.progressNode)
         {
            this.alfLog("error", "");
         }

         // Write to Dom:
         var messageVars = {
            0: filesAdded,
            1: totalFiles
         };
         this.displayUIMessage(this.message("renderer.progress.status", messageVars));

         // Set style on progress bar:
         this.updateProgressBar(percentageRemaining);

      },

      /**
       * Display the specified message in the dialog
       *
       * @instance
       * @param message
       */
      displayUIMessage: function alfresco_renderers_Progress__displayUIMessage(message) {
         this.alfLog("debug", "Progress message: " + message);
         if (this.labelNode) {
            this.labelNode.innerHTML = message;
         }
      },

      updateProgressBar: function alfresco_renderers_Progress__updateProgressBar(percentageRemaining) {
         domStyle.set(this.progressNode, "left", "-" + percentageRemaining + "%");
      },

      cleanUp: function alfresco_renderers_Progress__cleanUp(payload) {
         this.cleanProgressListeners(payload);
      },

      /**
       *
       * @param payload
       */
      cleanProgressListeners: function alfresco_renderers_Progress__cleanProgressListeners(payload) {

         if (!payload.subscriptionListeners)
         {
            this.alfLog("error", "No subscription listeners to unsubscribe from");
            return;
         }

         this.alfUnsubscribe(payload.subscriptionListeners);
      }

   });
});