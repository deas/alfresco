<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">

doclibCommon();

model.webScriptWidgets = [];

var documentList = {};
documentList.name = "Alfresco.DocumentList";
documentList.provideOptions = true;
documentList.provideMessages = true;
documentList.options = {};
if (model.repositoryUrl != null)
{
   documentList.options.repositoryUrl = model.repositoryUrl;
}
documentList.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
documentList.options.containerId = template.properties.container != null ? template.properties.container : "documentLibrary";
documentList.options.rootNode = model.rootNode != null ? model.rootNode : "null";
documentList.options.usePagination = args.pagination != null ? args.pagination : false;
documentList.options.sortAscending = model.preferences.sortAscending != null ? model.preferences.sortAscending : true;
documentList.options.sortField = model.preferences.sortField != null ? model.preferences.sortField : "cm:name";
documentList.options.showFolders = model.preferences.showFolders != null ? model.preferences.showFolders : true;
documentList.options.simpleView = model.preferences.simpleView != null ? model.preferences.simpleView : false;
documentList.options.viewRendererName = model.viewRendererName != null ? model.viewRendererName : "detailed";
documentList.options.viewRendererNames = model.viewRendererNames != null ? model.viewRendererNames : ["simple", "detailed"];
documentList.options.highlightFile = page.url.args["file"] != null ? page.url.args["file"] : "";
documentList.options.replicationUrlMapping = model.replicationUrlMapping != null ? model.replicationUrlMapping : "{}";
documentList.options.repositoryBrowsing = model.repositoryBrowsing != null; 
documentList.options.useTitle = model.useTitle != null ? model.useTitle : true;
documentList.options.userIsSiteManager = model.userIsSiteManager != null ? model.userIsSiteManager : false;
model.webScriptWidgets.push(documentList);
