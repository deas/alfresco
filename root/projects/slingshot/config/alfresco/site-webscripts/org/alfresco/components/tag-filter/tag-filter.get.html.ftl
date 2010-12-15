<script type="text/javascript">//<![CDATA[
   new Alfresco.TagFilter("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${template.properties.container!""}",
      rootNode: "${rootNode}"
   }).setMessages(
      ${messages}
   ).setFilterIds(["tag"]);
//]]></script>
<div class="filter">
	<h2>${msg("header.title")}</h2>
	<ul class="filterLink" id="${args.htmlid}-tags"><li>&nbsp;</li></ul>
</div>