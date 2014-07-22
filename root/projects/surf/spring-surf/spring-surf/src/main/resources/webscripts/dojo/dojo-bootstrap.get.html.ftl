<#assign webframeworkConfig = config.scoped["WebFramework"]["web-framework"]!>
<#if webframeworkConfig??>
   <#if webframeworkConfig.dojoEnabled>
      <@markup id="setDojoConfig">
         <script type="text/javascript">
            var appContext = "${url.context}";
         
            var dojoConfig = {
               baseUrl: "${url.context}${webframeworkConfig.dojoBaseUrl}",
               tlmSiblingOfDojo: false,
               async: true,
               parseOnLoad: false,
               packages: [
               <#assign packages = webframeworkConfig.dojoPackages>
               <#list packages?keys as name>
                  { name: "${name}", location: "${packages[name]}" }<#if name_has_next>,</#if>
               </#list>
               ]
            };
         </script>
      </@>
      <@markup id="loadDojo">
         <script type="text/javascript" src="${url.context}${webframeworkConfig.dojoBootstrapFile}"></script>
      </@>
   </#if>
</#if>
