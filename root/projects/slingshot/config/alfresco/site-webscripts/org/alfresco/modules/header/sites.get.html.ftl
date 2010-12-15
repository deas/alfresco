<#assign siteActive = args.siteId?? && (siteTitle?length > 0)>
<#assign id = args.htmlid?html>
<#assign id_js = id?js_string>
<script type="text/javascript">//<![CDATA[
   Alfresco.util.ComponentManager.get("${id_js}").setOptions(
   {
      siteTitle: "${siteTitle?js_string}",
      favouriteSites: {<#list favouriteSites as site>'${site.shortName}': '${site.title?js_string}'<#if site_has_next>,</#if></#list>}
   }).setMessages(${messages});
//]]></script>
<div id="${id}-sites-menu" class="yuimenu menu-with-icons">
   <div class="bd">
      <#assign favDisplay><#if favouriteSites?size != 0>block<#else>none</#if></#assign>
      <h6 id="${id}-favouritesContainer" class="favourite-sites" style="display: ${favDisplay}">${msg("label.favourite-sites")}</h6>
      <ul id="${id}-favouriteSites" class="favourite-sites-list separator" style="display: ${favDisplay}">
      <#if favouriteSites?size != 0>
         <#list favouriteSites as site>
         <li>
            <span><a href="${url.context}/page/site/${site.shortName}/dashboard">${site.title?html}</a></span>
         </li>
         </#list>
      <#else><li></li></#if>
      </ul>
      <#assign addFavDisplay><#if (siteActive && !currentSiteIsFav)>block<#else>none</#if></#assign>
      <ul id="${id}-addFavourite" class="add-favourite-menuitem separator" style="display: ${addFavDisplay}">
         <li style="display: ${addFavDisplay}">
            <span><a href="#" onclick='Alfresco.util.ComponentManager.get("${id_js}").addAsFavourite(); return false;'>${msg("label.add-favourite", siteTitle?html)}</a></span>
         </li>
      </ul>
      <ul class="site-finder-menuitem<#if !user.isGuest> separator</#if>">
         <li>
            <span><a href="${url.context}/page/site-finder">${msg("label.find-sites")}</a></span>
         </li>
      </ul>
      <ul class="create-site-menuitem">
         <li>
            <span><a href="#" onclick='Alfresco.util.ComponentManager.get("${id_js}").showCreateSite(); return false;'>${msg("label.create-site")}</a></span>
         </li>
      </ul>
   </div>
</div>