// Widget instantiation metadata...
model.widgets = [];

var discussionsTopicList = {};
discussionsTopicList.name = "Alfresco.DiscussionsTopicList";
discussionsTopicList.useOptions = true;
discussionsTopicList.useMessages = true;
discussionsTopicList.options = {};
discussionsTopicList.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
discussionsTopicList.options.containerId = (page.url.args.containerId != null) ? page.url.args.containerId : "discussions";

var initialFilter = 
{
   filterId: (page.url.args.filterId != null) ? page.url.args.filterId : "new",
   filterOwner: (page.url.args.filterOwner != null) ? page.url.args.filterOwner : "Alfresco.TopicListFilter",
   filterData: (page.url.args.filterData != null) ? page.url.args.filterData : null
}
discussionsTopicList.options.initialFilter = initialFilter;
model.widgets.push(discussionsTopicList);
