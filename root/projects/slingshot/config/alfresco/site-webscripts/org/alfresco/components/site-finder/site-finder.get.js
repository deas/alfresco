var url = "/api/invitations?inviteeUserName=" + encodeURIComponent(user.name),
   result = remote.connect("alfresco").get(url),
   inviteData = [];

if (result.status == status.STATUS_OK)
{
   var json = eval('(' + result.response + ')');
   inviteData = json.data;
}

model.inviteData = inviteData;


//Widget instantiation metadata...
var searchConfig = config.scoped['Search']['search'],
    defaultMinSearchTermLength = searchConfig.getChildValue('min-search-term-length'),
    defaultMaxSearchResults = searchConfig.getChildValue('max-search-results');

model.webScriptWidgets = [];
var siteFinder = {};
siteFinder.name = "Alfresco.SiteFinder";
siteFinder.provideMessages = true;
siteFinder.provideOptions = true;
siteFinder.options = {};
siteFinder.options.currentUser = user.name;
siteFinder.options.minSearchTermLength = (args.minSearchTermLength != null) ? args.minSearchTermLength : defaultMinSearchTermLength;
siteFinder.options.maxSearchResults = (args.maxSearchResults != null) ? args.maxSearchResults : defaultMaxSearchResults;
siteFinder.options.setFocus = (args.setFocus != null) ? args.setFocus : "false";
siteFinder.options.inviteData = [];
for (var i = 0; i < model.inviteData.length; i++)
{
   var invite = {};
   invite.id = model.inviteData[i].inviteId;
   invite.siteId = model.inviteData[i].resourceName;
   invite.type = model.inviteData[i].invitationType;
   siteFinder.options.inviteData.push(invite);
}
model.webScriptWidgets.push(siteFinder);