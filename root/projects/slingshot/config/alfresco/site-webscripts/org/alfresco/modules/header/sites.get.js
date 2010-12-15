/**
 * Sites module GET method
 */

const PREF_FAVOURITE_SITES = "org.alfresco.share.sites.favourites";

function sortByTitle(site1, site2)
{
   return (site1.title > site2.title) ? 1 : (site1.title < site2.title) ? -1 : 0;
}

function main()
{
   // Model variables - must have defaults
   var favouriteSites = [],
      currentSiteIsFav = false,
      siteTitle = "";

   var prefs,
      favourites;

   // Call the repo for the user's favourite sites
   // TODO: Clean-up old favourites here?
   var result = remote.call("/api/people/" + encodeURIComponent(user.name) + "/preferences?pf=" + PREF_FAVOURITE_SITES);
   if (result.status == 200 && result != "{}")
   {
      prefs = eval('(' + result + ')');
      
      // Populate the favourites object literal for easy look-up later
      favourites = eval('try{(prefs.' + PREF_FAVOURITE_SITES + ')}catch(e){}');
   }

   if (typeof favourites != "object")
   {
      favourites = {};
   }

   // Call the repo to return a specific list of site metadata i.e. those in the fav list
   // and ensure the current user is a member of each before adding to fav list
   var query =
   {
      shortName:
      {
         match: "exact",
         values: []
      }
   };
   var currentSite = args.siteId || "",
      ignoreCurrentSite = false,
      shortName;
   
   for (shortName in favourites)
   {
      if (favourites[shortName])
      {
         query.shortName.values.push(shortName);
      }
   }
   
   // Also tack the current site onto the query, so we can pass the Site Title to header.js
   if (currentSite !== "" && !favourites[currentSite])
   {
      query.shortName.values.push(currentSite);
      ignoreCurrentSite = true;
   }
   
   var connector = remote.connect("alfresco");
   result = connector.post("/api/sites/query", jsonUtils.toJSONString(query), "application/json");
   
   if (result.status == 200)
   {
      var i, ii;
      
      // Create javascript objects from the server response
      // Each item is a favourite site that the user is a member of
      var sites = eval('(' + result + ')'), site;
      
      if (sites.length != 0)
      {
         // Sort the sites by title
         sites.sort(sortByTitle);
         
         for (i = 0, ii = sites.length; i < ii; i++)
         {
            site = sites[i];
            if (site.shortName == currentSite)
            {
               siteTitle = site.title;
               if (ignoreCurrentSite)
               {
                  // The current site was piggy-backing the query call; it's not a favourite
                  continue;
               }
               currentSiteIsFav = true;
            }
            favouriteSites.push(site);
         }
      }
   }

   // Prepare the model for the template
   model.currentSiteIsFav = currentSiteIsFav;
   model.favouriteSites = favouriteSites;
   model.siteTitle = siteTitle;
   // Save the site title for downstream components - saves remote calls for Site Profile
   context.setValue("site-title", siteTitle);
}

main();