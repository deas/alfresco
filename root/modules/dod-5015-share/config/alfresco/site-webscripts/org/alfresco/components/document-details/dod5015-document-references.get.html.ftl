<script type="text/javascript">//<![CDATA[
   new Alfresco.RecordsDocumentReferences("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${template.properties.container!"documentLibrary"}",
      nodeRef : "${page.url.args.nodeRef?js_string}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="document-references">
   <div class="heading">${msg("label.heading")}</div>
   <div class="manageRefs"><span id="${args.htmlid}-manageRefs-button" class="yui-button yui-push-button"><span class="first-child"><button>${msg("label.manage-references")}</button></span></span></div>
   <div class="reflist">
      <h3>${msg('label.references-to-this')}</h3>
   <#if (references.toThisNode?size > 0)>
      <ul>
      <#list references.toThisNode as ref>
         <li>${ref.label?html} <a href="${url.context}/page/site/${page.url.templateArgs.site}/document-details?nodeRef=${ref.targetRef}"><span>${docNames.to[ref_index]}</span></a></li>
      </#list>
      </ul>
   <#else>
      <p class="no-ref-messages">${msg('message.no-messages')}</p>
   </#if>
      <h3>${msg('label.references-from-this')}</h3>
   <#if (references.fromThisNode?size > 0)>
      <ul>
      <#list references.fromThisNode as ref>
         <li>${ref.label?html} <a href="${url.context}/page/site/${page.url.templateArgs.site}/document-details?nodeRef=${ref.targetRef}"><span>${docNames.from[ref_index]}</span></a></li>
      </#list>
      </ul>
   <#else>
      <p class="no-ref-messages">${msg('message.no-messages')}</p>
   </#if>
   </div>
</div>