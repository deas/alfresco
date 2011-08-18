<#include "include/toolbar.lib.ftl" />
<@toolbarTemplate>
<script type="text/javascript">//<![CDATA[
   new Alfresco.DocListToolbar("${args.htmlid?js_string}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      rootNode: "${rootNode}",
      hideNavBar: ${(preferences.hideNavBar!false)?string},
      googleDocsEnabled: ${(googleDocsEnabled!false)?string},
      repositoryBrowsing: ${(rootNode??)?string},
      useTitle: ${((args.useTitle!config.scoped["DocumentLibrary"]["use-title"])!"true")?js_string}
   }).setMessages(
      ${messages}
   );
//]]></script>
</@>