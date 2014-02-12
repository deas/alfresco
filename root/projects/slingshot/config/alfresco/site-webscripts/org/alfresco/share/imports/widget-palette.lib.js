/* *********************************************************************************
 *                                                                                 *
 * GENERAL FUNCTIONS                                                               *
 *                                                                                 *
 ***********************************************************************************/

function getDocumentsSourceConfigWidgets() {
   return [{
      name: "alfresco/forms/controls/DojoRadioButtons",
      config: {
         name: "defaultConfig.type",
         label: "All or Just Documents",
         value: "all",
         optionsConfig: {
            fixed: [
               {label:"All",value:"all"},
               {label:"Documents",value:"documents"}
            ]
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoRadioButtons",
      config: {
         fieldId: "selectDataSource",
         name: "defaultConfig.dataSource",
         label: "Data Source",
         value: "siteToken",
         optionsConfig: {
            fixed: [
               {label:"Well known node",value:"wellKnown"},
               {label:"Specific Site",value:"specificSite"},
               {label:"Site URL token",value:"siteToken"},
               {label:"NodeRef URL tokens",value:"nodeRefTokens"},
               {label:"Custom NodeRef",value:"customNodeRef"}
            ]
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoSelect",
      config: {
         name: "defaultConfig.nodeRef",
         label: "Select well known node",
         value: "siteToken",
         postWhenHiddenOrDisabled: false,
         noValueUpdateWhenHiddenOrDisabled: true,
         optionsConfig: {
            fixed: [
               {label:"Company home",value:"alfresco://company/home"},
               {label:"User Home",value:"alfresco://user/home"},
               {label:"Shared Files",value:"alfresco://company/shared"}
            ]
         },
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "selectDataSource",
                  is: ["wellKnown"]
               }
            ]
         },
         requirementConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "selectDataSource",
                  is: ["wellKnown"]
               }
            ]
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoValidationTextBox",
      config: {
         name: "defaultConfig.nodeRef",
         description: "Enter a custom NodeRef in the form <store_type>://<store_id>/<id>",
         label: "Custom NodeRef",
         value: "",
         postWhenHiddenOrDisabled: false,
         noValueUpdateWhenHiddenOrDisabled: true,
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "selectDataSource",
                  is: ["customNodeRef"]
               }
            ]
         },
         requirementConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "selectDataSource",
                  is: ["customNodeRef"]
               }
            ]
         },
         validationConfig: {
            regex: "^[A-Za-z0-9-]+://[A-Za-z0-9-]+/[A-Za-z0-9-]+$"
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoValidationTextBox",
      config: {
         name: "defaultConfig.site",
         label: "Specific Site Shortname",
         value: "",
         postWhenHiddenOrDisabled: false,
         noValueUpdateWhenHiddenOrDisabled: true,
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "selectDataSource",
                  is: ["specificSite"]
               }
            ]
         },
         requirementConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "selectDataSource",
                  is: ["specificSite"]
               }
            ]
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoValidationTextBox",
      config: {
         name: "defaultConfig.site",
         label: "Site URL token",
         value: "$$site$$",
         postWhenHiddenOrDisabled: false,
         noValueUpdateWhenHiddenOrDisabled: true,
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "selectDataSource",
                  is: ["siteToken"]
               }
            ]
         },
         requirementConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "selectDataSource",
                  is: ["siteToken"]
               }
            ]
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoValidationTextBox",
      config: {
         name: "defaultConfig.nodeRef",
         label: "NodeRef URL Tokens",
         value: "$$store_type$$://$$store_id$$/$$id$$",
         postWhenHiddenOrDisabled: false,
         noValueUpdateWhenHiddenOrDisabled: true,
         visibilityConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "selectDataSource",
                  is: ["nodeRefTokens"]
               }
            ]
         },
         requirementConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "selectDataSource",
                  is: ["nodeRefTokens"]
               }
            ]
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoValidationTextBox",
      config: {
         name: "defaultConfig.container",
         label: "Container Type",
         value: "documentlibrary"
      }
   },
   {
      name: "alfresco/forms/controls/DojoValidationTextBox",
      config: {
         name: "defaultConfig.page",
         label: "Page",
         value: "1",
         requirementConfig: {
            initialValue: true
         },
         validationConfig: {
            regex: "^[0-9]+$"
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoRadioButtons",
      config: {
         fieldId: "selectFilter",
         name: "defaultConfig.filter.filterId",
         label: "Filter",
         value: "path",
         optionsConfig: {
            fixed: [
               {label:"Path",value:"path"},
               {label:"All",value:"all"}
            ]
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoValidationTextBox",
      config: {
         name: "defaultConfig.filter.filterData",
         label: "Path",
         value: "/",
         postWhenHiddenOrDisabled: false,
         visibilityConfig: {
            initialValue: true,
            rules: [
               {
                  targetId: "selectFilter",
                  is: ["path"]
               }
            ]
         },
         requirementConfig: {
            initialValue: false,
            rules: [
               {
                  targetId: "selectFilter",
                  is: ["path"]
               }
            ]
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoValidationTextBox",
      config: {
         name: "defaultConfig.pageSize",
         label: "Number of results",
         value: "25",
         requirementConfig: {
            initialValue: true
         },
         validationConfig: {
            regex: "^[0-9]+$"
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoSelect",
      config: {
         name: "defaultConfig.sortAscending",
         label: "Sort Order",
         value: "true",
         optionsConfig: {
            fixed: [
               {label:"Ascending",value:"true"},
               {label:"Descending",value:"false"}
            ]
         }
      }
   },
   {
      name: "alfresco/forms/controls/DojoSelect",
      config: {
         name: "defaultConfig.sortField",
         label: "Sort Property",
         value: "all",
         optionsConfig: {
            fixed: [
               {label:"Name",value:"cm:name"},
               {label:"Popularity",value:"cm:likesRatingSchemeCount"},
               {label:"Last Modification",value:"cm:modified"},
               {label:"Size",value:"cm:content.size"}
            ]
         }
      }
   }];
}

/* *********************************************************************************
 *                                                                                 *
 * PUBLICATIONS                                                                    *
 *                                                                                 *
 ***********************************************************************************/

function getRetrieveSingleDocumentPublication() {
   return {
      type: ["publication"],
      name: "Retrieve Single Document",
      module: "ALF_RETRIEVE_SINGLE_DOCUMENT_REQUEST",
      itemNameKey: "publishTopic",
      itemConfigKey: "publishPayload",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         nodeRef: "alfresco://company/home"
      },
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoRadioButtons",
            config: {
               fieldId: "selectDataSource",
               name: "defaultConfig.dataSource",
               label: "Data Source",
               value: "nodeRefTokens",
               optionsConfig: {
                  fixed: [
                     {label:"NodeRef URL tokens",value:"nodeRefTokens"},
                     {label:"Custom NodeRef",value:"customNodeRef"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.nodeRef",
               label: "NodeRef URL Tokens",
               value: "$$store_type$$://$$store_id$$/$$id$$",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               visibilityConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["nodeRefTokens"]
                     }
                  ]
               },
               requirementConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["nodeRefTokens"]
                     }
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.nodeRef",
               description: "Enter a custom NodeRef in the form <store_type>://<store_id>/<id>",
               label: "Custom NodeRef",
               value: "",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               visibilityConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["customNodeRef"]
                     }
                  ]
               },
               requirementConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["customNodeRef"]
                     }
                  ]
               },
               validationConfig: {
                  regex: "^[A-Za-z0-9-]+://[A-Za-z0-9-]+/[A-Za-z0-9-]+$"
               }
            }
         }
      ],
      // If set to true, then the actual widget will be previewed...
      previewWidget: false,
      // This is the widget structure to use to display the widget.
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Retrieve Single Document"
            }
         }
      ]
   }
}

