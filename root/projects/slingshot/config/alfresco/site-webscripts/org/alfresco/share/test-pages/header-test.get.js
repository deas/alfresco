model.jsonModel = {
   services: [
      "alfresco/tests/header/HeaderTestService"
   ],
   widgets: [
      {
         name: "alfresco/header/Header",
         config: {
            id: "HEADER",
            className: "alf-header",
            widgets: [
               {
                  name: "alfresco/header/AlfMenuBar",
                  align: "left",
                  config: {
                     id: "LEFT_MENU",
                     widgets: [
                        {
                           name: "alfresco/header/AlfMenuBarPopup",
                           config: {
                              label: "dd1.label",
                              widgets: [
                                 {
                                    name: "alfresco/menus/AlfMenuGroup",
                                    config: {
                                       id: "DROP_DOWN_1_GROUP_1",
                                       label: "dd1.group1.label",
                                       widgets: [
                                          {
                                             name: "alfresco/menus/AlfMenuItem",
                                             config: {
                                                id: "BEFORE_USER_STATUS",
                                                label: "dd1.group1.mi1",
                                                publishTopic: "KEYBOARD_CLICK",
                                                publishPayload: {
                                                   item: "BEFORE_USER_STATUS"
                                                }
                                             }
                                          },
                                          {
                                             name: "alfresco/header/UserStatus",
                                             config: {
                                                id: "USER_STATUS"
                                             }
                                          }
                                       ]
                                    }
                                 },
                                 {
                                    name: "alfresco/menus/AlfMenuGroup",
                                    config: {
                                       id: "DROP_DOWN_1_GROUP_2",
                                       label: "dd1.group2.label",
                                       widgets: [
                                          {
                                             name: "alfresco/menus/AlfMenuItem",
                                             config: {
                                                id: "AFTER_USER_STATUS",
                                                label: "dd1.group2.mi1",
                                                publishTopic: "KEYBOARD_CLICK",
                                                publishPayload: {
                                                   item: "AFTER_USER_STATUS"
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
               {
                  name: "alfresco/header/AlfMenuBar",
                  align: "right",
                  config: {
                     id: "RIGHT_MENU",
                     widgets: [
                        {
                           name: "alfresco/header/SearchBox",
                           config: {
                              id: "SEARCH_BOX"
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