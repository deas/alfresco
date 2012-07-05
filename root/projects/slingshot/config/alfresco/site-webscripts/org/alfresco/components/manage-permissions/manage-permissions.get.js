// Repository Library root node
var rootNode = "alfresco://company/home",
   repoConfig = config.scoped["RepositoryLibrary"]["root-node"];
if (repoConfig !== null)
{
   rootNode = repoConfig.value;
}

model.rootNode = rootNode;

// Widget instantiation metadata...
model.webScriptWidgets = [];
var managePermissions = {};
managePermissions.name = "Alfresco.component.ManagePermissions";
managePermissions.provideMessages = true;
managePermissions.provideOptions = true;
managePermissions.options = {};
managePermissions.options.nodeRef = args.nodeRef;
model.webScriptWidgets.push(managePermissions);