function main()
{
   if (args.nodeRef != null)
   {
      var attachmentsAssocs = [],
          result = remote.call("/api/attachments?nodeRef=" + args.nodeRef);
      if (result.status == 200)
      {
         attachmentsAssocs = eval('(' + result + ')');
      }
      model.attachmentsAssocs = attachmentsAssocs;      
   }
}

main();