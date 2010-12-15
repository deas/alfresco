<#include "../../include/alfresco-macros.lib.ftl" />
<#import "header.inc.ftl" as header>
<#assign siteActive = page.url.templateArgs.site??>
<#assign id = args.htmlid>
<#assign jsid = id?replace("-", "_")>
<#assign logo=msg("header.logo")><#if logo="header.logo"><#assign logo="app-logo.png"></#if>
<#if !user.isGuest>
<script type="text/javascript">//<![CDATA[
   var ${jsid} = new Alfresco.component.Header("${id}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      siteTitle: "${siteTitle?js_string}",
      minSearchTermLength: ${args.minSearchTermLength!config.scoped["Search"]["search"].getChildValue("min-search-term-length")},
      tokens:
      {
         site: "${page.url.templateArgs.site!""}",
         pageid: "${page.url.templateArgs.pageid!""}",
         userid: "${user.name?js_string}"
      }
   }).setMessages(${messages});
   Alfresco.util.createTwister.collapsed = "${collapsedTwisters?js_string}";
//]]></script>
<div class="header">
   <span class="header-left">
      <span class="logo">
         <a href="#" onclick="${jsid}.showAboutShare(); return false;"><img src="${url.context}/res/themes/${theme}/images/${logo}" alt="Alfresco Share" /></a>
      </span>
      <span class="logo-spacer">&nbsp;</span>
      <span id="${id}-appItems" class="app-items hidden"><@header.renderItems config.global.header.appItems id "app" /></span>
   </span>
<script type="text/javascript">//<![CDATA[
   ${jsid}.setAppItems([${header.js}]);
//]]></script>

   <span id="${id}-userItems" class="user-items">
      <div class="user-items-wrapper">
         <@header.renderItems config.global.header.userItems id "user" />
      </div>
      <div class="search-box">
         <span id="${id}-search_more" class="yui-button yui-menu-button">
            <span class="first-child" style="background-image: url(${url.context}/res/components/images/header/search-menu.png)">
               <button type="button" title="${msg("header.search.description")}" tabindex="0"></button>
            </span>
         </span>
         <input id="${id}-searchText" type="text" maxlength="1024" />
      </div>
      <div id="${id}-searchmenu_more" class="yuimenu yui-overlay yui-overlay-hidden">
         <div class="bd">
            <ul class="first-of-type">
               <li><span style="background-image: url(${url.context}/res/components/images/header/advanced-search.png)"><a title="${msg("header.advanced-search.description")}" href="${siteURL("advsearch")}">${msg("header.advanced-search.label")}</a></span></li>
            </ul>
         </div>
      </div>
   </span>
<script type="text/javascript">//<![CDATA[
   ${jsid}.setUserItems([${header.js}]);
//]]></script>
</div>
<#else>
<div class="header">
   <span class="header-left">
      <span class="logo">
         <a href="#" onclick="${jsid}.showAboutShare(); return false;"><img src="${url.context}/res/themes/${theme}/images/${logo}" alt="Alfresco Share" /></a>
      </span>
      <span class="logo-spacer">&nbsp;</span>
   </span>
</div>
</#if>
<div class="clear"></div>