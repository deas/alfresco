function main()
{
   // Widget instantiation metadata...
   model.widgets = [];
   var path = {
      name : "Alfresco.component.Path",
      options : {
         showIconType : (args.showIconType != null) ? args.showIconType : "true"
      }
   };
   model.widgets.push(path);
}

main();

