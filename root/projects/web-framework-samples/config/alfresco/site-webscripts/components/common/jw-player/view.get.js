<import resource="/components/common/js/component.js">

var source = Surf.Component.getSource();
if(source == null || source.value == null || source.value == "")
{
	// just use JW's default video
	model.src = url.context + "/components/common/jw-player/video.flv";
}
else
{
	// set up the source
	model.src = source.browserDownloadURI;
	
	var fileext = instance.object.properties["fileext"];
	if(fileext == null || fileext == "")
	{
		fileext = "flv";
	}
	model.fileext = fileext;
	
	model.src = model.src + "?filename=content." + fileext;

}

var previewImageUrl = instance.object.properties["previewImageUrl"];
if(previewImageUrl == null || previewImageUrl == "")
{
	previewImageUrl = url.context + "/components/common/jw-player/preview.jpg";
}
model.previewImageUrl = previewImageUrl;
	
model.ready = true;

