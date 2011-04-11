<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/console/rm-console.lib.js">

function main()
{
   var meta = [];
   
   var conn = remote.connect("alfresco");
   
   // retrieve user capabilities - can they access Audit?
   var hasAccess = hasCapability(conn, "AccessAudit");
   if (hasAccess)
   {
      // retrieve the RM custom properties - for display as meta-data fields etc.
      var elements = ["record", "recordFolder", "recordCategory", "recordSeries"];
      for each (var el in elements)
      {
         retrieveMetadataForElement(conn, meta, el);
      }
      model.meta = meta;
      model.events = retrieveAuditEvents(conn);
      model.eventsStr = model.events.toSource();
      model.enabled = getAuditStatus(conn);
   }
   model.hasAccess = hasAccess;
}

function retrieveMetadataForElement(conn, meta, el)
{
   var res = conn.get("/api/rma/admin/custompropertydefinitions?element=" + el);
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
}

function retrieveAuditEvents(conn)
{
   var res = conn.get("/api/rma/admin/listofvalues");
   if (res.status == 200)
   {
      return eval('(' + res + ')').data.auditEvents.items;
   }
   else
   {
      return [];
   }
}

function getAuditStatus(conn)
{
	var res = conn.get("/api/rma/admin/rmauditlog/status")
	if (res.status == 200)
    {
       return eval('(' + res + ')').data.enabled;
    }
    else
    {
      return true;
    }
}

main();