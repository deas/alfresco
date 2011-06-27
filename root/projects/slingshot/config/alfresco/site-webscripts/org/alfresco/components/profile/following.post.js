/**
 * User Profile Component - Following POST method
 */

function main()
{
   var params = new Array(1);
   params.push(args["unfollowuser"]);
   
   var connector = remote.connect("alfresco");
   var result = connector.post("/api/subscriptions/" + encodeURIComponent(user.name) + "/unfollow",
                               jsonUtils.toJSONString(params),
                               "application/json");
   
   status.code = 302;
   status.location = url.context + "/page/user/" + encodeURIComponent(user.name) + "/following";
   status.redirect = true;
}
main();