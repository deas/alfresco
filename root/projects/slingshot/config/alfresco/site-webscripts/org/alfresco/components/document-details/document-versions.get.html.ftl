<script type="text/javascript">//<![CDATA[
new Alfresco.DocumentVersions("${args.htmlid}").setOptions(
{
   versions: [
<#list versions as version>
      {
         label: "${version.label}",
         createdDate: "${version.createdDate}"
      }<#if (version_has_next)>,</#if>
</#list>
   ],
   filename: "${filename!}",
   nodeRef: "${nodeRef!}"
}).setMessages(
   ${messages}
);
//]]></script>

<div id="${args.htmlid}-body" class="document-versions hidden">

   <div class="info-section">

      <div class="heading">${msg("header.versionHistory")}</div>

      <#list versions as version>
         <#if version_index == 1>
            <div class="info-sub-section">
               <span class="meta-heading">${msg("section.olderVersion")}</span>
            </div>
         </#if>
         <a id="${args.htmlid}-expand-a-${version_index}" class="info more <#if version_index != 0>collapsed<#else>expanded</#if>" href="#">
            <span class="meta-section-label theme-color-1">${msg("label.label")} ${version.label}</span>
            <span id="${args.htmlid}-createdDate-span-${version_index}" class="meta-value">&nbsp;</span>
         </a>
         <div id="${args.htmlid}-moreVersionInfo-div-${version_index}" class="moreInfo" <#if version_index != 0>style="display: none;"</#if>>
            <div class="info">
               <span class="meta-label">${msg("label.creator")}</span>
               <span class="meta-value">${version.creator.firstName?html} ${version.creator.lastName?html}</span>
            </div>
            <div class="info">
               <span class="meta-label">${msg("label.description")}</span>
               <span class="meta-value">${version.description?html}</span>
            </div>
            <div class="actions">
               <span><a href="${url.context}/proxy/alfresco${version.downloadURL}" class="download">${msg("link.download")}</a></span>
               <#if version_index != 0>
                  <span class="hidden"><a id="${args.htmlid}-revert-a-${version_index}" class="revert" href="#">${msg("link.revert")}</a></span>
               </#if>
            </div>
         </div>
      </#list>

   </div>

</div>
