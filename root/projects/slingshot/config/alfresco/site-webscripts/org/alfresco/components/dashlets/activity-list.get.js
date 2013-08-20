<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
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

            summary = eval("(" + activity.activitySummary + ")");
            fullName = trim(summary.firstName + " " + summary.lastName);
            date = AlfrescoUtil.fromISO8601(activity.postDate);

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
               },
               title: summary.title || "title.generic",
               userName: activity.postUserId,
               userAvatar: activity.postUserAvatar || "avatar",
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
      
      siteTitles = getSiteTitles(sites);
   }

   model.activities = activities;
   model.siteTitles = siteTitles;
   model.cssClasses = getCSSClasses();
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
         item.userName = summary.memberUserName;
         item.fullName = trim(summary.memberFirstName + " " + summary.memberLastName);
         item.userProfile = userProfileUrl(summary.memberUserName);
         item.custom0 = msg.get("role." + summary.role);
         break;
      
      case "org.alfresco.site.liked":
         item.suppressSite = true;
         break;
      case "org.alfresco.subscriptions.followed":
         item.fullName = trim(summary.followerFirstName + " " + summary.followerLastName);
         item.userProfile = userProfileUrl(summary.followerUserName);
         item.secondFullName = trim(summary.userFirstName + " " + summary.userLastName);
         item.secondUserProfile = userProfileUrl(summary.userUserName);
         item.itemPage = item.userProfile;
         item.suppressSite = true;
         break;
      case "org.alfresco.subscriptions.subscribed":
         item.fullName = trim(summary.subscriberFirstName + " " + summary.subscriberLastName);
         item.userProfile = userProfileUrl(summary.subscriberUserName);
         item.custom0 = summary.node;
         item.suppressSite = true;
         break;
      case "org.alfresco.profile.status-changed":
         item.custom0 = summary.status;
         item.itemPage = item.userProfile;
         item.suppressSite = true;
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
   var mode = args.mode, site = args.site, userFilter = args.userFilter, activityFilter = args.activityFilter, connector,
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
      var cname = (args.loopback != null && args.loopback == "1") ? "alfresco" : "alfresco-feed";
      connector = remote.connect(cname);
   }

   // Filter by user
   var actParam = "";
   switch(userFilter)
   {
      case "others":
         actParam = "&exclUser=true";
         break; 
      case "mine":
         actParam = "&exclOthers=true";
         break;
      case "following":
         actParam = "&following=true";
         break;
   }

   // Filter by activityFilter
   if (activityFilter)
   {
	   actParam = actParam + "&activityFilter=" + encodeURI(activityFilter);
   }
   
   // Filter by site
   if (mode == "site" && site)
   {
	   actParam = actParam + "&s=" + encodeURI(site);
   }

   result = connector.get("/api/activities/feed/user?format=json" + actParam);

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

function getCSSClasses()
{
   var myConfig = new XML(config.script),
      css = {};

   for each (var xmlStyle in myConfig..style)
   {
      css[xmlStyle.@type.toString()] = xmlStyle.@css.toString();
   }
   
   return css;
}

main();