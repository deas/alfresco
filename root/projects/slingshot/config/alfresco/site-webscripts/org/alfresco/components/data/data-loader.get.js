function main()
{
   model.widgets = [];

   var dataLoader = {
      name : "Alfresco.DataLoader",
      options : {
         url : (args.url != null) ? args.url : ""
      }
   };
   if (args.eventData != null)
   {
      dataLoader.options.eventData = args.eventData;
   }
   if (args.useProxy != null)
   {
      dataLoader.options.useProxy = args.useProxy;
   }
   if (args.failureMessageKey != null)
   {
      dataLoader.options.failureMessageKey = args.failureMessageKey;
   }
   model.widgets.push(dataLoader);
}

main();
