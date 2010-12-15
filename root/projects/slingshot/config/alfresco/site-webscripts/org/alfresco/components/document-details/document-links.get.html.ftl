<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.DocumentLinks("${el}").setOptions(
   {
      <#if repositoryUrl??>repositoryUrl: "${repositoryUrl}"</#if>
   }).setMessages(${messages});
//]]></script>

<div id="${el}-body" class="document-links hidden">
   
   <div class="heading">${msg("document-links.heading")}</div>
   
   <!-- download link -->
   <div id="${el}-download" class="hidden">
      <div class="url-title"><label for="${el}-download-url">${msg("document-links.download")}</label></div>
      <input id="${el}-download-url" class="link-value" />
      <input id="${el}-download-button" type="button" class="copy-button" value="${msg("document-links.copy")}" />
   </div>
   
   <!-- document/view link -->
   <div id="${el}-view" class="hidden">
      <div class="url-title"><label for="${el}-view-url">${msg("document-links.view")}</label></div>
      <input id="${el}-view-url" class="link-value" />
      <input id="${el}-view-button" type="button" class="copy-button" value="${msg("document-links.copy")}" />
   </div>

<#if repositoryUrl??>
   <!-- webdav link -->
   <div id="${el}-webdav" class="hidden">
      <div class="url-title"><label for="${el}-webdav-url">${msg("document-links.webdav")}</label></div>
      <input id="${el}-webdav-url" class="link-value" />
      <input id="${el}-webdav-button" type="button" class="copy-button" value="${msg("document-links.copy")}" />
   </div>
</#if>

   <#-- cifs link (N/A)
   <div id="${el}-cifs" class="hidden">
      <div class="url-title"><label for="${el}-cifs-url">${msg("document-links.cifs")}</label></div>
      <input id="${el}-cifs-url" class="link-value" />
      <input id="${el}-cifs-button" type="button" class="copy-button" value="${msg("document-links.copy")}" />
   </div> -->

   <!-- page link -->
   <div id="${el}-page">
      <div class="url-title"><label for="${el}-page-url">${msg("document-links.page")}</label></div>
      <input id="${el}-page-url" class="link-value" />
      <input id="${el}-page-button" type="button" class="copy-button" value="${msg("document-links.copy")}" />
   </div>

</div>