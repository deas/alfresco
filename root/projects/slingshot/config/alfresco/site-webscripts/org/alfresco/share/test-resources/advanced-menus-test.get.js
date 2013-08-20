model.jsonModel = {
   services: [
      "alfresco/tests/menus/AdvancedMenusTestService"
   ],
   widgets: [
      {
         name: "alfresco/header/Header",
         config: {
            id: "HEADER",
            widgets: [
               {
                  name: "alfresco/menus/AlfMenuBar",
                  align: "left",
                  config: {
                     widgets: [
                        {
                           name: "alfresco/menus/AlfMenuBarPopup",
                           config: {
                              id: "MENU_BAR_POPUP1",
                              label: "menu1.label",
                              widgets: [
                                 {
                                    name: "alfresco/menus/AlfMenuGroup",
                                    config: {
                                       id: "BASIC_CHECKABLE_ITEM_GROUP",
                                       label: "menu1.group1.label",
                                       widgets: [
                                          {
                                             name: "alfresco/menus/AlfCheckableMenuItem",
                                             config: {
                                                id: "SIMPLE_CHECKABLE_1",
                                                label: "menu1.group1.item1.label",
                                                value: "OP1",
                                                publishTopic: "SIMPLE_CHECKABLE1"
                                             }
                                          },
                                          {
                                             name: "alfresco/menus/AlfCheckableMenuItem",
                                             config: {
                                                id: "SIMPLE_CHECKABLE_2",
                                                label: "menu1.group1.item2.label",
                                                value: "OP2",
                                                publishTopic: "SIMPLE_CHECKABLE2"
                                             }
                                          }
                                       ]
                                    }
                                 },
                                 {
                                    name: "alfresco/menus/AlfMenuGroup",
                                    config : {
                                       id: "GROUPED_CHECKABLE_ITEMS",
                                       label: "menu1.group2.label",
                                       widgets: [
                                          {
                                             name: "alfresco/menus/AlfCheckableMenuItem",
                                             config: {
                                                id: "GROUPED_CHECKABLE_1",
                                                label: "menu1.group2.item1.label",
                                                group: "CHECKABLE_GROUP",
                                                value: "CHECKED_OP1",
                                                publishTopic: "GROUPED_CHECKABLE"
                                             }
                                          },
                                          {
                                             name: "alfresco/menus/AlfCheckableMenuItem",
                                             config: {
                                                id: "GROUPED_CHECKABLE_2",
                                                label: "menu1.group2.item2.label",
                                                group: "CHECKABLE_GROUP",
                                                value: "CHECKED_OP2",
                                                publishTopic: "GROUPED_CHECKABLE"
                                             }
                                          },
                                          {
                                             name: "alfresco/menus/AlfCheckableMenuItem",
                                             config: {
                                                id: "GROUPED_CHECKABLE_3",
                                                label: "menu1.group2.item3.label",
                                                group: "CHECKABLE_GROUP",
                                                value: "CHECKED_OP3",
                                                publishTopic: "GROUPED_CHECKABLE"
                                             }
                                          }
                                       ]
                                    }
                                 }
                              ]
                           }
                        },
                        {
                           // Basic select menu bar that just renders the labels...
                           name: "alfresco/menus/AlfMenuBarSelect",
                           config: {
                              id: "MENU_BAR_SELECT",
                              label: "menu2.label",
                              selectionTopic: "MENU_BAR_SELECT",
                              widgets: [
                                 {
                                    name: "alfresco/menus/AlfMenuItem",
                                    config: {
                                       id: "SELECT_MENU_ITEM_1",
                                       label: "menu2.item1.label",
                                       publishTopic: "MENU_BAR_SELECT",
                                       publishPayload: {
                                          label: "menu2.item1.label"
                                       }
                                    }
                                 },
                                 {
                                    name: "alfresco/menus/AlfMenuItem",
                                    config: {
                                       id: "SELECT_MENU_ITEM_2",
                                       label: "menu2.item2.label",
                                       publishTopic: "MENU_BAR_SELECT",
                                       publishPayload: {
                                          label: "menu2.item2.label"
                                       }
                                    }
                                 }
                              ]
                           }
                        },
                        {
                           // Basic select menu bar that just renders the value...
                           name: "alfresco/menus/AlfMenuBarSelect",
                           config: {
                              id: "MENU_BAR_SELECT_VALUE",
                              label: "menu3.label",
                              selectionTopic: "MENU_BAR_SELECT_VALUE",
                              widgets: [
                                 {
                                    name: "alfresco/menus/AlfMenuItem",
                                    config: {
                                       id: "SELECT_MENU_ITEM_3",
                                       label: "menu3.item1.label",
                                       publishTopic: "MENU_BAR_SELECT_VALUE",
                                       publishPayload: {
                                          value: "Alpha"
                                       }
                                    }
                                 },
                                 {
                                    name: "alfresco/menus/AlfMenuItem",
                                    config: {
                                       id: "SELECT_MENU_ITEM_4",
                                       label: "menu3.item2.label",
                                       publishTopic: "MENU_BAR_SELECT_VALUE",
                                       publishPayload: {
                                          value: "Beta"
                                       }
                                    }
                                 }
                              ]
                           }
                        },
                        {
                           // Menu bar select that shows the icon as well as the label...
                           name: "alfresco/menus/AlfMenuBarSelect",
                           config: {
                              id: "MENU_BAR_SELECT_WITH_ICON",
                              label: "menu4.label",
                              updateIconOnSelection: true,
                              selectionTopic: "MENU_BAR_SELECT_WITH_ICONS",
                              widgets: [
                                 {
                                    name: "alfresco/menus/AlfMenuItem",
                                    config: {
                                       id: "SELECT_MENU_ITEM_5",
                                       iconClass: "alf-textdoc-icon",
                                       publishTopic: "MENU_BAR_SELECT_WITH_ICONS",
                                       publishPayload: {
                                          iconClass: "alf-textdoc-icon"
                                       }
                                    }
                                 },
                                 {
                                    name: "alfresco/menus/AlfMenuItem",
                                    config: {
                                       id: "SELECT_MENU_ITEM_6",
                                       iconClass: "alf-htmldoc-icon",
                                       publishTopic: "MENU_BAR_SELECT_WITH_ICONS",
                                       publishPayload: {
                                          iconClass: "alf-textdoc-icon"
                                       }
                                    }
                                 }
                              ]
                           }
                        },
                        {
                           // Select Items widget...
                           name: "alfresco/menus/AlfMenuBarSelectItems",
                           config: {
                              id: "MENU_BAR_SELECT_ITEMS",
                              label: "menu5.label",
                              notificationTopic: "ITEMS_SELECTED",
                              selectionTopic: "MENU_BAR_SELECT_ITEMS",
                              widgets: [
                                 {
                                    name: "alfresco/menus/AlfMenuItem",
                                    config: {
                                       label: "menu5.item1.label",
                                       publishTopic: "MENU_BAR_SELECT_ITEMS",
                                       publishPayload: {
                                          label: "menu5.item1.label",
                                          value: "selectAll"
                                       }
                                    }
                                 },
                                 {
                                    name: "alfresco/menus/AlfMenuItem",
                                    config: {
                                       label: "menu5.item2.label",
                                       publishTopic: "MENU_BAR_SELECT_ITEMS",
                                       publishPayload: {
                                          label: "menu5.item2.label",
                                          value: "selectNone"
                                       }
                                    }
                                 }
                              ]
                           }
                        },
                        {
                           // Should get "On"/"Off" labels and initially be off
                           name: "alfresco/menus/AlfMenuBarToggle",
                           config: {
                              id: "BASIC_MENU_BAR_TOGGLE"
                           }
                        },
                        {
                           // Should be initially set to on ("High")...
                           name: "alfresco/menus/AlfMenuBarToggle",
                           config: {
                              id: "MENU_BAR_TOGGLE_CUSTOM_LABEL",
                              checked: true,
                              onConfig: {
                                 label: "toggle1.on.label",
                                 publishTopic: "CLICK",
                                 publishPayload: {
                                    clicked: "TOGGLE_WITH_LABEL",
                                    value: "ON"
                                 }
                              },
                              offConfig: {
                                 label: "toggle1.off.label",
                                 publishTopic: "CLICK",
                                 publishPayload: {
                                    clicked: "TOGGLE_WITH_LABEL",
                                    value: "OFF"
                                 }
                              }
                           }
                        },
                        {
                           // Should be initially set to off ("Low")...
                           name: "alfresco/menus/AlfMenuBarToggle",
                           config: {
                              checked: false,
                              id: "MENU_BAR_TOGGLE_WITH_ICON",
                              onConfig: {
                                 label: "toggle2.on.label",
                                 iconClass: "alf-sort-ascending-icon",
                                 publishTopic: "CLICK",
                                 publishPayload: {
                                    clicked: "TOGGLE_WITH_ICON",
                                    value: "ON"
                                 }
                              },
                              offConfig: {
                                 label: "toggle2.off.label",
                                 iconClass: "alf-sort-descending-icon",
                                 publishTopic: "CLICK",
                                 publishPayload: {
                                    clicked: "TOGGLE_WITH_ICON",
                                    value: "OFF"
                                 }
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