
function main()
{
   var site, container, theUrl, connector, result, data;
   
   // gather all required data
   site = args["site"];
   container = "links";
   
   theUrl = '/api/links/site/' + site + '/' + container + "?page=1&pageSize=512";
   
   connector = remote.connect("alfresco");
   result = connector.get(theUrl);
   if (result.status != status.STATUS_OK)
   {
      status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, "Unable to do backend call. " +
                     "status: " + result.status + ", response: " + result.response);
      return null;
   }

   data = eval('(' + result.response + ')');
   model.items = data.items;

   // set additional properties
   model.lang = "en-us";
   model.site = site;
   model.container = container;
}

main();
