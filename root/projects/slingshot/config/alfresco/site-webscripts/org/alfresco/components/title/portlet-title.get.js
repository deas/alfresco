// Call the repository for the site profile
var json = remote.call("/api/sites/" + page.url.templateArgs.site);

var profile =
{
   title: "",
   shortName: "",
   visibility: "PUBLIC"
};

if (json.status == 200)
{
   // Create javascript objects from the repo response
   var obj = eval('(' + json + ')');
   if (obj)
   {
      profile = obj;
   }
}

// Prepare the model
model.profile = profile;