function main()
{
   // Widget instantiation metadata...
   
   var editMode = false;
   var linkId = "";
   if (page.url.args.linkId != null)
   {
      editMode = true;
      linkId = page.url.args.linkId;
   }
   
   model.widgets = [];
   var linkEdit = {
      name : "Alfresco.LinkEdit",
      options : {
         siteId : page.url.templateArgs.site != null,
         containerId : "links",
         editMode : editMode,
         linkId : linkId
      }
   };
   model.widgets.push(linkEdit);
}

main();

