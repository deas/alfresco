model.widgets = [];

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
categories.useOptions = true;
categories.useMessages = true;
categories.options = {};
categories.options.nodeRef = "alfresco://category/root"; 
categories.options.evaluateChildFolders = evaluateChildFolders;
model.widgets.push(categories);