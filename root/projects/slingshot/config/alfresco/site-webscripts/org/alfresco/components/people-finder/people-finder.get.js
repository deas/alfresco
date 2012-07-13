function main()
{
   // Widget instantiation metadata...
   var searchConfig = config.scoped['Search']['search'],
       defaultMinSearchTermLength = searchConfig.getChildValue('min-search-term-length'),
       defaultMaxSearchResults = searchConfig.getChildValue('max-search-results');

   model.widgets = [];
   var peopleFinder = {
      name : "Alfresco.PeopleFinder",
      options : {
         userId : user.name,
         siteId : (this.page != null) ? ((this.page.url.templateArgs.site != null) ? this.page.url.templateArgs.site : "") : ((args.site != null) ? args.site : ""),
         minSearchTermLength : (args.minSearchTermLength != null) ? args.minSearchTermLength : defaultMinSearchTermLength,
         maxSearchResults : (args.maxSearchResults != null) ? args.maxSearchResults : defaultMaxSearchResults,
         setFocus : (args.setFocus != null) ? args.setFocus : "false",
         addButtonSuffix : (args.addButtonSuffix != null) ? args.addButtonSuffix : "",
         dataWebScript : ((args.dataWebScript != null) ? args.dataWebScript : "api/groups").replace(/{/g, "[").replace(/}/g, "]"),
         viewMode : { ___value : "Alfresco.PeopleFinder.VIEW_MODE_DEFAULT", ___type: "REFERENCE"}
      }
   };
   model.widgets.push(peopleFinder);
}

main();

