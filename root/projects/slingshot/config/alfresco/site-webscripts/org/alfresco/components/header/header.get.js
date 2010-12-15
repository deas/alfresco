/**
 * Share Header component GET method
 */

const PREF_COLLAPSED_TWISTERS = "org.alfresco.share.twisters.collapsed";

/**
 * Twister Preferences
 */
function getTwisterPrefs()
{
   var collapsedTwisters = "",
      result,
      response;

   result = remote.call("/api/people/" + encodeURIComponent(user.name) + "/preferences?pf=" + PREF_COLLAPSED_TWISTERS);
   if (result.status == 200 && result != "{}")
   {
      response = eval('(' + result + ')');
      collapsedTwisters = eval('try{(response.' + PREF_COLLAPSED_TWISTERS + ')}catch(e){}');
      if (typeof collapsedTwisters != "string")
      {
         collapsedTwisters = "";
      }
   }
   model.collapsedTwisters = collapsedTwisters;
}

/**
 * Site Title
 */
function getSiteTitle()
{
   var siteTitle = "",
      result,
      response;

   var siteId = page.url.templateArgs.site || "";
   if (siteId !== "")
   {
      result = remote.call("/api/sites/" + encodeURIComponent(siteId));
      if (result.status == 200 && result != "{}")
      {
         response = eval('(' + result + ')');
         siteTitle = response.title;
         if (typeof siteTitle != "string")
         {
            siteTitle = "";
         }
      }
   }
   model.siteTitle = siteTitle;
   // Save the site title for downstream components - saves remote calls for Site Profile
   context.setValue("site-title", siteTitle);
}

/**
 * Theme Override
 */
function getThemeOverride()
{
   if (page.url.args["theme"] != null)
   {
      model.theme = page.url.args["theme"];
   }
}

/**
 * Customizable Header
 */
function getHeader()
{
   // Array of tokenised values for use in i18n messages
   model.labelTokens = [ user.name || "", user.firstName || "", user.lastName || "", user.fullName || ""];
   model.permissions =
   {
      guest: user.isGuest,
      admin: user.isAdmin
   };
}

/**
 * User Status
 */
function getUserStatus()
{
   var userStatus = msg.get("status.default"),
      userStatusTime = "";
   
   if (user.properties["userStatus"] != null)
   {
      userStatus = user.properties["userStatus"];
   }
   if (user.properties["userStatusTime"] != null)
   {
      userStatusTime = user.properties["userStatusTime"];
   }
   
   model.userStatus = userStatus;
   model.userStatusTime = userStatusTime;
}

function main()
{
   getTwisterPrefs();
   getSiteTitle();
   getThemeOverride();
   getHeader();
   getUserStatus();
}

if (!user.isGuest)
{
   main();
}