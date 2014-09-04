<#assign theme = (page.url.args.theme!theme)?html />

<#macro templateHeader doctype="strict">

   <#if doctype = "strict">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
   <#else>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
   </#if>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
   <title>Aikau Unit Testing</title>
   <meta http-equiv="X-UA-Compatible" content="IE=Edge" />


   <@generateMessages type="text/javascript" src="${url.context}/service/messages.js" locale="${locale}"/>
   
   <#-- Bootstrap Dojo -->
   <@createComponent scope="global" regionId="bootstrap" sourceId="global" uri="/surf/dojo/bootstrap"/>
   <@region scope="global" id="bootstrap" chromeless="true"/>
   
   <@outputJavaScript/>
   <@outputCSS/>
   
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/themes/${theme}/presentation.css" group="template-common" />
   <@link rel="stylesheet" type="text/css" href="${url.context}/${sitedata.getDojoPackageLocation('dijit')}/themes/claro/claro.css" group="share" forceAggregation="true"/>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/js/alfresco/css/global.css" group="share" forceAggregation="true"/>

   <#-- Template Resources (nested content from < @templateHeader > call) -->
   <#nested>
   
   <#-- Component Resources from .get.head.ftl files or from dependency directives processed before the
        <@outputJavaScript> and <@outputCSS> directives. -->
   ${head}
</#macro>

<#--
   Template "templateBody" macro.
   Pulls in main template body.
-->
<#macro templateBody>
<body id="Share" class="yui-skin-${theme} claro alfresco-share">
<#nested>
</#macro>

<#--
   Template "templateFooter" macro.
   Pulls in template footer.
-->
<#macro templateFooter>
   <div class="sticky-footer">
<#-- Template-specific footer markup -->
<#nested>
   </div>
<#-- This function call MUST come after all other component includes. -->
   <div id="alfresco-yuiloader"></div>
</body>
</html>
</#macro>

<#--
   Template "templateHtmlEditorAssets" macro.
   @deprecated These files are now brought in for every page from the extendable components/resources.get.html webscript.
-->
<#macro templateHtmlEditorAssets></#macro>
