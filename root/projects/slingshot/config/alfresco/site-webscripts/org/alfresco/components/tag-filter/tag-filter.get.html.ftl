<#assign maximumTags = "100">
<#assign filterConfig = config.scoped["DocumentLibrary"]["filters"]!>
<#if filterConfig.getChildValue??>
   <#assign maximumTags = filterConfig.getChildValue("maximum-tag-count")!"100">
</#if>
<script type="text/javascript">//<![CDATA[
   new Alfresco.TagFilter("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${template.properties.container!""}",
      rootNode: "${rootNode!"null"}",
      numTags: ${maximumTags?number?c}
   }).setMessages(
      ${messages}
   ).setFilterIds(["tag"]);
//]]></script>
<div class="filter">
   <h2 class="alfresco-twister">${msg("header.title")}</h2>
   <ul class="filterLink" id="${args.htmlid}-tags"><li>&nbsp;</li></ul>
</div>