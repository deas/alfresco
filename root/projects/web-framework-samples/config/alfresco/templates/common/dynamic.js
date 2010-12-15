var templateConfig = instance.properties["config"];
if(templateConfig != null)
{
	model.conf = templateConfig;
	
	var myJson = eval('(' + templateConfig + ')');
	model.templateConfig = myJson;
	//model.layoutType = myJson.type;

	model.layoutType = instance.properties["template-layout-type"];
	if(model.layoutType != null)
	{
		model.layoutType = model.layoutType.toLowerCase();
	}
	
	model.ready = true;
}	