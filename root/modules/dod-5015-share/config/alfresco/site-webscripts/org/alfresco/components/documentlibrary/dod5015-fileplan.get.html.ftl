<#assign filterIds = "">
<div class="filter fileplan-filter">
   <h2>${msg("header.fileplan")}</h2>
   <ul class="filterLink">
      <li><span class="transfers"><a rel="" href="#">${msg("label.transfers")}</a></span></li>
      <li><span class="holds"><a rel="" href="#">${msg("label.holds")}</a></span></li>
   </ul>
</div>
<script type="text/javascript">//<![CDATA[
   new Alfresco.component.BaseFilter("Alfresco.DocListFilePlan", "${args.htmlid}").setFilterIds(["transfers", "holds"]);
//]]></script>