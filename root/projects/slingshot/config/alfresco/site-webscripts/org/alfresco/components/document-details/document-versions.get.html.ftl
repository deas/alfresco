<#if allowNewVersionUpload??>
   <#if workingCopyVersion??>
      <!-- No version component is displayed since it is a working copy -->
   <#else>
      <#assign el=args.htmlid?js_string>
      <script type="text/javascript">//<![CDATA[
      new Alfresco.DocumentVersions("${el}").setOptions(
      {
         nodeRef: "${nodeRef?js_string}",
         siteId: <#if site??>"${site?js_string}"<#else>null</#if>,
         containerId: "${container?js_string}",
         workingCopyVersion: <#if workingCopyVersion??>"${workingCopyVersion?js_string}"<#else>null</#if>,
         allowNewVersionUpload: ${allowNewVersionUpload?string}
      }).setMessages(
         ${messages}
      );
      //]]></script>

      <div id="${el}-body" class="document-versions document-details-panel">

         <h2 id="${el}-heading" class="thin dark">
            ${msg("header.versionHistory")}
            <#if allowNewVersionUpload>
               <span class="alfresco-twister-actions">
                  <a href="#" name=".onUploadNewVersionClick" class="${el} edit" title="${msg("label.newVersion")}">&nbsp;</a>
               </span>
            </#if>
         </h2>

         <div class="panel-body">

            <h3 class="thin dark">${msg("section.latestVersion")}</h3>
            <div id="${el}-latestVersion" class="current-version version-list"></div>
            <hr />
            <h3 class="thin dark">${msg("section.olderVersion")}</h3>
            <div id="${el}-olderVersions" class="version-list"></div>

         </div>

         <script type="text/javascript">//<![CDATA[
            Alfresco.util.createTwister("${el}-heading", "DocumentVersions");
         //]]></script>


      </div>
   </#if>
</#if>