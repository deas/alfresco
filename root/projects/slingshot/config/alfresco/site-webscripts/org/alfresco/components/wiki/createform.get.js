function main()
{
   // Widget instantiation metadata...
   var wikiCreateForm = {
      id : "WikiCreateForm", 
      name : "Alfresco.WikiCreateForm",
      options : {
         siteId :(page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         locale : this.locale.substring(0, 2)
      }
   };
   model.widgets = [wikiCreateForm];
}

main();
