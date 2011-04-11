<#if documentDetailsJSON??>
   <#assign el=args.htmlid?js_string>
   <script type="text/javascript">//<![CDATA[
      new Alfresco.DocumentActions("${el}").setOptions(
      {
         nodeRef: "${nodeRef?js_string}",
         siteId: <#if site??>"${site?js_string}"<#else>null</#if>,
         containerId: "${container?js_string}",
         vtiServer: ${vtiServer},
         replicationUrlMapping: ${replicationUrlMappingJSON!"{}"},
         documentDetails: ${documentDetailsJSON},
         repositoryBrowsing: ${(rootNode??)?string}
      }).setMessages(
         ${messages}
      );
   //]]></script>

   <div id="${el}-body" class="document-actions document-details-panel">

      <h2 id="${el}-heading" class="thin dark">
         ${msg("heading")}
      </h2>

      <div class="doclist">
         <div id="${el}-actionSet" class="action-set"></div>
      </div>

      <script type="text/javascript">//<![CDATA[
         Alfresco.util.createTwister("${el}-heading", "DocumentActions");
      //]]></script>

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
</#if>
