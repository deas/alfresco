<script type="text/javascript">//<![CDATA[
(function()
{
   var webView = new Alfresco.dashlet.WebView("${args.htmlid}").setOptions(
   {
      componentId: "${instance.object.id}",
      webviewURI: "${uri}",
      webviewTitle: "${webviewTitle?js_string}",
      webviewHeight: "${height?js_string}",
      isDefault: ${isDefault?string}
   });
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");

   /**
    * Create a new custom YUI event and subscribe it to the WebViews onConfigWebViewClick
    * function. This custom event is then passed into the DashletTitleBarActions widget as
    * an eventOnClick action so that it can be fired when the user clicks on the Edit icon
   */
   var editDashletEvent = new YAHOO.util.CustomEvent("onDashletConfigure");
   editDashletEvent.subscribe(webView.onConfigWebViewClick, webView, true);

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
<div class="dashlet webview">
   <div class="title">
      <a id="${args.htmlid}-title-link" class="title-link" <#if !isDefault>href="${uri}"</#if> target="_blank"><#if webviewTitle != "">${webviewTitle?html}<#elseif !isDefault>${uri?html}<#else>${msg('label.header')}</#if></a>
   </div>
   <div class="body scrollablePanel"<#if args.height??> style="height: ${args.height}px;"</#if> id="${args.htmlid}-iframeWrapper">
   <#if isDefault>
      <h3 class="configureInstructions">${msg("label.noWebPage")}</h3>
   <#else>
      <iframe frameborder="0" scrolling="auto" width="100%" height="100%" src="${uri}"></iframe>
      <div class="resize-mask"></div>
   </#if>
   </div>

</div>