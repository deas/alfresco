<#include "include/toolbar.lib.ftl" />
<#assign el=args.htmlid?html>
<@toolbarTemplate>
<script type="text/javascript">//<![CDATA[
   new Alfresco.DocListToolbar("${el}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      hideNavBar: ${(preferences.hideNavBar!false)?string},
      googleDocsEnabled: ${(googleDocsEnabled!false)?string}
   }).setMessages(
      ${messages}
   );
//]]></script>
</@>