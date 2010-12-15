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
	model.isAudio = false;
	if(model.mimetype != null && model.mimetype.length > 5)
	{
		model.isAudio = (model.mimetype.substring(0,5) == "audio");
	}

	// players
	model.useQuicktime = false;
	model.useWindowsMedia = false;
	model.useShockwave = false;
	model.useReal = false;

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
	
	model.ready = true;
}