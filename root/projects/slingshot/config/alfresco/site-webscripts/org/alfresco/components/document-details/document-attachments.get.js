function main()
{
   if (args.nodeRef != null)
   {
      var nodeRef = args.nodeRef;
      
      var result = remote.call("/api/attachments?nodeRef=" + nodeRef);
      var attachmentsAssocs = [];
      if (result.status == 200)
      {
         attachmentsAssocs = eval('(' + result + ')');
      }
      model.attachmentsAssocs = attachmentsAssocs;      
   }
}

main();
