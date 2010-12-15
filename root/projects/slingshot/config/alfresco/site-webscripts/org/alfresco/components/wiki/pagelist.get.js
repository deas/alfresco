// retrieve the wiki pages for the current site
var uri = "/slingshot/wiki/pages/" + page.url.templateArgs.site;
var filter = page.url.args.filter;
if (filter)
{
   uri += "?filter=" + filter;
}

var connector = remote.connect("alfresco");
var result = connector.get(uri);
if (result.status.code == status.STATUS_OK)
{
   model.pageList = eval('(' + result.response + ')');
}
else
{
   model.error = "Error during remote call. Server code " + result.status + ".";
}