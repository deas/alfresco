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
        "dojo/text!./templates/DocumentList.html",
        "dojo/dom-construct"], 
        function(declare, WrappedShareWidget, _TemplatedMixin, template, domConstruct) {
   
   return declare([WrappedShareWidget, _TemplatedMixin], {

      /**
       * The CSS file referenced by the activities WebScript
       */
      cssRequirements: [{cssFile:"../../../../components/documentlibrary/actions.css"},
                        {cssFile:"../../../../components/documentlibrary/documentlist.css"}],
      
      /**
       * Set the name as that of the dashlet from Share. This then ensures that the correct scope is set so that the 
       * messages can be retrieved as usual. 
       */
      i18nScope: "Alfresco.DocumentList",
             
      /**
       * Specifies the properties file from the WebScript that is used to instantiate the widget. It's only necessary
       * to specify the default property file - the Dojo dependency handler will sort out the locale as necessary
       */
      i18nRequirements: [{i18nFile: "../../../../WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/documentlibrary/documentlist.get.properties"}],
      
      templateString: template,
      
      /**
       * The JavaScript file referenced by the activities WebScript
       */
      dependencies: [Alfresco.constants.URL_RESCONTEXT + "modules/documentlibrary/doclib-actions.js",
                     Alfresco.constants.URL_RESCONTEXT + "components/documentlibrary/actions.js",
                     Alfresco.constants.URL_RESCONTEXT + "components/documentlibrary/documentlist.js"],
                     
      templateMessages: null,
      
      /**
       * The constructor is extended so that we can construct an object containing all the i18n properties to 
       * be substituted by the template. This is because the template can't call functions to obtain data.
       * There are potentially better ways of doing this - but it does at least work.
       */
      constructor: function(args) {
         declare.safeMixin(this, args);
         
         this.url = {
            context : Alfresco.constants.URL_CONTEXT
         };
         
         this.templateMessages = {
            no_items_title: this.message("no.items.title"),
            dnd_drop_title: this.message("dnd.drop.title"),
            dnd_drop_doclist_description: this.message("dnd.drop.doclist.description"),
            dnd_drop_folder_description: this.message("dnd.drop.folder.description"),
            standard_upload_title: this.message("standard.upload.title"),
         };
      },
      
      /**
       * Creates the Share widgets.
       */
      createWidget: function(me) {
         
         var documentlist = new Alfresco.DocumentList(me.id).setOptions({
            repositoryBrowsing: false,
            highlightFile:"",
            simpleView:"null",
            replicationUrlMapping:"{}",
            viewRendererName:"detailed",
            viewRendererNames: ["simple", "detailed"],
            siteId: "site1",
            rootNode:"alfresco:\/\/company\/home",
            sortField:"cm:name",
            useTitle:true,
            sortAscending:true,
            usePagination:true,
            containerId:"documentLibrary",
            syncMode:"OFF",
            showFolders:true,
            userIsSiteManager:true
         });

         // It's necessary to call the onReady() actions of all the widgets created...
         documentlist.onReady();
         
         // Normally the history manager would fire this... but its currently not working
         YAHOO.Bubbling.fire("changeFilter", YAHOO.lang.merge(
            {
               doclistFirstTimeNav: true
            }, {}));
         
         
         Dom.setStyle(me.id + "-body", "visibility", "visible");
      }
   });
});