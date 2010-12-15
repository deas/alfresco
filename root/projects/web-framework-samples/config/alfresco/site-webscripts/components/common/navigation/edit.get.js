<import resource="/components/common/js/component.js">

// bind core properties and source
Surf.Component.bind();

// bind custom properties
Surf.Component.bindProperty("style");
Surf.Component.bindProperty("orientation");

Surf.Component.bindProperty("startingPage");
Surf.Component.bindProperty("topPage");
Surf.Component.bindProperty("childSiblings");


// default values
if(model.style.value == null || model.style.value == "")
{
	model.style.value = "tabbed";
}
if(model.orientation.value == null || model.orientation.value == "")
{
	model.orientation.value = "horizontal";
}
if(model.startingPage.value == null || model.startingPage.value == "")
{
	model.startingPage.value = "siteroot";
}
if(model.topPage.value == null || model.topPage.value == "")
{
	model.topPage.value = "show";
}
if(model.childSiblings.value == null || model.childSiblings.value == "")
{
	model.childSiblings.value = "showChildren";
}

