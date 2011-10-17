<#import "/org/alfresco/utils/feed.utils.ftl" as feedLib/>
<#assign DISPLAY_ITEMS = 100>
<#assign el=args.htmlid?js_string>
<script type="text/javascript">//<![CDATA[
(function()
{
   var rssFeed = new Alfresco.dashlet.RssFeed("${el}").setOptions(
   {
      componentId: "${instance.object.id}",
      feedURL: "${uri}",
      limit: <#if limit?number != DISPLAY_ITEMS>"${limit}"<#else>"all"</#if>
   }).setMessages(${messages});
   new Alfresco.widget.DashletResizer("${el}", "${instance.object.id}");

   var rssFeedDashletEvent = new YAHOO.util.CustomEvent("onConfigFeedClick");
   rssFeedDashletEvent.subscribe(rssFeed.onConfigFeedClick, rssFeed, true);

   new Alfresco.widget.DashletTitleBarActions("${args.htmlid}").setOptions(
   {
      actions:
      [
<#if userIsSiteManager>
         {
            cssClass: "edit",
            eventOnClick: rssFeedDashletEvent,
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
<div class="dashlet rssfeed">
   <div class="title" id="${el}-title">${title!msg("label.header")}</div>
   <div class="toolbar">
      <div>${msg("label.body")} <a href="${msg("label.addonsLink")}" target="${target}">${msg("label.addonsText")}</a></div>
   </div>
   <div class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
      <div class="dashlet-padding" id="${el}-scrollableList">
      
      
   <#if items?? && items?size &gt; 0>
      <#list items as item>
         <#if item_index &lt; limit?number><@feedLib.renderItem item=item target=target/><#else><#break></#if>
      </#list>
   <#elseif !error?exists>
         <h3>${msg("label.noItems")}</h3>
   </#if>
      </div>
   </div>
</div>
