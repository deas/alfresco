// Call the repo for the sites profile
var profile;

var json = remote.call("/api/sites/" + args.shortName);
if (json.status == 200)
{
   // Create javascript object from the repo response
   var obj = eval('(' + json + ')');
   if (obj)
   {
      profile = obj;
   }
}

// Prepare the model
model.profile = profile;
