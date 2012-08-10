<#if (attachmentsAssocs?size > 0)>
<#assign el=args.htmlid?js_string>
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
   <script type="text/javascript">//<![CDATA[
       Alfresco.util.createTwister("${el}-heading", "DocumentPublishing");
    //]]></script>
</div>
</#if>

