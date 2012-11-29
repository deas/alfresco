<@markup id="widgets">
   <#if (attachmentsAssocs?size > 0)>
      <@inlineScript group="document-details">
         YAHOO.util.Event.onContentReady("${args.htmlid?js_string}-heading", function() {
            Alfresco.util.createTwister("${args.htmlid?js_string}-heading", "DocumentPublishing");
         });
      </@>
   </#if>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#if (attachmentsAssocs?size > 0)>
      <#assign el=args.htmlid?html>
      <div id="${el}-body" class="document-attachment document-details-panel">
         <h2 id="${el}-heading" class="thin dark">
            ${msg("header.attachments")}
         </h2>
         <div class="panel-body">
            <#list attachmentsAssocs as assoc>
               <div id="${args.htmlid}-assoc-div-${assoc_index}" class="moreInfo">
                  <div class="info">
                     <a class="theme-color-1" href="${url.context}/page/document-details?nodeRef=${assoc.nodeRef}" >${assoc.name?html}</a>
                  </div>
               </div>
            </#list>
         </div>
      </div>
      </#if>
   </@>
</@>