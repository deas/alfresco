<script type="text/javascript">//<![CDATA[
   new Alfresco.InlineEditMgr("${args.htmlid}").setOptions(
   {
      nodeRef: new Alfresco.util.NodeRef("${page.url.args.nodeRef?js_string}"),
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div class="inline-edit-mgr">
   <div class="heading">${msg("inline-edit-mgr.heading")}</div>
</div>