function getRetrieveDocumentsPublication() {
   return {
      type: ["publication"],
      name: "Retrieve Documents",
      module: "ALF_RETRIEVE_DOCUMENTS_REQUEST",
      itemNameKey: "publishTopic",
      itemConfigKey: "publishPayload",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         path: "/",
         type: "all",
         site: "$$site$$",
         container: "documentlibrary",
         page: "1",
         pageSize: "25",
         sortAscending: "false",
         sortField: "cm:name",
         filter: {
            filterId: "path",
            filterData: ""
         }
      },
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [
         // {
         //    name: "alfresco/forms/controls/DojoValidationTextBox",
         //    config: {
         //       name: "defaultConfig.path",
         //       label: "Path",
         //       value: "/"
         //    }
         // },
         {
            name: "alfresco/forms/controls/DojoRadioButtons",
            config: {
               name: "defaultConfig.type",
               label: "All or Just Documents",
               value: "all",
               optionsConfig: {
                  fixed: [
                     {label:"All",value:"all"},
                     {label:"Documents",value:"documents"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoRadioButtons",
            config: {
               fieldId: "selectDataSource",
               name: "defaultConfig.dataSource",
               label: "Data Source",
               value: "siteToken",
               optionsConfig: {
                  fixed: [
                     {label:"Well known node",value:"wellKnown"},
                     {label:"Specific Site",value:"specificSite"},
                     {label:"Site URL token",value:"siteToken"},
                     {label:"NodeRef URL tokens",value:"nodeRefTokens"},
                     {label:"Custom NodeRef",value:"customNodeRef"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.nodeRef",
               label: "Select well known node",
               value: "siteToken",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               optionsConfig: {
                  fixed: [
                     {label:"Company home",value:"alfresco://company/home"},
                     {label:"User Home",value:"alfresco://user/home"},
                     {label:"Shared Files",value:"alfresco://company/shared"}
                  ]
               },
               visibilityConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["wellKnown"]
                     }
                  ]
               },
               requirementConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["wellKnown"]
                     }
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.nodeRef",
               description: "Enter a custom NodeRef in the form <store_type>://<store_id>/<id>",
               label: "Custom NodeRef",
               value: "",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               visibilityConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["customNodeRef"]
                     }
                  ]
               },
               requirementConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["customNodeRef"]
                     }
                  ]
               },
               validationConfig: {
                  regex: "^[A-Za-z0-9-]+://[A-Za-z0-9-]+/[A-Za-z0-9-]+$"
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.site",
               label: "Specific Site Shortname",
               value: "",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               visibilityConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["specificSite"]
                     }
                  ]
               },
               requirementConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["specificSite"]
                     }
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.site",
               label: "Site URL token",
               value: "$$site$$",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               visibilityConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["siteToken"]
                     }
                  ]
               },
               requirementConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["siteToken"]
                     }
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.nodeRef",
               label: "NodeRef URL Tokens",
               value: "$$store_type$$://$$store_id$$/$$id$$",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               visibilityConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["nodeRefTokens"]
                     }
                  ]
               },
               requirementConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectDataSource",
                        is: ["nodeRefTokens"]
                     }
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.container",
               label: "Container Type",
               value: "documentlibrary"
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.page",
               label: "Page",
               value: "1",
               requirementConfig: {
                  initialValue: true
               },
               validationConfig: {
                  regex: "^[0-9]+$"
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoRadioButtons",
            config: {
               fieldId: "selectFilter",
               name: "defaultConfig.filter.filterId",
               label: "Filter",
               value: "path",
               optionsConfig: {
                  fixed: [
                     {label:"Path",value:"path"},
                     {label:"All",value:"all"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.filter.filterData",
               label: "Path",
               value: "/",
               postWhenHiddenOrDisabled: false,
               visibilityConfig: {
                  initialValue: true,
                  rules: [
                     {
                        targetId: "selectFilter",
                        is: ["path"]
                     }
                  ]
               },
               requirementConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "selectFilter",
                        is: ["path"]
                     }
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.pageSize",
               label: "Number of results",
               value: "25",
               requirementConfig: {
                  initialValue: true
               },
               validationConfig: {
                  regex: "^[0-9]+$"
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.sortAscending",
               label: "Sort Order",
               value: "true",
               optionsConfig: {
                  fixed: [
                     {label:"Ascending",value:"true"},
                     {label:"Descending",value:"false"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.sortField",
               label: "Sort Property",
               value: "all",
               optionsConfig: {
                  fixed: [
                     {label:"Name",value:"cm:name"},
                     {label:"Popularity",value:"cm:likesRatingSchemeCount"},
                     {label:"Last Modification",value:"cm:modified"},
                     {label:"Size",value:"cm:content.size"}
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
            name: "alfresco/html/Label",
            config: {
               label: "Retrieve Documents"
            }
         }
      ]
   };
}

function getDialogRequestPublication() {
   return {
      type: ["publication"],
      name: "Dialog Request",
      module: "ALF_CREATE_FORM_DIALOG_REQUEST",
      itemNameKey: "publishTopic",
      itemConfigKey: "publishPayload",
      itemDroppedItemsKey: "publishPayload.widgets",
      defaultConfig: {
         dialogTitle: "Default Title",
         dialogConfirmationButtonTitle: "OK",
         dialogCancellationButtonTitle: "Cancel",
         formSubmissionTopic: "ALF_CREATE_CONTENT_REQUEST"
      },
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               fieldId: "test",
               name: "defaultConfig.dialogTitle",
               label: "Dialog Title",
               value: "Default Title"
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.dialogConfirmationButtonTitle",
               label: "Confirmation Button Label",
               value: "OK"
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.dialogCancellationButtonTitle",
               label: "Cancellation Button Label",
               value: "Cancel"
            }
         },
         {
            name: "alfresco/forms/controls/DojoRadioButtons",
            config: {
               name: "defaultConfig.formSubmissionTopic",
               label: "Dialog Confirmation Topic",
               value: "ALF_CREATE_CONTENT_REQUEST",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               optionsConfig: {
                  fixed: [
                     {label:"Create content",value:"ALF_CREATE_CONTENT_REQUEST"},
                     {label:"Update content",value:"ALF_UPDATE_CONTENT_REQUEST"}
                  ]
               }
            }
         }
      ],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               acceptTypes: ["widget"],
               horizontal: false
            }
         }
      ]
   };
}

