<script type="text/javascript">//<![CDATA[
   new Alfresco.FolderActions("${args.htmlid}").setOptions(
   {
      <#if repositoryUrl??>repositoryUrl: "${repositoryUrl}",</#if>
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${template.properties.container!"documentLibrary"}",
      replicationUrlMapping: ${replicationUrlMappingJSON!"{}"},
      repositoryBrowsing: ${(rootNode??)?string}
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="folder-actions">

   <div class="heading">${msg("heading")}</div>

   <div class="doclist">
      <div id="${args.htmlid}-actionSet" class="action-set"></div>
   </div>

   <!-- Action Set Templates -->
   <div style="display:none">
<#list actionSets?keys as key>
   <#assign actionSet = actionSets[key]>
      <div id="${args.htmlid}-actionSet-${key}" class="action-set">
   <#list actionSet as action>
         <div class="${action.id}"><a rel="${action.permission!""}" href="${action.href}" class="${action.type}" title="${msg(action.label)}"><span>${msg(action.label)}</span></a></div>
   </#list>
      </div>
</#list>
   </div>

</div>