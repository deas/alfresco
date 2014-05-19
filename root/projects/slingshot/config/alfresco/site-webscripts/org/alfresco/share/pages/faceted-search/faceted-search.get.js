<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">

// Get the initial header services and widgets...
var services = getHeaderServices();
var widgets = getHeaderModel("Search"); // Needs translating!

// var searchForm = {
//    name: "alfresco/forms/Form",
//    config: {
//       okButtonLabel: "Search...",
//       okButtonPublishTopic : "ALF_SET_SEARCH_TERM",
//       okButtonPublishGlobal: true,
//       showCancelButton: false,
//       widgets: [
//          {
//             name: "alfresco/forms/controls/DojoValidationTextBox",
//             config: {
//                label: "Search",
//                name: "term",
//                value: "",
//                visibilityConfig: {
//                   initialValue: true
//                }
//             }
//          }
//       ]
//    }
// };

var searchForm = {
   name: "alfresco/forms/SingleEntryForm",
   config: {
      okButtonLabel: "Search",
      okButtonPublishTopic : "ALF_SET_SEARCH_TERM",
      okButtonPublishGlobal: true,
      okButtonIconClass: "alf-white-search-icon",
      okButtonClass: "call-to-action",
      entryFieldName: "term"
   }
};

var searchDocLib = {
   name: "alfresco/documentlibrary/AlfSearchList",
   config: {
      useHash: true,
      widgets: [
         {
            name: "alfresco/documentlibrary/views/AlfDocumentListView",
            config: {
               widgets: [
                  {
                     name:  "alfresco/documentlibrary/views/layouts/Row",
                     config: {
                        widgets: [
                           {
                              name: "alfresco/documentlibrary/views/layouts/Cell",
                              config: {
                                 width: "100px",
                                 widgets: [
                                    {
                                       name: "alfresco/renderers/Thumbnail",
                                       linkClickTopic: "ALF_NO_OP"
                                    }
                                 ]
                              }
                           },
                           {
                              name: "alfresco/documentlibrary/views/layouts/Column",
                              config: {
                                 widgets: [
                                    {
                                       name: "alfresco/documentlibrary/views/layouts/Cell",
                                       config: {
                                          widgets: [
                                             {
                                                name: "alfresco/renderers/PropertyLink",
                                                config: {
                                                   propertyToRender: "displayName",
                                                   renderSize: "large",
                                                   publishTopic: "ALF_SEARCH_RESULT_LINK"
                                                }
                                             },
                                             {
                                                name: "alfresco/renderers/Property",
                                                config: {
                                                   propertyToRender: "title",
                                                   renderSize: "small",
                                                   renderedValuePrefix: "(",
                                                   renderedValueSuffix: ")",
                                                   renderFilter: [
                                                      {
                                                         property: "title",
                                                         values: [""],
                                                         negate: true
                                                      }
                                                   ]
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
                                                name: "alfresco/renderers/Date",
                                                config: {
                                                   modifiedDateProperty: "modifiedOn",
                                                   modifiedByProperty: "modifiedBy"
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
                                                name: "alfresco/renderers/Property",
                                                config: {
                                                   propertyToRender: "description",
                                                   renderedValuePrefix: "Description: ",
                                                   warnIfNotAvailable: true,
                                                   warnIfNoteAvailableMessage: "No Description"
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
                                                name: "alfresco/renderers/Property",
                                                config: {
                                                   propertyToRender: "site.title",
                                                   renderedValuePrefix: "Site: "
                                                }
                                             }
                                          ]
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
                                       name: "alfresco/renderers/XhrActions"
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
};

var sideBarMenu = {
   name: "alfresco/layout/LeftAndRight",
   config: {
      widgets: [
         {
            name: "alfresco/html/Label",
            align: "left",
            config: {
               label: "Filter by:"
            }
         }
         // ,
         // {
         //    name: "alfresco/menus/AlfMenuBar",
         //    align: "right",
         //    config: {
         //       widgets: [
         //          {
         //             name: "alfresco/menus/AlfMenuBarToggle",
         //             config: {
         //                checked: true,
         //                onConfig: {
         //                   label: "Collapse All",
         //                   publishTopic: "",
         //                   publishPayload: {
         //                      direction: ""
         //                   }
         //                },
         //                offConfig: {
         //                   label: "Expand All",
         //                   publishTopic: "",
         //                   publishPayload: {
         //                      direction: ""
         //                   }
         //                }
         //             }
         //          }
         //       ]
         //    }
         // }
      ]
   }
};

var sortMenu = {
   name: "alfresco/menus/AlfMenuBarSelect",
   config: {
      selectionTopic: "ALF_DOCLIST_SORT_FIELD_SELECTION",
      widgets: [
         {
            id: "DOCLIB_SORT_FIELD_SELECT_GROUP",
            name: "alfresco/menus/AlfMenuGroup",
            config: {
               // TODO: Add the remaining sort fields
               widgets: [
                  {
                     name: "alfresco/menus/AlfCheckableMenuItem",
                     config: {
                        label: "Relevance",
                        value: "",
                        group: "DOCUMENT_LIBRARY_SORT_FIELD",
                        publishTopic: "ALF_DOCLIST_SORT_FIELD_SELECTION",
                        checked: true,
                        publishPayload: {
                           label: "Relevance",
                           direction: "descending"
                        }
                     }
                  },
                  {
                     name: "alfresco/menus/AlfCheckableMenuItem",
                     config: {
                        label: "Name",
                        value: "cm:name",
                        group: "DOCUMENT_LIBRARY_SORT_FIELD",
                        publishTopic: "ALF_DOCLIST_SORT_FIELD_SELECTION",
                        checked: false,
                        publishPayload: {
                           label: "Name",
                           direction: "descending"
                        }
                     }
                  },
                  {
                     name: "alfresco/menus/AlfCheckableMenuItem",
                     config: {
                        label: "Size",
                        value: ".size",
                        group: "DOCUMENT_LIBRARY_SORT_FIELD",
                        publishTopic: "ALF_DOCLIST_SORT_FIELD_SELECTION",
                        checked: false,
                        publishPayload: {
                           label: "Size",
                           direction: "descending"
                        }
                     }
                  }
               ]
            }
         }
      ]
   }
};

var searchResultsMenuBar = {
   name: "alfresco/layout/LeftAndRight",
   config: {
      widgets: [
         {
            name: "alfresco/html/Label",
            align: "left",
            config: {
               label: "No results",
               subscriptionTopic: "ALF_SEARCH_RESULTS_COUNT"
            }
         },
         {
            name: "alfresco/menus/AlfMenuBar",
            align: "right",
            config: {
               widgets: [
                  {
                     name: "alfresco/menus/AlfMenuBarToggle",
                     config: {
                        checked: true,
                        onConfig: {
                           iconClass: "alf-sort-ascending-icon",
                           publishTopic: "ALF_DOCLIST_SORT",
                           publishPayload: {
                              direction: "ascending"
                           }
                        },
                        offConfig: {
                           iconClass: "alf-sort-descending-icon",
                           publishTopic: "ALF_DOCLIST_SORT",
                           publishPayload: {
                              direction: "descending"
                           }
                        }
                     }
                  },
                  sortMenu
               ]
            }
         }
      ]
   }
};

var facets = {
   name: "alfresco/layout/VerticalWidgets",
   config: {
      widgets: [
         {
            name: "alfresco/search/FacetFilters",
            config: {
               label: "Formats",
               facetQName: "{http://www.alfresco.org/model/content/1.0}content.mimetype",
               sortBy: "DESCENDING",
               maxFilters: 6
            }
         },
         {
            name: "alfresco/search/FacetFilters",
            config: {
               label: "Description",
               facetQName: "{http://www.alfresco.org/model/content/1.0}description.__",
               sortBy: "DESCENDING",
               hitThreshold: 1,
               minFilterValueLength: 5,
               maxFilters: 6
            }
         },
         {
            name: "alfresco/search/FacetFilters",
            config: {
               label: "Creator",
               facetQName: "{http://www.alfresco.org/model/content/1.0}creator.__",
               sortBy: "ALPHABETICALLY",
               maxFilters: 3
            }
         },
         {
            name: "alfresco/search/FacetFilters",
            config: {
               label: "Modifier",
               facetQName: "{http://www.alfresco.org/model/content/1.0}modifier.__",
               sortBy: "ALPHABETICALLY",
               maxFilters: 3
            }
         }
      ]
   }
};



var main = {
   name: "alfresco/layout/VerticalWidgets",
   config: {
      baseClass: "side-margins",
      widgets: [
         searchForm,
         {
            name: "alfresco/layout/HorizontalWidgets",
            config: {
               widgets: [
                  {
                     name: "alfresco/layout/VerticalWidgets",
                     align: "sidebar",
                     widthPx: 350,
                     config: {
                        widgets: [
                           sideBarMenu
                        ]
                     }
                  },
                  {
                     name: "alfresco/layout/VerticalWidgets",
                     config: {
                        widgets: [
                           searchResultsMenuBar
                        ]
                     }
                  }
               ]
            }
         },
         {
            name: "alfresco/layout/HorizontalWidgets",
            config: {
               widgets: [
                  {
                     name: "alfresco/layout/VerticalWidgets",
                     align: "sidebar",
                     widthPx: 350,
                     config: {
                        widgets: [
                           facets
                        ]
                     }
                  },
                  {
                     name: "alfresco/layout/VerticalWidgets",
                     config: {
                        widgets: [
                           searchDocLib
                        ]
                     }
                  }
               ]
            }
         }
      ]
   }
};

// Add in the services needed for search...
services.push("alfresco/services/ContentService",
              "alfresco/services/DocumentService",
              "alfresco/dialogs/AlfDialogService",
              "alfresco/services/ActionService",
              "alfresco/services/SearchService",
              "alfresco/services/QuaddsService");

// Add in the search form and search doc lib...
widgets.push(main);


var licenseHolder = context.properties["editionInfo"].holder;
var footerConfig = config.scoped["Edition"]["footer"];
var footerCopyRight = footerConfig.getChildValue("label");
var footerCssClass = footerConfig.getChildValue("css-class");
var footerLogo = footerConfig.getChildValue("logo");
var footerLogoAltText = footerConfig.getChildValue("alt-text");

model.jsonModel = {
   services: services,
   widgets: [
      {
         name: "alfresco/footer/AlfStickyFooter",
         config: {
            widgets: [
               {
                  id: "SHARE_VERTICAL_LAYOUT",
                  name: "alfresco/layout/VerticalWidgets",
                  config: 
                  {
                     widgets: widgets
                  }
               }
            ],
            widgetsForFooter: [
               {
                  name: "alfresco/footer/AlfShareFooter",
                  config: {
                     licenseLabel: licenseHolder,
                     copyrightLabel: footerCopyRight,
                     altText: footerLogoAltText,
                     logoImageSrc: footerLogo
                  }
               }
            ]
         }
      }
   ]
};