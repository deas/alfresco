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

	// mimetype
	model.mimetype = instance.object.properties["mimetype"];
	if(model.mimetype != null && model.mimetype.length > 5)
	{
		model.isVideo = (model.mimetype.substring(0,5) == "video");
	}
	
	if(!model.isVideo)
	{
		model.msg = "The source file configured for this video component is not a video file.";
	}
	else
	{
		model.useQuicktime = false;
		model.useWindowsMedia = false;
		model.useReal = false;
		model.useShockwave = false;

		// which player to use
		model.player = instance.object.properties["player"];
		if(model.player == null || model.player == "quicktime")
		{
			model.useQuicktime = true;
		}
		if(model.player == "windowsmedia")
		{
			model.useWindowsMedia = true;
		}
		if(model.player == "shockwave")
		{
			model.useShockwave = true;
		}
		if(model.player == "real")
		{
			model.useReal = true;
		}
	}
	
	model.title = "Video";
	var x1 = model.src.lastIndexOf("/");
	if(x1 > -1)
	{
		model.title = model.src.substring(x1+1);
	}
	
	model.ready = true;
}

