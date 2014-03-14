<import resource="/include/support.js">

if (typeof Surf == "undefined")
{
	var Surf = {};
}

Surf.Component = function()
{
}

/**
 *  Returns the "source" resource for a component
 */
Surf.Component.getSource = function(modelObject)
{
	if(modelObject == null)
	{
		modelObject = instance.object;
	}

	var source = modelObject.resources.get("source");
	
	if(source != null && source.type == "current")
	{
		source = context.content.resource;
	}
	
	return source;
}

/**
 *  Returns the "source" content for a component
 *  This is a wrapper around the "source" resource object
 */
Surf.Component.getSourceContent = function(modelObject)
{
	if(modelObject == null)
	{
		modelObject = instance.object;
	}
	
	var content = null;

	var source = modelObject.resources.get("source");
	
	if(source != null)
	{
		if(source.type == "current")
		{
			content = context.content;
		}
		else
		{
			content = source.content;
		}
	}
	
	return content;
}

/**
 *  Returns the content data as JSON
 */
Surf.Component.getSourceContentData = function(modelObject)
{
	if(modelObject == null)
	{
		modelObject = instance.object;
	}
	
	var obj = null;

	var content = this.getSourceContent(modelObject);
	if(content != null)
	{
		var metadata = content.json;
		obj = JSON.parse(metadata);
	}
	
	return obj;
}


Surf.Component.bind = function(modelObject)
{
	if(modelObject == null)
	{
		modelObject = instance.object;	
	}

	var properties = modelObject.properties;
	var source = modelObject.resources.get("source");

	// source
	model["sourceType"] = form.bind("sourceType", (source != null ? source.type : null), "");
	model["sourceValue"] = form.bind("sourceValue", (source != null ? source.value : null), "");
	model["sourceEndpoint"] = form.bind("sourceEndpoint", (source != null ? source.endpoint : null), "");
	
	// properties
	model.title = form.bind("title", properties["title"], "");
	model.description = form.bind("description", properties["description"], "");
}

Surf.Component.bindProperty = function(propertyName, modelObject)
{
	if(modelObject == null)
	{
		modelObject = instance.object;	
	}

	model[propertyName] = form.bind(propertyName, modelObject.properties[propertyName], "");
}

Surf.Component.persist = function(modelObject)
{
	if(modelObject == null)
	{
		modelObject = instance.object;	
	}

	// source resource
	var source = modelObject.resources.get("source");
	if(source == null)
	{
		source = modelObject.resources.add("source");
	}

	// set properties
	for each(field in formdata.fields)
	{
		if(field.id != null)
		{	
			if ((field.id.length() >= 6) && (field.id.substring(0,6) == "source"))
			{
				var attributeName = field.id.substring(6).toLowerCase();
				if("type" == attributeName)
				{
					source.type = field.value;
				}
				else if("value" == attributeName)
				{
					source.value = field.value;
				}
				else if("endpoint" == attributeName)
				{
					source.endpoint = field.value;
				}
			}
			else
			{
				modelObject.setProperty(field.id, field.value);
			}
		}
	}

	modelObject.save();
}

