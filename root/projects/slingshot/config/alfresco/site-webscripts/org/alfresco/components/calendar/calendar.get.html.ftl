<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.Calendar("${args.htmlid?js_string}").setSiteId("${page.url.templateArgs.site!""}").setMessages(
      ${messages}
   );
//]]></script>

<div id="${el}-body">
   <div id="calendar"></div>
   <div>
      <div id="${el}-viewButtons" class="calendar-currentMonth"><a href="#" id="${el}-thisMonth-button">${msg("button.this-month")}</a></div>
   </div>
</div>