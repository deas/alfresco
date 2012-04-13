<#include "include/documentlist.lib.ftl" />
<@documentlistTemplate>
<script type="text/javascript">//<![CDATA[
   new Alfresco.DocumentList("${args.htmlid}").setOptions(
   {
      <#if repositoryUrl??>repositoryUrl: "${repositoryUrl}",</#if>
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${template.properties.container!"documentLibrary"}",
      rootNode: "${rootNode!"null"}",
      usePagination: ${(args.pagination!false)?string},
      sortAscending: ${(preferences.sortAscending!true)?string},
      sortField: "${(preferences.sortField!"cm:name")?js_string}",
      showFolders: ${(preferences.showFolders!true)?string},
      simpleView: <#if preferences.simpleView??>${preferences.simpleView?string}<#else>null</#if>,
      viewRendererName: "${(preferences.viewRendererName!"detailed")?js_string}",
      viewRendererNames: <#if viewRendererNames??>[<#list viewRendererNames as viewRendererName>"${viewRendererName}"<#if viewRendererName_has_next>, </#if></#list>]<#else>["simple", "detailed"]</#if>,
      highlightFile: "${(page.url.args["file"]!"")?js_string}",
      replicationUrlMapping: ${replicationUrlMappingJSON!"{}"},
      repositoryBrowsing: ${(rootNode??)?string},
      useTitle: ${(useTitle!true)?string},
      userIsSiteManager: ${(userIsSiteManager!false)?string}
   }).setMessages(
      ${messages}
   );
//]]></script>
</@>