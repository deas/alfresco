<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.CalendarToolbar("${args.htmlid?js_string}").setSiteId(
      "${page.url.templateArgs["site"]!""}"
   );
//]]></script>
<div id="${el}-body" class="toolbar calendar-toolbar theme-bg-2">
   <div class="yui-ge calendar-bar">
      <div class="yui-u first theme-bg-1">
         <button id="${el}-today-button">${msg("button.today")}</button>
         <span class="separator">&nbsp;</span>
         <button id="${el}-prev-button">&lt; ${msg("button.previous")}</button>
         <div id="${el}-navigation" class="yui-buttongroup inline">
            <#-- Don't insert linefeeds between these <input> tags -->
            <input id="${el}-day" type="radio" name="navigation" value="${msg("button.day")}" /><input id="${el}-week" type="radio" name="navigation" value="${msg("button.week")}" /><input id="${el}-month" type="radio" name="navigation" value="${msg("button.month")}" /><input id="${el}-agenda" type="radio" name="navigation" value="${msg("button.agenda")}" />
         </div>
         <button id="${el}-next-button">${msg("button.next")} &gt;</button>
      </div> 
      <div class="yui-u flat-button">
         <#if role = "SiteCollaborator" || role = "SiteManager">
         <div id="${el}-viewButtons" class="addEvent">
            <button id="${el}-addEvent-button" name="addEvent">${msg("button.add-event")}</button>
         </div>
         <div class="separator">&nbsp;</div>
         </#if>
         <div class="ical-feed">
            <a id="${el}-publishEvents-button" href="${page.url.context}/proxy/alfresco-feed/calendar/eventList?site=${page.url.templateArgs["site"]}&amp;format=calendar" rel="_blank">${msg("button.ical")}</a>
         </div>
      </div>
   </div>
   <div id="${el}-addEvent"></div>
</div>
<script type="text/javascript">//<![CDATA[
(function()
{
   Alfresco.util.relToTarget("${el}-body");
})();
//]]></script>