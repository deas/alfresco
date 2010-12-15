<script type="text/javascript">//<![CDATA[
	new Alfresco.dashlet.SiteLinks("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!''}"
   });
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
//]]></script>

<#assign site=page.url.templateArgs.site>

<div class="dashlet site-links">
   <div class="title">${msg("header.links")}</div>
   <div class="toolbar">
      <a id="${args.htmlid}-createLink-button" class="theme-color-1" href="#">${msg("link.createLink")}</a>
   </div>
   <div class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
<#if numLinks?? && numLinks!=0>
   <#list links as link>
      <div class="detail-list-item <#if link_index = 0>first-item<#elseif !link_has_next>last-item</#if>">
         <div>
            <div class="link">
               <a <#if !link.internal>target="_blank"</#if> href="<#if link.url?substring(0,1) == "/" || link.url?index_of("://") == -1>http://</#if>${link.url?html}" class="theme-color-1">${link.title?html}</a>
            </div>
            <div class="actions">
               <a id="${args.htmlid}-details-span-${link_index}" href="${url.context}/page/site/${site}/links-view?linkId=${link.name}" class="details" title="${msg("link.details")}">&nbsp;</a>
            </div>
         </div>
      </div>
   </#list>
<#else>
      <div class="detail-list-item first-item last-item">
         <span>${msg("label.noLinks")}</span>
      </div>
</#if>
   </div>
</div>