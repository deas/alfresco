<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <head>
      <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
      <title>Alfresco Share</title>
      <@outputCSS/>
      
      <#-- Load up the core Alfresco messages as is currently done for each page in Share. This makes all global messages
           available in the JavaScript object "Alfresco.messages.global" -->
      <@markup id="messages">
         <#-- Common i18n msg properties -->
         <@generateMessages type="text/javascript" src="${url.context}/service/messages.js" locale="${locale}"/>
      </@markup>
      
      <#-- Bootstrap Dojo -->
      <@createComponent scope="global" regionId="bootstrap" sourceId="global" uri="/surf/dojo/bootstrap"/>
      <@region scope="global" id="bootstrap" chromeless="true"/>
      <#-- This is a markup section for any global JavaScript constants that might be required -->
      <@markup id="constants">
         <script type="text/javascript">
            Alfresco.constants = Alfresco.constants || {};
            <#-- Alfresco.constants.DEBUG = ${DEBUG?string}; -->
            <#-- Alfresco.constants.AUTOLOGGING = ${AUTOLOGGING?string}; -->
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
            <#-- Alfresco.constants.PORTLET = ${PORTLET?string}; -->
            <#-- Alfresco.constants.PORTLET_URL = unescape("${(context.attributes.portletUrl!"")?js_string}"); -->
            Alfresco.constants.JS_LOCALE = "${locale}";
            Alfresco.constants.USERPREFERENCES = "${preferences?js_string}";
            
            
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
            <#if config.scoped["Social"]["quickshare"].getChildValue("url")??>
            Alfresco.constants.QUICKSHARE_URL = "${config.scoped["Social"]["quickshare"].getChildValue("url")?replace("{context}", url.context)?js_string}";
            </#if>
            <#if config.scoped["Social"]["linkshare"].childrenMap["action"]??>
            Alfresco.constants.LINKSHARE_ACTIONS = [
               <#list config.scoped["Social"]["linkshare"].childrenMap["action"] as a>
               {
               id: "${a.attributes["id"]}", type: "${a.attributes["type"]}", index: ${a.attributes["index"]},
               params: { <#list a.childrenMap["param"] as p>"${p.attributes["name"]}": "${p.value?js_string}"<#if p_has_next>,</#if></#list> }
               }<#if a_has_next>,</#if>
               </#list>
            ];
            </#if>
         </script>
      </@>
      <@outputJavaScript/>
      <@link rel="stylesheet" type="text/css" href="${url.context}/${sitedata.getDojoPackageLocation('dijit')}/themes/claro/claro.css" group="share" forceAggregation="true"/>
      <@link rel="stylesheet" type="text/css" href="${url.context}/res/js/alfresco/css/global.css" group="share" forceAggregation="true"/>
   </head>
   <#-- PLEASE NOTE: The "yui-skin-default" class is just for wrapping Share widgets. Ideally we wouldn't include this
                     and there maybe the argument that we don't include it here or have the wrapped Share class add
                     it to the body as necessary. It was discovered that without it certain CSS selectors weren't being
                     applied - notably on the DocumentList. -->
   <body class="claro alfresco-share yui-skin-default">
      <div id="content">
         <#-- Here we create the a component purely to serve the WebScript requested. If the Component already exists then 
              it won't get recreated. This allows us to never need to create components -->
         <#assign regionId = page.url.templateArgs.webscript?replace("/", "-")/>
         <@createComponent scope="global" regionId="${regionId}" sourceId="global" uri="/${page.url.templateArgs.webscript}"/>
         <@region scope="global" id="${regionId}" chromeless="true"/>
      </div>
   </body>
</html>