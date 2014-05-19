<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-footer.lib.js">

// Get the initial header services and widgets...
var services = getHeaderServices(),
    widgets = getHeaderModel(msg.get("faceted-search-config.page.title"));

services.push("alfresco/services/QuaddsService");

var main = {
   name: "alfresco/layout/HorizontalWidgets",
   config: {
      widgetMarginLeft: "10",
      widgetMarginRight: "10",
      widgets: [
         {
            name: "alfresco/layout/VerticalWidgets",
            config: {
               widgets: [
                  {
                     name: "alfresco/buttons/AlfButton",
                     config: {
                        label: "Add New Filter",
                        publishTopic: "ALF_CRUD_FORM_CREATE"
                     }
                  },
                  {
                     name: "alfresco/buttons/AlfButton",
                     config: {
                        label: "Refresh",
                        publishTopic: "ALF_DOCLIST_RELOAD_DATA"
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
                        createButtonLabel: "Create",
                        createButtonPublishTopic: "ALF_CREATE_QUADDS_ITEM",
                        createButtonPublishGlobal: true,
                        updateButtonLabel: "Update",
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
                                 label: "Facet Name",
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
                                 description: "Enter the field to facet on",
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
                                 description: "",
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
                                          label: "Alphabetically",
                                          value: "ALPHABETICALLY"
                                       },
                                       {
                                          label: "Hits (ascending)",
                                          value: "ASCENDING"
                                       },
                                       {
                                          label: "Hits (descending)",
                                          value: "DESCENDING"
                                       }
                                    ]
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
};

widgets.push(main);

// Push services and widgets into the getFooterModel to return with a sticky footer wrapper
model.jsonModel = getFooterModel(services, widgets);