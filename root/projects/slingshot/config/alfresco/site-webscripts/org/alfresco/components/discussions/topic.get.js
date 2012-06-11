// Widget instantiation metadata...
model.webScriptWidgets = [];

var discussionsTopic = {};
discussionsTopic.name = "Alfresco.DiscussionsTopic";
discussionsTopic.provideOptions = true;
discussionsTopic.provideMessages = true;
discussionsTopic.options = {};
discussionsTopic.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
discussionsTopic.options.containerId = (page.url.args.containerId != null) ? page.url.args.containerId : "discussions";
discussionsTopic.options.topicId = (page.url.args.topicId != null) ? page.url.args.topicId : "";
model.webScriptWidgets.push(discussionsTopic);