function getNavigationRequestPublication() {
   return {
      type: ["publication"],
      name: "Navigation Request",
      module: "ALF_NAVIGATE_TO_PAGE",
      itemNameKey: "publishTopic",
      itemConfigKey: "publishPayload",
      itemDroppedItemsKey: "publishPayload.widgets",
      defaultConfig: {
         url: "",
         type: "SHARE_PAGE_RELATIVE",
         target: "CURRENT"
      },
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.url",
               label: "The URL to use",
               value: "",
               requirementConfig: {
                  initialValue: true
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoRadioButtons",
            config: {
               name: "defaultConfig.type",
               label: "URL type",
               value: "SHARE_PAGE_RELATIVE",
               optionsConfig: {
                  fixed: [
                     {label:"Relative to Alfresco Share pages",value:"SHARE_PAGE_RELATIVE"},
                     {label:"Relative to Share application",value:"CONTEXT_RELATIVE"},
                     {label:"A full external path",value:"FULL_PATH"},
                     {label:"Hash the current URL",value:"HASH"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoRadioButtons",
            config: {
               name: "defaultConfig.target",
               label: "Where to load URL",
               value: "CURRENT",
               optionsConfig: {
                  fixed: [
                     {label:"Current Window",value:"CURRENT"},
                     {label:"New Window",value:"NEW"}
                  ]
               }
            }
         }
      ],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Navigate"
            }
         }
      ]
   };
}

