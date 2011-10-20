<#assign idJS=args.htmlid?js_string>
<script type="text/javascript">//<![CDATA[
(function()
{
   new Alfresco.dashlet.ImageSummary("${idJS}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(${messages});
   new Alfresco.widget.DashletResizer("${idJS}", "${instance.object.id}");
   new Alfresco.widget.DashletTitleBarActions("${idJS}").setOptions(
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
<#assign el=args.htmlid?html>
<div class="hidden">
   <#-- HTML template for an image item -->
   <div id="${el}-item-template" class="item">
      <div class="thumbnail">
         <div class="action-overlay">
            <a href="${url.context}/page/site/${page.url.templateArgs.site}/document-details?nodeRef={nodeRef}"><img title="${msg("label.viewdetails")}" src="${url.context}/res/components/documentlibrary/actions/document-view-details-16.png" width="16" height="16" /></a>
            <a href="${url.context}/proxy/alfresco/api/node/content/{nodeRefUrl}/{name}?a=true"><img title="${msg("label.download")}" src="${url.context}/res/components/documentlibrary/actions/document-download-16.png" width="16" height="16"/></a>
         </div>
         <a href="${url.context}/proxy/alfresco/api/node/content/{nodeRefUrl}/{name}" onclick="showLightbox(this);return false;" title="{title} - {modifier} {modified}"><img src="${url.context}/proxy/alfresco/api/node/{nodeRefUrl}/content/thumbnails/doclib?c=force"/></a>
      </div>
   </div>
</div>
<div class="dashlet">
   <div class="title">${msg("header.title")}</div>
   <div id="${el}-list" class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
      <div class="dashlet-padding">
         <div id="${el}-wait" class="images-wait"></div>
         <div id="${el}-message" class="images-message hidden"></div>
         <div id="${el}-images" class="images hidden"></div>
      </div>
   </div>
</div>
