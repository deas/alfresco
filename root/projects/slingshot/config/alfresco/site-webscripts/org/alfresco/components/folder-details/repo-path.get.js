function main()
{
   // Widget instantiation metadata...
   model.widgets = [];
   var path = {
      name : "Alfresco.component.Path",
      options : {
         rootPage : "repository",
         rootLabelId : "path.repository"
      }
   };
   model.widgets.push(path);
}

main();
