model.jsonModel = {
   services: [
//      "alfresco/tests/doclib-widgets/DocLibWidgetsTestService"
   ],
   widgets: [
      {
         name: "alfresco/layout/VerticalWidgets",
         config: {
            widgets: [
               {
                  name: "alfresco/menus/AlfDynamicMenuBar",
                  config: {
                     updateTopic: "ALF_DOCLIST_PROVIDE_ADDITIONAL_VIEW_CONTROLS",
                     widgets: [
                        {
                           name: "alfresco/menus/AlfMenuBarPopup",
                           config: {
                              iconClass: "alf-configure-icon",
                              widgets: [
                                 {
                                    name: "alfresco/documentlibrary/AlfViewSelectionGroup"
                                 }
                              ]
                           }
                        }
                     ]
                  }
               },
               {
                  name: "alfresco/documentlibrary/AlfDocumentList",
                  config: {
                     _currentData: [],
                     showFolders: true,
                     usePagination: true,
                     sortAscending: true,
                     sortField: "cm:name",
                     rootNode: user.properties['userHome'],
                     widgets: [
                        {
                           name: "alfresco/documentlibrary/views/AlfSimpleView"
                        },
                        {
                           name: "alfresco/documentlibrary/views/AlfDetailedView"
                        },
                        {
                           name: "alfresco/documentlibrary/views/AlfGalleryView"
                        }
                     ]
                  }
               }
            ]
         }
      }
   ]
}