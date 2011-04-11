<#if docName??>
   <#include "../../include/alfresco-macros.lib.ftl" />
   <#assign el=args.htmlid?js_string/>
   <script type="text/javascript">//<![CDATA[
      new Alfresco.RecordsDocumentReferences("${el}").setOptions(
      {
         siteId: "${site?js_string}",
         containerId: "${container?js_string}",
         nodeRef : "${nodeRef?js_string}"
      }).setMessages(
         ${messages}
      );
   //]]></script>

   <div id="${el}-body" class="document-references document-details-panel">

      <h2 id="${el}-heading" class="thin dark">
         ${msg("label.heading")}
         <#if allowManageReferences>
         <span class="alfresco-twister-actions">
            <a href="${siteURL(("rmreferences?nodeRef="+nodeRef+"&parentNodeRef="+parentNodeRef+"&docName="+docName)?js_string)}" class="edit" title="${msg("label.manage-references")}">&nbsp;</a>
         </span>
         </#if>
      </h2>

      <div class="reflist panel-body">
         <h3 class="thin dark">${msg('label.references-to-this')}</h3>
         <hr/>
      <#if (references.toThisNode?size > 0)>
         <ul>
         <#list references.toThisNode as ref>
            <li>${ref.label?html} <a href="${siteURL("document-details?nodeRef="+ref.targetRef)}"><span>${docNames.to[ref_index]}</span></a></li>
         </#list>
         </ul>
      <#else>
         <p class="no-ref-messages">${msg('message.no-messages')}</p>
      </#if>
         <h3 class="thin dark">${msg('label.references-from-this')}</h3>
         <hr/>
      <#if (references.fromThisNode?size > 0)>
         <ul>
         <#list references.fromThisNode as ref>
            <li>${ref.label?html} <a href="${siteURL("document-details?nodeRef="+ref.targetRef)}"><span>${docNames.from[ref_index]}</span></a></li>
         </#list>
         </ul>
      <#else>
         <p class="no-ref-messages">${msg('message.no-messages')}</p>
      </#if>
      </div>

      <script type="text/javascript">//<![CDATA[
         Alfresco.util.createTwister("${el}-heading", "RecordsDocumentActions");
      //]]></script>

   </div>
</#if>