model.jsonModel = {
   services: [
      "alfresco/tests/doclib-widgets/DocLibWidgetsTestService"
   ],
   widgets: [
      {
         name: "alfresco/layout/VerticalWidgets",
         config: {
            widgets: [
               {
                  name: "alfresco/menus/AlfMenuBar",
                  config: {
                     widgets: [
                        {
                           name: "alfresco/documentlibrary/AlfSelectedItemsMenuBarPopup",
                           config: {
                              id: "MENU1",
                              label: "menu1.label",
                              widgets: [
                                 {
                                    name: "alfresco/menus/AlfMenuGroup",
                                    config: {
                                       label: "menu1.group1.label",
                                       widgets: [
                                          {
                                             // This item should be hidden post filter because the CancelCheckOut permission won't be true
                                             name: "alfresco/documentlibrary/AlfDocumentActionMenuItem",
                                             config: {
                                                id: "MENU1_ITEM1",
                                                label: "menu1.group1.item1.label",
                                                permission: "CancelCheckOut,ChangePermissions,CreateChildren",
                                                hasAspect: "cm:titled",
                                                notAspect: "",
                                                publishTopic: "KEYBOARD_CLICK",
                                                publishPayload: {
                                                   item: "ACTION_1"
                                                }
                                             }
                                          },
                                          {
                                             // This item should be hidden post filter because the "cm:versionable" aspect won't available
                                             name: "alfresco/documentlibrary/AlfDocumentActionMenuItem",
                                             config: {
                                                id: "MENU1_ITEM2",
                                                label: "menu1.group1.item2.label",
                                                permission: "ChangePermissions",
                                                hasAspect: "cm:versionable,cm:titled",
                                                notAspect: "",
                                                publishTopic: "KEYBOARD_CLICK",
                                                publishPayload: {
                                                   item: "ACTION_2"
                                                }
                                             }
                                          },
                                          {
                                             // This item should be hidden post filter because the "cm:titled" aspect will be available
                                             name: "alfresco/documentlibrary/AlfDocumentActionMenuItem",
                                             config: {
                                                id: "MENU1_ITEM3",
                                                label: "menu1.group1.item3.label",
                                                permission: "ChangePermissions,CreateChildren",
                                                hasAspect: "cm:author",
                                                notAspect: "cm:titled",
                                                publishTopic: "KEYBOARD_CLICK",
                                                publishPayload: {
                                                   item: "ACTION_3"
                                                }
                                             }
                                          },
                                          {
                                             // This item should be shown post filter because it meets all the rules
                                             name: "alfresco/documentlibrary/AlfDocumentActionMenuItem",
                                             config: {
                                                id: "MENU1_ITEM4",
                                                label: "menu1.group1.item4.label",
                                                permission: "ChangePermissions,CreateChildren",
                                                hasAspect: "cm:titled,cm:author",
                                                notAspect: "cm:versionable",
                                                publishTopic: "KEYBOARD_CLICK",
                                                publishPayload: {
                                                   item: "ACTION_4"
                                                }
                                             }
                                          }
                                       ]
                                    }
                                 }
                              ]
                           }
                        },
                        {
                           name: "alfresco/menus/AlfMenuBarPopup",
                           config: {
                              id: "MENU2",
                              label: "menu2.label",
                              widgets: [
                                 {
                                    name: "alfresco/menus/AlfMenuGroup",
                                    config: {
                                       label: "menu2.group1.label",
                                       widgets: [
                                          {
                                             name: "alfresco/documentlibrary/AlfCreateContentMenuItem",
                                             config: {
                                                id: "MENU2_ITEM1",
                                                label: "menu2.group1.item1.label",
                                                permission: "CancelCheckout,CreateChildren",
                                                publishTopic: "KEYBOARD_CLICK",
                                                publishPayload: {
                                                   item: "ACTION_5"
                                                }
                                             }
                                          },
                                          {
                                             name: "alfresco/documentlibrary/AlfCreateContentMenuItem",
                                             config: {
                                                id: "MENU2_ITEM2",
                                                label: "menu2.group1.item2.label",
                                                permission: "CreateChildren",
                                                publishTopic: "KEYBOARD_CLICK",
                                                publishPayload: {
                                                   item: "ACTION_6"
                                                }
                                             }
                                          },
                                          {
                                             name: "alfresco/documentlibrary/AlfCreateTemplateContentMenu",
                                             config: {
                                                id: "MENU2_ITEM3",
                                                label: "menu2.group1.item3.label",
                                                _templatesUrl: "/fail"
                                             }
                                          },
                                          {
                                             name: "alfresco/documentlibrary/AlfCreateTemplateContentMenu",
                                             config: {
                                                id: "MENU2_ITEM4",
                                                label: "menu2.group1.item4.label",
                                                _templatesUrl: "/share/service/template-content-no-data"
                                             }
                                          },
                                          {
                                             name: "alfresco/documentlibrary/AlfCreateTemplateContentMenu",
                                             config: {
                                                id: "MENU2_ITEM5",
                                                label: "menu2.group1.item5.label",
                                                _templatesUrl: "/share/service/template-content-data",
                                                templatePublishTopic: "CREATE_TEMPLATE_CONTENT"
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
                  name: "alfresco/documentlibrary/AlfBreadcrumbTrail",
                  config: {
                     id: "BREADCRUMB_TRAIL_1",
                     showRootLabel: false
                  }
               },
               {
                  name: "alfresco/documentlibrary/AlfBreadcrumbTrail",
                  config: {
                     id: "BREADCRUMB_TRAIL_2",
                     rootLabel: "breadcrumbtrail2.root.label"
                  }
               }
            ]
         }
      }
   ]
}