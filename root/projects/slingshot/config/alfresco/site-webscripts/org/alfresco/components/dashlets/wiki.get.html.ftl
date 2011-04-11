<script type="text/javascript">//<![CDATA[
(function()
{
   var wiki = new Alfresco.dashlet.WikiDashlet("${args.htmlid}").setOptions(
   {
      guid: "${instance.object.id}",
      siteId: "${page.url.templateArgs.site!""}",
      pages: [<#if (pageList?? && pageList.pages?size &gt; 0)><#list pageList.pages as p>"${p.name?js_string}"<#if p_has_next>, </#if></#list></#if>]
   }).setMessages(${messages});
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");

   var editDashletEvent = new YAHOO.util.CustomEvent("onDashletConfigure");
   editDashletEvent.subscribe(wiki.onConfigFeedClick, wiki, true);

   new Alfresco.widget.DashletTitleBarActions("${args.htmlid}").setOptions(
   {
      actions:
      [
<#if userIsSiteManager>
         {
            cssClass: "edit",
            eventOnClick: editDashletEvent,
            tooltip: "${msg("dashlet.edit.tooltip")?js_string}"
         },
</#if>
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
<div class="dashlet wiki">
   <div class="title" id="${args.htmlid}-title">${msg("label.header-prefix")}<#if wikiLink??> - <a href="wiki-page?title=${wikiLink?url}">${pageTitle!msg("label.header")}</a></#if></div>
   <div class="body scrollablePanel" <#if args.height??>style="height: ${args.height}px;"</#if>>
      <div id="${args.htmlid}-scrollableList" class="rich-content dashlet-padding">
<#if wikipage??>
         ${wikipage}
<#else>
         <h3>${msg("label.noConfig")}</h3>
</#if>
      </div>
   </div>
</div>
