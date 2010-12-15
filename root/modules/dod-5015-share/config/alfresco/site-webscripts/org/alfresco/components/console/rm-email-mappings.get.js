<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/console/rm-console.lib.js">

function main()
{
   var conn = remote.connect("alfresco");
   
   // test user capabilities - can they access Email Mappings?
   var hasAccess = hasCapability(conn, "MapEmailMetadata");
   if (hasAccess)
   {
      // get custom properties for Record type
      var meta = [];
      var res = conn.get("/api/rma/admin/custompropertydefinitions?element=record");
      if (res.status == 200)
      {
         var props = eval('(' + res + ')').data.customProperties;
         for (var id in props)
         {
            var prop = props[id];
            meta.push(
            {
               name: id,
               title: prop.label
            });
         }
      }
      model.meta = meta;
   }
   model.hasAccess = hasAccess;
}

main();