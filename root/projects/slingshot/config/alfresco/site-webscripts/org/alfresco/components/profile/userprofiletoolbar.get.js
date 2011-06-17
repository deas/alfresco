/**
 * User Profile - Toolbar Component GET method
 */

var userId = page.url.templateArgs["userid"];
model.activeUserProfile = (userId == null || userId == user.name);

if(model.activeUserProfile) {
	var following = remote.call("/api/subscriptions/" + encodeURIComponent(userId) + "/following/count");
	model.following = eval('(' + following + ')').count;
	
	var followers = remote.call("/api/subscriptions/" + encodeURIComponent(userId) + "/followers/count");
	model.followers = eval('(' + followers + ')').count;
}
