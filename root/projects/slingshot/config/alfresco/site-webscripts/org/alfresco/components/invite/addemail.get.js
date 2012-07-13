function main()
{
   // get details of the authentication chain
   var res = remote.call("/api/authentication");
   var json = eval('(' + res + ')');

   model.allowEmailInvite = json.data.creationAllowed;

   // Widget instantiation metadata...
   model.widgets = [];
   var addEmailInvite = {
      name : "Alfresco.AddEmailInvite",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : ""
      }
   };
   model.widgets.push(addEmailInvite);
}

main();

