<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.DocumentInfo("${el}").setMessages(${messages});
//]]></script>

<div id="${el}-body" class="document-info">
   
   <div class="info-section">
      <div class="heading">${msg("label.tags")}</div>
      
      <div id="${el}-tags"></div>
   </div>
   
   <div id="${el}-permissionSection" class="info-section hidden">
      <div class="heading">${msg("document-info.permissions")}</div>
      
      <div class="info">
         <span class="meta-label">${msg("document-info.managers")}:</span>
         <span id="${el}-perms-managers" class="meta-value"></span>
      </div>
      <div class="info">
         <span class="meta-label">${msg("document-info.collaborators")}:</span>
         <span id="${el}-perms-collaborators" class="meta-value"></span>
      </div>
      <div class="info">
         <span class="meta-label">${msg("document-info.consumers")}:</span>
         <span id="${el}-perms-consumers" class="meta-value"></span>
      </div>
      <div class="info">
         <span class="meta-label">${msg("document-info.everyone")}:</span>
         <span id="${el}-perms-everyone" class="meta-value"></span>
      </div>
   </div>

</div>