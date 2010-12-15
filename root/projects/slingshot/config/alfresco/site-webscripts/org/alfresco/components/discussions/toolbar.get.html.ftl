<script type="text/javascript">//<![CDATA[
   new Alfresco.DiscussionsToolbar("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs["site"]!""}",
      containerId: "${template.properties.container!'discussions'}",
      allowCreate: ${forum.forumPermissions.create?string}
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-body" class="share-toolbar discussions-toolbar flat-button theme-bg-2">

   <div class="navigation-bar <#if (args.showNavigationBar == "false")>hide</#if>">
      <div>
         <span class="<#if (page.url.args.listViewLinkBack! == "true")>backLink<#else>forwardLink</#if>">
            <a href="${url.context}/page/site/${page.url.templateArgs.site}/discussions-topiclist">${msg("link.listView")}</a>
         </span>
      </div>
   </div>

   <div class="action-bar theme-bg-1">
      <div class="new-topic"><button id="${args.htmlid}-create-button">${msg("button.create")}</button></div>
   </div>

   <div class="rss-feed">
      <div>
         <a id="${args.htmlid}-rssFeed-button" href="${url.context}/proxy/alfresco-feed/slingshot/wiki/pages/${page.url.templateArgs["site"]}?format=rss">${msg("button.rssfeed")}</a>
      </div>
   </div>

</div>
<div class="clear"></div>
        
