var userId = page.url.templateArgs["userid"];
if (userId == null)
{
   userId = user.name;
}

var result = remote.call("/api/subscriptions/" + encodeURIComponent(userId) + "/following");
model.numPeople = 0;
if (result.status == 200)
{
   model.data = eval('(' + result + ')');
   model.numPeople = model.data.people.length;
}
