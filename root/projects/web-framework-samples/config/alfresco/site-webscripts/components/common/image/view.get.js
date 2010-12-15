<import resource="/components/common/js/component.js">

var source = Surf.Component.getSource();
if(source == null)
{
	model.ready = false;
	
}
else
{
	// set up the source
	model.src = source.browserDownloadURI;

	model.title = instance.properties["title"];
	if(model.title == null)
	{
		model.title = "";
	}

	model.description = instance.properties["description"];
	if(model.description == null)
	{
		model.description = "";
	}

	var text = instance.properties["imageText"];
	if(text != null)
	{
		model.text = text;
	}

	model.ready = true;
}
