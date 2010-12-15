<script type="text/javascript">//<![CDATA[
   new Alfresco.WikiList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs["site"]!""}",
      <#if error??>error: true,</#if>
      pages: [<#if pageList?? && pageList.pages?size &gt; 0><#list pageList.pages as p>"${p.name}"<#if p_has_next>, </#if></#list></#if>],
      <#if pageList?? && pageList.permissions??>
         <#assign permissions = pageList.permissions>
      <#else>
         <#assign permissions = {}>
      </#if>
      permissions:
      {
         "create": ${(permissions["create"]!false)?string}
      },
      filterId: "${(page.url.args.filter!"recentlyModified")?js_string}"
   });                                       
//]]></script>
<div id="${args.htmlid}-pagelist" class="yui-navset pagelist"> 
<#if pageList?? && pageList.pages?size &gt; 0>
<#list pageList.pages as p>
   <div class="wikipage <#if p.tags??><#list p.tags as t>wp-${t}<#if t_has_next> </#if></#list></#if>">
   <div class="actionPanel">
      <#if p.permissions.edit><div class="editPage"><a href="${url.context}/page/site/${page.url.templateArgs.site?html}/wiki-page?title=${p.name?url}&amp;action=edit&amp;listViewLinkBack=true">${msg("link.edit")}</a></div></#if>
      <div class="detailsPage"><a href="${url.context}/page/site/${page.url.templateArgs.site}/wiki-page?title=${p.name?url}&amp;action=details&amp;listViewLinkBack=true">${msg("link.details")}</a></div>
      <#if p.permissions.delete><div class="deletePage"><a href="#" class="delete-link" title="${p.name}">${msg("link.delete")}</a></div></#if>
   </div>
   <div class="pageTitle"><a class="pageTitle theme-color-1" href="${url.context}/page/site/${page.url.templateArgs.site?html}/wiki-page?title=${p.name?url}&amp;listViewLinkBack=true">${p.title}</a></div>
   <div class="publishedDetails">
      <span class="attrLabel">${msg("label.creator")}</span> <span class="attrValue"><a href="${url.context}/page/user/${p.createdByUser?url}/profile" class="theme-color-1" >${p.createdBy?html}</a></span>
      <span class="spacer">&nbsp;</span>
      <span class="attrLabel">${msg("label.createDate")}</span> <span class="attrValue">${p.createdOn?date("MMM dd yyyy, HH:mm:ss")?string(msg("date-format.defaultFTL"))}</span>
      <span class="spacer">&nbsp;</span>
      <span class="attrLabel">${msg("label.modifier")}</span> <span class="attrValue"><a href="${url.context}/page/user/${p.modifiedByUser?url}/profile" class="theme-color-1">${p.modifiedBy?html}</a></span>
      <span class="spacer">&nbsp;</span>
      <span class="attrLabel">${msg("label.modifiedDate")}</span> <span class="attrValue">${p.modifiedOn?date("MMM dd yyyy, HH:mm:ss")?string(msg("date-format.defaultFTL"))}</span>
   </div>
   <#assign pageCopy>${(p.text!"")?replace("</?[^>]+>", " ", "ir")}</#assign>
   <div class="pageCopy rich-content"><#if pageCopy?length &lt; 1000>${pageCopy}<#else>${pageCopy?substring(0, 1000)}...</#if></div>
   <#-- Display tags, if any -->
   <div class="pageTags">
      <span class="tagDetails">${msg("label.tags")}</span>
      <#if p.tags?? && p.tags?size &gt; 0><#list p.tags as tag><a href="#"  class="wiki-tag-link">${tag}</a><#if tag_has_next>,&nbsp;</#if></#list><#else>${msg("label.none")}</#if>
   </div>
   </div><#-- End of wikipage -->
</#list>
<#elseif error??>
   <div class="error-alt">${error}</div>
<#else>
   <div class="noWikiPages">${msg("label.noPages")}</div>
</#if>
</div>