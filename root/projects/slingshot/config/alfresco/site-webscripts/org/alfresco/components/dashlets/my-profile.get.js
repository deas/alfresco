<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

/**
 * My Profile Component GET method
 */
function main()
{
   // If there's a usrStatusTime, then convert to a relative time
   if (user.properties.userStatusTime != null)
   {
      model.userStatusRelativeTime = AlfrescoUtil.relativeTime(user.properties.userStatusTime);
   }
}

main();