<import resource="/components/common/js/component.js">

function startsWith(str, car)
{
	return (str.indexOf(car) == 0);
}

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

	model.container = instance.properties["container"];
	if(model.container == null)
	{
		model.container = "div";
	}
	
	// load the content (if container is a div)
	if (model.container == "div")
	{
		model.src = source.downloadURI;

		// if the src starts with "/", then it is a server side include
		if (startsWith(model.src, "/"))
		{			
			// TODO: this should use the source.endpoint
			// TODO: app.include(source.value, source.endpoint);
			// TODO: leave out for now since Web Studio doesn't yet make this easy
			var data = app.include(source.value);
			model.data = data.toString() + "";
		}
		else
		{
			var conn = remote.connect("http");
			var data = conn.get(model.src);
			model.data = data.toString() + "";
		}
	}

	model.ready = true;
}
