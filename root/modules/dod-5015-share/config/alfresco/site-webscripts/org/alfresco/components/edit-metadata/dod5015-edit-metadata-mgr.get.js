<import resource="/org/alfresco/components/edit-metadata/edit-metadata-mgr.get.js">

function alfresco_dod5015_main()
{
   // Call for meta data again (response is cached) so we can alter the nodeType
   var nodeRef = model.nodeRef,
      nodeType = model.nodeType;
      result = remote.connect("alfresco").get("/slingshot/edit-metadata/node/" + nodeRef.replace(":/", ""));

   if (result.status == 200)
   {
      // Determine the return page's nodeType and nodeRef depending on type being edited
      var metadata = eval('(' + result + ')');
      switch (String(metadata.node.type))
      {
         case "dod:recordSeries":
            nodeType = "record-series";
            break;

         case "dod:recordCategory":
            nodeType = "record-category";
            break;

         case "rma:recordFolder":
            nodeType = "record-folder";
            break;

         case "rma:dispositionSchedule":
            nodeType = "record-category";
            nodeRef = metadata.parent.nodeRef;
            break;
      }
   }
   model.nodeRef = nodeRef;
   model.nodeType = nodeType;
}

alfresco_dod5015_main();