function getAllPublications() {
   return [
      getRetrieveSingleDocumentPublication(),
      getRetrieveDocumentsPublication(),
      getDialogRequestPublication(),
      getNavigationRequestPublication()
   ];
}

/* *********************************************************************************
 *                                                                                 *
 * SERVICES                                                                        *
 *                                                                                 *
 ***********************************************************************************/

function getNavigationService() {
   return {
      type: ["service"],
      name: "Navigation Service",
      module: "alfresco/services/NavigationService",
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
            name: "alfresco/html/Label",
            config: {
               label: "Navigation Service"
            }
         }
      ]
   };
}

function getActionService() {
   return {
      type: ["service"],
      name: "Action Service",
      module: "alfresco/services/ActionService",
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
            name: "alfresco/html/Label",
            config: {
               label: "Action Service"
            }
         }
      ]
   };
}

function getContentService() {
   return {
      type: ["service"],
      name: "Content Service",
      module: "alfresco/services/ContentService",
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
            name: "alfresco/html/Label",
            config: {
               label: "Content Service"
            }
         }
      ]
   };
}

function getDocumentService() {
   return {
      type: ["service"],
      name: "Document Service",
      module: "alfresco/services/DocumentService",
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
            name: "alfresco/html/Label",
            config: {
               label: "Document Service"
            }
         }
      ]
   };
}

function getDialogService() {
   return {
      type: ["service"],
      name: "Dialog Service",
      module: "alfresco/dialogs/AlfDialogService",
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
            name: "alfresco/html/Label",
            config: {
               label: "Dialog Service"
            }
         }
      ]
   };
}

function getLoggingService() {
   return {
      type: ["service"],
      name: "Logging Service",
      module: "alfresco/services/LoggingService",
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
            name: "alfresco/html/Label",
            config: {
               label: "Logging Service"
            }
         }
      ]
   };
}

function getPageService() {
   return {
      type: ["service"],
      name: "Page Service",
      module: "alfresco/services/PageService",
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
            name: "alfresco/html/Label",
            config: {
               label: "Page Service"
            }
         }
      ]
   };
}

function getPreferenceService() {
   return {
      type: ["service"],
      name: "Preference Service",
      module: "alfresco/services/PreferenceService",
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
            name: "alfresco/html/Label",
            config: {
               label: "Preference Service"
            }
         }
      ]
   };
}

function getSiteService() {
   return {
      type: ["service"],
      name: "Site Service",
      module: "alfresco/services/SiteService",
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
            name: "alfresco/html/Label",
            config: {
               label: "Site Service"
            }
         }
      ]
   };
}

function getUserService() {
   return {
      type: ["service"],
      name: "User Service",
      module: "alfresco/services/UserService",
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
            name: "alfresco/html/Label",
            config: {
               label: "User Service"
            }
         }
      ]
   };
}

function getAllServices() {
   return [
      getNavigationService(),
      getActionService(),
      getContentService(),
      getDocumentService(),
      getDialogService(),
      getLoggingService(),
      getPageService(),
      getPreferenceService(),
      getSiteService(),
      getUserService()
   ];
}

/* *********************************************************************************
 *                                                                                 *
 * GENERAL WIDGETS                                                                 *
 *                                                                                 *
 ***********************************************************************************/

function getSetTitleWidget() {
   return {
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
   };
}

function getLogoWidget() {
   return {
      type: ["widget"],
      name: "Logo",
      module: "alfresco/logo/Logo",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         logoClasses: "alfresco-logo-large"
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
                     {label:"Standard Alfresco",value:"alfresco-logo-large"},
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
   };
}

function getGeneralWidgets() {
   return [
      getSetTitleWidget(),
      getLogoWidget()
   ];
}

/* *********************************************************************************
 *                                                                                 *
 * DOCUMENT LIST WIDGETS                                                           *
 *                                                                                 *
 ***********************************************************************************/

function getDocumentWidget() {
   return {
      type: ["widget"],
      name: "Document",
      module: "alfresco/documentlibrary/AlfDocument",
      defaultConfig: {},
      widgetsForConfig: [],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getDocumentListWidget() {
   return {
      type: ["widget"],
      name: "Document List",
      module: "alfresco/documentlibrary/AlfDocumentList",
      defaultConfig: {
         path: "/",
         type: "all",
         site: "$$site$$",
         container: "documentlibrary",
         page: "1",
         pageSize: "25",
         sortAscending: "false",
         sortField: "cm:name",
         filter: {
            filterId: "path",
            filterData: ""
         }
      },
      widgetsForConfig: getDocumentsSourceConfigWidgets(),
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getAbstractDocListViewWidget() {
   return {
      type: ["widget"],
      name: "Abstract Document List View",
      module: "alfresco/documentlibrary/views/AlfDocumentListView",
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
               horizontal: true
            }
         }
      ]
   };
}

function getDocListRowWidget() {
   return {
      type: ["widget"],
      name: "Row (for Document List View)",
      module: "alfresco/documentlibrary/views/layouts/Row",
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
               horizontal: true
            }
         }
      ]
   };
}

