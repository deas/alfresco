/**
 * Site Members People component GET method
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

//Widget instantiation metadata...
var searchConfig = config.scoped['Search']['search'],
    defaultMinSearchTermLength = searchConfig.getChildValue('min-search-term-length'),
    defaultMaxSearchResults = searchConfig.getChildValue('max-search-results');

model.widgets = [];
var siteMembers = {};
siteMembers.name = "Alfresco.SiteMembers";
siteMembers.useMessages = true;
siteMembers.useOptions = true;
siteMembers.options = {};
siteMembers.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
siteMembers.options.currentUser = user.name;
siteMembers.options.currentUserRole = model.currentUserRole;
if (model.error)
{
   siteMembers.options.error = model.error;
}
siteMembers.options.roles = model.siteRoles;
siteMembers.options.minSearchTermLength = (args.minSearchTermLength != null) ? args.minSearchTermLength : defaultMinSearchTermLength;
siteMembers.options.maxSearchResults = (args.maxSearchResults != null) ? args.maxSearchResults : defaultMaxSearchResults;
siteMembers.options.setFocus = (args.setFocus != null) ? args.setFocus : "false";
model.widgets.push(siteMembers);