<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/dashlets/webview.css" group="dashlets"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/dashlets/webview.js" group="dashlets"/>
   <@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js" group="dashlets"/>
</@>

<@markup id="pre">
   <@inlineScript group="dashlets">
      var editWebViewDashletEvent = new YAHOO.util.CustomEvent("onDashletConfigure");
   </@>
</@>

<@markup id="widgets">
   <@createWidgets group="dashlets"/>
</@>

<@markup id="post">
   <@inlineScript group="dashlets">
      editWebViewDashletEvent.subscribe(webView.onConfigWebViewClick, webView, true);
   </@>
</@>

<@markup id="html">
   <@uniqueIdDiv>
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
   </@>
</@>