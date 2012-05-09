<#assign idJS=args.htmlid?js_string>
<script type="text/javascript">//<![CDATA[
(function()
{
   new Alfresco.dashlet.SiteSearch("${idJS}").setOptions(
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
<div class="dashlet sitesearch">
   <div class="title">${msg("header.title")}</div>
   <div class="toolbar flat-button">
      <div class="hidden">
         <span class="align-left search-text">
            <span class="first-child">
               <input type="text" id="${el}-search-text" maxlength="1024"/>
            </span>
         </span>
         <span id="${el}-search-button" class="align-left yui-button yui-push-button search-icon">
            <span class="first-child">
               <button id="${el}-search-button" type="button">${msg("searchButton.text")}</button>
            </span>
         </span>
         <div class="align-right">
            <span class="yui-button yui-menu-button" id="${el}-resultSize">
               <span class="first-child">
                  <button type="button" tabindex="0"></button>
               </span>
            </span>
         </div>
         <select id="${el}-resultSize-menu">
            <option value="10">10</option>
            <option value="25">25</option>
            <option value="50">50</option>
            <option value="100">100</option>
         </select>
      </div>
      <div class="clear"></div>
   </div>
   <div id="${el}-list" class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
      <div id="${el}-search-results"></div>
   </div>
</div>