function getDocListColumnWidget() {
   return {
      type: ["widget"],
      name: "Column (for Document List View)",
      module: "alfresco/documentlibrary/views/layouts/Column",
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
               horizontal: true
            }
         }
      ]
   };
}

function getDocListCellWidget() {
   return {
      type: ["widget"],
      name: "Cell (for Document List View)",
      module: "alfresco/documentlibrary/views/layouts/Cell",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         width: ""
      },
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.width",
               label: "Width",
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
   };
}

function getPropertyWidget() {
   return {
      type: ["widget"],
      name: "Property (for Document List View)",
      module: "alfresco/renderers/Property",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         propertyToRender: "node.properties.cm:name",
         postParam: "prop_cm_name",
         renderSize: "medium",
         renderAsLink: false,
         renderedValuePrefix: "",
         renderedValueSuffix: ""
      },
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.propertyToRender",
               label: "Property to render",
               value: "node.properties.cm:name",
               optionsConfig: {
                  fixed: [
                     {label:"Name",value:"node.properties.cm:name"},
                     {label:"Title",value:"node.properties.cm:title"},
                     {label:"Description",value:"node.properties.cm:description"},
                     {label:"Version",value:"node.properties.cm:versionLabel"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.postParam",
               label: "Parameter to post",
               value: "prop_cm_name",
               optionsConfig: {
                  fixed: [
                     {label:"Name",value:"prop_cm_name"},
                     {label:"Title",value:"prop_cm_title"},
                     {label:"Description",value:"prop_cm_description"},
                     {label:"Version",value:"prop_cm_versionLabel"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.renderSize",
               label: "Render Size",
               value: "medium",
               optionsConfig: {
                  fixed: [
                     {label:"Small",value:"small"},
                     {label:"Medium",value:"medium"},
                     {label:"Large",value:"large"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoCheckBox",
            config: {
               fieldId: "renderAsLink",
               name: "defaultConfig.renderAsLink",
               label: "Render as a link",
               value: false
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               fieldId: "linkType",
               name: "defaultConfig.linkType",
               label: "Link type",
               value: "default",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               optionsConfig: {
                  fixed: [
                     {label:"Default",value:"default"},
                     {label:"Custom",value:"custom"}
                  ]
               },
               visibilityConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "renderAsLink",
                        is: [true]
                     }
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.customDetailsURL",
               description: "Enter a custom URL to link to. This will be relative to 'share/page' and will automatically be appended with the NodeRef",
               label: "Custom Link URL",
               value: "",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               visibilityConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "linkType",
                        is: ["custom"]
                     }
                  ]
               },
               requirementConfig: {
                  initialValue: false,
                  rules: [
                     {
                        targetId: "linkType",
                        is: ["custom"]
                     }
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.renderedValuePrefix",
               label: "Prefix",
               value: ""
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.renderedValueSuffix",
               label: "Suffix",
               value: ""
            }
         }
      ],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getInlineEditPropertyWidget() {
   return {
      type: ["widget"],
      name: "Editable Property (for Document List View)",
      module: "alfresco/renderers/InlineEditProperty",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         propertyToRender: "node.properties.cm:name",
         postParam: "prop_cm_name",
         renderSize: "medium",
         renderAsLink: false,
         renderedValuePrefix: "",
         renderedValueSuffix: ""
      },
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.propertyToRender",
               label: "Property to render",
               value: "node.properties.cm:name",
               optionsConfig: {
                  fixed: [
                     {label:"Name",value:"node.properties.cm:name"},
                     {label:"Title",value:"node.properties.cm:title"},
                     {label:"Description",value:"node.properties.cm:description"},
                     {label:"Version",value:"node.properties.cm:versionLabel"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.postParam",
               label: "Parameter to post",
               value: "prop_cm_name",
               optionsConfig: {
                  fixed: [
                     {label:"Name",value:"prop_cm_name"},
                     {label:"Title",value:"prop_cm_title"},
                     {label:"Description",value:"prop_cm_description"},
                     {label:"Version",value:"prop_cm_versionLabel"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.renderSize",
               label: "Render Size",
               value: "medium",
               optionsConfig: {
                  fixed: [
                     {label:"Small",value:"small"},
                     {label:"Medium",value:"medium"},
                     {label:"Large",value:"large"}
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoCheckBox",
            config: {
               name: "defaultConfig.renderAsLink",
               label: "Render as a link",
               value: false
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.renderedValuePrefix",
               label: "Prefix",
               value: ""
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.renderedValueSuffix",
               label: "Suffix",
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
   };
}

function getThumbnailWidget() {
   return {
      type: ["widget"],
      name: "Thumbnail (for Document List View)",
      module: "alfresco/renderers/Thumbnail",
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
               horizontal: true
            }
         }
      ]
   };
}

function getDocListActionsWidget() {
   return {
      type: ["widget"],
      name: "Actions (for Document List View)",
      module: "alfresco/renderers/Actions",
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
               horizontal: true
            }
         }
      ]
   };
}

function getDocListSelectorWidget() {
   return {
      type: ["widget"],
      name: "Selector (for Document List View)",
      module: "alfresco/renderers/Selector",
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
               horizontal: true
            }
         }
      ]
   };
}

function getDocListIndicatorsWidget() {
   return {
      type: ["widget"],
      name: "Indicators (for Document List View)",
      module: "alfresco/renderers/Indicators",
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
               horizontal: true
            }
         }
      ]
   };
}

function getDocListDateWidget() {
   return {
      type: ["widget"],
      name: "Date (for Document List View)",
      module: "alfresco/renderers/Date",
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
               horizontal: true
            }
         }
      ]
   };
}

