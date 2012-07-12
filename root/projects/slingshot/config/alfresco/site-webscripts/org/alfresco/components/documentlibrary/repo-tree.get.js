/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   // Repository Library root node
   var rootNode = "alfresco://company/home",
      repoConfig = config.scoped["RepositoryLibrary"]["root-node"];
   if (repoConfig !== null)
   {
      rootNode = repoConfig.value;
   }
   
   model.rootNode = rootNode;
   
   model.widgets = [];

   var evaluateChildFolders = "true",
       maximumFolderCount = "-1";
   var docLibConfig = config.scoped["RepositoryLibrary"];
   if (docLibConfig != null)
   {
      var tree = docLibConfig["tree"];
      if (tree != null)
      {
         var tmp = tree.getChildValue("evaluate-child-folders");
         evaluateChildFolders = tmp != null ? tmp : "true";
         tmp = tree.getChildValue("maximum-folder-count");
         maximumFolderCount = tmp != null ? tmp : "-1";
      }
   }

   var repoDocListTree = {};
   repoDocListTree.name = "Alfresco.RepositoryDocListTree";
   repoDocListTree.useOptions = true;
   repoDocListTree.useMessages = true;
   repoDocListTree.options = {};
   repoDocListTree.options.rootNode = model.rootNode != null ? model.rootNode : "null";
   repoDocListTree.options.evaluateChildFolders = evaluateChildFolders;
   repoDocListTree.options.maximumFolderCount = maximumFolderCount;
   repoDocListTree.options.setDropTargets = true;
   model.widgets.push(repoDocListTree);
}

main();