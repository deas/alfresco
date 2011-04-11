<script type="text/javascript" charset="utf-8">    
    new Alfresco.RM_Audit('${htmlid}-audit').setOptions(
    {
       <#if (nodeRef?exists)>nodeRef: '${nodeRef}',</#if>          
       siteId: "${page.url.templateArgs.site}",
       containerId: "${template.properties.container!"documentLibrary"}",
       viewMode: Alfresco.RM_Audit.VIEW_MODE_COMPACT
    }).setMessages(${messages});
</script>
<div id="${htmlid}-audit">
   <#-- for a specified noderef -->
   <#if (page.url.args.nodeName??)>
      <h1>${msg("label.title-for", page.url.args.nodeName)?html}</h1>
   <#else>
      <h1>${msg("label.title")}</h1>
   </#if>
   <div class="auditActions">
      <button id="${htmlid}-audit-export" name="audit-export" class="audit-export">${msg("label.button-export")}</button>
      <button id="${htmlid}-audit-file-record" name="audit-file-record" class="audit-file-record">${msg("label.button-file-record")}</button>            
   </div>
   <div class="audit-info">
   <#-- only for full log (not noderef) -->
   <#if (!page.url.args.nodeName??)>
      <span class="label">${msg('label.property')}:</span>
      <span class="value">${msg('label.all')}</span>
      <span class="label">${msg('label.user')}:</span>
      <span class="value">${msg('label.all')}</span>
      <span class="label">${msg('label.event')}:</span>
      <span class="value">${msg('label.all')}</span>
   </#if>
   </div>
   <#list auditStatus.entries as x>
      <div class="audit-entry">
         <div class="audit-entry-header">
            <span class="label">${msg('label.timestamp')}:</span>
            <span class="value">${x.timestampDate?datetime?string("EEE MMM dd yyyy HH:mm:ss 'GMT'Z")}</span>
            <span class="label">${msg('label.user')}:</span>
            <span class="value">${x.fullName?html}</span>
            <span class="label">${msg('label.event')}:</span>
            <span class="value">${x.event?html}</span>
         </div>
         <div class="audit-entry-node">
            <span class="label">${msg('label.identifier')}:</span><span class="value">${x.identifier?html}</span>
            <span class="label">${msg('label.type')}:</span><span class="value">${x.nodeType?html}</span>
            <span class="label">${msg('label.location')}:</span><span class="value">${x.path?html}</span>
         </div>
         <#if (x.changedValues?size >0)>
            <table class="changed-values-table" cellspacing="0">
               <thead>
                  <tr>
                     <th>${msg('label.property')}</th>
                     <th>${msg('label.previous-value')}</th>
                     <th>${msg('label.new-value')}</th>
                  </tr>
               </thead>
               <tbody>
                  <#list x.changedValues as v>
                  <tr>
                     <td>${v.name?html}</td>
                  <#if (v.previous == "")>
                     <td>${msg('label.no-previous')?html}</td>
                  <#else>
                     <td>${v.previous?html}</td>
                  </#if>
                     <td>${v.new?html}</td>
                  </tr>
                  </#list>
               </tbody>
            </table>      
         </#if>
      </div>
   </#list>
</div>