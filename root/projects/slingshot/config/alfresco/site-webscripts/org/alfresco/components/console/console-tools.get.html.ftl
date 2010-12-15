<div id="${args.htmlid?html}-body" class="tool tools-link">
   <h2>${msg("header.tools")}</h2>
   <ul class="toolLink">
      <#list tools as tool>
      <li class="<#if tool_index=0>first-link</#if><#if tool.selected> selected</#if>"><span><a href="${tool.id}" class="tool-link" title="${tool.description?html}">${tool.label?html}</a></span></li>
      </#list>
   </ul>
</div>