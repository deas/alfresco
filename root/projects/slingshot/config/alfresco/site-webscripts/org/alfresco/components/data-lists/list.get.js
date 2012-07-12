function main()
{
   model.lists = ['listA', 'listB'];

   // Widget instantiation metadata...
   model.widgets = [];
   var list = {
      name : "Alfresco.DataListList",
      options : {
         siteId : (page.url.templateArgs["site"] != null) ? page.url.templateArgs["site"] : ""
      }
   };
   model.widgets.push(list);
}

main();
