<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/document-library.lib.js">

// Ideally we'd build the array of widgets to go in the main vertical stack starting with the header widgets 
// and then adding in the document library widgets. However, this won't be possible until either the full-page.get.html.ftl
// template has been updated to include all of the "legacy" resources (e.g. YAHOO), or they have been explicitly requested
// as non-AMD dependencies in the widgets referenced on the page. In the meantime this page will be rendered as a hybrid.
// var widgets = getHeaderModel().concat([getDocumentLibraryModel("", "", user.properties['userHome'])]);

var services = getDocumentLibraryServices(page.url.templateArgs.site, "documentLibrary", null);
var widgets = [getDocumentLibraryModel(page.url.templateArgs.site, "documentLibrary", null)];

// Get the name of the current site (if available) to set as the page title...
var pageTitle = "",
    siteData = getSiteData();
if (siteData != null)
{
   pageTitle = siteData.profile.title;
}

// Build the page definition...
model.jsonModel = {
   services: services,
   widgets: [
      {
         id: "SET_PAGE_TITLE",
         name: "alfresco/header/SetTitle",
         config: {
            title: pageTitle
         }
      },
      {
         id: "SHARE_VERTICAL_LAYOUT",
         name: "alfresco/layout/VerticalWidgets",
         config: 
         {
            widgets: widgets
         }
      }
   ]
};
