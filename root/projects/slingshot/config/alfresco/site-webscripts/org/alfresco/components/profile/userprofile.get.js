<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

/**
 * User Profile Component GET method
 */

function main()
{
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
   
   // convert biography text to use <br/> line breaks
   var bio = model.profile.biography;
   if (bio != null)
   {
      model.biohtml = stringUtils.replaceLineBreaks(bio);
   }
   
   // if there's a usrStatusTime, then convert to a relative time
   if (model.profile.properties.userStatusTime != null)
   {
      model.userStatusRelativeTime = AlfrescoUtil.relativeTime(model.profile.properties.userStatusTime);
   }
   
   // editable if request profile is for the current user
   model.isEditable = (profileId == null || profileId == user.name);
}

main();