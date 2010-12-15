<#assign filterIds = "">
<div class="filter datalist-filter">
   <h2>${msg("header.items")}</h2>
   <ul class="filterLink">
   <#list filters as filter>
      <#assign filterIds>${filterIds}"${filter.id}"<#if filter_has_next>,</#if></#assign>
      <li><span class="${filter.id}"><a class="filter-link" rel="${filter.data?html}" href="#">${msg(filter.label)}</a></span></li>
   </#list>
   </ul>
</div>
<script type="text/javascript">//<![CDATA[
   new Alfresco.component.BaseFilter("Alfresco.DataListFilter", "${args.htmlid?js_string}").setFilterIds([${filterIds}]);
//]]></script>