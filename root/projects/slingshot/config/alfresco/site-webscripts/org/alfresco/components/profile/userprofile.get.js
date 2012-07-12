<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/upload/uploadable.lib.js">

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

//Widget instantiation metadata...
model.widgets = [];
var userProfile = {};
userProfile.name = "Alfresco.UserProfile";
userProfile.useMessages = true;
userProfile.useOptions = true;
userProfile.options = {};
userProfile.options.userId = user.name;
userProfile.options.profile = {};
userProfile.options.profile.isEditable = model.isEditable;
userProfile.options.profile.name = (model.profile.name != null) ? model.profile.name : "";
userProfile.options.profile.lastName = (model.profile.lastName != null) ? model.profile.lastName : "";
userProfile.options.profile.firstName = (model.profile.firstName != null) ? model.profile.firstName : "";
userProfile.options.profile.jobtitle = (model.profile.jobTitle != null) ? model.profile.jobTitle : "";
userProfile.options.profile.location = (model.profile.location != null) ? model.profile.location : "";
userProfile.options.profile.bio = (model.profile.biography != null) ? model.profile.biography : "";
userProfile.options.profile.telephone = (model.profile.telephone != null) ? model.profile.telephone : "";
userProfile.options.profile.mobile = (model.profile.mobilePhone != null) ? model.profile.mobilePhone : "";
userProfile.options.profile.email = (model.profile.email != null) ? model.profile.email : "";
userProfile.options.profile.skype = (model.profile.skype != null) ? model.profile.skype : "";
userProfile.options.profile.instantmsg = (model.profile.instantMsg != null) ? model.profile.instantMsg : "";
userProfile.options.profile.googleusername = (model.profile.googleUsername != null) ? model.profile.googleUsername : "";
userProfile.options.profile.organization = (model.profile.organization != null) ? model.profile.organization : "";
userProfile.options.profile.companyaddress1 = (model.profile.companyAddress1 != null) ? model.profile.companyAddress1 : "";
userProfile.options.profile.companyaddress2 = (model.profile.companyAddress2 != null) ? model.profile.companyAddress2 : "";
userProfile.options.profile.companyaddress3 = (model.profile.companyAddress3 != null) ? model.profile.companyAddress3 : "";
userProfile.options.profile.companypostcode = (model.profile.companyPostcode != null) ? model.profile.companyPostcode : "";
userProfile.options.profile.companytelephone = (model.profile.companyTelephone != null) ? model.profile.companyTelephone : "";
userProfile.options.profile.companyfax = (model.profile.companyFax != null) ? model.profile.companyFax : "";
userProfile.options.profile.companyemail = (model.profile.companyEmail != null) ? model.profile.companyEmail : "";
model.widgets.push(userProfile);