model.jsonModel = {
   services: [
      "alfresco/testing/UnitTestService"
   ],
   widgets: [
      {
         name: "alfresco/forms/Form",
         config: {
            id: "UNIT_TEST_FORM",
            showOkButton: false,
            showCancelButton: false,
            widgets: [
               {
                  name: "alfresco/forms/controls/DojoTextarea",
                  config: {
                     id: "UNIT_TEST_MODEL_FIELD",
                     label: "Test Model",
                     name: "unitTestModel",
                     requirementConfig: {
                        initialValue: true
                     }
                  }
               }
            ],
            widgetsAdditionalButtons: [
               {
                  name: "alfresco/buttons/AlfButton",
                  config: {
                     id: "LOAD_TEST_BUTTON",
                     label: "TEST",
                     publishGlobal: true,
                     publishTopic: "ALF_REQUEST_UNIT_TEST"
                  }
               }
            ]
         }
      }
   ]
};