function getDocListSizeWidget() {
   return {
      type: ["widget"],
      name: "Size (for Document List View)",
      module: "alfresco/renderers/Size",
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
               horizontal: true
            }
         }
      ]
   };
}

function getDocListDetailedView() {
   return {
      type: ["widget"],
      name: "Detailed Document List View",
      module: "alfresco/documentlibrary/views/AlfDetailedView",
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
            name: "alfresco/html/Label",
            config: {
               label: "Detailed View"
            }
         }
      ]
   };
}

function getDocListSimpleView() {
   return {
      type: ["widget"],
      name: "Simple Document List View",
      module: "alfresco/documentlibrary/views/AlfSimpleView",
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
            name: "alfresco/html/Label",
            config: {
               label: "Simple View"
            }
         }
      ]
   };
}

function getDocumentPreview() {
   return {
      type: ["widget"],
      name: "Document Preview",
      module: "alfresco/preview/AlfDocumentPreview",
      defaultConfig: {},
      widgetsForConfig: [],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Preview"
            }
         }
      ]
   };
}

function getFileTypeWidget() {
   return {
      type: ["widget"],
      name: "File Type Image",
      module: "alfresco/renderers/FileType",
      defaultConfig: {},
      widgetsForConfig: [],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "FileType"
            }
         }
      ]
   };
}

function getAllDocListWidgets() {
   return [
      getDocumentWidget(),
      getDocumentPreview(),
      getDocumentListWidget(),
      getDocListSimpleView(),
      getDocListDetailedView(),
      getAbstractDocListViewWidget(),
      getDocListRowWidget(),
      getDocListCellWidget(),
      getDocListColumnWidget(),
      getPropertyWidget(),
      getInlineEditPropertyWidget(),
      getDocListSelectorWidget(),
      getDocListIndicatorsWidget(),
      getThumbnailWidget(),
      getFileTypeWidget(),
      getDocListSizeWidget(),
      getDocListDateWidget(),
      getDocListActionsWidget()
   ];
}

/* *********************************************************************************
 *                                                                                 *
 * MENU WIDGETS                                                                    *
 *                                                                                 *
 ***********************************************************************************/

function getMenuBarWidget() {
   return {
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
   };
}

function getMenuBarItemWidget() {
   return {
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
               value: "default"
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
   };
}

function getDropDownMenuWidget() {
   return {
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
               value: "default"
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
               horizontal: false
            }
         }
      ]
   };
}

function getMenuGroupWidget() {
   return {
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
               value: "default"
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
   };
}

function getMenuItemWidget() {
   return {
      type: ["widget"],
      name: "Menu Item",
      module: "alfresco/menus/AlfMenuItem",
      itemDroppedMixinKey: "0",
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
               value: "default"
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
               value: ""
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.publishTopic",
               label: "Publish Topic",
               value: ""
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
            name: "alfresco/creation/PublicationDropZone",
            config: {
               horizontal: true
            }
         }
      ]
   };
}

function getCascadingMenuWidget() {
   return {
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
               value: "default"
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
   };
}

function getAllMenuWidgets() {
   return [
      getMenuBarWidget(),
      getMenuBarItemWidget(),
      getDropDownMenuWidget(),
      getMenuGroupWidget(),
      getMenuItemWidget(),
      getCascadingMenuWidget()
   ];
}

/* *********************************************************************************
 *                                                                                 *
 * LAYOUT WIDGETS                                                                  *
 *                                                                                 *
 ***********************************************************************************/


function getVerticalLayoutWidget() {
   return {
      type: ["widget"],
      name: "Vertical Layout",
      module: "alfresco/layout/VerticalWidgets",
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
               horizontal: false
            }
         }
      ]
   };
}

function getHorizontalLayoutWidget() {
   return {
      type: ["widget"],
      name: "Horizontal Layout",
      module: "alfresco/layout/HorizontalWidgets",
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
               horizontal: false
            }
         }
      ]
   };
}

function getLeftAndRightWidget() {
   return {
      type: ["widget"],
      name: "Sliding Tabs",
      module: "alfresco/layout/LeftAndRight",
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
                     name: "alfresco/forms/controls/DojoSelect",
                     config: {
                        name: "additionalConfig.align",
                        label: "Alignment",
                        value: "left",
                        optionsConfig: {
                           fixed: [
                              {label:"Align Left",value:"left"},
                              {label:"Align Right",value:"right"}
                           ]
                        }
                     }
                  }
               ]
            }
         }
      ]
   };
}

