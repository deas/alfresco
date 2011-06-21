var userid = user.name;

if (args["unfollowuser"])
{
   userid = args["unfollowuser"];
   var params = new Array(1);
   params.push(userid);

   var connector = remote.connect("alfresco");
   var result = connector.post("/api/subscriptions/" + encodeURIComponent(user.name) + "/unfollow",
                               jsonUtils.toJSONString(params),
                               "application/json");
}
else if (args["followuser"])
{
   userid = args["followuser"];
   var params = new Array(1);
   params.push(userid);

   var connector = remote.connect("alfresco");
   var result = connector.post("/api/subscriptions/" + encodeURIComponent(user.name) + "/follow",
                               jsonUtils.toJSONString(params),
                               "application/json");
}


status.code = 302;
status.location = url.context + "/page/user/" + encodeURIComponent(userid) + "/profile";
status.redirect = true;