<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/document-library.lib.js">

// Get the initial header services and widgets...
var services = getHeaderServices(),
    widgets = getHeaderModel("My Files");

// Get the DocLib specific services and widgets...
var docLibServices = getDocumentLibraryServices();
var docLibWidgets = getDocumentLibraryModel(null, null, user.properties['userHome']);

// Add the DocLib services and widgets...
services.push("alfresco/services/PreferenceService",
              "alfresco/services/NavigationService",
              "alfresco/services/SearchService",
              "alfresco/services/ActionService",
              "alfresco/services/DocumentService",
              "alfresco/services/TagService",
              "alfresco/services/RatingsService",
              "alfresco/dialogs/AlfDialogService");
widgets.push(docLibWidgets);

// Push services and widgets into the getFooterModel to return with a sticky footer wrapper
model.jsonModel = getFooterModel(services, widgets);
model.jsonModel.groupMemberships = user.properties["alfUserGroups"];
