<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-footer.lib.js">

// Get the initial header services and widgets...
var services = getHeaderServices(),
    widgets = getHeaderModel("My Files");

// Get the DocLib specific services and widgets...
// var docLibServices = [];
// var docLibWidgets = [getDocumentLibraryModel(null, null, user.properties['userHome'])];

// // Change the root label of the tree to be "My Files" rather than "Documents"
// var tree = widgetUtils.findObject(widgets, "id", "DOCLIB_TREE");
// if (tree != null)
// {
//    tree.config.rootLabel = "my-files.root.label";
// }

var doclib = {
   name: "alfresco/layout/AlfSideBarContainer",
   config: {
      showSidebar: true,
      footerHeight: 50,
      customResizeTopics: ["ALF_DOCLIST_READY","ALF_RESIZE_SIDEBAR"],
      widgets: [
         {
            id: "DOCLIB_SIDEBAR_BAR",
            align: "sidebar",
            name: "alfresco/layout/VerticalWidgets",
            config: {
               widgets: [
                  {
                     id: "DOCLIB_FILTERS",
                     name: "alfresco/documentlibrary/AlfDocumentFilters",
                     config: {
                        label: "filter.label.documents",
                        widgets: [
                           {
                              name: "alfresco/documentlibrary/AlfDocumentFilter",
                              config: {
                                 label: "link.all",
                                 filter: "all",
                                 description: "link.all.description"
                              }
                           }
                        ]
                     }
                  },
                  {
                     name: "alfresco/layout/Twister",
                     config: {
                        label: "my-files.root.label",
                        widgets: [
                           {
                              name: "alfresco/navigation/PathTree",
                              config: {
                                 siteId: null,
                                 containerId: null,
                                 rootNode: user.properties['userHome']
                              }
                           }
                        ]
                     }
                  },
                  {
                     id: "DOCLIB_TAGS",
                     name: "alfresco/documentlibrary/AlfTagFilters",
                     config: {
                        label: "filter.label.tags"
                     }
                  },
                  {
                     name: "alfresco/layout/Twister",
                     config: {
                        label: "twister.categories.label",
                        widgets: [
                           {
                              name: "alfresco/navigation/CategoryTree"
                           }
                        ]
                     }
                  }
               ]
            }
         },
         {
            id: "DOCLIB_SIDEBAR_MAIN",
            name: "alfresco/layout/VerticalWidgets",
            config: 
            {
               widgets: [
                  {
                     id: "DOCLIB_DOCUMENT_LIST",
                     name: "alfresco/documentlibrary/AlfDocumentList",
                     config: {
                        useHash: true,
                        rootNode: user.properties['userHome'],
                        usePagination: true,
                        widgets: [
                           {
                              name: "alfresco/documentlibrary/views/AlfSimpleView"
                           },
                           {
                              name: "alfresco/documentlibrary/views/AlfDetailedView"
                           },
                           {
                              name: "alfresco/documentlibrary/views/AlfGalleryView"
                           }
                        ]
                     }
                  }
               ]
            }
         }
      ]
   }
};


// Add the DocLib services and widgets...
services.push("alfresco/services/NavigationService",
              "alfresco/services/SearchService",
              "alfresco/services/ActionService",
              "alfresco/services/DocumentService",
              "alfresco/dialogs/AlfDialogService");
widgets.push(doclib);

// Push services and widgets into the getFooterModel to return with a sticky footer wrapper
model.jsonModel = getFooterModel(services, widgets);
model.jsonModel.groupMemberships = user.properties["alfUserGroups"];
