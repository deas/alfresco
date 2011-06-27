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
         model.profile = user.getUser(user.id);
      }
   }
   else
   {
      // if no profile specified, must be current user which will allow editing
      model.profile = user.getUser(user.id);
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
   
   // add follow/unfollow buttons if request profile is not for the current user
   if (!model.isEditable)
   {
      var params = new Array(1);
      params.push(page.url.templateArgs["userid"]);
      
      var connector = remote.connect("alfresco");
      var result = connector.post("/api/subscriptions/" + encodeURIComponent(user.name) + "/follows",
                                  jsonUtils.toJSONString(params),
                                  "application/json");
      if (result.status == 200)
      {
         model.follows = eval('(' + result + ')')[0][page.url.templateArgs["userid"]];
      }
   }
}

main();