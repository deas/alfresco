<script type="text/javascript">//<![CDATA[
   new Alfresco.BlogToolbar("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs["site"]!""}",
      containerId: "${template.properties.container!'blog'}",
      allowCreate: ${blog.permissions.create?string},
      allowConfigure: ${blog.permissions.edit?string}
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-body" class="share-toolbar blog-toolbar flat-button theme-bg-2">

   <div class="navigation-bar <#if ((args.showNavigationBar!"false") == "false")>hide</#if>">
      <div>
         <span class="<#if (page.url.args.listViewLinkBack! == "true")>backLink<#else>forwardLink</#if>">
            <a href="${url.context}/page/site/${page.url.templateArgs.site}/blog-postlist">${msg("link.listView")}</a>
         </span>
      </div>
   </div>

   <div class="action-bar theme-bg-1">
      <div class="new-blog"><button id="${args.htmlid}-create-button">${msg("button.create")}</button></div>
      <div class="separator">&nbsp;</div>
      <div class="configure-blog"><button id="${args.htmlid}-configure-button" name="postlist-configure-button">${msg("button.configure")}</button></div>
   </div>

   <div class="rss-feed">
      <div>
         <a id="${args.htmlid}-rssFeed-button" href="#">${msg("button.rssfeed")}</a>
      </div>
   </div>

</div>
<div class="clear"></div>
