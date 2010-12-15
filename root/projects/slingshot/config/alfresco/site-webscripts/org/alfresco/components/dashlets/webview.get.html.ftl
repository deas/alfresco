<script type="text/javascript">//<![CDATA[
   new Alfresco.dashlet.WebView("${args.htmlid}").setOptions(
   {
      componentId: "${instance.object.id}",
      webviewURI: "${uri}",
      webviewTitle: "${webviewTitle?js_string}",
      webviewHeight: "${height?js_string}",
      isDefault: ${isDefault?string}
   });
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
//]]></script>
<div class="dashlet webview">
   <div class="title">
      <a id="${args.htmlid}-title-link" class="title-link" <#if !isDefault>href="${uri}"</#if> target="_blank"><#if webviewTitle != "">${webviewTitle?html}<#elseif !isDefault>${uri?html}<#else>${msg('label.header')}</#if></a>
   </div>

   <div class="toolbar">
      <a id="${args.htmlid}-configWebView-link" class="configure theme-color-1" href="#">${msg("label.configure")}</a>
   </div>

   <div class="body scrollablePanel"<#if args.height??> style="height: ${args.height}px;"</#if> id="${args.htmlid}-iframeWrapper">
       <iframe frameborder="0" scrolling="auto" width="100%" height="100%" src="${uri}"></iframe>
       <div class="resize-mask"></div>
   </div>

</div>