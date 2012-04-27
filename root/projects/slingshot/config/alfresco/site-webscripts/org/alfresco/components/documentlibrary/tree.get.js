model.webScriptWidgets = [];

var evaluateChildFolders = "true",
    maximumFolderCount = "-1";
var docLibConfig = config.scoped["DocumentLibrary"];
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

var docListTree = {};
docListTree.name = "Alfresco.DocListTree";
docListTree.provideOptions = true;
docListTree.provideMessages = true;
docListTree.options = {};
docListTree.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
docListTree.options.containerId = template.properties.container != null ? template.properties.container : "documentLibrary";
docListTree.options.evaluateChildFolders = evaluateChildFolders;
docListTree.options.maximumFolderCount = maximumFolderCount;
docListTree.options.setDropTargets = true;
model.webScriptWidgets.push(docListTree);