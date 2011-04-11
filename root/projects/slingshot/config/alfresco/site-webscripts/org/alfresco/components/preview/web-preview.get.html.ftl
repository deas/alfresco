<#if node??>
   <#assign el=args.htmlid?html>
   <#if (node?exists)>
   <script type="text/javascript">//<![CDATA[
   new Alfresco.WebPreview("${el}").setOptions(
   {
      nodeRef: "${nodeRef}",
      name: "${node.name?js_string}",
      mimeType: "${node.mimeType}",
      size: "${node.size}",
      thumbnails: [<#list node.thumbnails as t>"${t}"<#if (t_has_next)>, </#if></#list>],
      pluginConditions: ${pluginConditionsJSON}
   }).setMessages(${messages});
   //]]></script>
   </#if>
   <div id="${el}-body" class="web-preview">
      <div id="${el}-previewer-div" class="previewer">
         <div class="message"><#if (node?exists)>${msg("label.preparingPreviewer")}</#if></div>
      </div>
   </div>
</#if>