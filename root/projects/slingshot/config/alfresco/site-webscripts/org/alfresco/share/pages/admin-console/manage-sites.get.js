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
                  id: "DOCLIB_DOCUMENT_LIST",
                  name: "alfresco/documentlibrary/AlfSitesList",
                  config: {
                     useHash: false,
                     sortAscending: true,
                     sortField: "title",
                     usePagination: true,
                     dataRequestTopic: "ALF_GET_SITES",
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
                                       label: "Site Name",
                                       sortable: true,
                                       sortValue: "title",
                                       toolTipMsg: "Sort by site name"
                                    }
                                 },
                                 {
                                    name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                    config: {
                                       id: "descriptionTableHeader",
                                       label: "Site Description",
                                       sortable: true,
                                       sortValue: "description",
                                       toolTipMsg: "Sort by site description"
                                    }
                                 },
                                 {
                                     name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                     config: {
                                        label: "Visibility"
                                     }
                                  },
                                 {
                                     name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                     config: {
                                        label: "Actions",
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
                                                      name: "alfresco/renderers/PublishingDropDownMenu",
                                                      config: {
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
                                                               {label: "Public", value: "PUBLIC"},
                                                               {label: "Moderated", value: "MODERATED"},
                                                               {label: "Private", value: "PRIVATE"}
                                                            ]
                                                         }
                                                      }
                                                   }
                                                ]
                                             }
                                          },
                                          {
                                             name: "alfresco/documentlibrary/views/layouts/Cell",
                                             config: {
                                                class: "last",
                                                widgets: [
                                                   {
                                                      name: "alfresco/buttons/AlfButton",
                                                      config: {
                                                         id: "deleteSiteButton",
                                                         icon : "document-delete",
                                                         label: "Delete Site",
                                                         publishTopic: "DELETE_SITE"
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