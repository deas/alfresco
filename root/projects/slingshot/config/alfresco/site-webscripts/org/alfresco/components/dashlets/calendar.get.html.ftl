<script type="text/javascript">//<![CDATA[
(function()
{
   new Alfresco.dashlet.MiniCalendar("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(${messages});
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
   new Alfresco.widget.DashletTitleBarActions("${args.htmlid}").setOptions(
   {
      actions:
      [
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
<div class="dashlet calendar">
   <div class="title">${msg("label.header")}</div>
   <div id="${args.htmlid}-eventsContainer" class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
   </div>
</div>