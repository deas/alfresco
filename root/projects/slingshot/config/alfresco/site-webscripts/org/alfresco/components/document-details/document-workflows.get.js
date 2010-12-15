function main()
{
   if (args.nodeRef != null)
   {
      var nodeRef = args.nodeRef;
      
      // Call the repo to get the workflows this node is part of
      var response = remote.call("/api/node/" + nodeRef.replace(":/", "") + "/workflow-instances");
      
      // Create javascript objects from the server response
      var workflows = [];
      
      if (response.status == 200)
      {
         var result = eval('(' + response + ')');
         
         workflows = result.data;
      }
      
      // Prepare the model for the template
      model.nodeRef = nodeRef;
      model.workflows = workflows;
   }
}

main();