function getTitleDescAndContentWidget() {
   return {
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
               value: "title"
            }
         },
         {
            name: "alfresco/forms/controls/DojoTextarea",
            config: {
               name: "defaultConfig.description",
               label: "Description",
               value: "description"
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
   };
}



function getClassicWindowWidget() {
   return {
      type: ["widget"],
      name: "Classic Window",
      module: "alfresco/layout/ClassicWindow",
      // This is the initial configuration that will be provided when the widget
      // is dropped into the drop-zone...
      defaultConfig: {
         title: "Default Title"
      },
      // These are the widgets used to configure the dropped widget.
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.title",
               label: "Title",
               value: "Default Title"
            }
         }
      ],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: false
            }
         }
      ]
   };
}

function getSideBarWidget() {
   return {
      type: ["widget"],
      name: "Sidebar Container",
      module: "alfresco/layout/AlfSideBarContainer",
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
                     name: "alfresco/forms/controls/DojoSelect",
                     config: {
                        name: "additionalConfig.align",
                        label: "Sidebar or body",
                        value: "sidebar",
                        optionsConfig: {
                           fixed: [
                              {label:"Add to sidebar",value:"sidebar"},
                              {label:"Add to main content",value:"main"}
                           ]
                        }
                     }
                  }
               ]
            }
         }
      ]
   };
}

function getSlidingTabsWidget() {
   return {
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
                        value: "title"
                     }
                  }
               ]
            }
         }
      ]
   };
}


function getAllLayoutWidgets() {
   return [
      getVerticalLayoutWidget(),
      getHorizontalLayoutWidget(),
      getLeftAndRightWidget(),
      getTitleDescAndContentWidget(),
      getSlidingTabsWidget(),
      getClassicWindowWidget(),
      getSideBarWidget()
   ];
}

/* *********************************************************************************
 *                                                                                 *
 * FORM WIDGETS                                                                  *
 *                                                                                 *
 ***********************************************************************************/

function getForm() {
   return {
      type: [ "widget" ],
      name: "Form",
      module: "alfresco/forms/Form",
      iconClass: "checkbox",
      defaultConfig: {
         displayButtons: true,
         okButtonLabel: "OK",
         cancelButtonLabel: "Cancel"
      },
      widgetsForConfig: [
         {
            name: "alfresco/forms/controls/DojoCheckBox",
            config: {
               fieldId: "showFormButtons",
               name: "defaultConfig.displayButtons",
               label: "Display Buttons",
               value: true
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.okButtonLabel",
               label: "Confirmation Button Label",
               value: "OK",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               visibilityConfig: {
                  initialValue: true,
                  rules: [
                     {
                        targetId: "showFormButtons",
                        is: [true]
                     }
                  ]
               },
               requirementConfig: {
                  initialValue: true,
                  rules: [
                     {
                        targetId: "showFormButtons",
                        is: [true]
                     }
                  ]
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "defaultConfig.cancelButtonLabel",
               label: "Cancellation Button Label",
               value: "Cancel",
               postWhenHiddenOrDisabled: false,
               noValueUpdateWhenHiddenOrDisabled: true,
               visibilityConfig: {
                  initialValue: true,
                  rules: [
                     {
                        targetId: "showFormButtons",
                        is: [true]
                     }
                  ]
               },
               requirementConfig: {
                  initialValue: true,
                  rules: [
                     {
                        targetId: "showFormButtons",
                        is: [true]
                     }
                  ]
               }
            }
         }
      ],
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/creation/DropZone",
            config: {
               horizontal: false
            }
         }
      ]
   };
}

function getCommonFormControlConfigWidgets() {
   return [
      {
         name: "alfresco/forms/controls/RandomValueGenerator",
         config: {
            name: "defaultConfig.fieldId",
            visibilityConfig: {
               initialValue: false
            }
         }
      },
      {
         name: "alfresco/forms/controls/DojoValidationTextBox",
         config: {
            name: "defaultConfig.name",
            label: "Post parameter",
            value: "default"
         }
      },
      {
         name: "alfresco/forms/controls/DojoValidationTextBox",
         config: {
            name: "defaultConfig.value",
            label: "Initial Value",
            value: ""
         }
      },
      {
         name: "alfresco/forms/controls/DojoValidationTextBox",
         config: {
            name: "defaultConfig.label",
            label: "Label",
            value: "Default Label"
         }
      },
      {
         name: "alfresco/forms/controls/DojoValidationTextBox",
         config: {
            name: "defaultConfig.description",
            label: "Description",
            value: "Default description"
         }
      },
      {
         name: "alfresco/forms/controls/DojoValidationTextBox",
         config: {
            name: "defaultConfig.unitsLabel",
            label: "Units Label",
            value: "units"
         }
      },
      {
         name: "alfresco/forms/controls/DojoCheckBox",
         config: {
            name: "defaultConfig.visibilityConfig.initialValue",
            label: "Initially visible",
            value: true
         }
      },
      {
         name: "alfresco/forms/creation/FormRulesConfigControl",
         config: {
            name: "defaultConfig.visibilityConfig.rules",
            label: "Dynamic visibility behaviour configuration"
         }
      },
      {
         name: "alfresco/forms/controls/DojoCheckBox",
         config: {
            name: "defaultConfig.requirementConfig.initialValue",
            label: "Initially required",
            value: false
         }
      },
      {
         name: "alfresco/forms/creation/FormRulesConfigControl",
         config: {
            name: "defaultConfig.requirementConfig.rules",
            label: "Dynamic requirement behaviour configuration"
         }
      }
      ,
      {
         name: "alfresco/forms/controls/DojoCheckBox",
         config: {
            name: "defaultConfig.disablementConfig.initialValue",
            label: "Initially disabled",
            value: false
         }
      },
      {
         name: "alfresco/forms/creation/FormRulesConfigControl",
         config: {
            name: "defaultConfig.disablementConfig.rules",
            label: "Dynamic disablement behaviour configuration"
         }
      }
   ];
}


