function main()
{
   // Widget instantiation metadata...
   var createTopic = {
      id : "TopicReplies", 
      name : "Alfresco.TopicReplies",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId : (page.url.args.containerId != null) ? page.url.args.containerId : "discussions",
         editorConfig : {
            width: "85%",
            height: 300,
            language: this.locale.substring(0, 2)
         }
      }
   };
   
   model.widgets = [createTopic];
}

main();
