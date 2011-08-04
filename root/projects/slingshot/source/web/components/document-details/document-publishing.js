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
 * Document Details Publishing History component.
 *
 * @namespace Alfresco
 * @class Alfresco.DocumentPublishing
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $userProfileLink = Alfresco.util.userProfileLink,
      $userAvatar = Alfresco.Share.userAvatar;

   /**
    * DocumentPublishing constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocumentPublishing} The new component instance
    * @constructor
    */
   Alfresco.DocumentPublishing = function DocumentPublishing_constructor(htmlId)
   {
      Alfresco.DocumentPublishing.superclass.constructor.call(this, "Alfresco.DocumentPublishing", htmlId, ["datasource", "datatable", "paginator", "history", "animation"]);

      YAHOO.Bubbling.on("metadataRefresh", this.doRefresh, this);
      return this;
   };

   YAHOO.extend(Alfresco.DocumentPublishing, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type {object} object literal
       */
      options:
      {
         /**
          * Reference to the current document
          *
          * @property nodeRef
          * @type string
          */
         nodeRef: null,

         /**
          * Current siteId, if any.
          *
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * The name of container that the node lives in, will be used when uploading new versions.
          *
          * @property containerId
          * @type string
          */
         containerId: null
      },

      /**
       * Fired by YUI when parent element is available for scripting
       *
       * @method onReady
       */
      onReady: function DocumentPublishing_onReady()
      {
         this.widgets.alfrescoDataTable = new Alfresco.util.DataTable(
         {
            dataSource:
            {
               url: Alfresco.constants.PROXY_URI + "api/publishing/" + this.options.nodeRef.replace("://", "/") + "/events",
               doBeforeParseData: this.bind(function(oRequest, oFullResponse)
               {
                  // Publish Events are returned in an array, starting with the earliest - we need to most recent first.
                  oFullResponse.data.reverse();
                  return oFullResponse
               })
            },
            dataTable:
            {
               container: this.id + "-publishing-events",
               columnDefinitions:
               [
                  { key: "publishingEvent", sortable: false, formatter: this.bind(this.renderCellPublishingEvent) }
               ],
               config:
               {
                  MSG_EMPTY: this.msg("message.noVersions")
               }
            }
         });
      },

      /**
       * Publishing Event renderer
       *
       * @method renderCellPublishingEvent
       */
      renderCellPublishingEvent: function DocumentPublishing_renderCellPublishingEvent(elCell, oRecord, oColumn, oData)
      {
         elCell.innerHTML = this.getDocumentPublishingEventMarkup(oRecord.getData());
      },

      /**
       * Builds and returns the markup for a version.
       *
       * @method getDocumentVersionMarkup
       * @param doc {Object} The details for the document
       */
      getDocumentPublishingEventMarkup: function DocumentPublishing_getDocumentPublishingEventMarkup(event)
      {
         
         // Set up the data we need.
         var nodeRef = this.options.nodeRef,
            node = "",
            nodeName = "",
            nodeLabel = "",
            channelIcon = Alfresco.constants.PROXY_URI + event.channel.channelType.icon + "/32",
            channelTitle = event.channel.channelType.title,
            channelName = event.channel.title,
            channelId = event.channel.id,
            statusType = this.msg("publishingHistory.status." + event.status),
            statusTime = Alfresco.util.relativeTime(event.scheduledTime.dateTime),
            statusDisplay = this.msg("publishingHistory.status.display", statusType, statusTime),
            statusIcon = Alfresco.constants.URL_RESCONTEXT + "components/document-details/images/status-"+ event.status + ".png",
            html = "",
            eventType = "";
            
         // Find the right node in the array of nodes that were published
         for (var i=0; i < event.publishNodes.length; i++) {
            node = event.publishNodes[i];
            //see if this published node is the correct one
            if (node.nodeRef === this.options.nodeRef) {
               nodeName = node.name;
               nodeLabel = node.version;
               eventType = this.msg("publishingHistory.event.published", channelName);
            }
         }
         
         // If we've not found a publish event, check unpublish ones too:
         if (nodeName !== "") 
         {   
            // Loop through unpublish events too:
            for (var i=0; i < event.unpublishNodes.length; i++) {
               node = event.unpublishNodes[i];
               //see if this node is the correct one
               if (node.nodeRef === this.options.nodeRef) {
                  nodeName = node.name;
                  nodeLabel = node.version;
                  eventType = this.msg("publishingHistory.event.unpublished", channelName);
               }
            }
         }
         
         // Check we have a matching node, then render the HTML for it.
         if (nodeName !== "") {
            html += '<div class="publishing-panel-left">'
            html += '   <span class="document-version">' + $html(nodeLabel) + '</span>';
            html += '</div>';
            html += '<div class="publishing-panel-right">';
            html += '   <h3 class="thin dark">' + $html(nodeName) +  '</h3>';         
            html += '   <span class="actions">';
            // Files can be unpublished only if the channel supports it AND they have successfully been published
            if (event.channel.channelType.canUnpublish && event.status === "COMPLETED") {
               html += '		<a href="#" name=".onUnpublishClick" rel="' + nodeRef + '" class="' + this.id + ' unpublish" title="' + this.msg("publishingHistory.action.unpublish") + '">&nbsp;</a>';
            }
            html += '   </span>';
            html += '   <div class="clear"></div>';
            html += '   <div class="channel-details">';
            html += '      <img src="' + $html(channelIcon) + '" alt="' + $html(eventType) + '" title="' + $html(eventType) + '"/><span class="channel-name">' + $html(eventType) + '<span>';
            html += '   </div>';
            html += '   <div class="status-details">';
            html += '      <img src="' + $html(statusIcon) + '" alt="' + $html(statusType) + '" title="' + $html(statusType) + '"/><span class="status">' + statusDisplay + '<span>';
            html += '   </div>';
            html += '</div>';
   
            html += '<div class="clear"></div>';
         }
         return html;
      },

      onUnpublishClick: function DocumentPublishing_onUnpublishClick(){
         console.log("unpublishing");
      },
      
      /**
       * Refresh component in response to metadataRefresh event
       *
       * @method doRefresh
       */
      doRefresh: function DocumentPublishing_doRefresh()
      {
         YAHOO.Bubbling.unsubscribe("metadataRefresh", this.doRefresh);
         // TODO Update this Refreshing URL.
         //this.refresh('components/document-details/document-versions?nodeRef={nodeRef}' + (this.options.siteId ? '&site={siteId}' :  ''));
      }
   });
})();
