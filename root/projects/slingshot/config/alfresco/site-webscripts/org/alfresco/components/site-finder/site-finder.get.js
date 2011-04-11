var url = "/api/invitations?inviteeUserName=" + encodeURIComponent(user.name),
   result = remote.connect("alfresco").get(url),
   inviteData = [];

if (result.status == status.STATUS_OK)
{
   var json = eval('(' + result.response + ')');
   inviteData = json.data;
}

model.inviteData = inviteData;