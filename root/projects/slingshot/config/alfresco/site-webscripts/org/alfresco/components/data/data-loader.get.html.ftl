<script type="text/javascript">//<![CDATA[
   new Alfresco.DataLoader('${args.htmlid}').setOptions(
   {
      url: "${args.url!""}",
      <#if args.eventData??>eventData: "${args.eventData?js_string}",</#if>
      <#if args.useProxy??>useProxy: ${args.useProxy},</#if>
      <#if args.failureMessageKey??>failureMessageKey: "${args.failureMessageKey}",</#if>      
      eventName: "${args.eventName!""}"
   }).setMessages(${messages});
//]]></script>

