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
   model.webScriptWidgets = [];
   var replicationJob = {};
   replicationJob.name = "Alfresco.component.ReplicationJob";
   replicationJob.provideMessages = true;
   replicationJob.provideOptions = true;
   replicationJob.options = {};
   replicationJob.options.jobName = jobName;
   var payload = [];
   if (jobDetail != null)
   {
      for (var p in jobDetail)
      {
         payload.push(p.nodeRef);
      }
   }
   replicationJob.options.payload = payload;
   replicationJob.options.targetName = jobDetail.targetName;
   replicationJob.options.scheduleStart = jobDetail.schedule.start.iso8601;
   model.webScriptWidgets.push(replicationJob);
}

main();