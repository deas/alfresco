<#include "../component.head.inc">
<#-- Stylesheets gathered and rendered using @import to workaround IEBug KB262161 -->
<#assign templateStylesheets = []>
<#macro link rel type href>
   <#assign templateStylesheets = templateStylesheets + [href]>
</#macro>
<#--
   RESOURCES
-->
<@markup id="favicons">
<#if !PORTLET>
   <!-- Icons -->
   <link rel="shortcut icon" href="${url.context}/res/favicon.ico" type="image/vnd.microsoft.icon" />
   <link rel="icon" href="${url.context}/res/favicon.ico" type="image/vnd.microsoft.icon" />
</#if>
</@markup>

<@markup id="yui">
   <!-- YUI -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/reset-fonts-grids/reset-fonts-grids.css" />
   <@link rel="stylesheet" type="text/css" href="${page.url.context}/res/yui/columnbrowser/assets/columnbrowser.css" />
   <@link rel="stylesheet" type="text/css" href="${page.url.context}/res/yui/columnbrowser/assets/skins/default/columnbrowser-skin.css" />
   <#if theme = 'default'>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/assets/skins/default/skin.css" />
   <#else>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/themes/${theme}/yui/assets/skin.css" />
   </#if>
   <#if DEBUG>
   <script type="text/javascript" src="${url.context}/res/js/log4javascript.v1.4.1.js"></script>
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
   <script type="text/javascript" src="${url.context}/res/yui/datasource/datasource-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/autocomplete/autocomplete-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/paginator/paginator-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/datatable/datatable-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/history/history-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/treeview/treeview-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/autocomplete/autocomplete-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/yui-patch.js"></script>
   <script type="text/javascript">//<![CDATA[
      YAHOO.util.Event.throwErrors = true;
   //]]></script>
   <#else>
   <script type="text/javascript" src="${url.context}/res/js/yui-common.js"></script>
   </#if>
   <@script type="text/javascript" src="${url.context}/res/js/bubbling.v2.1.js"></@script>
   <script type="text/javascript">//<![CDATA[
      YAHOO.Bubbling.unsubscribe = function(layer, handler)
      {
         this.bubble[layer].unsubscribe(handler);
      };
   //]]></script>
</@>

<@markup id="messages">
   <!-- Common i18n msg properties -->
   <script type="text/javascript" src="${url.context}/service/messages.js?locale=${locale}"></script>
</@markup>

<@markup id="alfrescoConstants">
   <!-- Alfresco web framework constants -->
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
      Alfresco.constants.USERNAME = "${(user.name!"")?js_string}";
      Alfresco.constants.SITE = "<#if page??>${(page.url.templateArgs.site!"")?js_string}</#if>";
      Alfresco.constants.PAGEID = "<#if page??>${(page.url.templateArgs.pageid!"")?js_string}</#if>";
      Alfresco.constants.PORTLET = ${PORTLET?string};
      Alfresco.constants.PORTLET_URL = unescape("${(context.attributes.portletUrl!"")?js_string}");
      Alfresco.constants.JS_LOCALE = "${locale}";
   <#if PORTLET>
      document.cookie = "JSESSIONID=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=${url.context}";
   </#if>
   //]]></script>
</@>

<@markup id="alfrescoResources">
   <!-- Alfresco web framework common resources -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/base.css" />
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/yui-layout.css" />
   <@script type="text/javascript" src="${url.context}/res/js/flash/AC_OETags.js"></@script>
   <@script type="text/javascript" src="${url.context}/res/js/alfresco.js"></@script>
   <script type="text/javascript" src="${url.context}/res/modules/editors/tiny_mce/tiny_mce${DEBUG?string("_src", "")}.js"></script>
   <@script type="text/javascript" src="${url.context}/res/modules/editors/tiny_mce.js"></@script>
   <@script type="text/javascript" src="${url.context}/res/modules/editors/yui_editor.js"></@script>
   <@script type="text/javascript" src="${url.context}/res/js/forms-runtime.js"></@script>
</@>

<@markup id="shareConstants">
   <!-- Share Constants -->
   <script type="text/javascript">//<![CDATA[
      Alfresco.service.Preferences.FAVOURITE_DOCUMENTS = "org.alfresco.share.documents.favourites";
      Alfresco.service.Preferences.FAVOURITE_FOLDERS = "org.alfresco.share.folders.favourites";
      Alfresco.service.Preferences.FAVOURITE_SITES = "org.alfresco.share.sites.favourites";
      Alfresco.service.Preferences.IMAP_FAVOURITE_SITES = "org.alfresco.share.sites.imapFavourites";
      Alfresco.service.Preferences.COLLAPSED_TWISTERS = "org.alfresco.share.twisters.collapsed";
      Alfresco.service.Preferences.RULE_PROPERTY_SETTINGS = "org.alfresco.share.rule.properties";
      Alfresco.constants.URI_TEMPLATES =
      {
         <#list config.scoped["UriTemplate"]["uri-templates"].childrenMap["uri-template"] as c>
         "${c.attributes["id"]}": "${c.value}"<#if c_has_next>,</#if>
         </#list>
      };
      Alfresco.constants.HELP_PAGES =
      {
         <#list config.scoped["HelpPages"]["help-pages"].children as c>
         "${c.name}": "${c.value}"<#if c_has_next>,</#if>
         </#list>
      };
      Alfresco.constants.HTML_EDITOR = 'tinyMCE';
   //]]></script>
</@>

<@markup id="shareResources">
   <!-- Share resources -->
   <@script type="text/javascript" src="${url.context}/res/js/share.js"></@script>
   <@script type="text/javascript" src="${page.url.context}/res/js/lightbox.js"></@script>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/themes/${theme}/presentation.css" />
</@>

   <#if (templateStylesheets?size > 0)>
   <!-- Common stylesheets gathered to workaround IEBug KB262161 -->
   <style type="text/css" media="screen">
      <#list templateStylesheets as href>
      @import "${href}";
      </#list>
   </style>
   </#if>

<@markup id="resources">
   <#-- Use this "markup id" to add in a extension's resources -->
</@>
