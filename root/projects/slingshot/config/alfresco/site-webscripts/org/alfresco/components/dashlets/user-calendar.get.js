<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/calendar/helper.js">
var now = new Date();
var fromDate = now.getFullYear() + "/" + (now.getMonth() + 1) + "/" + now.getDate();
var uri = "/calendar/events/user?from=" + encodeURIComponent(fromDate);

var data = doGetCall(uri);
if (data !== null)
{
   for (var i=0,len = data.events.length;i<len;i++)
   {
      data.events[i].when = CalendarScriptHelper.convertFromISOString(data.events[i].when);
   }
   model.eventList = data.events;
}