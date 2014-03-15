// TODO: This currently only contains the model for the main content (i.e. it will work with the hybrid-template)
//       but ideally we should sort out all the lib files so that it runs in the full template...
//       /share/page/dp/ws/manage-sites as opposed to /share/page/hdp/ws/manage-sites


model.jsonModel = {
   services: [{
      name: "alfresco/services/LoggingService",
      config: {
         loggingPreferences: {
            enabled: true,
            all: true,
            warn: true,
            error: true
         }
      }
   },
   "alfresco/services/SiteService"],
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
            widgets: [
               {
                  name: "alfresco/menus/AlfMenuBar",
                  config: {
                     widgets: [
                        {
                           name: "alfresco/menus/AlfMenuBarItem",
                           config: {
                              label: "All"
                           }
                        },
                        {
                           name: "alfresco/menus/AlfMenuBarItem",
                           config: {
                              label: "Public"
                           }
                        },
                        {
                           name: "alfresco/menus/AlfMenuBarItem",
                           config: {
                              label: "Moderated"
                           }
                        },
                        {
                           name: "alfresco/menus/AlfMenuBarItem",
                           config: {
                              label: "Private"
                           }
                        }
                     ]
                  }
               },
               {
                  name: "alfresco/layout/LeftAndRight",
                  config: {
                     widgets: [
                        {
                           name: "alfresco/forms/controls/DojoValidationTextBox",
                           align: "left",
                           config: {
                              label: ""
                           }
                        },
                        {
                           name: "alfresco/buttons/AlfButton",
                           align: "left",
                           config: {
                              label: "Filter"
                           }
                        },
                        {
                           name: "alfresco/buttons/AlfButton",
                           align: "right",
                           config: {
                              label: "Export"
                           }
                        }
                     ]
                  }
               },
               {
                  id: "MANAGE_SITES_TOOLBAR",
                  name: "alfresco/documentlibrary/AlfToolbar",
                  config: {
                     id: "MANAGE_SITES_TOOLBAR",
                     widgets: [
                        {
                           id: "MANAGE_SITES_TOOLBAR_LEFT_MENU",
                           name: "alfresco/menus/AlfMenuBar",
                           align: "left",
                           config: {
                              widgets: [
                                 {
                                    id: "MANAGE_SITES_SELECT_ITEMS_MENU",
                                    name: "alfresco/documentlibrary/AlfSelectDocumentListItems"
                                 },
                                 {
                                    id: "MANAGE_SITES_SELECTED_ITEMS_MENU",
                                    name: "alfresco/documentlibrary/AlfSelectedItemsMenuBarPopup",
                                    config: {
                                       label: "Selected sites...",
                                       widgets: [
                                          {
                                             id: "MANAGE_SITES_SELECTED_ITEMS_MENU_GROUP1",
                                             name: "alfresco/menus/AlfMenuGroup",
                                             config: {
                                                widgets: []
                                             }
                                          }
                                       ]
                                    }
                                 }
                              ]
                           }
                        },
                        {
                           id: "MANAGE_SITES_PAGINATION_MENU",
                           name: "alfresco/documentlibrary/AlfDocumentListPaginator",
                           align: "left"
                        }
                     ]
                  }
               },
               {
                  id: "DOCLIB_DOCUMENT_LIST",
                  name: "alfresco/documentlibrary/AlfSitesList",
                  config: {
                     useHash: false,
                     sortAscending: true,
                     sortField: "title",
                     usePagination: true,
                     widgets: [
                        {
                           name: "alfresco/documentlibrary/views/AlfDocumentListWithHeaderView",
                           config: {
                              widgetsForHeader: [
                                 {
                                    name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                    config: {
                                       label: "",
                                       sortable: false,
                                       sortValue: ""
                                    }
                                 },
                                 {
                                    name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                    config: {
                                       label: "Name",
                                       sortable: true,
                                       sortValue: "title"
                                    }
                                 },
                                 {
                                    name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                    config: {
                                       label: "Description",
                                       sortable: true,
                                       sortValue: "description"
                                    }
                                 },
                                 {
                                    name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                    config: {
                                       label: "Actions",
                                       sortable: false,
                                       sortValue: ""
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
                                                widgets: [
                                                   {
                                                      name: "alfresco/renderers/Selector"
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
                                                widgets: [
                                                   {
                                                      name: "alfresco/renderers/Actions",
                                                      config: {
                                                         customActions: [
                                                            {
                                                               label: "Wiew Members",
                                                               icon : "document-view-content",
                                                               index: "10",
                                                               publishTopic : "",
                                                               type: "javascript"
                                                            },
                                                            {
                                                               label: "Members",
                                                               icon : "document-delete",
                                                               index: "20",
                                                               publishTopic : "",
                                                               type: "javascript"
                                                            },
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
};
