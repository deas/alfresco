function main()
{
   // Need to know what type of node this is - document or folder
   var nodeRef = page.url.args.nodeRef,
      nodeType = "document",
      connector = remote.connect("alfresco"),
      result = connector.get("/slingshot/edit-metadata/node/" + nodeRef.replace(":/", ""));

   if (result.status == 200)
   {
      // Determine the return page's nodeType and nodeRef depending on type being edited
      var metadata = eval('(' + result + ')');
      nodeType = metadata.node.isContainer ? "folder" : "document";
      nodeRef = metadata.node.nodeRef;

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

main();