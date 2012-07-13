function main()
{
   // Widget instantiation metadata...
   model.widgets = [];
   var wikiCreateForm = {
      name : "Alfresco.WikiCreateForm",
      options : {
         siteId :(page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         locale : this.locale.substring(0, 2)
      }
   };
   model.widgets.push(wikiCreateForm);
}

main();
