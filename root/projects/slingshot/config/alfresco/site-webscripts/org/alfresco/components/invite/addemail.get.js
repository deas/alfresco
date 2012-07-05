// get details of the authentication chain
var res = remote.call("/api/authentication");
var json = eval('(' + res + ')');

model.allowEmailInvite = json.data.creationAllowed;

// Widget instantiation metadata...
model.webScriptWidgets = [];
var addEmailInvite = {};
addEmailInvite.name = "Alfresco.AddEmailInvite";
addEmailInvite.provideMessages = true;
addEmailInvite.provideOptions = true;
addEmailInvite.options = {};
addEmailInvite.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
model.webScriptWidgets.push(addEmailInvite);