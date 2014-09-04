model.jsonModel = {
   services: [
      {
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
      "aikauTesting/mockservices/FormControlValidationTestService",
      "alfresco/services/ErrorReporter"
   ],
   widgets: [
      {
         name: "alfresco/forms/Form",
         config: {
            widgets: [
               {
                  id: "TEST_CONTROL",
                  name: "alfresco/forms/controls/DojoValidationTextBox", 
                  config: {
                     label: "Three Letters Or More",
                     name: "name",
                     value: "",
                     validationConfig: [
                        {
                           validation: "minLength",
                           length: 3,
                           errorMessage: "Too short"
                        },
                        {
                           validation: "maxLength",
                           length: 5,
                           errorMessage: "Too long"
                        },
                        {
                           validation: "regex",
                           regex: "^[A-Za-z]+$",
                           errorMessage: "Letters only"
                        },
                        {
                           validation: "validateUnique",
                           errorMessage: "Already used",
                           itemsProperty: "someData",
                           publishTopic: "GET_DUMMY_VALUES"
                        }
                     ]
                  }
               }
            ]
         }
      },
      {
         id: "BLOCK_RESPONSE",
         name: "alfresco/buttons/AlfButton",
         config: {
            label: "Block responses",
            publishTopic: "BLOCK_RESPONSES"
         }
      },
      {
         id: "UNBLOCK_RESPONSE",
         name: "alfresco/buttons/AlfButton",
         config: {
            label: "Unblock responses",
            publishTopic: "UNBLOCK_RESPONSES"
         }
      },
      {
         name: "alfresco/logging/SubscriptionLog"
      },
      {
         name: "aikauTesting/TestCoverageResults"
      }
   ]
};