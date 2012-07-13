function main()
{
   // Widget instantiation metadata...
   var searchConfig = config.scoped['Search']['search'],
       defaultMinSearchTermLength = searchConfig.getChildValue('min-search-term-length'),
       defaultMaxSearchResults = searchConfig.getChildValue('max-search-results');
   
   model.widgets = [];
   var sentInvites = {
      name : "Alfresco.SentInvites",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         minSearchTermLength : (args.minSearchTermLength != null) ? args.minSearchTermLength : defaultMinSearchTermLength,
         maxSearchResults : (args.maxSearchResults != null) ? args.maxSearchResults : defaultMaxSearchResults,
         setFocus : (args.setFocus != null) ? args.setFocus : "false"
      }
   };
   model.widgets.push(sentInvites);
}

main();
