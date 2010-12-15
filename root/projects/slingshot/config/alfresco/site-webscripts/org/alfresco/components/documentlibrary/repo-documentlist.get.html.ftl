<#include "include/documentlist.lib.ftl" />
<@documentlistTemplate>
<script type="text/javascript">//<![CDATA[
   new Alfresco.RepositoryDocumentList("${args.htmlid}").setOptions(
   {
      <#if repositoryUrl??>repositoryUrl: "${repositoryUrl}",</#if>
      rootNode: "${rootNode}",
      usePagination: ${(args.pagination!false)?string},
      showFolders: ${(preferences.showFolders!false)?string},
      simpleView: ${(preferences.simpleView!false)?string},
      highlightFile: "${(page.url.args.file!"")?js_string}",
      replicationUrlMapping: ${replicationUrlMappingJSON!"{}"}
   }).setMessages(
      ${messages}
   );
//]]></script>
</@>