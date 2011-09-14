<#include "include/toolbar.lib.ftl" />
<@toolbarTemplate>
<script type="text/javascript">//<![CDATA[
   new Alfresco.RepositoryDocListToolbar("${args.htmlid?js_string}").setOptions(
   {
      rootNode: "${rootNode!"null"}",
      hideNavBar: ${(preferences.hideNavBar!false)?string},
      googleDocsEnabled: ${(googleDocsEnabled!false)?string},
      repositoryBrowsing: ${(rootNode??)?string},
      useTitle: ${((args.useTitle!config.scoped["DocumentLibrary"]["use-title"].value)!"true")?js_string}
   }).setMessages(
      ${messages}
   );
//]]></script>
</@>