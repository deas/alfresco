main();

/**
 * Main entrypoint
 */
function main()
{
   var activityFeed = getActivities();
   var activities = [], activity, item, summary, fullName, date, sites = {}, siteTitles = {};
   var dateFilter = args.dateFilter, oldestDate = getOldestDate(dateFilter);

   if (activityFeed != null)
   {
      var mode = args.mode,
         site = (mode == "site") ? args.site : null;
      
      for (var i = 0, ii = activityFeed.length; i < ii; i++)
      {
         activity = activityFeed[i];

         if (activity.activitySummaryFormat == "json")
         {
            summary = eval("(" + activity.activitySummary + ")");
            fullName = trim(summary.firstName + " " + summary.lastName);
            date = fromISO8601(activity.postDate);

            // Outside oldest date?
            if (date < oldestDate)
            {
               break;
            }

            item =
            {
               id: activity.id,
               type: activity.activityType,
               siteId: activity.siteNetwork,
               date:
               {
                  isoDate: activity.postDate,
                  fullDate: date,
                  hour: date.getHours()
               },
               title: summary.title || "title.generic",
               fullName: fullName,
               itemPage: itemPageUrl(activity, summary),
               sitePage: sitePageUrl(activity, summary),
               userProfile: userProfileUrl(activity.postUserId),
               custom0: summary.custom0 || "",
               custom1: summary.custom1 || "",
               suppressSite: false
            };
            
            // Add to our list of unique sites
            sites[activity.siteNetwork] = true;
            
            // Run through specialize function for special cases
            activities.push(specialize(item, activity, summary));
         }
      }
      
      siteTitles = getSiteTitles(sites);
   }

   model.activities = activities;
   model.siteTitles = siteTitles;
}


/**
 * Optionally specify each activity item by type
 */
function specialize(item, activity, summary)
{
   switch (activity.activityType)
   {
      case "org.alfresco.site.group-added":
      case "org.alfresco.site.group-removed":
         item.suppressSite = true;
         // Fall through....
      case "org.alfresco.site.group-role-changed":
         item.fullName = summary.groupName.replace("GROUP_", "");
         item.userProfile = null;
         item.custom0 = msg.get("role." + summary.role);
         break;

      case "org.alfresco.site.user-joined":
      case "org.alfresco.site.user-left":
         item.suppressSite = true;
         // Fall through....
      case "org.alfresco.site.user-role-changed":
         item.fullName = trim(summary.memberFirstName + " " + summary.memberLastName);
         item.userProfile = userProfileUrl(summary.memberUserName);
         item.custom0 = msg.get("role." + summary.role);
         break;
   }
   
   return item;
}


/**
 * Call remote Repo script to get relevant activities
 */
function getActivities()
{
   // Call the correct repo script depending on the mode
   var mode = args.mode, site = args.site, userFilter = args.userFilter, connector,
      result =
      {
         status: 0
      };

   if (format.name == "html")
   {
      connector = remote.connect("alfresco");
   }
   else
   {
      // Use alfresco-feed connector as a basic HTTP auth challenge will be issued
      connector = remote.connect("alfresco-feed");
   }

   // Filter by user
   var excl = "";
   switch(userFilter)
   {
      case "others":
         excl = "&exclUser=true";
         break; 
      case "mine":
         excl = "&exclOthers=true";
         break; 
   }

   // Filter by site
   if (mode == "site" && site)
   {
      excl = excl + "&s=" + encodeURI(site);
   }

   result = connector.get("/api/activities/feed/user?format=json" + excl);

   if (result.status == 200)
   {
      // Create javascript objects from the server response
      return eval("(" + result + ")");
   }
   
   status.setCode(result.status, result.response);
   return null;
}

/**
 * Call remote Repo script to get site titles
 */
function getSiteTitles(p_sites)
{
   var connector, result, query, shortName, siteTitles = {};

   if (format.name == "html")
   {
      connector = remote.connect("alfresco");
   }
   else
   {
      // Use alfresco-feed connector as a basic HTTP auth challenge will be issued
      connector = remote.connect("alfresco-feed");
   }

   // Sites query template
   query =
   {
      shortName:
      {
         match: "exact",
         values: []
      }
   };

   // Add our list of site names to the query
   for (shortName in p_sites)
   {
      if (p_sites[shortName])
      {
         query.shortName.values.push(shortName);
      }
   }
   
   // Call the repo to return a specific list of site metadata
   result = connector.post("/api/sites/query", jsonUtils.toJSONString(query), "application/json");
   
   if (result.status == 200)
   {
      var sites = eval('(' + result + ')'), site;

      // Extract site titles
      for (var i = 0, ii = sites.length; i < ii; i++)
      {
         site = sites[i];
         siteTitles[site.shortName] = site.title;
      }
   }
   return siteTitles;
}

/**
 * URL to user profile page
 */
function userProfileUrl(userId)
{
   return url.context + "/page/user/" + encodeURI(userId) + "/profile";
}

/**
 * URL to item page (could be site dashboard page)
 */
function itemPageUrl(activity, summary)
{
   return url.context + "/page/site/" + encodeURI(activity.siteNetwork) + (summary.page !== undefined ? "/" + summary.page : "/dashboard");
}

/**
 * URL to site dashboard page
 */
function sitePageUrl(activity, summary)
{
   return url.context + "/page/site/" + encodeURI(activity.siteNetwork) + "/dashboard";
}

/**
 * Work out the oldest date we should be processing
 */
function getOldestDate(filter)
{
   var date = new Date();
   date.setHours(0, 0, 0, 0);
   
   if (filter != "today")
   {
      date.setDate(date.getDate() - filter);
   }

   return date;
}

/**
 * Trim leading and trailing spaces
 */
function trim(str)
{
   try
   {
      return str.replace(/^\s+|\s+$/g, "");
   }
   catch(e)
   {
   }
   return str;
}

/**
 * Convert from ISO8601 date to JavaScript date
 */

function fromISO8601(formattedString)
{
   var isoRegExp = /^(?:(\d{4})(?:-(\d{2})(?:-(\d{2}))?)?)?(?:T(\d{2}):(\d{2})(?::(\d{2})(.\d+)?)?((?:[+-](\d{2}):(\d{2}))|Z)?)?$/;

   var match = isoRegExp.exec(formattedString);
   var result = null;

   if (match)
   {
      match.shift();
      if (match[1]){match[1]--;} // Javascript Date months are 0-based
      if (match[6]){match[6] *= 1000;} // Javascript Date expects fractional seconds as milliseconds

      result = new Date(match[0]||1970, match[1]||0, match[2]||1, match[3]||0, match[4]||0, match[5]||0, match[6]||0);

      var offset = 0;
      var zoneSign = match[7] && match[7].charAt(0);
      if (zoneSign != 'Z')
      {
         offset = ((match[8] || 0) * 60) + (Number(match[9]) || 0);
         if (zoneSign != '-')
         {
            offset *= -1;
         }
      }
      if (zoneSign)
      {
         offset -= result.getTimezoneOffset();
      }
      if (offset)
      {
         result.setTime(result.getTime() + offset * 60000);
      }
   }

   return result; // Date or null
}
