<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   var enabledViewList =  [];
   <#if enabledViews.day>
      <#assign day='<input id="${el}-day" type="radio" name="navigation" value="${msg("button.day")}" />'/>
      enabledViewList.push("day");
   </#if>
   <#if enabledViews.week>
      <#assign week='<input id="${el}-week" type="radio" name="navigation" value="${msg("button.week")}" />'/>
      enabledViewList.push("week");
   </#if>
   <#if enabledViews.month>
      <#assign month='<input id="${el}-month" type="radio" name="navigation" value="${msg("button.month")}" />'/>
      enabledViewList.push("month");
   </#if>
   <#if enabledViews.agenda>
      <#assign agenda='<input id="${el}-agenda" type="radio" name="navigation" value="${msg("button.agenda")}" />'/>
      enabledViewList.push("agenda");
   </#if>
   new Alfresco.CalendarToolbar("${args.htmlid?js_string}", enabledViewList, "${defaultView}").setSiteId(
      "${page.url.templateArgs["site"]!""}"
   );
//]]></script>
<div id="${el}-body" class="toolbar calendar-toolbar theme-bg-2">
   <div class="yui-ge calendar-bar">
      <#if role = "SiteCollaborator" || role = "SiteManager">
         <div class="yui-u flat-button addEventContainer">
            <div id="${el}-viewButtons" class="addEvent">
               <button id="${el}-addEvent-button" name="addEvent">${msg("button.add-event")}</button>
            </div>
         </div>
         <span class="separator">&nbsp;</span>
      </#if>
      <div class="yui-u first theme-bg-1">
   		<button id="${el}-today-button" class="today-button">${msg("button.today")}</button>
         <#if viewToolbarNav >
            <button id="${el}-prev-button" class="prev-button">${msg("button.previous")}</button>
			</#if>
         <#if viewToolbarViewCount>
   			<div id="${el}-navigation" class="yui-buttongroup inline">
   				<#-- Don't insert linefeeds between these <input> tags -->
   				  ${day!""}${week!""}${month!""}${agenda!""}
   			</div>         
         </#if>
         <#if viewToolbarNav >
   			<button id="${el}-next-button">${msg("button.next")}</button>
         </#if>
		</div>
      <div class="flat-button work-hours">
         <span id="${el}-workHours-button" class="yui-button yui-checkbox-button">
            <span class="first-child">
               <button name="workHours"></button>
            </span>
         </span>
      </div>
      <div class="yui-u flat-button">
         <div class="ical-feed">
            <a id="${el}-publishEvents-button" href="${page.url.context}/proxy/alfresco-feed/calendar/eventList-${page.url.templateArgs["site"]}.ics?site=${page.url.templateArgs["site"]}&amp;format=calendar" rel="_blank">${msg("button.ical")}</a>
         </div>
      </div>
   </div>
   <div id="${el}-addEvent"></div>
</div>
<script type="text/javascript">//<![CDATA[
(function()
{
   Alfresco.util.relToTarget("${el}-body");
   Alfresco.util.addMessages(${messages}, "Alfresco.CalendarToolbar");
})();
//]]></script>