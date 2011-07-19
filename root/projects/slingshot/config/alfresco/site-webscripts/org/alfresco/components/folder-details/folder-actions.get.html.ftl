<#if folderDetailsJSON??>
   <#assign el=args.htmlid?js_string>
   <script type="text/javascript">//<![CDATA[
      new Alfresco.FolderActions("${el}").setOptions(
      {
         nodeRef: "${nodeRef?js_string}",
         siteId: <#if site??>"${site?js_string}"<#else>null</#if>,
         containerId: "${container?js_string}",
         <#if repositoryUrl??>repositoryUrl: "${repositoryUrl}",</#if>
         replicationUrlMapping: ${replicationUrlMappingJSON!"{}"},
         repositoryBrowsing: ${(rootNode??)?string},
         folderDetails: ${folderDetailsJSON}
      }).setMessages(
         ${messages}
      );
   //]]></script>

   <div id="${el}-body" class="folder-actions folder-details-panel">
      <h2 id="${el}-heading" class="thin dark">
         ${msg("heading")}
      </h2>
      <div class="doclist">
         <div id="${el}-actionSet" class="action-set"></div>
      </div>
   </div>

   <script type="text/javascript">//<![CDATA[
      Alfresco.util.createTwister("${el}-heading", "FolderActions");
   //]]></script>
</#if>