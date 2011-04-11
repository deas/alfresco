<#include "include/toolbar.lib.ftl" />
<@toolbarTemplate>
<script type="text/javascript">//<![CDATA[
   new Alfresco.RepositoryDocListToolbar("${args.htmlid?js_string}").setOptions(
   {
      rootNode: "${rootNode!"null"}",
      hideNavBar: ${(preferences.hideNavBar!false)?string},
      googleDocsEnabled: ${(googleDocsEnabled!false)?string}
   }).setMessages(
      ${messages}
   );
//]]></script>
</@>