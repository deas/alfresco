<import resource="classpath:alfresco/site-webscripts/org/alfresco/utils.js">

function inviteUser(site)
{
   var userObj;
   //get user object based on user name, if specified
   if (page.url.args.name)
   {
      userObj = user.getUser(page.url.args['name']);      
   }
   // otherwise use details for external user
   else 
   {
      userObj = {
         firstName : page.url.args.firstName,
         lastName : page.url.args.lastName,
         email : page.url.args.email
      };
   }

   var params = '{'+
         '"acceptURL":"page/accept-invite",'+
         '"invitationType":"NOMINATED",'+
         '"inviteeEmail":"'+userObj.email+'",'+
         '"inviteeFirstName":"'+userObj.firstName+'",'+
         '"inviteeLastName":"'+userObj.lastName+'",'+
         '"inviteeRoleName":"'+page.url.args.role+'",'+
         '"rejectURL":"page/reject-invite",'+
         '"serverPath":"http://localhost:8081/mobile/"'
   if (userObj.id)
   {
      params+=',"inviteeUserName":"'+userObj.id.toString()+'"';
   }
   params+='}';

   model.params = params;
   var connector = remote.connect("alfresco");
   var result = connector.post("/api/sites/"+stringUtils.urlEncode(site)+"/invitations",params, "application/json");

   return JSON.parse(result);   
}


var overdueTasks = getUserTasks('overdue').tasks;
var todaysTasks = getUserTasks('today').tasks;
var userEvents = getUserEvents().events;
var inviteResult = inviteUser(page.url.args.site);

model.inviteUserResult = (inviteResult.data && inviteResult.data.inviteId) ? 'true' : 'false';

model.numOverdueTasks = overdueTasks.length;
model.numTodaysTasks = todaysTasks.length;
model.numEvents = userEvents.length;
model.backButton = true;


