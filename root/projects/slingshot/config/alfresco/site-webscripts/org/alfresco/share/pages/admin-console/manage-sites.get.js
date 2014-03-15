// Ideally we would want the SiteService to subscribe to global topics, but because it is used within
// the AdminConsole we need to render the page as a "hybrid" of multiple Components. The header component
// has it's own SiteService so we need to scope this one in order to prevent duplicate HTTP requests from
// occurring. It is not possible to simply omit this SiteService and rely on the one provided by the
// share-header.get WebScript as race conditions come into play...
var siteServiceScope = "MANAGE_SITES_SITE_SERVICE_";

model.jsonModel = {
   services: [{
      name: "alfresco/services/SiteService",
      config: {
         pubSubScope: siteServiceScope
      }
   }],
   widgets: [
      {
         id: "SET_PAGE_TITLE",
         name: "alfresco/header/SetTitle",
         config: {
            title: msg.get("manage-sites.page.title")
         }
      },
      {
         id: "SHARE_VERTICAL_LAYOUT",
         name: "alfresco/layout/VerticalWidgets",
         config: 
         {
            pubSubScope: siteServiceScope,
            widgets: [
               {
                  id: "DOCLIB_DOCUMENT_LIST",
                  name: "alfresco/documentlibrary/AlfSitesList",
                  config: {
                     useHash: false,
                     sortAscending: true,
                     sortField: "title",
                     usePagination: true,
                     dataRequestTopic: "ALF_GET_SITES_ADMIN",
                     renderFilter: [
                        {
                           property: "groups.GROUP_SITE_ADMINISTRATORS",
                           values: [true]
                        }
                     ],
                     widgets: [
                        {
                           name: "alfresco/documentlibrary/views/AlfDocumentListWithHeaderView",
                           config: {
                              itemKey: "shortName",
                              widgetsForHeader: [
                                 {
                                    name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                    config: {
                                       id: "titleTableHeader",
                                       label: msg.get("message.site-name-header-label"),
                                       sortable: false
                                    }
                                 },
                                 {
                                    name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                    config: {
                                       id: "descriptionTableHeader",
                                       label: msg.get("message.site-description-header-label"),
                                       sortable: false
                                    }
                                 },
                                 {
                                    name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                    config: {
                                       label: msg.get("message.visibility-header-label")
                                    }
                                 },
//                                 {
//                                    name: "alfresco/documentlibrary/views/layouts/HeaderCell",
//                                    config: {
//                                       label: msg.get("message.created-header-label")
//                                    }
//                                 },
//                                 {
//                                    name: "alfresco/documentlibrary/views/layouts/HeaderCell",
//                                    config: {
//                                       label: msg.get("message.last-modified-header-label")
//                                    }
//                                 },
                                 {
                                    name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                    config: {
                                       label: msg.get("message.actions-header-label"),
                                       class: "last"
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
                                                class: "siteName",
                                                widgets: [
                                                   {
                                                      name: "alfresco/renderers/Property",
                                                      config: {
                                                         propertyToRender: "title",
                                                         renderAsLink: false
                                                      }
                                                   }
                                                ]
                                             }
                                          },
                                          {
                                             name: "alfresco/documentlibrary/views/layouts/Cell",
                                             config: {
                                                class: "siteDescription",
                                                widgets: [
                                                   {
                                                      name: "alfresco/renderers/Property",
                                                      config: {
                                                         propertyToRender: "description",
                                                         renderAsLink: false
                                                      }
                                                   }
                                                ]
                                             }
                                          },
                                          {
                                             name: "alfresco/documentlibrary/views/layouts/Cell",
                                             config: {
                                                class: "visibility smallpad",
                                                widgets: [
                                                   {
                                                      name: "alfresco/renderers/PublishingDropDownMenu",
                                                      config: {
                                                         class: "unmargined",
                                                         publishTopic: "ALF_UPDATE_SITE_DETAILS",
                                                         publishPayload: {
                                                            shortName: {
                                                               alfType: "item",
                                                               alfProperty: "shortName"
                                                            },
                                                            visibility: {
                                                               alfType: "payload",
                                                               alfProperty: "value"
                                                            }
                                                         },
                                                         propertyToRender: "visibility",
                                                         optionsConfig: {
                                                            fixed: [
                                                               {label: msg.get("message.site-visibility-dropdown-public-label"), value: "PUBLIC"},
                                                               {label: msg.get("message.site-visibility-dropdown-moderated-label"), value: "MODERATED"},
                                                               {label: msg.get("message.site-visibility-dropdown-private-label"), value: "PRIVATE"}
                                                            ]
                                                         }
                                                      }
                                                   }
                                                ]
                                             }
                                          },
//                                          {
//                                             name: "alfresco/documentlibrary/views/layouts/Cell",
//                                             config: {
//                                                widgets: [
//                                                   {
//                                                      name: "alfresco/renderers/Property",
//                                                      config: {
//                                                         propertyToRender: "createdDate",
//                                                         renderAsLink: false
//                                                      }
//                                                   }
//                                                ]
//                                             }
//                                          },
//                                          {
//                                             name: "alfresco/documentlibrary/views/layouts/Cell",
//                                             config: {
//                                                widgets: [
//                                                   {
//                                                      name: "alfresco/renderers/Property",
//                                                      config: {
//                                                         propertyToRender: "lastModifiedDate",
//                                                         renderAsLink: false
//                                                      }
//                                                   }
//                                                ]
//                                             }
//                                          },
                                          {
                                             name: "alfresco/documentlibrary/views/layouts/Cell",
                                             config: {
                                                class: "actions smallpad last",
                                                widgets: [
                                                   {
                                                      name: "alfresco/menus/AlfMenuBar",
                                                      align: "left",
                                                      config: {
                                                         widgets: [
                                                            {
                                                               name: "alfresco/menus/AlfMenuBarPopup",
                                                               config: {
                                                                  label: "Actions",
                                                                  widgets: [
                                                                     {
                                                                        name: "alfresco/menus/AlfMenuGroup",
                                                                        config: {
                                                                           class: "unmargined",
                                                                           widgets: [
                                                                              {
                                                                                 name: "alfresco/menus/AlfMenuItem",
                                                                                 config: {
                                                                                    label: msg.get("button.site-delete.label"),
                                                                                    iconClass: "document-delete",
                                                                                    publishTopic: "ALF_DELETE_SITE"
                                                                                 }
                                                                              },
                                                                              {
                                                                                 name: "alfresco/menus/AlfMenuItem",
                                                                                 config: {
                                                                                    label: msg.get("button.site-manage.label"),
                                                                                    iconClass: "folder-manage-permissions",
                                                                                    publishTopic: "ALF_BECOME_SITE_MANAGER",
                                                                                    publishPayload: {
                                                                                       site: {
                                                                                          alfType: "item",
                                                                                          alfProperty: "shortName"
                                                                                       }
                                                                                    }
//                                                                              ,
//                                                                                    renderFilter: [
//                                                                                       {
//                                                                                          currentContext: true,
//                                                                                          property: "userIsSiteManager",
//                                                                                          values: ["false"]
//                                                                                       }
//                                                                                    ]
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
                                 }
                              ]
                           }
                        }
                     ]
                  }
               },
               {
                  name: "alfresco/layout/CenteredWidgets",
                  config: {
                     pubSubScope: siteServiceScope,
                     widgets: [
                        {
                           id: "DOCLIB_PAGINATION_MENU",
                           name: "alfresco/documentlibrary/AlfDocumentListPaginator",
                           widthCalc: 430
                        }
                     ]
                  }
               }
            ]
         }
      }
   ]
};