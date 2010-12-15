// Get the Documents Modified data for this site
var url = args.dod5015 ? "/slingshot/doclib/dod5015/doclist/documents/site/" : "/slingshot/doclib/doclist/documents/site/";
var json = remote.call(url + page.url.templateArgs.site + "/documentLibrary?filter=recentlyModified&max=10");
if (json.status == 200)
{
   // Create the model
   var docs = eval('(' + json + ')');
   model.docs = docs;
}
else
{
   model.docs = {message: json.message};
}