<#--
   Returns a URL to a site page given a relative URL.
   If the current page is within a Site context, that context is used for the generated link.
   The function understands that &amp; needs to be unescaped when in portlet mode.
-->
<#function siteURL relativeURL=page.url.uri?substring(page.url.context?length) siteId=page.url.templateArgs.site!"">
   <#assign portlet = context.attributes.portletHost!false>
   <#assign portlet_url = (context.attributes.portletUrl!"")>
   <#assign site_url = relativeURL>

   <#if (siteId?length > 0)>
      <#assign site_url = "site/${siteId}/${site_url}">
   </#if>

   <#if site_url?starts_with("/")><#assign site_url = site_url?substring(1)></#if>
   <#if !site_url?starts_with("page/")><#assign site_url = ("page/" + site_url)></#if>
   <#assign site_url = "/" + site_url>

   <#if portlet>
      <#assign site_url = portlet_url?replace("%24%24scriptUrl%24%24", site_url?replace("&amp;", "&")?url)>
   <#else>
      <#assign site_url = url.context + site_url>
   </#if>

   <#return site_url>
</#function>

<#--
   I18N Message string using an array of tokens as the second argument
-->
<#function msgArgs msgId msgTokens>
   <#if msgTokens??>
      <#if msgTokens?is_sequence>
         <#assign templateTokens><#list msgTokens as token>"${token?j_string}"<#if token_has_next>,</#if></#list></#assign>
         <#assign templateSource = r"${msg(msgId," + templateTokens + ")}">
         <#assign inlineTemplate = [templateSource, "msgArgsTemplate"]?interpret>
         <#assign returnValue><@inlineTemplate /></#assign>
         <#return returnValue />
      </#if>
      <#return msg(msgId, msgTokens) />
   </#if>
   <#return msg(msgId) />
</#function>
