/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * 
 * @module surf-dynamic/constants/Url
 * @author Dave Draper
 */
define(["dojo/_base/lang"], 
        function(lang) {
   
   return {
      
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
      <#assign AUTOLOGGING=(globalConfig("client-debug-autologging", "false") = "true")>
      
      <#-- Portlet container detection -->
      <#assign PORTLET=(context.attributes.portletHost!false)>

      DEBUG: ${DEBUG?string},
      AUTOLOGGING: ${AUTOLOGGING?string},
      PROXY_URI: window.location.protocol + "//" + window.location.host + "${url.context}/proxy/alfresco/",
      PROXY_URI_RELATIVE: "${url.context}/proxy/alfresco/",
      PROXY_FEED_URI: window.location.protocol + "//" + window.location.host + "${url.context}/proxy/alfresco-feed/",
      URL_CONTEXT: "${url.context}/",
      URL_RESCONTEXT: "${url.context}/res/",
      URL_PAGECONTEXT: "${url.context}/page/",
      URL_SERVICECONTEXT: "${url.context}/service/",
      URL_FEEDSERVICECONTEXT: "${url.context}/feedservice/",
      USERNAME: "${(user.name!"")?js_string}",
      PORTLET: ${PORTLET?string},
      PORTLET_URL: unescape("${(context.attributes.portletUrl!"")?js_string}"),
      JS_LOCALE: "${locale}",
      CSRF_POLICY: {
         enabled: ${((config.scoped["CSRFPolicy"]["filter"].getChildren("rule")?size > 0)?string)!false},
         cookie: "${config.scoped["CSRFPolicy"]["client"].getChildValue("cookie")!""}",
         header: "${config.scoped["CSRFPolicy"]["client"].getChildValue("header")!""}",
         parameter: "${config.scoped["CSRFPolicy"]["client"].getChildValue("parameter")!""}",
         properties: {
            <#if config.scoped["CSRFPolicy"]["properties"]??>
               <#assign csrfProperties = (config.scoped["CSRFPolicy"]["properties"].children)![]>
               <#list csrfProperties as csrfProperty>
            ${csrfProperty.name?js_string}: "${(csrfProperty.value!"")?js_string}"<#if csrfProperty_has_next>,</#if>
               </#list>
            </#if>
         }
      },
      URI_TEMPLATES: {
         <#list config.scoped["UriTemplate"]["uri-templates"].childrenMap["uri-template"] as c>
            "${c.attributes["id"]}": "${c.value}"<#if c_has_next>,</#if>
         </#list>
      }
   };
});