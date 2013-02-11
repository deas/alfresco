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
define(["dojo/_base/declare",
        "alfresco/core/WrappedShareWidget", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/MyActivities.html",
        "dojo/dom-construct"], 
        function(declare, WrappedShareWidget, _TemplatedMixin, template, domConstruct) {
   
   return declare([WrappedShareWidget, _TemplatedMixin], {

      /**
       * The CSS file referenced by the activities WebScript
       */
      cssRequirements: [{cssFile:"../../../../components/dashlets/activities.css"}],
      
      /**
       * Set the name as that of the dashlet from Share. This then ensures that the correct scope is set so that the 
       * messages can be retrieved as usual. 
       */
      i18nScope: "Alfresco.dashlets.Activities",
             
      /**
       * Specifies the properties file from the WebScript that is used to instantiate the widget. It's only necessary
       * to specify the default property file - the Dojo dependency handler will sort out the locale as necessary
       */
      i18nRequirements: [{i18nFile: "../../../../WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/dashlets/my-activities.get.properties"}],
      
      templateString: template,
      
      /**
       * The JavaScript file referenced by the activities WebScript
       */
      dependencies: [Alfresco.constants.URL_RESCONTEXT + "components/dashlets/activities.js"],
                     
      site: "",
      
      templateMessages: null,
      
      /**
       * The constructor is extended so that we can construct an object containing all the i18n properties to 
       * be substituted by the template. This is because the template can't call functions to obtain data.
       * There are potentially better ways of doing this - but it does at least work.
       */
      constructor: function(args) {
         declare.safeMixin(this, args);
         
         var filter = {
            mine: this.message("filter.mine"),
            others: this.message("filter.others"),
            all: this.message("filter.all"),
            following: this.message("filter.following"),
            allItems: this.message("filter.allItems"),
            statusItems: this.message("filter.statusItems"),
            commentItems: this.message("filter.commentItems"),
            contentItems: this.message("filter.contentItems"),
            membershipItems: this.message("filter.membershipItems"),
            today: this.message("filter.today"),
            _7days: this.message("filter.7days"),
            _14days: this.message("filter.14days"),
            _28days: this.message("filter.28days")
         };
         
         var empty = {
            title: this.message("empty.title"),
            description: this.message("empty.description")
         };

         this.templateMessages = {
            header: this.message("header"),
            filter: filter,
            empty: empty
         };
      },
      
      /**
       * Creates the Share widgets.
       */
      createWidget: function(me) {
         
         var activities = new Alfresco.dashlet.Activities(me.id).setOptions({
            siteId: "",
            mode: "user",
            regionId: me.id
         });
         
         Alfresco.constants.DASHLET_RESIZE = true; // This is a temporary hack to get the resizer to appear - it's supposed to be based on user criteria
         var resizer = new Alfresco.widget.DashletResizer(me.id, "${instance.object.id}");

         var activitiesFeedDashletEvent = new YAHOO.util.CustomEvent("openFeedClick");
         activitiesFeedDashletEvent.subscribe(activities.openFeedLink, activities, true);

         var titleBarActions = new Alfresco.widget.DashletTitleBarActions(me.id).setOptions({
            actions:
            [
               {
                  cssClass: "rss",
                  eventOnClick: activitiesFeedDashletEvent,
                  tooltip: "dashlet.rss.tooltip"
               },
               {
                  cssClass: "help",
                  bubbleOnClick:
                  {
                     message: "dashlet.help"
                  },
                  tooltip: "dashlet.help.tooltip"
               }
            ]
         });
         
         // It's necessary to call the onReady() actions of all the widgets created...
         activities.onReady();
         resizer.onReady();
         titleBarActions.onReady();
      }
   });
});