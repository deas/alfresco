<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-footer.lib.js">

// Get the initial header services and widgets...
var services = getHeaderServices(),
    widgets = getHeaderModel(msg.get("faceted-search-config.page.title"));

services.push("alfresco/services/CrudService",
              "alfresco/services/NotificationService",
              "alfresco/services/OptionsService");


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
                                       name: "alfresco/lists/AlfList",
                                       config: {
                                          loadDataPublishTopic: "ALF_CRUD_GET_ALL",
                                          loadDataPublishPayload: {
                                             url: "api/solr/facet-config"
                                          },
                                          itemsProperty: "facets",
                                          widgets: [
                                             {
                                                name: "alfresco/documentlibrary/views/AlfDocumentListView",
                                                config: {
                                                   additionalCssClasses: "bordered",
                                                   widgetsForHeader: [
                                                      {
                                                         name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                                         config: {
                                                            label: "",
                                                            sortable: false
                                                         }
                                                      },
                                                      {
                                                         name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                                         config: {
                                                            label: msg.get("faceted-search-config.filterId.label"),
                                                            sortable: false
                                                         }
                                                      },
                                                      {
                                                         name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                                         config: {
                                                            label: msg.get("faceted-search-config.isEnabled.label")
                                                         }
                                                      },
                                                      {
                                                         name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                                         config: {
                                                            label: msg.get("faceted-search-config.isDefault.label")
                                                         }
                                                      },
                                                      {
                                                         name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                                         config: {
                                                            label: ""
                                                         }
                                                      },
                                                      {
                                                         name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                                         config: {
                                                            label: ""
                                                         }
                                                      }
                                                   ],
                                                   widgets: [
                                                      {
                                                         name: "alfresco/documentlibrary/views/layouts/Row",
                                                         config: {
                                                            widgets: [
                                                               {
                                                                  name: "alfresco/documentlibrary/views/layouts/Cell",
                                                                  config: {
                                                                     additionalCssClasses: "mediumpad",
                                                                     width: "50px",
                                                                     widgets: [
                                                                        {
                                                                           name: "alfresco/renderers/Reorder"
                                                                        }
                                                                     ]
                                                                  }
                                                               },
                                                               {
                                                                  name: "alfresco/documentlibrary/views/layouts/Cell",
                                                                  config: {
                                                                     additionalCssClasses: "mediumpad",
                                                                     width: "",
                                                                     widgets: [
                                                                        {
                                                                           name: "alfresco/renderers/PropertyLink",
                                                                           config: {
                                                                              propertyToRender: "filterID",
                                                                              publishTopic: "ALF_CRUD_FORM_UPDATE",
                                                                              defaultConfig: {
                                                                                 propertyToRender: "displayName",
                                                                                 publishTopic: "ALF_CRUD_FORM_UPDATE"
                                                                              }
                                                                           }
                                                                        }
                                                                     ]
                                                                  }
                                                               },
                                                               {
                                                                  name: "alfresco/documentlibrary/views/layouts/Cell",
                                                                  config: {
                                                                     additionalCssClasses: "mediumpad",
                                                                     width: "50px",
                                                                     widgets: [
                                                                        {
                                                                           name: "alfresco/renderers/Boolean",
                                                                           config: {
                                                                              propertyToRender: "isEnabled"
                                                                           }
                                                                        }
                                                                     ]
                                                                  }
                                                               },
                                                               {
                                                                  name: "alfresco/documentlibrary/views/layouts/Cell",
                                                                  config: {
                                                                     additionalCssClasses: "mediumpad",
                                                                     width: "50px",
                                                                     widgets: [
                                                                        {
                                                                           name: "alfresco/renderers/Boolean",
                                                                           config: {
                                                                              propertyToRender: "isDefault"
                                                                           }
                                                                        }
                                                                     ]
                                                                  }
                                                               },
                                                               {
                                                                  name: "alfresco/documentlibrary/views/layouts/Cell",
                                                                  config: {
                                                                     additionalCssClasses: "mediumpad",
                                                                     width: "50px",
                                                                     widgets: [
                                                                        {
                                                                           name: "alfresco/renderers/Property",
                                                                           config: {
                                                                              propertyToRender: "index"
                                                                           }
                                                                        }
                                                                     ]
                                                                  }
                                                               },
                                                               {
                                                                  name: "alfresco/documentlibrary/views/layouts/Cell",
                                                                  config: {
                                                                     additionalCssClasses: "mediumpad",
                                                                     width: "50px",
                                                                     widgets: [
                                                                        {
                                                                           name: "alfresco/renderers/PublishAction",
                                                                           config: {
                                                                              iconClass: "delete-16",
                                                                              publishTopic: "ALF_CRUD_DELETE",
                                                                              publishPayloadType: "PROCESS",
                                                                              publishPayload: {
                                                                                 url: "api/solr/facet-config/{filterID}"
                                                                              },
                                                                              publishPayloadModifiers: ["processCurrentItemTokens"],
                                                                              renderFilter: [
                                                                                 {
                                                                                    property: "isDefault",
                                                                                    values: [false],
                                                                                    renderOnAbsentProperty: false
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
                     widthPx: "500"
                  },
                  {
                     name: "alfresco/layout/VerticalWidgets",
                     config: {
                        widgets: [
                           {
                              name: "alfresco/forms/CrudForm",
                              config: {
                                 createButtonLabel: "Save",
                                 createButtonPublishTopic: "ALF_CRUD_CREATE",
                                 createButtonPublishPayload: {
                                    url: "api/solr/facet-config"
                                 },
                                 createButtonPublishGlobal: true,
                                 updateButtonLabel: "Save",
                                 updateButtonPublishTopic: "ALF_CRUD_UPDATE",
                                 updateButtonPublishPayload: {
                                    url: "api/solr/facet-config"
                                 },
                                 updateButtonPublishGlobal: true,
                                 deleteButtonLabel: "Delete",
                                 deleteButtonPublishTopic: "ALF_CRUD_DELETE",
                                 deleteButtonPublishPayload: {
                                    url: "api/solr/facet-config"
                                 },
                                 deleteButtonPublishGlobal: true,
                                 showInfoTopics: ["ALF_CRUD_CREATE", "ALF_CRUD_UPDATE", "ALF_CRUD_DELETE"],
                                 showFormTopics: [],
                                 widgets: [
                                    {
                                       name: "alfresco/forms/controls/DojoValidationTextBox",
                                       config: {
                                          fieldId: "FILTER_ID",
                                          name: "filterID",
                                          value: "",
                                          label: "faceted-search-config.filterId.label",
                                          description: "faceted-search-config.filterId.description",
                                          visibilityConfig: {
                                             initialValue: true
                                          },
                                          requirementConfig: {
                                             initialValue: true
                                          },
                                          disablementConfig: {
                                             initialValue: false,
                                             rules: [
                                                {
                                                   targetId: "IS_DEFAULT",
                                                   is: [true]
                                                }
                                             ]
                                          }
                                       }
                                    },
                                    {
                                       name: "alfresco/forms/controls/DojoCheckBox",
                                       config: {
                                          fieldId: "IS_ENABLED",
                                          name: "isEnabled",
                                          value: "true",
                                          label: "faceted-search-config.isEnabled.label",
                                          description: "faceted-search-config.isEnabled.description"
                                       }
                                    },
                                    {
                                       name: "alfresco/forms/controls/DojoCheckBox",
                                       config: {
                                          fieldId: "IS_DEFAULT",
                                          name: "isDefault",
                                          value: "false",
                                          label: "faceted-search-config.isDefault.label",
                                          description: "faceted-search-config.isDefault.description",
                                          postWhenHiddenOrDisabled: false,
                                          disablementConfig: {
                                             initialValue: true
                                          }
                                       }
                                    },
                                    {
                                       name: "alfresco/forms/controls/DojoSelect",
                                       config: {
                                          fieldId: "FACET_QNAME",
                                          name: "facetQName",
                                          value: "",
                                          label: "faceted-search-config.facetQName.label",
                                          description: "faceted-search-config.facetQName.description",
                                          requirementConfig: {
                                             initialValue: true
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
                                                },
                                                {
                                                   label: "Created",
                                                   value: "{http://www.alfresco.org/model/content/1.0}created"
                                                },
                                                {
                                                   label: "Modified",
                                                   value: "{http://www.alfresco.org/model/content/1.0}modified"
                                                },
                                                {
                                                   label: "Size",
                                                   value: "{http://www.alfresco.org/model/content/1.0}content.size"
                                                }
                                             ]
                                          }
                                       }
                                    },
                                    {
                                       name: "alfresco/forms/controls/DojoSelect",
                                       config: {
                                          fieldId: "DISPLAY_CONTROL",
                                          name: "displayControl",
                                          value: "alfresco/search/FacetFilters",
                                          label: "faceted-search-config.displayControl.label",
                                          description: "faceted-search-config.displayControl.description",
                                          optionsConfig: {
                                             fixed: [
                                                {
                                                   label: "faceted-search-config.displayControl.basic.label",
                                                   value: "alfresco/search/FacetFilters"
                                                }
                                             ]
                                          }
                                       }
                                    },
                                    {
                                       name: "alfresco/forms/controls/DojoValidationTextBox",
                                       config: {
                                          fieldId: "DISPLAY_NAME",
                                          name: "displayName",
                                          value: "",
                                          label: "faceted-search-config.displayName.label",
                                          placeHolder: "faceted-search-config.displayName.placeHolder",
                                          description: "faceted-search-config.displayName.description",
                                          visibilityConfig: {
                                             initialValue: true
                                          },
                                          requirementConfig: {
                                             initialValue: true
                                          }
                                       }
                                    },
                                    {
                                       name: "alfresco/forms/controls/DojoSelect",
                                       config: {
                                          fieldId: "SORTBY",
                                          name: "sortBy",
                                          value: "ALPHABETICALLY",
                                          label: "faceted-search-config.sortBy.label",
                                          description: "faceted-search-config.sortBy.description",
                                          optionsConfig: {
                                             fixed: [
                                                {
                                                   label: "faceted-search-config.sortBy.AtoZ.label",
                                                   value: "ALPHABETICALLY"
                                                },
                                                {
                                                   label: "faceted-search-config.sortBy.highToLow.label",
                                                   value: "ASCENDING"
                                                },
                                                {
                                                   label: "faceted-search-config.sortBy.lowToHigh.label",
                                                   value: "DESCENDING"
                                                }
                                             ]
                                          }
                                       }
                                    },
                                    {
                                       name: "alfresco/forms/controls/NumberSpinner",
                                       config: {
                                          fieldId: "MAXFILTERS",
                                          name: "maxFilters",
                                          value: "10",
                                          label: "faceted-search-config.maxFilters.label",
                                          description: "faceted-search-config.maxFilters.description",
                                          min: 1,
                                          max: 20,
                                          validationConfig: {
                                             regex: "^[0-9]+$"
                                          }
                                       }
                                    },
                                    {
                                       name: "alfresco/forms/controls/NumberSpinner",
                                       config: {
                                          fieldId: "MIN_FILTER_VALUE_LENGTH",
                                          name: "minFilterValueLength",
                                          value: "10",
                                          label: "faceted-search-config.minFilterValueLength.label",
                                          description: "faceted-search-config.minFilterValueLength.description",
                                          min: 1,
                                          max: 20,
                                          validationConfig: {
                                             regex: "^[0-9]+$"
                                          }
                                       }
                                    },
                                    {
                                       name: "alfresco/forms/controls/NumberSpinner",
                                       config: {
                                          fieldId: "HIT_THRESHOLD",
                                          name: "hitThreshold",
                                          value: "1",
                                          label: "faceted-search-config.hitThreshold.label",
                                          description: "faceted-search-config.hitThreshold.description",
                                          min: 1,
                                          max: 20,
                                          validationConfig: {
                                             regex: "^[0-9]+$"
                                          }
                                       }
                                    },
                                    {
                                       name: "alfresco/forms/controls/DojoSelect",
                                       config: {
                                          fieldId: "SCOPE",
                                          name: "scope",
                                          value: "",
                                          label: "faceted-search-config.scope.label",
                                          description: "faceted-search-config.scope.description",
                                          optionsConfig: {
                                             fixed: [
                                                {
                                                   label: "faceted-search-config.scope.none.label",
                                                   value: "ALL"
                                                },
                                                {
                                                   label: "faceted-search-config.scope.site.label",
                                                   value: "SCOPED_SITES"
                                                }
                                             ]
                                          }
                                       }
                                    },
                                    {
                                       name: "alfresco/forms/controls/MultipleEntryFormControl",
                                       config: {
                                          fieldId: "SCOPED_SITES",
                                          name: "scopedSites",
                                          value: "",
                                          label: "faceted-search-config.scopedSites.label",
                                          description: "faceted-search-config.scopedSites.description",
                                          useSimpleValues: true,
                                          widgets: [
                                             {
                                                name: "alfresco/forms/controls/DojoSelect",
                                                config: {
                                                   fieldId: "SCOPED_SITES_SITE",
                                                   name: "value",
                                                   value: "",
                                                   label: "faceted-search-config.scopedSites.site.label",
                                                   description: "faceted-search-config.scopedSites.site.description",
                                                   optionsConfig: {
                                                      pubSub: {
                                                         publishTopic: "ALF_GET_FORM_CONTROL_OPTIONS",
                                                         publishPayload: {
                                                            url: url.context + "/proxy/alfresco/api/sites",
                                                            itemsAttribute: "",
                                                            labelAttribute: "title",
                                                            valueAttribute: "shortName"
                                                         }
                                                      }
                                                   }
                                                }
                                             }
                                          ],
                                          visibilityConfig: {
                                             initialValue: false,
                                             rules: [
                                                {
                                                   targetId: "SCOPE",
                                                   is: ["SCOPED_SITES"]
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
         },
      ]
   }
};

widgets.push(main);

// Push services and widgets into the getFooterModel to return with a sticky footer wrapper
model.jsonModel = getFooterModel(services, widgets);