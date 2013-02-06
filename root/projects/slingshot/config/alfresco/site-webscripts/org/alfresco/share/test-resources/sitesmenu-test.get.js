model.jsonModel = {
   services: [
      "alfresco/tests/header/SitesMenuTestService"
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
                     widgets: [
                        {
                           name: "alfresco/header/AlfSitesMenu",
                           config: {
                              id: "SITES_MENU_1",
                              _menuUrl: "/fail"
                           }
                        },
                        {
                           name: "alfresco/header/AlfSitesMenu",
                           config: {
                              id: "SITES_MENU_2",
                              _menuUrl: "/share/service/sites-menu-data-2",
                              _favouritesUrl: "/fail",
                              currentSite: "testsite1",
                              currentUser: "Bob"
                           }
                        },
                        {
                           name: "alfresco/header/AlfSitesMenu",
                           config: {
                              id: "SITES_MENU_3",
                              _menuUrl: "/share/service/sites-menu-data-1",
                              _favouritesUrl: "/share/service/sites-menu-favourites-data",
                              currentSite: "testsite3",
                              currentUser: "Bob"
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