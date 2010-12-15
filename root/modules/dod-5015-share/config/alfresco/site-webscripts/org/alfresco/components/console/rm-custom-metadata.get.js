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
   
   // test user capabilities - can they access Custom Metadata?
   model.hasAccess = hasCapability(conn, "CreateModifyDestroyFileplanTypes");
}

main();