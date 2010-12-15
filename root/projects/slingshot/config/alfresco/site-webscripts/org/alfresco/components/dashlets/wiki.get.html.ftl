<script type="text/javascript">//<![CDATA[
   new Alfresco.dashlet.WikiDashlet("${args.htmlid}").setOptions(
   {
      guid: "${instance.object.id}",
      siteId: "${page.url.templateArgs.site!""}",
      pages: [<#if (pageList?? && pageList.pages?size &gt; 0)><#list pageList.pages as p>"${p.name?js_string}"<#if p_has_next>, </#if></#list></#if>]
   }).setMessages(
      ${messages}
   );
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
//]]></script>
<div class="dashlet wiki">
   <div class="title" id="${args.htmlid}-title">${msg("label.header-prefix")}<#if wikiLink??> - <a href="wiki-page?title=${wikiLink?url}">${pageTitle!msg("label.header")}</a></#if></div>
<#if userIsSiteManager>
   <div class="toolbar">
      <a class="theme-color-1" href="#" id="${args.htmlid}-wiki-link">${msg("label.configure")}</a>
   </div>
</#if>
   <div class="body scrollablePanel" <#if args.height??>style="height: ${args.height}px;"</#if>>
      <div id="${args.htmlid}-scrollableList" class="rich-content">
         ${wikipage!msg("label.noConfig")}
      </div>
   </div>
</div>