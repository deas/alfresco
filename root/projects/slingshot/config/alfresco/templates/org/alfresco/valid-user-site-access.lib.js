/**
 * Access test to ensure page is appropriate for the current user.
 * 
 * - User Dashboard - user must be same user id as specified in the page view id
 * - Site Dashboard - cannot view or modify a non-public private site
 * 
 * @param siteManager   True if the Site Dashboard test must be for SiteManager status
 */
function isValidUserOrSite(siteManager)
{
   var valid = true;
   if (page.url.templateArgs.userid != null)
   {
      // User Dashboard - user must be same user as per page view id
      valid = (user.name.toLowerCase() == page.url.templateArgs.userid.toLowerCase());
   }
   else if (page.url.templateArgs.site != null)
   {
      valid = false;
      
      // Site Dashboard - cannot view/enter private site pages
      var json = remote.call("/api/sites/" + page.url.templateArgs.site);
      if (json.status == 200)
      {
         // Any 200 return from the call means the site was not Private or
         // we are a valid member of a Private site.
         var site = eval('(' + json + ')');
         
         // Store the site profile in the request context, it is used
         // downstream by other components - saves making same call many times
         context.setValue("site-profile", site);
         
         if (site.visibility != "MODERATED")
         {
            // Do we want to test for SiteManager role status?
            valid = (!siteManager || isSiteManager(site));
         }
         else
         {
            // If this site is Moderated - we need to see if we are a member to view dashboards etc.
            json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + encodeURIComponent(user.name));
            if (json.status == 200)
            {
               // Any 200 return from the call means we are a member - else 404 is returned
               var obj = eval('(' + json + ')');
               
               // Store the memberships in the request context, it is used
               // downstream by other components - saves making same call many times
               context.setValue("memberships", obj);
               
               valid = (!siteManager || isSiteManager(site));
            }
         }
      }
   }
   return valid;
}

function isSiteManager(site)
{
   var managers = site.siteManagers;
   for (var i = 0; i < managers.length; i++)
   {
      if (managers[i] == user.name)
      {
         return true;
      }
   }
   return false;
}