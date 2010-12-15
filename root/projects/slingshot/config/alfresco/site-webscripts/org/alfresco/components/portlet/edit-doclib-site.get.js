function getSites()
{
   var sites = [],
      json = remote.call("/api/sites");

   if (json.status == 200)
   {
      var obj = eval('(' + json + ')');
      if (obj)
      {
         sites = obj;
      }
   }
   return sites;
}

var portlet = context.attributes.portletHost || false,
   mode = context.attributes.mode || "view";

if (portlet && mode == "edit")
{
   model.sites = getSites();
}