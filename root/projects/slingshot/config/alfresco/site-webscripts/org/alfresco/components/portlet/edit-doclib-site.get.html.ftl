<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/portlet/edit-doclib-site.css" group="portlet"/>
</@>

<@markup id="js">
   <#-- No JavaScript Dependencies -->
</@>

<@markup id="widgets">
   <@createWidgets group="portlet"/>
   <@inlineScript group="portlet">
      YAHOO.lang.later(YAHOO.env.ua.ie > 0 ? 5000 : 2000, this, function() {
         Alfresco.util.Anim.fadeOut("${args.htmlid?js_string}-updated");
      });
   </@>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#include "../../include/alfresco-macros.lib.ftl" />
      <#assign id=args.htmlid?html>
      <#assign portlet = context.attributes.portletHost!false>
      <#assign mode=(context.attributes.mode!"view")?lower_case>
      <h1>${msg("label.header")}</h1>
      <#if !portlet>
         <div class="banner"><h3>${msg("message.portlet-only")}</h3></div>
      <#elseif mode = "view">
         <div class="banner"><h3>${msg("message.not-configured")}</h3></div>
      <#else>
         <#assign actionURL = siteURL()>
         <#assign isUpdated = (context.attributes.updated)!false>
         <#assign prefSiteId = (context.attributes.pref_siteId[0])!"">
         
         <div class="preferences">
            <form action="${actionURL}" method="post" accept-charset="utf-8">
               <fieldset>
                  <legend>Choose Site</legend>
                  <label for="${id}-site">${msg("label.site")}</label>
                  <select id="${id}-site" name="siteId">
                  <#list sites as site>
                     <option value="${site.shortName}"<#if site.shortName = prefSiteId> selected="selected"</#if>>${site.title?html}</option>
                  </#list>
                  </select>
                  <input type="submit" value="${msg("button.select")}">
               </fieldset>
            </form>
         </div>
         <#if isUpdated>
            <div id="${id}-updated" class="small-banner">${msg("message.updated")}</div>
         </#if>
      </#if>
   </@>
</@>