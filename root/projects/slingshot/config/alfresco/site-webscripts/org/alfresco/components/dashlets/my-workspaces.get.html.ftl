<script type="text/javascript">//<![CDATA[
(function()
{
   new Alfresco.dashlet.MyWorkspaces("${args.htmlid}").setOptions(
   {
      imapEnabled: ${imapServerEnabled?string},
      sites: [
<#if sites??>
   <#list sites as site>
      {
         shortName: '${site.shortName?js_string}',
         title: '${site.title?js_string}',
         description: '${site.description?js_string}',
         isFavourite: ${site.isFavourite?string},
         <#if imapServerEnabled>isIMAPFavourite: ${site.isIMAPFavourite?string},</#if>
         isSiteManager: ${site.isSiteManager?string}
      }<#if site_has_next>,</#if>
   </#list>
</#if>
      ]
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

<div class="dashlet my-workspaces">
   <div class="title">${msg("header.myWorkspaces")}</div>
<#if (sites?? && sites?size > 0)>
   <div id="${args.htmlid}-workspaces" class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
<#else>
   <div class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
      <div class="dashlet-padding">
         <h3>${msg("label.noWorkspaces")}</h3>
      </div>
</#if>
   </div>
</div>