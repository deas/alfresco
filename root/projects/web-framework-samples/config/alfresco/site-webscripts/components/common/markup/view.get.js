var html = instance.properties["html"];
if(html == null)
{
	model.ready = false;
}
else
{
	html = html.replace('${app.context}', app.context);
	model.html = html;
	
	model.ready = true;
}
