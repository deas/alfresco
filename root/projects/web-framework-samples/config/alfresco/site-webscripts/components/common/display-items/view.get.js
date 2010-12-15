<import resource="/components/common/js/mimetypes.js">
<import resource="/components/common/js/component.js">

var sourceContent = Surf.Component.getSourceContent();
if(sourceContent == null)
{
	model.ready = false;
	
}
else
{
	// load the JSON object
	model.container = Surf.Component.getSourceContentData();

	// property: view
	var view = instance.object.properties["view"];
	if(view == null)
	{
		view = "views/list";
	}
	model.view = view;

	// property: icon size
	var iconSize = instance.object.properties["iconSize"];
	if(iconSize == null)
	{
		iconSize = "72";
	}

	// set up mimetype icons
	var mimetypes = new Surf.Mimetypes();
	for(var i = 0; i < model.container.children.length; i++)
	{
		var child = model.container.children[i];

		var mimetype = child.mimetype;
		var filename = child.title;
		
		var iconUrl = mimetypes.getIcon(filename, mimetype, iconSize);
		child.iconUrl = iconUrl;
	}

	// set up link urls
	for(var i = 0; i < model.container.children.length; i++)
	{
		var child = model.container.children[i];
		child.linkUrl = context.linkBuilder.object(child.id);
	}
	
	model.ready = true;
}