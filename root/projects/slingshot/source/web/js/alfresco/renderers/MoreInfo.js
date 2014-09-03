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
 * 
 * @module alfresco/renderers/MoreInfo
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes dijit/_OnDijitClickMixin
 * @mixes module:alfresco/core/Core
 * @mixes module:alfresco/core/ObjectTypeUtils
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dijit/_OnDijitClickMixin",
        "alfresco/renderers/_XhrActionsMixin",
        "alfresco/core/ObjectTypeUtils",
        "dojo/text!./templates/MoreInfo.html",
        "alfresco/core/Core",
        "alfresco/documentlibrary/views/layouts/Popup",
        "dojo/_base/lang",
        "dojo/dom-class",
        "dojo/_base/event"], 
        function(declare, _WidgetBase, _TemplatedMixin, _OnDijitClickMixin, _XhrActionsMixin, ObjectTypeUtils, template, AlfCore, Popup, lang, domClass, event) {

   return declare([_WidgetBase, _TemplatedMixin, _OnDijitClickMixin, _XhrActionsMixin, AlfCore], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{cssFile:"./css/MoreInfo"}]
       */
      cssRequirements: [{cssFile:"./css/MoreInfo.css"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {string}
       */
      templateString: template,
      
      /**
       * This is the object that the property to be rendered will be retrieved from.
       * 
       * @instance
       * @type {object}
       * @default null
       */
      currentItem: null,
      
      /**
       * This is used to hold a reference to the information dialog that is popped up when the widget is clicked.
       * The dialog is not instantiated until the first time that a user requests additional information.
       * 
       * @instance
       * @type {object}
       * @default null
       */
      moreInfoDialog: null,
      
      /**
       * Used to indicate whether or not the info to display needs to be asynchronously retrieved. Defaults
       * to false assuming that all the data required is currently available in "currentItem".
       * 
       * @instance
       * @type {boolean}
       * @default false
       */
      xhrRequired: false,

      /**
       * This is called when the user clicks on the "info" symbol and creates a new
       * [popup]{@link module:alfresco/documentlibrary/views/layouts/Popup} containing the info
       * to be displayed.
       *
       * @instance
       * @param {object} evt The click event.
       */
      onMoreInfo: function alfresco_renderers_MoreInfo__onMoreInfo(evt) {
         event.stop(evt);
         if (this.moreInfoDialog == null)
         {
            if (this.xhrRequired == true)
            {
               this.getXhrData()
            }
            else
            {
               this.createMoreInfoDialog();
            }
         }
         else
         {
            this.moreInfoDialog.show();
         }
      },

      /**
       * Creates the dialog containing the information to be displayed
       *
       * @instance
       */
      createMoreInfoDialog: function alfresco_renderers_MoreInfo__createMoreInfoDialog() {
         var title = "";
         if (ObjectTypeUtils.isObject(this.currentItem) && lang.exists("displayName", this.currentItem))
         {
            title = lang.getObject("displayName", false, this.currentItem);
         }
         
         this.moreInfoDialog = new Popup({
            title: title,
            currentItem: this.currentItem,
            widgetsContent: lang.clone(this.widgets)
         });
         this.moreInfoDialog.show();
      },

      /**
       * Overrides the [inherited function]{@link module:alfresco/renderers/_XhrActionsMixin#clearLoadingItem}
       * to intentionally perform no action.
       * 
       * @instance
       */
      clearLoadingItem: function alfresco_renderers_MoreInfo__clearLoadingItem() {
         // No action by design.
      },

      /**
       * Overrides the [inherited function]{@link module:alfresco/renderers/_XhrActionsMixin#addXhrItems}
       * to create the dialog with the requested data by calling the 
       * [createMoreInfoDialog function]{@link module:alfresco/renderers/MoreInfo#createMoreInfoDialog}.
       *
       * @instance
       */
      addXhrItems: function alfresco_renderers_MoreInfo__addXhrItems() {
         this.createMoreInfoDialog();
      },
      
      /**
       * The default JSON model for the widgets to add to the dialog.
       * @instance
       * @type {object[]}
       *
       */
      widgets: [
         {
            name: "alfresco/documentlibrary/views/layouts/Table",
            config: {
               widgets: [
                  {
                     name: "alfresco/documentlibrary/views/layouts/Row",
                     config: {
                        widgets: [
                           {
                              name: "alfresco/documentlibrary/views/layouts/Cell",
                              config: {
                                 widgets: [
                                    {
                                       name: "alfresco/renderers/Thumbnail",
                                       config: {
                                          renditionName: "imgpreview"
                                       }
                                    }
                                 ]
                              }
                           },
                           {
                              name: "alfresco/documentlibrary/views/layouts/Cell",
                              config: {
                                 widgets: [
                                    {
                                       name: "alfresco/renderers/Actions"
                                    }
                                 ]
                              }
                           }
                        ]
                     }
                  },
                  {
                     name: "alfresco/documentlibrary/views/layouts/Row",
                     config: {
                        widgets: [
                           {
                              name: "alfresco/documentlibrary/views/layouts/Cell",
                              config: {
                                 widgets: [
                                    {
                                       name: "alfresco/renderers/InlineEditProperty",
                                       config: {
                                          propertyToRender: "node.properties.cm:name",
                                          postParam: "prop_cm_name",
                                          renderSize: "large",
                                          publishTopic: "ALF_CRUD_CREATE",
                                          publishPayloadType: "PROCESS",
                                          publishPayloadModifiers: ["processCurrentItemTokens"],
                                          publishPayloadItemMixin: false,
                                          publishPayload: {
                                             url: "api/node/{jsNode.nodeRef.uri}/formprocessor",
                                             noRefresh: false,
                                             successMessage: "moreInfo.inlineEdit.update.success"
                                          }
                                       }
                                    },
                                    {
                                       name: "alfresco/renderers/InlineEditProperty",
                                       config: {
                                          propertyToRender: "node.properties.cm:title",
                                          postParam: "prop_cm_title",
                                          renderedValuePrefix: "(",
                                          renderedValueSuffix: ")",
                                          renderFilter: [
                                             {
                                                property: "node.properties.cm:title",
                                                values: [""],
                                                negate: true
                                             }
                                          ],
                                          publishTopic: "ALF_CRUD_CREATE",
                                          publishPayloadType: "PROCESS",
                                          publishPayloadModifiers: ["processCurrentItemTokens"],
                                          publishPayloadItemMixin: false,
                                          publishPayload: {
                                             url: "api/node/{jsNode.nodeRef.uri}/formprocessor",
                                             noRefresh: false,
                                             successMessage: "moreInfo.inlineEdit.update.success"
                                          }
                                       }
                                    },
                                    {
                                       name: "alfresco/renderers/Version",
                                       config: {
                                          renderFilter: [
                                             {
                                                property: "node.isContainer",
                                                values: [false]
                                             },
                                             {
                                                property: "workingCopy.isWorkingCopy",
                                                values: [false],
                                                renderOnAbsentProperty: true
                                             }
                                          ]
                                       }
                                    }
                                 ]
                              }
                           }
                        ]
                     }
                  },
                  {
                     name: "alfresco/documentlibrary/views/layouts/Row",
                     config: {
                        widgets: [
                           {
                              name: "alfresco/documentlibrary/views/layouts/Cell",
                              config: {
                                 widgets: [
                                    {
                                       name: "alfresco/renderers/Date"
                                    },
                                    {
                                       name: "alfresco/renderers/Size"
                                    }
                                 ]
                              }
                           }
                        ]
                     }
                  },
                  {
                     name: "alfresco/documentlibrary/views/layouts/Row",
                     config: {
                        widgets: [
                           {
                              name: "alfresco/documentlibrary/views/layouts/Cell",
                              config: {
                                 widgets: [
                                    {
                                       name: "alfresco/renderers/InlineEditProperty",
                                       config: {
                                          propertyToRender: "node.properties.cm:description",
                                          postParam: "prop_cm_description",
                                          warnIfNotAvailable: true,
                                          warnIfNoteAvailableMessage: "no.description.message",
                                          publishTopic: "ALF_CRUD_CREATE",
                                          publishPayloadType: "PROCESS",
                                          publishPayloadModifiers: ["processCurrentItemTokens"],
                                          publishPayloadItemMixin: false,
                                          publishPayload: {
                                             url: "api/node/{jsNode.nodeRef.uri}/formprocessor",
                                             noRefresh: false
                                          }
                                       }
                                    }
                                 ]
                              }
                           }
                        ]
                     }
                  },
                  {
                     name: "alfresco/documentlibrary/views/layouts/Row",
                     config: {
                        widgets: [
                           {
                              name: "alfresco/documentlibrary/views/layouts/Cell",
                              config: {
                                 renderFilter: [
                                    {
                                       property: "workingCopy.isWorkingCopy",
                                       values: [false],
                                       renderOnAbsentProperty: true
                                    }
                                ],
                                widgets: [
                                    {
                                       name: "alfresco/renderers/Favourite"
                                    },
                                    {
                                       name: "alfresco/renderers/Separator"
                                    },
                                    {
                                       name: "alfresco/renderers/Like"
                                    },
                                    {
                                       name: "alfresco/renderers/Separator"
                                    },
                                    {
                                       name: "alfresco/renderers/Comments"
                                    },
                                    {
                                       name: "alfresco/renderers/Separator",
                                       config: {
                                          renderFilter: [
                                             {
                                                property: "node.isContainer",
                                                values: [false]
                                             }
                                          ]
                                       }
                                    },
                                    {
                                       name: "alfresco/renderers/QuickShare",
                                       config: {
                                          renderFilter: [
                                             {
                                                property: "node.isContainer",
                                                values: [false]
                                             }
                                          ]
                                       }
                                    }
                                 ]
                              }
                           }
                        ]
                     }
                  }
               ]
            }
         }
      ]
   });
});