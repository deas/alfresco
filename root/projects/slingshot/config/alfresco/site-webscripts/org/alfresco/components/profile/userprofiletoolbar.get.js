/**
 * User Profile - Toolbar Component GET method
 */

var userId = page.url.templateArgs["userid"];
if (userId == null)
{
   userId = user.name;
}
model.activeUserProfile = (userId == user.name);

model.following = -1
model.followers = -1

var following = remote.call("/api/subscriptions/" + encodeURIComponent(userId) + "/following/count");
if (following.status == 200)
{
   model.following = eval('(' + following + ')').count;
   
   if (model.activeUserProfile)
   {
	   var followers = remote.call("/api/subscriptions/" + encodeURIComponent(userId) + "/followers/count");
	   if(followers.status == 200)
	   {
	      model.followers = eval('(' + followers + ')').count;
	   }
   } 
}