var params = new Array(2);
params["private"] = (args["private"] == "1");

var connector = remote.connect("alfresco");
var result = connector.put("/api/subscriptions/" + encodeURIComponent(user.name) + "/private",
                           jsonUtils.toJSONString(params),
                           "application/json");

status.code = 302;
status.location = url.context + "/page/user/" + encodeURIComponent(user.name) + "/following";
status.redirect = true;