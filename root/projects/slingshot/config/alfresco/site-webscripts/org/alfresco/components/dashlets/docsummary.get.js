// Get the Documents Modified data for this site
var url = args.dod5015 ? "/slingshot/doclib/dod5015/doclist/documents/site/" : "/slingshot/doclib/doclist/documents/site/";
var json = remote.call(url + page.url.templateArgs.site + "/documentLibrary?filter=recentlyModified&max=10");
var obj = eval('(' + json + ')');
if (json.status == 200)
{
   // Create the model
   model.docs = obj;
}
else
{
   model.docs = {message: obj.message};
}