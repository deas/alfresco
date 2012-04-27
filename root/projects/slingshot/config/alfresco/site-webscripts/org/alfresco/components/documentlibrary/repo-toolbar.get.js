model.webScriptWidgets = [];

var useTitle = "true";
var docLibConfig = config.scoped["DocumentLibrary"];
if (docLibConfig != null)
{
   var tmp = docLibConfig["use-title"];
   useTitle = tmp != null ? tmp : "true";
}

var repoDocListToobar = {};
repoDocListToobar.name = "Alfresco.RepositoryDocListToolbar";
repoDocListToobar.provideOptions = true;
repoDocListToobar.provideMessages = true;
repoDocListToobar.options = {};
repoDocListToobar.options.rootNode = model.rootNode != null ? model.rootNode : "";
repoDocListToobar.options.hideNavBar = model.preferences.hideNavBar != null ? model.preferences.hideNavBar != null : "false";
repoDocListToobar.options.googleDocsEnabled = model.googleDocsEnabled != null ? model.googleDocsEnabled : "false";
repoDocListToobar.options.repositoryBrowsing = model.rootNode != null;
repoDocListToobar.options.useTitle = useTitle;
model.webScriptWidgets.push(repoDocListToobar);
