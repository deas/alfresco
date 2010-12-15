<div class="filter savedsearch-filter">
   <h2>${msg("header.savedsearch")}</h2>
   <ul class="filterLink">
   <#list savedSearches as s>
      <li><span class="savedsearch"><a rel="${s.data?html}" href="#" title="${s.description?html}">${msg(s.label)}</a></span></li>
   </#list>
   </ul>
</div>
<script type="text/javascript">//<![CDATA[
   new Alfresco.component.BaseFilter("Alfresco.DocListSavedSearch", "${args.htmlid}").setFilterIds("savedsearch");
//]]></script>