function main()
{
   // Widget instantiation metadata...
   model.widgets = [];
   var authorityFinder = {
      name : "Alfresco.AuthorityFinder",
      options : {
         siteId : (page.exists == true) ? ((page.url.templateArgs.site != null) ? page.url.templateArgs.site : "") : ((args.site != null) ? args.site : ""),
         minSearchTermLength =: (args.minSearchTermLength != null) ? args.minSearchTermLength : "3",
         maxSearchResults : (args.maxSearchResults != null) ? args.maxSearchResults : "100",
         setFocus : (args.setFocus != null) ? args.setFocus : "false",
         addButtonSuffix : (args.addButtonSuffix != null) ? args.addButtonSuffix : "",
         dataWebScript : { ___value : "dataWebScript", ___type: "REFERENCE"},
         viewMode : { ___value : "Alfresco.AuthorityFinder.VIEW_MODE_DEFAULT", ___type: "REFERENCE"},
         authorityType : { ___value : "Alfresco.AuthorityFinder.AUTHORITY_TYPE_ALL", ___type: "REFERENCE"}
      }
   };
   model.widgets.push(authorityFinder);
}

main();

