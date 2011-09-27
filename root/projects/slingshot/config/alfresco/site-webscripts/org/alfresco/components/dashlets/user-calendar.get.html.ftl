<#assign dashboardconfig=config.scoped['Dashboard']['dashboard']>
<script type="text/javascript">//<![CDATA[
(function()
{
   new Alfresco.dashlet.UserCalendar("${args.htmlid?js_string}").setOptions(
   {
      listSize: ${dashboardconfig.getChildValue('summary-list-size')!100}
   }).setMessages(${messages});
   new Alfresco.widget.DashletResizer("${args.htmlid?js_string}", "${instance.object.id}");
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

<div class="dashlet user-calendar">
   <div class="title">${msg("label.header")}</div>
   <div id="${args.htmlid?html}-events" class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>></div>
</div>