<#include "../../include/alfresco-macros.lib.ftl" />
<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.component.Path("${el}").setOptions(
   {
      rootPage: "repository",
      rootLabelId: "path.repository"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div class="path-nav">
   <span class="heading">${msg("path.location")}:</span>
   <#assign href>${url.context}/page/repository</#assign>
   <span id="${el}-defaultPath" class="path-link"><a href="${siteURL("repository")}">${msg("path.repository")}</a></span>
   <span id="${el}-path"></span>
</div>
<#if (args.showIconType!"true") == "true">
<div id="${el}-iconType" class="icon-type"></div>
</#if>