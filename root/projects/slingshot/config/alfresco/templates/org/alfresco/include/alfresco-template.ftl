<#import "../import/alfresco-common.ftl" as common />

<#-- Global flags retrieved from share-config (or share-config-custom) -->
<#assign DEBUG=(common.globalConfig("client-debug", "false") = "true")>
<#assign AUTOLOGGING=(common.globalConfig("client-debug-autologging", "false") = "true")>
<#-- allow theme to be specified in url args - helps debugging themes -->
<#assign theme = (page.url.args.theme!theme)?html />
<#-- Portlet container detection -->
<#assign PORTLET=(context.attributes.portletHost!false)>

<#-- Look up page title from message bundles where possible -->
<#assign pageTitle = page.title />
<#if page.titleId??>
   <#assign pageTitle = (msg(page.titleId))!page.title>
</#if>
<#if context.properties["page-titleId"]??>
   <#assign pageTitle = msg(context.properties["page-titleId"])>
</#if>

<#--
   JavaScript minimisation via YUI Compressor.
-->
<#macro script type src>
   <script type="${type}" src="${DEBUG?string(src, src?replace(".js", "-min.js"))}"></script>
</#macro>
<#--
   Stylesheets gathered and rendered using @import to workaround IEBug KB262161
-->
<#assign templateStylesheets = []>
<#macro link rel type href>
   <#assign templateStylesheets = templateStylesheets + [href]>
</#macro>
<#macro renderStylesheets>
   <style type="text/css" media="screen">
   <#list templateStylesheets as href>
      @import "${href}";
   </#list>
   </style>
</#macro>

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
   <title>${msg("page.title", pageTitle)}</title>
   <meta http-equiv="X-UA-Compatible" content="Edge" />
</#if>

<!-- Shortcut Icons -->
   <link rel="shortcut icon" href="${url.context}/res/favicon.ico" type="image/vnd.microsoft.icon" /> 
   <link rel="icon" href="${url.context}/res/favicon.ico" type="image/vnd.microsoft.icon" />

<!-- Site-wide YUI Assets -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/reset-fonts-grids/reset-fonts-grids.css" />
   <#if theme = 'default'>
      <@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/assets/skins/default/skin.css" />
   <#else>
      <@link rel="stylesheet" type="text/css" href="${url.context}/res/themes/${theme}/yui/assets/skin.css" />   
   </#if>
<#-- Selected components preloaded here for better UI experience. -->
<#if DEBUG>
   <script type="text/javascript" src="${url.context}/res/js/log4javascript.v1.4.1.js"></script>
<!-- Common YUI components: DEBUG -->
   <script type="text/javascript" src="${url.context}/res/yui/yahoo/yahoo-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/event/event-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/dom/dom-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/dragdrop/dragdrop-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/animation/animation-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/logger/logger-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/connection/connection-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/element/element-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/get/get-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/yuiloader/yuiloader-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/button/button-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/container/container-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/menu/menu-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/json/json-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/selector/selector-debug.js"></script>
<!-- YUI Patches -->
   <script type="text/javascript" src="${url.context}/res/yui/yui-patch.js"></script>
   <script type="text/javascript">//<![CDATA[
      YAHOO.util.Event.throwErrors = true;
   //]]></script>
<#else>
<!-- Common YUI components: RELEASE concatenated -->
   <script type="text/javascript" src="${url.context}/res/js/yui-common.js"></script>
</#if>

