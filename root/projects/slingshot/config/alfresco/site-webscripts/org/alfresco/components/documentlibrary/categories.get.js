model.webScriptWidgets = [];

var evaluateChildFolders = "true";
var docLibConfig = config.scoped["DocumentLibrary"];
if (docLibConfig != null)
{
   var categories = docLibConfig["categories"];
   if (categories != null)
   {
      var tmp = categories.getChildValue("evaluate-child-folders");
      evaluateChildFolders = tmp != null ? tmp : "true";
   }
}

var categories = {};
categories.name = "Alfresco.DocListCategories";
categories.provideOptions = true;
categories.provideMessages = true;
categories.options = {};
categories.options.nodeRef = "alfresco://category/root"; 
categories.options.evaluateChildFolders = evaluateChildFolders;
model.webScriptWidgets.push(categories);