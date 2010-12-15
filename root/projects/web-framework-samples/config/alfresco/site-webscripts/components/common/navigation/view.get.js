// STYLE
var style = instance.object.properties["style"];
if(style == null || style == "")
{
	style = "tabbed";
}
model.style = style;


// ORIENTATION
var orientation = instance.properties["orientation"];
if(orientation == null || orientation == "")
{
	orientation = "horizontal";
}
model.orientation = orientation;


// BACKGROUND COLOR
var backgroundColor = instance.properties["backgroundColor"];
if(backgroundColor == null || backgroundColor == "")
{
	backgroundColor = "lightblue";
}
model.backgroundColor = backgroundColor;


// STARTING PAGE
var startingPage = instance.properties["startingPage"];
if(startingPage == null || startingPage == "")
{
	startingPage = "siteroot";
}
model.startingPage = startingPage;


// TOP PAGE
var topPage = instance.properties["topPage"];
if(topPage == null || topPage == "")
{
	topPage = "show";
}
model.topPage = topPage;


// CHILD SIBLINGS
var childSiblings = instance.properties["childSiblings"];
if(childSiblings == null || childSiblings == "")
{
	childSiblings = "showChildren";
}
model.childSiblings = childSiblings;


// set up rendering attributes
model.rootPage = sitedata.getRootPage();
model.linkbuilder = context.getLinkBuilder();
model.showTopPage = (model.topPage == "show");
model.showChildren = (model.childSiblings == "showChildren");
model.showSiblings = (model.childSiblings == "showSiblings");

// determine the current page id
model.currentPageId = "";
if(context.page != null)
{
	model.currentPageId = context.page.id;
}

// set up the base dir
model.baseDir = url.context + "/components/common/navigation/styles/" + model.style;

// mark as ready
model.ready = true;
