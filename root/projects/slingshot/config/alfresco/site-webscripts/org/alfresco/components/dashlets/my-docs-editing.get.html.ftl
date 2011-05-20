<#include "../../include/alfresco-macros.lib.ftl" />
<#macro doclibUrl doc>
   <a href="${url.context}/page/site/${doc.location.site}/documentlibrary?file=${doc.fileName?url}&amp;filter=editingMe" class="theme-color-1">${doc.displayName?html}</a>
</#macro>

<#-- Render no items text -->
<#macro renderNoItems>
   <div class="detail-list-item first-item">
      <span class="faded">${msg("label.noItems")}</span>
   </div>
</#macro>

<#macro renderItems contents icon>
   <#assign items=contents.items />
   <#list items?sort_by("modifiedOn") as doc>
   <#assign modifiedBy><a class="theme-color-1" href="${url.context}/page/user/${doc.modifiedByUser?url}/profile">${doc.modifiedBy?html}</a></#assign>
   <div class="detail-list-item <#if doc_index = 0>first-item</#if>">
      <div>
         <div class="icon">
            <img src="${url.context}/res/${icon}" alt="${doc.displayName?html}" />
         </div>
         <div class="details">
            <h4><a href="${siteURL(doc.browseUrl, doc.site.shortName)}" class="theme-color-1">${doc.displayName?html}</a></h4>
            <div>
               <#assign siteLink><a class="theme-color-1 site-link" href="${siteURL("dashboard", doc.site.shortName)}">${doc.site.title?html}</a></#assign>
               ${msg("text.edited-on", xmldate(doc.modifiedOn)?string(msg("date-format.defaultFTL")), siteLink)}
            </div>
         </div>
      </div>
   </div>
   </#list>
</#macro>
<script type="text/javascript">//<![CDATA[
(function()
{
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
   new Alfresco.widget.DashletTitleBarActions("${args.htmlid}").setOptions(
   {
      actions:
      [
         {
            cssClass: "help",
            bubbleOnClick:
            {
               message: "${msg("dashlet.help")?js_string}"
            },
            tooltip: "${msg("dashlet.help.tooltip")?js_string}"
         }
      ]
   });
})();
//]]></script>
<div class="dashlet" id="myEditingDocsDashlet">
   <div class="title">${msg("header")}</div>
   <div class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
<#if documents.error?exists>
      <div class="detail-list-item first-item last-item">
         <span class="error">${msg(documents.message)}</span>
      </div>
<#else>
      <div class="hdr">
         <h3>${msg('text.documents')}</h3>
      </div>
   <#if documents.items?size != 0>
      <#list documents.items?sort_by("modifiedOn") as doc>
         <#assign modifiedBy><a href="${url.context}/page/user/${doc.modifiedByUser?url}/profile">${doc.modifiedBy?html}</a></#assign>
         <#assign fileExtIndex = doc.fileName?last_index_of(".")>
         <#assign fileExt = (fileExtIndex > -1)?string(doc.fileName?substring(fileExtIndex + 1), "generic")>
      <div class="detail-list-item <#if doc_index = 0>first-item</#if>">
         <div>
            <div class="icon">
               <img src="${url.context}/components/images/filetypes/${fileExt}-file-32.png"
                    onerror="this.src='${url.context}/res/components/images/filetypes/generic-file-32.png'"
                    title="${(doc.displayName!doc.fileName)?html}" width="32" />
            </div>
            <div class="details">
               <h4><@doclibUrl doc /></h4>
               <div>
                  <#assign siteLink><a class="theme-color-1 site-link" href="${siteURL("dashboard", doc.location.site)}">${doc.location.siteTitle?html}</a></#assign>
                  ${msg("text.editing-since", xmldate(doc.modifiedOn)?string(msg("date-format.defaultFTL")), siteLink)}
               </div>
            </div>
         </div>
      </div>
      </#list>
   <#else>
      <@renderNoItems />
   </#if>
</#if>

<#if content.error?exists>
      <div class="detail-list-item first-item last-item">
         <span class="error">${msg(content.message?html)}</span>
      </div>
<#else>
      <div class="hdr">
         <h3>${msg('text.blogposts')}</h3>
      </div>
   <#if content.blogPosts.items?size != 0>
      <@renderItems content.blogPosts 'components/images/blogpost-32.png' />
   <#else>
      <@renderNoItems />
   </#if>
      <div class="hdr">
         <h3>${msg('text.wikipages')}</h3>
      </div>
   <#if content.wikiPages.items?size != 0>
      <@renderItems content.wikiPages 'components/images/wikipage-32.png' />
   <#else>
      <@renderNoItems />
   </#if>
      <div class="hdr">
         <h3>${msg('text.forumposts')}</h3>
      </div>
   <#if content.forumPosts.items?size != 0>
      <@renderItems content.forumPosts 'components/images/topicpost-32.png' />
   <#else>
      <@renderNoItems />
   </#if>
</#if>
   </div>
</div>