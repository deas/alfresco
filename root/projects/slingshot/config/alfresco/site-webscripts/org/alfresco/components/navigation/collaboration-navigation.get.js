/**
 * Collaboration Site Navigation component GET method
 */

function main()
{
   // Get ids for all used pages
   var siteId = page.url.templateArgs.site,
      siteProperties = null,
      pageMetadata = {},
      usedPages = [];
   
   var p = sitedata.getPage("site/" + siteId + "/dashboard");
   if (p !== null)
   {
      // Don't try to simplify this - it won't work.
      usedPages = eval('(' + p.properties.sitePages + ')');
      usedPages = usedPages != null ? usedPages : [];
      pageMetadata = eval('(' + p.properties.pageMetadata + ')');
      pageMetadata = pageMetadata != null ? pageMetadata : {};
      
      var availablePages = config.scoped["SitePages"]["pages"].childrenMap["page"], urlMap = {};
      for (var i = 0; i < availablePages.size(); i++)
      {
         // Get page id from config file
         pageId = availablePages.get(i).attributes["id"];
         if (pageId)
         {
            pageUrl = availablePages.get(i).value;
            urlMap[pageId] = pageUrl;
         }
      }
      
      // Find the label for each page
      for (var i = 0; i < usedPages.length; i++)
      {
         var usedPage = usedPages[i],
            pageId = usedPage.pageId,
            p = sitedata.getPage(pageId),
            pageUrl = urlMap[pageId],
            pageMeta = pageMetadata[pageId] || {};
      
         if (p != null)
         {
            usedPage.title = p.title;
            usedPage.titleId = pageMeta.titleId != null ? pageMeta.titleId : p.titleId;
            if (pageUrl)
            {
               // Overwrite the stored pageUrl with the latest one from config file
               usedPage.pageUrl = pageUrl;  
            }
         }
         else
         {
            // page does not exist! output error to help the developer
            usedPage.title = "ERROR: page " + usedPage.pageId + " not found!";
         }
      }
      
      model.siteExists = true;
   }
   
   // Prepare template model
   model.pages = usedPages;
}

main();