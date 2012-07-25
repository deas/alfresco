function main() {
   
   var search = null,
       searchMain = config.scoped["Search"];
   if (searchMain != null)
   {
      search = searchMain["search"];
   }
   var users = null,
       usersMain = config.scoped["Users"];
   if (usersMain != null)
   {
      users = usersMain["users"];
   }
   
   var minSearchTermLength = (args.minSearchTermLength != null) ? args.minSearchTermLength : search.getChildValue("min-search-term-length"),
       maxSearchResults  = (args.maxSearchResults != null) ? args.maxSearchResults : search.getChildValue("max-search-results"),
       minUsernameLength = users.getChildValue('username-min-length'),
       minPasswordLength = users.getChildValue('password-min-length');
   
   // Widget instantiation metadata...
   var widget = {
      id : "ConsoleUsers", 
      name : "Alfresco.ConsoleUsers",
      options : {
         minSearchTermLength: minSearchTermLength,
         maxSearchResults: maxSearchResults,
         minUsernameLength: minUsernameLength,
         minPasswordLength: minPasswordLength
      }
   };
   model.widgets = [widget];
}
main();