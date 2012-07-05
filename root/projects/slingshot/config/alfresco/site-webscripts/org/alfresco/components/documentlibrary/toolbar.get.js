<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/toolbar.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/upload/uploadable.lib.js">

model.webScriptWidgets = [];

var useTitle = "true";
var docLibConfig = config.scoped["DocumentLibrary"];
if (docLibConfig != null)
{
   var tmp = docLibConfig["use-title"];
   useTitle = tmp != null ? tmp : "true";
}

var docListToolbar = {};
docListToolbar.name = "Alfresco.DocListToolbar";
docListToolbar.provideOptions = true;
docListToolbar.provideMessages = true;
docListToolbar.options = {};
docListToolbar.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
docListToolbar.options.rootNode = model.rootNode != null ? model.rootNode : "";
docListToolbar.options.hideNavBar = model.preferences.hideNavBar != null ? model.preferences.hideNavBar != null : "false";
docListToolbar.options.googleDocsEnabled = model.googleDocsEnabled != null ? model.googleDocsEnabled : "false";
docListToolbar.options.repositoryBrowsing = model.rootNode != null;
docListToolbar.options.useTitle = useTitle;
model.webScriptWidgets.push(docListToolbar);
