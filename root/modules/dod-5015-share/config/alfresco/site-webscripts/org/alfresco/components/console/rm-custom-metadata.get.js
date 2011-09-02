<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/console/rm-console.lib.js">

/**
 * RM Custom Metadata WebScript component
 */
function main()
{
   var constraints = [];
   
   var conn = remote.connect("alfresco");
   
   // retrieve the RM constraints - an array is returned
   var res = conn.get("/api/rma/admin/rmconstraints");
   if (res.status == 200)
   {
      constraints = eval('(' + res + ')').data;
   }
   model.constraints = constraints;
   
   
   // retrieve the customisable aspects and types
   var customisable = [];
   var res2 = conn.get("/api/rma/admin/customisable");
   if (res2.status == 200)
   {
	   customisable = eval('(' + res2 + ')').data;
   }
   model.customisable = customisable;
   
   // test user capabilities - can they access Custom Metadata?
   model.hasAccess = hasCapability(conn, "CreateModifyDestroyFileplanTypes");
}

main();