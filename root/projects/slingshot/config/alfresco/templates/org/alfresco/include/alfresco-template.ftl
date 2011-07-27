<#import "../import/alfresco-common.ftl" as common />
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
-->
<#-- Javascript import that brings in minified version in debug mode. -->
<#macro script type src>
   <script type="${type}" src="${DEBUG?string(src, src?replace(".js", "-min.js"))}"></script>
</#macro>
<#-- Stylesheets gathered and rendered using @import to workaround IEBug KB262161 -->
<#assign templateStylesheets = []>
<#macro link rel type href>
   <#assign templateStylesheets = templateStylesheets + [href]>
</#macro>
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
   <meta http-equiv="X-UA-Compatible" content="Edge" />
</#if>
   <@region id="head-resources" scope="global" chromeless="true"/>

   <!-- Template Resources (nested content from < @templateHeader > call) -->
   <#nested>
   <@markup id="resources">
   <!-- Additional template resources -->
   </@markup>

   <!-- Component Resources (from .get.head.ftl files) -->
   ${head}

   <#if (templateStylesheets?size > 0)>
   <!-- Template & Component Resources' stylesheets gathered to workaround IEBug KB262161 -->
   <style type="text/css" media="screen">
      <#list templateStylesheets as href>
      @import "${href}";
      </#list>
   </style>
   </#if>

   <@markup id="ieStylesheets">
   <!-- MSIE CSS fix overrides -->
   <!--[if lt IE 7]><link rel="stylesheet" type="text/css" href="${url.context}/res/css/ie6.css" /><![endif]-->
   <!--[if IE 7]><link rel="stylesheet" type="text/css" href="${url.context}/res/css/ie7.css" /><![endif]-->
   </@>

   <@markup id="ipadStylesheets">
   <!-- iPad CSS overrides -->
   <link media="only screen and (max-device-width: 1024px)" rel="stylesheet" type="text/css" href="${url.context}/res/css/ipad.css"/>
   </@>
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
<body id="Share" class="yui-skin-${theme} alfresco-share">
</#if>
   <div class="sticky-wrapper">
      <div id="doc3">
<#-- Template-specific body markup -->
<#nested>
      </div>
      <div class="sticky-push"></div>
   </div>
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
