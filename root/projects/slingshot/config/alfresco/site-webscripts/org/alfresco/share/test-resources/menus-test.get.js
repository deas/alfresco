model.jsonModel = {
   services: [
      "alfresco/tests/menus/MenusTestService"
   ],
   widgets: [
      {
         name: "alfresco/menus/AlfMenuBar",
         align: "left",
         config: {
            id: "MENU_BAR",
            widgets: [
               {
                  name: "alfresco/menus/AlfMenuBarPopup",
                  config: {
                     id: "DROP_DOWN_MENU_1",
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
                                       id: "MENU_ITEM_1",
                                       label: "dd1.group1.mi1",
                                       closeOnClick: false,
                                       publishTopic: "KEYBOARD_CLICK",
                                       publishPayload: {
                                          item: "MENU_ITEM_1"
                                       }
                                    }
                                 },
                                 {
                                    name: "alfresco/menus/AlfMenuItem",
                                    config: {
                                       id: "MENU_ITEM_2",
                                       label: "dd1.group1.mi2",
                                       closeOnClick: false,
                                       publishTopic: "KEYBOARD_CLICK",
                                       publishPayload: {
                                          item: "MENU_ITEM_2"
                                       }
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
                                     id: "MENU_ITEM_3",
                                     label: "dd1.group1.mi3",
                                     closeOnClick: false,
                                     publishTopic: "KEYBOARD_CLICK",
                                     publishPayload: {
                                        item: "MENU_ITEM_3"
                                     }
                                  }
                               }
                            ]
                           }
                        },
                        {
                           name: "alfresco/menus/AlfMenuGroup",
                           config: {
                              id: "DROP_DOWN_1_GROUP_3",
                              widgets: [
                               {
                                  name: "alfresco/menus/AlfMenuItem",
                                  config: {
                                     id: "MENU_ITEM_4",
                                     label: "dd1.group1.mi4",
                                     closeOnClick: false,
                                     publishTopic: "KEYBOARD_CLICK",
                                     publishPayload: {
                                        item: "MENU_ITEM_4"
                                     }
                                  }
                               },
                               {
                                  name: "alfresco/menus/AlfMenuItem",
                                  config: {
                                     id: "MENU_ITEM_5",
                                     label: "dd1.group1.mi5",
                                     closeOnClick: false,
                                     publishTopic: "KEYBOARD_CLICK",
                                     publishPayload: {
                                        item: "MENU_ITEM_5"
                                     }
                                  }
                               },
                               {
                                  name: "alfresco/menus/AlfMenuItem",
                                  config: {
                                     id: "MENU_ITEM_6",
                                     label: "dd1.group1.mi6",
                                     closeOnClick: false,
                                     publishTopic: "KEYBOARD_CLICK",
                                     publishPayload: {
                                        item: "MENU_ITEM_6"
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
                  name: "alfresco/menus/AlfMenuBarItem",
                  config: {
                     id: "MENU_BAR_ITEM_1",
                     label: "mb.mi1",
                     targetUrl: "MENU_BAR_ITEM_1"
                  }
               },
               {
                  name: "alfresco/menus/AlfMenuBarPopup",
                  config: {
                     id: "DROP_DOWN_MENU_2",
                     label: "dd2.label",
                     widgets: [
                        {
                           name: "alfresco/menus/AlfMenuItem",
                           config: {
                              id: "MENU_ITEM_7",
                              closeOnClick: false,
                              label: "dd2.mi7",
                              targetUrl: "MENU_ITEM_7"
                           }
                        },
                        {
                           name: "alfresco/menus/AlfMenuItem",
                           config: {
                              id: "MENU_ITEM_8",
                              closeOnClick: false,
                              label: "dd2.mi8",
                              targetUrl: "MENU_ITEM_8"
                           }
                        }
                     ]
                  }
               },
               {
                  name: "alfresco/menus/AlfMenuBarPopup",
                  config: {
                     id: "DROP_DOWN_MENU_3",
                     label: "dd3.label",
                     widgets: [
                        {
                           name: "alfresco/logo/Logo"
                        }
                     ]
                  }
               },
               {
                  name: "alfresco/menus/AlfMenuBarPopup",
                  config: {
                     id: "DROP_DOWN_MENU_4",
                     label: "dd4.label",
                     iconClass: "alf-configure-icon",
                     widgets: [
                        {
                           name: "alfresco/menus/AlfMenuGroup",
                           config: {
                              id: "DROP_DOWN_4_GROUP_1",
                              label: "dd4.group1.label",
                              widgets: [
                                 {
                                    name: "alfresco/menus/AlfMenuItem",
                                    config: {
                                       id: "MENU_ITEM_9",
                                       label: "dd4.group1.mi1",
                                       iconClass: "alf-edit-icon",
                                       closeOnClick: false,
                                       publishTopic: "KEYBOARD_CLICK",
                                       publishPayload: {
                                          item: "MENU_ITEM_9"
                                       }
                                    }
                                 },
                                 {
                                    name: "alfresco/menus/AlfMenuItem",
                                    config: {
                                       id: "MENU_ITEM_10",
                                       label: "dd4.group1.mi2",
                                       closeOnClick: false,
                                       iconClass: "alf-cog-icon"
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
                     id: "DROP_DOWN_MENU_5",
                     label: "dd5.label",
                     widgets: [
                        {
                           name: "alfresco/menus/AlfMenuGroup",
                           config: {
                              id: "DROP_DOWN_5_GROUP_1",
                              widgets: [
                                 {
                                    name: "alfresco/menus/AlfCascadingMenu",
                                    config: {
                                       id: "CASCADING_MENU_1",
                                       label: "cascade1.label",
                                       widgets: [
                                          {
                                             name: "alfresco/menus/AlfMenuGroup",
                                             config: {
                                                id: "CASCADE_GROUP_1",
                                                label: "cascade1.group1.label",
                                                widgets: [
                                                   {
                                                      name: "alfresco/menus/AlfMenuItem",
                                                      config: {
                                                         id: "CASCADE_MENU_ITEM_1",
                                                         label: "cascade.mi1.label",
                                                         closeOnClick: false,
                                                         publishTopic: "KEYBOARD_CLICK",
                                                         publishPayload: {
                                                            item: "CASCADE_MENU_ITEM_1"
                                                         }
                                                      }
                                                   },
                                                   {
                                                      name: "alfresco/menus/AlfMenuItem",
                                                      config: {
                                                         id: "CASCADE_MENU_ITEM_2",
                                                         label: "cascade.mi2.label",
                                                         closeOnClick: false,
                                                         publishTopic: "KEYBOARD_CLICK",
                                                         publishPayload: {
                                                            item: "CASCADE_MENU_ITEM_2"
                                                         }
                                                      }
                                                   },
                                                   {
                                                      name: "alfresco/menus/AlfMenuItem",
                                                      config: {
                                                         id: "CASCADE_MENU_ITEM_3",
                                                         label: "cascade.mi3.label",
                                                         closeOnClick: false,
                                                         publishTopic: "KEYBOARD_CLICK",
                                                         publishPayload: {
                                                            item: "CASCADE_MENU_ITEM_3"
                                                         }
                                                      }
                                                   }
                                                ]
                                             }
                                          },
                                          {
                                             name: "alfresco/menus/AlfMenuGroup",
                                             config: {
                                                id: "CASCADE_GROUP_2",
                                                label: "cascade1.group2.label",
                                                widgets: [
                                                   {
                                                      name: "alfresco/menus/AlfCascadingMenu",
                                                      config: {
                                                         id: "CASCADE_SUB_MENU_1",
                                                         label: "cascade.submenu1.label",
                                                         widgets: [
                                                            {
                                                               name: "alfresco/menus/AlfMenuItem",
                                                               config: {
                                                                  id: "CASCADE_MENU_ITEM_4",
                                                                  label: "cascade.mi4.label",
                                                                  closeOnClick: false,
                                                                  publishTopic: "KEYBOARD_CLICK",
                                                                  publishPayload: {
                                                                     item: "CASCADE_MENU_ITEM_4"
                                                                  }
                                                               }
                                                            },
                                                            {
                                                               name: "alfresco/menus/AlfMenuItem",
                                                               config: {
                                                                  id: "CASCADE_MENU_ITEM_5",
                                                                  label: "cascade.mi5.label",
                                                                  closeOnClick: false,
                                                                  publishTopic: "KEYBOARD_CLICK",
                                                                  publishPayload: {
                                                                     item: "CASCADE_MENU_ITEM_5"
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
                  }
               }
            ]
         }
      }
   ]
}