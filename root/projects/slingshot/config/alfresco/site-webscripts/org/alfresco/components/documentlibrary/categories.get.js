function main()
{
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

   var categories = {
      name : "Alfresco.DocListCategories",
      options : {
         nodeRef : "alfresco://category/root", 
         evaluateChildFolders : evaluateChildFolders
      }
   };
   model.widgets.push(categories);
}

main();

