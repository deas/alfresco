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

// Widget instantiation metadata...
model.webScriptWidgets = [];
var wikiList = {};
wikiList.name = "Alfresco.WikiList";
wikiList.provideMessages = true;
wikiList.provideOptions = true;
wikiList.options = {};
wikiList.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
wikiList.options.pages = [];
if (model.pageList != null)
{
   for (var i=0; i<model.pageList.length; i++)
   {
      wikiList.options.pages.push(model.pageList[i].name);
   }
}
wikiList.options.permissions = {};
wikiList.options.permissions.create = (model.pageList != null && model.pageList.permissions != null && model.pageList.permissions.create != null) ? model.pageList.permissions.create : "false";
wikiList.options.filterId = (page.url.args.filter != null) ? page.url.args.filter : "recentlyModified";
model.webScriptWidgets.push(wikiList);