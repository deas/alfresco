/**
 * Admin Console Application Tool POST method
 */
/**
 * Admin Console Application Tool POST method
 */

function main()
{
   var themeId = json.get("console-options-theme-menu");
   context.setThemeId(new String(themeId));
   
   // persist across Share application if this is the admin user
   if (user.isAdmin)
   {
      var sc = context.getSiteConfiguration();
      sc.setProperty("theme", themeId);
      sc.save();
   }
   
   model.success = true;
}

main();