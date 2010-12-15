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
 * RM Property Selector Menu Component
 * 
 * @namespace Alfresco
 * @class Alfresco.RMPropertyMenu
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
    * RMPropertyMenu constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RMPropertyMenu} The new component instance
    * @constructor
    */
   Alfresco.RMPropertyMenu = function RMPropertyMenu_constructor(htmlId)
   {
      Alfresco.RMPropertyMenu.superclass.constructor.call(this, "Alfresco.RMPropertyMenu", htmlId, ["button", "container", "menu"]);     
      return this;
   };
   
   YAHOO.extend(Alfresco.RMPropertyMenu, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Flag indicating whether search related fields are visible or not.
          * 
          * @property showSearchFields
          * @type boolean
          */
         showSearchFields: false,
         
         /**
          * Flag indicating whether special type related fields are visible or not.
          * 
          * @property showSpecialTypeFields
          * @type boolean
          */
         showSpecialTypeFields: true,
         
         /**
          * Flag indicating whether IMAP related fields are visible or not.
          * 
          * @property showIMAPFields
          * @type boolean
          */
         showIMAPFields: false,
         
         /**
          * Flag indicating whether Record Identifier field is visible or not.
          * 
          * @property showIdentiferField
          * @type boolean
          */
         showIdentiferField: false,
         
         /**
          * Flag indicating whether special 'All' field is visible or not.
          * 
          * @property showAllField
          * @type boolean
          */
         showAllField: false,
         
         /**
          * Flag passed to YUI menu constructor whether to wait for first display to render menu.
          * 
          * @property lazyLoadMenu
          * @type boolean
          */
         lazyLoadMenu: true,
         
         /**
          * Flag indicating whether the menu button should update the label to mirror the selected item text.
          * 
          * @property updateButtonLabel
          * @type boolean
          */
         updateButtonLabel: true,
         
         /**
          * Custom rmc meta fields to display - objects with 'id' and 'title' properties
          * 
          * @property customFields
          * @type Array
          */
         customFields: []
      },
      
      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function RMPropertyMenu_onReady()
      {
         var items = [];
         
         if (this.options.showSearchFields)
         {
            // add content fields
            items.push(
            {
               text: this.msg("label.menu.content"),
               submenu:
               { 
                  id: this.id + "_content",
                  itemdata:
                  [ 
                     { text: this.msg("label.name"), value: "name" },
                     { text: this.msg("label.title"), value: "title" },
                     { text: this.msg("label.description"), value: "description" },
                     { text: this.msg("label.creator"), value: "creator" },
                     { text: this.msg("label.created"), value: "created" },
                     { text: this.msg("label.modifier"), value: "modifier" },
                     { text: this.msg("label.modified"), value: "modified" },
                     { text: this.msg("label.author"), value: "author" }
                  ]
               }
            });
            
            // add record fields
            items.push(
            {
               text: this.msg("label.menu.records"),
               submenu:
               { 
                  id: this.id + "_records",
                  itemdata:
                  [ 
                     { text: this.msg("label.originator"), value: "originator" },
                     { text: this.msg("label.dateFiled"), value: "dateFiled" },
                     { text: this.msg("label.publicationDate"), value: "publicationDate" },
                     { text: this.msg("label.reviewDate"), value: "reviewAsOf" },
                     { text: this.msg("label.originatingOrganization"), value: "originatingOrganization" },
                     { text: this.msg("label.mediaType"), value: "mediaType" },
                     { text: this.msg("label.format"), value: "format" },
                     { text: this.msg("label.dateReceived"), value: "dateReceived" },
                     { text: this.msg("label.location"), value: "location" },
                     { text: this.msg("label.address"), value: "address" },
                     { text: this.msg("label.otherAddress"), value: "otherAddress" },
                     { text: this.msg("label.supplementalMarkingList"), value: "markings" }
                  ]
               }
            });
         }
         else
         {
            // add content fields
            items.push(
            {
               text: this.msg("label.menu.content"),
               submenu:
               { 
                  id: this.id + "_content",
                  itemdata:
                  [ 
                     { text: this.msg("label.name"), value: "cm:name" },
                     { text: this.msg("label.title"), value: "cm:title" },
                     { text: this.msg("label.description"), value: "cm:description" },
                     { text: this.msg("label.creator"), value: "cm:creator" },
                     { text: this.msg("label.created"), value: "cm:created" },
                     { text: this.msg("label.modifier"), value: "cm:modifier" },
                     { text: this.msg("label.modified"), value: "cm:modified" },
                     { text: this.msg("label.author"), value: "cm:author" }
                  ]
               }
            });
            
            // add record fields
            items.push(
            {
               text: this.msg("label.menu.records"),
               submenu:
               { 
                  id: this.id + "_records",
                  itemdata:
                  [ 
                     { text: this.msg("label.originator"), value: "rma:originator" },
                     { text: this.msg("label.dateFiled"), value: "rma:dateFiled" },
                     { text: this.msg("label.publicationDate"), value: "rma:publicationDate" },
                     { text: this.msg("label.reviewDate"), value: "rma:reviewAsOf" },
                     { text: this.msg("label.originatingOrganization"), value: "rma:originatingOrganization" },
                     { text: this.msg("label.mediaType"), value: "rma:mediaType" },
                     { text: this.msg("label.format"), value: "rma:format" },
                     { text: this.msg("label.dateReceived"), value: "rma:dateReceived" },
                     { text: this.msg("label.location"), value: "rma:location" },
                     { text: this.msg("label.address"), value: "rma:address" },
                     { text: this.msg("label.otherAddress"), value: "rma:otherAddress" },
                     { text: this.msg("label.supplementalMarkingList"), value: "rmc:supplementalMarkingList" }
                  ]
               }
            });
         }
         
         if (this.options.showAllField)
         {
            items.splice(0, 0,
            {
               text: this.msg("label.all"),
               value: "ALL"
            });
         }
         
         if (this.options.showIdentiferField)
         {
            // insert RMA Identifer field
            items[1].submenu.itemdata.splice(0, 0,
            {
               text: this.msg("label.identifier"),
               value: "rma:identifier"
            });
         }
         
         if (this.options.showSearchFields)
         {
            // insert KEYWORDS special search field
            items[0].submenu.itemdata.splice(0, 0,
            {
               text: this.msg("label.keywords"),
               value: "keywords"
            });
            
            // insert search roll-up special field menu
            items.push(
            {
               text: this.msg("label.menu.disposition"),
               submenu:
               { 
                  id: this.id + "_disposition",
                  itemdata:
                  [ 
                     { text: this.msg("label.dispositionEvents"), value: "dispositionEvents" },
                     { text: this.msg("label.dispositionActionName"), value: "dispositionActionName" },
                     { text: this.msg("label.dispositionActionAsOf"), value: "dispositionActionAsOf" },
                     { text: this.msg("label.dispositionEventsEligible"), value: "dispositionEventsEligible" },
                     { text: this.msg("label.dispositionPeriod"), value: "dispositionPeriod" },
                     { text: this.msg("label.hasDispositionSchedule"), value: "hasDispositionSchedule" },
                     { text: this.msg("label.dispositionInstructions"), value: "dispositionInstructions" },
                     { text: this.msg("label.dispositionAuthority"), value: "dispositionAuthority" },
                     { text: this.msg("label.holdReason"), value: "holdReason" },
                     { text: this.msg("label.vitalRecordReviewPeriod"), value: "vitalRecordReviewPeriod" }
                  ]
               }
            });
         }
         
         if (this.options.showIMAPFields)
         {
            // insert IMAP field menu
            items.push(
            {
               text: this.msg("label.menu.imap"),
               submenu:
               { 
                  id: this.id + "_imap",
                  itemdata:
                  [ 
                     { text: this.msg("label.imap.threadIndex"), value: "imap:threadIndex" },
                     { text: this.msg("label.imap.messageFrom"), value: "imap:messageFrom" },
                     { text: this.msg("label.imap.messageTo"), value: "imap:messageTo" },
                     { text: this.msg("label.imap.messageCc"), value: "imap:messageCc" },
                     { text: this.msg("label.imap.messageSubject"), value: "imap:messageSubject" },
                     { text: this.msg("label.imap.dateReceived"), value: "imap:dateReceived" },
                     { text: this.msg("label.imap.dateSent"), value: "imap:dateSent" }
                  ]
               }
            });
         }
         
         if (this.options.showSpecialTypeFields)
         {
            // insert special type fields menu
            items.push(
            {
               text: this.msg("label.menu.specialtypes"),
               submenu:
               { 
                  id: this.id + "_specialtypes",
                  itemdata:
                  [ 
                     [
                     { text: this.msg("label.dod.scannedFormatVersion"), value: "dod:scannedFormatVersion" },
                     { text: this.msg("label.dod.resolutionX"), value: "dod:resolutionX" },
                     { text: this.msg("label.dod.resolutionY"), value: "dod:resolutionY" },
                     { text: this.msg("label.dod.scannedBitDepth"), value: "dod:scannedBitDepth" }
                     ],
                     [
                     { text: this.msg("label.dod.producingApplication"), value: "dod:producingApplication" },
                     { text: this.msg("label.dod.producingApplicationVersion"), value: "dod:producingApplicationVersion" },
                     { text: this.msg("label.dod.pdfVersion"), value: "dod:pdfVersion" },
                     { text: this.msg("label.dod.creatingApplication"), value: "dod:creatingApplication" },
                     { text: this.msg("label.dod.documentSecuritySettings"), value: "dod:documentSecuritySettings" }
                     ],
                     [
                     { text: this.msg("label.dod.caption"), value: "dod:caption" },
                     { text: this.msg("label.dod.photographer"), value: "dod:photographer" },
                     { text: this.msg("label.dod.copyright"), value: "dod:copyright" },
                     { text: this.msg("label.dod.bitDepth"), value: "dod:bitDepth" },
                     { text: this.msg("label.dod.imageSizeX"), value: "dod:imageSizeX" },
                     { text: this.msg("label.dod.imageSizeY"), value: "dod:imageSizeY" },
                     { text: this.msg("label.dod.imageSource"), value: "dod:imageSource" },
                     { text: this.msg("label.dod.compression"), value: "dod:compression" },
                     { text: this.msg("label.dod.iccIcmProfile"), value: "dod:iccIcmProfile" },
                     { text: this.msg("label.dod.exifInformation"), value: "dod:exifInformation" }
                     ],
                     [
                     { text: this.msg("label.dod.webFileName"), value: "dod:webFileName" },
                     { text: this.msg("label.dod.webPlatform"), value: "dod:webPlatform" },
                     { text: this.msg("label.dod.webSiteName"), value: "dod:webSiteName" },
                     { text: this.msg("label.dod.webSiteURL"), value: "dod:webSiteURL" },
                     { text: this.msg("label.dod.captureMethod"), value: "dod:captureMethod" },
                     { text: this.msg("label.dod.captureDate"), value: "dod:captureDate" },
                     { text: this.msg("label.dod.contact"), value: "dod:contact" },
                     { text: this.msg("label.dod.contentManagementSystem"), value: "dod:contentManagementSystem" }
                     ]
                  ]
               }
            });
         }
         
         // add custom fields menu
         var customMenu =
         {
            text: this.msg("label.menu.custom"),
            submenu:
            { 
               id: this.id + "_custom",
               itemdata:
               [
               ]
            }
         };
         if (this.options.customFields.length !== 0)
         {
            // specified in the options as an array of objects with 'id' and 'title' fields
            var itemdata = customMenu.submenu.itemdata;
            for (var i=0, j=this.options.customFields.length; i<j; i++)
            {
               var prop = this.options.customFields[i];
               itemdata.push(
               {
                  text: $html(prop.title),
                  value: prop.id
               });
            }
         }
         items.push(customMenu);
         
         // menu button widget setup
         this.widgets.menubtn = new YAHOO.widget.Button(this.id,
         {
            type: "menu",
            menu: items,
            lazyloadmenu: this.options.lazyLoadMenu
         });
         this.widgets.menubtn.getMenu().subscribe("click", function(e, args)
         {
            var menuItem = args[1];
            
            // only process if an actual menu item (not a header) has been selected
            if (menuItem.value)
            {
               var label = menuItem.cfg.getProperty("text");
               if (this.options.updateButtonLabel)
               {
                  // update menu button label
                  this.widgets.menubtn.set("label", label);
               }
               
               // fire event so page component can deal with it
               YAHOO.Bubbling.fire("PropertyMenuSelected",
               {
                  value: menuItem.value,
                  label: label
               });
            }
            
         }, this, true);
      }
   });
})();