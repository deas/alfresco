var siteId, theUrl, json, data;

siteId = page.url.templateArgs.site;

// get the roles available for the given site
theUrl = "/api/sites/" + siteId + "/roles";
json = remote.call(theUrl);
data = eval('(' + json + ')');

// add all roles except "None"
model.siteRoles = [];
for (var i = 0, j = data.siteRoles.length; i < j; i++)
{
   if (data.siteRoles[i] != "None")
   {
      model.siteRoles.push(data.siteRoles[i]);
   }
}

// Widget instantiation metadata...
model.webScriptWidgets = [];
var groupsList = {};
groupsList.name = "Alfresco.GroupsList";
groupsList.provideMessages = true;
groupsList.provideOptions = true;
groupsList.options = {};
groupsList.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
groupsList.options.roles = model.siteRoles;
model.webScriptWidgets.push(groupsList);