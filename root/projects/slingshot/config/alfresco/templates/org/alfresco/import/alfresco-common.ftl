<#macro uriTemplates>
   <#assign uriConfig = config.scoped["UriTemplate"]["uri-templates"]>
   <script type="text/javascript">//<![CDATA[
      Alfresco.constants.URI_TEMPLATES =
      {
   <#list uriConfig.childrenMap["uri-template"] as c>
         "${c.attributes["id"]}": "${c.value}"<#if c_has_next>,</#if>
   </#list>
      }
   //]]></script>
</#macro>

<#macro helpPages>
   <#assign helpConfig = config.scoped["HelpPages"]["help-pages"]>
   <script type="text/javascript">//<![CDATA[
      Alfresco.constants.HELP_PAGES =
      {
   <#list helpConfig.children as c>
         "${c.name}": "${c.value}"<#if c_has_next>,</#if>
   </#list>
      }
   //]]></script>
</#macro>

<#macro htmlEditor htmlEditor="YAHOO.widget.SimpleEditor">
   <script type="text/javascript">//<![CDATA[
      Alfresco.constants.HTML_EDITOR = '${htmlEditor}';
   //]]></script>
</#macro>

<#function globalConfig key default>
   <#if config.global.flags??>
      <#assign values = config.global.flags.childrenMap[key]>
      <#if values?? && values?is_sequence>
         <#return values[0].value>
      </#if>
   </#if>
   <#return default>
</#function>