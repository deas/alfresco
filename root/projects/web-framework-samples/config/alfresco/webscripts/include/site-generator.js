<import resource="/include/web-framework-helper-api.js">

// remove model objects from the current site
// this will not include template types
// this will not include component types
function removeSiteObjects()
{
	removeObjects(sitedata.getComponents());
	removeObjects(sitedata.getConfigurations());
	removeObjects(sitedata.getContentAssociations());
	removeObjects(sitedata.getPages());
	removeObjects(sitedata.getPageAssociations());
	removeObjects(sitedata.getTemplates());
	
	// do not remove template types
	// do not remove component types
	
	/*
	if(removeTemplateTypes)
	{
		removeObjects(sitedata.getTemplateTypes());
	}
	
	if(removeComponentTypes)
	{
		removeObjects(sitedata.getComponentTypes());
	}
	*/
}

function createSite(name, description)
{
	// create a new root page
	var rootPage = sitedata.newPage();
	rootPage.setTitle("Home");
	rootPage.setProperty("description", "Home Page for '" + name + "'");
	rootPage.save();
	
	// create a new site configuration
	var siteConfiguration = sitedata.newConfiguration("site");
	siteConfiguration.setTitle(name);
	siteConfiguration.setDescription(description);	
	siteConfiguration.setProperty("root-page", rootPage.id);
	siteConfiguration.setId("default.site.configuration");	
	siteConfiguration.save();
	
	return siteConfiguration;	
}

function generateSite(siteType)
{
	// no generation
	if("none" == siteType)	
	{
		return;
	}
	
	// basic public web site
	if("green" == siteType)
	{
		buildBasicPublicWebsite();
	}	
}

function buildBasicPublicWebsite()
{
	var rootPage = sitedata.getRootPage();	
	
	// create root navigation nodes
	var nd1 = newPage("Products", rootPage);
	var nd2 = newPage("Services", rootPage);
	var nd3 = newPage("Customers", rootPage);
	var nd4 = newPage("About Us", rootPage);
	var nd11 = newPage("Product A", nd1);
	var nd12 = newPage("Product B", nd1);

	// generate the templates
	var t1 = newTemplate("Home Template", "tt-basic-home-template");
	var t2 = newTemplate("Landing Template", "tt-basic-landing-template");
	var t3 = newTemplate("Content Template", "tt-basic-content-template");
	var t4 = newTemplate("Print Template", "tt-basic-print-template");
	
	// freemarker sample template
	var t5 = newFreemarkerTemplate("Freemarker Sample Template", "/web/sample/home");

	// associate templates
	associateTemplate(rootPage, t1, "default");
	associateTemplate(nd1, t2, "default");
	associateTemplate(nd2, t2, "default");
	associateTemplate(nd3, t5, "default");
	associateTemplate(nd4, t2, "default");
	

	// associate content templates
	var articleDefaultViewerPage = newPage("Article Default Viewer", null);
	associateContentType("article", articleDefaultViewerPage.getId(), "default");
	var articlePrintViewerPage = newPage("Article Print Viewer", null);
	associateContentType("article", articlePrintViewerPage.getId(), "print");
	
	// set up site scoped components
	var c1 = newImageComponent("Header", "/build/basic/images/header.jpg");
	var c4 = newImageComponent("Footer", "/build/basic/images/footer.jpg");
	associateSiteComponent(c1, "header");
	associateSiteComponent(c4, "footer");
	
	// set up the home page	
	var c2 = newNavComponent("Top Navigation");
	var c3 = newImageComponent("Home: Blurb", "/build/basic/images/blurb.jpg");
	var c4 = newImageComponent("Home: Our Services", "/build/basic/images/our_services.jpg");
	var c5 = newImageComponent("Home: New Arrivals", "/build/basic/images/new_arrivals.jpg");
	var c6 = newImageComponent("Home: Gifts and Gadgets", "/build/basic/images/gifts_and_gadgets.jpg");
	var c7 = newImageComponent("Home: Latest News", "/build/basic/images/latest_news.jpg");
	associateTemplateComponent(c2, t1, "topNav");
	associatePageComponent(c3, rootPage, "blurb");
	associatePageComponent(c4, rootPage, "leftContent1");
	associatePageComponent(c5, rootPage, "rightContent1");
	associatePageComponent(c6, rootPage, "leftContent2");
	associatePageComponent(c7, rootPage, "content");
	
	// set up the products page
	var c11 = newNavComponent("Horz Nav");
	var c12 = newNavComponent("Vert Nav", "vertical", "1");
	var c13 = newImageComponent("Popular Links", "/build/basic/images/popular_links.jpg");
	associateTemplateComponent(c11, t2, "topNav");
	associateTemplateComponent(c12, t2, "leftNav");
	associateTemplateComponent(c13, t2, "rightContent");
	
	// set up the about us page
	var c43 = newItemComponent("Article List", "specific", "/test/articles/sample1.xml", "templateTitle", "article-list", "alfresco-webuser");
	associatePageComponent(c43, nd4, "content");
	
	// set up the customers page (freemarker and web script samples)
	associateTemplateComponent(c11, t5, "topNav");
	var c31 = newWebScriptComponent("WebScript Sample 1", "/web/components/sample1");
	var c32 = newWebScriptComponent("WebScript Sample 2", "/web/components/sample2");
	associatePageComponent(c31, nd3, "content");
	associateTemplateComponent(c32, t5, "leftNav");			
	
	// set up the content template (for item views)
	var c91 = newItemComponent("Item Viewer", "current", null, "templateTitle", "article-full", "alfresco-webuser");
	associateTemplateComponent(c91, t3, "contentitem");
	associateTemplateComponent(c11, t3, "topNav");
	associateTemplateComponent(c12, t3, "leftNav");	
	
	// set up the print template (for item views)
	associateTemplateComponent(c91, t4, "contentitem");	
}

