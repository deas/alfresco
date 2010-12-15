
// Get ids for all used pages
var siteId = page.url.templateArgs.site;
var p = sitedata.getPage("site/" + siteId + "/dashboard");

// Don't try to simplify this - it won't work.
var usedPages = eval('(' + p.properties.sitePages + ')');
usedPages = usedPages != null ? usedPages : [];
pageMetadata = eval('(' + p.properties.pageMetadata + ')');
pageMetadata = pageMetadata != null ? pageMetadata : {};

// Get ids for all pages that have been configured to be addable for sites
var availablePages = config.scoped["SitePages"]["pages"].childrenMap["page"];
if (availablePages == null)
{
   availablePages = [];
}

// The pages
var pages = [];

// Start by adding the current pages in the order they were added
for (var i = 0; i < usedPages.length; i++)
{
   var pageId = usedPages[i].pageId,
      usedPage = usedPages[i],
      pageMeta = pageMetadata[pageId] || {};
   
   // Look up real page object from framework
   var p = sitedata.getPage(pageId);
   if (p)
   {
      // Resolve I18N strings for title/description
      var titleId = pageMeta.titleId != null ? pageMeta.titleId : p.titleId,
         descriptionId = pageMeta.descriptionId != null ? pageMeta.descriptionId : p.descriptionId;
      
      var title = titleId != null ? msg.get(titleId) : p.title;
      var description = descriptionId != null ? msg.get(descriptionId) : p.description;
      
      // Create a page object
      pages[pages.length] =
      {
         pageId: pageId,
         title: title,
         description: description,
         used: true
      };
   }
}

// add the pages that aren't used in the end of the list
for (i = 0; i < availablePages.size(); i++)
{
   // Get page id from config file
   var pageId = availablePages.get(i).attributes["id"];
   var used = false;
   for (var j = 0; j < usedPages.length; j++)
   {
      if (usedPages[j].pageId == pageId)
      {
         used = true;
         break;
      }
   }
   if (!used)
   {
      // Look up real page object from framework
      var p = sitedata.getPage(pageId);
      if (p)
      {
         // Resolve I18N strings for title/description
         var title = (p.titleId != null ? msg.get(p.titleId) : p.title);
         var description = (p.descriptionId != null ? msg.get(p.descriptionId) : p.description);
         
         // Create a page object
         pages[pages.length] =
         {
            pageId: pageId,
            title: title,
            description: description,
            used: false
         };
      }
   }
}

// Sort the order of the pages and find out if this page is used or not

// Prepare model for template
model.siteId = siteId;
model.pages = pages;