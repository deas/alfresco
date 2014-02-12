
var pageDefinition;
if (page.url.args.testdata != null)
{
   var json = page.url.args.testdata;
   try
   {
      model.jsonModel = jsonUtils.toObject(json);
   }
   catch(e)
   {
      model.jsonModelError = "test.page.load.error";
   }
}
else
{
   // No page name supplied...
   model.jsonModelError = "test.page.error.nopage"
}