<!-- Site-wide Common Assets -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/base.css" />
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/yui-layout.css" />   
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/themes/${theme}/presentation.css" />
   <@script type="text/javascript" src="${url.context}/res/js/bubbling.v2.1.js"></@script>
   <script type="text/javascript">//<![CDATA[
      YAHOO.Bubbling.unsubscribe = function(layer, handler)
      {
         this.bubble[layer].unsubscribe(handler);
      }
   //]]></script>
   <@script type="text/javascript" src="${url.context}/res/js/flash/AC_OETags.js"></@script>
   <#-- NOTE: Do not attempt to load -min.js version of messages.js -->
   <script type="text/javascript" src="${url.context}/service/messages.js?locale=${locale}"></script>
   <script type="text/javascript">//<![CDATA[
      Alfresco.constants = Alfresco.constants || {};
      Alfresco.constants.DEBUG = ${DEBUG?string};
      Alfresco.constants.AUTOLOGGING = ${AUTOLOGGING?string};
      Alfresco.constants.PROXY_URI = window.location.protocol + "//" + window.location.host + "${url.context}/proxy/alfresco/";
      Alfresco.constants.PROXY_URI_RELATIVE = "${url.context}/proxy/alfresco/";
      Alfresco.constants.PROXY_FEED_URI = window.location.protocol + "//" + window.location.host + "${url.context}/proxy/alfresco-feed/";
      Alfresco.constants.THEME = "${theme}";
      Alfresco.constants.URL_CONTEXT = "${url.context}/";
      Alfresco.constants.URL_RESCONTEXT = "${url.context}/res/";
      Alfresco.constants.URL_PAGECONTEXT = "${url.context}/page/";
      Alfresco.constants.URL_SERVICECONTEXT = "${url.context}/service/";
      Alfresco.constants.URL_FEEDSERVICECONTEXT = "${url.context}/feedservice/";
      Alfresco.constants.USERNAME = "${user.name!""}";
      Alfresco.constants.SITE = "${(page.url.templateArgs.site!"")?js_string}";
      Alfresco.constants.PAGEID = "${(page.url.templateArgs.pageid!"")?js_string}";
      Alfresco.constants.PORTLET = ${(context.attributes.portletHost!false)?string};
      Alfresco.constants.PORTLET_URL = unescape("${(context.attributes.portletUrl!"")?js_string}");
   <#if PORTLET>
      document.cookie = "JSESSIONID=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=${url.context}";
   </#if>
   //]]></script>
   <@script type="text/javascript" src="${url.context}/res/js/alfresco.js"></@script>
   <@script type="text/javascript" src="${url.context}/res/js/forms-runtime.js"></@script>
   <@script type="text/javascript" src="${url.context}/res/js/share.js"></@script>
   <@common.uriTemplates />
   <@common.helpPages />
   <@common.htmlEditor htmlEditor="tinyMCE"/>
   
   <!-- Share Preference keys -->
   <script type="text/javascript">//<![CDATA[
      Alfresco.service.Preferences.FAVOURITE_DOCUMENTS = "org.alfresco.share.documents.favourites";
      Alfresco.service.Preferences.FAVOURITE_FOLDERS = "org.alfresco.share.folders.favourites";
      Alfresco.service.Preferences.FAVOURITE_SITES = "org.alfresco.share.sites.favourites";
      Alfresco.service.Preferences.IMAP_FAVOURITE_SITES = "org.alfresco.share.sites.imapFavourites";
      Alfresco.service.Preferences.COLLAPSED_TWISTERS = "org.alfresco.share.twisters.collapsed";
      Alfresco.service.Preferences.RULE_PROPERTY_SETTINGS = "org.alfresco.share.rule.properties";
   //]]></script>

<!-- Template Assets -->
<#nested>
<@renderStylesheets />

<!-- Component Assets -->
${head}

<!-- MSIE CSS fix overrides -->
   <!--[if lt IE 7]><link rel="stylesheet" type="text/css" href="${url.context}/res/css/ie6.css" /><![endif]-->
   <!--[if IE 7]><link rel="stylesheet" type="text/css" href="${url.context}/res/css/ie7.css" /><![endif]-->
   <!--[if IE 8]><link rel="stylesheet" type="text/css" href="${url.context}/res/css/ie8.css" /><![endif]-->
<#if !PORTLET>
</head>
</#if>
</#macro>


<#--
   Template "templateHtmlEditorAssets" macro.
   Loads wrappers for Rich Text editors.
-->
<#macro templateHtmlEditorAssets>
<!-- HTML Editor Assets -->
   <#-- NOTE: Do not attempt to load -min.js version of tiny_mce/tiny_mce.js -->
   <script type="text/javascript" src="${page.url.context}/res/modules/editors/tiny_mce/tiny_mce.js"></script>
   <@script type="text/javascript" src="${page.url.context}/res/modules/editors/tiny_mce.js"></@script>
   <@script type="text/javascript" src="${page.url.context}/res/modules/editors/yui_editor.js"></@script>
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