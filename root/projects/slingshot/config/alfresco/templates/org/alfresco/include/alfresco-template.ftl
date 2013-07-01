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
   <#-- This MUST be placed before the <@outputJavaScript> directive to ensure that the Alfresco namespace
        gets setup before any of the other Alfresco JavaScript dependencies try to make use of it. -->
   <@markup id="messages">
      <#-- Common i18n msg properties -->
      <@generateMessages type="text/javascript" src="${url.context}/service/messages.js" locale="${locale}"/>
   </@markup>
   <@markup id="dojoBootstrap">
      <@region scope="global" id="bootstrap" chromeless="true"/>
   </@>
   
   <#-- This is where the JavaScript and CSS dependencies will initially be added through the use of the 
        <@script> and <@link> directives. The JavaScript can be moved through the use 
        of the <@relocateJavaScript> directive (i.e. to move it to the end of the page). These directives 
        must be placed before directives that add dependencies to them otherwise those resources will
        be placed in the output of the ${head} variable (i.e. this applied to all usage of those directives
        in *.head.ftl files) -->
   <@outputJavaScript/>
   <@outputCSS/>
   
   <#-- Common Resources -->
   <@region id="head-resources" scope="global" chromeless="true"/>
   
   <#-- Template Resources (nested content from < @templateHeader > call) -->
   <#nested>
   
   <@markup id="resources">
   <#-- Additional template resources -->
   </@markup>

   <#-- Component Resources from .get.head.ftl files or from dependency directives processed before the
        <@outputJavaScript> and <@outputCSS> directives. -->
   ${head}

   <@markup id="ieStylesheets">
   <!-- MSIE CSS fix overrides -->
   <!--[if lt IE 7]><link rel="stylesheet" type="text/css" href='<@checksumResource src="${url.context}/res/css/ie6.css"/>'/><![endif]-->
   <!--[if IE 7]><link rel="stylesheet" type="text/css" href='<@checksumResource src="${url.context}/res/css/ie7.css"/>'/><![endif]-->
   </@markup>

   <@markup id="ipadStylesheets">
   <#assign tabletCSS><@checksumResource src="${url.context}/res/css/tablet.css"/></#assign>
   <!-- Android & iPad CSS overrides -->
   <link media="only screen and (max-device-width: 1024px)" rel="stylesheet" type="text/css" href="${tabletCSS}"/>
   <script type="text/javascript">
      if (navigator.userAgent.indexOf(" Android ") !== -1 || navigator.userAgent.indexOf("iPad;") !== -1 || navigator.userAgent.indexOf("iPhone;") !== -1 )  document.write("<link rel='stylesheet' type='text/css' href='${tabletCSS}'/>");
   </script>
   </@markup>
<#if !PORTLET>
</head>
</#if>
</#macro>

<#--
   Template "templateBody" macro.
   Pulls in main template body.
-->
<#macro templateBody type="">
<#if !PORTLET>
<body id="Share" class="yui-skin-${theme} alfresco-share ${type} claro">
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
