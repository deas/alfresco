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
}

main();