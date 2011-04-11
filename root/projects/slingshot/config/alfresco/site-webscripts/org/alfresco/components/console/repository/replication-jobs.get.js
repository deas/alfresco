/**
 * Admin Console Replication Jobs Tool component
 */

function main()
{
   // Model variables
   var targetGroupPath = "/Data Dictionary/Transfers/Transfer Target Groups/Default Group";

   var targetGroupXPath = "/app:company_home/app:dictionary/app:transfers/app:transfer_groups/cm:default",
      response = remote.call("/api/forms/picker/node/children?xpath=" + encodeURIComponent(targetGroupXPath));

   if (response.status == 200)
   {
      var json = eval('(' + response + ')'),
         parent = json.data.parent;

      targetGroupPath = "/" + parent.displayPath.split("/").slice(2).join("/") + "/" + parent.name;
   }
   
   model.targetGroupPath = targetGroupPath;
}

main();