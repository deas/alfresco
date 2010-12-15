<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.RepositoryFolderInfo("${el}").setMessages(${messages});
//]]></script>

<div id="${el}-body" class="folder-info">
   
   <div class="info-section">
      <div class="heading">${msg("folder-info.tags")}</div>
      
      <div id="${el}-tags"></div>
   </div>

</div>