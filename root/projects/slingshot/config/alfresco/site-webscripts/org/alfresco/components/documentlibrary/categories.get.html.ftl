<#assign el=args.htmlid?html>
<#assign categoryConfig = config.scoped["DocumentLibrary"]["categories"]!>
<#if categoryConfig.getChildValue??><#assign evaluateChildFolders = categoryConfig.getChildValue("evaluate-child-folders")!"true"></#if>
<script type="text/javascript">//<![CDATA[
   new Alfresco.DocListCategories("${el}").setOptions(
   {
      nodeRef: "alfresco://category/root",
      evaluateChildFolders: ${evaluateChildFolders!"true"}
   }).setMessages(
      ${messages}
   );
//]]></script>
<div class="categoryview filter">
   <h2 id="${el}-h2">${msg("header.library")}</h2>
   <div id="${el}-treeview" class="category"></div>
</div>