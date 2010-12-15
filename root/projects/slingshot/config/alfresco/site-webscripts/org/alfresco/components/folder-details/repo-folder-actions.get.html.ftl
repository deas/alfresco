<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.RepositoryFolderActions("${el}").setOptions(
   {
      <#if repositoryUrl??>repositoryUrl: "${repositoryUrl}",</#if>
      replicationUrlMapping: ${replicationUrlMappingJSON!"{}"}
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${el}-body" class="folder-actions">

   <div class="heading">${msg("heading")}</div>

   <div class="doclist">
      <div id="${el}-actionSet" class="action-set"></div>
   </div>

   <!-- Action Set Templates -->
   <div style="display:none">
<#list actionSets?keys as key>
   <#assign actionSet = actionSets[key]>
      <div id="${el}-actionSet-${key}" class="action-set">
   <#list actionSet as action>
         <div class="${action.id}"><a rel="${action.permission!""}" href="${action.href}" class="${action.type}" title="${msg(action.label)}"><span>${msg(action.label)}</span></a></div>
   </#list>
      </div>
</#list>
   </div>

</div>