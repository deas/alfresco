function main()
{
   // Widget instantiation metadata...
   var authorityFinder = {
      id : "AuthorityFinder", 
      name : "Alfresco.AuthorityFinder",
      options : {
         siteId : (page.exists == true) ? ((page.url.templateArgs.site != null) ? page.url.templateArgs.site : "") : ((args.site != null) ? args.site : ""),
         minSearchTermLength : parseInt((args.minSearchTermLength != null) ? args.minSearchTermLength : "3"),
         maxSearchResults : parseInt((args.maxSearchResults != null) ? args.maxSearchResults : "100"),
         setFocus : Boolean((args.setFocus != null) ? args.setFocus : "false"),
         addButtonSuffix : (args.addButtonSuffix != null) ? args.addButtonSuffix : "",
         dataWebScript : { _alfValue : "dataWebScript", _alfType: "REFERENCE"},
         viewMode : { _alfValue : "Alfresco.AuthorityFinder.VIEW_MODE_DEFAULT", _alfType: "REFERENCE"},
         authorityType : { _alfValue : "Alfresco.AuthorityFinder.AUTHORITY_TYPE_ALL", _alfType: "REFERENCE"}
      }
   };
   model.widgets = [authorityFinder];
}

main();

