<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.component.BaseFilter("Alfresco.LinkFilter", "${el}");
//]]></script>
<div class="filter links-filter">
   <h2 id="${el}-h2">${msg("header.links")}</h2>
   <ul class="filterLink">
   <#list filters as filter>
      <li><span class="${filter.id}"><a class="filter-link" href="#">${msg(filter.label)}</a></span></li>
   </#list>
   </ul>
</div>