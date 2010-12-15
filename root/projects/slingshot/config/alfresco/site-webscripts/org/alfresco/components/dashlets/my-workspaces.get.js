const PREF_SITES = "org.alfresco.share.sites";
const PREF_FAVOURITE_SITES = PREF_SITES + ".favourites";
const PREF_IMAP_FAVOURITE_SITES = PREF_SITES + ".imapFavourites";

/**
 * Sort favourites to the top, then in alphabetical order
 */
function sortSites(site1, site2)
{
   return (!site1.isFavourite && site2.isFavourite) ? 1 : (site1.isFavourite && !site2.isFavourite) ? -1 : (site1.title > site2.title) ? 1 : (site1.title < site2.title) ? -1 : 0;
}

function main()
{
   var sites = [],
      imapServerEnabled = false;
   
   // Call the repo for sites the user is a member of
   var result = remote.call("/api/people/" + encodeURIComponent(user.name) + "/sites");
   if (result.status == 200)
   {
      var site, favourites = {}, imapFavourites = {}, managers,
         i, ii, j, jj;
      
      // Create javascript objects from the server response
      sites = eval('(' + result + ')');
      
      if (sites.length > 0)
      {
         // Check for IMAP server status
         result = remote.call("/imap/servstatus");
         imapServerEnabled = (result.status == 200 && result == "enabled");
         
         // Call the repo for the user's favourite sites
         result = remote.call("/api/people/" + encodeURIComponent(user.name) + "/preferences?pf=" + PREF_SITES);
         if (result.status == 200 && result != "{}")
         {
            var prefs = eval('(' + result + ')');
            
            // Populate the favourites object literal for easy look-up later
            favourites = eval('try{(prefs.' + PREF_FAVOURITE_SITES + ')}catch(e){}');
            if (typeof favourites != "object")
            {
               favourites = {};
            }

            // Populate the imap favourites object literal for easy look-up later
            imapFavourites = eval('try{(prefs.' + PREF_IMAP_FAVOURITE_SITES + ')}catch(e){}');
            if (typeof imapFavourites != "object")
            {
               imapFavourites = {};
            }
         }
         
         for (i = 0, ii = sites.length; i < ii; i++)
         {
            site = sites[i];
            
            // Is current user a Site Manager for this site?
            site.isSiteManager = false;
            if (site.siteManagers)
            {
               managers = site.siteManagers;
               for (j = 0, jj = managers.length; j < jj; j++)
               {
                  if (managers[j] == user.name)
                  {
                     site.isSiteManager = true;
                     break;
                  }
               }
            }
            
            // Is this site a user favourite?
            site.isFavourite = !!(favourites[site.shortName]);
            
            // Is this site a user imap favourite?
            site.isIMAPFavourite = !!(imapFavourites[site.shortName]);
         }

         // Sort the favourites to the top
         sites.sort(sortSites);
      }

      // Prepare the model for the template
      model.sites = sites;
      model.imapServerEnabled = imapServerEnabled;
   }
}

main();