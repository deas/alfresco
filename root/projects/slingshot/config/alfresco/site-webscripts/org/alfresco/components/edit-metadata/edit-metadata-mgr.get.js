function main()
{
   // Need to know what type of node this is - document or folder
   var nodeRef = page.url.args.nodeRef,
      nodeType = "document",
      connector = remote.connect("alfresco"),
      result = connector.get("/slingshot/edit-metadata/node/" + nodeRef.replace(":/", ""));

   if (result.status == 200)
   {
      var metadata = eval('(' + result + ')');
      nodeType = metadata.node.isContainer ? "folder" : "document";
   }
   model.nodeRef = nodeRef;
   model.nodeType = nodeType;
}

main();