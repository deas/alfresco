model.jsonModel = {
   services: [
      "alfresco/tests/header/HeaderTestService"
   ],
   widgets: [
      {
         name: "alfresco/layout/VerticalWidgets",
         config: {
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
                                       id: "MENU_1",
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
                                                         closeOnClick: false,
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
                                                         closeOnClick: false,
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
               },
               {
                  name: "alfresco/header/LicenseWarning",
                  config: {
                     usage: {
                        lastUpdate: null,
                        users : null,
                        documents : null,
                        licenseMode : "UNKNOWN",
                        readOnly : true,
                        updated : false,
                        licenseValidUntil : null,
                        level : 0,
                        warnings: [],
                        errors: []
                     },
                     userIsAdmin: true
                  }
               },
               {
                  name: "alfresco/header/LicenseWarning",
                  config: {
                     usage: {
                        lastUpdate: null,
                        users : null,
                        documents : null,
                        licenseMode : "UNKNOWN",
                        readOnly : false,
                        updated : false,
                        licenseValidUntil : null,
                        level : 3,
                        warnings: ["Test warning for admin"],
                        errors: ["Test error for admin"]
                     },
                     userIsAdmin: true
                  }
               },
               {
                  name: "alfresco/header/LicenseWarning",
                  config: {
                     usage: {
                        lastUpdate: null,
                        users : null,
                        documents : null,
                        licenseMode : "UNKNOWN",
                        readOnly : false,
                        updated : false,
                        licenseValidUntil : null,
                        level : 1,
                        warnings: ["Should be hidden"],
                        errors: ["Should be hidden"]
                     },
                     userIsAdmin: false
                  }
               },
               {
                  name: "alfresco/header/LicenseWarning",
                  config: {
                     usage: {
                        lastUpdate: null,
                        users : null,
                        documents : null,
                        licenseMode : "UNKNOWN",
                        readOnly : false,
                        updated : false,
                        licenseValidUntil : null,
                        level : 2,
                        warnings: ["Test warning for users"],
                        errors: ["Test error for users"]
                     },
                     userIsAdmin: false
                  }
               }
            ]
         }
      }
   ]
}