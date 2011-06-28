<#assign id = args.htmlid>
<#assign jsid = args.htmlid?js_string>
<script type="text/javascript">//<![CDATA[
(function()
{
   var activities = new Alfresco.dashlet.Activities("${jsid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      mode: "site"
   }).setMessages(${messages});
   new Alfresco.widget.DashletResizer("${jsid}", "${instance.object.id}");

   var activitiesFeedDashletEvent = new YAHOO.util.CustomEvent("openFeedClick");
   activitiesFeedDashletEvent.subscribe(activities.openFeedLink, activities, true);

   new Alfresco.widget.DashletTitleBarActions("${jsid}").setOptions(
   {
      actions:
      [
         {
            cssClass: "rss",
            eventOnClick: activitiesFeedDashletEvent,
            tooltip: "${msg("dashlet.rss.tooltip")?js_string}"
         },
         {
            cssClass: "help",
            bubbleOnClick:
            {
               message: "${msg("dashlet.help")?js_string}"
            },
            tooltip: "${msg("dashlet.help.tooltip")?js_string}"
         }
      ]
   });
})();
//]]></script>

<div class="dashlet activities">
   <div class="title">${msg("header")}</div>
   <div class="toolbar flat-button">
      <div class="hidden">
         <span class="align-left yui-button yui-menu-button" id="${id}-user">
            <span class="first-child">
               <button type="button" tabindex="0"></button>
            </span>
         </span>
         <select id="${id}-user-menu">
         <#list filterTypes as filter>
            <option value="${filter.type?html}">${msg("filter." + filter.label)}</option>
         </#list>
         </select>
         <span class="align-left yui-button yui-menu-button" id="${id}-range">
            <span class="first-child">
               <button type="button" tabindex="0"></button>
            </span>
         </span>
         <select id="${id}-range-menu">
         <#list filterRanges as filter>
            <option value="${filter.type?html}">${msg("filter." + filter.label)}</option>
         </#list>
         </select>
         <span class="align-left first-child" style="padding-top:5px">${msg("label.filter")}:</span>
         <span class="align-left yui-button yui-menu-button" id="${id}-activities">
            <span class="first-child">
               <button type="button" tabindex="0"></button>
            </span>
         </span>
         <select id="${id}-activities-menu">
         <#list filterActivities as filter>
            <option value="${filter.activities?html}">${msg(filter.label)}</option>
         </#list>
         </select>
      </div>
   </div>
   <div id="${id}-activityList" class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>></div>
</div>

<#-- Empty results list template -->
<div id="${id}-empty" style="display: none">
   <div class="empty"><h3>${msg("empty.title")}</h3><span>${msg("empty.description")}</span></div>
</div>