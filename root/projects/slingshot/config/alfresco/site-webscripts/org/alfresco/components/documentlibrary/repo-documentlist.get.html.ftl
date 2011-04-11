<#include "include/documentlist.lib.ftl" />
<@documentlistTemplate>
<script type="text/javascript">//<![CDATA[
   new Alfresco.RepositoryDocumentList("${args.htmlid}").setOptions(
   {
      <#if repositoryUrl??>repositoryUrl: "${repositoryUrl}",</#if>
      rootNode: "${rootNode!"null"}",
      usePagination: ${(args.pagination!false)?string},
      sortAscending: ${(preferences.sortAscending!true)?string},
      sortField: "${(preferences.sortField!"cm:name")?js_string}",
      showFolders: ${(preferences.showFolders!true)?string},
      simpleView: ${(preferences.simpleView!false)?string},
      highlightFile: "${(page.url.args.file!"")?js_string}",
      replicationUrlMapping: ${replicationUrlMappingJSON!"{}"}
   }).setMessages(
      ${messages}
   );
//]]></script>
</@>