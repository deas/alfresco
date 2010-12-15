<script type="text/javascript">//<![CDATA[
   new Alfresco.BlogPostView("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site}",
      containerId: "blog",
      postId: "${page.url.args.postId?js_string}"
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-post">
   <div id="${args.htmlid}-post-view-div">
   </div>
</div>
