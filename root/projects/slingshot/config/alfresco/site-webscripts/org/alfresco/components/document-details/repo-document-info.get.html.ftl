<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.RepositoryDocumentInfo("${el}").setMessages(${messages});
//]]></script>

<div id="${el}-body" class="document-info">
   
   <div class="info-section">
      <div class="heading">${msg("document-info.tags")}</div>
      <div id="${el}-tags"></div>
   </div>
   
</div>