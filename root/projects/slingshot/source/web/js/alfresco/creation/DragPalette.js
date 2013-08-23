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
 * 
 * 
 * @module alfresco/creation/DragPalette
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/DragWidgetPalette.html",
        "dojo/text!./templates/WidgetTemplate.html",
        "alfresco/core/Core",
        "dojo/dnd/Source",
        "dojo/_base/lang",
        "dojo/string",
        "dojo/dom-construct"], 
        function(declare, _Widget, _Templated, template, WidgetTemplate, AlfCore, Source, lang, stringUtil, domConstruct) {
   
   return declare([_Widget, _Templated, AlfCore], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/DragWidgetPalette.css"}],
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type i18nRequirements {Array}
       */
      i18nRequirements: [{i18nFile: "./i18n/DragWidgetPalette.properties"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type template {String}
       */
      templateString: template,
      
      /**
       * @instance
       * @type {boolean}
       * @default false
       */
      dragWithHandles: false,
      
      /**
       * @instance
       */
      postCreate: function alfresco_creation_DragWidgetPalette__postCreate() {
         var palette = new Source(this.paletteNode, {
            copyOnly: true,
            selfCopy: false,
            creator: lang.hitch(this, "creator"),
            withHandles: this.dragWithHandles
         });
         palette.insertNodes(false, this.widgetsForPalette);
      },
      
      /**
       * Handles the creation of drag'n'drop avatars. This could check the supplied hint parameter
       * to see if an avatar is required, but since the source doesn't allow self-copying and is not
       * a target in itself then this is not necessary.
       * 
       * @instance
       */
      creator: function alfresco_creation_DragWidgetPalette__creator(item, hint) {
         this.alfLog("log", "Creating", item, hint);
         var node = domConstruct.toDom(stringUtil.substitute(WidgetTemplate, {
            title: (item.name != null) ? item.name : "",
            iconClass: (item.iconClass != null) ? item.iconClass : ""
         }));
         return {node: node, data: item, type: ["widget"]};
      },
      
      /**
       * @instance
       * @returns {object[]}
       */
      widgetsForPalette: [
         {
            type: ["widget"],
            name: "Page Title",
            module: "alfresco/header/SetTitle",
            // This is the initial configuration that will be provided when the widget
            // is dropped into the drop-zone...
            defaultConfig: {
               title: ""
            },
            // These are the widgets used to configure the dropped widget.
            widgetsForConfig: [
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     name: "defaultConfig.title",
                     label: "Page Title",
                     value: "",
                  }
               }
            ],
            // If set to true, then the actual widget will be previewed...
            previewWidget: false,
            // This is the widget structure to use to display the widget.
            widgetsForDisplay: [
               {
                  name: "alfresco/creation/DropZone",
                  config: {
                     horizontal: true
                  }
               }
            ]
         },
         {
            type: ["widget"],
            name: "Logo",
            module: "alfresco/logo/Logo",
            // This is the initial configuration that will be provided when the widget
            // is dropped into the drop-zone...
            defaultConfig: {
               logoClasses: ""
            },
            // These are the widgets used to configure the dropped widget.
            widgetsForConfig: [
               {
                  name: "alfresco/forms/controls/DojoSelect",
                  config: {
                     name: "defaultConfig.logoClasses",
                     label: "Logo Classes",
                     value: "",
                     optionsConfig: {
                        fixed: [
                           {label:"Standard Alfresco",value:""},
                           {label:"Large Alfresco",value:"alfresco-logo-large"},
                           {label:"Alfresco Logo Only",value:"alfresco-logo-only"},
                           {label:"3D Alfresco",value:"alfresco-logo-3d"},
                           {label:"Surf Large",value:"surf-logo-large"},
                           {label:"Surf Small",value:"surf-logo-small"}
                        ]
                     }
                  }
               }
            ],
            // If set to true, then the actual widget will be previewed...
            previewWidget: false,
            // This is the widget structure to use to display the widget.
            widgetsForDisplay: [
               {
                  name: "alfresco/creation/DropZone",
                  config: {
                     horizontal: true
                  }
               }
            ]
         },
         {
            type: ["widget"],
            name: "Menu Bar",
            module: "alfresco/menus/AlfMenuBar",
            // This is the initial configuration that will be provided when the widget
            // is dropped into the drop-zone...
            defaultConfig: {
            },
            // These are the widgets used to configure the dropped widget.
            widgetsForConfig: [
            ],
            // If set to true, then the actual widget will be previewed...
            previewWidget: false,
            // This is the widget structure to use to display the widget.
            widgetsForDisplay: [
               {
                  name: "alfresco/creation/DropZone",
                  config: {
                     horizontal: true
                  }
               }
            ]
         },
         {
            type: ["widget"],
            name: "Menu Bar Item",
            module: "alfresco/menus/AlfMenuBarItem",
            // This is the initial configuration that will be provided when the widget
            // is dropped into the drop-zone...
            defaultConfig: {
               label: "default",
               iconClass: "",
               altText: ""
            },
            // These are the widgets used to configure the dropped widget.
            widgetsForConfig: [
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     name: "defaultConfig.label",
                     label: "Label",
                     value: "default",
                  }
               },
               {
                  name: "alfresco/forms/controls/DojoSelect",
                  config: {
                     name: "defaultConfig.iconClass",
                     label: "Icon",
                     value: "",
                     optionsConfig: {
                        fixed: [
                           {label:"None",value:""},
                           {label:"Configure",value:"alf-configure-icon"},
                           {label:"Invite User",value:"alf-user-icon"},
                           {label:"Upload",value:"alf-upload-icon"},
                           {label:"Create",value:"alf-create-icon"},
                           {label:"All Selected",value:"alf-allselected-icon"},
                           {label:"Some Selected",value:"alf-someselected-icon"},
                           {label:"None Selected",value:"alf-noneselected-icon"},
                           {label:"Back",value:"alf-back-icon"},
                           {label:"Forward",value:"alf-forward-icon"}
                        ]
                     }
                  }
               },
               {
                  name: "alfresco/forms/controls/DojoTextarea",
                  config: {
                     name: "defaultConfig.altText",
                     label: "Alt Text",
                     value: "",
                  }
               }
            ],
            // If set to true, then the actual widget will be previewed...
            previewWidget: false,
            // This is the widget structure to use to display the widget.
            widgetsForDisplay: [
               {
                  name: "alfresco/creation/DropZone",
                  config: {
                     horizontal: true
                  }
               }
            ]
         },
         {
            type: ["widget"],
            name: "Drop-down menu",
            module: "alfresco/menus/AlfMenuBarPopup",
            // This is the initial configuration that will be provided when the widget
            // is dropped into the drop-zone...
            defaultConfig: {
               label: "default",
               iconClass: "",
               altText: ""
            },
            // These are the widgets used to configure the dropped widget.
            widgetsForConfig: [
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     name: "defaultConfig.label",
                     label: "Label",
                     value: "default",
                  }
               },
               {
                  name: "alfresco/forms/controls/DojoSelect",
                  config: {
                     name: "defaultConfig.iconClass",
                     label: "Icon",
                     value: "",
                     optionsConfig: {
                        fixed: [
                           {label:"None",value:""},
                           {label:"Configure",value:"alf-configure-icon"},
                           {label:"Invite User",value:"alf-user-icon"},
                           {label:"Upload",value:"alf-upload-icon"},
                           {label:"Create",value:"alf-create-icon"},
                           {label:"All Selected",value:"alf-allselected-icon"},
                           {label:"Some Selected",value:"alf-someselected-icon"},
                           {label:"None Selected",value:"alf-noneselected-icon"},
                           {label:"Back",value:"alf-back-icon"},
                           {label:"Forward",value:"alf-forward-icon"}
                        ]
                     }
                  }
               },
               {
                  name: "alfresco/forms/controls/DojoTextarea",
                  config: {
                     name: "defaultConfig.altText",
                     label: "Alt Text",
                     value: "",
                  }
               }
            ],
            // If set to true, then the actual widget will be previewed...
            previewWidget: false,
            // This is the widget structure to use to display the widget.
            widgetsForDisplay: [
               {
                  name: "alfresco/creation/DropZone",
                  config: {
                     horizontal: false
                  }
               }
            ]
         },
         {
            type: ["widget"],
            name: "Menu Group",
            module: "alfresco/menus/AlfMenuGroup",
            // This is the initial configuration that will be provided when the widget
            // is dropped into the drop-zone...
            defaultConfig: {
               label: "default"
            },
            // These are the widgets used to configure the dropped widget.
            widgetsForConfig: [
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     name: "defaultConfig.label",
                     label: "Label",
                     value: "default",
                  }
               }
            ],
            // If set to true, then the actual widget will be previewed...
            previewWidget: false,
            // This is the widget structure to use to display the widget.
            widgetsForDisplay: [
               {
                  name: "alfresco/creation/DropZone",
                  config: {
                     horizontal: false
                  }
               }
            ]
         },
         {
            type: ["widget"],
            name: "Menu Item",
            module: "alfresco/menus/AlfMenuItem",
            // This is the initial configuration that will be provided when the widget
            // is dropped into the drop-zone...
            defaultConfig: {
               label: "default",
               iconClass: "",
               altText: "",
               publishTopic: "",
               publishPayload: ""
            },
            // These are the widgets used to configure the dropped widget.
            widgetsForConfig: [
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     name: "defaultConfig.label",
                     label: "Label",
                     value: "default",
                  }
               },
               {
                  name: "alfresco/forms/controls/DojoSelect",
                  config: {
                     name: "defaultConfig.iconClass",
                     label: "Icon",
                     value: "",
                     optionsConfig: {
                        fixed: [
                           {label:"None",value:""},
                           {label:"Edit",value:"alf-edit-icon"},
                           {label:"Configure",value:"alf-cog-icon"},
                           {label:"Leave",value:"alf-leave-icon"},
                           {label:"User",value:"alf-profile-icon"},
                           {label:"Password",value:"alf-password-icon"},
                           {label:"Help",value:"alf-help-icon"},
                           {label:"Logout",value:"alf-logout-icon"},
                           {label:"Simple List",value:"alf-simplelist-icon"},
                           {label:"Detailed List",value:"alf-detailedlist-icon"},
                           {label:"Gallery",value:"alf-gallery-icon"},
                           {label:"Show Folders",value:"alf-showfolders-icon"},
                           {label:"Show Path",value:"alf-showpath-icon"},
                           {label:"Show Sidebar",value:"alf-showsidebar-icon"},
                           {label:"Text",value:"alf-textdoc-icon"},
                           {label:"HTML Selected",value:"alf-htmldoc-icon"},
                           {label:"XML",value:"alf-xmldoc-icon"}
                        ]
                     }
                  }
               },
               {
                  name: "alfresco/forms/controls/DojoTextarea",
                  config: {
                     name: "defaultConfig.altText",
                     label: "Alt Text",
                     value: "",
                  }
               },
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     name: "defaultConfig.publishTopic",
                     label: "Publish Topic",
                     value: "",
                  }
               },
               {
                  name: "alfresco/forms/controls/MultipleKeyValuePairFormControl",
                  config: {
                     name: "defaultConfig.publishPayload",
                     label: "Publish Payload",
                     value: ""
                  }
               }
            ],
            // If set to true, then the actual widget will be previewed...
            previewWidget: false,
            // This is the widget structure to use to display the widget.
            widgetsForDisplay: [
               {
                  name: "alfresco/creation/DropZone",
                  config: {
                     horizontal: true
                  }
               }
            ]
         },
         {
            type: ["widget"],
            name: "Cascading Menu",
            module: "alfresco/menus/AlfCascadingMenu",
            // This is the initial configuration that will be provided when the widget
            // is dropped into the drop-zone...
            defaultConfig: {
               label: "default"
            },
            // These are the widgets used to configure the dropped widget.
            widgetsForConfig: [
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     name: "defaultConfig.label",
                     label: "Label",
                     value: "default",
                  }
               }
            ],
            // If set to true, then the actual widget will be previewed...
            previewWidget: false,
            // This is the widget structure to use to display the widget.
            widgetsForDisplay: [
               {
                  name: "alfresco/creation/DropZone",
                  config: {
                     horizontal: false
                  }
               }
            ]
         },
         {
            type: ["widget"],
            name: "Title, Description And Content",
            module: "alfresco/layout/TitleDescriptionAndContent",
            // This is the initial configuration that will be provided when the widget
            // is dropped into the drop-zone...
            defaultConfig: {
               title: "title",
               description: "description"
            },
            // These are the widgets used to configure the dropped widget.
            widgetsForConfig: [
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     name: "defaultConfig.title",
                     label: "Title",
                     value: "title",
                  }
               },
               {
                  name: "alfresco/forms/controls/DojoTextarea",
                  config: {
                     name: "defaultConfig.description",
                     label: "Description",
                     value: "description",
                  }
               }
            ],
            // If set to true, then the actual widget will be previewed...
            previewWidget: false,
            // This is the widget structure to use to display the widget.
            widgetsForDisplay: [
               {
                  name: "alfresco/creation/DropZone",
                  config: {
                     horizontal: false
                  }
               }
            ]
         },
         {
            type: ["widget"],
            name: "Sliding Tabs",
            module: "alfresco/layout/SlidingTabs",
            // This is the initial configuration that will be provided when the widget
            // is dropped into the drop-zone...
            defaultConfig: {},
            // These are the widgets used to configure the dropped widget.
            widgetsForConfig: [],
            // If set to true, then the actual widget will be previewed...
            previewWidget: false,
            // This is the widget structure to use to display the widget.
            widgetsForDisplay: [
               {
                  name: "alfresco/creation/DropZone",
                  config: {
                     horizontal: false,
                     widgetsForNestedConfig: [
                         {
                            name: "alfresco/forms/controls/DojoValidationTextBox",
                            config: {
                               name: "additionalConfig.title",
                               label: "Tab Title",
                               value: "title",
                            }
                         }
                      ]
                  }
               }
            ]
            
         }
      ]
   });
});