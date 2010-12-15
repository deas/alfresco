<#function globalConfig key default>
   <#if config.global.flags??>
      <#assign values = config.global.flags.childrenMap[key]>
      <#if values?? && values?is_sequence>
         <#return values[0].value>
      </#if>
   </#if>
   <#return default>
</#function>

<#-- Global flags retrieved from web-framework-config-application -->
<#assign DEBUG=(globalConfig("client-debug", "false") = "true")>

<#--
   JavaScript minimisation via YUI Compressor
-->
<#macro script type src>
   <script type="${type}" src="${DEBUG?string(src, src?replace(".js", "-min.js"))}"></script>
</#macro>