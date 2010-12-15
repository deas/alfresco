// get details of the authentication chain
var res = remote.call("/api/authentication");
var json = eval('(' + res + ')');

model.allowEmailInvite = json.data.creationAllowed;