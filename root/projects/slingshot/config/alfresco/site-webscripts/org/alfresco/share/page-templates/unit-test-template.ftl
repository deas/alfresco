<#import "./hybrid-common.ftl" as common />
<#--
   CONSTANTS & HELPERS
-->
<#-- Global flags retrieved from share-config (or share-config-custom) -->
<#assign DEBUG=(common.globalConfig("client-debug", "false") = "true")>
<#assign AUTOLOGGING=(common.globalConfig("client-debug-autologging", "false") = "true")>
<#-- allow theme to be specified in url args - helps debugging themes -->
<#assign theme = (page.url.args.theme!theme)?html />
<#-- Portlet container detection -->
<#assign PORTLET=(context.attributes.portletHost!false)>
<#--
   UTILITY METHODS
   - <@script> & <@link> macros are now directives to improve resource handling
-->
<#--
   TEMPLATE MACROS
-->
<#--
   Template "templateHeader" macro.
   Includes preloaded YUI assets and essential site-wide libraries.
-->
<#macro templateHeader doctype="strict">
<#if !PORTLET>
   <#if doctype = "strict">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
   <#else>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
   </#if>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
   <title><@region id="head-title" scope="global" chromeless="true"/></title>
   <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
</#if>

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


<#if !PORTLET>
</head>
</#if>
</#macro>

<#--
   Template "templateBody" macro.
   Pulls in main template body.
-->
<#macro templateBody>
<#if !PORTLET>
<body id="Share" class="yui-skin-${theme} claro alfresco-share">
</#if>
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
   <#-- <@relocateJavaScript/> -->
   
   <#-- In portlet mode, Share doesn't own the <body> tag -->
   <script type="text/javascript">//<![CDATA[
      Alfresco.util.YUILoaderHelper.loadComponents(true);
      if (Alfresco.constants.PORTLET)
      {
         YUIDom.addClass(document.body, "yui-skin-${theme} alfresco-share");
      }
   //]]></script>
<#if !PORTLET>
</body>
</html>
</#if>
</#macro>

<#--
   Template "templateHtmlEditorAssets" macro.
   @deprecated These files are now brought in for every page from the extendable components/resources.get.html webscript.
-->
<#macro templateHtmlEditorAssets></#macro>