function getTextField() {
   return {
      type: [ "widget" ],
      name: "Text Box",
      module: "alfresco/forms/controls/DojoValidationTextBox",
      iconClass: "textbox",
      defaultConfig: {
         name: "default",
         label: "Text box",
         description: "Default description",
         unitsLabel: "units"
      },
      widgetsForConfig: getCommonFormControlConfigWidgets().concat([
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.validationConfig.regex",
               label: "Validation rules",
               optionsConfig: {
                  fixed: [
                     { label: "None", value: ".*"},
                     { label: "E-mail", value: "^([0-9a-zA-Z]([-.\w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-\w]*[0-9a-zA-Z]\.)+[a-zA-Z]{2,9})$"},
                     { label: "Number", value: "^([0-9]+)$"}
                  ]
               }
            }
         }
      ]),
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Text Box"
            }
         }
      ]
   };
}

function getTextArea() {
   return {
      type: [ "widget" ],
      name: "Text Area",
      module: "alfresco/forms/controls/DojoTextarea",
      iconClass: "textarea",
      defaultConfig: {
         name: "default",
         label: "Text area",
         description: "Default description"
      },
      widgetsForConfig: getCommonFormControlConfigWidgets(),
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Text Area"
            }
         }
      ]
   };
}

function getSelectField() {
   return {
      type: [ "widget" ],
      name: "Select Menu",
      module: "alfresco/forms/controls/DojoSelect",
      iconClass: "dropdown",
      defaultConfig: {
         name: "default",
         label: "Drop down",
         description: "Default description",
         unitsLabel: "units",
         optionsConfig: {
            fixed: [
               { label: "Option1", value: "Value1"},
               { label: "Option2", value: "Value2"}
            ]
         }
      },
      widgetsForConfig: getCommonFormControlConfigWidgets().concat([
         {
            name: "alfresco/forms/controls/MultipleKeyValuePairFormControl",
            config: {
               name: "defaultConfig.optionsConfig.fixed",
               label: "Options"
            }
         }
      ]),
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Select Menu"
            }
         }
      ]
   };
}

function getRadioButtonsField() {
   return {
      type: [ "widget" ],
      name: "Radio Buttons",
      module: "alfresco/forms/controls/DojoRadioButtons",
      defaultConfig: {
         name: "default",
         label: "Radio Buttons",
         description: "Default description",
         unitsLabel: "units",
         optionsConfig: {
            fixed: [
               { label: "Option1", value: "Value1"},
               { label: "Option2", value: "Value2"}
            ]
         }
      },
      widgetsForConfig: getCommonFormControlConfigWidgets().concat([
         {
            name: "alfresco/forms/controls/MultipleKeyValuePairFormControl",
            config: {
               name: "defaultConfig.optionsConfig.fixed",
               label: "Options"
            }
         }
      ]),
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Radio Buttons"
            }
         }
      ]
   };
}

function getCheckBox() {
   return {
      type: [ "widget" ],
      name: "Check box",
      module: "alfresco/forms/controls/DojoCheckBox",
      iconClass: "checkbox",
      defaultConfig: {
         name: "default",
         label: "Check box",
         description: "Default description"
      },
      widgetsForConfig: getCommonFormControlConfigWidgets(),
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "Check Box"
            }
         }
      ]
   };
}

function getAceEditor() {
   return {
      type: [ "widget" ],
      name: "ACE Editor",
      module: "alfresco/forms/controls/AceEditor",
      iconClass: "checkbox",
      defaultConfig: {
         name: "default",
         label: "ACE Editor",
         editMode: "text",
         description: "Default description"
      },
      widgetsForConfig: getCommonFormControlConfigWidgets().concat([
         {
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "defaultConfig.editMode",
               label: "Edit Mode",
               optionsConfig: {
                  fixed: [
                     {label:"Text",value:"text"},
                     {label:"XML",value:"xml"},
                     {label:"JavaScript",value:"javascript"},
                     {label:"FreeMarker",value:"freemarker"},
                     {label:"JSON",value:"json"}
                  ]
               }
            }
         }
      ]),
      previewWidget: false,
      widgetsForDisplay: [
         {
            name: "alfresco/html/Label",
            config: {
               label: "ACE Editor"
            }
         }
      ]
   };
}

function getAllFormWidgets() {
   return [
      getForm(),
      getTextField(),
      getTextArea(),
      getSelectField(),
      getRadioButtonsField(),
      getCheckBox(),
      getAceEditor()
   ];
}
