// Repository Library root node
var rootNode = "alfresco://company/home",
   repoConfig = config.scoped["RepositoryLibrary"]["root-node"];

if (repoConfig !== null)
{
   rootNode = repoConfig.value;
}

model.rootNode = rootNode;

// Widget instantiation metadata...
var filters = config.scoped['DocumentLibrary']['filters'],
    maxTagCount = filters.getChildValue('maximum-tag-count');

if (maxTagCount == null)
{
   maxTagCount = "100";
}

model.webScriptWidgets = [];
var tagFilter = {};
tagFilter.name = "Alfresco.TagFilter";
tagFilter.assignToVariable = "tagFilter";
tagFilter.provideMessages = true;
tagFilter.provideOptions = true;
tagFilter.options = {};
tagFilter.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
tagFilter.options.containerId = (template.properties.container != null) ? template.properties.container : "";
tagFilter.options.rootNode = model.rootNode;
tagFilter.options.numTags = maxTagCount;
model.webScriptWidgets.push(tagFilter);
