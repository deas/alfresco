<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/document-library.lib.js">

// Ideally we'd build the array of widgets to go in the main vertical stack starting with the header widgets 
// and then adding in the document library widgets. However, this won't be possible until either the full-page.get.html.ftl
// template has been updated to include all of the "legacy" resources (e.g. YAHOO), or they have been explicitly requested
// as non-AMD dependencies in the widgets referenced on the page. In the meantime this page will be rendered as a hybrid.
// var widgets = getHeaderModel().concat([getDocumentLibraryModel("", "", user.properties['userHome'])]);

var services = getDocumentLibraryServices(null, null, user.properties['userHome']);
var widgets = [getDocumentLibraryModel(null, null, user.properties['userHome'])];

// Change the root label of the tree to be "My Files" rather than "Documents"
var tree = widgetUtils.findObject(widgets, "id", "DOCLIB_TREE");
if (tree != null)
{
   tree.config.rootLabel = "my-files.root.label";
}

model.jsonModel = {
   services: services,
   widgets: [
      {
         id: "SET_PAGE_TITLE",
         name: "alfresco/header/SetTitle",
         config: {
            title: "My Files"
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