/**
 * User Profile - Title Component
 */

var profileId = page.url.templateArgs["userid"];
if (profileId != null)
{
   // load user details for the profile from the repo
   var userObj = user.getUser(profileId);
   if (userObj != null)
   {
      model.profile = userObj;
   }
   else
   {
      // fallback if unable to get user details
      // TODO: display error?
      model.profile = user;
   }
}
else
{
   // if no profile specified, must be current user which will allow editing
   model.profile = user;
}