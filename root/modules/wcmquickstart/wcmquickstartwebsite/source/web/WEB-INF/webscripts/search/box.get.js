model.searchroot = context.properties.rootnav;
if (model.searchroot == null)
{
	model.searchroot = webSite.rootSection;
}
if (url.args['phrase'] != null) 
{
	model.phrase = url.args['phrase'];
}
else
{
	model.phrase = null;
}