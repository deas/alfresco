function main()
{
   // Widget instantiation metadata...
   var path = {
      id : "Path", 
      name : "Alfresco.component.Path",
      options : {
         showIconType : (args.showIconType != null) ? args.showIconType : "true"
      }
   };
   model.widgets = [path];
}

main();

