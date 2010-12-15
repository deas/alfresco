<script type="text/javascript">//<![CDATA[
   new Alfresco.component.Path("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${template.properties.container!"documentLibrary"}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div class="path-nav">
   <span class="heading">${msg("path.location")}:</span>
   <span id="${args.htmlid}-defaultPath" class="path-link"><a href="${url.context}/page/site/${page.url.templateArgs.site}/documentlibrary">${msg("path.documents")}</a></span>
   <span id="${args.htmlid}-path"></span>
</div>

<div id="${args.htmlid}-iconType" class="icon-type"></div>