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
      "alfresco/services/NavigationService",
      "alfresco/services/SearchService",
      "alfresco/services/ErrorReporter"
   ],
   widgets: [
      {
         name: "alfresco/documentlibrary/AlfSearchList",
         config: {
            useHash: true,
            useInfiniteScroll: true,
            widgets: [
               {
                  name: "alfresco/documentlibrary/views/AlfSearchListView"
               }
            ]
         }
      },
      {
         name: "alfresco/documentlibrary/AlfDocumentListInfiniteScroll"
      },
      {
         name: "alfresco/testing/mockservices/SearchScrollMockXhr"
      }
      {
         name: "alfresco/testing/SubscriptionLog"
      },
      {
         name: "alfresco/testing/TestCoverageResults"
      }
   ]
};