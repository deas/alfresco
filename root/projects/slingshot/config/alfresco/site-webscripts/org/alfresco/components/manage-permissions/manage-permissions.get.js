// Repository Library root node
var rootNode = "alfresco://company/home",
   repoConfig = config.scoped["RepositoryLibrary"]["root-node"];
if (repoConfig !== null)
{
   rootNode = repoConfig.value;
}

model.rootNode = rootNode;

// Widget instantiation metadata...
model.widgets = [];
var managePermissions = {};
managePermissions.name = "Alfresco.component.ManagePermissions";
managePermissions.useMessages = true;
managePermissions.useOptions = true;
managePermissions.options = {};
managePermissions.options.nodeRef = args.nodeRef;
model.widgets.push(managePermissions);