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
 * @module alfresco/kickstart/Step
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/Step.html",
        "alfresco/core/Core",
        "dojo/_base/lang",
        "alfresco/menus/AlfMenuBar",
        "alfresco/dialogs/AlfDialog",
        "dojo/dom-construct",
        "dojo/dom-class",
        "dojo/fx",
        "dojo/on"], 
        function(declare, _Widget, _Templated, template, AlfCore, lang, AlfMenuBar, AlfDialog, domConstruct, domClass, coreFx, on) {
   
   return declare([_Widget, _Templated, AlfCore], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/Step.css"}],
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type i18nRequirements {Array}
       */
      i18nRequirements: [{i18nFile: "./i18n/Step.properties"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type template {String}
       */
      templateString: template,
      
      /**
       * @instance
       * @type {string}
       * @default ""
       */
      stepTitle: "",
      
      iconClass: "",
      
      /**
       * @instance
       * @type {boolean}
       * @default false
       */
      collapsed: false,
      
      /**
       * @instance
       */
      postCreate: function alfresco_kickstart_Step__postCreate() {
         
         // Create the subscriptions for the topics that can be published from the
         // configuration menu...
         this.alfSubscribe("ALF_KICKSTART_DELETE_STEP", lang.hitch(this, "onDeleteStepRequest"));
         this.alfSubscribe("ALF_KICKSTART_RENAME_STEP", lang.hitch(this, "onRenameStepRequest"));
         this.alfSubscribe("ALF_KICKSTART_DUPLICATE_STEP", lang.hitch(this, "onDuplicateStepRequest"));
         this.alfSubscribe("ALF_KICKSTART_VIEW_STEP_FLOW", lang.hitch(this, "onViewFlowRequest"));
         
         this.alfSubscribe("ALF_KICKSTART_DELETE_STEP_CONFIRMATION", lang.hitch(this, "onDeleteStepConfirmation"));
         this.alfSubscribe("ALF_KICKSTART_RENAME_STEP_CONFIRMATION", lang.hitch(this, "onRenameStepConfirmation"));
         
         this.configMenu = new AlfMenuBar({
            pubSubScope: this.pubSubScope,
            widgets: [
               {
                  name: "alfresco/menus/AlfMenuBarPopup",
                  config: {
                     label: "",
                     iconClass: "alf-configure-icon",
                     pubSubScope: this.pubSubScope,
                     widgets: [
                        {
                           name: "alfresco/menus/AlfMenuGroup",
                           config: {
                              widgets: [
                                 {
                                    name: "alfresco/menus/AlfMenuItem",
                                    config: {
                                       label: "step.config.delete",
                                       publishTopic: "ALF_KICKSTART_DELETE_STEP",
                                       publishPayload: {
                                          
                                       }
                                    }
                                 },
                                 {
                                    name: "alfresco/menus/AlfMenuItem",
                                    config: {
                                       label: "step.config.rename",
                                       publishTopic: "ALF_KICKSTART_RENAME_STEP",
                                       publishPayload: {
                                          
                                       }
                                    }
                                 },
                                 {
                                    name: "alfresco/menus/AlfMenuItem",
                                    config: {
                                       label: "step.config.duplicate",
                                       publishTopic: "ALF_KICKSTART_DUPLICATE_STEP",
                                       publishPayload: {
                                          
                                       }
                                    }
                                 },
                                 {
                                    name: "alfresco/menus/AlfMenuItem",
                                    config: {
                                       label: "step.config.viewFlow",
                                       publishTopic: "ALF_KICKSTART_VIEW_STEP_FLOW",
                                       publishPayload: {
                                          
                                       }
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
         this.configMenu.placeAt(this.configControlNode);
         
         var content = this.getInitialContent();
         if (content.domNode != null)
         {
            domConstruct.place(content.domNode, this.mainNode);
         }
      },
      
      /**
       * This is intended to be overridden to create the actual initial content for the widget.
       * 
       * @instance
       * @returns {object} The content to be placed in the collapsing section.
       */
      getInitialContent: function alfresco_kickstart_Step__getInitialContent() {
         return "Default";
      },
      
      /**
       * Handles collapsing and expanding the step.
       * 
       * @instance
       */
      onCollapseControlClick: function alfresco_kickstart_Step__onCollapseControlClick() {
         if (this.collapsed == true)
         {
            this.collapsed = false;
            domClass.remove(this.collapseControlNode, "collapsed");
            coreFx.wipeIn({
               node: this.mainNode
            }).play();
         }
         else
         {
            this.collapsed = true;
            domClass.add(this.collapseControlNode, "collapsed");
            coreFx.wipeOut({
               node: this.mainNode
            }).play();
         }
      },
      
      /**
       * @instance
       * @param {object} payload The request payload
       */
      onDeleteStepRequest: function alfresco_kickstart_Step__onDeleteStepRequest(payload) {
         var dialog = new AlfDialog({
            pubSubScope: this.pubSubScope,
            title: this.message("step.dialog.delete.title"),
            textContent: this.message("step.dialog.delete.content"),
            widgetsButtons: [
               {
                  name: "alfresco/buttons/AlfButton",
                  config: {
                     label: this.message("step.dialog.button.delete"),
                     publishTopic: "ALF_KICKSTART_DELETE_STEP_CONFIRMATION",
                     publishPayload: payload
                  }
               },
               {
                  name: "alfresco/buttons/AlfButton",
                  config: {
                     label: this.message("step.dialog.button.cancel"),
                     publishTopic: "ALF_KICKSTART_DELETE_STEP_CANCELLATION",
                     publishPayload: payload
                  }
               }
            ]
         });
         dialog.show();
      },
      
      /**
       * @instance
       * @param {object} payload The deletion confirmation payload
       */
      onDeleteStepConfirmation: function alfresco_kickstart_Step__onDeleteStepConfirmation(payload) {
         on.emit(this.domNode, "onWidgetDelete", {
            bubbles: true,
            cancelable: true,
            widgetToDelete: this
         });
      },
      
      /**
       * @instance
       * @param {object} payload The request payload
       */
      onRenameStepRequest: function alfresco_kickstart_Step__onRenameStepRequest(payload) {
         var dialog = new AlfDialog({
            pubSubScope: this.pubSubScope,
            title: this.message("step.dialog.rename.title"),
            widgetsContent: [
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  assignTo: "contents",
                  config: {
                     label: "step.dialog.rename.name.label",
                     description: "step.dialog.rename.name.description",
                     value: this.name
                  }
               }
            ],
            widgetsButtons: [
               {
                  name: "alfresco/buttons/AlfButton",
                  config: {
                     label: this.message("step.dialog.button.ok"),
                     publishTopic: "ALF_KICKSTART_RENAME_STEP_CONFIRMATION",
                     publishPayload: {
                        dialog: dialog
                     }
                  }
               },
               {
                  name: "alfresco/buttons/AlfButton",
                  config: {
                     label: this.message("step.dialog.button.cancel"),
                     publishTopic: "ALF_KICKSTART_RENAME_STEP_CANCELLATION",
                     publishPayload: payload
                  }
               }
            ]
         });
         dialog.show();
      },
      
      /**
       * @instance
       */
      onRenameStepConfirmation: function alfresco_kickstart_Step__onRenameStepConfirmation(payload) {
         this.alfLog("log", "Rename confirmation", payload);
         var newName = payload.dialogContent[0].getValue();
         this.titleNode.innerHTML = this.encodeHTML(newName);
         this.alfSetData(this.id + ".name", newName);
         this.name = newName;
      },
      
      /**
       * @instance
       * @param {object} payload The request payload
       */
      onDuplicateStepRequest: function alfresco_kickstart_Step__onDuplicateStepRequest(payload) {
         
      },
      
      /**
       * @instance
       * @param {object} payload The request payload
       */
      onViewFlowRequest: function alfresco_kickstart_Step__onViewFlowRequest(payload) {
         // TODO: This needs moving to a logging service
         var dialog = new AlfDialog({
            pubSubScope: this.pubSubScope,
            title: this.message("step.dialog.rename.title"),
            widgetsContent: [
               {
                  name: "alfresco/debug/CoreDataDebugger"
               }
            ],
            widgetsButtons: [
               {
                  name: "alfresco/buttons/AlfButton",
                  config: {
                     label: this.message("step.dialog.button.ok"),
                     publishTopic: "ALF_KICKSTART_RENAME_STEP_CONFIRMATION",
                     publishPayload: {
                        dialog: dialog
                     }
                  }
               },
               {
                  name: "alfresco/buttons/AlfButton",
                  config: {
                     label: this.message("step.dialog.button.cancel"),
                     publishTopic: "ALF_KICKSTART_RENAME_STEP_CANCELLATION",
                     publishPayload: payload
                  }
               }
            ]
         });
         dialog.show();
      }
   });
});