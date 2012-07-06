/**
 * Site Members Groups component GET method
 */
function main()
{
   var siteId, theUrl, json, membership, data;
   
   siteId = page.url.templateArgs.site;
   
   // get the membership info for the current user in the current site
   theUrl = "/api/sites/" + siteId + "/memberships/" + encodeURIComponent(user.name);
   json = remote.call(theUrl);
   membership = eval('(' + json + ')');
   
   // add the role to the model
   model.currentUserRole = membership.role ? membership.role : "";
   
   // get the roles available in the current site
   theUrl = "/api/sites/" + siteId + "/roles";
   json = remote.call(theUrl);
   data = eval('(' + json + ')');
   
   // add all roles except "None"
   model.siteRoles = [];
   
   if (json.status == 200 && data.siteRoles)
   {
      for (var i = 0, j = data.siteRoles.length; i < j; i++)
      {
         if (data.siteRoles[i] != "None")
         {
            model.siteRoles.push(data.siteRoles[i]);
         }
      }
   }
   else
   {
      model.error = membership.message;
   }
}

main();


// Widget instantiation metadata...
var searchConfig = config.scoped['Search']['search'],
    defaultMinSearchTermLength = searchConfig.getChildValue('min-search-term-length'),
    defaultMaxSearchResults = searchConfig.getChildValue('max-search-results');

model.webScriptWidgets = [];
var siteGroups = {};
siteGroups.name = "Alfresco.SiteGroups";
siteGroups.provideMessages = true;
siteGroups.provideOptions = true;
siteGroups.options = {};
siteGroups.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
siteGroups.options.currentUser = user.name;
siteGroups.options.currentUserRole = model.currentUserRole;
if (model.error)
{
   siteGroups.options.error = model.error;
}
siteGroups.options.roles = model.siteRoles;
siteGroups.options.minSearchTermLength = (args.minSearchTermLength != null) ? args.minSearchTermLength : defaultMinSearchTermLength;
siteGroups.options.maxSearchResults = (args.maxSearchResults != null) ? args.maxSearchResults : defaultMaxSearchResults;
siteGroups.options.setFocus = (args.setFocus != null) ? args.setFocus : "false";
model.webScriptWidgets.push(siteGroups);