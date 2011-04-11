<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.FolderInfo("${el}").setMessages(${messages});
//]]></script>

<div id="${el}-body" class="folder-info">
   
   <div class="info-section">
      <div class="heading">${msg("label.tags")}</div>
      
      <div id="${el}-tags"></div>
   </div>
   
   <div class="info-section">
      <div class="heading">${msg("folder-info.permissions")}</div>
      
      <div class="info">
         <span class="meta-label">${msg("folder-info.managers")}:</span>
         <span id="${el}-perms-managers" class="meta-value"></span>
      </div>
      <div class="info">
         <span class="meta-label">${msg("folder-info.collaborators")}:</span>
         <span id="${el}-perms-collaborators" class="meta-value"></span>
      </div>
      <div class="info">
         <span class="meta-label">${msg("folder-info.contributors")}:</span>
         <span id="${el}-perms-contributors" class="meta-value"></span>
      </div>
      <div class="info">
         <span class="meta-label">${msg("folder-info.consumers")}:</span>
         <span id="${el}-perms-consumers" class="meta-value"></span>
      </div>
      <div class="info">
         <span class="meta-label">${msg("folder-info.everyone")}:</span>
         <span id="${el}-perms-everyone" class="meta-value"></span>
      </div>
   </div>

</div>