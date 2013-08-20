model.jsonModel = {
   services: [],
   widgets: [
      {
         id: "SET_PAGE_TITLE",
         name: "alfresco/header/SetTitle",
         config: {
            title: "Kickstart Prototype"
         }
      },
      {
         name: "alfresco/layout/SlidingTabs",
         config: {
            widgets: [
               {
                  name: "alfresco/layout/TitleDescriptionAndContent",
                  title: "Create Workflow Template",
                  config: {
                     title: "Create Workflow Template",
                     description: "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt anim id est laborum. Lorem ipsum dolor sit amet",
                     widgets: [
                        {
                           name: "alfresco/forms/controls/DojoValidationTextBox",
                           config: {
                              label: "Name",
                              requirementConfig: {
                                 initialValue: true
                              }
                           }
                        },
                        {
                           name: "alfresco/forms/controls/DojoTextarea",
                           config: {
                              label: "Description"
                           }
                        },
                        {
                           name: "alfresco/forms/controls/DojoSelect",
                           config: {
                              label: "Template",
                              optionsConfig: {
                                 fixed: [{label:"Blank Template",value:"BLANK"}]
                              }
                           }
                        },
                        {
                           name: "alfresco/forms/controls/DojoCheckBox",
                           config: {
                              label: "Allow Commenting"
                           }
                        }
                     ]
                  }
               },
               {
                  name: "alfresco/layout/TitleDescriptionAndContent",
                  title: "Design Workflow Start Form",
                  config: {
                     generatePubSubScope: true,
                     title: "Design Workflow Start Form",
                     description: "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt anim id est laborum. Lorem ipsum dolor sit amet",
                     widgets: [
                        {
                           name: "alfresco/layout/SlideOverlay",
                           config: {
                              showTopics: ["ALF_CONFIGURE_WIDGET"],
                              hideTopics: ["ALF_UPDATE_RENDERED_WIDGET","ALF_CLEAR_CONFIGURE_WIDGET"],
                              adjustHeightTopics: ["ALF_CONFIGURE_WIDGET"],
                              widgets: [
                                 {
                                    name: "alfresco/layout/HorizontalWidgets",
                                    align: "underlay",
                                    assignTo: "layoutWidget",
                                    config: {
                                       widgets: [
                                          {
                                             name: "alfresco/creation/DropAndPreview",
                                             assignTo: "previewWidget",
                                             config: {
                                             }
                                          },
                                          {
                                             name: "alfresco/creation/DragWidgetPalette",
                                             config: {
                                             }
                                          }
                                       ]
                                    }
                                 },
                                 {
                                    name: "alfresco/creation/WidgetConfig",
                                    align: "overlay",
                                    assignTo: "configWidget",
                                    config: {
                                       width: "50%"
                                    }
                                 }
                              ]
                           }
                        }
                     ]
                  }
               },
               {
                  name: "alfresco/layout/TitleDescriptionAndContent",
                  title: "Define Workflow Steps",
                  config: {
                     generatePubSubScope: true,
                     title: "Define Workflow Steps",
                     description: "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt anim id est laborum. Lorem ipsum dolor sit amet",
                     widgets: [
                        {
                           name: "alfresco/layout/HorizontalWidgets",
                           config: {
                              widgets: [
                                 {
                                     name: "alfresco/kickstart/StepPalette",
                                     config: {
                                        width: "20%"
                                     }
                                  },
                                  {
                                     name: "alfresco/kickstart/StepDropAndPreview",
                                     config: {
                                        width: "80%"
                                     }
                                  }
                              ]
                           }
                        }
                     ]
                  }
               },
               {
                  generatePubSubScope: true,
                  name: "alfresco/layout/TitleDescriptionAndContent",
                  title: "Create and Activate",
                  config: {
                     title: "Create and Activate",
                     description: "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt anim id est laborum. Lorem ipsum dolor sit amet",
                     widgets: []
                  }
               }
            ]
         }
      }
   ]
};
