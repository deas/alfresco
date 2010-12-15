<script type="text/javascript">//<![CDATA[
   new Alfresco.LinksView("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site}",
      containerId: "links",
      linkId: "${page.url.args.linkId?js_string}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div class="linksview-header">
   <div class="navigation-bar">
      <div>
         <span class="<#if (page.url.args.listViewLinkBack! == "true")>back-link<#else>forward-link</#if>">
            <a href="${url.context}/page/site/${page.url.templateArgs.site}/links">${msg("header.back")}</a>
         </span>
      </div>
   </div>

   <div class="action-bar">
   </div>
</div>

<div id="${args.htmlid}-link">
   <div id="${args.htmlid}-link-view-div">
   </div>
</div>
