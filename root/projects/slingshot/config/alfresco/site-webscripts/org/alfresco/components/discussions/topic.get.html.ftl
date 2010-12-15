<#assign container = template.properties.container!'discussions' />

<script type="text/javascript">//<![CDATA[
   new Alfresco.DiscussionsTopic("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site}",
      containerId: "${container}",
      topicId: "${(page.url.args.topicId!'')?js_string}"
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-topic">
   <div id="${args.htmlid}-topic-view-div">
   </div>
   <div id="${args.htmlid}-topic-edit-div" class="hidden">
   </div>
</div>
