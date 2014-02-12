<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header-access.lib.js">

var headerContent = getHeaderModel();
var headerServices = getHeaderServices();

model.jsonModel = {
   services: headerServices,
   widgets: [
      {
         name: "alfresco/accessibility/AccessibilityMenu",
         config: {
            menu: [
               {url: "#accesskey-skip", key: "s", msg: "skip.to.content.message"},
               {url: "/share/page/accessibility-help", key: "0", msg: "access.keys.message"},
               {url: "/share/page/user/admin/dashboard", key: "1", msg: "home.page.message"},
               {url: "/share/page/advsearch", key: "4", msg: "search.this.site.message"},
               {url: "/share/page/site-help", key: "6", msg: "accessibility.help.message"},
               {url: "/share/page/terms", key: "8", msg: "terms.and.conditions.message"},
               {url: "#accesskey-foot", key: "b", msg: "skip.to.foot.message"}
            ],
            targets: [
               {domid: "HEADER_TITLE", targetid: "accesskey-skip", after: false},
               {domid: "MAIN_CONTENT", targetid: "accesskey-foot", after: true}
            ]
         }
      },
      {
         id: "SHARE_VERTICAL_LAYOUT",
         name: "alfresco/layout/VerticalWidgets",
         config: {
            id: "MAIN_CONTENT",
            widgets: headerContent
         }
      }
   ]
};