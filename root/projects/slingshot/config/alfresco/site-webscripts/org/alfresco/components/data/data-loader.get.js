function main()
{
   var dataLoader = {
      id : "DataLoader", 
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
      dataLoader.options.useProxy = Boolean(args.useProxy);
   }
   if (args.failureMessageKey != null)
   {
      dataLoader.options.failureMessageKey = args.failureMessageKey;
   }
   model.widgets = [dataLoader];
}

main();
