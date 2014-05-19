<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-footer.lib.js">

// Get the initial header services and widgets...
var services = getHeaderServices(),
    widgets = getHeaderModel(msg.get("faceted-search-config.page.title"));

services.push("alfresco/services/QuaddsService", "alfresco/services/NotificationService");


var main = {
   name: "alfresco/layout/VerticalWidgets",
   config: {
      currentItem: {
         user: _processedUserData
      },
      baseClass: "side-margins",
      widgets: [
         {
            name: "alfresco/html/Spacer",
            config: {
               height: "20px",
               additionalCssClasses: "top-border-beyond-gutters"
            }
         },
         {
            name: "alfresco/layout/HorizontalWidgets",
            config: {
               renderFilterMethod: "ALL",
               renderFilter: [
                  {
                     property: "user.groups.GROUP_ALFRESCO_ADMINISTRATORS",
                     renderOnAbsentProperty: true,
                     values: [false]
                  },
                  {
                     property: "user.groups.GROUP_SEARCH_ADMINISTRATORS",
                     renderOnAbsentProperty: true,
                     values: [false]
                  },
                  {
                     property: "user.isNetworkAdmin",
                     values: [false]
                  }
               ],
               widgets: [
                  {
                     name: "alfresco/header/Warning",
                     config: {
                        warnings: [
                           {
                              message: msg.get("faceted-search-config.page.no-permissions"),
                              level: 3
                           }
                        ]
                     }
                  }
               ]
            }
         },
         {
            name: "alfresco/layout/HorizontalWidgets",
            config: {
               renderFilterMethod: "ANY",
               renderFilter: [
                  {
                     property: "user.groups.GROUP_ALFRESCO_ADMINISTRATORS",
                     values: [true]
                  },
                  {
                     property: "user.groups.GROUP_SEARCH_ADMINISTRATORS",
                     values: [true]
                  },
                  {
                     property: "user.isNetworkAdmin",
                     values: [true]
                  }
               ],
               widgetMarginRight: "10",
               widgets: [
                  {
                     name: "alfresco/layout/VerticalWidgets",
                     config: {
                        widgetMarginBottom: "10",
                        widgets: [
                           {
                              name: "alfresco/layout/VerticalWidgets",
                              className: "add-borders",
                              config: {
                                 widgetMarginBottom: "10",
                                 widgets: [
                                    {
                                       name: "alfresco/buttons/AlfButton",
                                       config: {
                                          label: "Add New Filter",
                                          publishTopic: "ALF_CRUD_FORM_CREATE",
                                          additionalCssClasses: "call-to-action"
                                       }
                                    },
                                    {
                                       name: "alfresco/documentlibrary/QuaddsList",
                                       config: {
                                          quadds: "facets",
                                          widgets: [
                                             {
                                                name: "alfresco/documentlibrary/views/AlfDocumentListView",
                                                config: {
                                                   additionalCssClasses: "no-borders",
                                                   widgets: [
                                                      {
                                                         name: "alfresco/documentlibrary/views/layouts/Row",
                                                         config: {
                                                            widgets: [
                                                               {
                                                                  name: "alfresco/documentlibrary/views/layouts/Cell",
                                                                  config: {
                                                                     width: "",
                                                                     widgets: [
                                                                        {
                                                                           name: "alfresco/renderers/PropertyLink",
                                                                           config: {
                                                                              propertyToRender: "name",
                                                                              publishTopic: "ALF_CRUD_FORM_UPDATE",
                                                                              defaultConfig: {
                                                                                 propertyToRender: "name",
                                                                                 publishTopic: "ALF_CRUD_FORM_UPDATE"
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
                                                }
                                             }
                                          ]
                                       }
                                    }
                                 ]
                              }
                           },
                           {
                              name: "alfresco/html/Label",
                              config: {
                                 label: "A short intro about this new feature here. Yada yada yada."
                              }
                           }
                        ]
                     },
                     widthPx: "300"
                  },
                  {
                     name: "alfresco/layout/VerticalWidgets",
                     config: {
                        widgets: [
                           {
                              name: "alfresco/forms/CrudForm",
                              config: {
                                 createButtonLabel: "Save",
                                 createButtonPublishTopic: "ALF_CREATE_QUADDS_ITEM",
                                 createButtonPublishGlobal: true,
                                 updateButtonLabel: "Save",
                                 updateButtonPublishTopic: "ALF_UPDATE_QUADDS_ITEM",
                                 updateButtonPublishGlobal: true,
                                 deleteButtonLabel: "Delete",
                                 deleteButtonPublishTopic: "ALF_DELETE_QUADDS_ITEM",
                                 deleteButtonPublishGlobal: true,
                                 widgets: [
                                    {
                                       name: "alfresco/forms/controls/DojoValidationTextBox",
                                       config: {
                                          fieldId: "QUADDS_ID",
                                          name: "quadds",
                                          value: "facets",
                                          label: "QuADDS ID",
                                          description: "",
                                          unitsLabel: "",
                                          visibilityConfig: {
                                             initialValue: false,
                                             rules: []
                                          },
                                          requirementConfig: {
                                             initialValue: true,
                                             rules: []
                                          },
                                          disablementConfig: {
                                             initialValue: true,
                                             rules: []
                                          },
                                          validationConfig: {
                                             regex: ".*"
                                          }
                                       }
                                    },
                                    {
                                       name: "alfresco/forms/controls/DojoValidationTextBox",
                                       config: {
                                          fieldId: "FACET_NAME",
                                          name: "name",
                                          value: "",
                                          label: "Filter ID",
                                          placeHolder: "Enter an ID for the filter",
                                          description: "",
                                          unitsLabel: "",
                                          visibilityConfig: {
                                             initialValue: true,
                                             rules: []
                                          },
                                          requirementConfig: {
                                             initialValue: true,
                                             rules: []
                                          },
                                          disablementConfig: {
                                             initialValue: false,
                                             rules: []
                                          },
                                          validationConfig: {
                                             regex: ".*"
                                          }
                                       }
                                    },
                                    {
                                       name: "alfresco/forms/controls/DojoValidationTextBox",
                                       config: {
                                          fieldId: "DISPLAY_NAME",
                                          name: "data.widget.config.label",
                                          value: "",
                                          label: "Display Name",
                                          placeHolder: "Enter the name for the filter",
                                          description: "",
                                          unitsLabel: "",
                                          visibilityConfig: {
                                             initialValue: true,
                                             rules: []
                                          },
                                          requirementConfig: {
                                             initialValue: true,
                                             rules: []
                                          },
                                          disablementConfig: {
                                             initialValue: false,
                                             rules: []
                                          },
                                          validationConfig: {
                                             regex: ".*"
                                          }
                                       }
                                    },
                                    {
                                       name: "alfresco/forms/controls/DojoSelect",
                                       config: {
                                          fieldId: "FACET_QNAME",
                                          name: "data.widget.config.facetQName",
                                          value: "",
                                          label: "Field Name",
                                          description: "Internal property name for content model",
                                          unitsLabel: "",
                                          visibilityConfig: {
                                             initialValue: true,
                                             rules: []
                                          },
                                          requirementConfig: {
                                             initialValue: false,
                                             rules: []
                                          },
                                          disablementConfig: {
                                             initialValue: false,
                                             rules: []
                                          },
                                          optionsConfig: {
                                             // TODO: Currently using hard-coded values - these need to be retrieved from the available properties
                                             fixed: [
                                                {
                                                   label: "MIME Type",
                                                   value: "{http://www.alfresco.org/model/content/1.0}content.mimetype"
                                                },
                                                {
                                                   label: "Description",
                                                   value: "{http://www.alfresco.org/model/content/1.0}description.__"
                                                },
                                                {
                                                   label: "Creator",
                                                   value: "{http://www.alfresco.org/model/content/1.0}creator.__"
                                                },
                                                {
                                                   label: "Modifier",
                                                   value: "{http://www.alfresco.org/model/content/1.0}modifier.__"
                                                }
                                             ]
                                          }
                                       }
                                    },
                                    {
                                       name: "alfresco/forms/controls/DojoSelect",
                                       config: {
                                          fieldId: "DISPLAY_CONTROL",
                                          name: "data.widget.name",
                                          value: "alfresco/search/FacetFilters",
                                          label: "Display Control",
                                          description: "Select the control with which to display the facet filters",
                                          visibilityConfig: {
                                             initialValue: true,
                                             rules: []
                                          },
                                          requirementConfig: {
                                             initialValue: false,
                                             rules: []
                                          },
                                          disablementConfig: {
                                             initialValue: false,
                                             rules: []
                                          },
                                          optionsConfig: {
                                             fixed: [
                                                {
                                                   label: "Standard Filter Control",
                                                   value: "alfresco/search/FacetFilters"
                                                }
                                             ]
                                          }
                                       }
                                    },
                                    {
                                       name: "alfresco/forms/controls/DojoSelect",
                                       config: {
                                          fieldId: "SORTBY",
                                          name: "data.widget.config.sortBy",
                                          value: "ALPHABETICALLY",
                                          label: "Sort by",
                                          description: "Display order of filter items",
                                          unitsLabel: "",
                                          visibilityConfig: {
                                             initialValue: true,
                                             rules: []
                                          },
                                          requirementConfig: {
                                             initialValue: false,
                                             rules: []
                                          },
                                          disablementConfig: {
                                             initialValue: false,
                                             rules: []
                                          },
                                          optionsConfig: {
                                             fixed: [
                                                {
                                                   label: "A-Z",
                                                   value: "ALPHABETICALLY"
                                                },
                                                {
                                                   label: "Results (low to high)",
                                                   value: "ASCENDING"
                                                },
                                                {
                                                   label: "Results (high to low)",
                                                   value: "DESCENDING"
                                                }
                                             ]
                                          }
                                       }
                                    },
                                    {
                                       name: "alfresco/forms/controls/NumberSpinner",
                                       config: {
                                          fieldId: "LIMIT",
                                          name: "data.widget.config.maxFilters",
                                          value: "10",
                                          label: "Limit",
                                          description: "Maximum number of filter terms to display before \"More Choices\" link",
                                          unitsLabel: "",
                                          min: 1,
                                          max: 20,
                                          visibilityConfig: {
                                             initialValue: true,
                                             rules: []
                                          },
                                          requirementConfig: {
                                             initialValue: true,
                                             rules: []
                                          },
                                          disablementConfig: {
                                             initialValue: false,
                                             rules: []
                                          },
                                          validationConfig: {
                                             regex: "^[0-9]+$"
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
            }
         },
      ]
   }
};

widgets.push(main);

// Push services and widgets into the getFooterModel to return with a sticky footer wrapper
model.jsonModel = getFooterModel(services, widgets);