// Get the all image files in the doclibrary for this site
var url = "/slingshot/doclib/images/site/" + page.url.templateArgs.site + "/documentLibrary?max=500";
var json = remote.call(url);
if (json.status == 200)
{
   // Create the model from the response data
   model.images = eval('(' + json + ')');
}
else
{
   model.images = {message: json.message};
}