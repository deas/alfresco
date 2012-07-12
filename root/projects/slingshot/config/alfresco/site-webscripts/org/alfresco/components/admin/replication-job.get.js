function main()
{
   // Model variables
   var jobDetail = {};
   
   var jobName = page.url.args.jobName;
   if (jobName != null)
   {
      var response = remote.call("/api/replication-definition/" + encodeURIComponent(jobName));
      if (response.status == 200)
      {
         var json = eval('(' + response + ')');
         jobDetail = json.data;
      }
   }
   
   model.jobDetail = jobDetail;
   
   // Define widget model...
   model.widgets = [];
   
   var payload = [];
   if (jobDetail != null)
   {
      for (var p in jobDetail)
      {
         payload.push(p.nodeRef);
      }
   }
   
   var replicationJob = {
      name: "Alfresco.component.ReplicationJob",
      options : {
         jobName : jobName,
         payload : payload,
         targetName : jobDetail.targetName,
         scheduleStart :jobDetail.schedule.start.iso8601
      },
   };
   
   model.widgets.push(replicationJob);
}

main();