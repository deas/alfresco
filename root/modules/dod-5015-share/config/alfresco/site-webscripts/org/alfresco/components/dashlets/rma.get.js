function main()
{
   // Check for RMA site existence
   var conn = remote.connect("alfresco");
   var res = conn.get("/api/sites/rm");
   if (res.status == 404)
   {
      // site does not exist yet
      model.foundsite = false;
   }
   else if (res.status == 200)
   {
      // site already exists
      model.foundsite = true;
   }
}

main();