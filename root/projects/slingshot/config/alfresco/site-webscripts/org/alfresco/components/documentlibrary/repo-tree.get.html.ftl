<#assign el=args.htmlid?html>
<#assign treeConfig = config.scoped["RepositoryLibrary"]["tree"]!>
<#if treeConfig.getChildValue??>
   <#assign evaluateChildFolders = treeConfig.getChildValue("evaluate-child-folders")!"true">
   <#assign maximumFolderCount = treeConfig.getChildValue("maximum-folder-count")!"-1">
</#if>
<script type="text/javascript">//<![CDATA[
   new Alfresco.RepositoryDocListTree("${el}").setOptions(
   {
      rootNode: "${rootNode}",
      evaluateChildFolders: ${evaluateChildFolders!"false"},
      maximumFolderCount: ${maximumFolderCount!"-1"}
   }).setMessages(
      ${messages}
   );
//]]></script>
<div class="treeview filter">
   <h2 id="${el}-h2">${msg("header.library")}</h2>
   <div id="${el}-treeview" class="tree"></div>
</div>