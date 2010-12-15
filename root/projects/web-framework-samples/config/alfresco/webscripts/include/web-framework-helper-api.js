<import resource="/include/utils.js">

function save(modelObject)
{
	if(modelObject != null)
	{
		modelObject.save();
	}
};

function remove(modelObject)
{
	if(modelObject != null)
	{
		modelObject.remove();
	}
};

function removeObjects(array)
{
	if(array != null)
	{
		for(var i = 0; i < array.length; i++)
		{
			remove(array[i]);
		}
	}
};

function assertPage(pageId, pageName, pageDescription)
{
	var page = sitedata.getPage(pageId);
	if(page != null)
	{
		page.setTitle(pageName);
		page.setDescription(pageDescription);
		page.save();
	}
};


function addChildPage(parentPageId, pageName, pageDescription)
{
	var childPage = sitedata.newPage();

	childPage.setTitle(pageName);
	childPage.setDescription(pageDescription);
	childPage.save();
	
	// associate to parent with child relationship
	if(parentPageId != null)
	{
		sitedata.associatePage(parentPageId, childPage.getId());
	}
		
	return childPage;
};

function removeChildPage(parentPageId, childPageId, recurse)
{
	sitedata.unassociatePage(parentPageId, childPageId);
};

function newPage(name, parentPage)
{
	var page = sitedata.newPage();
	page.setTitle(name);
	page.save();	
	if(parentPage != null)
		sitedata.associatePage(parentPage.getId(), page.getId());		
	return page;
};

function newTemplate(name, templateTypeId)
{
	var template = null;
	var templateType = sitedata.getTemplateType(templateTypeId);
	if(templateType != null)
	{
		var template = sitedata.newTemplate();
		template.setTitle(name);
		template.setProperty("template-type", templateTypeId);		
		template.save();
	}
	return template;
};

function newFreemarkerTemplate(name, uri)
{
	var template = sitedata.newTemplate();
	template.setTitle(name);
	template.setProperty("template-type", "freemarker");
	template.setProperty("uri", uri);
	save(template);
	
	return template;
};

function associateTemplate(page, template, formatId)
{
	sitedata.associateTemplate(template.getId(), page.getId());
};

function associateContent(contentId, pageId, formatId)
{
	sitedata.associateContent(contentId, pageId, formatId);
};

function associateContentType(contentTypeId, pageId, formatId)
{
	sitedata.associateContentType(contentTypeId, pageId, formatId);
};

function newComponent(name, componentTypeId)
{
	var c = sitedata.newComponent();
	c.setTitle(name);
	c.setProperty("component-type-id", componentTypeId);
	c.save();
	return c;
};

function associateSiteComponent(component, regionId)
{
	associateGlobalComponent(component, regionId);
};

function associateGlobalComponent(component, regionId)
{
	sitedata.bindComponent(component.getId(), "global", regionId, "global");
};

function associateTemplateComponent(component, template, regionId)
{
	sitedata.bindComponent(component.getId(), "template", regionId, template.getId());
};

function associatePageComponent(component, page, regionId)
{	
	sitedata.bindComponent(component.getId(), "page", regionId, page.getId());
};

function setConfig(o, propertyName, propertyValue)
{
	o.setProperty(propertyName, propertyValue);
};

function newImageComponent(name, imageUrl)
{
	var c = newComponent(name, "ct-imageComponent");
	setConfig(c, "imageLocation", imageUrl);
	save(c);
	return c;
};

function newNavComponent(name, orientation, style)
{
	var c = newComponent(name, "ct-navComponent");
	if(orientation == null)
		orientation = "horizontal";
	setConfig(c, "orientation", orientation);
	if(style == null)
		style = "0";
	setConfig(c, "style", style);
	save(c);
	return c;
};

function newItemComponent(name, itemType, itemPath, howToRender, renderData, endpointId)
{
	var c = newComponent(name, "ct-itemComponent");
	
	if(itemType == null)
		itemType = "specific";
	if(itemPath == null)
		itemPath = "";
	if(howToRender == null)
		howToRender = "templateTitle";
	if(renderData == null)
		renderData = "article-list";
	if(endpointId == null)
		endpointId = "alfresco-webuser";
		
	c.setProperty("itemType", itemType);
	c.setProperty("itemPath", itemPath);
	c.setProperty("howToRender", howToRender);
	c.setProperty("renderData", renderData);
	c.setProperty("endpoint-id", endpointId);
	save(c);

	return c;
};

function newWebScriptComponent(name, uri)
{
	var c = newComponent(name, "webscript");		
	c.setProperty("uri", uri);
	save(c);

	return c;
};
