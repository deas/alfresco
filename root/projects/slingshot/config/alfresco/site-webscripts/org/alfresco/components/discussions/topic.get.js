function main()
{
   // Widget instantiation metadata...
   model.widgets = [];
   
   var discussionsTopic = {
      name : "Alfresco.DiscussionsTopic",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId : (page.url.args.containerId != null) ? page.url.args.containerId : "discussions",
         topicId : (page.url.args.topicId != null) ? page.url.args.topicId : ""
      }
   };
   model.widgets.push(discussionsTopic);
}

main();
